package thread;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import server.ServerController;

/**
 * Thread per inizializzare il server e aspettare le chiamate
 *
 * @author Alberto Costamagna and Damiano Gianotti
 */
public class ServerThread extends Thread {

    private final int NUM_PORTA = 8070;
    private final int MAX_NUM_THREAD = 100;
    private ServerSocket server;
    private Socket accepted;
    private final ServerController controller;
    public static ArrayList<GestClienThread> socketList;
    public static HashMap<String, Integer> clientList;
    private final AtomicBoolean running = new AtomicBoolean(false);

    public ServerThread(ServerController c) {
        super();
        this.controller = c;
        socketList = new ArrayList<>(MAX_NUM_THREAD);
        clientList = new HashMap<>(MAX_NUM_THREAD);
    }

    @Override
    public void run() {
        startServer();
    }

    /**
     * Metodo che inzializza il socket del server aspettando le connessione dei
     * vari client. Una volta che un client si è connesso il server fa partire
     * un thread che va a gestire quella specifica connessione il thread viene
     * aggiunto alla lista
     */
    private void startServer() {
        try {
            controller.printLog("Inizializzazione del server...");
            // Avvio il socket di ascolto sulla porta 8070
            server = new ServerSocket(NUM_PORTA);
            running.set(true);
        } catch (IOException ex) {
            controller.printLog("Errore nell' apertura del server sulla porta: " + NUM_PORTA + ". " + ex.getMessage());
        }
        controller.printLog("Server inizializzato correttamente sulla porta: " + NUM_PORTA);

        while (running.get()) {

            try {
                if (socketList.size() < MAX_NUM_THREAD) {
                    Thread thread = new Thread(new AcceptionTask());
                    thread.start();
                    thread.join();
                    if (accepted != null && !server.isClosed()) {
                        GestClienThread client = new GestClienThread(accepted, controller);
                        socketList.add(client);
                        client.start();
                    }
                } else {
                    controller.printLog("Raggiunto numero massimo di conessioni disponibili");
                }
            } catch (NullPointerException ex) {
                controller.printLog("Errore nella ricezione della richiesta di un client: " + ex.getMessage());
            } catch (InterruptedException exN) {
                controller.printLog("Errore puntatore a null: " + exN.getMessage());
            }
        }

    }

    /**
     * Metodo per fermare il server
     */
    public void stopServer() {
        try {
            running.set(false);
            server.close();
            controller.printLog("Server chiusto correttamente");
        } catch (IOException | NullPointerException ex) {
            controller.printLog("Errore nella chiusura del server sulla porta: " + NUM_PORTA + ". " + ex.getMessage());
        }
    }

    /**
     * Thread per l' accettazione di nuove connessioni da parte dei client
     */
    private class AcceptionTask implements Runnable {

        @Override
        public void run() {
            try {
                controller.printLog("Aspetto una connessione ...");
                accepted = server.accept();
            } catch (SocketException e) {
                if (server.isClosed()) {
                    controller.printLog("Connesione chiusa");
                }
            } catch (IOException ex) {
                controller.printLog("Accept fallita" + ex.getMessage());
            }
        }

    }

}
