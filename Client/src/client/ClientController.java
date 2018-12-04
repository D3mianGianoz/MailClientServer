package client;

import connection.ClientSocket;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import model.DataModel;
import model.Email;

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
        CCsocket = Client.getClsocket();
        CCsocket.cls();
        Client.showLoginView();
    }

    @FXML
    void menuNewEmail(ActionEvent event) {
        Client.showComposeEmail();
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

        //Binding Lista
        bindingListW();

        //Binding dei campi Email
        bindingFields();
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
