package org.example.trabajo_final.CasoEs3;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainCaso3 extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/trabajo_final/caso3_view.fxml"));
        Scene scene = new Scene(loader.load());

        primaryStage.setTitle("Caso de Estudio 3 - Caja de Ventas Concurrente");
        primaryStage.setScene(scene);
        primaryStage.setResizable(true);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}