module se2203.isky2 {
    requires javafx.controls;
    requires javafx.fxml;


    opens se2203.isky2 to javafx.fxml;
    exports se2203.isky2;
}