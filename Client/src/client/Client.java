/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import connection.ClientSocket;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.DataModel;

/**
 *
 * @author Damiano
 */
public class Client extends Application {

    private static Stage primaryStage;
    private static Stage secondaryStage;
    private static AnchorPane mainLayout;
    private static ClientSocket clsocket;

    public static ClientSocket getClsocket() {
        return clsocket;
    }

    public static void setClsocket(ClientSocket clsocket) {
        Client.clsocket = clsocket;
    }

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        primaryStage.setTitle("Effetua il Login! ");
        showLoginView();
        
        //Se chiudo il client chiudo tutto anche il socket
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override 
            public void handle(WindowEvent event) {
                clsocket.cls();
                Platform.exit();
                System.exit(0);
            }
        });
    }
    
    protected static void showLoginView() {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Client.class.getResource("/login/Login.fxml"));
        try {
            mainLayout = loader.load();
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, "load del layout Login", ex);
        }
        Scene scene = new Scene(mainLayout);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void showEmailClient(String emailLogg) {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Client.class.getResource("ClientView.fxml"));
        
        AnchorPane ClientPane;
        try {
            ClientPane = loader.load();
            
             //Prendo il controller
            ClientController controller = loader.getController();
            
            //Carico il DataModel
            DataModel model = new DataModel();
            
            //Provo caricare i dati dal server
            model.loadDataReal(clsocket);
            Logger.getLogger(Client.class.getName()).log(Level.FINE, "Carico il data Model con queste Email", model.toString());
            
            //faccio partire il controller
            controller.initModel(model);
            
            Scene sceneEm = new Scene(ClientPane);
            primaryStage.setTitle("Client Email di " + emailLogg);
            primaryStage.setScene(sceneEm);
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, "load del layout Principale", ex);
        }
    }

    public static void showComposeEmail() {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Client.class.getResource("/compose/GUInewEmail.fxml"));
        AnchorPane ClientPane;
        try {
            ClientPane = loader.load();
            Scene sceneEm = new Scene(ClientPane);
            secondaryStage = new Stage();
            secondaryStage.setTitle("Nuova Email");
            secondaryStage.setScene(sceneEm);
            secondaryStage.show();
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, "load del layout ComposeEmail", ex);
        }
    }

        /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
