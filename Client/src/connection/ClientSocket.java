/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package connection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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

    Socket socket;

    public ClientSocket() {
        try {
            this.socket = new Socket("localhost", 8070);
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
            writer.write(toSend);
        } catch (IOException ex) {
            Logger.getLogger(ClientSocket.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            this.cls();
        }
    }

    //TODO da finire
    public void sendObject() {
        try {
            OutputStream outputStream = this.socket.getOutputStream();
            PrintWriter writer = new PrintWriter(outputStream, true);
        } catch (IOException ex) {
            Logger.getLogger(ClientSocket.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            this.cls();
        }
    }
    
    public String readString(){
        try {
            InputStreamReader inputStreamR;
            inputStreamR = new InputStreamReader(this.socket.getInputStream());
            BufferedReader reader = new BufferedReader(inputStreamR);
            String readLine = reader.readLine();
            return readLine;
        } catch (IOException ex) {
            Logger.getLogger(ClientSocket.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "Error in readString";
    }
}
