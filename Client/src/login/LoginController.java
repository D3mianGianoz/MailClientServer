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
import javafx.scene.control.Button;
import javax.swing.JOptionPane;

/**
 * FXML Controller class
 *
 * @author Damiano
 */
public class LoginController implements Initializable {

    private ClientSocket clsocket;

    @FXML
    private TextField txtMail;
    @FXML
    private Button btnLogin;

    @FXML
    private void handleLogin(ActionEvent event) {
        clsocket = new ClientSocket();
        
        // Cominico al server la richiesta login
        clsocket.sendObject("login");
        
        //Aspetto l'ack da parte del server
        String ret = clsocket.readObjectString();
        
        if (ret.equals("ACK login"))
        {
            System.out.println(ret);
            // Mando la mail al server
            clsocket.sendObject(txtMail.getText());
            
            String ack = clsocket.readObjectString();
            if (ack.equals("ACK email login"))
                alert("Login effettuato", "login");
            else
                alert("errore login","errore");
        }
        else
            System.out.println("Errore nella rispota del login al server");
        
        
        /*
        String readString = clsocket.readString();
        
        if (readString.equals("OK LOGIN")) {
            
            clsocket.sendString(txtMail.getText());
            String connessione = clsocket.readString();
            if (connessione.equals("connesso")) {
                alert("Login Effettuato", "Success");
                Client.showEmailClient();
            } else {
                alert("Errore nel login..", "Errore, please retry");
            }
        } else {
            alert("Errore nella connessione","problema con il server");
        }
        */
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

    // Metodo per il popup di un alert box
    public void alert(String messaggio, String titolo) {
        JOptionPane.showMessageDialog(null, messaggio, titolo, JOptionPane.INFORMATION_MESSAGE);
    }

}
