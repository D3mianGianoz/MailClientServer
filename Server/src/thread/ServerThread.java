package thread;

import java.io.IOException;
import java.net.ServerSocket;
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
    
    public ServerThread(ServerController c)
    {
        super();
        this.controller = c;
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
            controller.printLog("Server inizializzato correttamente sulla porta: "+NUM_PORTA);
        } catch (IOException ex) {
            controller.printLog("Errore nell' apertura del server sulla porta: "+NUM_PORTA+".\n "+ex.getMessage());
        } 
    }
    
    public void stopServer()
    {
        try {
            server.close();
            controller.printLog("Server chiusto correttamente");
        } catch (IOException ex) {
            controller.printLog("Errore nella chiusura del server sulla porta: "+NUM_PORTA+".\n "+ex.getMessage());
        }
    }
    
}
