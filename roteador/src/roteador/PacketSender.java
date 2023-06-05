package roteador;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class PacketSender {

    public static void main(String[] args) {
        try (DatagramSocket socket = new DatagramSocket()) {
            String message = "Hello, roteador!";
            byte[] buffer = message.getBytes();

            InetAddress enderecoDestino = InetAddress.getByName("26.219.143.93");

            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, enderecoDestino, 5000);
            socket.send(packet);

            System.out.println("Pacote enviado para o roteador.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
