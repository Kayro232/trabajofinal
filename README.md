Sistema Concurrente de Venta de Entradas — Caso 3 (JavaFX)
 Descripción del Proyecto
Aplicación desarrollada en JavaFX que simula la gestión concurrente de ventas mediante vendedores (VendedorTask) que interactúan con un recurso compartido de gestión de inventario y caja (CajaVentas). Esta implementación sigue el patrón MVC estructurado bajo el paquete org.example.trabajo_final.CasoEs3.
 Requisitos del Sistema y Tecnologías
Lenguaje: Java (JDK 17 o superior)
Framework GUI: JavaFX 17 / 21
Concurrencia: javafx.concurrent.Task, Platform.runLater y ExecutorService
Gestor de Dependencias: Apache Maven
 Estructura del Proyecto (Caso 3)
src/main/java/org/example/trabajo_final/CasoEs3/
├── CajaVentas.java       # Recurso compartido / Modelo (Control de inventario y sección crítica)
├── Caso3Controller.java  # Controlador FXML que gestiona la UI e hilo principal
├── VendedorTask.java     # Tarea concurrente (hilos en segundo plano por vendedor)
└── MainCaso3.java        # Punto de entrada para la aplicación del Caso 3

 Instrucciones de Ejecución
Opción 1: Desde el IDE (IntelliJ IDEA / Eclipse / NetBeans)
Clona el repositorio e importa la carpeta trabajofinal-caso3 como un proyecto Maven.
Verifica que la versión asignada del SDK sea JDK 17 o superior.
Ejecute la clase principal MainCaso3.java ubicada en: src/main/java/org/example/trabajo_final/CasoEs3/MainCaso3.java
Opción 2: Desde la Terminal (Maven Wrapper)
En la raíz de la carpeta del caso 3, ejecuta:
# En Windows (CMD / PowerShell):
.\mvnw.cmd clean javafx:run

# En Linux / macOS:
./mvnw clean javafx:run

 Arquitectura y Sincronización
1. Recurso Compartido (CajaVentas.java)
Centraliza el número de entradas disponibles y procesa las peticiones mediante bloques/métodos sincronizados (synchronized). Esto asegura que la verificación y la deducción de inventario se realicen de manera atómica, impidiendo condiciones de carrera (race conditions).
public synchronized boolean procesarVenta(String nombreVendedor) {
    if (entradasDisponibles > 0) {
        entradasDisponibles--;
        ventasExitosas++;
        return true;
    } else {
        ventasRechazadas++;
        return false;
    }
}
2. Hilos de Venta (VendedorTask.java)
Cada vendedor se ejecuta de forma independiente en un hilo secundario implementando Task<Void>. Al completar o rechazar cada solicitud, delega la actualización de la interfaz gráfica al hilo principal de JavaFX usando Platform.runLater().

 Criterios de Aceptación y Verificación
Al ejecutar las simulaciones desde la vista caso3_view.fxml:
Invariante de Inventario: $$\text{Entradas Disponibles} + \text{Ventas Exitosas} = \text{Inventario Inicial}$$
Sin bloqueos en la interfaz: La vista responde en todo momento durante el procesamiento en segundo plano.
Control de concurrencia: Se deshabilita la opción de reanimar o duplicar ejecuciones simultáneas mientras una simulación está activa.
Reinicio seguro: El botón de reinicio restablece las métricas en CajaVentas y actualiza los indicadores visibles en la UI.
