package reto.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import reto.model.Message;
import reto.model.Type;

/**
 * Hilo que atiende a UN cliente (como HiloCliente del Cine).
 * Lee sus mensajes en bucle y se los pasa al ClientManager compartido.
 */
public class ClientThread extends Thread {

    private final Socket clientSocket;
    private final ClientManager manager;
    private ObjectOutputStream output;   // lo guardamos para que otros hilos puedan enviarle mensajes
    private String username = "?";

    public ClientThread(Socket clientSocket, ClientManager manager) {
        this.clientSocket = clientSocket;
        this.manager = manager;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public void run() {
        boolean registered = false;

        // Primero Output y después Input (igual que en el cliente)
        try (ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream())) {

            output = out;

            // 1. El primer mensaje tiene que ser el LOGIN con el nombre
            Message login = (Message) in.readObject();
            if (login.getType() != Type.CONNECT) {
                return;
            }
            username = login.getMessengerName();

            // 2. Pedimos entrar. Si devuelve un texto, es el motivo del rechazo
            String error = manager.addClient(this);
            if (error != null) {
                send(new Message(Type.SYSTEM, error, "SERVIDOR"));
                return;
            }
            registered = true;

            // 3. Bucle de mensajes hasta LOGOUT
            while (true) {
                Message m = (Message) in.readObject();

                if (m.getType() == Type.DISCONNECT) {
                    break;
                }

                m.setMessengerName(username);   // el emisor siempre es el de esta conexión

                if (m.getType() == Type.PRIVATE) {
                    manager.privateMessage(m, this);
                } else if (m.getType() == Type.PUBLIC) {
                    manager.publicMessage(m);
                }
            }

        } catch (IOException | ClassNotFoundException e) {
            // El cliente ha cerrado sin avisar o se ha cortado la red
            manager.log("Conexión perdida con " + username + " (" + e.getClass().getSimpleName() + ")");
        } finally {
            if (registered) {
                manager.removeClient(this);
            }
            try {
                clientSocket.close();
            } catch (IOException e) {
                manager.log("No se pudo cerrar el socket de " + username);
            }
        }
    }

    /**
     * Envía un mensaje a este cliente. Lo llaman otros hilos (a través del
     * ClientManager), por eso es synchronized: dos hilos no pueden escribir
     * a la vez en el mismo flujo.
     */
    public synchronized void send(Message m) {
        try {
            output.writeObject(m);
            output.flush();
        } catch (IOException e) {
            // Si falla, este cliente se ha ido: su propio hilo lo quitará de la lista
            manager.log("No se pudo enviar a " + username);
        }
    }
}
