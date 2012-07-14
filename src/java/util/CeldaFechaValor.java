package util;

import java.io.Serializable;
import java.util.Calendar;

/**
 *
 * @author alberto
 */
public class CeldaFechaValor implements Comparable,Serializable{
    
    private Calendar fecha;
    private double valor;
    private String fechaTexto;
    private String valorRedondeado;

    public CeldaFechaValor(Calendar fecha, double valor) {
        this.fecha = fecha;
        this.valor = valor;
    }

    /**
     * @return the fecha
     */
    public Calendar getFecha() {
        return fecha;
    }

    /**
     * @param fecha the fecha to set
     */
    public void setFecha(Calendar fecha) {
        this.fecha = fecha;
    }

    /**
     * @return the valor
     */
    public double getValor() {
        return valor;
    }

    /**
     * @param valor the valor to set
     */
    public void setValor(double valor) {
        this.valor = valor;
    }


    @Override
    public int compareTo(Object t) {
        CeldaFechaValor comparar=(CeldaFechaValor) t;
        return this.getFecha().compareTo(comparar.fecha);
    }

    /**
     * @return the fechaTexto
     */
    public String getFechaTexto() {
        return Funciones.DateToString(fecha.getTime());
    }

    /**
     * @param fechaTexto the fechaTexto to set
     */
    public void setFechaTexto(String fechaTexto) {
        this.fechaTexto = fechaTexto;
    }

    /**
     * @return the valorRedondeado
     */
    public String getValorRedondeado() {
        
        return Funciones.ponerComasCantidades(Math.round(valor));
    }

    /**
     * @param valorRedondeado the valorRedondeado to set
     */
    public void setValorRedondeado(String valorRedondeado) {
        this.valorRedondeado = valorRedondeado;
    }

    @Override
    public String toString() {
        return "CeldaFechaValor{" + "fecha=" + fecha + "," + valor + ", fechaTexto=" + fechaTexto + ", valorRedondeado=" + valorRedondeado + '}';
    }
    
    
    
}
