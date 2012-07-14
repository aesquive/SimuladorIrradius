/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package auxiliar;

import calculador.vehiculo.DistribuidorModeloVehiculo;
import java.util.List;
import java.util.Set;
import javax.faces.component.UIComponent;
import org.primefaces.model.chart.MeterGaugeChartModel;
import pojos.EtpTirVeh;
import pojos.PryVeh;
import util.Espejo;

/**
 *
 * @author alberto
 */
public class InversionSimulacionBean {

    private DistribuidorModeloVehiculo distribuidor;
    private double tir0;
    private double inv0;
    private double grl0;
    private double tir1;
    private double inv1;
    private double grl1;
    private double tir2;
    private double inv2;
    private double grl2;
    private double tir3;
    private double inv3;
    private double grl3;
    private double tirPry;
    private double tirInv;
    private double porInv;
    private double porGrl;
    private MeterGaugeChartModel tacometro;

    
    private double porcentajeTirMinima;
    private double porcentajeTirPrimera;
    private double porcentajeTirMeta;
    private double porcentajeExcedente;
    
    
    private boolean act0;
    private boolean act1;
    private boolean act2;
    private boolean act3;
    
    public InversionSimulacionBean(DistribuidorModeloVehiculo distribuidor){
        this.distribuidor=distribuidor;
        distribuidor.llenarBeanInversion(this);
    }

    /**
     * @return the tir0
     */
    public double getTir0() {
        return tir0;
    }

    /**
     * @param tir0 the tir0 to set
     */
    public void setTir0(double tir0) {
        this.tir0 = tir0;
    }

    /**
     * @return the inv0
     */
    public double getInv0() {
        return inv0;
    }

    /**
     * @param inv0 the inv0 to set
     */
    public void setInv0(double inv0) {
        this.inv0 = inv0;
    }

    /**
     * @return the grl0
     */
    public double getGrl0() {
        return grl0;
    }

    /**
     * @param grl0 the grl0 to set
     */
    public void setGrl0(double grl0) {
        this.grl0 = grl0;
    }

    /**
     * @return the tir1
     */
    public double getTir1() {
        return tir1;
    }

    /**
     * @param tir1 the tir1 to set
     */
    public void setTir1(double tir1) {
        this.tir1 = tir1;
    }

    /**
     * @return the inv1
     */
    public double getInv1() {
        return inv1;
    }

    /**
     * @param inv1 the inv1 to set
     */
    public void setInv1(double inv1) {
        this.inv1 = inv1;
    }

    /**
     * @return the grl1
     */
    public double getGrl1() {
        return grl1;
    }

    /**
     * @param grl1 the grl1 to set
     */
    public void setGrl1(double grl1) {
        this.grl1 = grl1;
    }

    /**
     * @return the tir2
     */
    public double getTir2() {
        return tir2;
    }

    /**
     * @param tir2 the tir2 to set
     */
    public void setTir2(double tir2) {
        this.tir2 = tir2;
    }

    /**
     * @return the inv2
     */
    public double getInv2() {
        return inv2;
    }

    /**
     * @param inv2 the inv2 to set
     */
    public void setInv2(double inv2) {
        this.inv2 = inv2;
    }

    /**
     * @return the grl2
     */
    public double getGrl2() {
        return grl2;
    }

    /**
     * @param grl2 the grl2 to set
     */
    public void setGrl2(double grl2) {
        this.grl2 = grl2;
    }

    /**
     * @return the tir3
     */
    public double getTir3() {
        return tir3;
    }

    /**
     * @param tir3 the tir3 to set
     */
    public void setTir3(double tir3) {
        this.tir3 = tir3;
    }

    /**
     * @return the inv3
     */
    public double getInv3() {
        return inv3;
    }

    /**
     * @param inv3 the inv3 to set
     */
    public void setInv3(double inv3) {
        this.inv3 = inv3;
    }

    /**
     * @return the grl3
     */
    public double getGrl3() {
        return grl3;
    }

    /**
     * @param grl3 the grl3 to set
     */
    public void setGrl3(double grl3) {
        this.grl3 = grl3;
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
    
    
     public void ponerValor(UIComponent component , Object valor){
       component.getAttributes().put("value", valor);
   }
   
   public Object sacarValor(UIComponent component,String atributo){
        return component.getAttributes().get(atributo);
   }
   
   public DistribuidorModeloVehiculo preSimulacion(){
       List<PryVeh> originales=getDistribuidor().getProyectos();
       List<PryVeh> proyectos=modificarProyectos();
        getDistribuidor().setProyectos(proyectos);
        return distribuidor;
   }
   
   public void postSimulacion(){
       distribuidor.llenarBeanInversion(this);
   }

   public  String procesarSimulacion(){
       List<PryVeh> originales=getDistribuidor().getProyectos();
       List<PryVeh> proyectos=modificarProyectos();
        getDistribuidor().setProyectos(proyectos);
        getDistribuidor().modelarPrincipal();
        getDistribuidor().llenarBeanInversion(this);
        getDistribuidor().setProyectos(originales);
        getDistribuidor().modelarPrincipal();
       return "index";
   }

    private List<PryVeh> modificarProyectos() {
        List<PryVeh> proyectos = getDistribuidor().getProyectos();
        for(int t=0;t<proyectos.size();t++){
            PryVeh pry=proyectos.get(t).clone();
            Set<EtpTirVeh> etpTirVehs = pry.getEtpTirVehs();
            modificarValores(etpTirVehs);
        }
        return proyectos;
    }




    private void modificarValores(Set<EtpTirVeh> etpTirVehs) {
        int indiceMayor=4;
        for(int t=0;t<indiceMayor;t++){
            boolean val=false;
            switch(t){
                case 0 : val=isAct0();break;
            case 1 : val=isAct1();break;
            case 2 : val=isAct2();break;
            case 3 : val=isAct3();break;
            }
            if(val){
            meterValores(t,etpTirVehs);
                
            }
        }
    }

    private void meterValores(int t, Set<EtpTirVeh> etpTirVehs) {
        EtpTirVeh etapaTir=buscarEtapa(t,"veh_tir_etp",etpTirVehs);
        EtpTirVeh etapaInv=buscarEtapa(t,"veh_dis_inv",etpTirVehs);
        etapaTir.setVal(String.valueOf(Espejo.invocarGetter(this, Espejo.getMetodo(this.getClass(), "getTir"+t)))+"%");
        etapaInv.setVal(String.valueOf(Espejo.invocarGetter(this, Espejo.getMetodo(this.getClass(), "getInv"+t)))+"%");
    }

    private EtpTirVeh buscarEtapa(int indiceEscalon, String nombreVariable, Set<EtpTirVeh> etpTirVehs) {
        for(EtpTirVeh etp:etpTirVehs){
            if(etp.getVarVeh().getDesTca().equals(nombreVariable) && etp.getNumEta()==indiceEscalon){
                return etp;
            }
        }
        return null;
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

    /**
     * @return the act0
     */
    public boolean isAct0() {
        return act0;
    }

    /**
     * @param act0 the act0 to set
     */
    public void setAct0(boolean act0) {
        this.act0 = act0;
    }

    /**
     * @return the act1
     */
    public boolean isAct1() {
        return act1;
    }

    /**
     * @param act1 the act1 to set
     */
    public void setAct1(boolean act1) {
        this.act1 = act1;
    }

    /**
     * @return the act2
     */
    public boolean isAct2() {
        return act2;
    }

    /**
     * @param act2 the act2 to set
     */
    public void setAct2(boolean act2) {
        this.act2 = act2;
    }

    /**
     * @return the act3
     */
    public boolean isAct3() {
        return act3;
    }

    /**
     * @param act3 the act3 to set
     */
    public void setAct3(boolean act3) {
        this.act3 = act3;
    }


    
    
}
