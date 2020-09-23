package model.events;

public enum EventType {
    PARTICULAR_ARRIVAL("Llegada particular"),
    TAXI_ARRIVAL("Llegada taxi"),
    BUS_ARRIVAL("Llegada colectivo"),
    LIGHT_CHANGE("Cambio de semáforo"),
    END_OF_CROSSING("Fin de cruce"),
    END_OF_SIMULATION("Fin de simulación");

    private final String nameToDraw;
    private final int employeeId;

    EventType(String name) {
        this.nameToDraw = name;
        this.employeeId = 0;
    }

    EventType(String name, int employeeId) {
        this.nameToDraw = name;
        this.employeeId = employeeId;
    }

    public final String getNameToDraw() {
        return nameToDraw;
    }

    public int getEmployeeId() {
        return employeeId;
    }
}
