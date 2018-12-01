package server;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javax.swing.JOptionPane;
import thread.ServerThread;

/**
 *
 * @author Alberto Costamagna and Damiano Gianotti
 */
public class ServerController implements Initializable {
    
    private ServerThread server;
    
    @FXML
    private Label lblStatus;
    
    @FXML
    private Button btnAvvia;
    
    @FXML
    private Button btnStop;
    
    @FXML
    private TextArea txtLog;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        lblStatus.setText("Server is not running");        
    }
    
    // Funzione richiamata al click del bottone Avvia
    // Inizializza il serverMail mettendo in ascolto il socket sulla porta indicata dal txtPorta
    public void startServer()
    {
        server = new ServerThread(this);                           //TODO Passi sempre lo stesso servercontroller
        this.printLog("Inizializzazione del server...");
        server.start();
        lblStatus.setText("Server is running");
    }
    
    public void stopServer()
    {
        this.printLog("Stopping server...");
        server.stopServer();
        lblStatus.setText("Server is not running");      
    }
    
    
    // Metodo per il popup di un alert box
    public void alert(String messaggio,String titolo)
    {
        JOptionPane.showMessageDialog(null, messaggio , titolo, JOptionPane.INFORMATION_MESSAGE);    
    }
    
    public void printLog(String log)
    {
        txtLog.appendText(log+"\n");
    }
    
}
