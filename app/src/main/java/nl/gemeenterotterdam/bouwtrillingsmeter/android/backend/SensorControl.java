package nl.gemeenterotterdam.bouwtrillingsmeter.android.backend;

/**
 * TODO Do wee need this?
 *
 * @author Thomas Beckers
 * @version 1.0
 *
 * SensorControl class
 *
 * This is the only part of the application that communicates with the sensors of the phone.
 * This will get data from the sensors, called by the MotionDataParser class.
 */
class SensorControl {

    private static boolean hasSensorAccess;

    public static boolean getHasSensorAccess() {
        return hasSensorAccess;
    }

    /**
     * Initializes the sensorcontrol
     */
    public static void initialize() {
        if (!hasSensorAccess) {
            tryGetSensorAccess();
        }
    }

    /**
     * This gets the data from the accelleromater
     * @return An integer array with [x, y, z] angles. This is null if we are not allowed to read out anything.
     */
    public static int[] getCurrentSensorValues() {
        if (hasSensorAccess) {
            int[] result = new int[3];

            result[0] = 0;
            result[1] = 0;
            result[2] = 0;

            return result;
        }

        else {
            System.out.println("We do not have access to the phones sensors");
            return null;
        }
    }

    /**
     * This attempts to get sensor access
     *
     * @return True if we get access, false if we don't
     */
    private static boolean tryGetSensorAccess() {
        hasSensorAccess = true;
        return hasSensorAccess;
    }

}
