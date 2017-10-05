package simulation;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum CongestionState {
    @JsonProperty("normal")
    NORMAL(0),
    @JsonProperty("slight")
    SLIGHT(1),
    @JsonProperty("mild")
    MILD(2),
    @JsonProperty("moderate")
    MODERATE(3),
    @JsonProperty("severe")
    SEVERE(4);

    private int state;

    CongestionState(int state) {
        this.state = state;
    }

    public int getState() {
        return state;
    }

    public static CongestionState getStateByOccupancy(double occupancy) {
        if (occupancy < 40) {
            return CongestionState.NORMAL;
        } else if (occupancy > 40 && occupancy < 55) {
            return CongestionState.SLIGHT;
        } else if (occupancy > 55 && occupancy < 70) {
            return CongestionState.MILD;
        }else if (occupancy > 70 && occupancy < 85) {
            return CongestionState.MODERATE;
        }else {
            return CongestionState.SEVERE;
        }
    }
}
