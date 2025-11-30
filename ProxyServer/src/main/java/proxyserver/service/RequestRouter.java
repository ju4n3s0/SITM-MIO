package proxyserver.service;

import citizen.dto.ObtenerInformacionParaCiudadano;
import citizen.dto.InformacionAlCiudadano;
import datacenter.service.DataCenterFacade;
import datacenter.service.IServicioDataCenter;
import proxyserver.cache.CacheManager;


public class RequestRouter implements IServicioCache {

    private final CacheManager cacheManager;
    private final IServicioDataCenter dataCenter;

    public RequestRouter() {
        this.cacheManager = new CacheManager();
        this.dataCenter = new DataCenterFacade();
    }

    @Override
    public InformacionAlCiudadano obtenerInformacionParaCiudadano(ObtenerInformacionParaCiudadano solicitud) {
        String key = solicitud.getOrigenId() + "-" + solicitud.getDestinoId();
        InformacionAlCiudadano info = cacheManager.get(key);
        if (info != null) {
            return info;
        }
        // Convertir el DTO del cliente al DTO del DataCenter
        datacenter.dto.ObtenerInformacionParaCiudadano dcSolicitud =
                new datacenter.dto.ObtenerInformacionParaCiudadano(
                        solicitud.getOrigenId(), solicitud.getDestinoId());
        // Consultar al DataCenter
        datacenter.dto.InformacionAlCiudadano dcInfo = dataCenter.obtenerInformacionParaCiudadano(dcSolicitud);
        if (dcInfo != null) {
            // Convertir el mensaje del DataCenter al DTO del cliente
            info = new InformacionAlCiudadano(dcInfo.getMensaje());
            cacheManager.put(key, info);
        }
        return info;
    }
}