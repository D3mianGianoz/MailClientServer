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
import static util.Utility.alertConf;

/**
 *
 * @author Alberto Costamagna and Damiano Gianotti
 */
public class ClientController implements Initializable {

    private ClientSocket CCsocket;

    private DataModel clmodel;
    
    private String userEmail;

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
    
    @FXML
    void menuChiSiamo(ActionEvent event) {
        alert("Alberto Costamagna\nDamiano Gianotti", Alert.AlertType.INFORMATION, "Sviluppato da:\n");
    }

    @FXML         
    void onDelete(ActionEvent event) {
        if (alertConf("Sei sicuro di volere cancellare l'email ?", Alert.AlertType.CONFIRMATION)) {
            Email emailToRemove = clmodel.getCurrentEmail();

            CCsocket.sendObject("rimuoviEmail");
            String ack = CCsocket.readString();
            if (ack.equals("rimuovi email")) {
                SimpleEmail toSend = emailToRemove.getSimpleEmail();
                CCsocket.sendObject(toSend);
                ack = CCsocket.readString();
                if (ack.equals("ack rimozione email")) {
                    alert("Email rimossa correttamente", Alert.AlertType.INFORMATION,"Success",true);
                } else {
                    alert("Delete Email fallita", Alert.AlertType.ERROR);
                    return;
                }
            } else {
                System.out.println("Errore di comunicazione");
                alert("Errore di comunicazione.\n Il server potrebbe essere spento", Alert.AlertType.ERROR);
                return;
            }

            clmodel.removeEmail(emailToRemove);
        } else {
            System.out.println("Canc annullata");
        }
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
        this.userEmail = Client.getUserEmail();

        //Binding Lista
        bindingListW();

        //Binding dei campi Email
        bindingFields();

        //creo il Thread per le nuove Email
        listenerNewEmails();
    }

    private void listenerNewEmails() {
        ClientThread cThread = new ClientThread(0, this, this.userEmail);
        int portaVariabile = cThread.getPortaClient();
        if (sendPortaUser(portaVariabile)) {
            cThread.start();
        } else {
            alert("Could not sync with server", Alert.AlertType.ERROR);
        }
    }

    private boolean sendPortaUser(int porta) {
        CCsocket.sendObject("aggiungiPorta");
        String ack = CCsocket.readString();
        if (ack.equals("aggiungi porta")) {
            CCsocket.sendObject(porta);
            ack = CCsocket.readString();
            if (ack.equals("ack aggiunta porta")) {
                CCsocket.sendObject(Client.getUserEmail());
                ack = CCsocket.readString();
                if (ack.equals("ack aggiunto user; END")) {
                    System.out.println("porta e user aggiunti correttamente");
                    return true;
                }
            }
        } else {
            System.out.println("Errore creazione sync email");
            return false;
        }
        return false;
    }
    
    public void addEmail(Email toAdd){
        clmodel.addEmail(toAdd);
        alert("Nuova Email per: " + this.userEmail + "!", Alert.AlertType.INFORMATION, "Nuova Email !" ,true);
        System.out.println("Email correttamente aggiunta");
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
