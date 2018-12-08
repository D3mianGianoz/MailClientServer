/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package connection;

import client.ClientController;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.Alert;
import model.DataModel;
import model.Email;
import model.SimpleEmail;
import static util.Utility.alert;

/**
 *
 * @author Damiano
 */
public class EmailTask implements Runnable {

    private DataModel model;
    private final ClientController tskController;
    private final Email emailT;

    public EmailTask(ClientController controller, DataModel dm,SimpleEmail newEmailS) {
        this.tskController = controller;
        this.model = dm;
        this.emailT = new Email(newEmailS);
    }

    @Override
    public void run() {
        model.addEmail(emailT);
        Logger.getLogger(ClientThread.class.getName()).log(Level.FINE, "Email correttamente aggiunta");
        alert("Nuova Email !", Alert.AlertType.INFORMATION, true);
    }
}
