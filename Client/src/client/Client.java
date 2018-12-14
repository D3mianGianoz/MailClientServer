package client;

import compose.ComposeController;
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
 * @author Alberto Costamagna and Damiano Gianotti
 */
public class Client extends Application {

    private static Stage primaryStage;
    private static Stage secondaryStage;
    private static AnchorPane mainLayout;
    private static ClientSocket clSocket;
    private static ClientController CController;
    private static DataModel coreModel;
    private static String userEmail;

    public static String getUserEmail() {
        return userEmail;
    }

    public static ClientSocket getClsocket() {
        return clSocket;
    }

    public static void setClsocket(ClientSocket clsocket) {
        Client.clSocket = clsocket;
    }


    /**
     * Metodo di start per la visualizzazione dell'interfaccia grafica Inoltre
     * se chiudo lo stage principale chiamo il metodo cls() per la terminazione
     * del Thread GestClient e Platform.exit() per chiudere tutti gli eventuali
     * stage aperti
     *
     * @param stage
     * @throws java.lang.Exception
     */
    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        showLoginView();

        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                if (clSocket != null) {
                    clSocket.cls();
                    System.out.println("Socket chiuso nel client");
                }
                
                if (CController != null) {
                    CController.getcThread().exit();
                    System.out.println("Thread di sync chiuso nel client");
                }

                Platform.exit();
                System.exit(0);
            }
        });
    }

    /**
     * Metoto per visualizzare la schermata di login
     */
    protected static void showLoginView() {
        primaryStage.setTitle("Effetua il Login! ");
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

    /**
     * Metoto per creare e caricare la schermata di visualizzazione delle email,
     * la pagina principale del Client Prendo il controller collegato e
     * iniziallizo il controller con lo un nuovo model
     *
     * @param email email identificativa del client
     */
    public static void showEmailClient(String email) {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Client.class.getResource("ClientView.fxml"));

        AnchorPane ClientPane;
        try {
            ClientPane = loader.load();

            CController = loader.getController();
            
            coreModel = new DataModel();

            userEmail = email;

            CController.initModel(coreModel);

            Scene sceneEm = new Scene(ClientPane);
            primaryStage.setTitle("Client Email di " + userEmail);
            primaryStage.setScene(sceneEm);
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, "load del layout Principale", ex);
        }
    }

    /**
     * Metoto per visualizzare la schermata di composizione delle nuove email da
     * inviare Prendo il controller collegato e iniziallizo il controller con lo
     * stesso model
     * 
     * @param onAction azione da eseguire
     */
    public static void showComposeEmail(String onAction) {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Client.class.getResource("/compose/newEmail.fxml"));
        AnchorPane ClientPane;
        try {
            ClientPane = loader.load();

            ComposeController cmpController = loader.getController();

            cmpController.initModel(coreModel, onAction);

            Scene sceneEm = new Scene(ClientPane);
            secondaryStage = new Stage();
            secondaryStage.setTitle(onAction);
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
