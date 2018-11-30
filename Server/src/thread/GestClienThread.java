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
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private String emailClient;
    // Lista dei client connessi in quel momento
    // Invio la mail tramite il socket solo ai client connessi, altrimento scrivo solo sul loro file
    private ArrayList<GestClienThread> clientList;
    
    public GestClienThread(Socket r,ServerController c,ArrayList clients)
    {
        super("ThreadGestioneClient");
        this.socket = r;
        this.controller = c;
        this.clientList = clients;
         try {
            in = new ObjectInputStream(socket.getInputStream());
        } catch (IOException ex) {
             controller.printLog("Errore nella creazione dell' input stream " + ex.getMessage());
        }
        
        try {
            out = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException ex) {
            controller.printLog("Errore nella creazione dell' output stream " + ex.getMessage());
        }
    }
    
    /*
        Versione FORSE SBAGLIATA
    @Override
    public void run()
    {
        // Creo l' oggetto per leggere la richiesta del client
        try {
            in = new ObjectInputStream(socket.getInputStream());
        } catch (IOException ex) {
            controller.printLog("Errore nella creazione dell' input stream " + ex.getMessage());
        }
        // Creo l' oggetto per rispondere al client
        try {
            outStream = socket.getOutputStream();
        } catch (IOException ex) {
            controller.printLog("Errore nella creazione dell' output stream");
        }
        
        out = new PrintWriter(outStream,true);
        
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
    */
    
    // VERSIONE DI PROVA
    @Override
    public void run()
    {
       
        String richiesta = "";
        try {
            richiesta = (String)in.readObject();
        } catch (IOException ex) {
            controller.printLog("Errore nella ricezione della stringa di richiesta dal client");
        } catch (ClassNotFoundException ex) {
            controller.printLog("Class not found "+ex.getMessage());
        }
        
        controller.printLog("Lettura stringa del client: "+ richiesta);
        
        
    }
    
    
    private void gestsciLogin()
    {
        // Scrivo al client che accetto la sua richiesta di login
        
        
       
       
        
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
        
        
        
        
        controller.printLog("Client "+this.emailClient+" connesso");
        
        
        
        
    }
    
    public String getEmailFromSocket()
    {
        return this.emailClient;
    }
    
}
