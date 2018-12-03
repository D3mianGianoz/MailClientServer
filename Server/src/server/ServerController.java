package server;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
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
        try {
            server = new ServerThread(this);                           //TODO Passi sempre lo stesso servercontroller
            this.printLog("Inizializzazione del server...");
            server.start();
            lblStatus.setText("Server is running");
        } catch (Exception e) {
            Logger.getLogger(ServerController.class.getName()).log(Level.SEVERE, "Non puoi ristartare un server", e);
        }
    }
    
    public void stopServer()
    {
        this.printLog("Stopping server...");
        server.stopServer();
        lblStatus.setText("Server is not running");      
    }
    
    public void printLog(String log)
    {
        txtLog.appendText(log+"\n");
    }
    
}
