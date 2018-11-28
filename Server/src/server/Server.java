package server;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import thread.ServerThread;

/**
 *
 * @author Alberto Costamagna and Damiano Gianotti
 */
public class Server extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        // Recupero il loader
        //FXMLLoader loader = new FXMLLoader(getClass().getResource("FXMLDocument.fxml"));
        
        Parent root = FXMLLoader.load(getClass().getResource("FXMLDocument.fxml"));
        
       
        
        
        Scene scene = new Scene(root);
        
        stage.setScene(scene);
        stage.show();
        
       
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
