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
    private final int linevariant; // para saber la variante de la línea
    private final int orientation; // para saber si es ida o vuelta
    private final int fromSequence; // secuencia de la parada "from"

    public Arc(Stop from, Line line, int orientation, Stop to, int linevariant, int fromSequence) {
        this.from = from;
        this.line = line;
        this.orientation = orientation;
        this.to = to;
        this.linevariant = linevariant;
        this.fromSequence = fromSequence;
    }

    public int getFromSequence() {
        return fromSequence;
    }

    public int getLinevariant() {
        return linevariant;
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

    public int getOrientation() {
        return orientation;
    }

}
