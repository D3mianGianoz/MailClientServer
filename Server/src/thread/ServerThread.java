package thread;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import server.ServerController;

/**
 *
 * @author Alberto Costamanga, Damiano Gianotti
 */
// Thread per inizializzare il server e aspettare le chiamate
public class ServerThread extends Thread {

    private final int NUM_PORTA = 8070;
    private ServerSocket server;
    private ServerController controller;
    public static ArrayList<GestClienThread> socketList;
    private boolean exit = false;

    public ServerThread(ServerController c) {
        super();
        this.controller = c;
        socketList = new ArrayList<>();
    }

    // Metodo richiamato una volta startato il thread
    @Override
    public void run() {
        startServer();
    }

    private void startServer() {
        try {
            // Avvio il socket di ascolto sulla porta 8070
            server = new ServerSocket(NUM_PORTA);
            // Se voglio far ripartire il server questa deve essere false !!
            exit = false;
        } catch (IOException ex) {
            controller.printLog("Errore nell' apertura del server sulla porta: " + NUM_PORTA + ". " + ex.getMessage());
        }
        controller.printLog("Server inizializzato correttamente sulla porta: " + NUM_PORTA);

        // Inizio ad ascoltare per le richieste del client
        while (!exit) {
            // Avvio un nuovo thread per gestire la richiesta dell' utente
            // e lo aggiungo alla lista di client
            try {
                GestClienThread client = new GestClienThread(server.accept(), controller);
                socketList.add(client);
                client.start();
            } catch (IOException ex) {  //Test
                controller.printLog("Errore nella ricezione della richiesta di un client: " + ex.getMessage());
                Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, "Errore nella start del Server", ex);
            } catch (NullPointerException exN) {
                Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, "Errore Puntatore a null", exN);
            }
        }

    }

    // TODO si blocca quando clicco sul bottone di stop, da un errore di array out of bound e altri mille errori
    public void stopServer() {
        try {
            exit = true;
            server.close();
            controller.printLog("Server chiusto correttamente");
        } catch (IOException | NullPointerException ex ) {
            controller.printLog("Errore nella chiusura del server sulla porta: " + NUM_PORTA + ". " + ex.getMessage());
        }
    }

}
