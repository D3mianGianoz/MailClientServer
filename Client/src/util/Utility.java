/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.util.Optional;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

/**
 *
 * @author Damiano
 */
public class Utility {

    // Metodo per il popup di un alert box con type coerente  
    public static void alert(String messaggio, Alert.AlertType type) {
        Alert alert = new Alert(type, messaggio);
        alert.showAndWait();
    }

    public static void alert(String messaggio, Alert.AlertType type, boolean NoWait) {
        Alert alert = new Alert(type, messaggio);
        alert.show();
    }

    public static boolean alertConf(String messaggio, Alert.AlertType type) {
        Alert alert = new Alert(type, messaggio);
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }
}
