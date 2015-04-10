package utdallas.ridetrackers.server.datatypes;

import org.omg.CORBA.TRANSACTION_MODE;

/**
 * Created by matt lautz on 4/9/2015.
 */
public class TimeRange {

    private String start;
    private String end;

    public TimeRange() {}

    public TimeRange(String start, String end) {
        this.start = start;
        this.end = end;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }
}
