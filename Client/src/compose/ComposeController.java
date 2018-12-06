/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compose;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import model.DataModel;
import model.Email;
import connection.ClientSocket;
import client.*;
import model.*;
import java.time.LocalDate;

/**
 * FXML Controller class
 *
 * @author Damiano
 */
public class ComposeController implements Initializable {

    private DataModel cmpModel;

    @FXML
    private TextArea txtTestoNw;

    @FXML
    private TextArea txtOggettoNw;

    @FXML
    private TextArea txtDestinatarioNw;

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
            Email email = new Email(new SimpleEmail(1, "prova",dest, "prova invio LOCK ESCLUSIVO", "email inviata dal client prova per costi", LocalDate.now()));
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

    public void initModel(DataModel model) {
        if (this.cmpModel != null) {
            throw new IllegalStateException("Il Model può essere iniziallato una volta sola");
        }

        this.cmpModel = model;
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    private void newEmail() {
        String dstUniti = txtDestinatarioNw.getText();
        List<String> asList = Arrays.asList((dstUniti.split("\\s*,\\s*")));
        String oggetto = txtOggettoNw.getText();
        String testo = txtTestoNw.getText();
        
    }

}
