package reto.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * JavaFX App (cliente del chat)
 */
public class App extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(App.class.getResource("primary.fxml"));
        Parent root = loader.load();
        MainController controller = loader.getController();

        stage.setTitle("Chat PSP");
        stage.setScene(new Scene(root, 960, 540));
        // Si se cierra la ventana estando conectado, avisamos al servidor
        stage.setOnCloseRequest(e -> controller.onDisconnect());
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

}
