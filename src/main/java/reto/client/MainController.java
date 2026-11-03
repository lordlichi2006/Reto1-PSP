package reto.client;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class MainController {

    @FXML
    private Button btnConnect;

    @FXML
    private Button btnSendMsg;

    @FXML
    private CheckBox chkPrivateMessage;

    @FXML
    private Label lblConnected;

    @FXML
    private Label lblError;

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

}
