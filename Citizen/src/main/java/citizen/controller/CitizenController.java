package citizen.controller;

import citizen.cache.ClienteCache;
import citizen.dto.InformacionAlCiudadano;
import citizen.dto.ObtenerInformacionParaCiudadano;
import citizen.service.ServicioTiempoViaje;
import citizen.service.ServicioDetenerQuery;
import citizen.ui.UiCiudadano;
import proxyserver.service.IServicioCache;
import proxyserver.service.RequestRouter;


public class CitizenController {

    private final UiCiudadano view;
    private final ClienteCache cache;
    private final ServicioTiempoViaje servicio;
    private final ServicioDetenerQuery servicioDetener;

    public CitizenController(UiCiudadano view) {
        this.view = view;
        
        this.servicioDetener = new ServicioDetenerQuery();
        IServicioCache proxy = new RequestRouter();
        this.cache = new ClienteCache(proxy, servicioDetener);
        this.servicio = new ServicioTiempoViaje(cache);
    }

    
    public void consultar(long origenId, long destinoId) {
        ObtenerInformacionParaCiudadano solicitud = new ObtenerInformacionParaCiudadano(origenId, destinoId);
        InformacionAlCiudadano info = servicio.consultar(solicitud);
        if (info == null) {
            view.mostrarInformacionAlCiudadano("No se pudo obtener información para las paradas " + origenId + " → " + destinoId);
        } else {
            view.mostrarInformacionAlCiudadano(info.getMensaje());
        }
    }

    
    public void detenerConsulta(String solicitudKey) {
        cache.cancelar(solicitudKey);
        view.mostrarInformacionAlCiudadano("Se ha solicitado la cancelación de la consulta " + solicitudKey);
    }
}