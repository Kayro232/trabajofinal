Sistema Concurrente de Reserva de Entradas (JavaFX)
 Descripción del Proyecto
Aplicación desarrollada en JavaFX que simula un sistema concurrente de reserva de entradas para un evento. El sistema cuenta con un inventario inicial de 20 entradas y tres agentes simultáneos en segundo plano que intentan realizar 10 reservas cada uno.
 Requisitos del Sistema y Tecnologías
Lenguaje: Java (JDK 17 o superior)
Framework GUI: JavaFX 17 / 21
Concurrencia: javafx.concurrent.Task, Platform.runLater y ExecutorService
Gestor de Dependencias: Apache Maven
 Instrucciones de Ejecución
Opción 1: Desde el IDE (IntelliJ IDEA / Eclipse / NetBeans)
Clona el repositorio desde GitHub:
git clone https://github.com/TU_USUARIO/TU_REPOSITORIO.git

Abre la carpeta del proyecto en tu IDE como proyecto Maven.
Asegúrate de tener configurado JDK 17 o superior.
Ejecuta la clase principal HelloApplication.java ubicada en src/main/java/org/example/trabajo_final/.
Opción 2: Desde la Terminal (Maven Wrapper)
En la raíz del proyecto, ejecuta:
# En Windows (CMD / PowerShell):
.\mvnw.cmd clean javafx:run

# En Linux / macOS:
./mvnw clean javafx:run

Arquitectura y Sincronización
Sección Crítica (synchronized)
Para evitar condiciones de carrera (race conditions) y sobreventas de inventario, la verificación de disponibilidad y el descuento de entradas están encapsulados en un método atómico y sincronizado dentro de EventoController.java:
public synchronized boolean intentarReservar(String nombreAgente) {
    if (entradasDisponibles > 0) {
        entradasDisponibles--;
        reservasConfirmadas++;
        return true;
    } else {
        solicitudesRechazadas++;
        return false;
    }
}

Agentes Concurrentes
Los 3 agentes ejecutan sus peticiones en segundo plano de manera simultánea e independiente mediante la clase Task<Void>. Cada agente intenta procesar 10 solicitudes de reserva sin congelar o bloquear la interfaz de usuario.
 Resultados Esperados y Pruebas de Aceptación
El sistema garantiza los siguientes criterios de consistencia en cada simulación:
Invariante de Inventario:
$$
\text{Entradas Disponibles} + \text{Reservas Confirmadas} = 20
$$
Resultado al finalizar cada simulación completa:
Entradas Disponibles: 0
Reservas Confirmadas: 20
Solicitudes Rechazadas: 10
Total de Solicitudes Procesadas: 30 (3 agentes × 10 intentos)
Control de Inicios Duplicados: Se bloquean los botones de inicio durante la simulación activa para evitar la creación de agentes adicionales o ejecuciones concurrentes no deseadas.
Restablecimiento: El botón Reiniciar reestablece de forma segura los valores al estado inicial (Disponibles: 20, Confirmadas: 0, Rechazadas: 0).
