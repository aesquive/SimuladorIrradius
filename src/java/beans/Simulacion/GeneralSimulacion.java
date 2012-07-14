/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package beans.Simulacion;

import auxiliar.CandleBean;
import auxiliar.EneadaBean;
import auxiliar.GraficasSimulacion;
import auxiliar.InversionSimulacionBean;
import auxiliar.SimulacionCapital;
import auxiliar.SimulacionMinistracion;
import auxiliar.SimuladorGeneral;
import base.Dao;
import calculador.vehiculo.DistribuidorModeloVehiculo;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import pojos.MdlVeh;
import pojos.PryVeh;
import util.Funciones;

/**
 *
 * @author Quants
 */
public class GeneralSimulacion {

    private static String sufijo = "/home/alberto/apache-tomcat-7.0.14/webapps/Faces/";
    public static String delimitadorArchivos = "/";
    //public static String sufijo="";
    private boolean estanCalculadosMapas;
    private String numeroLogin;
    private String numeroModelo;
    private DistribuidorModeloVehiculo distribuidor;
    private MdlVeh modelo;
    private EneadaBean eneadaBeanPrecioTasa;
    private EneadaBean eneadaBeanApaTasa;
    private EneadaBean eneadaBeanPrecioApa;
    private EneadaBean eneadaBeanCapApa;
    private InversionSimulacionBean inversionSimulacionBean;
    private GraficasSimulacion graficasBalance;
    private GraficasSimulacion graficasFinancieras;
    private GraficasSimulacion graficasParametricas;
    private SimulacionMinistracion simulacionMinistracionBean;
    private CandleBean candleBean;
    private SimulacionCapital simulacionCapital;
    
    private SimuladorGeneral simuladorGeneral;
    private List<PryVeh> proyectos;

    public GeneralSimulacion() {
   //    parseLinea("8-167");
        sacarProyectoActual();
        generarDistribuidor();

        this.inversionSimulacionBean = new InversionSimulacionBean(distribuidor);
        this.simulacionMinistracionBean = new SimulacionMinistracion(distribuidor);
        this.estanCalculadosMapas = buscarMapas();
        if (estanCalculadosMapas) {
            this.eneadaBeanPrecioTasa = new EneadaBean(modelo, distribuidor, "Precio Promedio", "Tasa Interes", 0, 0);
            this.eneadaBeanApaTasa = new EneadaBean(modelo, distribuidor, "Porcentaje Apalancamiento", "Tasa Interes", 1, 1);
            this.eneadaBeanPrecioApa = new EneadaBean(modelo, distribuidor, "Precio Promedio", "Porcentaje Apalancamiento", 2, 2);
        }
        //this.eneadaBeanCapApa=new EneadaBean(modelo, distribuidor, "Capital", "Porcentaje Apalancamiento", 3, 3);
        this.candleBean = new CandleBean(distribuidor);
        this.simulacionCapital = new SimulacionCapital(distribuidor);
        this.simuladorGeneral=new SimuladorGeneral(modelo,proyectos,modelo.getMesMin(),modelo.getMesPag());
        ponerGraficas();
    }
    
    public String calcularMapas(){
            this.eneadaBeanPrecioTasa = new EneadaBean(modelo, distribuidor, "Precio Promedio", "Tasa Interes", 0, 0);
            this.eneadaBeanApaTasa = new EneadaBean(modelo, distribuidor, "Porcentaje Apalancamiento", "Tasa Interes", 1, 1);
            this.eneadaBeanPrecioApa = new EneadaBean(modelo, distribuidor, "Precio Promedio", "Porcentaje Apalancamiento", 2, 2);
            return "";
    }

    private void ponerGraficas() {
        List<String> listaBalance = new LinkedList<String>();
        listaBalance.add("veh_bal_roe");
        listaBalance.add("veh_bal_roa");
        listaBalance.add("veh_bal_ebi");
        listaBalance.add("veh_pal_ope");
        listaBalance.add("veh_pal_apa");
        listaBalance.add("veh_liq_anu");//
        listaBalance.add("veh_cap_tra_anu");
        listaBalance.add("veh_cob_deu");
        listaBalance.add("veh_rot_inv");
        listaBalance.add("veh_dia_cta_por_cob");
        listaBalance.add("veh_dia_cta_por_pag");
        listaBalance.add("veh_cob_ser_deu");
        this.graficasBalance = new GraficasSimulacion(numeroModelo, distribuidor, listaBalance);
        List<String> listaFinancieras = new LinkedList<String>();
        listaFinancieras.add("veh_mar_ope");
        listaFinancieras.add("veh_mar_ant_imp");
        listaFinancieras.add("veh_mar_net");
        listaFinancieras.add("veh_bal_efe_anu");
        listaFinancieras.add("veh_bal_cta_cob_anu");
        listaFinancieras.add("veh_bal_inv_viv_anu");
        listaFinancieras.add("veh_bal_tot_act_anu");
        listaFinancieras.add("veh_bal_cta_por_pag_anu");
        listaFinancieras.add("veh_bal_deu_anu");
        listaFinancieras.add("veh_bal_tot_pas_anu");
        listaFinancieras.add("veh_bal_cap_anu");
        listaFinancieras.add("veh_utl_per_anu");
        listaFinancieras.add("veh_utl_ret_anu");
        listaFinancieras.add("veh_bal_tot_pat_anu");
        this.graficasFinancieras = new GraficasSimulacion(numeroModelo, distribuidor, listaFinancieras);
        List<String> listaParametrias = new LinkedList<String>();
        listaParametrias.add("veh_cet_uni_edf_anu");
        listaParametrias.add("veh_cet_uni_edf_acu_anu");
        listaParametrias.add("veh_cet_uni_dis_anu");
        listaParametrias.add("veh_cet_uni_dis_acu_anu");
        listaParametrias.add("veh_cet_uni_ven_anu");
        listaParametrias.add("veh_cet_uni_ven_acu_anu");
        this.graficasParametricas = new GraficasSimulacion(numeroModelo, distribuidor, listaParametrias);
    }

    /**
     * @return the eneadaBeanPrecioTasa
     */
    public EneadaBean getEneadaBeanPrecioTasa() {
        return eneadaBeanPrecioTasa;
    }

    /**
     * @param eneadaBeanPrecioTasa the eneadaBeanPrecioTasa to set
     */
    public void setEneadaBeanPrecioTasa(EneadaBean eneadaBeanPrecioTasa) {
        this.eneadaBeanPrecioTasa = eneadaBeanPrecioTasa;
    }

    /**
     * @return the eneadaBeanApaTasa
     */
    public EneadaBean getEneadaBeanApaTasa() {
        return eneadaBeanApaTasa;
    }

    /**
     * @param eneadaBeanApaTasa the eneadaBeanApaTasa to set
     */
    public void setEneadaBeanApaTasa(EneadaBean eneadaBeanApaTasa) {
        this.eneadaBeanApaTasa = eneadaBeanApaTasa;
    }

    /**
     * @return the eneadaBeanPrecioApa
     */
    public EneadaBean getEneadaBeanPrecioApa() {
        return eneadaBeanPrecioApa;
    }

    /**
     * @param eneadaBeanPrecioApa the eneadaBeanPrecioApa to set
     */
    public void setEneadaBeanPrecioApa(EneadaBean eneadaBeanPrecioApa) {
        this.eneadaBeanPrecioApa = eneadaBeanPrecioApa;
    }

    /**
     * @return the inversionSimulacionBean
     */
    public InversionSimulacionBean getInversionSimulacionBean() {
        return inversionSimulacionBean;
    }

    /**
     * @param inversionSimulacionBean the inversionSimulacionBean to set
     */
    public void setInversionSimulacionBean(InversionSimulacionBean inversionSimulacionBean) {
        this.inversionSimulacionBean = inversionSimulacionBean;
    }

    /**
     * @return the simulacionMinistracionBean
     */
    public SimulacionMinistracion getSimulacionMinistracionBean() {
        return simulacionMinistracionBean;
    }

    /**
     * @param simulacionMinistracionBean the simulacionMinistracionBean to set
     */
    public void setSimulacionMinistracionBean(SimulacionMinistracion simulacionMinistracionBean) {
        this.simulacionMinistracionBean = simulacionMinistracionBean;
    }

    private void sacarProyectoActual() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(getSufijo() + "Simulacion" + delimitadorArchivos + "simulacion.conf"));
            String linea = reader.readLine();
            parseLinea(linea);
            reader.close();
        } catch (IOException ex) {
            Logger.getLogger(GeneralSimulacion.class.getName()).log(Level.SEVERE, null, ex);
        }
        generarDistribuidor();
    }

    private void parseLinea(String linea) {
        String[] split = linea.split("-");
        this.setNumeroLogin(split[0]);
        this.setNumeroModelo(split[1]);
    }

    /**
     * @return the numeroLogin
     */
    public String getNumeroLogin() {
        return numeroLogin;
    }

    /**
     * @param numeroLogin the numeroLogin to set
     */
    public void setNumeroLogin(String numeroLogin) {
        this.numeroLogin = numeroLogin;
    }

    /**
     * @return the numeroModelo
     */
    public String getNumeroModelo() {
        return numeroModelo;
    }

    /**
     * @param numeroModelo the numeroModelo to set
     */
    public void setNumeroModelo(String numeroModelo) {
        this.numeroModelo = numeroModelo;
    }

    private void generarDistribuidor() {
        Dao dao = new Dao();
        modelo = dao.getModelo(Integer.parseInt(this.numeroModelo));
        this.proyectos = Funciones.sacarProyectos(modelo);
        distribuidor = new DistribuidorModeloVehiculo(proyectos, true,modelo.getMesMin(),modelo.getMesPag());
        distribuidor.modelarPrincipal();
    }

    public static void main(String[] args) {
        GeneralSimulacion gen = new GeneralSimulacion();

    }

    /**
     * @return the sufijo
     */
    public static String getSufijo() {
        return sufijo;
    }

    /**
     * @param aSufijo the sufijo to set
     */
    public static void setSufijo(String aSufijo) {
        sufijo = aSufijo;
    }

    /**
     * @return the graficasBalance
     */
    public GraficasSimulacion getGraficasBalance() {
        return graficasBalance;
    }

    /**
     * @param graficasBalance the graficasBalance to set
     */
    public void setGraficasBalance(GraficasSimulacion graficasBalance) {
        this.graficasBalance = graficasBalance;
    }

    /**
     * @return the graficasFinancieras
     */
    public GraficasSimulacion getGraficasFinancieras() {
        return graficasFinancieras;
    }

    /**
     * @param graficasFinancieras the graficasFinancieras to set
     */
    public void setGraficasFinancieras(GraficasSimulacion graficasFinancieras) {
        this.graficasFinancieras = graficasFinancieras;
    }

    /**
     * @return the graficasParametricas
     */
    public GraficasSimulacion getGraficasParametricas() {
        return graficasParametricas;
    }

    /**
     * @param graficasParametricas the graficasParametricas to set
     */
    public void setGraficasParametricas(GraficasSimulacion graficasParametricas) {
        this.graficasParametricas = graficasParametricas;
    }

    /**
     * @return the eneadaBeanCapApa
     */
    public EneadaBean getEneadaBeanCapApa() {
        return eneadaBeanCapApa;
    }

    /**
     * @param eneadaBeanCapApa the eneadaBeanCapApa to set
     */
    public void setEneadaBeanCapApa(EneadaBean eneadaBeanCapApa) {
        this.eneadaBeanCapApa = eneadaBeanCapApa;
    }

    /**
     * @return the candleBean
     */
    public CandleBean getCandleBean() {
        return candleBean;
    }

    /**
     * @param candleBean the candleBean to set
     */
    public void setCandleBean(CandleBean candleBean) {
        this.candleBean = candleBean;
    }

    /**
     * @return the simulacionCapital
     */
    public SimulacionCapital getSimulacionCapital() {
        return simulacionCapital;
    }

    /**
     * @param simulacionCapital the simulacionCapital to set
     */
    public void setSimulacionCapital(SimulacionCapital simulacionCapital) {
        this.simulacionCapital = simulacionCapital;
    }

    /**
     * @return the estanCalculadosMapas
     */
    public boolean isEstanCalculadosMapas() {
        return estanCalculadosMapas;
    }

    /**
     * @param estanCalculadosMapas the estanCalculadosMapas to set
     */
    public void setEstanCalculadosMapas(boolean estanCalculadosMapas) {
        this.estanCalculadosMapas = estanCalculadosMapas;
    }

    private boolean buscarMapas() {
        Set mapCalVehs = modelo.getMapCalVehs();
        if(mapCalVehs!=null && mapCalVehs.size()>0){
            return true;
        }
        return false;
    }

    /**
     * @return the simuladorGeneral
     */
    public SimuladorGeneral getSimuladorGeneral() {
        return simuladorGeneral;
    }

    /**
     * @param simuladorGeneral the simuladorGeneral to set
     */
    public void setSimuladorGeneral(SimuladorGeneral simuladorGeneral) {
        this.simuladorGeneral = simuladorGeneral;
    }
}
