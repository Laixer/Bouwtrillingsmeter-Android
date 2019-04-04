package nl.gemeenterotterdam.bouwtrillingsmeter.android.backend;

import org.jtransforms.fft.FloatFFT_1D;

import java.util.ArrayList;

import nl.gemeenterotterdam.bouwtrillingsmeter.android.R;
import nl.gemeenterotterdam.bouwtrillingsmeter.android.frontend.Utility;

/**
 * @author Marijn Otte
 * @author Thomas Beckers
 * @since 1.0
 * <p>
 * This class performs all calculations to extract certain values from our data intervals.
 * All fourier calculations etc are done by this class.
 */
class Calculator {
    /**
     * yv: margin on speeddata. Each speed value calculated is multiplied with this value.
     * yt: margin on limitvalue.
     * Determined by input of user on question:
     * TODO Find out why we need these.
     */

    private static DataPoint3D<Long> accelerationPrevious;
    private static float[] velocity;
    private static float yvPartialSafetyFactor = 0;
    private static float ytPartialSafetyFactor = 0;


    /**
     * This initializes the calculator.
     * You should call this before starting a measurement.
     * This is due to the correct starting boundary conditions for our velocity.
     * This also determines our factors and limits.
     */
    public static void onStartMeasurementCalculations() {
        accelerationPrevious = new DataPoint3D<Long>((long) 0, new float[]{0, 0, 0});
        velocity = new float[]{0, 0, 0};

        // TODO Implement this

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
    public static ArrayList<DataPoint3D<Long>> calculateVelocityFromAcceleration(ArrayList<DataPoint3D<Long>> data) {
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

        // Add our margins for our safety factor
        /*for (int dimension = 0; dimension < 3; dimension++) {
            for (int i = 0; i < data.size(); i++) {
                result.get(i).values[dimension] *= partialSafetyFactor;
            }
        }*/

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
    public static ArrayList<DataPoint3D<Double>> fft(ArrayList<DataPoint3D<Long>> accelerations3D) {
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

    public static DominantFrequencies calculateDominantFrequencies(ArrayList<DataPoint3D<Double>> frequencyAmplitudes) {
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
     * Old functions - not used right now
     */

//
//    /**
//     * Adds margin to the maximum speed. See documentation for more information
//     *
//     * @param data maximum speed in x, y and z direction
//     * @return float maximum speed multiplied with margin
//     */
//    public static float[] addMargin(float[] data) {
//        data[0] *= yv;
//        data[1] *= yv;
//        data[2] *= yv;
//        return data;
//    }
//
//    /**
//     * @param acc acceleration data in frequency xAxisValue (frequency + acceleration)
//     * @return velocity data in frequency xAxisValue (frequency + velocity)
//     */
//    public static ArrayList<DataPoint3D<int[]>> calcVelocityFreqDomain(ArrayList<DataPoint3D<int[]>> acc) {
//        float xVel = 0;
//        float yVel = 0;
//        float zVel = 0;
//        float maxzvel = 0;
//        ArrayList<DataPoint3D<int[]>> velocities = new ArrayList<DataPoint3D<int[]>>();
//        for (DataPoint3D<int[]> dataPoint3D : acc) {
//            float xAcc = dataPoint3D.values[0];
//            float yAcc = dataPoint3D.values[1];
//            float zAcc = dataPoint3D.values[2];
//
//            int xFreq = dataPoint3D.xAxisValue[0];
//            int yFreq = dataPoint3D.xAxisValue[1];
//            int zFreq = dataPoint3D.xAxisValue[2];
//
//            xVel = xAcc / (2f * (float) Math.PI * (float) xFreq);
//            yVel = yAcc / (2f * (float) Math.PI * (float) yFreq);
//            zVel = zAcc / (2f * (float) Math.PI * (float) zFreq);
//            maxzvel = Math.max(zVel, maxzvel);
//            velocities.add(new DataPoint3D<int[]>(new int[]{xFreq, yFreq, zFreq}, new float[]{xVel, yVel, zVel}));
//        }
//        velocities.remove(0);
//        return velocities;
//    }
//
//    /**
//     * @param velocities list of velocities obtained for 1 second in frequency xAxisValue
//     * @return limitValue (m/s) for each velocity
//     */
//
//    public static ArrayList<DataPoint3D<int[]>> limitValue(ArrayList<DataPoint3D<int[]>> velocities) {
//        ArrayList<DataPoint3D<int[]>> limitValues = new ArrayList<DataPoint3D<int[]>>();
//        for (DataPoint3D<int[]> dataPoint3D : velocities) {
//            int xfreq = dataPoint3D.xAxisValue[0];
//            int yfreq = dataPoint3D.xAxisValue[1];
//            int zfreq = dataPoint3D.xAxisValue[2];
//
//            float xLimit = findLimit(xfreq);
//            float yLimit = findLimit(yfreq);
//            float zLimit = findLimit(zfreq);
//
//            //add margin
//            xLimit = xLimit / yt;
//            yLimit = yLimit / yt;
//            zLimit = zLimit / yt;
//
//            limitValues.add(new DataPoint3D<int[]>(new int[]{xfreq, yfreq, zfreq}, new float[]{xLimit, yLimit, zLimit}));
//        }
//        return limitValues;
//    }
//
//    /**
//     * @param freq frequency
//     * @return limitValue corresponding to frequency obtained from LimitValueTable (see doc for more information)
//     */
//    private static float findLimit(int freq) {
//        return LimitValueTable.getLimitValue(freq);
//    }
//
//    /**
//     * Calculates dominant frequency (highest ratio limitValue / velocity)
//     *
//     * @param limitValues array with limitValue for each frequency
//     * @param velocities  array with velocity for each frequency
//     * @return dominant frequency for each direction (x,y,z)
//     */
//    public static DominantFrequencies getDominantFrequencies(ArrayList<DataPoint3D<int[]>> limitValues, ArrayList<DataPoint3D<int[]>> velocities) {
//        int domFreqX = -1;
//        float ratioX = 0;
//        float domVelX = -1;
//        int domFreqY = -1;
//        float ratioY = 0;
//        float domVelY = -1;
//        int domFreqZ = -1;
//        float ratioZ = 0;
//        float domVelZ = -1;
//
//        for (int i = 0; i < limitValues.size(); i++) {
//            DataPoint3D<int[]> limitValue = limitValues.get(i);
//            DataPoint3D<int[]> velocity = velocities.get(i);
//
//            if (velocity.values[0] / limitValue.values[0] > ratioX) {
//                ratioX = velocity.values[0] / limitValue.values[0];
//                domFreqX = limitValue.xAxisValue[0];
//                domVelX = velocity.values[0];
//            }
//
//            if (velocity.values[1] / limitValue.values[1] > ratioY) {
//                ratioY = velocity.values[1] / limitValue.values[1];
//                domFreqY = limitValue.xAxisValue[1];
//                domVelY = velocity.values[1];
//            }
//
//            if (velocity.values[2] / limitValue.values[2] > ratioZ) {
//                ratioZ = velocity.values[2] / limitValue.values[2];
//                domFreqZ = limitValue.xAxisValue[2];
//                domVelZ = velocity.values[2];
//            }
//        }
//        return new DominantFrequencies(new int[]{domFreqX, domFreqY, domFreqZ}, new float[]{domVelX, domVelY, domVelZ}, new boolean[]{ratioX > 1, ratioY > 1, ratioZ > 1});
//    }

}
