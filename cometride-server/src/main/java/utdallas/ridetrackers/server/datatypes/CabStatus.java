package utdallas.ridetrackers.server.datatypes;

/**
 * Created with IntelliJ IDEA.
 * User: mlautz
 */
public class CabStatus {

    private String cabId;
    private LatLng location;
    private Integer maxCapacity;
    private Integer passengerCount;
    private String routeId;
    private String status;

    public CabStatus() {}

    public CabStatus( String cabId, LatLng location, Integer maxCapacity,
                      Integer passengerCount, String routeId, String status) {
        this.cabId = cabId;
        this.location = location;
        this.maxCapacity = maxCapacity;
        this.passengerCount = passengerCount;
        this.routeId = routeId;
        this.status = status;
    }

    public String getCabId() {
        return cabId;
    }

    public void setCabId(String cabId) {
        this.cabId = cabId;
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

    public void setMaxCapacity(Integer maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    public Integer getPassengerCount() {
        return passengerCount;
    }

    public void setPassengerCount(Integer passengerCount) {
        this.passengerCount = passengerCount;
    }

    public String getRouteId() {
        return routeId;
    }

    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
