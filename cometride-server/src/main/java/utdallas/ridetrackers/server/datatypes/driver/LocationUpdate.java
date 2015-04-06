package utdallas.ridetrackers.server.datatypes.driver;

import java.sql.Timestamp;

/**
 * Created with IntelliJ IDEA.
 * User: mlautz
 */
public class LocationUpdate {

    private String driverId;
    private double lat;
    private double lng;
    private Timestamp timestamp;

    // TODO: Driver ID set by session info somehow?
    // TODO: Document that timestamps are set by server


    public LocationUpdate() {}

    public LocationUpdate(String driverId, double lat, double lng, Timestamp timestamp) {
        this.driverId = driverId;
        this.lat = lat;
        this.lng = lng;
        this.timestamp = timestamp;
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}