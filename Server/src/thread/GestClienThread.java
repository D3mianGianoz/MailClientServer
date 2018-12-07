package thread;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.SimpleEmail;
import server.ServerController;
import static thread.ServerThread.clientAttivi;
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

                case "invioEmail":
                    gestisciInvioEmail();
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
        clientFile = new File("EmailFiles/" + emailClient + ".dat");
        if (!clientFile.exists()) {
            try {
                // Creo il file per il client
                clientFile.createNewFile();
                // Scrivo un' arraylist di email vuoto 
                writeFile(clientFile,new ArrayList<SimpleEmail>());
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

        //Login compiuto con successo, aggiungo alla lista di client attivi
        ServerThread.clientAttivi.add(this.emailClient);
        //Creo un nuovo socket TODO porta variabile
        Socket mailsocket; //-----------------------------

        controller.printLog("Client " + this.emailClient + " connesso");

    }
    
    private void writeFile(File f,ArrayList<SimpleEmail> el)
    {
        FileChannel fch = null;
        ObjectOutputStream o = null;
        FileOutputStream fo = null;
        try {
             fo = new FileOutputStream(f);
        } catch (FileNotFoundException ex) {
            controller.printLog("File per la scrittura non trovato: "+ex.getMessage());
        }
        
        try {
            o = new ObjectOutputStream(fo);
        } catch (IOException ex) {
            controller.printLog("Impossibile creare ObjectOutputStream: "+ex.getMessage());
        }
        
        // Acquisisco il lock esclusivo per il file
        try {
            fch = FileChannel.open(f.toPath(), StandardOpenOption.WRITE, StandardOpenOption.APPEND);
            FileLock lock = fch.lock();
        } catch (IOException ex) {
            controller.printLog("Impossibile acquisire il lock del file per la scrittura");
        }
        
        try {
            o.writeObject(el);
        } catch (IOException ex) {
            controller.printLog("Impossibile scrivere sul file: "+ex.getMessage());
        }
        
        try {
            o.close();
            fo.close();
            fch.close();
        } catch (IOException ex) {
            controller.printLog("Impossibile chiudere output stream: "+ex.getMessage());
        }
    }
    
    private ArrayList<SimpleEmail> readEmailFile(File f)
    {
        FileChannel fch = null;
        FileInputStream fi = null;
        ObjectInputStream oi = null;
        
        try {
            fi = new FileInputStream(f);
        } catch (FileNotFoundException ex) {
            controller.printLog("Errore nell' apertura del file da leggere: "+ex.getMessage());
        }
        
        try {
            oi = new ObjectInputStream(fi);
        } catch (IOException ex) {
            controller.printLog("Errore nell' apertura dell' object input stream");
        }
        
        try {
            fch = FileChannel.open(f.toPath(), StandardOpenOption.READ);
            FileLock lock = fch.lock(0, fch.size(), true);
        } catch (IOException ex) {
            controller.printLog("Errore nell' acquisizione del shared lock per la lettura del file: "+ex.getMessage());
        }
        
        
        ArrayList<SimpleEmail> ret = null;
        try {
            ret = (ArrayList<SimpleEmail>) oi.readObject();
        } catch (IOException ex) {
            controller.printLog("Errore nella lettura del file: "+ex.getMessage());
        } catch (ClassNotFoundException ex) {
            controller.printLog(ex.getMessage());
        }
        
        try {
            oi.close();
            fi.close();
            fch.close();
        } catch (IOException ex) {
            controller.printLog("Errore chiusura ObjectInputStream: "+ex.getMessage());
        }
        
        
        return ret;
    }

    private void getMyEmails() {
        /*
        BufferedReader br = null;
        ArrayList<SimpleEmail> emaiList = new ArrayList<>();
        FileChannel fch = null;
        try {
            // Creo bufferedReader per leggere dal file
            br = new BufferedReader(new FileReader(this.clientFile));
            fch = FileChannel.open(this.clientFile.toPath(), StandardOpenOption.READ);
        } catch (FileNotFoundException ex) {
            controller.printLog("File del client: " + this.emailClient + " non trovato");
        } catch (IOException ex) {
            Logger.getLogger(GestClienThread.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            // Acquisico il lock shadre per la lettura del file
            FileLock lock = fch.lock(0, fch.size(), true);

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

                    emaiList.add(new SimpleEmail(Integer.parseInt(valori[0]), valori[1], dest, valori[3], valori[4], LocalDate.parse(valori[5], DateTimeFormatter.ISO_DATE)));
                }

                out.writeObject(emaiList);
                out.flush();
            }
        } catch (IOException ex) {
            controller.printLog("Errore: " + ex.getMessage());
        } finally {
            try {
                // Chiudo il buffer e rilascio il lock
                br.close();
                fch.close();
            } catch (IOException ex) {
                controller.printLog("Errore nella chiusura del file per la lettura delle email");
            }
        }
        */
        
        ArrayList<SimpleEmail> ret = readEmailFile(this.clientFile);
        try {
            out.writeObject(ret);
        } catch (IOException ex) {
            controller.printLog("Impossibile inviare lista email al client: "+ex.getMessage());
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
        ArrayList<String> destinatari = email.getDestinatri();

        // Per ogni destinatario vado a scrivere la mail nel rispettivo file
        for (String dest : destinatari) {
            // Recupero la lista delle email del file
            File fileDest = new File("EmailFiles/" + dest + ".dat");
            if (!fileDest.exists())
                try {
                    fileDest.createNewFile();
                    writeFile(fileDest,new ArrayList<SimpleEmail>());
            } catch (IOException ex) {
                controller.printLog("Impossibile creare nuovo file per i destinantari: "+ex.getMessage());
            }
            ArrayList<SimpleEmail> prevEmail = readEmailFile(fileDest);
            prevEmail.add(email);
            writeFile(fileDest, prevEmail);
            if (clientAttivi.contains(dest)) {
                //mandoEmailClient(dest, email, destinatari);
            }
        }

        try {
            out.writeObject("ack scrittura email");
        } catch (IOException ex) {
            controller.printLog("Errore risposta client");
        }
    }

    private void gestisciLogout() {
        clientAttivi.remove(this.emailClient);
        socketList.remove(this);
        controller.printLog("Client: " + this.emailClient + " gestito correttamente, disconesso");
    }

    public String getEmailFromSocket() {
        return this.emailClient;
    }

    private void scrivoEmailFile(String dest, SimpleEmail email, ArrayList<String> destinatari) {
        FileChannel fch = null;
        try {
            // Apro il file corrispondente al destinatario
            // Se c'è un nuovo destinatario creo il nuo
            File f = new File("EmailFiles/" + dest + ".txt");
            if (!f.exists()) {
                try {
                    f.createNewFile();
                } catch (IOException ex) {
                    controller.printLog("Errore creazione nuovo file per destinatario");
                }
            }

            // Recupero il file su cui devo scrivere 
            fch = FileChannel.open(f.toPath(), StandardOpenOption.WRITE, StandardOpenOption.APPEND);
            // Mi posiziono in fondo al file
            fch.position(fch.size() - 1);
            // Acquisisco il lock esclusivo per il file
            FileLock lock = fch.lock();

            // Creo la stringa da scrivere e la trasformo in un bytebuffer
            String strToWrite = "Id:" + email.getId() + ",Mittente:" + email.getMittente() + ",Destinatario/i:" + getDestinatari(destinatari) + ",Oggetto:" + email.getOggetto() + ",Testo:" + email.getTesto() + ",Data:" + email.getData().format(DateTimeFormatter.ISO_DATE) + "\n";
            ByteBuffer buffer = ByteBuffer.wrap(strToWrite.getBytes());

            // Scrivo sul file
            fch.write(buffer);

            /*
            BufferedWriter bw = null;
            try {
                bw = new BufferedWriter(new FileWriter(f, true));
            } catch (IOException ex) {
                controller.printLog("Impossibile aprire il bufferedWriter");
            } */
            controller.printLog("EMAIL SCRITTA SU FILE: Id:" + email.getId() + ",Mittente:" + email.getMittente() + ",Destinatario/i:" + getDestinatari(destinatari) + ",Oggetto:" + email.getOggetto() + ",Testo:" + email.getTesto() + ",Data:" + email.getData().format(DateTimeFormatter.ISO_DATE));

            //bw.write("Id:"+email.getId()+",Mittente:"+email.getMittente()+",Destinatario/i:"+getDestinatari(destinatari)+",Oggetto:"+email.getOggetto()+",Testo:"+email.getTesto()+",Data:"+email.getData().format(DateTimeFormatter.ISO_DATE)+"\n");                       
            //bw.close();
            //controller.printLog("Email ricevuto dal client "+this.emailClient);
        } catch (IOException ex) {
            Logger.getLogger(GestClienThread.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                // Rilascio il lock esclusivo del file
                fch.close();
            } catch (IOException ex) {
                controller.printLog(ex.getMessage());
            }
        }
    }

    private void mandoEmailClient(String dest, SimpleEmail email, ArrayList<String> destinatari) {
        try {
            Socket mailsocket = new Socket("127.0.0.1", 8080);
            
            //TODO invio della mail
            
        } catch (IOException ex) {
            Logger.getLogger(GestClienThread.class.getName()).log(Level.SEVERE, "Fallito", ex);
        }
    }

    private String getDestinatari(ArrayList<String> dest) {
        String ret = dest.get(0);
        if (dest.size() > 1) {
            for (int i = 1; i < dest.size(); i++) {
                ret = ret + ";" + dest.get(i);
            }
        }

        return ret;
    }

}
