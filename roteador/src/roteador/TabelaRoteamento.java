package roteador;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

public class TabelaRoteamento {

    private Map<String, Rota> tabela;

    public TabelaRoteamento() {
        tabela = new HashMap<>();
    }

    public synchronized void updateTabela(String tabelaS, InetAddress ipAddress) {
        System.out.println(ipAddress.getHostAddress() + ": " + tabelaS);
        String[] rotas = tabelaS.split("\\*");

        for (String rota : rotas) {
            String[] campos = rota.split(";");
            if (campos.length == 2) {
                String ipDestino = campos[0];
                int metrica = Integer.parseInt(campos[1]);
                String ipSaida = ipAddress.getHostAddress();

                if (tabela.containsKey(ipDestino)) {
                    Rota rotaExistente = tabela.get(ipDestino);
                    if (metrica < rotaExistente.metrica) {
                        rotaExistente.metrica = metrica;
                        rotaExistente.ipSaida = ipSaida;
                        System.out.println("Rota atualizada: " + ipDestino + " Métrica: " + metrica + " IP de Saída: " + ipSaida);
                    }
                } else {
                    tabela.put(ipDestino, new Rota(metrica, ipSaida));
                    System.out.println("Nova rota adicionada: " + ipDestino + " Métrica: " + metrica + " IP de Saída: " + ipSaida);
                }
            }
        }
    }

    public synchronized void removerRota(String ipDestino) {
        if (tabela.containsKey(ipDestino)) {
            tabela.remove(ipDestino);
            System.out.println("Rota removida: " + ipDestino);
        }
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

    private class Rota {
        private int metrica;
        private String ipSaida;

        public Rota(int metrica, String ipSaida) {
            this.metrica = metrica;
            this.ipSaida = ipSaida;
        }
    }
}
