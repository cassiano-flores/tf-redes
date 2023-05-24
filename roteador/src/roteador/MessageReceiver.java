package roteador;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class MessageReceiver implements Runnable {

    private static final int BUFFER_SIZE = 1024;
    private DatagramSocket socket;
    private TabelaRoteamento tabela;

    public MessageReceiver(DatagramSocket socket, TabelaRoteamento tabela) {
        this.socket = socket;
        this.tabela = tabela;
    }

    @Override
    public void run() {
        byte[] buffer = new byte[BUFFER_SIZE];

        while (true) {
            try {
                DatagramPacket packet = new DatagramPacket(buffer, BUFFER_SIZE);
                socket.receive(packet);

                String message = new String(packet.getData(), 0, packet.getLength());
                tabela.updateTabela(message, packet.getAddress());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
