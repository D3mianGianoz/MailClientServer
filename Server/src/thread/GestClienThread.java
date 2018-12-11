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
        runningT.set(true);
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

    @Override
    public void run() {

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

    public void serverExit() { // Forse inutile
        runningT.set(false);
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
            controller.printLog("Impossibile leggere email di login del client" + ex.getMessage());
        } catch (ClassNotFoundException ex) {
            controller.printLog(ex.getMessage());
        }

        // Controllo se la mail del client è nuova o il suo file con le email precedenti esiste
        clientFile = new File("EmailFiles/" + emailClient + ".dat");
        if (!clientFile.exists()) {
            try {
                // Creo il file per il client
                clientFile.createNewFile();
                // Scrivo un' arraylist di email vuoto 
                writeFile(clientFile, new ArrayList<>());
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

        ArrayList<SimpleEmail> ret = readEmailFile(this.clientFile);
        try {
            out.writeObject(ret);
        } catch (IOException ex) {
            controller.printLog("Impossibile inviare lista email al client: " + ex.getMessage());
        }
    }

    private void gestisciInvioEmail() {
        // Comunico al client di mandarmi la mail
        try {
            out.writeObject("manda email");
        } catch (IOException ex) {
            controller.printLog("Errore risposta client");
        }

        SimpleEmail email = null;
        try {
            // Aspetto che il client mi invio la sua simple email
            email = (SimpleEmail) in.readObject();
        } catch (IOException ex) {
            controller.printLog("Errore impossibile ricevere email");
        } catch (ClassNotFoundException ex) {
            controller.printLog(ex.getMessage());
        }

        // Recupero i destinatri della email
        ArrayList<String> destinatari = email.getDestinatari();

        // Per ogni destinatario vado a scrivere la mail nel rispettivo file
        for (String dest : destinatari) {
            // Recupero la lista delle email del file
            File fileDest = new File("EmailFiles/" + dest + ".dat");
            if (!fileDest.exists()) {
                try {
                    fileDest.createNewFile();
                    writeFile(fileDest, new ArrayList<>());
                } catch (IOException ex) {
                    controller.printLog("Impossibile creare nuovo file per i destinantari: " + ex.getMessage());
                }
            }
            ArrayList<SimpleEmail> prevEmail = readEmailFile(fileDest);
            prevEmail.add(email);
            writeFile(fileDest, prevEmail);
            if (clientList.containsKey(dest)) {
                mandoEmailClient(dest, email);
            }
        }

        try {
            out.writeObject("ack scrittura email");
            controller.printLog("Email ricevuta correttamente dal client: " + this.emailClient);
        } catch (IOException ex) {
            controller.printLog("Errore risposta client");
        }
    }

    private void gestisciDeleteEmail() {
        try {
            // Dico al client di inviarmi la email da eliminare
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

        // Recupero la lista delle email del client stesso
        ArrayList<SimpleEmail> el = readEmailFile(this.clientFile);
        el.remove(deleteEmail);

        // Dopo aver tolto la email riscrivo il file con la nuova lista
        writeFile(this.clientFile, el);

        try {
            out.writeObject("ack rimozione email");
            controller.printLog("Email del client: " + this.emailClient + " cancellata correttamente");
        } catch (IOException ex) {
            controller.printLog("Errore impossibile inviare messaggio di risposta al client: " + ex.getMessage());
        }
    }

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

    private void gestisciLogout() {
        socketList.remove(this);
        if (clientList.containsKey(this.emailClient)) {
            clientList.remove(this.emailClient);
        }
        controller.printLog("Client: " + this.emailClient + " gestito correttamente, disconesso");
    }

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

            // Acquisisco il lock esclusivo per il file
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
        } finally { //Ho un dubbio sull' ordine 
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
