package proxyserver.service;

import citizen.dto.InformacionAlCiudadano;
import citizen.dto.ObtenerInformacionParaCiudadano;


public class RequestRouter implements IServicioCache {

    @Override
    public InformacionAlCiudadano obtenerInformacionParaCiudadano(ObtenerInformacionParaCiudadano solicitud) {
        String mensaje = String.format(
                "Simulación de ProxyServer: tiempo promedio de viaje entre %s y %s aún no está " +
                "conectado al DataCenter en esta versión de prueba.",
                solicitud.getOrigenId(),
                solicitud.getDestinoId()
        );

        return new InformacionAlCiudadano(mensaje);
    }
}
