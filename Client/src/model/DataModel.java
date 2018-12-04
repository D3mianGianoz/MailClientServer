package model;

import connection.ClientSocket;
import java.util.ArrayList;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author Alberto Costamagna , Damiano Gianotti
 *
 * Necessaria per il Client
 */
public class DataModel {

    private ObservableList<Email> emailList = FXCollections.observableArrayList();
    private final ObjectProperty<Email> currentEmail = new SimpleObjectProperty<>(null);

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

    //TODO da rimuovere solo per provare
    public void loadData() {
        // Caricare i dati inviati al Server

        Email l = new Email();
        ArrayList<Email> load = l.load();
        emailList = FXCollections.observableArrayList(load);
    }

    public void loadDataReal(ClientSocket clsocketDM) {
        // PROVO A RICHIEDERE LA LISTE DELLE EMAIL DAL SERVER
        clsocketDM.sendObject("getMyEmails");
        ArrayList<SimpleEmail> read = (ArrayList<SimpleEmail>) clsocketDM.readObject();
        fromSimpletoEmail(read);
    }

    public void fromSimpletoEmail(ArrayList<SimpleEmail> serverL) {
        ArrayList<Email> load = new ArrayList<>();

        for (SimpleEmail smail : serverL) {
            load.add(new Email(smail));
        }
        emailList = FXCollections.observableArrayList(load);
    }

    @Override
    public String toString() {
        return "DataModel{" + "emailList=" + emailList.toString() + ", currentEmail=" + currentEmail.toString() + '}';
    }

}
