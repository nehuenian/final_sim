package model;

public enum VehicleType {
    PARTICULAR("particular"), TAXI("taxi"), BUS("colectivo"), NON_IMPORTANT("no importante");

    private final String nameToDraw;

    VehicleType(String name) {
        this.nameToDraw = name;
    }

    public final String getNameToDraw() {
        return nameToDraw;
    }
}
