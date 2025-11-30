package datacenter.dto;


public class ObtenerInformacionParaCiudadano {

    private final long origenId;
    private final long destinoId;

    public ObtenerInformacionParaCiudadano(long origenId, long destinoId) {
        this.origenId = origenId;
        this.destinoId = destinoId;
    }

    public long getOrigenId() {
        return origenId;
    }

    public long getDestinoId() {
        return destinoId;
    }
}