package utdallas.ridetrackers.server.datatypes.admin;

import utdallas.ridetrackers.server.datatypes.LatLng;
import utdallas.ridetrackers.server.datatypes.Route;
import utdallas.ridetrackers.server.datatypes.TimeRange;

import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: mlautz
 */
public class RouteDetails extends Route {

    // TODO: Have Route Details Contain Scheduling Information
    private List<String> days;
    private List<TimeRange> times;
    private Date startDate;
    private Date endDate;

    public RouteDetails() {}

    public RouteDetails(String color, String id, String name, String shortName, String status,
                        List<LatLng> waypoints, List<LatLng> safepoints, List<String> days,
                        List<TimeRange> times, Date startDate, Date endDate) {
        super(color, id, name, shortName, status, waypoints, safepoints);
        this.days = days;
        this.times = times;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public List<String> getDays() {
        return days;
    }

    public void setDays(List<String> days) {
        this.days = days;
    }

    public List<TimeRange> getTimes() {
        return times;
    }

    public void setTimes(List<TimeRange> times) {
        this.times = times;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
}
