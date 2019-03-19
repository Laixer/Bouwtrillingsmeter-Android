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
    public static float yv = 0f;
    public static float yt = 0f;

    private static DataPoint3D<Long> accelerationPrevious;
    private static float[] velocity;

    /**
     * This initializes the calculator.
     * You should call this before starting a measurement.
     * This is due to the correct starting boundary conditions.
     */
    public static void onStartMeasurementCalculations() {
        accelerationPrevious = new DataPoint3D<Long>((long) 0, new float[]{0, 0, 0});
        velocity = new float[]{0, 0, 0};
    }

    /**
     * Calculates velocity from acceleration data.
     * This is done by integrating each step.
     * We calculate the area under the graph,
     * then add it to the total in order to get the
     * velocity for each point.
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

        // Return our result
        return result;
    }

    /**
     * Calculates max acceleration from arraylist with different acceleration datapoints
     *
     * @param dataArray array with DataPoints, corresponding to accelerations or velocities
     */
    public static <T> float[] maxValueInArray(ArrayList<DataPoint3D<int[]>> dataArray) {
        float maxx = 0;
        float maxy = 0;
        float maxz = 0;

        for (DataPoint3D<int[]> dataPoint3D : dataArray) {
            float xAcc = Math.abs(dataPoint3D.values[0]);
            float yAcc = Math.abs(dataPoint3D.values[1]);
            float zAcc = Math.abs(dataPoint3D.values[2]);

            maxx = Math.max(maxx, xAcc);
            maxy = Math.max(maxy, yAcc);
            maxz = Math.max(maxz, zAcc);
        }

        float[] results = new float[]{maxx, maxy, maxz};
        return results;
    }

    /**
     * @param dataArray array of frequency datapoints
     * @return max frequency of array in x,y and z direction
     */
    public static int[] maxFrequencies(ArrayList<DataPoint3D<int[]>> dataArray) {
        float maxx = 0;
        float maxy = 0;
        float maxz = 0;
        int freqx = 0;
        int freqy = 0;
        int freqz = 0;

        for (DataPoint3D dataPoint3D : dataArray) {
            int[] frequencies = (int[]) dataPoint3D.xAxisValue;
            float[] magnitudes = (float[]) dataPoint3D.values;
            float xVel = magnitudes[0];
            float yVel = magnitudes[1];
            float zVel = magnitudes[2];

            if (xVel > maxx) {
                freqx = frequencies[0];
            }

            if (yVel > maxy) {
                freqy = frequencies[1];
            }

            if (zVel > maxz) {
                freqz = frequencies[2];
            }

            maxx = Math.max(maxx, xVel);
            maxy = Math.max(maxy, yVel);
            maxz = Math.max(maxz, zVel);


        }

        int[] results = new int[]{freqx, freqy, freqz};
        return results;
    }

    /**
     * Calculate fft
     * realForward returns Re+Im values
     * Calculate Magnitude from Re + Im
     * <p>
     * TODO n = 2^x voor optimale fft
     * <p>
     * n = amount of datapoints
     * fft[0] = DC value (average of the entire sample)
     * t / dt = sampling frequency fs
     * Bb = fs
     * df in fourier domain = Bb / n
     * <p>
     * max f = Bb / 2 (sampling theorem = nyquist frequency)
     * -> halverwege is de maximale frequentie die je in het tijdsdomein kan laten zien
     * hierom pakken we meestal alleen de eerste helft van de array
     * <p>
     * Math.hypot returns sqrt(x^2 + y^2)
     * Extract frequencies from first half
     * The second half is not accurate because this is beyond the nyquist frequency
     * <p>
     * From the documentation the data is ordened as follows
     * a[2*k] = Re[k], 0<=k<n/2
     * a[2*k+1] = Im[k], 0<k<n/2
     * a[1] = Re[n/2]
     *
     * @param accelerations3D list of velocities obtained for 1 second
     * @return float frequency in x, y and z direction in range (0-50)Hz with corresponding magnitude
     */
    public static ArrayList<DataPoint3D<Double>> fft(ArrayList<DataPoint3D<Long>> accelerations3D) {
        // Calculate extraction constants
        // TODO df is incorrect? Overwritten as = 1
        int n = accelerations3D.size();
        double df = (((double) Constants.intervalInMilliseconds / 1000.0) / n);
        df = 1;

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

        // Create storage variables
        float[] magnitude = new float[3];
        float[] magnitudeMax = new float[3];
        int[] magnitudeMaxIndex = new int[3];

        // Loop trough fft transformed datapoints
        ArrayList<DataPoint3D<Double>> result = new ArrayList<DataPoint3D<Double>>();
        for (int i = 0; i < n / 2; i++) {
            for (int dimension = 0; dimension < 3; dimension++) {
                // Get real and imaginairy parts of each datapoint and calculate the combined magnitude
                double re = accelerationsSplit[dimension][2 * i];
                double im = accelerationsSplit[dimension][2 * i + 1];
                magnitude[dimension] = (float) Math.hypot(im, re);

                // Track our maxima and their index
                if (magnitude[dimension] > magnitudeMax[dimension]) {
                    magnitudeMax[dimension] = magnitude[dimension];
                    magnitudeMaxIndex[dimension] = i;
                }
            }
            double frequency = df * i;
            result.add(new DataPoint3D<Double>(frequency, magnitude));
        }

        // Return the fourier domain data points
        return result;
    }

    /**
     * Adds margin to the maximum speed. See documentation for more information
     *
     * @param data maximum speed in x, y and z direction
     * @return float maximum speed multiplied with margin
     */
    public static float[] addMargin(float[] data) {
        data[0] *= yv;
        data[1] *= yv;
        data[2] *= yv;
        return data;
    }

    /**
     * @param acc acceleration data in frequency xAxisValue (frequency + acceleration)
     * @return velocity data in frequency xAxisValue (frequency + velocity)
     */
    public static ArrayList<DataPoint3D<int[]>> calcVelocityFreqDomain(ArrayList<DataPoint3D<int[]>> acc) {
        float xVel = 0;
        float yVel = 0;
        float zVel = 0;
        float maxzvel = 0;
        ArrayList<DataPoint3D<int[]>> velocities = new ArrayList<DataPoint3D<int[]>>();
        for (DataPoint3D<int[]> dataPoint3D : acc) {
            float xAcc = dataPoint3D.values[0];
            float yAcc = dataPoint3D.values[1];
            float zAcc = dataPoint3D.values[2];

            int xFreq = dataPoint3D.xAxisValue[0];
            int yFreq = dataPoint3D.xAxisValue[1];
            int zFreq = dataPoint3D.xAxisValue[2];

            xVel = xAcc / (2f * (float) Math.PI * (float) xFreq);
            yVel = yAcc / (2f * (float) Math.PI * (float) yFreq);
            zVel = zAcc / (2f * (float) Math.PI * (float) zFreq);
            maxzvel = Math.max(zVel, maxzvel);
            velocities.add(new DataPoint3D<int[]>(new int[]{xFreq, yFreq, zFreq}, new float[]{xVel, yVel, zVel}));
        }
        velocities.remove(0);
        return velocities;
    }

    /**
     * @param velocities list of velocities obtained for 1 second in frequency xAxisValue
     * @return limitValue (m/s) for each velocity
     */

    public static ArrayList<DataPoint3D<int[]>> limitValue(ArrayList<DataPoint3D<int[]>> velocities) {
        ArrayList<DataPoint3D<int[]>> limitValues = new ArrayList<DataPoint3D<int[]>>();
        for (DataPoint3D<int[]> dataPoint3D : velocities) {
            int xfreq = dataPoint3D.xAxisValue[0];
            int yfreq = dataPoint3D.xAxisValue[1];
            int zfreq = dataPoint3D.xAxisValue[2];

            float xLimit = findLimit(xfreq);
            float yLimit = findLimit(yfreq);
            float zLimit = findLimit(zfreq);

            //add margin
            xLimit = xLimit / yt;
            yLimit = yLimit / yt;
            zLimit = zLimit / yt;

            limitValues.add(new DataPoint3D<int[]>(new int[]{xfreq, yfreq, zfreq}, new float[]{xLimit, yLimit, zLimit}));
        }
        return limitValues;
    }

    /**
     * @param freq frequency
     * @return limitValue corresponding to frequency obtained from LimitValueTable (see doc for more information)
     */
    private static float findLimit(int freq) {
        return LimitValueTable.getLimitValue(freq);
    }

    /**
     * Calculates dominant frequency (highest ratio limitValue / velocity)
     *
     * @param limitValues array with limitValue for each frequency
     * @param velocities  array with velocity for each frequency
     * @return dominant frequency for each direction (x,y,z)
     */
    public static DominantFrequencies getDominantFrequencies(ArrayList<DataPoint3D<int[]>> limitValues, ArrayList<DataPoint3D<int[]>> velocities) {
        int domFreqX = -1;
        float ratioX = 0;
        float domVelX = -1;
        int domFreqY = -1;
        float ratioY = 0;
        float domVelY = -1;
        int domFreqZ = -1;
        float ratioZ = 0;
        float domVelZ = -1;

        for (int i = 0; i < limitValues.size(); i++) {
            DataPoint3D<int[]> limitValue = limitValues.get(i);
            DataPoint3D<int[]> velocity = velocities.get(i);

            if (velocity.values[0] / limitValue.values[0] > ratioX) {
                ratioX = velocity.values[0] / limitValue.values[0];
                domFreqX = limitValue.xAxisValue[0];
                domVelX = velocity.values[0];
            }

            if (velocity.values[1] / limitValue.values[1] > ratioY) {
                ratioY = velocity.values[1] / limitValue.values[1];
                domFreqY = limitValue.xAxisValue[1];
                domVelY = velocity.values[1];
            }

            if (velocity.values[2] / limitValue.values[2] > ratioZ) {
                ratioZ = velocity.values[2] / limitValue.values[2];
                domFreqZ = limitValue.xAxisValue[2];
                domVelZ = velocity.values[2];
            }
        }
        return new DominantFrequencies(new int[]{domFreqX, domFreqY, domFreqZ}, new float[]{domVelX, domVelY, domVelZ}, new boolean[]{ratioX > 1, ratioY > 1, ratioZ > 1});
    }


}
