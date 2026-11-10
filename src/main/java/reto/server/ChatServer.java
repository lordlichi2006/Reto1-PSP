package reto.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Servidor del chat (consola). Es el ServidorCine con un ClientManager
 * compartido en lugar del Cine: while(true) + accept() + un hilo por cliente.
 */
public class ChatServer {

    private static final int PORT = 5000;
    private static final int MONITOR_SECONDS = 10;

    public static void main(String[] args) {

        // Un único objeto compartido por todos los hilos
        ClientManager manager = new ClientManager();

        // Hilo que cada X segundos muestra las estadísticas
        new MonitorThread(manager, MONITOR_SECONDS).start();

        try (ServerSocket server = new ServerSocket(PORT)) {

            manager.log("Servidor de chat escuchando en el puerto " + PORT);

            while (true) {
                Socket socket = server.accept();
                manager.log("Nueva conexión desde " + socket.getInetAddress().getHostAddress());
                new ClientThread(socket, manager).start();   // start(), no run()
            }

        } catch (IOException e) {
            System.out.println("Error en el servidor: " + e.getMessage());
        }
    }
}
