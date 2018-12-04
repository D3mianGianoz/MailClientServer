/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compose;

import client.Client;
import connection.ClientSocket;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import model.Email;
import model.SimpleEmail;

/**
 * FXML Controller class
 *
 * @author Damiano
 */
public class GUIController implements Initializable {
   
    @FXML
    private TextArea txtTestoNw;

    @FXML
    private TextArea txtOggettoNw;

    @FXML
    private TextArea txtDestinatario;

    @FXML
    void c64040(ActionEvent event) {

    }

    @FXML
    void sendEmail(ActionEvent event) {
        ClientSocket socket = Client.getClsocket();
        socket.sendObject("invioEmail");
        String ack = (String) socket.readObject();
        if(ack.equals("manda email"))
        {
            ArrayList<String> dest = new ArrayList();
            dest.add("costi");
            dest.add("destinatario2");
            Email email = new Email(new SimpleEmail(1, "prova",dest, "prova invio", "email inviata dal client prova per costi", LocalDate.now()));
            socket.sendObject(email.getSimpleEmail());
            ack = (String) socket.readObject();
            if (ack.equals("ack scrittura email"))
                System.out.println("mail inviata correttamente");
        }
        else
            System.out.println("Errore invio email");

    }

    @FXML
    void goBackHome(ActionEvent event) {
        //Client.showEmailClient();
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
}
