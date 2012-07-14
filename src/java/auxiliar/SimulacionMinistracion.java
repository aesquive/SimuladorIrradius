/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package auxiliar;

import calculador.vehiculo.DistribuidorModeloVehiculo;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.primefaces.model.chart.CartesianChartModel;
import org.primefaces.model.chart.MeterGaugeChartModel;
import util.Funciones;
import util.Parametros;

/**
 *
 * @author alberto
 */
public class SimulacionMinistracion{
    
    private DistribuidorModeloVehiculo distribuidor;
    private List<Parametros> tirProyectos;
    private CartesianChartModel grafica;
    private MeterGaugeChartModel tacometro;
    private int mesesMinistracion;
    private int mesesPago;
    private int mesesRevolvencia;
    private double tirPry;
    private double tirInv;
    private double porInv;
    private double porGrl;

    private int minX;
    private int maxX;
    
    
    private double porcentajeTirMinima;
    private double porcentajeTirPrimera;
    private double porcentajeTirMeta;
    private double porcentajeExcedente;
    
    /** Creates a new instance of LineChart */
    public SimulacionMinistracion(DistribuidorModeloVehiculo distribuidor) {
        this.distribuidor=distribuidor;
        distribuidor.llenarBeanGrafica(this);
    }

    

    /**
     * @return the mesesMinistracion
     */
    public int getMesesMinistracion() {
        return mesesMinistracion;
    }

    /**
     * @param mesesMinistracion the mesesMinistracion to set
     */
    public void setMesesMinistracion(int mesesMinistracion) {
        this.mesesMinistracion = mesesMinistracion;
    }

    /**
     * @return the mesesPago
     */
    public int getMesesPago() {
        return mesesPago;
    }

    /**
     * @param mesesPago the mesesPago to set
     */
    public void setMesesPago(int mesesPago) {
        this.mesesPago = mesesPago;
    }

    /**
     * @return the mesesRevolvencia
     */
    public int getMesesRevolvencia() {
        return mesesRevolvencia;
    }

    /**
     * @param mesesRevolvencia the mesesRevolvencia to set
     */
    public void setMesesRevolvencia(int mesesRevolvencia) {
        this.mesesRevolvencia = mesesRevolvencia;
    }

    /**
     * @return the tirPry
     */
    public double getTirPry() {
        return tirPry;
    }

    /**
     * @param tirPry the tirPry to set
     */
    public void setTirPry(double tirPry) {
        this.tirPry = tirPry;
    }

    /**
     * @return the tirInv
     */
    public double getTirInv() {
        return tirInv;
    }

    /**
     * @param tirInv the tirInv to set
     */
    public void setTirInv(double tirInv) {
        this.tirInv = tirInv;
    }

    /**
     * @return the porInv
     */
    public double getPorInv() {
        return porInv;
    }

    /**
     * @param porInv the porInv to set
     */
    public void setPorInv(double porInv) {
        this.porInv = porInv;
    }

    /**
     * @return the porGrl
     */
    public double getPorGrl() {
        return porGrl;
    }

    /**
     * @param porGrl the porGrl to set
     */
    public void setPorGrl(double porGrl) {
        this.porGrl = porGrl;
    }

    

    /**
     * @return the tirProyectos
     */
    public List<Parametros> getTirProyectos() {
        Collections.sort(tirProyectos);
        tirProyectos.add(0,new Parametros("IRR Vehiculo", String.valueOf(tirPry)+"%"));
        this.tirProyectos=quitarRepetidos(tirProyectos);
        return tirProyectos;
    }

    /**
     * @param tirProyectos the tirProyectos to set
     */
    public void setTirProyectos(List<Parametros> tirProyectos) {
        this.tirProyectos=tirProyectos;
    }

    /**
     * @return the distribuidor
     */
    public DistribuidorModeloVehiculo getDistribuidor() {
        return distribuidor;
    }

    /**
     * @param distribuidor the distribuidor to set
     */
    public void setDistribuidor(DistribuidorModeloVehiculo distribuidor) {
        this.distribuidor = distribuidor;
    }

    /**
     * @return the grafica
     */
    public CartesianChartModel getGrafica() {
        return grafica;
    }

    /**
     * @param grafica the grafica to set
     */
    public void setGrafica(CartesianChartModel grafica) {
        this.grafica = grafica;
    }

   

    /**
     * @return the tacometro
     */
    public MeterGaugeChartModel getTacometro() {
        return tacometro;
    }

    /**
     * @param tacometro the tacometro to set
     */
    public void setTacometro(MeterGaugeChartModel tacometro) {
        this.tacometro = tacometro;
    }


    public DistribuidorModeloVehiculo preSimulacion(){
        distribuidor.setTiempoMinistracion(mesesMinistracion);
        distribuidor.setTiempoPago(mesesPago);
        return distribuidor;
    }
    
    public void postSimulacion(){
        distribuidor.llenarBeanGrafica(this);
    }
    
    public String simularMinistracion(){
        if(getMesesMinistracion()==0){
            Funciones.mandarMensaje("principal:mensaje", "El tiempo de ministracion no puede ser 0");
            return "";
        }
        if(getMesesMinistracion()+getMesesPago()>60){
            Funciones.mandarMensaje("principal:mensaje","El tiempo de revolvencia es invalido");
            return "";
        }
        getDistribuidor().setTiempoMinistracion(getMesesMinistracion());
        getDistribuidor().setTiempoPago(getMesesPago());
        getDistribuidor().modelarPrincipal();
        getDistribuidor().llenarBeanGrafica(this);
        getDistribuidor().setTiempoMinistracion(18);
        getDistribuidor().setTiempoPago(12);
        return "";
    }

    /**
     * @return the minX
     */
    public int getMinX() {
        return minX;
    }

    /**
     * @param minX the minX to set
     */
    public void setMinX(int minX) {
        this.minX = minX;
    }

    /**
     * @return the maxX
     */
    public int getMaxX() {
        return maxX;
    }

    /**
     * @param maxX the maxX to set
     */
    public void setMaxX(int maxX) {
        this.maxX = maxX;
    }

    /**
     * @return the porcentajeTirMinima
     */
    public double getPorcentajeTirMinima() {
        return porcentajeTirMinima;
    }

    /**
     * @param porcentajeTirMinima the porcentajeTirMinima to set
     */
    public void setPorcentajeTirMinima(double porcentajeTirMinima) {
        this.porcentajeTirMinima = porcentajeTirMinima;
    }

    /**
     * @return the porcentajeTirPrimera
     */
    public double getPorcentajeTirPrimera() {
        return porcentajeTirPrimera;
    }

    /**
     * @param porcentajeTirPrimera the porcentajeTirPrimera to set
     */
    public void setPorcentajeTirPrimera(double porcentajeTirPrimera) {
        this.porcentajeTirPrimera = porcentajeTirPrimera;
    }

    /**
     * @return the porcentajeTirMeta
     */
    public double getPorcentajeTirMeta() {
        return porcentajeTirMeta;
    }

    /**
     * @param porcentajeTirMeta the porcentajeTirMeta to set
     */
    public void setPorcentajeTirMeta(double porcentajeTirMeta) {
        this.porcentajeTirMeta = porcentajeTirMeta;
    }

    /**
     * @return the porcentajeExcedente
     */
    public double getPorcentajeExcedente() {
        return porcentajeExcedente;
    }

    /**
     * @param porcentajeExcedente the porcentajeExcedente to set
     */
    public void setPorcentajeExcedente(double porcentajeExcedente) {
        this.porcentajeExcedente = porcentajeExcedente;
    }

    private List<Parametros> quitarRepetidos(List<Parametros> tirProyectos) {
        Set<String> conj=new HashSet<String>();
        List<Parametros> parms=new LinkedList<Parametros>();
        for(Parametros m:tirProyectos){
            if(!conj.contains(m.getRegistroDescripcion())){
                parms.add(m);
            }
        }
        return parms;
    }
}
