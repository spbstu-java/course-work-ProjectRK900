import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;


public class JavaFXGUI extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("Курсовая работа");

        URL url = new File("GUI/src/gui.fxml").toURI().toURL();
        Parent root = FXMLLoader.load(url);

        var scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void init() throws Exception {
        super.init();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
    }
}