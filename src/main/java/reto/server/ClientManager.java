package reto.server;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import reto.model.Message;
import reto.model.Type;

/**
 * Recurso compartido del servidor (como el Cine del ejercicio de hilos).
 * Hay UN solo objeto y todos los ClientThread lo usan, por eso todos los
 * métodos públicos son synchronized: solo un hilo a la vez puede tocar
 * la lista de clientes, el historial o el último mensaje.
 */
public class ClientManager {

    private static final int MAX_CLIENTS = 5;
    private static final DateTimeFormatter HOUR = DateTimeFormatter.ofPattern("HH:mm:ss");

    private final ArrayList<ClientThread> clients = new ArrayList<>();
    private final ArrayList<Message> history = new ArrayList<>();
    private final long startTime = System.currentTimeMillis();

    /**
     * Intenta registrar un cliente. Devuelve null si ha entrado,
     * o el motivo del rechazo (servidor lleno o nombre repetido).
     */
    public synchronized String addClient(ClientThread newClient) {
        String name = newClient.getUsername();

        if (clients.size() >= MAX_CLIENTS) {
            log("RECHAZADO " + name + ": servidor lleno");
            return "Servidor lleno (máximo " + MAX_CLIENTS + " usuarios). Inténtalo más tarde.";
        }

        for (ClientThread c : clients) {
            if (c.getUsername().equalsIgnoreCase(name)) {
                log("RECHAZADO " + name + ": nombre en uso");
                return "El nombre " + name + " ya está en uso.";
            }
        }

        clients.add(newClient);
        log("ENTRA " + name + " (" + clients.size() + "/" + MAX_CLIENTS + ")");

        // Le mandamos el historial: los públicos y los privados que son suyos
        for (Message message : history) {
            if (!message.isPrivate() || name.equals(message.getMessengerName())
                    || name.equals(message.getRecipientName())) {
                newClient.send(message);
            }
        }

        sendToAll(new Message(Type.SYSTEM, "@" + name + " se ha conectado", "SERVIDOR"));
        return null;
    }

    /** Quita un cliente de la lista y avisa al resto. */
    public synchronized void removeClient(ClientThread client) {
        if (clients.remove(client)) {
            log("SALE " + client.getUsername() + " (" + clients.size() + "/" + MAX_CLIENTS + ")");
            sendToAll(new Message(Type.SYSTEM, "@" + client.getUsername() + " se ha desconectado", "SERVIDOR"));
        }
    }

    /** Mensaje público: se guarda y se reenvía a todos. */
    public synchronized void publicMessage(Message message) {
        history.add(message);
        log(message.toString());
        sendToAll(message);
    }

    /** Mensaje privado: solo al destinatario y una copia al que lo envía. */
    public synchronized void privateMessage(Message message, ClientThread sender) {
        ClientThread recipient = null;
        for (ClientThread c : clients) {
            if (c.getUsername().equals(message.getRecipientName())) {
                recipient = c;
            }
        }

        if (recipient == null) {
            sender.send(
                    new Message(Type.SYSTEM, "El usuario @" + message.getRecipientName() + " no está conectado",
                            "SERVIDOR"));
            return;
        }

        history.add(message);
        log(message.toString());

        recipient.send(message);
        if (recipient != sender) {
            sender.send(message);
        }
    }

    /** Log global: cada acción importante con su hora. */
    public synchronized void log(String text) {
        System.out.println("[" + LocalTime.now().format(HOUR) + "] " + text);
    }

    /** Datos para el hilo monitor. */
    public synchronized String getStats() {

        String lastMessage = "(ninguno)";
        if (!history.isEmpty()) {
            lastMessage = history.get(history.size() - 1).toString();
        }

        long seconds = (System.currentTimeMillis() - startTime) / 1000;
        return "Usuarios conectados: " + clients.size()
                + " | Tiempo activo: " + (seconds / 60) + " min " + (seconds % 60) + " s"
                + " | Último mensaje: " + lastMessage;
    }

    // Solo se llama desde métodos synchronized, así que ya tiene el cerrojo
    private void sendToAll(Message m) {
        for (ClientThread c : clients) {
            c.send(m);
        }
    }
}
