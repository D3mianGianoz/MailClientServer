/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import javafx.scene.control.Alert;

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
    
}
