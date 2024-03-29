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

    /**
     * Metodo richiamato in automatico all' inizializzazione dell' interfaccia
     * grafica
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        assert btnStop != null : "fx:id=\"btnStop\" was not injected: check your FXML file 'ServerView.fxml'.";
        assert txtLog != null : "fx:id=\"txtLog\" was not injected: check your FXML file 'ServerView.fxml'.";
        assert btnAvvia != null : "fx:id=\"btnAvvia\" was not injected: check your FXML file 'ServerView.fxml'.";
        assert lblStatus != null : "fx:id=\"lblStatus\" was not injected: check your FXML file 'ServerView.fxml'.";

        lblStatus.setText("Server is not running");
        btnStop.setDisable(true);
    }

    /**
     * Funzione richiamata al click del bottone Avvia Inizializza il serverMail
     * mettendo in ascolto il socket sulla porta indicata dal txtPorta
     */
    public void startServer() {
        try {
            btnAvvia.setDisable(true);
            server = new ServerThread(this);
            server.start();
            lblStatus.setText("Server is running");
            btnStop.setDisable(false);
        } catch (Exception e) {
            Logger.getLogger(ServerController.class.getName()).log(Level.SEVERE, "Non puoi ristartare un server", e);
        }
    }

    /**
     * Funzione richiamata al click del bottone stop. Va a bloccare il serer
     * aspettando che termini il suo thread
     */
    public void stopServer() {
        this.printLog("Stopping server...");
        server.stopServer();
        try {
            server.join();
        } catch (InterruptedException ex) {
            Logger.getLogger(ServerController.class.getName()).log(Level.SEVERE, "Sto morendo", ex);
        }
        lblStatus.setText("Server is not running");
        btnAvvia.setDisable(false);
        btnStop.setDisable(true);
    }

    public synchronized void printLog(String log) {
        txtLog.appendText(log + "\n");
    }

}
