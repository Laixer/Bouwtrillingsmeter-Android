package nl.gemeenterotterdam.bouwtrillingsmeter.android.backend;

import org.jtransforms.fft.FloatFFT_1D;

import java.util.ArrayList;

/**
 * @author Marijn Otte
 * @author Thomas Beckers
 * @since 1.0
 * <p>
 * This class performs all calculations to extract certain values from our data intervals.
 * All fourier calculations etc are done by this class.
 */
class Calculator {
    private static DataPoint3D<Long> accelerationPrevious;
    private static float[] velocity;
    private static float yv = 0;
    private static float yt = 0;
    private static float[][] limitValuesAsFloatPoints;

    /**
     * This initializes the calculator.
     * You should call this before starting a measurement.
     * This is due to the correct starting boundary conditions for our velocity.
     * This also determines our factors and limits.
     */
    static void onStartMeasurementCalculations() {
        // Used in our velocity calculations
        accelerationPrevious = new DataPoint3D<Long>((long) 0, new float[]{0, 0, 0});
        velocity = new float[]{0, 0, 0};

        // Get constants from limitconstants
        Settings settings = MeasurementControl.getCurrentMeasurement().settings;
        yt = LimitConstants.getYtFromSettings(settings);
        yv = LimitConstants.getYvFromSettings(settings);
        limitValuesAsFloatPoints = LimitConstants.getLimitAsFloatPoints(settings);
    }

    /**
     * Calculates velocity from acceleration data.
     * This is done by integrating each step.
     * We calculate the area under the graph,
     * then add it to the total in order to get the
     * velocity for each point.
     * TODO This does not seem to work properly.
     *
     * @param data values from acceleroMeter (retrieved for 1 second)
     * @return A new arraylist, with a velocity for each point. All time values will be the same as the input data.
     */
    static ArrayList<DataPoint3D<Long>> calculateVelocityFromAcceleration(ArrayList<DataPoint3D<Long>> data) {
        // Create a result array
        ArrayList<DataPoint3D<Long>> result = new ArrayList<DataPoint3D<Long>>();

        /**
         * Differentiate based on our {@link accelerationPrevious} variable.
         * Set this variable after each point.
         * This ensures cross interval consistent boundary conditions.
         */
        for (int i = 0; i < data.size(); i++) {
            // Calculate dt [ms]
            long timeCurrent = data.get(i).xAxisValue;
            long timePrevious = accelerationPrevious.xAxisValue;
            double dtSeconds = (timeCurrent - timePrevious) / 1000.0;

            // Compute the integral by adding each bit to the total velocity
            velocity[0] += data.get(i).values[0] * dtSeconds;
            velocity[1] += data.get(i).values[1] * dtSeconds;
            velocity[2] += data.get(i).values[2] * dtSeconds;

            // Create a new datapoint with the new velocities
            result.add(new DataPoint3D<Long>(data.get(i).xAxisValue, velocity));

            // Store our datapoint as previous for the next iteration
            accelerationPrevious = data.get(i);
        }

        // Return our result
        return result;
    }


    /**
     * Finds the maximum values in our array in 3 dimensions
     *
     * @param dataPoints The datapoints
     * @param <T>        The datapoint type
     * @return A float[3] with the highest values
     */
    private static <T> int[] getMaxIndexesInArray3D(ArrayList<DataPoint3D<T>> dataPoints) {
        float[] highestValue = new float[]{Float.MIN_VALUE, Float.MIN_VALUE, Float.MIN_VALUE};
        int[] index = new int[]{-1, -1, -1};

        for (int i = 0; i < dataPoints.size(); i++) {
            for (int dimension = 0; dimension <= 2; dimension++) {
                float value = dataPoints.get(i).values[dimension];
                if (value > highestValue[dimension]) {
                    highestValue[dimension] = value;
                    index[dimension] = i;
                }
            }
        }

        return index;
    }

    /**
     * Calculate fft
     *
     * @param accelerations3D list of velocities obtained for 1 second
     * @return float frequency in x, y and z direction in range (0-50)Hz with corresponding magnitude
     */
    static ArrayList<DataPoint3D<Double>> fft(ArrayList<DataPoint3D<Long>> accelerations3D) {
        // Calculate extraction constants
        // TODO df is incorrect? Overwritten as = 1 seems to work
        int n = accelerations3D.size();
        double sampleRate = n / ((double) Constants.intervalInMilliseconds / 1000);

        // Create velocity array for each dimension
        float[][] accelerationsSplit = new float[3][n];
        accelerationsSplit[0] = new float[n];
        accelerationsSplit[1] = new float[n];
        accelerationsSplit[2] = new float[n];

        // Fill our acceleration arrays
        for (int i = 0; i < accelerations3D.size(); i++) {
            accelerationsSplit[0][i] = accelerations3D.get(i).values[0];
            accelerationsSplit[1][i] = accelerations3D.get(i).values[1];
            accelerationsSplit[2][i] = accelerations3D.get(i).values[2];
        }

        // Apply FFT on each dimension array.
        // The results are stored in said array.
        FloatFFT_1D fft = new FloatFFT_1D(n);
        fft.realForward(accelerationsSplit[0]);
        fft.realForward(accelerationsSplit[1]);
        fft.realForward(accelerationsSplit[2]);

        // Loop trough fft transformed datapoints
        float[] magnitude = new float[3];
        ArrayList<DataPoint3D<Double>> result = new ArrayList<DataPoint3D<Double>>();
        for (int i = 0; i < n / 2; i++) {
            for (int dimension = 0; dimension < 3; dimension++) {
                // Get real and imaginairy parts of each datapoint and calculate the combined magnitude
                double re = accelerationsSplit[dimension][2 * i];
                double im = accelerationsSplit[dimension][2 * i + 1];
                magnitude[dimension] = (float) Math.hypot(im, re);
            }
            double frequency = (sampleRate * i) / (n);
            result.add(new DataPoint3D<Double>(frequency, magnitude));
        }

        // Return the fourier domain data points
        return result;
    }

    static DominantFrequencies calculateDominantFrequencies(ArrayList<DataPoint3D<Double>> frequencyAmplitudes) {
        // Get the indexes (=frequencies) and the values
        int[] maxIndexes = getMaxIndexesInArray3D(frequencyAmplitudes);
        float[] maxValues = new float[3];
        for (int dimension = 0; dimension < maxValues.length; dimension++) {
            maxValues[dimension] = frequencyAmplitudes.get(maxIndexes[dimension]).values[dimension];
        }

        // Check if we have exceeded any limits
        // TODO Implement
        boolean[] exceeded = new boolean[3];

        // Return all as a new DominantFrequencies object
        return new DominantFrequencies(maxIndexes, maxValues, exceeded);
    }

    /**
     * Checks if a given value is above our set limit for this settings.
     * This simply linearly interpolates.
     *
     * @param frequency The frequency
     * @param amplitude The amplitude
     * @return True if we exceed the limit
     */
    private static boolean isAboveLimit(float frequency, float amplitude) {
        // Acquire our segment index
        int segmentIndex = 0;
        while (limitValuesAsFloatPoints[segmentIndex+1][0] < frequency && segmentIndex < limitValuesAsFloatPoints[0].length - 1) {
            segmentIndex++;
        }

        // Acquire our before and after points
        float x1 = limitValuesAsFloatPoints[segmentIndex][0];
        float y1 = limitValuesAsFloatPoints[segmentIndex][1];
        float x2 = limitValuesAsFloatPoints[segmentIndex + 1][0];
        float y2 = limitValuesAsFloatPoints[segmentIndex + 1][1];
        float dx = x2 - x1;
        float dy = y2 - y1;

        // Edge case, division by 0
        if (dx == 0) {
            return amplitude >= limitValuesAsFloatPoints[segmentIndex][1];
        }

        // Calculate our new amplitude
        float d = dy / dx;
        float limitAmplitude = y1 + d*(frequency - x1);
        return amplitude >= limitAmplitude;
    }

}
