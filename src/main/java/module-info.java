module org.openjfx.dreamlandvendors {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;
    // Use MySQL connector without requiring a specific module name
    // requires com.mysql.cj;

    opens org.openjfx.dreamlandvendors to javafx.fxml;
    opens org.openjfx.dreamlandvendors.login to javafx.fxml;
    opens org.openjfx.dreamlandvendors.admin to javafx.fxml;
    opens org.openjfx.dreamlandvendors.vendor to javafx.fxml;
    opens org.openjfx.dreamlandvendors.model to javafx.base;

    exports org.openjfx.dreamlandvendors;
    exports org.openjfx.dreamlandvendors.login;
    exports org.openjfx.dreamlandvendors.admin;
    exports org.openjfx.dreamlandvendors.vendor;
}