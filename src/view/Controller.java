package view;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import model.events.*;
import service.IService;
import service.Service;

import java.util.ArrayList;
import java.util.List;

import static util.CommonUtils.*;

public class Controller extends StartingPageView implements IQueueController {

    private final ArrayList<String> initialSimulationColumnsHeadText = new ArrayList<>();

    private IService _service;

    public void onWindowLoad(ActionEvent actionEvent) {
        _service = new Service();
        setUpSimulationTableView();
    }

    public void onSimulate() {
        if (validateEventInput()) {
            IEventGenerator particularArrivalGenerator =
                    new ExponentialEventGenerator(getSafeDouble(txt_particular_media), EventType.PARTICULAR_ARRIVAL);
            IEventGenerator taxiArrivalGenerator =
                    new ExponentialEventGenerator(getSafeInt(txt_taxi_media), EventType.TAXI_ARRIVAL);
            IEventGenerator busArrivalGenerator =
                    new ExponentialEventGenerator(getSafeInt(txt_bus_media), EventType.BUS_ARRIVAL);
            _service.simulate(
                    particularArrivalGenerator,
                    taxiArrivalGenerator,
                    busArrivalGenerator,
                    getSafeDouble(txt_light_change_time),
                    getSafeDouble(txt_crossing_time),
                    getSafeInt(txt_simulation_time),
                    this
            );
        } else {
            lbl_error.setVisible(true);
        }
    }

    @Override
    public void drawSimulationRows(List<SimulationRow> rows, List<VehicleRow> vehicles) {
        setUpSimulationDynamicsTableColumns(vehicles);
        rows.forEach(row -> tv_queue.getItems().add(row.getTableViewRow()));
    }

    @Override
    public void drawSimulationResult(SimulationResult result) {
        txt_blocked_crossing_percentage.setText(String.format("%.2f",result.getBlockedPercentage()) + "%");
    }

    private void setUpSimulationTableView() {
        getInitalSimulationTableColumns();
        tv_queue.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tv_queue.setOnSort(javafx.event.Event::consume);
        MenuItem item = new MenuItem("Copiar");
        item.setOnAction(event -> {
            StringBuilder clipboardString = new StringBuilder();
            tv_queue.getColumns().forEach(listTableColumn -> clipboardString.append(listTableColumn.getText()).append("\t"));
            clipboardString.append('\n');
            tv_queue.getSelectionModel().getSelectedItems().forEach(selectedRow -> {
                selectedRow.forEach(selectedCell -> clipboardString.append(selectedCell).append("\t"));
                clipboardString.append('\n');
            });

            tv_queue.getSelectionModel().getSelectedItems().iterator();

            final ClipboardContent content = new ClipboardContent();

            content.putString(clipboardString.toString());
            Clipboard.getSystemClipboard().setContent(content);
        });
        ContextMenu menu = new ContextMenu();
        menu.getItems().add(item);
        tv_queue.setContextMenu(menu);
    }

    private void getInitalSimulationTableColumns() {
        tv_queue.getColumns().forEach(column -> initialSimulationColumnsHeadText.add(column.getText()));
        tv_queue.getColumns().clear();
    }

    private boolean validateEventInput() {
        lbl_error.setVisible(false);
        boolean allInputsValid = true;

        if (!tryParseDouble(txt_particular_media) || getSafeDouble(txt_particular_media) <= 0) {
            lbl_error.setText("Por favor, introduce una media válida para la llegada de particulares.");
            allInputsValid = false;
        }

        if (!tryParseDouble(txt_taxi_media) || getSafeDouble(txt_taxi_media) <= 0) {
            lbl_error.setText("Por favor, introduce una media válida para la llegada de taxis.");
            allInputsValid = false;
        }

        if (!tryParseDouble(txt_bus_media) || getSafeDouble(txt_bus_media) <= 0) {
            lbl_error.setText("Por favor, introduce una media válida para la llegada de colectivos.");
            allInputsValid = false;
        }

        if (!tryParseDouble(txt_light_change_time) || getSafeDouble(txt_light_change_time) <= 0) {
            lbl_error.setText("Por favor, introduce un tiempo válido para el tiempo de cambio de semáforo.");
            allInputsValid = false;
        }

        if (!tryParseDouble(txt_crossing_time) || getSafeDouble(txt_crossing_time) <= 0) {
            lbl_error.setText("Por favor, introduce un tiempo válido para el tiempo de cruce de vehículos.");
            allInputsValid = false;
        }

        if (!tryParseDouble(txt_simulation_time) || getSafeDouble(txt_simulation_time) <= 0) {
            lbl_error.setText("Por favor, introduce un tiempo válido a simular.");
            allInputsValid = false;
        } else if (!tryParseDouble(txt_minute_from)
                || getSafeDouble(txt_minute_from) < 0
                || getSafeDouble(txt_minute_from) > getSafeDouble(txt_simulation_time)) {
            allInputsValid = false;
            lbl_error.setText("El minuto desde el cual mostrar filas debe ser positivo y menor al valor total de minutos de la simulación.");
        } else if (!tryParseInt(txt_iteration_to)
                || getSafeInt(txt_iteration_to) <= 0) {
            allInputsValid = false;
            lbl_error.setText("El valor de iteraciones a dibujar debe ser mayor a cero.");
        }

        return allInputsValid;
    }

    public void setUpSimulationDynamicsTableColumns(List<VehicleRow> vehicles) {
        tv_queue.getItems().clear();
        tv_queue.getColumns().clear();

        // Creación dinámica de las columnas (parte estática)
        initialSimulationColumnsHeadText.forEach(columnName -> addTableViewColumn(columnName, tv_queue.getColumns().size(), tv_queue));

        // Creación dinámica de las columnas (parte dinámica)
        vehicles.forEach(vehicleRow -> vehicleRow.getTableViewHeaders().forEach(header -> addTableViewColumn(header, tv_queue.getColumns().size(), tv_queue)));
    }

    private void addTableViewColumn(String columnName, int index, TableView<List<String>> tv_view) {
        TableColumn<List<String>, String> col = new TableColumn<>(columnName);
        addTableColumn(index, col, tv_view);
    }

    private void addTableColumn(int index, TableColumn<List<String>, String> col, TableView<List<String>> tv_queue) {
        final int colIndex = index;
        col.setMinWidth(80);
        col.setCellValueFactory(data -> {
            List<String> rowValues = data.getValue();
            String cellValue;
            if (colIndex < rowValues.size()) {
                cellValue = rowValues.get(colIndex);
            } else {
                cellValue = "";
            }
            return new ReadOnlyStringWrapper(cellValue);
        });
        tv_queue.getColumns().add(col);
    }

    @Override
    public Double getMinutesFrom() {
        return getSafeDouble(txt_minute_from);
    }

    @Override
    public Integer getIterationsToDraw() {
        return getSafeInt(txt_iteration_to);
    }

    public void onRestart(ActionEvent actionEvent) {
        txt_blocked_crossing_percentage.setText("");
        txt_particular_media.setText("1");
        txt_taxi_media.setText("2");
        txt_bus_media.setText("7");
        txt_light_change_time.setText("10");
        txt_crossing_time.setText("0.3");
        txt_simulation_time.setText("3600");
        txt_minute_from.setText("10");
        txt_iteration_to.setText("50");
    }
}
