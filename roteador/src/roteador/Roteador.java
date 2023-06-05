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

public class Roteador {

    private static final String VIZINHOS_FILE = "IPVizinhos.txt";
    private static String ip_host;

    public static void main(String[] args) {
        List<InetAddress> vizinhos = readVizinhosFromFile();

        try (DatagramSocket socket = new DatagramSocket(5000)) {
            TabelaRoteamento tabela = new TabelaRoteamento(ip_host, vizinhos.toArray(new InetAddress[0]));

            MessageReceiver receiver = new MessageReceiver(socket, tabela, ip_host);
            MessageSender sender = new MessageSender(socket, tabela, vizinhos.toArray(new InetAddress[0]));

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

    private static List<InetAddress> readVizinhosFromFile() {
        List<InetAddress> vizinhos = new ArrayList<>();

        URL path = Roteador.class.getResource(Roteador.VIZINHOS_FILE);

        try {
            File file = new File(path.getFile());
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;

            while ((line = reader.readLine()) != null) {
                InetAddress address = InetAddress.getByName(line);

                if (ip_host == null) {
                    ip_host = address.toString();
                } else {
                    vizinhos.add(address);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return vizinhos;
    }
}
