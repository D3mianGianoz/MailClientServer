package thread;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
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
    private ArrayList<GestClienThread> clientList;
    
    public ServerThread(ServerController c)
    {
        super();
        this.controller = c;
        clientList = new ArrayList<>();
    }
    
    
    // Metodo richiamato una volta startato il thread
    @Override
    public void run()
    {
        startServer();
    }
    
    
    private void startServer()
    {
        try {
            // Avvio il socket di ascolto sulla porta 8070
            server = new ServerSocket(NUM_PORTA);
        }
        catch (IOException ex) {
            controller.printLog("Errore nell' apertura del server sulla porta: "+NUM_PORTA+". "+ex.getMessage());
        }
        controller.printLog("Server inizializzato correttamente sulla porta: "+NUM_PORTA);
        
        // Inizio ad ascoltare per le richieste del client
        while(true)
        {
            // Avvio un nuovo thread per gestire la richiesta dell' utente
            // e lo aggiungo alla lista di client
            Socket incoming = null;
            try {
                incoming = server.accept();
            } catch (IOException ex) {
                controller.printLog("Errore nella ricezione della richiesta di un client: "+ex.getMessage());
            }
            GestClienThread client = new GestClienThread(incoming,controller,clientList);
            clientList.add(client);
            client.start();
        }
        
    }
    
    public void stopServer()
    {
        try {
            server.close();
            controller.printLog("Server chiusto correttamente");
        } catch (IOException ex) {
            controller.printLog("Errore nella chiusura del server sulla porta: "+NUM_PORTA+". "+ex.getMessage());
        }
    }
    
}
