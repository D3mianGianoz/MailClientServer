/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compose;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import model.DataModel;
import model.Email;

/**
 * FXML Controller class
 *
 * @author Damiano
 */
public class ComposeController implements Initializable {

    private DataModel cmpModel;

    @FXML
    private TextArea txtTestoNw;

    @FXML
    private TextArea txtOggettoNw;

    @FXML
    private TextArea txtDestinatarioNw;

    @FXML
    void c64040(ActionEvent event) {
    }

    @FXML
    void sendEmail(ActionEvent event) {
    }

    @FXML
    void goBackHome(ActionEvent event) {
        //Client.showEmailClient();
    }

    public void initModel(DataModel model) {
        if (this.cmpModel != null) {
            throw new IllegalStateException("Il Model può essere iniziallato una volta sola");
        }

        this.cmpModel = model;
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    private void newEmail() {
        String dstUniti = txtDestinatarioNw.getText();
        List<String> asList = Arrays.asList((dstUniti.split("\\s*,\\s*")));
        String oggetto = txtOggettoNw.getText();
        String testo = txtTestoNw.getText();
        
    }

}
