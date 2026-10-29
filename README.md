# 💻 Documentación Técnica: Caso de Estudio 3
## Caja de Ventas Concurrente con Control de Hilos

---

### 1. Fundamentos Técnicos y Arquitectura

#### 🔒 Sincronización de Recursos Compartidos
> [!IMPORTANT]
> Para garantizar la integridad y consistencia de los datos en un entorno concurrente con tres vendedores trabajando simultáneamente, se implementó una clase de modelo `CajaVentas` protegida mediante bloques y métodos con la palabra clave `synchronized`.

Cada vez que un hilo (`VendedorTask`) completa la transacción de un cliente, incrementa el contador de ventas y calcula la recaudación equivalente dentro de una misma sección crítica atómica:

$$Recaudación = Ventas \times C\$100$$

Esto evita condiciones de carrera (*Race Conditions*) y garantiza que en ningún momento una lectura concurrente muestre una inconsistencia entre el total de ventas y el dinero en caja.

#### 🛑 Cancelación Cooperativa y Ciclo de Vida
> [!NOTE]
> La cancelación de las tareas se gestiona de forma cooperativa mediante la comprobación del método `isCancelled()` dentro del bucle de procesamiento de cada `VendedorTask`.

Cuando el usuario presiona el botón **Cancelar**, la interfaz realiza el siguiente protocolo:
1. Envía una señal de interrupción a los hilos a través de `task.cancel()`.
2. Ejecuta un `executor.shutdownNow()` en un hilo de ejecución secundario.

Este flujo evita bloquear el hilo de eventos de JavaFX (*JavaFX Application Thread*), asegura que ninguna venta a medio procesar quede registrada a medias y confirma la terminación efectiva de todos los trabajadores antes de habilitar el botón de **Reiniciar datos**.

---

### 2. Protocolo y Resultados de Pruebas de Aceptación

| Escenario de Prueba | Resultado Esperado y Obtenido | Estado |
| :--- | :--- | :---: |
| **Escenario 1:** Ejecución Completa Exitosa | Las 3 tareas finalizaron correctamente alcanzando la meta de 15 ventas confirmadas y **C$ 1,500.00** de recaudación total. | `Éxito` |
| **Escenario 2:** Cancelación Cooperativa | Las barras de progreso se detuvieron de inmediato. El total de ventas registradas coincidió exactamente con la recaudación ($Total = Ventas \times C\$100$), sin registros parciales. | `Éxito` |
| **Escenario 3:** Control de Inicios Duplicados | El botón *Iniciar simulación* se deshabilita automáticamente al arrancar el proceso, impidiendo la creación de múltiples Pools de hilos concurrentes. | `Éxito` |
| **Escenario 4:** Cancelación y Restablecimiento | Al presionar *Reiniciar datos*, los contadores volvieron a cero, las barras de progreso se restablecieron y se desvincularon las propiedades para una nueva ejecución limpia. | `Éxito` |
| **Escenario 5:** Cierre Limpio de Ventana | Al cerrar la ventana mediante `setOnCloseRequest`, se ejecutó `executor.shutdownNow()`, finalizando el proceso en la JVM sin dejar hilos huérfanos. | `Éxito` |

---

### 🛠️ Especificaciones del Entorno

* **Lenguaje y JDK:** ![Java](https://shields.io) 21
* **Interfaz Gráfica:** ![JavaFX](https://shields.io) (Controls & FXML)
* **Clase Principal:** `org.example.trabajo_final.CasoEs3.MainCaso3`
* **Vista FXML:** `caso3_view.fxml`
*
