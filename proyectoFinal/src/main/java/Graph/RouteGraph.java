package Graph;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import model.Stop;

public class RouteGraph {
    
    private final Map<Long, Stop> stopsById = new HashMap<>();

    public void addStop(Stop stop) {
        stopsById.put(stop.getId(), stop);
    }

    public Stop getStop(long id) {
        return stopsById.get(id);
    }

    public Collection<Stop> getStops() {
        return stopsById.values();
    }

    public int size() {
        return stopsById.size();
    }
}