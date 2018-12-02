/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.util.ArrayList;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author Damiano
 * 
 * Necessaria per il Client, forse anche per il server ?
 */
public class DataModel {

    private ObservableList<Email> emailList = FXCollections.observableArrayList();
    private final ObjectProperty<Email> currentEmail = new SimpleObjectProperty<Email>(null);

    public ObservableList<Email> getEmailList() {
        return emailList;
    }

    public ObjectProperty<Email> getCurrentEmailProperty() {
        return currentEmail;
    }

    public Email getCurrentEmail() {
        return currentEmail.get();
    }

    public final void setCurrentEmail(Email email) {
        currentEmail.set(email);
    }

    public void remove(Email emailR) {
        emailList.remove(emailR);
    }

    public void loadData() {
        // Caricare i dati inviati al Server

        //TODO da rimuovere solo per provare
        Email l = new Email();
        ArrayList<Email> load = l.load();
        emailList = FXCollections.observableArrayList(load);
    }

    @Override
    public String toString() {
        return "DataModel{" + "emailList=" + emailList.toString() + ", currentEmail=" + currentEmail.toString() + '}';
    }

}
