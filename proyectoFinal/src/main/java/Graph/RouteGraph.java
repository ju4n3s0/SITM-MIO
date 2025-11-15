package Graph;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import model.Line;
import model.Stop;

public class RouteGraph {
    
    // Paradas
    private final Map<Long, Stop> stopsById = new HashMap<>();
    // Líneas
    private final Map<Long, Line> linesById = new HashMap<>();
    
    // ======== STOPS ========

    public void addStop(Stop stop) {
        stopsById.put(stop.getId(), stop);
    }

    public Stop getStop(long id) {
        return stopsById.get(id);
    }

    public Collection<Stop> getStops() {
        return stopsById.values();
    }

    public int stopCount() {
        return stopsById.size();
    }

    public int size() {
        return stopsById.size();
    }

    // ======== LINES ========

    public void addLine(Line line) {
        linesById.put(line.getId(), line);
    }

    public Line getLine(long id) {
        return linesById.get(id);
    }

    public Collection<Line> getLines() {
        return linesById.values();
    }

    public int lineCount() {
        return linesById.size();
    }

}
