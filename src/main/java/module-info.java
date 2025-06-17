module org.openjfx.dreamlandvendors {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.openjfx.dreamlandvendors to javafx.fxml;
    exports org.openjfx.dreamlandvendors;
}