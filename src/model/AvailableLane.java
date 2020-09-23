package model;

public enum AvailableLane {
    LEFT_LANE("Izquierdo"), RIGHT_LANE("Derecho");

    private final String nameToDraw;

    AvailableLane(String name) {
        this.nameToDraw = name;
    }

    public final String getNameToDraw() {
        return nameToDraw;
    }
}
