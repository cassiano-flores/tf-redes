package roteador;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class    Roteador {
    private static final String VIZINHOS_FILE = "IPVizinhos.txt";

    public static void main(String[] args) {
        List<InetAddress> vizinhos = readVizinhosFromFile();

        try (DatagramSocket socket = new DatagramSocket(5000)) {
            TabelaRoteamento tabela = new TabelaRoteamento();

            MessageReceiver receiver = new MessageReceiver(socket, tabela);
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

        // para testar localmente
        try {
            vizinhos.add(InetAddress.getByName("127.0.0.2"));
            vizinhos.add(InetAddress.getByName("127.0.0.3"));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

//        URL path = Roteador.class.getResource(Roteador.VIZINHOS_FILE);
//
//        try {
//            File file = new File(path.getFile());
//            BufferedReader reader = new BufferedReader(new FileReader(file));
//            String line;
//
//            while ((line = reader.readLine()) != null) {
//                InetAddress address = InetAddress.getByName(line);
//                vizinhos.add(address);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        return vizinhos;
    }
}
