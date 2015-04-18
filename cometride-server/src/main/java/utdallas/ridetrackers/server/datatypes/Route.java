package utdallas.ridetrackers.server.datatypes;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: mlautz
 */
public class Route {

    protected String color;
    protected String id;
    protected String name;
    protected String status;
    protected List<LatLng> waypoints;
    protected List<LatLng> safepoints;

    public Route() {}

    public Route(String color, String id, String name, String status, List<LatLng> waypoints, List<LatLng> safepoints) {
        this.color = color;
        this.id = id;
        this.name = name;
        this.status = status;
        this.waypoints = waypoints;
        this.safepoints = safepoints;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<LatLng> getWaypoints() {
        return waypoints;
    }

    public void setWaypoints(List<LatLng> waypoints) {
        this.waypoints = waypoints;
    }

    public List<LatLng> getSafepoints() {
        return safepoints;
    }

    public void setSafepoints(List<LatLng> safepoints) {
        this.safepoints = safepoints;
    }
}
