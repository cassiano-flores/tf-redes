package roteador;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TabelaRoteamento {

    private Map<String, Rota> tabela_de_vizinhos;
    private Map<String, Rota> tabela;
    private String ip_host;

    public TabelaRoteamento(String ip, InetAddress[] vizinhos) {
        tabela = new HashMap<>();
        tabela_de_vizinhos = new HashMap<>();
        ip_host = ip;
        for(InetAddress v : vizinhos){
            String ipvizinho = v.toString().substring(1);
            Rota r = new Rota(1,ipvizinho);
            tabela.put(ipvizinho,r);
            tabela_de_vizinhos.put(ipvizinho,r);
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
                //System.out.println("IPHOST: "+ip_host+", ipdestino: "+ipDestino);

                if(!ip_host.equals(ipDestino)) {

                    if (tabela.containsKey(ipDestino)) {

                        Rota rotaExistente = tabela.get(ipDestino);
                        if (metrica < rotaExistente.metrica) {
                            rotaExistente.metrica = metrica;
                            rotaExistente.ipSaida = ipSaida;
                            System.out.println("Rota atualizada: " + ipDestino + " Métrica: " + metrica + " IP de Saída: " + ipSaida);
                        }

                    } else {
                        boolean is_vizinho = false;
                        if(tabela_de_vizinhos.containsKey(ipDestino))
                            is_vizinho = true;

                        if(!is_vizinho)
                            metrica++;

                        tabela.put(ipDestino, new Rota(metrica, ipSaida));
                        System.out.println("Nova rota adicionada: " + ipDestino + " Métrica: " + metrica + " IP de Saída: " + ipSaida);
                    }

                }
            }
        }

        imprimirTabela();
    }

    public void add_rota_vizinho(Rota rota){
        tabela.put(rota.ipSaida,rota);
    }
    public synchronized void removerRota(String ipDestino) {
        if (tabela.containsKey(ipDestino)) {
            tabela.remove(ipDestino);
            System.out.println("Rota removida: " + ipDestino);
        }

        imprimirTabela();
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
        System.out.println("Tabela de Roteamento:");
        System.out.println("IP Destino\tMétrica\tIP de Saída");

        for (Map.Entry<String, Rota> entry : tabela.entrySet()) {
            String ipDestino = entry.getKey();
            Rota rota = entry.getValue();
            System.out.println(ipDestino + "\t\t" + rota.metrica + "\t\t" + rota.ipSaida);
        }

        System.out.println();
    }

}
