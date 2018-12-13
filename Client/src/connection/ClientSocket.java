package connection;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Classe utilizzata per la comincazione tra client e server
 * @author Alberto Costamagna and Damiano Gianotti
 */
public class ClientSocket {

    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    public ClientSocket() {
        try {
            this.socket = new Socket("127.0.0.1", 8070);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            System.out.println("Ho aperto il socket verso il server");
        } catch (IOException ex) {
            Logger.getLogger(ClientSocket.class.getName()).log(Level.SEVERE, "Connesione al socket fallita", ex.getMessage());
        }
    }

    public void cls() {
        try {
            this.sendObject("exit");
            this.socket.close();
        } catch (IOException ex) {
            Logger.getLogger(ClientSocket.class.getName()).log(Level.SEVERE, "Chiusura socket fallita", ex.getMessage());
        }
    }

    public void sendObject(Object obj) {
        try {
            out.writeObject(obj);
            out.flush();
        } catch (IOException ex) {
            Logger.getLogger(ClientSocket.class.getName()).log(Level.SEVERE, "Client: fallito invio di: " + obj.getClass(), ex.getMessage());
        }
    }

    public Object readObject() {
        try {
            return in.readObject();
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(ClientSocket.class.getName()).log(Level.SEVERE, "Client : fallito ricezione Obj", ex.getMessage());
        }
        return null;
    }

    public void sendString(String str) {
        try {
            out.writeObject(str);
            out.flush();
        } catch (IOException ex) {
            Logger.getLogger(ClientSocket.class.getName()).log(Level.SEVERE, "Client : fallito invio della stringa: " + str, ex.getMessage());
        }
    }

    public String readString() {
        try {
            return (String) in.readObject();
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(ClientSocket.class.getName()).log(Level.SEVERE, "Client : fallito ricezione String", ex.getMessage());
        }
        return "Error Error Error";
    }
    
    public boolean isClosed(){
        return socket.isClosed();
    }
}
