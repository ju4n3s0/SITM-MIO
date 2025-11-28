package ui;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import Graph.RouteGraph;
import Repository.Arc;
import Repository.LineRepository;
import Repository.LineStopRepository;
import Repository.StopRepository;
import db.DatabaseManager;
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
                        && prev.getLinevariant() == current.getLinevariant()
                        && prev.getOrientation() == current.getOrientation()
                        && current.getSequence() == prev.getSequence() + 1
                        ) {

                    // Son consecutivos en la MISMA línea y la MISMA orientación
                    Stop from = graph.getStop(prev.getStopId());
                    Stop to = graph.getStop(current.getStopId());
                    Line line = graph.getLine(current.getLineId());

                    if (from != null && to != null && line != null) {
                        graph.addArc(new Arc(
                            from, 
                            line, 
                            current.getOrientation(),
                            to,
                            current.getLinevariant(),
                            prev.getSequence()
                            ));
                    }
                }
                prev = current;
            }

            System.out.println("Grafo cargado con " + graph.arcCount() + " arcos.");

            // --- Imprimir en consola los arcos por ruta y orientación ---
            printArcsByLine(graph);

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

    
    private static void printArcsByLine(RouteGraph graph) {
        // Agrupar arcos por (lineId, orientación) en el orden en que se recorren
        Map<String, List<Arc>> grupos = new LinkedHashMap<>();

        for (Arc arc : graph.getArcs()) {
            String key = arc.getLine().getId() + "|" + arc.getOrientation();
            grupos.computeIfAbsent(key, k -> new ArrayList<>()).add(arc);
        }

        System.out.println();
        System.out.println("========== LISTA DE ARCOS POR RUTA Y ORIENTACION ==========");

        for (List<Arc> arcs : grupos.values()) {
            if (arcs.isEmpty()) continue;

            arcs.sort(Comparator.comparingInt(Arc::getFromSequence));
            
            Arc firstArc = arcs.get(0);
            Line line = arcs.get(0).getLine();
            int variant = firstArc.getLinevariant();
            int orientation = arcs.get(0).getOrientation();
            String orientationLabel = orientationLabel(orientation);

            System.out.println("---------------------------------------------------------");
            System.out.printf("Linea %d (%s) - Variante %d - %s%n",
                    line.getId(), line.getShortName(), variant, orientationLabel);
            System.out.println("Arcos (origen -> destino):");

            for (Arc arc : arcs) {
                Stop from = arc.getFrom();
                Stop to = arc.getTo();

                System.out.printf("  %d -> %d%n",
                        from.getId(),
                        to.getId());
            }
            System.out.println();
        }

        System.out.println("===========================================================");
    }

    private static String orientationLabel(int orientation) {
        if (orientation == 0) return "IDA";
        if (orientation == 1) return "REGRESO";
        return String.valueOf(orientation);
    }
}
