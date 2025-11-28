package db;
import java.sql.Connection;
import java.util.List;

import Graph.RouteGraph;
import Repository.StopRepository;
import model.Stop;

public class TestConnection {

    public static void main(String[] args) {

        DatabaseManager dbManager = new DatabaseManager();
        StopRepository stopRepo = new StopRepository(dbManager);
        RouteGraph graph = new RouteGraph();

        try (Connection conn = DatabaseManager.getConnection()) {
            List<Stop> stops = stopRepo.findAll();
            System.out.println("Se leyeron " + stops.size() + " paradas desde la BD");

            // 2. Agregarlas al grafo
            for (Stop s : stops) {
                graph.addStop(s);
            }

            System.out.println("Grafo construido con " + graph.size() + " nodos (paradas)");

            // 3. Imprimir algunas paradas de ejemplo
            System.out.println("Algunas paradas:");
            graph.getStops().stream()
                    .limit(5)
                    .forEach(System.out::println);
        } catch (Exception e) {
            System.out.println("Error conectando");
            e.printStackTrace();
        }
    }
}
