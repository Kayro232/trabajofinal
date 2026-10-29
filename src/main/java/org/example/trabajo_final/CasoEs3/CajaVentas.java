package org.example.trabajo_final.CasoEs3;

public class CajaVentas {
    private int totalVentas;
    private double totalRecaudacion;
    private static final double PRECIO_VENTA = 100.0;

    public CajaVentas() {
        this.totalVentas = 0;
        this.totalRecaudacion = 0.0;
    }

    public synchronized void registrarVenta() {
        this.totalVentas++;
        this.totalRecaudacion = this.totalVentas * PRECIO_VENTA;
    }

    public synchronized int getTotalVentas() {
        return totalVentas;
    }

    public synchronized double getTotalRecaudacion() {
        return totalRecaudacion;
    }

    public synchronized void reiniciar() {
        this.totalVentas = 0;
        this.totalRecaudacion = 0.0;
    }
}