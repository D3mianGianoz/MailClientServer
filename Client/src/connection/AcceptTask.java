package connection;

import client.ClientController;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import model.SimpleEmail;

/**
 * Semplice Task per pushare le Email in realtime
 * 
 * @author Damiano Gianotti and Alberto Costamagna
 */
public class AcceptTask implements Runnable {

    private Socket dataS;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private ClientController cont;

    AcceptTask(Socket sS, ClientController contoller) {
        this.dataS = sS;
        this.cont = contoller;
    }

    @Override
    public void run() {
        getStreams();
        listenAndTask();
    }

    private void getStreams() {
        try {
            in = new ObjectInputStream(dataS.getInputStream());
            out = new ObjectOutputStream(dataS.getOutputStream());
        } catch (IOException ex) {
            Logger.getLogger(ClientThread.class.getName()).log(Level.SEVERE, "Errore nella creazione degli Streams", ex.getMessage());
        }
    }

    /**
     * Metodo che ascolta sul socket per far sì che il server possa inviare in tempo reale le email ai destinatari connessi
     */
    private void listenAndTask() {

        String request = "";
        try {
            request = (String) in.readObject();
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(ClientThread.class.getName()).log(Level.SEVERE, "Errore nella ricezione della stringa di richiesta", ex);
        }
        
        switch (request) {
            case "pushEmail":
                addNewEmail();
                break;
                        
            default:
                Logger.getLogger(ClientThread.class.getName()).log(Level.INFO, "Stringa di richiesta non gestita");
                break;
        }
    }

    /**
     * Ricevuta la nuova email la salva nella rispettivo data model
     * Uso il metodo runLater per fare partire lo specifico Runnable sul il Thread JavaFX Application 
     */
    private void addNewEmail() {
        try {
            out.writeObject("ACK push");
        } catch (IOException ex) {
            Logger.getLogger(ClientThread.class.getName()).log(Level.SEVERE, "Errore nell' invio del ACK", ex);
        }

        try {
            SimpleEmail email = (SimpleEmail) in.readObject();
            out.writeObject("ACK ricezione mail; END");

            EmailTask task = new EmailTask(this.cont, email);
            Platform.runLater(task);
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(ClientThread.class.getName()).log(Level.SEVERE, "Errore nella ricezione dell' Email", ex);
        }
    }
}
