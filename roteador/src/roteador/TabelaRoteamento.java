package roteador;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TabelaRoteamento {

    private Map<String,String> tabela_aprendizado;
    private Map<String, Rota> tabela_de_vizinhos;
    private Map<String, Rota> tabela;
    private String ip_host;

    public TabelaRoteamento(String ip, InetAddress[] vizinhos) {
        tabela_aprendizado = new HashMap<>();
        tabela = new HashMap<>();
        tabela_de_vizinhos = new HashMap<>();
        ip_host = ip.substring(1);
        for(InetAddress v : vizinhos){
            String ipvizinho = v.toString().substring(1);
            Rota r = new Rota(1,ipvizinho);
            tabela.put(ipvizinho,r);
            tabela_de_vizinhos.put(ipvizinho,r);
        }
    }

    public void zera_e_adiciona_vizinhos(){
        tabela = new HashMap<>();
        for (String s : tabela_de_vizinhos.keySet()){
            tabela.put(s,tabela_de_vizinhos.get(s));
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

                if(!ipDestino.equals(ip_host)) {

                    if (tabela.containsKey(ipDestino)) {

                        Rota rotaExistente = tabela.get(ipDestino);
                        if (metrica < rotaExistente.metrica) {
                            rotaExistente.metrica = metrica + 1;
                            rotaExistente.ipSaida = ipSaida;
                            System.out.println("Rota atualizada: " + ipDestino + " Métrica: " + metrica + " IP de Saída: " + ipSaida);
                            tabela_aprendizado.remove(ipSaida);
                            tabela_aprendizado.put(ipSaida,ipDestino);
                        }

                    } else {
                        boolean is_vizinho = false;
                        if(tabela_de_vizinhos.containsKey(ipDestino))
                            is_vizinho = true;

                        if(!is_vizinho){
                            metrica++;
                            if(!contains_value_aprendizado(ipDestino))
                                tabela_aprendizado.put(ipSaida,ipDestino);
                        }

                        //System.out.println("TABLELA GET IPDESTINO: "+ ipDestino +"->" +tabela_aprendizado.get(ipDestino));
                        //System.out.println("CONTAINS: "+tabela_aprendizado.get(ipDestino).equals(ipAddress.getHostAddress()));

                        tabela.put(ipDestino, new Rota(metrica, ipSaida));

                        //recebeu uma rota de loop
                        if(!ip_aprendido(ipAddress.getHostAddress())){
                            tabela.remove(ipDestino);
                        }

                        System.out.println("Nova rota adicionada: " + ipDestino + " Métrica: " + metrica + " IP de Saída: " + ipSaida);
                    }

                }
            }
        }

        imprimirTabela();
    }

    public boolean ip_aprendido(String value){
        for (String s:tabela_aprendizado.values()){
            if (s == value)
                return true;
        }
        return false;
    }
    public boolean contains_value_aprendizado(String value){
        for (String s:tabela_aprendizado.keySet()){
            if (tabela_aprendizado.get(s) == value)
                return true;
        }
        return false;
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
