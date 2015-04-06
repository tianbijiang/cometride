package utdallas.ridetrackers.server.datatypes;

/**
 * Created with IntelliJ IDEA.
 * User: mlautz
 */
public class CabStatus {

    private String cabId;
    private LatLng location;
    private Integer maxCapacity;
    private String name;
    private Integer passengerCount;
    private String status;

    public CabStatus() {}

    public CabStatus( String cabId, LatLng location, Integer maxCapacity, Integer passengerCount, String status) {
        this.location = location;
        this.maxCapacity = maxCapacity;
        this.passengerCount = passengerCount;
        this.status = status;
    }

    public LatLng getLocation() {
        return location;
    }

    public void setLocation(LatLng location) {
        this.location = location;
    }

    public Integer getMaxCapacity() {
        return maxCapacity;
    }

    public void setMaxCapacity(int maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    public Integer getPassengerCount() {
        return passengerCount;
    }

    public void setPassengerCount(int passengerCount) {
        this.passengerCount = passengerCount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
