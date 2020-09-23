package model;

public enum LightState {
    RED("Rojo"), GREEN("Verde");

    private final String nameToDraw;

    LightState(String name) {
        this.nameToDraw = name;
    }

    public final String getNameToDraw() {
        return nameToDraw;
    }
}
