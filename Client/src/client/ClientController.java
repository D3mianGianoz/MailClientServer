package client;

import connection.ClientSocket;
import connection.ClientThread;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import model.DataModel;
import model.Email;
import model.SimpleEmail;
import static util.Utility.alert;

/**
 *
 * @author Alberto Costamagna , Damiano Gianotti
 */
public class ClientController implements Initializable {

    private ClientSocket CCsocket;

    private DataModel clmodel;

//Binding tra Controller e View
//<editor-fold defaultstate="collapsed" desc="FXML declaration">
    @FXML
    private ListView<Email> lwEmail;

    @FXML
    private TextArea txtData;

    @FXML
    private TextArea txtDestinatario;

    @FXML
    private TextArea txtMittente;

    @FXML
    private TextArea txtOggetto;

    @FXML
    private TextArea txtTesto;

    @FXML
    private Button btReplyAll;

    @FXML
    private Button btDelete;

    @FXML
    private Button btReply;
//</editor-fold>

    @FXML
    void menuLogout(ActionEvent event) {
        Client.getClsocket().cls();

        Client.showLoginView();
    }

    @FXML
    void menuNewEmail(ActionEvent event) {
        Client.showComposeEmail("New Email");
    }

    @FXML
    void onReply(ActionEvent event) {
        Client.showComposeEmail("Reply");
    }

    @FXML
    void onFoward(ActionEvent event) {
        Client.showComposeEmail("Foward");
    }

    @FXML
    void onReplyAll(ActionEvent event) {
        Client.showComposeEmail("ReplyAll");
    }

    @FXML         //TODO comunicazione al server della cancellazione dell ' email
    void onDelete(ActionEvent event) {
        Email emailToRemove = clmodel.getCurrentEmail();

        CCsocket.sendObject("rimuoviEmail");
        String ack = (String) CCsocket.readObject();
        if (ack.equals("rimuovi email")) {
            SimpleEmail toSend = emailToRemove.getSimpleEmail();
            CCsocket.sendObject(toSend);
            ack = (String) CCsocket.readObject();
            if (ack.equals("ack rimozione email")) {
                System.out.println("mail rimossa correttamente");
                alert("Email rimossa correttamente", Alert.AlertType.INFORMATION, true);
            } else {
                alert("Delete Email fallita", Alert.AlertType.ERROR);
                return;
            }
        } else {
            System.out.println("Errore di comunicazione");
            return;
        }

        clmodel.removeEmail(emailToRemove);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //
    }

    public void initModel(DataModel model) {
        //mi assicuro che il modello venga impostato una volta soltanto
        if (this.clmodel != null) {
            throw new IllegalStateException("Il Model può essere iniziallato una volta sola");
        }

        if (model == null) {
            throw new IllegalStateException("Model passato nullo! Errore");
        }

        this.clmodel = model;
        CCsocket = Client.getClsocket();

        //Binding Lista
        bindingListW();

        //Binding dei campi Email
        bindingFields();

        //creo il Thread per le nuove Email
        listenerNewEmails();
    }

    private void listenerNewEmails() {
        ClientThread cThread = new ClientThread(0, this, clmodel, Client.getUserEmail());
        int portaVariabile = cThread.getPortaClient();
        if (sendPorta(portaVariabile))
            cThread.start();
        else
            alert("Could not sync with server", Alert.AlertType.ERROR);
    }

    private boolean sendPorta(int porta) {
        CCsocket.sendObject("aggiungiPorta");
        String ack = (String) CCsocket.readObject();
        if (ack.equals("aggiungi porta")) {
            CCsocket.sendObject(porta);
            ack = (String) CCsocket.readObject();
            if (ack.equals("ack aggiunta porta")) {
                System.out.println("porta aggiunta correttamente");
                alert("Email rimossa correttamente", Alert.AlertType.INFORMATION, true);
                return true;
            }
        } else {
            System.out.println("Errore rimozione email");
            return false;
        }        
        return false;
    }

    private void bindingListW() {

        lwEmail.setItems(clmodel.getEmailList());

        lwEmail.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Email>() {
            @Override
            public void changed(ObservableValue<? extends Email> observable, Email oldSelection, Email newSelection) {
                clmodel.setCurrentEmail(newSelection);
            }
        });

        clmodel.getCurrentEmailProperty().addListener(new ChangeListener<Email>() {
            @Override
            public void changed(ObservableValue<? extends Email> observable, Email oldEmail, Email newEmail) {
                if (newEmail == null) {
                    lwEmail.getSelectionModel().clearSelection();
                } else {
                    lwEmail.getSelectionModel().select(newEmail);
                }
            }
        });

    }

    private void bindingFields() {

        clmodel.getCurrentEmailProperty().addListener(new ChangeListener<Email>() {

            @Override
            public void changed(ObservableValue<? extends Email> observable, Email oldEmail, Email newEmail) {
                if (oldEmail != null) {
                    txtData.textProperty().unbindBidirectional(oldEmail.dataProperty());
                    txtDestinatario.textProperty().unbindBidirectional(oldEmail.destinatariStrProperty());
                    txtMittente.textProperty().unbindBidirectional(oldEmail.mittenteProperty());
                    txtOggetto.textProperty().unbindBidirectional(oldEmail.oggettoProperty());
                    txtTesto.textProperty().unbindBidirectional(oldEmail.testoProperty());
                }
                if (newEmail == null) {
                    txtData.setText("");
                    txtDestinatario.setText("");
                    txtMittente.setText("");
                    txtOggetto.setText("");
                    txtTesto.setText("");
                } else {
                    txtData.textProperty().bindBidirectional(newEmail.dataProperty());
                    txtDestinatario.textProperty().bindBidirectional(newEmail.destinatariStrProperty());
                    txtMittente.textProperty().bindBidirectional(newEmail.mittenteProperty());
                    txtOggetto.textProperty().bindBidirectional(newEmail.oggettoProperty());
                    txtTesto.textProperty().bindBidirectional(newEmail.testoProperty());
                }

            }
        });
    }

}
