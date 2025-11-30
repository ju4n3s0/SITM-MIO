package citizen.dto;


public class InformacionAlCiudadano {

    private final String mensaje;

    public InformacionAlCiudadano(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getMensaje() {
        return mensaje;
    }
}