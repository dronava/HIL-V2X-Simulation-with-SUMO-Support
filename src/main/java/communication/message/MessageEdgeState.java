package communication.message;

public class MessageEdgeState {

    String edge;
    double state;

    public double getState() {
        return state;
    }

    public void setState(double state) {
        this.state = state;
    }

    public String getEdge() {
        return edge;
    }

    public void setEdge(String edge) {
        this.edge = edge;
    }


    public MessageEdgeState(String edge, double state) {
        this.edge = edge;
        this.state = state;
    }

    public MessageEdgeState() {
    }
}
