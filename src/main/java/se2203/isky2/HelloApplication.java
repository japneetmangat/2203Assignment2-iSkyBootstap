package se2203.isky2;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 850, 600);
        stage.setTitle("Initialize iSky (one-time)");
        stage.getIcons().add(
                new Image(getClass().getResourceAsStream("/se2203/isky2/WesternLogo.png"))
        );
        stage.setScene(scene);
        stage.show();
    }
}
