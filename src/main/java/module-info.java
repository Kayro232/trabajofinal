module org.example.trabajo_final {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.trabajo_final to javafx.fxml;
    exports org.example.trabajo_final;
    exports org.example.trabajo_final.CasoEs2;
    opens org.example.trabajo_final.CasoEs2 to javafx.fxml;
}