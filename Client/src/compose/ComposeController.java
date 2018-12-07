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
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import model.DataModel;
import model.Email;
import model.SimpleEmail;
import static util.Utility.alert;

/**
 * FXML Controller class
 *
 * @author Damiano Gianotti Alberto Costamagna
 */
public class ComposeController implements Initializable {

    private DataModel cmpModel;
    private Email selectedEmail;
    private String accountName;

//<editor-fold defaultstate="collapsed" desc="FXML declaration">
    @FXML
    private TextArea txtTestoNw;

    @FXML
    private TextArea txtOggettoNw;

    @FXML
    private TextArea txtDestinatarioNw;

    @FXML
    void c64040(ActionEvent event) {
    }
//</editor-fold>

    @FXML     
    void sendEmail(ActionEvent event) {
        ClientSocket socket = Client.getClsocket();
        
        socket.sendObject("invioEmail");
        String ack = (String) socket.readObject();
        if (ack.equals("manda email")) {
            SimpleEmail toSend = newEmail();
            socket.sendObject(toSend);
            ack = (String) socket.readObject();
            if (ack.equals("ack scrittura email")) {
                System.out.println("mail inviata correttamente");
                alert("Email inviata correttamente", Alert.AlertType.INFORMATION, true);
            }
        } else {
            System.out.println("Errore invio email");
        }

        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }

    @FXML
    void goBackHome(ActionEvent event) {
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        assert txtTestoNw != null : "fx:id=\"txtTestoNw\" was not injected: check your FXML file 'newEmail.fxml'.";
        assert txtOggettoNw != null : "fx:id=\"txtOggettoNw\" was not injected: check your FXML file 'newEmail.fxml'.";
        assert txtDestinatarioNw != null : "fx:id=\"txtDestinatario\" was not injected: check your FXML file 'newEmail.fxml'.";
    }

    public void initModel(DataModel model, String action) {
        if (this.cmpModel != null) {
            throw new IllegalStateException("Il Model può essere iniziallato una volta sola");
        }

        this.cmpModel = model;
        selectedEmail = cmpModel.getCurrentEmail();
        if (selectedEmail != null || !action.equals("New Email"))
            Logger.getLogger(Client.class.getName()).log(Level.FINE, "Email selezionata \n{0}\n{1}", new Object[]{selectedEmail.getMittente(), action});
        accountName = Client.getUserEmail();

        switch (action) {
            case "Reply":
                txtDestinatarioNw.setText(selectedEmail.getMittente() + ";");
                txtOggettoNw.setText("Re: " + selectedEmail.getOggetto());
                break;

            case "ReplyAll":
                txtDestinatarioNw.setText(selectedEmail.getDestinatari());
                txtOggettoNw.setText("Re: " + selectedEmail.getOggetto());
                break;

            case "Foward":
                txtOggettoNw.setText("Fwd: " + selectedEmail.getOggetto());
                txtTestoNw.setText("\n\n\n --------- Messaggio inoltrato --------- \n" + selectedEmail.getTesto());
                break;
        }
    }

    private SimpleEmail newEmail() {
        String mittente = accountName;
        String dstUniti = txtDestinatarioNw.getText();
        List<String> asList = Arrays.asList((dstUniti.split("\\s*;\\s*")));
        ArrayList<String> destinatari = new ArrayList<>(asList);
        String oggetto = txtOggettoNw.getText();
        String testo = txtTestoNw.getText();

        return new SimpleEmail(1, mittente, destinatari, oggetto, testo, LocalDate.now());
    }
}
