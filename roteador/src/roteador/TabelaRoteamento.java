package roteador;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

public class TabelaRoteamento {

    private final Map<String, String> tabela_aprendizado;
    private final Map<String, Rota> tabela_de_vizinhos;
    private Map<String, Rota> tabela;
    private final String ip_host;

    public TabelaRoteamento(String ip, InetAddress[] vizinhos) {
        tabela_aprendizado = new HashMap<>();
        tabela = new HashMap<>();
        tabela_de_vizinhos = new HashMap<>();
        ip_host = ip.substring(1);

        for (InetAddress v : vizinhos) {
            String ipvizinho = v.toString().substring(1);
            Rota r = new Rota(1, ipvizinho);
            tabela.put(ipvizinho, r);
            tabela_de_vizinhos.put(ipvizinho, r);
        }
    }

    public void zera_e_adiciona_vizinhos() {
        tabela = new HashMap<>();

        for (String s : tabela_de_vizinhos.keySet()) {
            tabela.put(s, tabela_de_vizinhos.get(s));
        }
    }

    public synchronized void updateTabela(String message, InetAddress ipAddress) {
        System.out.println("IP HOST DA MENSAGEM: " + ipAddress.getHostAddress() + ", MENSAGEM RECEBIDA:" + message);
        String[] rotas = message.split("\\*");

        for (String rota : rotas) {
            String[] campos = rota.split(";");

            if (campos.length == 2) {
                String ipDestino = campos[0];
                int metrica = Integer.parseInt(campos[1]);
                String ipSaida = ipAddress.getHostAddress();

                if (!ipDestino.equals(ip_host)) {
                    if (tabela.containsKey(ipDestino)) {
                        Rota rotaExistente = tabela.get(ipDestino);

                        if (metrica < rotaExistente.metrica) {
                            rotaExistente.metrica = metrica + 1;
                            rotaExistente.ipSaida = ipSaida;
                            System.out.println("Rota atualizada: " + ipDestino + " Métrica: " + metrica + " IP de Saída: " + ipSaida);
                            tabela_aprendizado.remove(ipDestino);
                            tabela_aprendizado.put(ipDestino, ipSaida);
                        }
                    } else {
                        boolean is_vizinho = tabela_de_vizinhos.containsKey(ipDestino);

                        if (!is_vizinho) {
                            metrica++;
                            if (!tabela_aprendizado.containsKey(ipDestino))
                                tabela_aprendizado.put(ipDestino, ipSaida);
                        }

                        System.out.println("TABLELA GET IPDESTINO: " + ipDestino + "->" + tabela_aprendizado.get(ipDestino));
                        System.out.println("CONTAINS: " + tabela_aprendizado.get(ipDestino).equals(ipAddress.getHostAddress()));

                        tabela.put(ipDestino, new Rota(metrica, ipSaida));

                        //Recebeu uma possivel rota de loop
                        if (!tabela_aprendizado.get(ipDestino).equals(ipAddress.getHostAddress())) {
                            tabela.remove(ipDestino);
                        }

                        System.out.println("Nova rota adicionada: " + ipDestino + " Métrica: " + metrica + " IP de Saída: " + ipSaida);
                    }
                }
            }
        }

        imprimirTabela();
    }

    public void add_rota_vizinho(Rota rota) {
        tabela.put(rota.ipSaida, rota);
    }

    public synchronized String getTabelaString() {
        StringBuilder tabelaString = new StringBuilder();

        for (Map.Entry<String, Rota> entry : tabela.entrySet()) {
            String ipDestino = entry.getKey();
            Rota rota = entry.getValue();
            tabelaString.append("*").append(ipDestino).append(";").append(rota.metrica);
        }

        return tabelaString.toString();
    }

    public synchronized void imprimirTabela() {
        System.out.println("\n\n                Routing Table");
        System.out.println("------------------------------------------------");
        System.out.println("|  Destination IP  | Metric |    Outgoing IP   |");
        System.out.println("------------------------------------------------");

        for (Map.Entry<String, Rota> entry : tabela.entrySet()) {
            String ipDestino = entry.getKey();
            Rota rota = entry.getValue();
            System.out.printf("| %1$-16s | %2$-6d | %3$-16s |\n", ipDestino, rota.metrica, rota.ipSaida);
        }

        System.out.println("------------------------------------------------");
    }
}
