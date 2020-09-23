package model;

public enum VehicleState {
    STOPPED_IN_X("PX"), STOPPED_IN_Y("PY"), CROSSING_X("CX");

    private final String nameToDraw;

    VehicleState(String name) {
        this.nameToDraw = name;
    }

    public final String getNameToDraw() {
        return nameToDraw;
    }
}
