package utdallas.ridetrackers.server.datatypes.driver;

/**
 * Created with IntelliJ IDEA.
 * User: mlautz
 */
public class DriverStatus {

    private String driverId;
    private String dutyStatus;
    private String routeId;
    private int maxCapacity;

    // TODO: Driver ID set by session info somehow?

    // TODO: Should status updates be through a separate object?


    public DriverStatus() {}

    public DriverStatus(String driverId, String dutyStatus, String routeId, int maxCapacity) {
        this.driverId = driverId;
        this.dutyStatus = dutyStatus;
        this.routeId = routeId;
        this.maxCapacity = maxCapacity;
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public String getDutyStatus() {
        return dutyStatus;
    }

    public void setDutyStatus(String dutyStatus) {
        this.dutyStatus = dutyStatus;
    }

    public String getRouteId() {
        return routeId;
    }

    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }

    public int getMaxCapacity() {
        return maxCapacity;
    }

    public void setMaxCapacity(int maxCapacity) {
        this.maxCapacity = maxCapacity;
    }
}
