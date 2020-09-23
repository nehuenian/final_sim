import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import view.Controller;

import java.net.URL;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        URL location = getClass().getResource("./resources/mainApp.fxml");
        FXMLLoader loader = new FXMLLoader(location);
        Parent root = loader.load();
        primaryStage.setTitle("SIM - UTN FRC - Queue System");
        primaryStage.setScene(new Scene(root));

        Controller controller = loader.getController();
        primaryStage.setOnShown(e -> controller.onWindowLoad(new ActionEvent()));
        primaryStage.show();

        primaryStage.show();

    }


    public static void main(String[] args) {
        launch(args);
    }
}
