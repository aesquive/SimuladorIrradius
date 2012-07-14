/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package auxiliar;

import calculador.vehiculo.DistribuidorModeloVehiculo;
import java.util.List;
import javax.faces.event.AjaxBehaviorEvent;
import org.primefaces.model.chart.MeterGaugeChartModel;
import util.Funciones;
import util.Parametros;

/**
 *
 * @author alberto
 */
public class SimulacionCapital {
    
    private boolean valorGeneralModificado;
    private boolean valorProyectoModificado;
    
    private String capitalInversionista;
    private List<Parametros> capitalProyecto;
    private double tasaInteres;
    private double porcentajeApalancamiento;
    private double porcentajeCajaMinima;
    private double porcentajeCreditoPuente;
    private String valorPromedioVivienda;
    private List<Parametros> valorPromedioProyecto;
    private double porcentajeTirMinima;
    private double porcentajeTirPrimera;
    private double porcentajeTirMeta;
    private MeterGaugeChartModel tacometro;
    private String leyenda;
    private String lineaCredito;
    private double tirPry;
    private double tirInv;
    private double porInv;
    private double porGrl;
    private CandleBean vela;
    private DistribuidorModeloVehiculo distribuidor;
    private boolean NoViable;

    
    private double probabilidadIncumplimiento;
    private double porcentajeCasasIncumplimiento;
    private String leyendaProbabilidad;
    
    public SimulacionCapital(DistribuidorModeloVehiculo distribuidorModeloVehiculo){
        valorGeneralModificado=false;
        this.distribuidor=distribuidorModeloVehiculo;
        llenarBeanSimulacionCapital();
        vela=new CandleBean(distribuidor);
        NoViable=vela.isNoViable();
    }
    
    /**
     * @return the capitalInversionista
     */
    public String getCapitalInversionista() {
        return capitalInversionista;
    }

    /**
     * @param capitalInversionista the capitalInversionista to set
     */
    public void setCapitalInversionista(String capitalInversionista) {
        this.capitalInversionista = capitalInversionista;
    }

    /**
     * @return the capitalProyecto
     */
    public List<Parametros> getCapitalProyecto() {
        return capitalProyecto;
    }

    /**
     * @param capitalProyecto the capitalProyecto to set
     */
    public void setCapitalProyecto(List<Parametros> capitalProyecto) {
        this.capitalProyecto = capitalProyecto;
    }

    /**
     * @return the porcentajeApalancamiento
     */
    public double getPorcentajeApalancamiento() {
        return porcentajeApalancamiento;
    }

    /**
     * @param porcentajeApalancamiento the porcentajeApalancamiento to set
     */
    public void setPorcentajeApalancamiento(double porcentajeApalancamiento) {
        this.porcentajeApalancamiento = porcentajeApalancamiento;
    }

    /**
     * @return the porcentajeCreditoPuente
     */
    public double getPorcentajeCreditoPuente() {
        return porcentajeCreditoPuente;
    }

    /**
     * @param porcentajeCreditoPuente the porcentajeCreditoPuente to set
     */
    public void setPorcentajeCreditoPuente(double porcentajeCreditoPuente) {
        this.porcentajeCreditoPuente = porcentajeCreditoPuente;
    }

    /**
     * @return the valorPromedioVivienda
     */
    public String getValorPromedioVivienda() {
        return valorPromedioVivienda;
    }

    /**
     * @param valorPromedioVivienda the valorPromedioVivienda to set
     */
    public void setValorPromedioVivienda(String valorPromedioVivienda) {
        this.valorPromedioVivienda = valorPromedioVivienda;
    }

    /**
     * @return the valorPromedioProyecto
     */
    public List<Parametros> getValorPromedioProyecto() {
        return valorPromedioProyecto;
    }

    /**
     * @param valorPromedioProyecto the valorPromedioProyecto to set
     */
    public void setValorPromedioProyecto(List<Parametros> valorPromedioProyecto) {
        this.valorPromedioProyecto = valorPromedioProyecto;
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

    /**
     * @return the leyenda
     */
    public String getLeyenda() {
        return leyenda;
    }

    /**
     * @param leyenda the leyenda to set
     */
    public void setLeyenda(String leyenda) {
        this.leyenda = leyenda;
    }

    /**
     * @return the lineaCredito
     */
    public String getLineaCredito() {
        return lineaCredito;
    }

    /**
     * @param lineaCredito the lineaCredito to set
     */
    public void setLineaCredito(String lineaCredito) {
        this.lineaCredito = lineaCredito;
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
     * @return the vela
     */
    public CandleBean getVela() {
        return vela;
    }

    /**
     * @param vela the vela to set
     */
    public void setVela(CandleBean vela) {
        this.vela = vela;
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

    private void llenarBeanSimulacionCapital() {
        distribuidor.llenarBeanSimulacionCapital(this);
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
    
    public DistribuidorModeloVehiculo preSimulacion(){
        Double valor = Double.parseDouble(String.valueOf(Funciones.castearVAlor(String.valueOf(probabilidadIncumplimiento))));
        if(valor==100){
            valor=99.0;
        }
        distribuidor.modificarSimulacionCapital(this);
        return distribuidor;
    }
    
    public void postSimulacion(){
        distribuidor.llenarBeanSimulacionCapital(this);
        vela=new CandleBean(distribuidor);
        NoViable=vela.isNoViable();
        valorGeneralModificado=false;
    }
    
    
    public String simulacionCapital(){
        Double valor = Double.parseDouble(String.valueOf(Funciones.castearVAlor(String.valueOf(probabilidadIncumplimiento))));
        if(valor==100){
            this.setLeyendaProbabilidad("No podemos tener probabilidad de incumplimiento 0");
            return "";
        }
        this.setLeyendaProbabilidad("");
        distribuidor.modificarSimulacionCapital(this);
        distribuidor.modelarPrincipal();
        distribuidor.llenarBeanSimulacionCapital(this);
        vela=new CandleBean(distribuidor);
        NoViable=vela.isNoViable();
        valorGeneralModificado=false;
        return "";
    }

    /**
     * @return the NoViable
     */
    public boolean isNoViable() {
        return NoViable;
    }

    /**
     * @param NoViable the NoViable to set
     */
    public void setNoViable(boolean NoViable) {
        this.NoViable = NoViable;
    }

    /**
     * @return the porcentajeCajaMinima
     */
    public double getPorcentajeCajaMinima() {
        return porcentajeCajaMinima;
    }

    /**
     * @param porcentajeCajaMinima the porcentajeCajaMinima to set
     */
    public void setPorcentajeCajaMinima(double porcentajeCajaMinima) {
        this.porcentajeCajaMinima = porcentajeCajaMinima;
    }

    /**
     * @return the valorGeneralModificado
     */
    public boolean isValorGeneralModificado() {
        return valorGeneralModificado;
    }

    /**
     * @param valorGeneralModificado the valorGeneralModificado to set
     */
    public void setValorGeneralModificado(boolean valorGeneralModificado) {
        this.valorGeneralModificado = valorGeneralModificado;
    }

    public void modificadoValor(AjaxBehaviorEvent  event){
        this.valorGeneralModificado=true;
        System.out.println("cambiando a true");
    }

    public void modificadoValorProyecto(AjaxBehaviorEvent  event){
        this.valorProyectoModificado=true;
        System.out.println("cambiando a true");
    }
    
    /**
     * @return the valorProyectoModificado
     */
    public boolean isValorProyectoModificado() {
        return valorProyectoModificado;
    }

    /**
     * @param valorProyectoModificado the valorProyectoModificado to set
     */
    public void setValorProyectoModificado(boolean valorProyectoModificado) {
        this.valorProyectoModificado = valorProyectoModificado;
    }

    /**
     * @return the probabilidadIncumplimiento
     */
    public double getProbabilidadIncumplimiento() {
        return probabilidadIncumplimiento;
    }

    /**
     * @param probabilidadIncumplimiento the probabilidadIncumplimiento to set
     */
    public void setProbabilidadIncumplimiento(double probabilidadIncumplimiento) {
        this.probabilidadIncumplimiento = probabilidadIncumplimiento;
    }

    /**
     * @return the porcentajeCasasIncumplimiento
     */
    public double getPorcentajeCasasIncumplimiento() {
        return porcentajeCasasIncumplimiento;
    }

    /**
     * @param porcentajeCasasIncumplimiento the porcentajeCasasIncumplimiento to set
     */
    public void setPorcentajeCasasIncumplimiento(double porcentajeCasasIncumplimiento) {
        this.porcentajeCasasIncumplimiento = porcentajeCasasIncumplimiento;
    }

    /**
     * @return the leyendaProbabilidad
     */
    public String getLeyendaProbabilidad() {
        return leyendaProbabilidad;
    }

    /**
     * @param leyendaProbabilidad the leyendaProbabilidad to set
     */
    public void setLeyendaProbabilidad(String leyendaProbabilidad) {
        this.leyendaProbabilidad = leyendaProbabilidad;
    }

    /**
     * @return the tasaInteres
     */
    public double getTasaInteres() {
        return tasaInteres;
    }

    /**
     * @param tasaInteres the tasaInteres to set
     */
    public void setTasaInteres(double tasaInteres) {
        this.tasaInteres = tasaInteres;
    }

    
}
