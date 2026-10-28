package org.example.trabajo_final;

public class EventoController {
    private static final int TOTAL = 20;

    private int disponibles = TOTAL;
    private int confirmadas = 0;
    private int rechazadas = 0;

    //  Verificar y descontar en la MISMA sección crítica
    public synchronized boolean reservar() {
        if (disponibles > 0) {
            disponibles--;
            confirmadas++;
            return true;
        } else {
            rechazadas++;
            return false;
        }
    }

    //  Lectura protegida: devuelve {disponibles, confirmadas, rechazadas}
    public synchronized int[] snapshot() {
        return new int[]{disponibles, confirmadas, rechazadas};
    }

    //  Restablece TODOS los contadores
    public synchronized void reiniciar() {
        disponibles = TOTAL;
        confirmadas = 0;
        rechazadas = 0;
    }

}
