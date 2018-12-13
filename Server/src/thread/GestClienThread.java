package thread;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import model.SimpleEmail;
import server.ServerController;
import static thread.ServerThread.clientList;
import static thread.ServerThread.socketList;
import util.MailSocket;

/**
 *
 * @author Alberto Costamagna and Damiano Gianotti
 */
public class GestClienThread extends Thread {

    private Socket socket;
    private ServerController controller;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private String emailClient;
    private File clientFile;
    private final AtomicBoolean runningT = new AtomicBoolean(false);

    //Lock per i file, shared in lettura, exclusive in scrittura
    private final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private final Lock write = readWriteLock.writeLock();
    private final Lock read = readWriteLock.readLock();

    public GestClienThread(Socket r, ServerController c) {
        super("ThreadGestioneClient");
        this.clientFile = null;
        this.socket = r;
        this.controller = c;
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

    /**
     * Metodo per la gestione delle richieste da parte del client
     */
    @Override
    public void run() {

        runningT.set(true);

        while (runningT.get()) {

            String richiesta = "";
            try {
                richiesta = (String) in.readObject();  //Operazione bloccante
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

                case "invioEmail":
                    gestisciInvioEmail();
                    break;

                case "rimuoviEmail":
                    gestisciDeleteEmail();
                    break;

                case "aggiungiPorta":
                    gestisciAggiuntaPorta();
                    break;

                // Esco e blocco il ciclo infinito e mi rimuvo dalla lista
                // test con atomicBoolean, forse meglio del return
                case "exit":
                    gestisciLogout();
                    runningT.set(false);
                    break;
            }
        }

    }

    public String getEmailFromSocket() {
        return this.emailClient;
    }


    /**
     * Metodo per la gestione della richiesta di login da parte del client
     * Per prima cosa mi comunica la sua email, se l' utente non esiste creo un nuovo file in cui salvare le sue email
     * Dopo di che comico l' avvenuto login al client
     */
    private void gestsciLogin() {
        
        controller.printLog("Sto gestendo la richiesta di login da parte del client");

        try {            
            out.writeObject("ACK login");
            out.flush();
        } catch (IOException ex) {
            controller.printLog("Impossibile mandare ACK al client");
        }

        try {            
            this.emailClient = (String) in.readObject();
        } catch (IOException ex) {
            controller.printLog("Impossibile leggere email di login del client" + ex.getMessage());
        } catch (ClassNotFoundException ex) {
            controller.printLog(ex.getMessage());
        }
        
        clientFile = new File("EmailFiles/" + emailClient + ".dat");
        if (!clientFile.exists()) {
            try {        
                clientFile.createNewFile();
                writeFile(clientFile, new ArrayList<>());
            } catch (IOException ex) {
                controller.printLog("Errore nella creazione del file per le email dell' utente "+ex.getMessage());
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

    /**
     * Metodo che ritorna al client cha ha fatto richiesta la sua lista delle email ricevute
     */
    private void getMyEmails() {

        ArrayList<SimpleEmail> ret = readEmailFile(this.clientFile);
        try {
            out.writeObject(ret);
        } catch (IOException ex) {
            controller.printLog("Impossibile inviare lista email al client: " + ex.getMessage());
        }
    }

    /**
     * Metodo per la gestione dell' invio di una email da parte di un client ad un/altri client
     * Vado a scrivere la email sul file corrispondente del/dei destinartio/i e se sono connessi in quel momento gli mando in tempo reale la email
     * Se inoltre la email che voglio inviare contiene un destinatario che non esiste, lo cominco con un errore
     */
    private void gestisciInvioEmail() {
        
        try {
            out.writeObject("manda email");
        } catch (IOException ex) {
            controller.printLog("Errore risposta client");
        }

        SimpleEmail email = null;
        try {            
            email = (SimpleEmail) in.readObject();
        } catch (IOException ex) {
            controller.printLog("Errore impossibile ricevere email "+ex.getMessage());
        } catch (ClassNotFoundException ex) {
            controller.printLog(ex.getMessage());
        }

        
        ArrayList<String> destinatari = email.getDestinatari();
        ArrayList<String> dstNotFound = new ArrayList<>();

        
        for (String dest : destinatari) {            
            File fileDest = new File("EmailFiles/" + dest + ".dat");
            if (fileDest.exists()) {
                ArrayList<SimpleEmail> prevEmail = readEmailFile(fileDest);
                prevEmail.add(email);
                writeFile(fileDest, prevEmail);
                if (clientList.containsKey(dest)) {
                    mandoEmailClient(dest, email);
                }
            } else {
                dstNotFound.add(dest);
            }
        }

        try {
            if (dstNotFound.isEmpty()) {
                out.writeObject("ack scrittura email");
                controller.printLog("Email ricevuta correttamente dal client: " + this.emailClient);
            } else {
                out.writeObject("destinatario/i non validi");
                out.writeObject(dstNotFound);
                controller.printLog("Email non inviata dal client: " + this.emailClient + "  utenti: " + dstNotFound.toString() + " non trovati");
            }
        } catch (IOException ex) {
            controller.printLog("Errore risposta client" + ex.getMessage());
        }
    }
  
  
    /**
     * Metodo per la cancellazione delle email 
     * Il client cominica al server quale email vuole eliminare
     * Il server recupera l' arraylist dal file dell' utente, elimina l' email desiderata e poi riscrive la lista sul file corrispondente
     */

    private void gestisciDeleteEmail() {
        try {         
            out.writeObject("rimuovi email");
        } catch (IOException ex) {
            controller.printLog("Errore impossibile inviare messaggio di risposta al client: " + ex.getMessage());
        }
        SimpleEmail deleteEmail = null;
        try {
            deleteEmail = (SimpleEmail) in.readObject();
        } catch (IOException ex) {
            controller.printLog("Impossibile ricevere la email da cancellare: " + ex.getMessage());
        } catch (ClassNotFoundException ex) {
            controller.printLog(ex.getMessage());
        }
        
        ArrayList<SimpleEmail> el = readEmailFile(this.clientFile);
        el.remove(deleteEmail);
        
        writeFile(this.clientFile, el);

        try {
            out.writeObject("ack rimozione email");
            controller.printLog("Email del client: " + this.emailClient + " cancellata correttamente");
        } catch (IOException ex) {
            controller.printLog("Errore impossibile inviare messaggio di risposta al client: " + ex.getMessage());
        }
    }

    /**
     * Metodo per la mappatura delle porte dei client connessi in tempo reale al server per l'invio istantaneo delle email ricevute
     */
    private void gestisciAggiuntaPorta() {
        try {
            out.writeObject("aggiungi porta");
            int NumeroPorta = (int) in.readObject();
            out.writeObject("ack aggiunta porta");
            String destinatario = (String) in.readObject();
            out.writeObject("ack aggiunto user; END");
            clientList.put(destinatario, NumeroPorta);
            controller.printLog("Porta di sync creata correttamente per: " + this.emailClient);
        } catch (IOException | ClassNotFoundException | NullPointerException ex) {
            controller.printLog("Errore nell' aggiunta della porta " + ex.getMessage());
        }
    }

    /**
     * Metodo per gestire la richiesta di logout da parte di un client.
     * Va rimuovere ogni suo riferimento dalla lista dei client connessi e dalla hashmap 
     */
    private void gestisciLogout() {
        socketList.remove(this);
        if (clientList.containsKey(this.emailClient)) {
            clientList.remove(this.emailClient);
        }
        controller.printLog("Client: " + this.emailClient + " gestito correttamente, disconesso");
    }

    /**
     * Metodo per la scrittura dei file contenenti le email dei client
     */
    private void writeFile(File f, ArrayList<SimpleEmail> el) {
        ObjectOutputStream o = null;
        FileOutputStream fo = null;

        try {
            try {
                fo = new FileOutputStream(f);
            } catch (FileNotFoundException ex) {
                controller.printLog("File per la scrittura non trovato: " + ex.getMessage());
            }

            try {
                o = new ObjectOutputStream(fo);
            } catch (IOException ex) {
                controller.printLog("Impossibile creare ObjectOutputStream: " + ex.getMessage());
            }

            
            try {
                write.lock();
            } catch (Exception ex) {
                controller.printLog("Impossibile acquisire il lock del file per la scrittura");
            }

            try {
                o.writeObject(el);
            } catch (IOException ex) {
                controller.printLog("Impossibile scrivere sul file: " + ex.getMessage());
            }
        } finally {
            try {
                o.close();
                fo.close();
                write.unlock();
            } catch (IOException | NullPointerException ex) {
                controller.printLog("Impossibile chiudere output stream: " + ex.getMessage());
            }
        }
    }

    /**
     * Metodo per la lettura dei file delle email dei client
     */
    private ArrayList<SimpleEmail> readEmailFile(File f) {
        FileInputStream fi = null;
        ObjectInputStream oi = null;
        ArrayList<SimpleEmail> ret = null;

        try {
            try {
                fi = new FileInputStream(f);
            } catch (FileNotFoundException ex) {
                controller.printLog("Errore nell' apertura del file da leggere: " + ex.getMessage());
            }

            try {
                oi = new ObjectInputStream(fi);
            } catch (IOException ex) {
                controller.printLog("Errore nell' apertura dell' object input stream: " + ex.getMessage());
            }

            try {
                read.lock();
            } catch (Exception ex) {
                controller.printLog("Errore nell' acquisizione del shared lock per la lettura del file: " + ex.getMessage());
            }

            try {
                ret = (ArrayList<SimpleEmail>) oi.readObject();
            } catch (IOException ex) {
                controller.printLog("Errore nella lettura del file: " + ex.getMessage());
            } catch (ClassNotFoundException ex) {
                controller.printLog(ex.getMessage());
            }
        } finally { 
            try {
                oi.close();
                fi.close();
                read.unlock();
            } catch (IOException | NullPointerException ex) {
                controller.printLog("Errore chiusura ObjectInputStream: " + ex.getMessage());
            }
        }

        return ret;
    }

    /**
     * Metodo per l' invio in tempo reale delle email ai client corrispondenti
     * Dopo che il server si recuera la porta su cui il client destinatario ascolta,
     * invia la email e aspetta il risponso del client
     */
    private void mandoEmailClient(String dest, SimpleEmail email) {

        int nPorta = clientList.get(dest);
        MailSocket mailsocket = new MailSocket(controller, nPorta, dest);

        mailsocket.sendString("pushEmail");
        String ack = mailsocket.readString();
        if (ack.equals("ACK push")) {
            mailsocket.sendObject(email);
            ack = mailsocket.readString();
            if (ack.equals("ACK ricezione mail; END")) {
                controller.printLog("Mail correttamente inviata a: " + dest);
            } else {
                controller.printLog("Non sono riuscito ad inviare a: " + dest);
            }
        } else {
            controller.printLog("Fallita tot la procedura di invio " + dest);
        }
        mailsocket.cls();
    }

}
