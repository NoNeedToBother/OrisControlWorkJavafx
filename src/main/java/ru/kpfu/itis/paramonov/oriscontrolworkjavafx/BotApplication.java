package ru.kpfu.itis.paramonov.oriscontrolworkjavafx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class BotApplication extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    private static Stage primaryStage = null;

    @Override
    public void start(Stage primaryStage) throws IOException {
        this.primaryStage = primaryStage;
        FXMLLoader loader = new FXMLLoader(BotApplication.class.getResource("/bot_page.fxml"));
        VBox vBox = loader.load();
        Scene scene = new Scene(vBox);

        primaryStage.setTitle("Bot");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }
}
