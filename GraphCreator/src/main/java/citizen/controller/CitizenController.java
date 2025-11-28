package citizen.controller;

import citizen.service.ReceptorDeInformacionSistemaCiudadano;
import citizen.ui.UiCiudadano;


public class CitizenController {

    private final UiCiudadano view;
    private final ReceptorDeInformacionSistemaCiudadano receptor;

    public CitizenController(UiCiudadano view, ReceptorDeInformacionSistemaCiudadano receptor) {
        this.view = view;
        this.receptor = receptor;
    }

    
    public void consultarTiempoPromedio(long origenId, long destinoId) {
        String mensaje = receptor.obtenerInformacionParaCiudadano(origenId, destinoId);
        view.mostrarInformacionAlCiudadano(mensaje);
    }
}