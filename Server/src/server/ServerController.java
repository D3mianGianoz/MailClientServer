package server;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javax.swing.JOptionPane;

/**
 *
 * @author Alberto Costamagna and Damiano Gianotti
 */
public class ServerController implements Initializable {
    
    private int numPorta;
    
    
    @FXML
    private Label lblStatus;
    
    @FXML
    private Button btnAvvia;
    
    @FXML
    private Button btnStop;
    
    @FXML
    private TextArea txtLog;
    
    @FXML
    private TextField txtPorta;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        lblStatus.setText("Server is not running");
        // TODO
    }
    
    // Funzione richiamata al click del bottone Avvia
    // Inizializza il serverMail mettendo in ascolto il socket sulla porta indicata dal txtPorta
    public void startServer()
    {
        //Check del valore della porta (NON PUO ESSERE == NULL)
        if(txtPorta.getText() == null)            
            alert("Inserire il valore della porta","Errore numero porta");
        else
        {
            numPorta = Integer.parseInt(txtPorta.getText());
            
            //Check valore porta > 0
            if (numPorta <= 0)
                alert("Numero porta non valido","Errore numero porta");
                
            lblStatus.setText("Server is running");
            txtLog.appendText("Starting server on door n: "+numPorta+"\n");
            txtLog.appendText("Server started\n");
        }
        
        
        
    }
    
    public void stopServer()
    {
        lblStatus.setText("Server is not running");
        txtLog.appendText("Stopping server...\n");
        txtLog.appendText("Server stoped\n");
    }
    
    
    // Metodo per il popup di un alert box
    public void alert(String messaggio,String titolo)
    {
        JOptionPane.showMessageDialog(null, messaggio , titolo, JOptionPane.INFORMATION_MESSAGE);    
    }
    
}
