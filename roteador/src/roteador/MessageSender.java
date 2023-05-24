package roteador;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Timer;
import java.util.TimerTask;

public class MessageSender implements Runnable {

    private static final int SEND_INTERVAL = 10000;
    private DatagramSocket socket;
    private TabelaRoteamento tabela;
    private InetAddress[] vizinhos;

    public MessageSender(DatagramSocket socket, TabelaRoteamento tabela, InetAddress[] vizinhos) {
        this.socket = socket;
        this.tabela = tabela;
        this.vizinhos = vizinhos;
    }

    @Override
    public void run() {
        Timer timer = new Timer();

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                sendTabela();
            }
        }, 0, SEND_INTERVAL);
    }

    private void sendTabela() {
        String tabelaString = tabela.getTabelaString();
        byte[] buffer = tabelaString.getBytes();

        for (InetAddress vizinho : vizinhos) {
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, vizinho, 5000);
            try {
                socket.send(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
