package utdallas.ridetrackers.server.datatypes.admin;

import utdallas.ridetrackers.server.datatypes.LatLng;
import utdallas.ridetrackers.server.datatypes.Route;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: mlautz
 */
public class RouteDetails extends Route {

    // TODO: Have Route Details Contain Scheduling Information

    // TODO: Represent route active times with TimeWindow table that has keys back to routes
    // TODO: Query against start and end times to find active routes

    public RouteDetails() {}

    public RouteDetails(String color, String id, String name, String status, List<LatLng> waypoints) {
        super(color, id, name, status, waypoints);
    }
}
