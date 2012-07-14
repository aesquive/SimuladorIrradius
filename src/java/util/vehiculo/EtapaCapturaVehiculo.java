    package util.vehiculo;

import java.util.Date;

/**
 *
 * @author alberto
 */
public class EtapaCapturaVehiculo {

    private int viviendasEtapa;
    private Date fechaInicioEtapa;
    private Date fechaFinEtapa;

    public EtapaCapturaVehiculo(int viviendasPorEtapa, Date fechaInicio, Date fechaFin) {
        this.viviendasEtapa = viviendasPorEtapa;
        this.fechaInicioEtapa = fechaInicio;
        this.fechaFinEtapa = fechaFin;
    }

    /**
     * @return the fechaInicioEtapa
     */
    public Date getFechaInicioEtapa() {
        return fechaInicioEtapa;
    }

    /**
     * @param fechaInicioEtapa the fechaInicioEtapa to set
     */
    public void setFechaInicioEtapa(Date fechaInicioEtapa) {
        this.fechaInicioEtapa = fechaInicioEtapa;
    }

    /**
     * @return the fechaFinEtapa
     */
    public Date getFechaFinEtapa() {
        return fechaFinEtapa;
    }

    /**
     * @param fechaFinEtapa the fechaFinEtapa to set
     */
    public void setFechaFinEtapa(Date fechaFinEtapa) {
        this.fechaFinEtapa = fechaFinEtapa;
    }

    /**
     * @return the viviendasEtapa
     */
    public int getViviendasEtapa() {
        return viviendasEtapa;
    }

    /**
     * @param viviendasEtapa the viviendasEtapa to set
     */
    public void setViviendasEtapa(int viviendasEtapa) {
        this.viviendasEtapa = viviendasEtapa;
    }

}
