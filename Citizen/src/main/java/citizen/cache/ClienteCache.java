package citizen.cache;

import java.util.HashMap;
import java.util.Map;

import citizen.dto.RespuestaConsulta;
import citizen.proxy.ClienteProxy;


public class ClienteCache {

    private final Map<String, RespuestaConsulta> cache = new HashMap<>();
    private final ClienteProxy proxy;

    public ClienteCache(ClienteProxy proxy) {
        this.proxy = proxy;
    }

    
    public RespuestaConsulta obtenerRespuesta(long origenId, long destinoId) {
        String key = origenId + "-" + destinoId;
        RespuestaConsulta respuesta = cache.get(key);
        if (respuesta == null) {
            respuesta = proxy.solicitarTiempoViaje(origenId, destinoId);
            if (respuesta != null) {
                cache.put(key, respuesta);
            }
        }
        return respuesta;
    }
}