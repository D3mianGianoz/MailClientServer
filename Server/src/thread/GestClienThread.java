package thread;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import server.ServerController;

/**
 *
 * @author alberto
 */
public class GestClienThread extends Thread {
    
    private Socket socket;
    private ServerController controller;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private String emailClient;
    // Lista dei client connessi in quel momento
    // Invio la mail tramite il socket solo ai client connessi, altrimento scrivo solo sul loro file
    private ArrayList<GestClienThread> clientList;
    
    public GestClienThread(Socket r,ServerController c,ArrayList clients)
    {
        super();
        this.socket = r;
        this.controller = c;
        this.clientList = clients;
        // Creo l' oggetto per leggere la richiesta del client
        try {
            in = new ObjectInputStream(socket.getInputStream());
        } catch (IOException ex) {
            controller.printLog("Errore nella creazione dell' input stream");
        }
        // Creo l' oggetto per rispondere al client
        try {
            out = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException ex) {
            controller.printLog("Errore nella creazione dell' output stream");
        }
       
        
    }
    
    @Override
    public void run()
    {
        while(true)
        {
            String richiesta = "";
            
            // Leggo la richiesta del client
            try {
                richiesta = in.readUTF();
            } catch (IOException ex) {
                controller.printLog(ex.getMessage());
            }
            
            // Controllo che tipo di richiesta ha fatto il client
            switch(richiesta)
            {
                case "login":
                    gestsciLogin();
                    break;
                    
                // Esco e blocco il ciclo infinito
                case "exit":
                    return;
            }
        }
    }
    
    
    
    private void gestsciLogin()
    {
        // Scrivo al client che accetto la sua richiesta di login
        try {
            out.writeUTF("OK login");
            out.flush();
        } catch (IOException ex) {
            controller.printLog(ex.getMessage());
        }
        
        // Aspetto che il client mi comunichi la sua email
        // Leggo la richiesta del client e la salvo per il thread corrente
        try {
            emailClient = in.readUTF();
        } catch (IOException ex) {
            controller.printLog(ex.getMessage());
        }
        
        // Controllo se la mail del client è nuova o il suo file con le email precedenti esiste
        File f = new File("./EmailFiles/"+emailClient+".txt");
        if (!f.exists())
        {
            try {
                // Creo il file per il client
                f.createNewFile();
            } catch (IOException ex) {
                controller.printLog("Errore nella creazione del file per le email dell' utente");
            }
            controller.printLog("File per l'utente: "+this.emailClient+" crato correttamente");
        }
        
        try {
            out.writeUTF("connesso");
            out.flush();
            controller.printLog("Client "+this.emailClient+" connesso");
        } catch (IOException ex) {
            controller.printLog(ex.getMessage());
        }
        
        
        
    }
    
    public String getSocketClientEmail()
    {
        return this.emailClient;
    }
    
}
