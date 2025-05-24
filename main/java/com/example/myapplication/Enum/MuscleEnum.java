package com.example.myapplication.Enum;

public enum MuscleEnum {
    QUADS("Quads"),
    HAMSTRING("Hamstring"),
    CALVES("Calves"),
    GLUTES("Glutes"),
    BACK("Back"),
    CHEST("Chest"),
    SHOULDERS("Shoulders"),
    TRICEPS("Triceps"),
    BICEPS("Biceps"),
    TRAPS("Traps"),
    FULLBODY("Full Body"),  // Thêm đây
    CORE("Core");           // Thêm đây

    private final String displayName;

    MuscleEnum(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}