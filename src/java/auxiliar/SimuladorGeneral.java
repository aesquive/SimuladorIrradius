package auxiliar;

import calculador.vehiculo.DistribuidorModeloVehiculo;
import guardadores.GuardadorModelo;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import org.primefaces.model.chart.MeterGaugeChartModel;
import pojos.MdlVeh;
import pojos.PryVeh;

/**
 *
 * @author alberto
 */
public class SimuladorGeneral {

    private String leyendaGuardado;
    private String nuevoModelo;
    
    private DistribuidorModeloVehiculo distribuidor;
    private List<PryVeh> proyectos;
    private InversionSimulacionBean inversionSimulacionBean;
    private SimulacionMinistracion simulacionMinistracionBean;
    private SimulacionCapital simulacionCapital;
    private MeterGaugeChartModel tacometro;
    private final MdlVeh modelo;
    
    public SimuladorGeneral(MdlVeh modelo,List<PryVeh> proyectos , int tiempoMinistracion , int tiempoPago){
        this.proyectos=proyectos;
        this.modelo=modelo;
        distribuidor=new DistribuidorModeloVehiculo(proyectos, true,tiempoMinistracion, tiempoPago);
        distribuidor.modelarPrincipal();
        generarBeansIndependientes();
        llenarBeans();
    }

    private void generarBeansIndependientes() {
        this.setInversionSimulacionBean(new InversionSimulacionBean(getDistribuidor()));
        this.setSimulacionCapital(new SimulacionCapital(getDistribuidor()));
        this.setSimulacionMinistracionBean(new SimulacionMinistracion(getDistribuidor()));
        
    }

    public String simulacionTotal(){
        this.distribuidor=simulacionMinistracionBean.preSimulacion();
        simulacionCapital.setDistribuidor(distribuidor);
        this.distribuidor = simulacionCapital.preSimulacion();
        this.inversionSimulacionBean.setDistribuidor(distribuidor);
        this.distribuidor = inversionSimulacionBean.preSimulacion();
        distribuidor.modelarPrincipal();
        inversionSimulacionBean.setDistribuidor(distribuidor);
        simulacionCapital.setDistribuidor(distribuidor);
        simulacionMinistracionBean.setDistribuidor(distribuidor);
        inversionSimulacionBean.postSimulacion();
        simulacionCapital.postSimulacion();
        simulacionMinistracionBean.postSimulacion();
        llenarBeans();
        leyendaGuardado="";
        return "";
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
     * @return the proyectos
     */
    public List<PryVeh> getProyectos() {
        return proyectos;
    }

    /**
     * @param proyectos the proyectos to set
     */
    public void setProyectos(List<PryVeh> proyectos) {
        this.proyectos = proyectos;
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

    private void llenarBeans() {
        setTacometro(inversionSimulacionBean.getTacometro());
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
    

    public void guardarModelo(){
        GuardadorModelo guardador=new GuardadorModelo(nuevoModelo,modelo,distribuidor);
        String nombre =guardador.guardar();
        leyendaGuardado="El modelo "+nuevoModelo+" se ha guardado exitosamente ";
    }

    /**
     * @return the nuevoModelo
     */
    public String getNuevoModelo() {
        return nuevoModelo;
    }

    /**
     * @param nuevoModelo the nuevoModelo to set
     */
    public void setNuevoModelo(String nuevoModelo) {
        this.nuevoModelo = nuevoModelo;
    }

    /**
     * @return the leyendaGuardado
     */
    public String getLeyendaGuardado() {
        return leyendaGuardado;
    }

    /**
     * @param leyendaGuardado the leyendaGuardado to set
     */
    public void setLeyendaGuardado(String leyendaGuardado) {
        this.leyendaGuardado = leyendaGuardado;
    }
    
}
