module com.example.bordercontrol {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.logging;



    opens com.example.pj23_1188_17 to javafx.fxml;
    opens com.example.pj23_1188_17.controller to javafx.fxml;
    exports com.example.pj23_1188_17;
}