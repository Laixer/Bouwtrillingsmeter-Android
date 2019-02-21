package nl.gemeenterotterdam.bouwtrillingsmeter.android.backend;

public class Settings {

    public BuildingCategory buildingCategory;
    public VibrationCategory vibrationCategory;
    public boolean vibrationSensitive;

    /**
     * Constructor
     */
    public Settings() {
        buildingCategory = BuildingCategory.none;
        vibrationCategory = VibrationCategory.none;
        vibrationSensitive = false;
    }

}

