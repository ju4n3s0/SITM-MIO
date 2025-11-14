package ui;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.Collection;

import javax.swing.JPanel;

import Graph.RouteGraph;
import model.Stop;

public class GraphPanel extends JPanel {

    private final RouteGraph graph;

    // límites geográficos
    private double minLat, maxLat, minLon, maxLon;

    public GraphPanel(RouteGraph graph) {
        this.graph = graph;
        computeBounds();
    }

    private void computeBounds() {
        Collection<Stop> stops = graph.getStops();
        boolean first = true;

        for (Stop s : stops) {
            double lat = s.getLatitude();
            double lon = s.getLongitude();

            if (first) {
                minLat = maxLat = lat;
                minLon = maxLon = lon;
                first = false;
            } else {
                if (lat < minLat) minLat = lat;
                if (lat > maxLat) maxLat = lat;
                if (lon < minLon) minLon = lon;
                if (lon > maxLon) maxLon = lon;
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (graph.size() == 0) return;

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();
        int padding = 20;

        for (Stop s : graph.getStops()) {
            int x = lonToX(s.getLongitude(), w, padding);
            int y = latToY(s.getLatitude(), h, padding);

            int r = 4; // radio del puntico
            g2.fillOval(x - r, y - r, 2 * r, 2 * r);
        }
    }

    private int lonToX(double lon, int width, int padding) {
        double norm = (lon - minLon) / (maxLon - minLon); // 0..1
        return padding + (int) (norm * (width - 2 * padding));
    }

    private int latToY(double lat, int height, int padding) {
        // invertimos latitud para que norte quede arriba
        double norm = (lat - minLat) / (maxLat - minLat); // 0..1
        double inv = 1.0 - norm;
        return padding + (int) (inv * (height - 2 * padding));
    }
}