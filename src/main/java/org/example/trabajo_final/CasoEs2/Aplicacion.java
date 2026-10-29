package org.example.trabajo_final.CasoEs2;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Aplicacion extends Application {

    private PedidosController controlador;

    @Override
    public void start(Stage ventana) throws IOException {
        FXMLLoader cargador = new FXMLLoader(
                Aplicacion.class.getResource(
                        "/org/example/trabajo_final/caso2_view.fxml"
                )
        );

        Parent raiz = cargador.load();
        controlador = cargador.getController();

        ventana.setTitle("Procesamiento de pedidos");
        ventana.setScene(new Scene(raiz, 660, 540));
        ventana.setMinWidth(600);
        ventana.setMinHeight(540);

        ventana.setOnCloseRequest(evento -> controlador.cerrar());

        ventana.show();
    }

    @Override
    public void stop() {
        if (controlador != null) {
            controlador.cerrar();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}