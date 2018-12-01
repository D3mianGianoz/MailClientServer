/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import connection.ClientSocket;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;

/**
 *
 * @author Damiano
 */
public class ClientController implements Initializable {
    
    private ClientSocket CCsocket;
    
//Binding tra Controller e View
//<editor-fold defaultstate="collapsed" desc="FXML declaration">
    @FXML
    private ListView<?> lwEmail;
    
    @FXML
    private MenuItem EXIT;
    
    @FXML
    private TextArea txtData;
    
    @FXML
    private TextArea txtOggetto;
    
    @FXML
    private TextArea txtTesto;
    
    @FXML
    private TextArea txtMittente;
    
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

}
