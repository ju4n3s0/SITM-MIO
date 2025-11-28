package citizen.service;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Repository.StopRepository;
import citizen.utils.GeoUtils;
import db.DatabaseManager;
import model.Stop;


public class ReceptorDeInformacionSistemaCiudadano {

    
    private static final double VELOCIDAD_PROMEDIO_KMH = 20.0;

    private final Map<Long, Stop> stopsById = new HashMap<>();

    
    public ReceptorDeInformacionSistemaCiudadano() {
        DatabaseManager dbManager = new DatabaseManager();
        StopRepository stopRepo = new StopRepository(dbManager);
        try {
            List<Stop> stops = stopRepo.findAll();
            for (Stop s : stops) {
                stopsById.put(s.getId(), s);
            }
        } catch (SQLException e) {
            
            e.printStackTrace();
        }
    }

    
    public String obtenerInformacionParaCiudadano(long idOrigen, long idDestino) {
        Stop origen = stopsById.get(idOrigen);
        Stop destino = stopsById.get(idDestino);
        if (origen == null || destino == null) {
            return "No se encontraron una o ambas paradas especificadas. Por favor revise los identificadores.";
        }
        // Calcular distancia geodésica
        double distanciaKm = GeoUtils.distanceKm(origen.getLatitude(), origen.getLongitude(), destino.getLatitude(), destino.getLongitude());
        // Tiempo en horas = distancia / velocidad
        double tiempoHoras = distanciaKm / VELOCIDAD_PROMEDIO_KMH;
        double tiempoMinutos = tiempoHoras * 60.0;
        // Redondear a un decimal
        double tiempoRedondeado = Math.round(tiempoMinutos * 10.0) / 10.0;
        return String.format("El tiempo promedio estimado entre las paradas %d y %d es de %.1f minutos.",
                idOrigen, idDestino, tiempoRedondeado);
    }
}