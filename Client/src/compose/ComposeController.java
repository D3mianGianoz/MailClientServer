package compose;

import client.Client;
import connection.ClientSocket;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
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
 * @author Damiano Gianotti and Alberto Costamagna
 */
public class ComposeController implements Initializable {

    private DataModel cmpModel;
    private Email selectedEmail;
    private String accountName;
    private int IdnewEmail;

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

    /**
     * Metodo richiamato al click del bottone send
     * Cominica al server una SimplEmail con i valori indicati nei campi di input
     */
    @FXML
    void sendEmail(ActionEvent event) {
        ClientSocket socket = Client.getClsocket();

        if (!socket.isClosed()) {
            socket.sendObject("invioEmail");
            String ack = socket.readString();
            if (ack.equals("manda email")) {
                SimpleEmail toSend = newEmail();
                socket.sendObject(toSend);
                ack = socket.readString();
                if (ack.equals("ack scrittura email")) {
                    System.out.println("mail inviata correttamente");
                    alert("Email inviata correttamente", Alert.AlertType.INFORMATION, "Success", true);
                    
                    Node source = (Node) event.getSource();
                    Stage stage = (Stage) source.getScene().getWindow();
                    stage.close();
                } else if (ack.equals("destinatario/i non validi")) {
                    ArrayList<String> notFound = (ArrayList<String>) socket.readObject();
                    alert("Mancanti" + notFound.toString(), Alert.AlertType.ERROR, "Destinatario/i incorretti");
                }
            } else {
                alert("Errore di comunicazione.\n Il server potrebbe essere spento", Alert.AlertType.ERROR, "Fatal Error");
            }
        } else {
            alert("Fallito invio email, Server Offline ", Alert.AlertType.ERROR, "Fatal Error");
        }
    }

    /**
     * Metodo per tornare alla schermata precedente
     */
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

    /**
     * Essendo questa schermata richiamata da piu pulsanti con diverse funzionalità
     * questo metodo va a controllare quale bottone l' ha richiamata e si adatta di conseguenza
     */
    public void initModel(DataModel model, String action) {
        if (this.cmpModel != null) {
            throw new IllegalStateException("Il Model può essere iniziallato una volta sola");
        }

        this.cmpModel = model;
        selectedEmail = cmpModel.getCurrentEmail();

        accountName = Client.getUserEmail();
        IdnewEmail = 0xa;

        switch (action) {

            case "New Email":
                break;

            case "Reply":
                txtDestinatarioNw.setText(selectedEmail.getMittente() + ";");
                txtOggettoNw.setText("Re: " + selectedEmail.getOggetto());
                txtTestoNw.setText("\n\n\n --------- Messaggio originale --------- \n" + selectedEmail.getTesto());
                break;

            case "ReplyAll":
                txtDestinatarioNw.setText(selectedEmail.getDestinatari());
                txtOggettoNw.setText("Re: " + selectedEmail.getOggetto());
                txtTestoNw.setText("\n\n\n --------- Messaggio originale --------- \n" + selectedEmail.getTesto());
                break;

            case "Foward":
                txtOggettoNw.setText("Fwd: " + selectedEmail.getOggetto());
                txtTestoNw.setText("\n\n\n --------- Messaggio inoltrato --------- \n" + selectedEmail.getTesto());
                break;
        }
    }

    /**
     * Metodo che va a recuperare i valori dei campi di input
     */
    private SimpleEmail newEmail() {
        String mittente = accountName;
        String dstUniti = txtDestinatarioNw.getText();
        List<String> asList = Arrays.asList((dstUniti.split("\\s*;\\s*")));
        ArrayList<String> destinatari = new ArrayList<>(asList);
        String oggetto = txtOggettoNw.getText();
        String testo = txtTestoNw.getText();

        return new SimpleEmail(IdnewEmail++, mittente, destinatari, oggetto, testo, LocalDate.now());
    }
}
