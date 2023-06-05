package roteador;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.logging.Logger;

public class MessageReceiver implements Runnable {

    private static final Logger logger = Logger.getLogger(MessageReceiver.class.getName());
    private static final int BUFFER_SIZE = 1024;
    private final DatagramSocket socket;
    private final RoutingTable table;
    private final String ipHost;

    public MessageReceiver(DatagramSocket socket, RoutingTable table, String ip) {
        this.socket = socket;
        this.table = table;
        this.ipHost = ip.substring(1);
    }

    @Override
    public void run() {
        byte[] buffer = new byte[BUFFER_SIZE];

        logger.info("Thread MessageReceiver running...");

        while (true) {

            try {
                DatagramPacket packet = new DatagramPacket(buffer, BUFFER_SIZE, InetAddress.getByName(ipHost), 5000);
                socket.receive(packet);

                String message = new String(packet.getData(), 0, packet.getLength());

                table.updateTable(message, packet.getAddress());
            } catch (IOException e) {
                logger.warning("Exception thrown");
                e.printStackTrace();
            }
        }
    }
}
