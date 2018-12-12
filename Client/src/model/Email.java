package model;

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
 * @author Alberto Costamagna and Damiano Gianotti
 *
 * Client Email
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
        this.simpleClientEmail = new SimpleEmail(id, mit, dest, ogg, tes, data);
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

    @Override
    public String toString() {
        return ("Mittente: " + getMittente() + " | Oggetto: " + getOggetto() + " | Data: " + getData());
    }

    public String toFileFormat() {
        return ("Id: " + getId() + "\nMittente: " + getMittente() + "\nDestinatario/i: " + destinatari.toString()
                + "\nOggetto: " + getOggetto() + "\nTesto: " + getTesto() + "\nData: " + getData());
    }

    public SimpleEmail getSimpleEmail() {
        return this.simpleClientEmail;
    }

}
