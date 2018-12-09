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
 * @author Alberto Costamagna , Damiano Gianotti
 */
public class Client extends Application {

    private static Stage primaryStage;
    private static Stage secondaryStage;
    private static AnchorPane mainLayout;
    private static ClientSocket clSocket;
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

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        showLoginView();

        //Se chiudo il client chiudo tutto anche il socket
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                if (clSocket != null) {
                    clSocket.cls();
                }
                Platform.exit();
                System.exit(0);
            }
        });
    }

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

    public static void showEmailClient(String email) {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Client.class.getResource("ClientView.fxml"));

        AnchorPane ClientPane;
        try {
            ClientPane = loader.load();

            //Prendo il controller
            ClientController controller = loader.getController();

            //Creo il DataModel e lo salvo locamente
            coreModel = new DataModel();

            //Provo caricare i dati dal server
            coreModel.loadData(clSocket);
            //Logger.getLogger(Client.class.getName()).log(Level.FINE, "Carico il data Model con queste Email", coreModel.toString());

            //mi salvo l'email del client
            userEmail = email;

            //faccio partire il controller
            controller.initModel(coreModel);

            Scene sceneEm = new Scene(ClientPane);
            primaryStage.setTitle("Client Email di " + userEmail);
            primaryStage.setScene(sceneEm);
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, "load del layout Principale", ex);
        }
    }

    public static void showComposeEmail(String onAction) {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Client.class.getResource("/compose/newEmail.fxml"));
        AnchorPane ClientPane;
        try {
            ClientPane = loader.load();

            //Prendo il controller
            ComposeController cmpController = loader.getController();
            //iniziallizo il controller
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
