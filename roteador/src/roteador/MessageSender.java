package roteador;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

public class MessageSender implements Runnable {

    private static final Logger logger = Logger.getLogger(MessageSender.class.getName());
    private static final int SEND_INTERVAL = 10000;
    private DatagramSocket socket;
    private TabelaRoteamento tabela;
    private InetAddress[] vizinhos;
    private int delay = 0;

    public MessageSender(DatagramSocket socket, TabelaRoteamento tabela, InetAddress[] vizinhos) {
        this.socket = socket;
        this.tabela = tabela;
        this.vizinhos = vizinhos;
        logger.info("vizinhos length: "+vizinhos.length);
    }

    @Override
    public void run() {
        Timer timer = new Timer();

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                sendTabela();
                if(delay >= 3){
                    delay = 0;
                    tabela.zera_e_adiciona_vizinhos();
                }

                //tabela.imprimirTabela();
                delay++;
            }
        }, 0, SEND_INTERVAL);
    }

    private void sendTabela() {
        String tabelaString = tabela.getTabelaString();

        for (InetAddress v : vizinhos){
            Rota r = new Rota(1,v.toString().substring(1));
            tabela.add_rota_vizinho(r);
        }

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
