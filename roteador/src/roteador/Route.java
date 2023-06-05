package roteador;

public class Route {

    int metric;
    String outgoingIp;

    public Route(int metric, String outgoingIp) {
        this.metric = metric;
        this.outgoingIp = outgoingIp;
    }
}
