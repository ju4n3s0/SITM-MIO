package citizen.service;

import citizen.dto.InformacionAlCiudadano;
import citizen.dto.ObtenerInformacionParaCiudadano;
import citizen.cache.ClienteCache;


public class ServicioTiempoViaje {
    private final ClienteCache cache;

    public ServicioTiempoViaje(ClienteCache cache) {
        this.cache = cache;
    }

    
    public InformacionAlCiudadano consultar(ObtenerInformacionParaCiudadano solicitud) {
        return cache.consultar(solicitud);
    }
}