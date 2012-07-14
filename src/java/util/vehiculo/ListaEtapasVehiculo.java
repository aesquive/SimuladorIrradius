/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util.vehiculo;

import java.util.List;

/**
 *
 * @author alberto
 */
public class ListaEtapasVehiculo {
    private List<EtapaCapturaVehiculo> listaEtapas;

    public ListaEtapasVehiculo(List<EtapaCapturaVehiculo> listaEtapas) {
        this.listaEtapas = listaEtapas;
    }
    
    public void agregar(EtapaCapturaVehiculo etapa){
        getListaEtapas().add(etapa);
    }

    /**
     * @return the listaEtapas
     */
    public List<EtapaCapturaVehiculo> getListaEtapas() {
        return listaEtapas;
    }

    /**
     * @param listaEtapas the listaEtapas to set
     */
    public void setListaEtapas(List<EtapaCapturaVehiculo> listaEtapas) {
        this.listaEtapas = listaEtapas;
    }
    
}
