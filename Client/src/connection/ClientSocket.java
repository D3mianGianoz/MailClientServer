/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package connection;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Alberto Costamagna , Damiano Gianotti
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
            Logger.getLogger(ClientSocket.class.getName()).log(Level.SEVERE, "Connesione al socket fallita", ex.toString());
        }
    }

    public void cls() {
        try {
            this.sendObject("exit");
            this.socket.close();
        } catch (IOException ex) {
            Logger.getLogger(ClientSocket.class.getName()).log(Level.SEVERE, "Chiusura socket fallita", ex);
        }
    }

    public void sendObject(Object obj) {
        try {
            out.writeObject(obj);
            out.flush();
        } catch (IOException ex) {
            Logger.getLogger(ClientSocket.class.getName()).log(Level.SEVERE, "Client: fallito invio di: " + obj.getClass(), ex);
        }
    }

    public Object readObject() {
        try {
            return in.readObject();
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(ClientSocket.class.getName()).log(Level.SEVERE, "Client : fallito ricezione Obj", ex);
        }
        return null;
    }
    
    public void sendString(String str){
        try {
            out.writeUTF(str);
            out.flush();
        } catch (IOException ex) {
            Logger.getLogger(ClientSocket.class.getName()).log(Level.SEVERE, "Client : fallito invio della stringa: " + str, ex);
        }
    }
    
    public String readString(){
        try {
            return in.readUTF();
        } catch (IOException ex) {
            Logger.getLogger(ClientSocket.class.getName()).log(Level.SEVERE, "Client : fallito ricezione String", ex);
        }
        return "Error Error Error";
    }
}
