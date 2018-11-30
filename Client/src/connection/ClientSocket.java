/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package connection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Damiano
 */
public class ClientSocket {

    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    public ClientSocket() {
        try {
            this.socket = new Socket("127.0.0.1", 8070);
            out = new ObjectOutputStream(socket.getOutputStream());
            in =  new ObjectInputStream(socket.getInputStream());
            System.out.println("Ho aperto il socket verso il server");
            
        } catch (IOException ex) {
            Logger.getLogger(ClientSocket.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void cls() {
        try {
            this.socket.close();
        } catch (IOException ex) {
            Logger.getLogger(ClientSocket.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void sendString(String toSend) { //in teoria flush automatico
        try {
            OutputStream outputStream = this.socket.getOutputStream();
            PrintWriter writer = new PrintWriter(outputStream, true);
            writer.println(toSend);
        } catch (IOException ex) {
            Logger.getLogger(ClientSocket.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            this.cls();
        }
    }

    //TODO da finire
    public void sendObject(Object obj) {
        try {
          out.writeObject(obj);
             
        } catch (IOException ex) {
            Logger.getLogger(ClientSocket.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }
    
    public String readObjectString(){
        try {
            String ret = (String)in.readObject();
            return ret;
        } catch (IOException ex) {
            Logger.getLogger(ClientSocket.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ClientSocket.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "Error in readString";
    }
}
