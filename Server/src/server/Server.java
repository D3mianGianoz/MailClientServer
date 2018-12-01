package server;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 *
 * @author Alberto Costamagna and Damiano Gianotti
 */
public class Server extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("FXMLDocument.fxml"));
        
        Scene scene = new Scene(root);
        
        stage.setScene(scene);
        stage.show();
        
        //Se chiudo il server chiudo tutto //TODO assiucurarsi che chiuda anche il socket
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override 
            public void handle(WindowEvent event) {
                Platform.exit();
                System.exit(0);
            }
        });
    }
    

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
