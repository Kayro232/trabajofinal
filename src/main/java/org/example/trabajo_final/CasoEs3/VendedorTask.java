package org.example.trabajo_final.CasoEs3;

import javafx.concurrent.Task;

public class VendedorTask extends Task<Void> {
    private final String nombreVendedor;
    private final CajaVentas caja;
    private final int MAX_VENTAS = 5;

    public VendedorTask(String nombreVendedor, CajaVentas caja) {
        this.nombreVendedor = nombreVendedor;
        this.caja = caja;
    }

    @Override
    protected Void call() throws Exception {
        for (int i = 1; i <= MAX_VENTAS; i++) {
            if (isCancelled()) {
                updateMessage("Cancelado");
                break;
            }

            // Simulación de tiempo por venta (1 segundo)
            Thread.sleep(1000);

            if (isCancelled()) {
                updateMessage("Cancelado");
                break;
            }

            // Registro seguro de la venta completada
            caja.registrarVenta();

            updateProgress(i, MAX_VENTAS);
            updateMessage("Ventas: " + i + "/" + MAX_VENTAS);
        }

        if (!isCancelled()) {
            updateMessage("Completado");
        }
        return null;
    }

    public String getNombreVendedor() {
        return nombreVendedor;
    }
}