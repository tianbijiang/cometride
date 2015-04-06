package utdallas.ridetrackers.server.datatypes.driver;

/**
 * Created with IntelliJ IDEA.
 * User: mlautz
 */
public class TallyUpdate {

    private String driverId;
    private int passengerCount;
    private int passengerTotal;

    // TODO: Driver ID set by session info somehow?

    public TallyUpdate() {}

    public TallyUpdate(String driverId, int passengerCount, int passengerTotal) {
        this.driverId = driverId;
        this.passengerCount = passengerCount;
        this.passengerTotal = passengerTotal;
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public int getPassengerCount() {
        return passengerCount;
    }

    public void setPassengerCount(int passengerCount) {
        this.passengerCount = passengerCount;
    }

    public int getPassengerTotal() {
        return passengerTotal;
    }

    public void setPassengerTotal(int passengerTotal) {
        this.passengerTotal = passengerTotal;
    }
}
