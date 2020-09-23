package view;

import model.AvailableLane;
import model.Vehicle;
import model.VehicleState;
import model.VehicleType;

import java.util.ArrayList;
import java.util.List;

public class VehicleRow {
    private final int id;
    private VehicleState state;
    private final VehicleType type;
    private Double endOfCruse;
    private AvailableLane lane;

    public VehicleRow(int id, VehicleState state, VehicleType type, Double endOfCruse, AvailableLane lane) {
        this.id = id;
        this.state = state;
        this.type = type;
        this.endOfCruse = endOfCruse;
        this.lane = lane;
    }

    public VehicleRow(VehicleRow vehicleRowToClone) {
        this.id = vehicleRowToClone.id;
        this.state = vehicleRowToClone.state;
        this.type = vehicleRowToClone.type;
        this.endOfCruse = vehicleRowToClone.endOfCruse;
        this.lane = vehicleRowToClone.lane;
    }

    public VehicleRow(Vehicle vehicle) {
        this.id = vehicle.getId();
        this.state = vehicle.getState();
        this.type = vehicle.getType();
        this.endOfCruse = vehicle.getCrossingEnd();
        this.lane = vehicle.getLane();
    }

    public List<String> getTableViewHeaders() {
        ArrayList<String> tableViewClientHeaders = new ArrayList<>();

        tableViewClientHeaders.add("Estado "+ type.getNameToDraw() + " "+ id);
        tableViewClientHeaders.add("Fin Cruce");
        tableViewClientHeaders.add("Carril");

        return tableViewClientHeaders;
    }

    public List<String> getTableViewRow() {
        ArrayList<String> tableViewCells = new ArrayList<>();

        if (state != null){
            tableViewCells.add(state.getNameToDraw());
        }else {
            tableViewCells.add("-");
        }
        if (endOfCruse != null) {
            tableViewCells.add(String.format("%.2f", endOfCruse));
        } else {
            tableViewCells.add("");
        }
        if (lane != null) {
            tableViewCells.add(lane.getNameToDraw());
        } else {
            tableViewCells.add("");
        }

        return tableViewCells;
    }

    public void setState(VehicleState state) {
        this.state = state;
    }

    public void setEndOfCruse(Double endOfCruse) {
        this.endOfCruse = endOfCruse;
    }

    public void setLane(AvailableLane lane) {
        this.lane = lane;
    }

    public int getId() {
        return id;
    }

    public VehicleState getState() {
        return state;
    }

    public double getEndOfCruse() {
        return endOfCruse;
    }

    public AvailableLane getLane() {
        return lane;
    }
}
