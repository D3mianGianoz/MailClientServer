/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package connection;

import client.ClientController;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Thread per l' inizializzazione del socket per la ricezione delle email da
 * parte del server
 *
 * @author Damiano Gianotti and Alberto Costamagna
 */
public class ClientThread extends Thread {

    private ServerSocket serverSocket;
    private int NUM_PORTA;
    private final ClientController controller;
    private final String emailClient;
    private final AtomicBoolean running = new AtomicBoolean(false);

    public ClientThread(int numPorta, ClientController controller, String idClient) {
        super("Client T per GestioneEmail con n: " + numPorta + "e idClient: " + idClient);
        this.controller = controller;
        this.NUM_PORTA = numPorta;
        this.emailClient = idClient;
    }

    /**
     * Genera il server socket con numero di porta variabile (la prima
     * disponibile)
     *
     * @return Porta di ascolto del client
     */
    public int getNewPortaClient() {
        try {
            serverSocket = new ServerSocket(NUM_PORTA);
        } catch (IOException ex) {
            Logger.getLogger(ClientThread.class.getName()).log(Level.SEVERE, "Errore nell' apertura sulla porta: " + NUM_PORTA, ex);
        }
        NUM_PORTA = serverSocket.getLocalPort();

        return NUM_PORTA;
    }

    /**
     * Aspetta connessioni dal Server e le gestisce con task appropriate
     */
    @Override
    public void run() {
        running.set(true);

        while (running.get()) {
            try {
                Socket accept = serverSocket.accept();
                if (!serverSocket.isClosed()) {
                    AcceptTask task = new AcceptTask(accept, controller);
                    Thread t = new Thread(task);
                    t.start();
                }
            } catch (SocketException e) {
                if (serverSocket.isClosed()) {
                    Logger.getLogger(ClientThread.class.getName()).log(Level.INFO, "Socket chiuso correttamente: ", e.getMessage());
                }
            } catch (IOException ex) {
                Logger.getLogger(ClientThread.class.getName()).log(Level.SEVERE, "Errore accettazione sul socket", ex.getMessage());
            }
        }
    }

    public void exit() {
        try {
            running.set(false);
            serverSocket.close();
        } catch (IOException ex) {
            Logger.getLogger(ClientThread.class.getName()).log(Level.SEVERE, "Fallita chiusura Socket di ascolto del client", ex.getMessage());
        }
    }
}
