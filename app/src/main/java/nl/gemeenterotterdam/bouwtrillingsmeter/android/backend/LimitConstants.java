package nl.gemeenterotterdam.bouwtrillingsmeter.android.backend;
import com.jjoe64.graphview.series.DataPoint;

import java.util.ArrayList;

/**
 * This contains all our determined constants.
 * These are documented in the provided job description documents.
 * All is determined according to the SBR Guideline A.
 */
public class LimitConstants {

    /**
     * Yv - Partial Safety Factor
     */
    private static final float yvIndicative = 1.6f;
    private static final float yvLimited = 1.4f;
    private static final float yvExtensive = 1.0f;
    private static final float yvVibrationSensitive = 1.0f;

    /**
     * Yt - Partial Safety Factor
     */
    private static final float ytVibrationShort = 1.0f;
    private static final float ytVibrationShortRepeated = 1.5f;
    private static final float ytVibrationContinuous = 2.5f;
    private static final float ytVibrationSensitive = 1.0f;

    /**
     * Limit values
     */
    private static final float[] lineBreakPointsFrequency = new float[]{0f, 10f, 50f, 100f};
    private static final float[][] lineBreakPointsVibrationShort = new float[][]{
            {20f, 20f, 40f, 50f},
            {5f, 5f, 15f, 20f},
            {3f, 3f, 8f, 10f}};
    private static final float[][] lineBreakPointsVibrationShortRepeated = new float[][]{
            {13f + (1f / 3f), 13f + (1f / 3f), 26f + (2f / 3f), 33f + (1f / 3f)},
            {3f + (1f / 3f), 3f + (1f / 3f), 10f, 13f + (1f / 3f)},
            {2f, 2f, 5f + (1f / 3f), 6f + (2f / 3f)}};
    private static final float[][] getLineBreakPointsVibrationContinuous = new float[][]{
            {10f, 10f, 20f, 25f},
            {2.5f, 2.5f, 7.5f, 10f},
            {1.5f, 1.5f, 4f, 5f}};

    /**
     * Gets our Yv based on the current settings.
     * This is used to correct our velocity.
     * For more info on this, see the SBR Guideline A.
     * TODO Implement this properly.
     *
     * @param settings The determined settings object
     * @return Yv
     */
    static float getYvFromSettings(Settings settings) {
        if (settings.vibrationSensitive) {
            return yvVibrationSensitive;
        }

        return yvVibrationSensitive;
        // throw new IllegalArgumentException("Unable to determine Yv");
    }

    /**
     * Gets our Yt based on the current settings.
     * This is used to correct our fft-calculated amplitude.
     * For more info on this, see the SBR Guideline A.
     *
     * @param settings The determined settings object
     * @return Yt
     */
    static float getYtFromSettings(Settings settings) {
        if (settings.vibrationSensitive) {
            return ytVibrationSensitive;
        }

        switch (settings.vibrationCategory) {
            case SHORT:
                return ytVibrationShort;
            case SHORT_REPEATED:
                return ytVibrationShortRepeated;
            case CONTINUOUS:
                return ytVibrationContinuous;
        }

        throw new IllegalArgumentException("Unable to determine Yt");
    }

    /**
     * Gets our y-values corresponding to our settings file.
     * The returned values correspond to the predefined frequency points.
     *
     * @param settings The settings file
     * @return The corresponding amplitude limit values.
     */
    private static float[] getLineBreakPointAmplitudesFromSettings(Settings settings) {
        int vibrationIndex = settings.vibrationCategory.ordinal();
        int buildingIndex = settings.buildingCategory.ordinal();

        float[] amplitudes;
        switch (vibrationIndex) {
            case 1:
                amplitudes = lineBreakPointsVibrationShort[buildingIndex];
                break;
            case 2:
                amplitudes = lineBreakPointsVibrationShortRepeated[buildingIndex];
                break;
            case 3:
                amplitudes = getLineBreakPointsVibrationContinuous[buildingIndex];
                break;
            default:
                throw new IllegalArgumentException("Could not compute limit values");
        }

        return amplitudes;
    }

    /**
     * This gets our limit line as an arraylist of {@link DataPoint}s.
     * Our Yv value is included in the result.
     *
     * @param settings The settings file
     * @return The DataPoint arraylist.
     */
    public static ArrayList<DataPoint> getLimitAsDataPoints(Settings settings) {
        float[] amplitudes = getLineBreakPointAmplitudesFromSettings(settings);
        ArrayList<DataPoint> result = new ArrayList<DataPoint>(amplitudes.length);
        float yt = getYvFromSettings(settings);
        for (int i = 0; i < lineBreakPointsFrequency.length; i++) {
            result.add(new DataPoint(lineBreakPointsFrequency[i], amplitudes[i] * yt));
        }
        return result;
    }

    /**
     * Get our limit values as a 2d float array.
     * Treat it as a point (x,y) by getting (result[i][0],result[i][1]).
     * [i][0] = frequency value
     * [i][1] = amplitude value
     * TODO This is not bulletproof. Rethink
     *
     * @param settings The corresponding settings.
     * @return Our 2d float array
     */
    static float[][] getLimitAsFloatPoints(Settings settings) {
        float[] amplitudes = getLineBreakPointAmplitudesFromSettings(settings);
        float yt = getYtFromSettings(settings);
        for (int i = 0; i < amplitudes.length; i++) {
            amplitudes[i] *= yt;

        }
        return new float[][]{lineBreakPointsFrequency, amplitudes};
    }
}
