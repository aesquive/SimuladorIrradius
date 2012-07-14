/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util.vehiculo;

import java.util.LinkedList;
import java.util.List;
import util.Parametros;

/**
 *
 * @author Alberto
 */
public class EscalonTir {
    
    private List<Parametros> valores;

    public EscalonTir(List<Parametros> valores) {
        this.valores = valores;
    }

    /**
     * @return the valores
     */
    public List<Parametros> getValores() {
        return valores;
    }

    /**
     * @param valores the valores to set
     */
    public void setValores(List<Parametros> valores) {
        this.valores = valores;
    }
    
    
    
}
