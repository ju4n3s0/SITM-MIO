package datacenter.dto;


public class RespuestaConsulta {

    private final long origenId;
    private final long destinoId;
    private final double distanciaKm;
    private final double tiempoMinutos;

    public RespuestaConsulta(long origenId, long destinoId, double distanciaKm, double tiempoMinutos) {
        this.origenId = origenId;
        this.destinoId = destinoId;
        this.distanciaKm = distanciaKm;
        this.tiempoMinutos = tiempoMinutos;
    }

    public long getOrigenId() {
        return origenId;
    }

    public long getDestinoId() {
        return destinoId;
    }

    public double getDistanciaKm() {
        return distanciaKm;
    }

    public double getTiempoMinutos() {
        return tiempoMinutos;
    }
}