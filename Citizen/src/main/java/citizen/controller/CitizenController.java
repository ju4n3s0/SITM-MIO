package citizen.controller;

import citizen.auth.Autenticador;
import citizen.cache.ClienteCache;
import citizen.dto.InformacionAlCiudadano;
import citizen.dto.RespuestaConsulta;
import citizen.formatter.IRespuestaCiudadanoFormatter;
import citizen.formatter.RespuestaCiudadanoFormatterImpl;
import citizen.proxy.ClienteProxy;
import citizen.service.ServicioTiempoViaje;
import citizen.ui.UiCiudadano;


public class CitizenController {

    private final UiCiudadano view;
    private final Autenticador autenticador;
    private final ClienteCache cache;
    private final IRespuestaCiudadanoFormatter formatter;
    
    private boolean autenticado;

    public CitizenController(UiCiudadano view) {
        this.view = view;
        this.autenticador = new Autenticador();
        ServicioTiempoViaje servicio = new ServicioTiempoViaje();
        ClienteProxy proxy = new ClienteProxy(servicio);
        this.cache = new ClienteCache(proxy);
        this.formatter = new RespuestaCiudadanoFormatterImpl();
        this.autenticado = false;
    }

    
    public void consultar(long origenId, long destinoId) {
        if (!autenticado) {
            // El usuario debe autenticarse antes de realizar consultas
            view.mostrarInformacionAlCiudadano("Debe iniciar sesión antes de realizar consultas.");
            return;
        }
        RespuestaConsulta respuesta = cache.obtenerRespuesta(origenId, destinoId);
        if (respuesta == null) {
            view.mostrarInformacionAlCiudadano("No se encontraron datos para las paradas especificadas.");
            return;
        }
        InformacionAlCiudadano info = formatter.format(respuesta);
        view.mostrarInformacionAlCiudadano(info.getMensaje());
    }

    
    public boolean autenticarUsuario(String usuario, String contrasena) {
        boolean ok = autenticador.autenticar(usuario, contrasena);
        this.autenticado = ok;
        return ok;
    }
}