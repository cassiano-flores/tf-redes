package roteador;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Timer;
import java.util.TimerTask;

public class MessageSender implements Runnable {

    private static final int SEND_INTERVAL = 10000;
    private final DatagramSocket socket;
    private final RoutingTable table;
    private final InetAddress[] neighbors;
    private int delay = 0;

    public MessageSender(DatagramSocket socket, RoutingTable table, InetAddress[] neighbors) {
        this.socket = socket;
        this.table = table;
        this.neighbors = neighbors;
    }

    @Override
    public void run() {
        Timer timer = new Timer();

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                sendTable();
                if (delay >= 3) {
                    delay = 0;
                    table.resetAndAddNeighbors();
                }
                delay++;
                table.printTable();
            }
        }, 0, SEND_INTERVAL);
    }

    private void sendTable() {
        String tableString = table.getTableString();
        byte[] buffer;

        for (InetAddress neighbor : neighbors) {
            Route route = new Route(1, neighbor.toString().substring(1));
            table.addNeighborRoute(route);
        }

        if (neighbors.length == 0) {
            buffer = "!".getBytes();
        } else {
            buffer = tableString.getBytes();
        }

        for (InetAddress neighbor : neighbors) {
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, neighbor, 5000);
            try {
                socket.send(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
