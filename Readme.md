# 💻 Documentación Técnica: Caso de Estudio 2
## Procesamiento de Pedidos con Cancelación

---

### 1. Fundamentos Técnicos y Arquitectura

#### ⚙️ Procesamiento en Segundo Plano
> [!IMPORTANT]
> Los 30 pedidos se procesan secuencialmente en un hilo trabajador independiente del hilo JavaFX, manteniendo la interfaz operativa.

Cada pedido incluye una espera simulada de **200 ms** mediante `Thread.sleep(200)` y se registra como completado únicamente después de terminar su procesamiento.

El contador utiliza `AtomicInteger` y las actualizaciones de la interfaz se realizan mediante `Platform.runLater()`.

$$Progreso = \frac{PedidosCompletados}{30} \times 100$$

#### 🛑 Cancelación Cooperativa y Ciclo de Vida
> [!NOTE]
> La cancelación se gestiona mediante una bandera `AtomicBoolean` y la interrupción del trabajador con `interrupt()`. No se utiliza `Thread.stop()`.

Cuando el usuario presiona **Cancelar**, la aplicación:
1. Muestra el estado **Cancelando**.
2. Activa la solicitud de cancelación e interrumpe al trabajador.
3. Gestiona `InterruptedException` si la espera es interrumpida, sin contar ese pedido.
4. Confirma la terminación del trabajador mediante `join()` en un hilo observador.
5. Muestra **Cancelado** y habilita nuevamente el botón **Iniciar**.

Este flujo evita bloquear el hilo JavaFX y conserva la cantidad de pedidos completados. Cada nueva ejecución restablece el contador y crea un trabajador nuevo. Al cerrar la ventana se solicita la cancelación sin bloquear la interfaz.

---

### 2. Protocolo y Resultados de Pruebas de Aceptación

| Escenario de Prueba | Resultado Esperado y Obtenido | Estado |
| :--- | :--- | :---: |
| **Escenario 1:** Ejecución Completa | Los 30 pedidos finalizaron correctamente, el contador alcanzó **30/30**, el progreso llegó al **100 %** y se mostró **Completado**. | `Éxito` |
| **Escenario 2:** Cancelación Cooperativa | El trabajador terminó y se mostró **Cancelado**. El contador parcial quedó estable, sin registrar el pedido interrumpido. | `Éxito` |
| **Escenario 3:** Control de Inicios Duplicados | El botón **Iniciar** permaneció deshabilitado durante la ejecución, impidiendo iniciar otro trabajador simultáneamente. | `Éxito` |
| **Escenario 4:** Cancelación y Reinicio | Después de confirmar la terminación del trabajador, se habilitó **Iniciar**. La nueva ejecución restableció el contador y el progreso, utilizando un trabajador nuevo. | `Éxito` |
| **Escenario 5:** Cierre Limpio de Ventana | Al cerrar la ventana durante el procesamiento, se solicitó la cancelación y el proceso finalizó sin dejar trabajadores activos. | `Éxito` |
### 🛠️ Especificaciones del Entorno

* **Lenguaje y JDK:** Java 21.0.12.
* **Interfaz Gráfica:** JavaFX 21 (Controls & FXML).
* **Clase Principal:** `org.example.trabajo_final.Aplicacion`.
* **Controlador:** `org.example.trabajo_final.PedidosController`.
* **Vista FXML:** `caso2_view.fxml`.