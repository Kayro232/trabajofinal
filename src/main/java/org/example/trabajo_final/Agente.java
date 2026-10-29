package org.example.trabajo_final;

import javafx.concurrent.Task;

public class Agente extends Task<Void> {
    private final String nombre;
    private final HelloController controlador;

    public Agente(String nombre, HelloController controlador) {
        this.nombre = nombre;
        this.controlador = controlador;
    }

    @Override
    protected Void call() throws Exception {
        for (int i = 1; i <= 10; i++) {
            controlador.intentarReservar(nombre, i);
            
            // Pausa entre 50ms y 150ms para permitir alternancia de hilos
            Thread.sleep((long) (Math.random() * 100 + 50));
        }
        return null;
    }
}
