package reto.server;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientThread extends Thread {

    private final Socket clientSocket;
    private final String name;

    public ClientThread(Socket socketCliente, String name) {

        this.clientSocket = socketCliente;
        this.name = name;
    }

    @Override
    public void run() {

        try (ObjectOutputStream input = new ObjectOutputStream(clientSocket.getOutputStream());
             ObjectInputStream output = new ObjectInputStream(clientSocket.getInputStream())){


            String respuestaCliente = (String) output.readObject();

            

            if (respuestaCliente.equalsIgnoreCase("Y")) {

                respuesta = cine.reservarEntrada();

            } else {

                respuesta = "Reservation Canceled.";
            }

            input.writeObject(respuesta);
            input.flush();

            clientSocket.close();

        } catch (Exception e) {

        }
    }
}
