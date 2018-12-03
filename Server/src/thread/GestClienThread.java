package thread;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import model.Email;
import server.ServerController;
import static thread.ServerThread.socketList;

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
    private File clientFile;
    // Lista dei client connessi in quel momento
    // Invio la mail tramite il socket solo ai client connessi, altrimento scrivo solo sul loro file
    //private ArrayList<GestClienThread> clientList;

    public GestClienThread(Socket r, ServerController c) {
        super("ThreadGestioneClient");
        this.clientFile = null;
        this.socket = r;
        this.controller = c;
        //this.clientList = clients;
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

    // VERSIONE DI PROVA
    @Override
    public void run() {

        while (true) {
            String richiesta = "";
            try {
                richiesta = (String) in.readObject();
            } catch (IOException ex) {
                controller.printLog("Errore nella ricezione della stringa di richiesta dal client");
            } catch (ClassNotFoundException ex) {
                controller.printLog("Class not found " + ex.getMessage());
            }

            //controller.printLog("Lettura stringa del client: "+ richiesta);
            // Controllo che tipo di richiesta ha fatto il client
            switch (richiesta) {
                case "login":
                    gestsciLogin();
                    break;

                case "getMyEmails":
                    getMyEmails();
                    break;

                // Esco e blocco il ciclo infinito e mi rimuvo dalla lista
                case "exit":
                    gestisciLogout();
                    return;
            }
        }

    }

    private void gestsciLogin() {
        // Scrivo al client che accetto la sua richiesta di login
        controller.printLog("Sto gestendo la richiesta di login da parte del client");

        try {
            // Comunico al client di mandarmi la sua email
            out.writeObject("ACK login");
            out.flush();
        } catch (IOException ex) {
            controller.printLog("Impossibile mandare ACK al client");
        }

        try {
            // Leggo la email del client di login
            this.emailClient = (String) in.readObject();
        } catch (IOException ex) {
            controller.printLog("Impossibile leggere email di login del client");
        } catch (ClassNotFoundException ex) {
            controller.printLog(ex.getMessage());
        }

        // Controllo se la mail del client è nuova o il suo file con le email precedenti esiste
        clientFile = new File("EmailFiles/" + emailClient + ".txt");
        if (!clientFile.exists()) {
            try {
                // Creo il file per il client
                clientFile.createNewFile();
            } catch (IOException ex) {
                controller.printLog("Errore nella creazione del file per le email dell' utente");
            }
            controller.printLog("File per l'utente: " + this.emailClient + " creato correttamente");
        }

        try {
            out.writeObject("ACK email login");
            out.flush();
        } catch (IOException ex) {
            controller.printLog("Errore ack email login");
        }
        controller.printLog("Client " + this.emailClient + " connesso");

    }

    private void getMyEmails() {
        BufferedReader br = null;
        ArrayList<Email> emaiList = new ArrayList<>();
        try {
            // Creo bufferedReader per leggere dal file
            br = new BufferedReader(new FileReader(this.clientFile));
        } catch (FileNotFoundException ex) {
            controller.printLog("File del client: " + this.emailClient + " non trovato");
        }
        try {
            // Controllo che il file non sia vuoto
            String st;
            if (this.clientFile.length() == 0) {
                out.writeObject(emaiList); // Se il file è vuoto ritorno al client un arraylist di email vuoto
            } else {
                while ((st = br.readLine()) != null) {
                    // Splitto in parametri
                    String[] params = st.split(",");
                    String[] valori = new String[params.length];
                    
                    // Mi recupero i valori dei parametri
                    for (int i = 0; i < params.length; i++) {
                        String[] temp = params[i].split(":");
                        valori[i] = temp[1];
                    }

                    // Creo l' arraylist dei destinatari
                    ArrayList<String> dest = new ArrayList<>();
                    String[] destString = valori[2].split(";");
                    dest.addAll(Arrays.asList(destString));

                    emaiList.add(new Email(Integer.parseInt(valori[0]), valori[1], dest, valori[3], valori[4], LocalDate.parse(valori[5], DateTimeFormatter.ISO_DATE)));
                }
                controller.printLog(emaiList.toString());
                
                //QUI è il problema
                out.writeObject(emaiList.get(0));
                out.flush();
            }
        } catch (IOException ex) {
            controller.printLog("Errore: " + ex.getMessage());
        }

    }

    private void gestisciLogout() {
        socketList.remove(this);
        controller.printLog("Client: " + this.emailClient + " gestito correttamente, disconesso");
    }

    public String getEmailFromSocket() {
        return this.emailClient;
    }

}
