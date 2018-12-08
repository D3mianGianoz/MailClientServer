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
import java.util.logging.Level;
import java.util.logging.Logger;
import model.DataModel;

/**
 *
 * @author Damiano
 */
public class ClientThread extends Thread {

    private ServerSocket serverSocket;
    private final int NUM_PORTA;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private final ClientController controller;
    private DataModel thModel;
    private final String emailClient;

    public ClientThread(int numPorta, ClientController controller, DataModel clientModel, String idClient) {
        super("Client T per GestioneEmail con n: " + numPorta);
        this.controller = controller;
        this.thModel = clientModel;
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
            AcceptTask task = new AcceptTask(serverSocket, controller, thModel);
            Thread t = new Thread(task);
            t.start();
        }
    }
}
