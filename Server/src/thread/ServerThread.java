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
 *
 * @author Alberto Costamagna and Damiano Gianotti
 */

// Thread per inizializzare il server e aspettare le chiamate
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

    // Metodo richiamato una volta startato il thread
    @Override
    public void run() {
        startServer();
    }

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

        // Inizio ad ascoltare per le richieste del client
        while (running.get()) {
            // Avvio un nuovo thread per gestire la richiesta dell' utente
            // e lo aggiungo alla lista di client
            try {
                if (socketList.size() < MAX_NUM_THREAD) {                    
                    Thread thread = new Thread(new AcceptionTask());
                    thread.start();
                    thread.join();
                    if (accepted != null) {
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

    // TODO si blocca quando clicco sul bottone di stop, da un errore di array out of bound e altri mille errori
    public void stopServer() {
        try {
            running.set(false);
            server.close();
            stopThreads();
            controller.printLog("Server chiusto correttamente");
        } catch (IOException | NullPointerException | InterruptedException ex) {
            controller.printLog("Errore nella chiusura del server sulla porta: " + NUM_PORTA + ". " + ex.getMessage());
        }
    }
    
    
    //Non credo basti, gestire il blocco sulla richiesta
    public void stopThreads() throws InterruptedException {
        for (GestClienThread th : socketList) {
            System.out.println(th.getName() +" prima è: "+ th.isAlive());
            th.serverExit();
            th.join(5);
            System.out.println(th.getName() +" è al momento: "+ th.isAlive());
        }
    }

    private class AcceptionTask implements Runnable {

        @Override
        public void run() {
            try {
                controller.printLog("Aspetto una connessione ...");
                accepted = server.accept();
            } catch (SocketException e) {
                controller.printLog("Connesione chiusa");
            } catch (IOException ex) {
                controller.printLog("Accept fallita" + ex.getMessage());
            }
        }

    }

}
