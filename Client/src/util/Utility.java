package util;

import java.util.Optional;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

/**
 * Classe per la gestione di vari tipi di messaggi per la comincazione con l' utenete
 * @author Damiano Gianotti and Alberto Costamagna
 */
public class Utility {

    // Metodo per il popup di un alert box con type coerente  
    public static void alert(String messaggio, Alert.AlertType type) {
        Alert alert = new Alert(type, messaggio);
        alert.showAndWait();
    }

    public static void alert(String messaggio, Alert.AlertType type, String header) {
        Alert alert = new Alert(type, messaggio);
        alert.setHeaderText(header);
        alert.showAndWait();
    }

    public static void alert(String messaggio, Alert.AlertType type, boolean NoWait) {
        Alert alert = new Alert(type, messaggio);
        alert.setTitle("Notifica");
        alert.show();
    }

    public static void alert(String messaggio, Alert.AlertType type, String header, boolean NoWait) {
        Alert alert = new Alert(type, messaggio);
        alert.setTitle("Notifica");
        alert.setHeaderText(header);
        alert.show();
    }

    public static boolean alertConf(String messaggio, Alert.AlertType type) {
        Alert alert = new Alert(type, messaggio);
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }
}
