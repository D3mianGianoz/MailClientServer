package login;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import client.Client;
import connection.ClientSocket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;

/**
 * FXML Controller class
 *
 * @author Damiano Gianotti e Alberto Costamagna
 */
public class LoginController implements Initializable {

    private ClientSocket clsocketLogin;

    @FXML
    private TextField txtMail;
    @FXML
    private Button btnLogin;

    @FXML
    private void handleLogin(ActionEvent event) {
        clsocketLogin = new ClientSocket();
        try {
            // Cominico al server la richiesta login
            clsocketLogin.sendObject("login");

            //Aspetto l'ack da parte del server
            String ret = clsocketLogin.readObjectString();

            if (ret.equals("ACK login")) {
                System.out.println(ret);
                // Mando la mail al server
                String loginMail = txtMail.getText();
                clsocketLogin.sendObject(loginMail);

                String ack = clsocketLogin.readObjectString();
                if (ack.equals("ACK email login")) {
                    alert("Login effettuato", Alert.AlertType.INFORMATION);
                    
                    //setto il socket
                    Client.setClsocket(clsocketLogin); 
                    Client.showEmailClient(loginMail);
                } else {
                    alert("Errore login", Alert.AlertType.ERROR);
                }
            } else {
                System.out.println("Errore nella rispota del login al server");
            }
        } catch (NullPointerException ex) {
            //Gestione Login Fallito
            Logger.getLogger(ClientSocket.class.getName()).log(Level.SEVERE, "Gestione Login non riuscita", ex);
            alert("Login Fallito, Riprovare", Alert.AlertType.ERROR);
        }
    }

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        assert btnLogin != null : "fx:id=\"btnLogin\" was not injected: check your FXML file 'Login.fxml'.";
        assert txtMail != null : "fx:id=\"txtMail\" was not injected: check your FXML file 'Login.fxml'.";
    }

    // Metodo per il popup di un alert box con type coerente  
    public void alert(String messaggio, Alert.AlertType type) {
        Alert alert = new Alert(type, messaggio);
        alert.showAndWait();
    }
}
