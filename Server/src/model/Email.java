package model;

import java.time.LocalDate;
import java.util.ArrayList;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author alberto
 */
public class Email {

    
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
    
    
    // Ora Invio email
    private final ObjectProperty<LocalDate> string = new SimpleObjectProperty<>();

    public LocalDate getString() {
        return string.get();
    }

    public void setString(LocalDate value) {
        string.set(value);
    }

    public ObjectProperty stringProperty() {
        return string;
    }
    
    
    //Destinatari
    private final ListProperty<String> destinatari = new SimpleListProperty<>();

    public ObservableList getDestinatari() {
        return destinatari.get();
    }

    public void setDestinatari(ObservableList value) {
        destinatari.set(value);
    }

    public ListProperty destinatariProperty() {
        return destinatari;
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

    public Email(String mit, ArrayList<String> dest,String ogg,String tes,LocalDate data) {
        
    }
    
    
    
    
    
    
    
    
    
    
    
    
}
