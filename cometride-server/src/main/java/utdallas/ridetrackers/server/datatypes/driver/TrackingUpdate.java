package utdallas.ridetrackers.server.datatypes.driver;

import java.sql.Timestamp;

/**
 * Created with IntelliJ IDEA.
 * User: mlautz
 */
public class TrackingUpdate {

    private String cabSessionId;
    private Double lat;
    private Double lng;
    private int passengerCount;
    private int passengersAdded;
    private Timestamp timestamp;

    public TrackingUpdate() {}

    public TrackingUpdate(String cabSessionId,
                          Double lat,
                          Double lng,
                          int passengerCount,
                          int passengersAdded,
                          Timestamp timestamp) {

        this.cabSessionId = cabSessionId;
        this.lat = lat;
        this.lng = lng;
        this.passengerCount = passengerCount;
        this.passengersAdded = passengersAdded;
        this.timestamp = timestamp;
    }

    public String getCabSessionId() {
        return cabSessionId;
    }

    public void setCabSessionId(String cabSessionId) {
        this.cabSessionId = cabSessionId;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public int getPassengerCount() {
        return passengerCount;
    }

    public void setPassengerCount(int passengerCount) {
        this.passengerCount = passengerCount;
    }

    public int getPassengersAdded() {
        return passengersAdded;
    }

    public void setPassengersAdded(int passengersAdded) {
        this.passengersAdded = passengersAdded;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
