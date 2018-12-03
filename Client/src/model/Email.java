package model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
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
public class Email implements Serializable {

    // ID della email
    private final IntegerProperty id = new SimpleIntegerProperty();

    Email() {
        //TODO REMOVE
    }

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
}
