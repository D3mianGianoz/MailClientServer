package connection;

import client.ClientController;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.DataModel;
import model.SimpleEmail;

/**
 *
 * @author Damiano
 */
public class AcceptTask implements Runnable {

    private Socket dataS;
    private ServerSocket temp;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private ClientController cont;
    private DataModel tskModel1;

    AcceptTask(ServerSocket sS, ClientController contoller, DataModel thModel) {
        this.temp = sS;
        this.cont = contoller;
        this.tskModel1 = thModel;
    }

    @Override
    public void run() {
        try {
            this.dataS = temp.accept();
        } catch (IOException ex) {
            Logger.getLogger(AcceptTask.class.getName()).log(Level.SEVERE, "Fallita accettazione Client ", ex.getMessage());
        }
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

    private void listenAndTask() {

        String request = "";
        try {
            request = in.readUTF();
        } catch (IOException ex) {
            Logger.getLogger(ClientThread.class.getName()).log(Level.SEVERE, "Errore nella ricezione della stringa di richiesta", ex);
        }
        switch (request) {
            case "pushEmail":
                addNewEmail();
                break;
            
            case "exit":
                break;
            
            default:
                break;
        }
    }

    private void addNewEmail() {
        try {
            out.writeUTF("ACK push");
        } catch (IOException ex) {
            Logger.getLogger(ClientThread.class.getName()).log(Level.SEVERE, "Errore nell' invio del ACK", ex);
        }

        try {
            SimpleEmail email = (SimpleEmail) in.readObject();
            out.writeUTF("ACK ricezione mail; END");

            EmailTask task = new EmailTask(this.cont, this.tskModel1, email);
            Thread t = new Thread(task);
            t.start();

        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(ClientThread.class.getName()).log(Level.SEVERE, "Errore nella ricezione dell' Email", ex);
        }
    }
}
