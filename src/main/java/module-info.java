module ru.kpfu.itis.paramonov.oriscontrolworkjavafx {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.json;

    requires org.controlsfx.controls;

    opens ru.kpfu.itis.paramonov.oriscontrolworkjavafx to javafx.fxml;
    opens ru.kpfu.itis.paramonov.oriscontrolworkjavafx.controller to javafx.fxml;

    exports ru.kpfu.itis.paramonov.oriscontrolworkjavafx.controller to javafx.fxml;
    exports ru.kpfu.itis.paramonov.oriscontrolworkjavafx to javafx.graphics;
}