package datacenter.service;

import datacenter.dto.InformacionAlCiudadano;
import datacenter.dto.ObtenerInformacionParaCiudadano;
import datacenter.dto.RespuestaConsulta;
import datacenter.formatter.IRespuestaCiudadanoFormatter;
import datacenter.formatter.RespuestaCiudadanoFormatterImpl;
import datacenter.model.Stop;
import datacenter.repository.StopRepository;
import datacenter.utils.GeoUtils;

import java.sql.SQLException;


public class DataCenterFacade implements IServicioDataCenter {

    private static final double VELOCIDAD_KMH = 20.0;

    private final StopRepository stopRepository;
    private final IRespuestaCiudadanoFormatter formatter;

    public DataCenterFacade() {
        this.stopRepository = new StopRepository();
        this.formatter = new RespuestaCiudadanoFormatterImpl();
    }

    @Override
    public InformacionAlCiudadano obtenerInformacionParaCiudadano(ObtenerInformacionParaCiudadano solicitud) {
        long origenId = solicitud.getOrigenId();
        long destinoId = solicitud.getDestinoId();
        try {
            Stop origen = stopRepository.findById(origenId);
            Stop destino = stopRepository.findById(destinoId);
            if (origen == null || destino == null) {
                return new InformacionAlCiudadano(
                        "No se encontraron datos para alguna de las paradas especificadas: " + origenId + " → " + destinoId);
            }
            double distancia = GeoUtils.distanceKm(
                    origen.getLatitude(), origen.getLongitude(),
                    destino.getLatitude(), destino.getLongitude());
            double horas = distancia / VELOCIDAD_KMH;
            double minutos = horas * 60.0;
            RespuestaConsulta resp = new RespuestaConsulta(origenId, destinoId, distancia, minutos);
            return formatter.formatearRespuesta(resp);
        } catch (SQLException e) {
            // En caso de error con la base de datos se devuelve un mensaje genérico
            return new InformacionAlCiudadano("Ocurrió un error al procesar la consulta: " + e.getMessage());
        }
    }
}