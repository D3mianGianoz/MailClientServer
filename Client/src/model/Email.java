package model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author Alberto Costamagna, Damiano Gianotti
 *
 * Client
 */
public final class Email {

    private SimpleEmail simpleClientEmail;

    // ID della email
    private final IntegerProperty id = new SimpleIntegerProperty();

    public int getId() {
        return id.get();
    }

    public void setId(int value) {
        id.set(value);
    }

    public IntegerProperty idProperty() {
        return id;
    }

    // Mittente
    private final StringProperty mittente = new SimpleStringProperty();

    public String getMittente() {
        return mittente.get();
    }

    public void setMittente(String value) {
        mittente.set(value);
    }

    public StringProperty mittenteProperty() {
        return mittente;
    }

    // Data Invio email
    private final ObjectProperty<LocalDate> data = new SimpleObjectProperty<>();

    public LocalDate getData() {
        return data.get();
    }

    public void setData(LocalDate value) {
        data.set(value);
    }

    public StringProperty dataProperty() {
        String dataAsString = getData().toString();
        SimpleStringProperty result = new SimpleStringProperty(dataAsString);
        return result;
    }

    /*
    public ObjectProperty<LocalDate> dataProperty() {
        return data;
    }
     */
    //Destinatari
    private final ObservableList<String> destinatari = FXCollections.observableArrayList(new ArrayList<>());

    public ObservableList<String> getDestinatariList() {
        return destinatari;
    }

    public void setDestinatari(ArrayList<String> d) {
        d.forEach((String s) -> {
            this.destinatari.add(s);
        });
    }

    public String getDestinatari() {
        String result = "";

        if (destinatari != null) {
            for (String dest : destinatari) {
                result = result + dest + ";";
            }

            return result;
        }
        return "ERROR in get, null list";
    }

    public StringProperty destinatariStrProperty() {
        return new SimpleStringProperty(getDestinatari());
    }

    //Oggetto
    private final StringProperty oggetto = new SimpleStringProperty();

    public String getOggetto() {
        return oggetto.get();
    }

    public void setOggetto(String value) {
        oggetto.set(value);
    }

    public StringProperty oggettoProperty() {
        return oggetto;
    }

    //Testo
    private final StringProperty testo = new SimpleStringProperty();

    public String getTesto() {
        return testo.get();
    }

    public void setTesto(String value) {
        testo.set(value);
    }

    public StringProperty testoProperty() {
        return testo;
    }

    public Email(int id, String mit, ArrayList<String> dest, String ogg, String tes, LocalDate data) {
        setId(id);
        setMittente(mit);
        setDestinatari(dest);
        setOggetto(ogg);
        setTesto(tes);
        setData(data);
    }

    public Email(SimpleEmail sEmail) {
        this.simpleClientEmail = sEmail;
        setId(sEmail.getId());
        setMittente(sEmail.getMittente());
        setDestinatari(sEmail.getDestinatri());
        setOggetto(sEmail.getOggetto());
        setTesto(sEmail.getTesto());
        setData(sEmail.getData());
    }

    public Email() {
        //TODO REMOVE
    }

    @Override
    public String toString() {
        return ("Mittente: " + getMittente() + " | Oggetto: " + getOggetto() + " | Data: " + getData());
    }

    public String toFileFormat() {
        return ("Id: " + getId() + "\nMittente: " + getMittente() + "\nDestinatario/i: " + destinatari.toString()
                + "\nOggetto: " + getOggetto() + "\nTesto: " + getTesto() + "\nData: " + getData());
    }

    /*
     * public static void main(String[] args) { ArrayList<String> dest = new
     * ArrayList<>(); dest.add("Dami"); Email ess = new Email(0, "mit", dest,
     * "ogg", "tes", LocalDate.now()); dest.add("Costi"); Email es = new
     * Email(0, "mit", dest, "ogg", "tes", LocalDate.now());
     * System.out.println("Email 1\n" + ess.toString() + "\nEmail 2\n" +
     * es.toString()); }
     */
    public ArrayList<Email> load() {
        ArrayList<String> dest = new ArrayList<>();
        dest.add("Dami");
        Email ess = new Email(1, "Damiano", dest, "Client Email", "Funziona ?", LocalDate.now());
        dest.add("Costi");
        Email es = new Email(2, "Giovanni", dest, "Roberto mi infatistidisce", "Non ne posso più, ti prego aiutami", LocalDate.now());
        Email es3 = new Email(3, "Federico", dest, "Oggi c'è il sole", "Finalmente una bella giornata, peccato io debba studiare", LocalDate.now());

        ArrayList<Email> result = new ArrayList<>();
        result.add(es);
        result.add(ess);
        result.add(es3);

        return result;
    }

    /*
    // Metodo di prova per scriver l'oggetto email correttamente
    private void writeObject(ObjectOutputStream s) {
        try {
            s.defaultWriteObject();
            s.writeInt(getId());
            s.writeUTF(getMittente());
            s.writeObject(getDestinatari());
            s.writeUTF(getOggetto());
            s.writeUTF(getTesto());
            s.writeObject(getData());
        } catch (IOException ex) {
            Logger.getLogger(Email.class.getName()).log(Level.SEVERE, "Write Email fallita", ex);
        }

    }

    private void readObject(ObjectInputStream s) {
        try {
            setId(s.readInt());
            setMittente(s.readUTF());
            setDestinatari((ArrayList<String>) s.readObject());
            setOggetto(s.readUTF());
            setTesto(s.readUTF());
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(Email.class.getName()).log(Level.SEVERE, "Read Email fallita", ex);
        }
    }
     */
}
