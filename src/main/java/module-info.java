module org.example.trabajo_final {
    requires javafx.controls;
    requires javafx.fxml;

    opens org.example.trabajo_final to javafx.fxml;
    exports org.example.trabajo_final;

    // Abrir y exportar el paquete del Caso de Estudio 3 a JavaFX
    exports org.example.trabajo_final.CasoEs3;
    opens org.example.trabajo_final.CasoEs3 to javafx.fxml;


}