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
import model.DataModel;
import model.SimpleEmail;

/**
 *
 * @author Damiano
 */
public class ClientThread extends Thread {

    private ServerSocket serverSocket;
    private Socket clientSocket;
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

    @Override
    public void run() {

        try {
            serverSocket = new ServerSocket(NUM_PORTA);
        } catch (IOException ex) {
            Logger.getLogger(ClientThread.class.getName()).log(Level.SEVERE, "Errore nell' apertura sulla porta: " + NUM_PORTA, ex);
        }

        try {
            Socket accept = serverSocket.accept();
            if (accept != null) {
                this.clientSocket = accept;
            } else {
                throw new NullPointerException();
            }
        } catch (IOException | NullPointerException ex) {
            Logger.getLogger(ClientThread.class.getName()).log(Level.SEVERE, "Errore nell' accettazione del server", ex);
        }

        getStreams();

        listenAndTask();
    }

    private void getStreams() {
        try {
            in = new ObjectInputStream(clientSocket.getInputStream());
            out = new ObjectOutputStream(clientSocket.getOutputStream());
        } catch (IOException ex) {
            Logger.getLogger(ClientThread.class.getName()).log(Level.SEVERE, "Errore nella creazione degli Streams", ex);
        }
    }

    private void listenAndTask() {

        String request = "";
        while (true) {
            request = "";
            try {
                request = in.readUTF();
            } catch (IOException ex) {
                Logger.getLogger(ClientThread.class.getName()).log(Level.SEVERE, "Errore nella ricezione della stringa di richiesta", ex);
            }
            switch (request) {
                case "pushEmail":
                    addNewEmail();
                    break;

                default:
                    break;
            }
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
            ClientTask task = new ClientTask(controller, thModel, email);
            Thread t = new Thread(task);
            t.start();

        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(ClientThread.class.getName()).log(Level.SEVERE, "Errore nella ricezione dell' Email", ex);
        }
    }
}
