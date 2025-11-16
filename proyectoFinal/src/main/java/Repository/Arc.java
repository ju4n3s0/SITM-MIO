package Repository;

import model.Line;
import model.Stop;

/**
 * Arco del grafo: conecta dos paradas consecutivas para una misma línea
 * y una orientación (ida / regreso).
 */
public class Arc {

    private final Stop from;
    private final Stop to;
    private final Line line;
    private final String orientation; // para saber si es ida o vuelta

    public Arc(Stop from, Line line, String orientation, Stop to) {
        this.from = from;
        this.line = line;
        this.orientation = orientation;
        this.to = to;
    }

    public Stop getFrom() {
        return from;
    }

    public Stop getTo() {
        return to;
    }

    public Line getLine() {
        return line;
    }

    public String getOrientation() {
        return orientation;
    }

}
