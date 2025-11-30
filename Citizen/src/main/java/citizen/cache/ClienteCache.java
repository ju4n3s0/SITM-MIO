package citizen.cache;

import java.util.HashMap;
import java.util.Map;

import proxyserver.service.RequestRouter;

import citizen.dto.InformacionAlCiudadano;
import citizen.dto.ObtenerInformacionParaCiudadano;
import citizen.service.ServicioDetenerQuery;
import proxyserver.service.IServicioCache;


public class ClienteCache {

    
    private final Map<String, InformacionAlCiudadano> cache = new HashMap<>();

    /** Servicio remoto de cache al que se delegan las consultas. */
    private final IServicioCache proxyService;

    /** Servicio de cancelación utilizado para detener consultas. */
    private final ServicioDetenerQuery servicioDetener;

    public ClienteCache(IServicioCache proxyService, ServicioDetenerQuery servicioDetener) {
        this.proxyService = proxyService;
        this.servicioDetener = servicioDetener;
    }

    
    public InformacionAlCiudadano consultar(ObtenerInformacionParaCiudadano solicitud) {
        String key = solicitud.getOrigenId() + "-" + solicitud.getDestinoId();
        InformacionAlCiudadano info = cache.get(key);
        if (info == null) {
            info = proxyService.obtenerInformacionParaCiudadano(solicitud);
            if (info != null) {
                cache.put(key, info);
            }
        }
        return info;
    }

    
    public void cancelar(String solicitudKey) {
        servicioDetener.detenerConsulta(solicitudKey);
    }
}