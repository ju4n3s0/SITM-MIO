package ui;

import java.sql.SQLException;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import Graph.RouteGraph;
import Repository.LineRepository;
import Repository.LineStopRepository;
import Repository.StopRepository;
import db.DatabaseManager;
import model.Arc;
import model.Line;
import model.LineStop;
import model.Stop;

public class GraphViewer {

    public static void main(String[] args) {
        // 1. Construir el grafo desde la BD
        DatabaseManager dbManager = new DatabaseManager();
        StopRepository stopRepo = new StopRepository(dbManager);
        LineRepository lineRepo = new LineRepository(dbManager);
        LineStopRepository lineStopRepo = new LineStopRepository(dbManager);

        RouteGraph graph = new RouteGraph();

        try {
            // --- Paradas ---
            List<Stop> stops = stopRepo.findAll();
            for (Stop s : stops) {
                graph.addStop(s);
            }
            System.out.println("Grafo cargado con " + graph.stopCount() + " paradas.");

            // --- Líneas ---
            List<Line> lines = lineRepo.findAll();
            for (Line l : lines) {
                graph.addLine(l);
            }
            System.out.println("Grafo cargado con " + graph.lineCount() + " lineas.");

            // --- LineStops y Arcos ---
            List<LineStop> lineStops = lineStopRepo.findAll();

            LineStop prev = null;
            for (LineStop current : lineStops) {
                if (prev != null
                        && prev.getLineId() == current.getLineId()
                        && prev.getOrientation().equals(current.getOrientation())) {

                    // Son consecutivos en la MISMA línea y la MISMA orientación
                    Stop from = graph.getStop(prev.getStopId());
                    Stop to = graph.getStop(current.getStopId());
                    Line line = graph.getLine(current.getLineId());

                    if (from != null && to != null && line != null) {
                        graph.addArc(new Arc(from, to, line, current.getOrientation()));
                    }
                }
                prev = current;
            }

            System.out.println("Grafo cargado con " + graph.arcCount() + " arcos.");

        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }

        // 2. Crear ventana Swing en el hilo de eventos
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Grafo de paradas MIO");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            GraphPanel panel = new GraphPanel(graph);
            frame.add(panel);

            frame.setSize(1000, 700);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
