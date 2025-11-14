package ui;

import java.sql.SQLException;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import Graph.RouteGraph;
import Repository.StopRepository;
import db.DatabaseManager;
import model.Stop;

public class GraphViewer {

    public static void main(String[] args) {
        // 1. Construir el grafo desde la BD (lo mismo que hacías en TestConnection)
        DatabaseManager dbManager = new DatabaseManager();
        StopRepository stopRepo = new StopRepository(dbManager);
        RouteGraph graph = new RouteGraph();

        try {
            List<Stop> stops = stopRepo.findAll();
            for (Stop s : stops) {
                graph.addStop(s);
            }
            System.out.println("Grafo cargado con " + graph.size() + " paradas.");
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

            frame.setSize(800, 600);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}