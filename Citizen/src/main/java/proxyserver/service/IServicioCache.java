package proxyserver.service;

import citizen.dto.InformacionAlCiudadano;
import citizen.dto.ObtenerInformacionParaCiudadano;

/**
 * Stub de la interfaz del servicio de cache del ProxyServer.
 * Solo se usa para que el módulo Citizen pueda compilar y probar
 * la UI de Sistema Ciudadano de forma aislada.
 */
public interface IServicioCache {

    /**
     * Realiza la consulta de información de viaje para el ciudadano.
     * En una implementación real, esto haría una llamada remota al ProxyServer.
     */
    InformacionAlCiudadano obtenerInformacionParaCiudadano(ObtenerInformacionParaCiudadano solicitud);
}
