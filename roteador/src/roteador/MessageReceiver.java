package roteador;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class MessageReceiver implements Runnable {

    private static final Logger logger = Logger.getLogger(MessageReceiver.class.getName());
    private static final int BUFFER_SIZE = 1024;
    private DatagramSocket socket;
    private TabelaRoteamento tabela;

    private String ip_host;

    public MessageReceiver(DatagramSocket socket, TabelaRoteamento tabela, String ip) {
        this.socket = socket;
        this.tabela = tabela;
        this.ip_host = ip.substring(1);
    }

    @Override
    public void run() {
        byte[] buffer = new byte[BUFFER_SIZE];

        logger.info("Thread MessageReceiver running...");

        while (true) {

            try {
                DatagramPacket packet = new DatagramPacket(buffer, BUFFER_SIZE, InetAddress.getByName(ip_host),5000);
                socket.receive(packet);

                String message = new String(packet.getData(), 0, packet.getLength());

                tabela.updateTabela(message, packet.getAddress());
            } catch (IOException e) {
                logger.warning("Exception thrown");
                e.printStackTrace();
            }
        }
    }
}
