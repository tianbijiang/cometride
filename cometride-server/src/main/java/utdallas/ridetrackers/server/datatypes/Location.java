package utdallas.ridetrackers.server.datatypes;

import java.sql.Timestamp;

/**
 * Created with IntelliJ IDEA.
 * User: mlautz
 * Date: 3/5/15
 * Time: 5:43 PM
 * To change this template use File | Settings | File Templates.
 */
public class Location {

    private int id;
    private String cabId;
    private String lat;
    private String lon;
    private Timestamp timeStamp;

    public Location(){}

    public Location( String cabId, String lat, String lon, Timestamp timeStamp, int id ) {
        this.cabId = cabId;
        this.lat = lat;
        this.lon = lon;
        this.timeStamp = timeStamp;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCabId() {
        return cabId;
    }

    public void setCabId(String cabId) {
        this.cabId = cabId;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public Timestamp getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Timestamp timeStamp) {
        this.timeStamp = timeStamp;
    }

    @Override
    public String toString() {
        return new StringBuilder().
                append( "Cab Id: " ).append( cabId ).
                append( "Lat: " ).append( lat ).
                append( "Lon: " ).append( lon ).
                append( "Timestamp: " ).append( timeStamp ).
                toString();
    }

}
