package org.example.trabajo_final.CasoEs2;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class PedidosController {

    private static final int TOTAL_PEDIDOS = 30;
    private static final int ESPERA_MS = 200;

    @FXML
    private Button btnIniciar;

    @FXML
    private Button btnCancelar;

    @FXML
    private ProgressBar barraProgreso;

    @FXML
    private Label lblEstado;

    @FXML
    private Label lblPorcentaje;

    @FXML
    private Label lblContador;

    @FXML
    private Label lblMensaje;

    private enum Estado {
        LISTO("Listo", "#475569", "#e2e8f0"),
        EN_EJECUCION("En ejecución", "#1d4ed8", "#dbeafe"),
        CANCELANDO("Cancelando", "#a16207", "#fef3c7"),
        CANCELADO("Cancelado", "#b45309", "#ffedd5"),
        COMPLETADO("Completado", "#15803d", "#dcfce7"),
        ERROR("Error", "#b91c1c", "#fee2e2");

        final String texto;
        final String color;
        final String fondo;

        Estado(String texto, String color, String fondo) {
            this.texto = texto;
            this.color = color;
            this.fondo = fondo;
        }
    }

    /*
     * Cada ejecución tiene sus propios datos y un trabajador nuevo.
     * Los campos compartidos usan AtomicInteger y AtomicBoolean.
     */
    private static final class Ejecucion {
        final AtomicInteger completados = new AtomicInteger();
        final AtomicBoolean cancelacion = new AtomicBoolean();

        Thread trabajador;
        Throwable error;
    }

    // Solo se modifican desde el hilo JavaFX.
    private Ejecucion ejecucionActual;
    private boolean cerrando;
    private int numeroEjecucion;

    @FXML
    private void initialize() {
        actualizarProgreso(0);
        cambiarEstado(Estado.LISTO);
        btnCancelar.setDisable(true);
        lblMensaje.setText(
                "Presiona Iniciar para procesar los 30 pedidos."
        );
    }

    @FXML
    private void iniciar() {
        /*
         * Impide duplicados incluso si se invoca este método
         * cuando el botón está deshabilitado.
         */
        if (ejecucionActual != null || cerrando) {
            return;
        }

        Ejecucion ejecucion = new Ejecucion();
        ejecucionActual = ejecucion;
        numeroEjecucion++;

        actualizarProgreso(0);
        cambiarEstado(Estado.EN_EJECUCION);

        lblMensaje.setText("Procesando pedidos en segundo plano…");
        btnIniciar.setDisable(true);
        btnCancelar.setDisable(false);

        ejecucion.trabajador = new Thread(
                () -> procesar(ejecucion),
                "procesador-pedidos-" + numeroEjecucion
        );

        /*
         * El trabajador termina mediante cancelación cooperativa.
         * No depende de que la JVM lo elimine por ser daemon.
         */
        ejecucion.trabajador.setDaemon(false);
        ejecucion.trabajador.start();

        /*
         * Este observador confirma la terminación del trabajador.
         * join() se ejecuta fuera del hilo JavaFX.
         */
        Thread observador = new Thread(() -> {
            try {
                ejecucion.trabajador.join();

                System.out.println(
                        ejecucion.trabajador.getName()
                                + " terminó. Pedidos completados: "
                                + ejecucion.completados.get()
                );

                Platform.runLater(() -> finalizar(ejecucion));

            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }, "observador-pedidos-" + numeroEjecucion);

        observador.setDaemon(true);
        observador.start();
    }

    private void procesar(Ejecucion ejecucion) {
        try {
            for (int pedido = 1; pedido <= TOTAL_PEDIDOS; pedido++) {
                if (debeCancelar(ejecucion)) {
                    return;
                }

                // Simula el procesamiento de un pedido.
                Thread.sleep(ESPERA_MS);

                /*
                 * Si se cancela durante la espera, el pedido
                 * no se registra como completado.
                 */
                if (debeCancelar(ejecucion)) {
                    return;
                }

                int cantidad = ejecucion.completados.incrementAndGet();

                // Las modificaciones de controles ocurren en JavaFX.
                Platform.runLater(() -> {
                    if (!cerrando && ejecucionActual == ejecucion) {
                        actualizarProgreso(cantidad);
                    }
                });
            }

        } catch (InterruptedException ex) {
            // La interrupción de sleep forma parte de la cancelación.
            ejecucion.cancelacion.set(true);
            Thread.currentThread().interrupt();

        } catch (Exception ex) {
            ejecucion.error = ex;
        }
    }

    private boolean debeCancelar(Ejecucion ejecucion) {
        return ejecucion.cancelacion.get()
                || Thread.currentThread().isInterrupted();
    }

    @FXML
    private void cancelar() {
        Ejecucion ejecucion = ejecucionActual;

        if (ejecucion == null || ejecucion.cancelacion.get()) {
            return;
        }

        cambiarEstado(Estado.CANCELANDO);
        lblMensaje.setText(
                "Cancelación solicitada. Esperando la terminación…"
        );

        btnCancelar.setDisable(true);

        /*
         * Iniciar continúa deshabilitado hasta que el observador
         * confirme que el trabajador terminó.
         */
        ejecucion.cancelacion.set(true);
        ejecucion.trabajador.interrupt();
    }

    private void finalizar(Ejecucion ejecucion) {
        if (cerrando || ejecucionActual != ejecucion) {
            return;
        }

        /*
         * Este método se ejecuta después del join del observador.
         * Por tanto, el trabajador ya terminó efectivamente.
         */
        int cantidad = ejecucion.completados.get();
        actualizarProgreso(cantidad);

        if (ejecucion.error != null) {
            cambiarEstado(Estado.ERROR);
            lblMensaje.setText(
                    "Ocurrió un error: "
                            + ejecucion.error.getClass().getSimpleName()
            );
            ejecucion.error.printStackTrace();

        } else if (cantidad == TOTAL_PEDIDOS) {
            cambiarEstado(Estado.COMPLETADO);
            lblMensaje.setText(
                    "Se completaron correctamente los 30 pedidos."
            );

        } else if (ejecucion.cancelacion.get()) {
            cambiarEstado(Estado.CANCELADO);
            lblMensaje.setText(
                    "Trabajador terminado. Se conservaron "
                            + cantidad + " pedidos completados."
            );

        } else {
            cambiarEstado(Estado.ERROR);
            lblMensaje.setText(
                    "El trabajador terminó sin completar los pedidos."
            );
        }

        ejecucionActual = null;

        btnIniciar.setDisable(false);
        btnCancelar.setDisable(true);
    }

    private void actualizarProgreso(int cantidad) {
        double progreso = (double) cantidad / TOTAL_PEDIDOS;

        barraProgreso.setProgress(progreso);
        lblContador.setText(cantidad + " / " + TOTAL_PEDIDOS + " pedidos");
        lblPorcentaje.setText(Math.round(progreso * 100) + " %");
    }

    private void cambiarEstado(Estado estado) {
        lblEstado.setText(estado.texto);
        lblEstado.setStyle(
                "-fx-text-fill: " + estado.color + ";" +
                        "-fx-background-color: " + estado.fondo + ";" +
                        "-fx-background-radius: 20;" +
                        "-fx-padding: 7 14;" +
                        "-fx-font-size: 13px;" +
                        "-fx-font-weight: bold;"
        );
    }

    public void cerrar() {
        if (cerrando) {
            return;
        }

        cerrando = true;

        Ejecucion ejecucion = ejecucionActual;

        if (ejecucion != null) {
            ejecucion.cancelacion.set(true);
            ejecucion.trabajador.interrupt();
        }

        // No se usa join() aquí: el cierre no bloquea JavaFX.
    }
}