package datacenter.formatter;

import datacenter.dto.RespuestaConsulta;
import datacenter.dto.InformacionAlCiudadano;


public interface IRespuestaCiudadanoFormatter {

    
    InformacionAlCiudadano formatearRespuesta(RespuestaConsulta respuesta);
}