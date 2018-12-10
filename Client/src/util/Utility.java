package util;

import java.util.Optional;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

/**
 *
 * @author Damiano Gianotti and Alberto Costamagna
 */
public class Utility {

    // Metodo per il popup di un alert box con type coerente  
    public static void alert(String messaggio, Alert.AlertType type) {
        Alert alert = new Alert(type, messaggio);
        alert.showAndWait();
    }

    public static void alert(String messaggio, Alert.AlertType type, boolean NoWait) {
        Alert alert = new Alert(type, messaggio);
        alert.setTitle("Notifica");
        alert.show();
    }

    public static boolean alertConf(String messaggio, Alert.AlertType type) {
        Alert alert = new Alert(type, messaggio);
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }
}
