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
import javafx.scene.control.Button;
import javax.swing.JOptionPane;

/**
 * FXML Controller class
 *
 * @author Damiano
 */
public class LoginController implements Initializable {

    @FXML
    private TextField txtMail;
    @FXML
    private Button btnLogin;

    @FXML
    private void handleLogin(ActionEvent event) {
        if (txtMail.getText().equals("#")) {
            alert("Login Effettuato", "Success");
            Client.showEmailClient();
        } else {
            alert("Errore nel login..", "Errore, please retry");
        }
    }

    /**
     * Initializes the controller class.
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