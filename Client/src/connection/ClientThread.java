/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package connection;

import client.ClientController;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Damiano Gianotti and Alberto Costamagna
 */
public class ClientThread extends Thread {

    private ServerSocket serverSocket;
    private final int NUM_PORTA;
    private final ClientController controller;
    private final String emailClient;

    public ClientThread(int numPorta, ClientController controller, String idClient) {
        super("Client T per GestioneEmail con n: " + numPorta);
        this.controller = controller;
        NUM_PORTA = numPorta;
        this.emailClient = idClient;
    }

    public int getPortaClient() {
        try {
            serverSocket = new ServerSocket(NUM_PORTA);
        } catch (IOException ex) {
            Logger.getLogger(ClientThread.class.getName()).log(Level.SEVERE, "Errore nell' apertura sulla porta: " + NUM_PORTA, ex);
        }

        return serverSocket.getLocalPort();
    }

    @Override
    public void run() {
        while (true) {
            try {
                Socket accept = serverSocket.accept();
                AcceptTask task = new AcceptTask(accept, controller);
                Thread t = new Thread(task);
                t.start();
            } catch (IOException ex) {
                Logger.getLogger(ClientThread.class.getName()).log(Level.SEVERE, "Errore accettazione sul socket", ex.getMessage());
            }
        }
    }
}
