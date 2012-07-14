package util;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author alberto
 */
public class MatrizBidimensional implements Serializable {
    
    private List<CeldaFechaValor> celdas;
    
    
    /**
     *
     */
    public MatrizBidimensional(){
        celdas=new LinkedList<CeldaFechaValor>();
    }
    
    /**
     *
     */
    public void agregarCelda(CeldaFechaValor celda){
        getCeldas().add(celda);
    }

    /**
     * @return the celdas
     */
    public List<CeldaFechaValor> getCeldas() {
         Collections.sort(celdas);
         return celdas;
    }

    /**
     * @param celdas the celdas to set
     */
    public void setCeldas(List<CeldaFechaValor> celdas) {
        this.celdas = celdas;
    }

    @Override
    public String toString() {
        StringBuilder builder=new StringBuilder();
        for(CeldaFechaValor c:getCeldas()){
            builder.append("fecha "+c.getFechaTexto()+"\t "+c.getValor()+"\n");
        }
        return builder.toString();
    }

    
    
}
