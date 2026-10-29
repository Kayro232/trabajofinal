package org.example.trabajo_final;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.application.Platform;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class HelloController {

    @FXML private Label disponiblesLabel;
    @FXML private Label confirmadasLabel;
    @FXML private Label rechazadasLabel;
    @FXML private Label estadoLabel;
    @FXML private Button iniciarButton;
    @FXML private TextArea registroArea;

    private int disponibles = 20;
    private int confirmadas = 0;
    private int rechazadas = 0;

    private boolean simulacionEnCurso = false;

    @FXML
    public void initialize() {
        actualizarUI();
    }

    // Sección crítica sincronizada
    public synchronized boolean intentarReservar(String agente, int intento) {
        boolean exito;
        if (disponibles > 0) {
            disponibles--;
            confirmadas++;
            exito = true;
            agregarLog(agente + " - Solicitud #" + intento + ": RESERVA CONFIRMADA. (Quedan " + disponibles + ")");
        } else {
            rechazadas++;
            exito = false;
            agregarLog(agente + " - Solicitud #" + intento + ": RECHAZADA (Sin entradas disponibles).");
        }
        actualizarUI();
        return exito;
    }

    @FXML
    private void handleIniciar() {
        if (simulacionEnCurso) {
            return; // Bloquea lanzamientos redundantes
        }

        reiniciarSimulacion();

        simulacionEnCurso = true;
        iniciarButton.setDisable(true);
        estadoLabel.setText("Estado: Simulación en ejecución...");
        agregarLog("=== INICIO DE SIMULACIÓN (3 AGENTES CONCURRENTES) ===");

        ExecutorService executor = Executors.newFixedThreadPool(3);
        AtomicInteger tareasCompletadas = new AtomicInteger(0);

        for (int i = 1; i <= 3; i++) {
            Agente agente = new Agente("Agente " + i, this);

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

    private synchronized void reiniciarSimulacion() {
        this.disponibles = 20;
        this.confirmadas = 0;
        this.rechazadas = 0;
        registroArea.clear();
        actualizarUI();
    }

    private void finalizarSimulacion(ExecutorService executor) {
        executor.shutdown();
        Platform.runLater(() -> {
            simulacionEnCurso = false;
            iniciarButton.setDisable(false);
            estadoLabel.setText("Estado: Simulación finalizada.");
            agregarLog("=== SIMULACIÓN FINALIZADA ===");
        });
    }

    private synchronized void actualizarUI() {
        int d = disponibles;
        int c = confirmadas;
        int r = rechazadas;

        Platform.runLater(() -> {
            disponiblesLabel.setText(String.valueOf(d));
            confirmadasLabel.setText(String.valueOf(c));
            rechazadasLabel.setText(String.valueOf(r));
        });
    }

    private void agregarLog(String mensaje) {
        Platform.runLater(() -> registroArea.appendText(mensaje + "\n"));
    }
}
