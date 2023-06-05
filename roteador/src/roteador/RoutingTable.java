package roteador;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

public class RoutingTable {

    private final Map<String, String> learningTable;
    private final Map<String, Route> neighborsTable;
    private Map<String, Route> table;
    private final String ipHost;

    public RoutingTable(String ip, InetAddress[] neighbors) {
        learningTable = new HashMap<>();
        table = new HashMap<>();
        neighborsTable = new HashMap<>();
        ipHost = ip.substring(1);

        for (InetAddress neighbor : neighbors) {
            String ipNeighbor = neighbor.toString().substring(1);
            Route route = new Route(1, ipNeighbor);
            table.put(ipNeighbor, route);
            neighborsTable.put(ipNeighbor, route);
        }
    }

    public void resetAndAddNeighbors() {
        table = new HashMap<>();

        for (String s : neighborsTable.keySet()) {
            table.put(s, neighborsTable.get(s));
        }
    }

    public synchronized void updateTable(String message, InetAddress ipAddress) {
        System.out.println("\nMESSAGE HOST IP: " + ipAddress.getHostAddress() + ", RECEIVED MESSAGE: " + message);
        String[] routes = message.split("\\*");

        for (String route : routes) {
            String[] fields = route.split(";");

            if (fields.length == 2) {
                String destinationIp = fields[0];
                int metric = Integer.parseInt(fields[1]);
                String outgoingIp = ipAddress.getHostAddress();

                if (!destinationIp.equals(ipHost)) {
                    if (table.containsKey(destinationIp)) {
                        Route existingRoute = table.get(destinationIp);

                        if (metric < existingRoute.metric) {
                            existingRoute.metric = metric + 1;
                            existingRoute.outgoingIp = outgoingIp;
                            System.out.println("\nUPDATED ROUTE: " + destinationIp + ", METRIC: " + metric + ", OUTGOING IP: " + outgoingIp);
                            learningTable.remove(destinationIp);
                            learningTable.put(destinationIp, outgoingIp);
                        }
                    } else {
                        boolean isNeighbor = neighborsTable.containsKey(destinationIp);

                        if (!isNeighbor) {
                            metric++;
                            if (!learningTable.containsKey(destinationIp))
                                learningTable.put(destinationIp, outgoingIp);
                        }

                        table.put(destinationIp, new Route(metric, outgoingIp));

                        if (!learningTable.get(destinationIp).equals(ipAddress.getHostAddress())) {
                            table.remove(destinationIp);
                        }

                        System.out.println("NEW ADD ROUTE: " + destinationIp + ", METRIC: " + metric + ", OUTGOING IP: " + outgoingIp);
                    }
                }
            }
        }

        printTable();
    }

    public void addNeighborRoute(Route route) {
        table.put(route.outgoingIp, route);
    }

    public synchronized String getTableString() {
        StringBuilder tableString = new StringBuilder();

        for (Map.Entry<String, Route> entry : table.entrySet()) {
            String destinationIp = entry.getKey();
            Route route = entry.getValue();
            tableString.append("*").append(destinationIp).append(";").append(route.metric);
        }

        return tableString.toString();
    }

    public synchronized void printTable() {
        System.out.println("\n\n                Routing Table");
        System.out.println("------------------------------------------------");
        System.out.println("|  Destination IP  | Metric |    Outgoing IP   |");
        System.out.println("------------------------------------------------");

        for (Map.Entry<String, Route> entry : table.entrySet()) {
            String destinationIp = entry.getKey();
            Route route = entry.getValue();
            System.out.printf("| %1$-16s | %2$-6d | %3$-16s |\n", destinationIp, route.metric, route.outgoingIp);
        }

        System.out.println("------------------------------------------------");
    }
}
