package nl.gemeenterotterdam.bouwtrillingsmeter.android.backend;

/**
 * This contains all our determined constants.
 * These are documented in the provided job description documents.
 * All is determined according to the SBR Guideline A.
 */
public class LimitConstants {

    private static final float yvIndicative = 1.6f;
    private static final float yvLimited = 1.4f;
    private static final float yvExtensive = 1.0f;
    private static final float yvVibrationSensitive = 1.0f;

    private static final float ytVibrationShort = 1.0f;
    private static final float ytVibrationShortRepeated = 1.5f;
    private static final float ytVibrationContinuous = 2.5f;
    private static final float ytVibrationSensitive = 1.0f;


    /**
     * Gets our Yv based on the current settings.
     * This is used to correct our velocity.
     * For more info on this, see the SBR Guideline A.
     * TODO Implement properly.
     *
     * @param settings The determined settings object
     * @return Yv
     */
    public static float getYvFromSettings(Settings settings) {
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
    public static float getYtFromSettings(Settings settings) {
        if (settings.vibrationSensitive) {
            return ytVibrationSensitive;
        }

        switch(settings.vibrationCategory) {
            case SHORT:
                return ytVibrationShort;
            case SHORT_REPEATED:
                return ytVibrationShortRepeated;
            case CONTINUOUS:
                return ytVibrationContinuous;
        }

        throw new IllegalArgumentException("Unable to determine Yt");
    }


}
