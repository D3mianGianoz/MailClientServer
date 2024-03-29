package util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import server.ServerController;

/**
 * Classe per la gestione e l' invio  in tempo reale delle email tra client connessi contemporaneamente al server
 * @author Alberto Costamagna and Damiano Gianotti
 */
public class MailSocket {

    private Socket socket;
    private ServerController serController;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    public MailSocket(ServerController controller, int nPorta, String dest) {
        try {
            this.socket = new Socket("127.0.0.1", nPorta);
            this.out = new ObjectOutputStream(socket.getOutputStream());
            this.in = new ObjectInputStream(socket.getInputStream());
            this.serController = controller;
            serController.printLog("Ho aperto il MailSocket verso il client: " +dest+ " con la porta: " + nPorta);
        } catch (IOException ex) {
            serController.printLog("Connesione al MailSocket del client fallita: " + ex.getMessage());
        }
    }

    public void cls() {
        try {
            this.sendString("exit");
            this.socket.close();
        } catch (IOException ex) {
            serController.printLog("Chiusura MailSocket fallita: "+ ex.getMessage());
        }
    }

    public void sendObject(Object obj) {
        try {
            out.writeObject(obj);
            out.flush();
        } catch (IOException ex) {
            serController.printLog("Client: fallito invio di: " + obj.getClass()+ " causa: " + ex.getMessage());
        }
    }

    public Object readObject() {
        try {
            return in.readObject();
        } catch (IOException | ClassNotFoundException ex) {
            serController.printLog("Client : fallito ricezione Obj" + ex.getMessage());
        }
        return null;
    }
    
    public void sendString(String str){
        try {
            out.writeObject(str);
            out.flush();
        } catch (IOException ex) {
            serController.printLog("Client : fallito invio della stringa: " + str + " causa: " + ex.getMessage());
        }
    }
    
    public String readString(){
        try {
            return (String) in.readObject();
        } catch (IOException | ClassNotFoundException ex) {
            serController.printLog("Client : fallito ricezione String " + ex.getMessage());
            return "Excpetion error";
        }
    }
    
}
