package org.example.trabajo_final.CasoEs3;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Caso3Controller {

    @FXML private Button btnIniciar;
    @FXML private Button btnCancelar;
    @FXML private Button btnReiniciar;

    @FXML private Label lblEstado;
    @FXML private Label lblMensaje;
    @FXML private Label lblTotalVentas;
    @FXML private Label lblRecaudacion;

    @FXML private Label lblEstadoVendedor1;
    @FXML private Label lblVentasVendedor1;
    @FXML private ProgressBar progresoVendedor1;

    @FXML private Label lblEstadoVendedor2;
    @FXML private Label lblVentasVendedor2;
    @FXML private ProgressBar progresoVendedor2;

    @FXML private Label lblEstadoVendedor3;
    @FXML private Label lblVentasVendedor3;
    @FXML private ProgressBar progresoVendedor3;

    @FXML private ListView<String> listaVentas;

    private final CajaVentas caja = new CajaVentas();
    private final List<VendedorTask> tareas = new ArrayList<>();
    private ExecutorService executor;

    @FXML
    public void initialize() {
        btnIniciar.setOnAction(e -> iniciarSimulacion());
        btnCancelar.setOnAction(e -> cancelarSimulacion());
        btnReiniciar.setOnAction(e -> reiniciarSimulacion());
    }

    private void iniciarSimulacion() {
        btnIniciar.setDisable(true);
        btnCancelar.setDisable(false);
        btnReiniciar.setDisable(true);

        lblEstado.setText("En progreso");
        lblMensaje.setText("Procesando ventas concurrentes...");

        tareas.clear();
        executor = Executors.newFixedThreadPool(3);

        ProgressBar[] barras = {progresoVendedor1, progresoVendedor2, progresoVendedor3};
        Label[] lblEstados = {lblEstadoVendedor1, lblEstadoVendedor2, lblEstadoVendedor3};
        Label[] lblVentas = {lblVentasVendedor1, lblVentasVendedor2, lblVentasVendedor3};

        for (int i = 0; i < 3; i++) {
            int numVendedor = i + 1;
            final int index = i; // <-- Declaramos index como constante final para los arreglos
            VendedorTask task = new VendedorTask("Vendedor " + numVendedor, caja);
            tareas.add(task);

            barras[index].progressProperty().bind(task.progressProperty());
            lblEstados[index].textProperty().bind(task.messageProperty());

            task.progressProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != null && newVal.doubleValue() >= 0) {
                    int ventasActuales = (int) Math.round(newVal.doubleValue() * 5);
                    lblVentas[index].setText(ventasActuales + " / 5 ventas");

                    if (oldVal != null && newVal.doubleValue() > oldVal.doubleValue()) {
                        actualizarUI();
                        Platform.runLater(() ->
                                listaVentas.getItems().add(
                                        "Venta confirmada por Vendedor " + numVendedor + " (C$ 100.00)"
                                )
                        );
                    }
                }
            });

            task.setOnSucceeded(this::comprobarFinalizacion);
            task.setOnCancelled(this::comprobarFinalizacion);

            executor.submit(task);
        }
    }

    private void cancelarSimulacion() {
        btnCancelar.setDisable(true);
        lblEstado.setText("Cancelando");
        lblMensaje.setText("Deteniendo vendedores de forma segura...");

        for (VendedorTask task : tareas) {
            task.cancel();
        }

        new Thread(() -> {
            if (executor != null) {
                executor.shutdownNow();
            }
            Platform.runLater(() -> {
                actualizarUI();
                lblEstado.setText("Cancelado");
                lblMensaje.setText("Simulación detenida. Solo se registraron las ventas confirmadas.");
                btnReiniciar.setDisable(false);
            });
        }).start();
    }

    private void comprobarFinalizacion(WorkerStateEvent event) {
        boolean todasTerminadas = tareas.stream().allMatch(Task::isDone);
        if (todasTerminadas) {
            actualizarUI();
            if (!lblEstado.getText().equals("Cancelado")) {
                lblEstado.setText("Completado");
                lblMensaje.setText("Simulación finalizada exitosamente.");
            }
            btnCancelar.setDisable(true);
            btnReiniciar.setDisable(false);
            if (executor != null) {
                executor.shutdown();
            }
        }
    }

    private void reiniciarSimulacion() {
        caja.reiniciar();
        listaVentas.getItems().clear();

        ProgressBar[] barras = {progresoVendedor1, progresoVendedor2, progresoVendedor3};
        Label[] lblEstados = {lblEstadoVendedor1, lblEstadoVendedor2, lblEstadoVendedor3};
        Label[] lblVentas = {lblVentasVendedor1, lblVentasVendedor2, lblVentasVendedor3};

        for (int i = 0; i < 3; i++) {
            barras[i].progressProperty().unbind();
            barras[i].setProgress(0.0);
            lblEstados[i].textProperty().unbind();
            lblEstados[i].setText("En espera");
            lblVentas[i].setText("0 / 5 ventas");
        }

        actualizarUI();
        lblEstado.setText("Listo");
        lblMensaje.setText("Presiona Iniciar para comenzar una nueva simulación.");

        btnIniciar.setDisable(false);
        btnReiniciar.setDisable(true);
    }

    private void actualizarUI() {
        Platform.runLater(() -> {
            lblTotalVentas.setText(caja.getTotalVentas() + " / 15");
            lblRecaudacion.setText(String.format("C$%.2f", caja.getTotalRecaudacion()));
        });
    }
}