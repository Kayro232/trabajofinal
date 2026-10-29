package org.example.trabajo_final;

import javafx.concurrent.Task;
import javafx.application.Platform;

public class Agente extends Task<Void> {
    private final String nombre;
    private final EventoController controlador;
    private final Runnable callbackActualizacion;

    public Agente(String nombre, EventoController controlador, Runnable callbackActualizacion) {
        this.nombre = nombre;
        this.controlador = controlador;
        this.callbackActualizacion = callbackActualizacion;
    }

    @Override
    protected Void call() throws Exception {
        for (int i = 0; i < 10; i++) {
            // Intento de reserva directamente en el controlador
            controlador.intentarReservar(nombre);
            
            // Actualización segura en UI
            Platform.runLater(callbackActualizacion);

            Thread.sleep((long) (Math.random() * 100 + 50));
        }
        return null;
    }
}
