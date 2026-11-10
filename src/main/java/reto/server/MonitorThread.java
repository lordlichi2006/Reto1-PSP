package reto.server;

/**
 * Hilo monitor: cada X segundos muestra en consola usuarios conectados,
 * tiempo activo del servidor y último mensaje (como el Trabajador con sleep).
 */
public class MonitorThread extends Thread {

    private final ClientManager manager;
    private final int seconds;

    public MonitorThread(ClientManager manager, int seconds) {
        this.manager = manager;
        this.seconds = seconds;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(seconds * 1000L);
            } catch (InterruptedException e) {
                return;
            }
            System.out.println("[MONITOR] " + manager.getStats());
        }
    }
}
