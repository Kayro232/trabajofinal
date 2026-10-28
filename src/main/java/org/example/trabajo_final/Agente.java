package org.example.trabajo_final;

public class Agente {

    public static final int TOTAL_ENTRADAS = 20;
    private String nombre;

    public Agente(String nombre) {
        this.nombre = nombre;
    }

    // Metodo de validación
    public static boolean verificarInventario(int entradasDisponibles, int reservasConfirmadas) {
        if (entradasDisponibles < 0 || reservasConfirmadas < 0) {
            System.out.println("Error: ¡Hay inventario negativo!");
            return false;
        }

        if (entradasDisponibles + reservasConfirmadas != TOTAL_ENTRADAS) {
            System.out.println("Error: La suma no da 20.");
            return false;
        }

        System.out.println("Todo correcto: El inventario es válido.");
        return true;
    }

    // Simulación del comportamiento del agente al solicitar una entrada
    public void intentarReserva(int entradasDisponibles, int reservasConfirmadas, int cantidad) {

        // Comprueba si hay disponibilidad suficiente antes de modificar algún valor
        if (entradasDisponibles >= cantidad) {
            entradasDisponibles -= cantidad;
            reservasConfirmadas += cantidad;
            System.out.println("[" + this.nombre + "] Reserva con éxito: " + cantidad + " entrada(s).");
        } else {
            System.out.println("[" + this.nombre + "] No hay suficientes entradas.");
        }

        // Se ejecuta la comprobación de variantes luego de una transacción
        verificarInventario(entradasDisponibles, reservasConfirmadas);
    }
}