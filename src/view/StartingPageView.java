package view;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.util.List;

public class StartingPageView {

    public TextField txt_iteration_to;
    public TextField txt_minute_from;
    public Button btn_start_simulation;
    public Button btn_reset_data;
    public Label lbl_error;

    public TableView<List<String>> tv_queue;

    public TextField txt_simulation_time;
    public TextField txt_blocked_crossing_percentage;
    public TextField txt_particular_media;
    public TextField txt_taxi_media;
    public TextField txt_bus_media;
    public TextField txt_light_change_time;
    public TextField txt_crossing_time;

}
