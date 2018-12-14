/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package connection;

import client.ClientController;
import model.Email;
import model.SimpleEmail;

/**
 * Semplice Runnable
 * Salva la email nel data model
 * @author Damiano Gianotti and Alberto Costamagna
 */
public class EmailTask implements Runnable {

    private final ClientController tskController;
    private final Email emailT;

    public EmailTask(ClientController controller,SimpleEmail newEmailS) {
        this.tskController = controller;
        this.emailT = new Email(newEmailS);
    }

    @Override
    public void run() {
        tskController.addEmail(emailT);
    }
}
