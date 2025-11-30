package datacenter.service;

import datacenter.dto.InformacionAlCiudadano;
import datacenter.dto.ObtenerInformacionParaCiudadano;


public interface IServicioDataCenter {

    
    InformacionAlCiudadano obtenerInformacionParaCiudadano(ObtenerInformacionParaCiudadano solicitud);
}