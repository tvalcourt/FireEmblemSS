package ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * Created by Gatrie on 2/13/2016.
 */
public class FireEmblemUI extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        AnchorPane root = FXMLLoader.load(getClass().getResource("/ui/Main.fxml"));
        Scene scene = new Scene(root);

        primaryStage.setTitle("Fire Emblem Guide");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
