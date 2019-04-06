package nl.gemeenterotterdam.bouwtrillingsmeter.android.backend;

import org.jtransforms.fft.FloatFFT_1D;

import java.lang.reflect.Array;
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
     * Creates a new {@link DominantFrequencies} object based on our fft data.
     *
     * @param frequencyAmplitudes The frequency amplitudes against their frequency.
     * @return The new object
     */
    static DominantFrequencies calculateDominantFrequencies(ArrayList<DataPoint3D<Double>> frequencyAmplitudes) {
        // Store our dominant frequency indexes
        int[] maxIndexes = new int[]{0, 0, 0};

        // Find the index of our dominant frequency in every dimension
        for (int dimension = 0; dimension < 3; dimension++) {
            float highestRatio = 0;

            for (int i = 1; i < frequencyAmplitudes.size() - 1; i++) {
                // Get our points values
                double xAxisValue = frequencyAmplitudes.get(i).xAxisValue;
                float frequency = (float) xAxisValue;
                float amplitude = frequencyAmplitudes.get(i).values[dimension];

                // If it's a local maximum
                if (amplitude > frequencyAmplitudes.get(i - 1).values[dimension] && amplitude > frequencyAmplitudes.get(i + 1).values[dimension]) {
                    // Get the limit amplitude and its corresponding ratio
                    float limitAmplitude = getLimitAmplitudeFromFrequency(frequency);
                    float ratio = amplitude / limitAmplitude;

                    // If this point has a greater ratio
                    if (ratio > highestRatio) {
                        highestRatio = ratio;
                        maxIndexes[dimension] = i;
                    }
                }
            }
        }

        // Prepare variables
        float[] maxAmplitudes = new float[3];
        float[] maxFrequencies = new float[3];
        boolean[] exceeded = new boolean[]{false, false, false};

        // Extract variables
        for (int dimension = 0; dimension < 3; dimension++) {
            double xAxisValue = frequencyAmplitudes.get(maxIndexes[dimension]).xAxisValue;
            maxFrequencies[dimension] = (float) xAxisValue;
            maxAmplitudes[dimension] = frequencyAmplitudes.get(maxIndexes[dimension]).values[dimension];
            exceeded[dimension] = maxAmplitudes[dimension] > getLimitAmplitudeFromFrequency(maxFrequencies[dimension]);
        }

        // Return all as a new DominantFrequencies object
        return new DominantFrequencies(maxFrequencies, maxAmplitudes, exceeded);
    }

    /**
     * This gives us the exceed amplitude value for our frequency.
     * This simply linearly interpolates.
     *
     * @param frequency The frequency
     * @return True if we exceed the limit
     */
    private static float getLimitAmplitudeFromFrequency(float frequency) {
        // Acquire our segment index
        int segmentIndex = 0;
        while (limitValuesAsFloatPoints[0][segmentIndex + 1] < frequency && segmentIndex < limitValuesAsFloatPoints[0].length - 2) {
            segmentIndex++;
        }

        // Acquire our before and after points
        float x1 = limitValuesAsFloatPoints[0][segmentIndex];
        float y1 = limitValuesAsFloatPoints[1][segmentIndex];
        float x2 = limitValuesAsFloatPoints[0][segmentIndex + 1];
        float y2 = limitValuesAsFloatPoints[1][segmentIndex + 1];
        float dx = x2 - x1;
        float dy = y2 - y1;

        // Edge case, division by 0
        if (dx == 0) {
            return limitValuesAsFloatPoints[1][segmentIndex];
        }

        // Calculate our new amplitude
        float d = dy / dx;
        float limitAmplitude = y1 + d * (frequency - x1);
        return limitAmplitude;
    }
}
