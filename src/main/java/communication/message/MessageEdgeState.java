package communication.message;

import simulation.CongestionState;

public class MessageEdgeState {

    String edge;
    CongestionState congestionState;

    public String getEdge() {
        return edge;
    }

    public void setEdge(String edge) {
        this.edge = edge;
    }

    public CongestionState getCongestionState() {
        return congestionState;
    }

    public void setCongestionState(CongestionState congestionState) {
        this.congestionState = congestionState;
    }

    public MessageEdgeState(){}

    public MessageEdgeState(String edge, CongestionState congestionState) {
        this.edge = edge;
        this.congestionState = congestionState;
    }
}
