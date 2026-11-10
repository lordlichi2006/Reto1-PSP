package reto.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import reto.model.Message;
import reto.model.Type;

/**
 * Controlador de la ventana. Todo lo que hay aquí se ejecuta en el hilo
 * de JavaFX: conectar, enviar y desconectar. La recepción va en ReceiverThread.
 */
public class MainController {

    @FXML
    private Button btnConnect;

    @FXML
    private Button btnDisconnect;

    @FXML
    private Button btnSendMsg;

    @FXML
    private CheckBox chkPrivateMessage;

    @FXML
    private Label lblConnected;

    @FXML
    private Label lblError;

    @FXML
    private TextArea txtChat;

    @FXML
    private TextField txtIpaddress;

    @FXML
    private TextField txtMessage;

    @FXML
    private TextField txtPort;

    @FXML
    private TextField txtPrivateMessageUsername;

    @FXML
    private TextField txtUsername;

    private Socket socket;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private String username;
    private boolean connected = false;

    @FXML
    private void initialize() {
        lblError.setText("");
        setDisconnectedState();
    }

    @FXML
    private void onConnect() {
        lblError.setText("");

        String ip = txtIpaddress.getText().isBlank() ? "127.0.0.1" : txtIpaddress.getText().trim();
        String portText = txtPort.getText().isBlank() ? "5000" : txtPort.getText().trim();
        username = txtUsername.getText().trim();

        if (username.isEmpty()) {
            lblError.setText("Escribe un nombre de usuario");
            return;
        }

        int port;
        try {
            port = Integer.parseInt(portText);
        } catch (NumberFormatException e) {
            lblError.setText("El puerto tiene que ser un número");
            return;
        }

        try {
            socket = new Socket(ip, port);
            // Primero Output y después Input (igual que en el servidor)
            output = new ObjectOutputStream(socket.getOutputStream());
            input = new ObjectInputStream(socket.getInputStream());

            output.writeObject(new Message(Type.CONNECT, "", username));
            output.flush();

            connected = true;
            txtChat.clear();
            setConnectedState();

            // Hilo independiente para recibir
            new ReceiverThread(input, this).start();

        } catch (IOException e) {
            lblError.setText("No se pudo conectar con " + ip + ":" + port);
            closeConnection();
        }
    }

    @FXML
    private void onSend() {
        if (!connected) {
            return;
        }
        String text = txtMessage.getText().trim();
        if (text.isEmpty()) {
            return;
        }

        Message m;
        if (chkPrivateMessage.isSelected()) {
            String recipient = txtPrivateMessageUsername.getText().trim();
            if (recipient.isEmpty()) {
                lblError.setText("Escribe a quién va el mensaje privado");
                return;
            }
            m = new Message(Type.PRIVATE, text, recipient, username);
        } else {
            m = new Message(Type.PUBLIC, text, username);
        }

        try {
            output.writeObject(m);
            output.flush();
            txtMessage.clear();
            lblError.setText("");
        } catch (IOException e) {
            lblError.setText("No se pudo enviar el mensaje");
        }
    }

    @FXML
    public void onDisconnect() {
        if (!connected) {
            return;
        }
        connected = false;   // así connectionLost() sabe que ha sido a propósito
        try {
            output.writeObject(new Message(Type.DISCONNECT, "", username));
            output.flush();
        } catch (IOException e) {
            // el servidor ya no está; da igual
        }
        closeConnection();
        setDisconnectedState();
        txtChat.appendText("*** Te has desconectado ***\n");
    }

    /** Lo llama ReceiverThread a través de Platform.runLater. */
    public void showMessage(Message m) {
        txtChat.appendText(m + "\n");
    }

    /** Lo llama ReceiverThread cuando se corta la conexión. */
    public void connectionLost() {
        if (connected) {   // si no lo hemos pedido nosotros, es un error
            connected = false;
            lblError.setText("Se ha perdido la conexión con el servidor");
            closeConnection();
            setDisconnectedState();
        }
    }

    private void closeConnection() {
        try {
            if (socket != null) {
                socket.close();   // cerrar el socket cierra también sus flujos
            }
        } catch (IOException e) {
            lblError.setText("Error al cerrar la conexión");
        }
    }

    private void setConnectedState() {
        lblConnected.setText("CONECTADO como " + username + " ●");
        lblConnected.setTextFill(Color.web("#008844"));
        btnConnect.setDisable(true);
        btnDisconnect.setDisable(false);
        btnSendMsg.setDisable(false);
        txtIpaddress.setDisable(true);
        txtPort.setDisable(true);
        txtUsername.setDisable(true);
    }

    private void setDisconnectedState() {
        lblConnected.setText("NO CONECTADO ○");
        lblConnected.setTextFill(Color.web("#cc0055"));
        btnConnect.setDisable(false);
        btnDisconnect.setDisable(true);
        btnSendMsg.setDisable(true);
        txtIpaddress.setDisable(false);
        txtPort.setDisable(false);
        txtUsername.setDisable(false);
    }
}
