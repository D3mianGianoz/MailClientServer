package model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;

/**
 * Oggetto email inviabile tramite socket
 * @author Alberto Costamagna and Damiano Gianotti
 */
public class SimpleEmail implements Serializable {
    
    private static final long serialVersionUID = 123456789;
    
    private int id;
    private String mittente;
    private ArrayList<String> destinatri;
    private String oggetto;
    private String testo;
    private LocalDate data;

    public SimpleEmail(int id, String mittente, ArrayList<String> destinatri, String oggetto, String testo, LocalDate data) {
        this.id = id;
        this.mittente = mittente;
        this.destinatri = destinatri;
        this.oggetto = oggetto;
        this.testo = testo;
        this.data = data;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMittente() {
        return mittente;
    }

    public void setMittente(String mittente) {
        this.mittente = mittente;
    }

    public ArrayList<String> getDestinatri() {
        return destinatri;
    }

    public void setDestinatri(ArrayList<String> destinatri) {
        this.destinatri = destinatri;
    }

    public String getOggetto() {
        return oggetto;
    }

    public void setOggetto(String oggetto) {
        this.oggetto = oggetto;
    }

    public String getTesto() {
        return testo;
    }

    public void setTesto(String testo) {
        this.testo = testo;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "SimpleEmail{" + "id=" + id + ", mittente=" + mittente + ", destinatri=" + destinatri + ", oggetto=" + oggetto + ", testo=" + testo + ", data=" + data + '}';
    }
    
    
    
}
