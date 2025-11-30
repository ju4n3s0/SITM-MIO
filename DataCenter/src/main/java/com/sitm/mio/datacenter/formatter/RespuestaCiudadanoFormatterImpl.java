package datacenter.formatter;

import datacenter.dto.InformacionAlCiudadano;
import datacenter.dto.RespuestaConsulta;


public class RespuestaCiudadanoFormatterImpl implements IRespuestaCiudadanoFormatter {

    @Override
    public InformacionAlCiudadano formatearRespuesta(RespuestaConsulta respuesta) {
        String mensaje = String.format(
                "Entre las paradas %d y %d la distancia es de %.2f km y el tiempo promedio estimado es de %.2f minutos.",
                respuesta.getOrigenId(), respuesta.getDestinoId(), respuesta.getDistanciaKm(), respuesta.getTiempoMinutos());
        return new InformacionAlCiudadano(mensaje);
    }
}