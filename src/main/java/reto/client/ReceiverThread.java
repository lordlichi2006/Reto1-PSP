package reto.client;

import java.io.IOException;
import java.io.ObjectInputStream;

import javafx.application.Platform;
import reto.model.Message;

/**
 * Hilo que SOLO recibe mensajes del servidor, para que la ventana no se
 * bloquee mientras espera en readObject().
 * Este hilo no puede tocar la interfaz: se lo pide al hilo de JavaFX
 * con Platform.runLater (como en el "Ejercicio UI y números").
 */
public class ReceiverThread extends Thread {

    private final ObjectInputStream input;
    private final MainController controller;

    public ReceiverThread(ObjectInputStream input, MainController controller) {
        this.input = input;
        this.controller = controller;
    }

    @Override
    public void run() {
        try {
            while (true) {
                Message m = (Message) input.readObject();
                Platform.runLater(() -> controller.showMessage(m));
            }
        } catch (IOException | ClassNotFoundException e) {
            // El servidor ha cerrado la conexión (o nos hemos desconectado nosotros)
            Platform.runLater(() -> controller.connectionLost());
        }
    }
}
