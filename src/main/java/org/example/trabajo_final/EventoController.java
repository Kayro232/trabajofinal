package org.example.trabajo_final;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.application.Platform;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class EventoController {

    @FXML private Label lblDisponibles;
    @FXML private Label lblConfirmadas;
    @FXML private Label lblRechazadas;
    @FXML private Label lblEstado;
    @FXML private Button btnIniciar;
    @FXML private Button btnReiniciar;

    // Variables de estado del recurso compartido
    private int entradasDisponibles = 20;
    private int reservasConfirmadas = 0;
    private int solicitudesRechazadas = 0;

    private boolean simulacionEnCurso = false;

    @FXML
    public void initialize() {
        actualizarUI();
    }

    // Método sincronizado para la sección crítica
    public synchronized boolean intentarReservar(String nombreAgente) {
        if (entradasDisponibles > 0) {
            entradasDisponibles--;
            reservasConfirmadas++;
            return true;
        } else {
            solicitudesRechazadas++;
            return false;
        }
    }

    @FXML
    private void handleIniciar() {
        if (simulacionEnCurso) {
            return; // Evita inicios duplicados
        }

        simulacionEnCurso = true;
        btnIniciar.setDisable(true);
        btnReiniciar.setDisable(true);
        lblEstado.setText("Estado: Simulación en ejecución...");

        ExecutorService executor = Executors.newFixedThreadPool(3);
        AtomicInteger tareasCompletadas = new AtomicInteger(0);

        Runnable alActualizar = this::actualizarUI;

        for (int i = 1; i <= 3; i++) {
            // Se pasa 'this' (el controlador) como referencia al agente
            Agente agente = new Agente("Agente " + i, this, alActualizar);
            
            agente.setOnSucceeded(e -> {
                if (tareasCompletadas.incrementAndGet() == 3) {
                    finalizarSimulacion(executor);
                }
            });

            agente.setOnFailed(e -> {
                if (tareasCompletadas.incrementAndGet() == 3) {
                    finalizarSimulacion(executor);
                }
            });

            executor.submit(agente);
        }
    }

    @FXML
    private void handleReiniciar() {
        if (simulacionEnCurso) {
            return;
        }
        reiniciarDatos();
        actualizarUI();
        lblEstado.setText("Estado: Sistema reiniciado. Listo para iniciar.");
    }

    private synchronized void reiniciarDatos() {
        this.entradasDisponibles = 20;
        this.reservasConfirmadas = 0;
        this.solicitudesRechazadas = 0;
    }

    private void finalizarSimulacion(ExecutorService executor) {
        executor.shutdown();
        Platform.runLater(() -> {
            simulacionEnCurso = false;
            btnIniciar.setDisable(false);
            btnReiniciar.setDisable(false);
            lblEstado.setText("Estado: Simulación finalizada.");
        });
    }

    private synchronized void actualizarUI() {
        int disp = entradasDisponibles;
        int conf = reservasConfirmadas;
        int rech = solicitudesRechazadas;

        Platform.runLater(() -> {
            lblDisponibles.setText(String.valueOf(disp));
            lblConfirmadas.setText(String.valueOf(conf));
            lblRechazadas.setText(String.valueOf(rech));
        });
    }
}
