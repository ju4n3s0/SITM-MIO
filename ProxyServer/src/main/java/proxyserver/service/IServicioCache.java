package proxyserver.service;

import citizen.dto.ObtenerInformacionParaCiudadano;
import citizen.dto.InformacionAlCiudadano;


public interface IServicioCache {

    
    InformacionAlCiudadano obtenerInformacionParaCiudadano(ObtenerInformacionParaCiudadano solicitud);
}