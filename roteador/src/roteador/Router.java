package roteador;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Router {

    private static final String NEIGHBORS_FILE = "IPNeighbors.txt";
    private static String ipHost;

    public static void main(String[] args) {
        List<InetAddress> neighbors = readNeighborsFromFile();

        try (DatagramSocket socket = new DatagramSocket(5000)) {
            RoutingTable table = new RoutingTable(ipHost, neighbors.toArray(new InetAddress[0]));

            MessageReceiver receiver = new MessageReceiver(socket, table, ipHost);
            MessageSender sender = new MessageSender(socket, table, neighbors.toArray(new InetAddress[0]));

            Thread receiverThread = new Thread(receiver);
            Thread senderThread = new Thread(sender);

            receiverThread.start();
            senderThread.start();

            receiverThread.join();
            senderThread.join();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static List<InetAddress> readNeighborsFromFile() {
        List<InetAddress> neighbors = new ArrayList<>();

        URL path = Router.class.getResource(Router.NEIGHBORS_FILE);

        try {
            File file = new File(path.getFile());
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;

            while ((line = reader.readLine()) != null) {
                InetAddress address = InetAddress.getByName(line);

                if (ipHost == null) {
                    ipHost = address.toString();
                } else {
                    neighbors.add(address);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return neighbors;
    }
}
