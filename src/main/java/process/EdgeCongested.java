package process;

import simulation.CongestionState;

public class EdgeCongested {

    private CongestionState congestionState;
    private double weight;
    private int beginTime;
    private int endTime;

    public EdgeCongested(){
        this.congestionState = CongestionState.SLIGHT;
        this.weight = 0;
        this.beginTime = -1;
        this.endTime = -1;
    }

    public EdgeCongested(CongestionState congestionState, double weight, int beginTime, int endTime) {
        this.congestionState = congestionState;
        this.weight = weight;
        this.beginTime = beginTime;
        this.endTime = endTime;
    }

    public CongestionState getCongestionState() {
        return congestionState;
    }

    public void setCongestionState(CongestionState congestionState) {
        this.congestionState = congestionState;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public int getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(int beginTime) {
        this.beginTime = beginTime;
    }

    public int getEndTime() {
        return endTime;
    }

    public void setEndTime(int endTime) {
        this.endTime = endTime;
    }
}
