package variables;

import base.Catalogueador;
import base.Dao;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import pojos.DatVeh;
import pojos.EtpVeh;
import pojos.MatVeh;
import pojos.PryVeh;
import pojos.VarVeh;
import util.CeldaFechaValor;
import util.Funciones;
import util.MatrizBidimensional;
import util.vehiculo.EtapaCapturaVehiculo;
import util.vehiculo.ListaEtapasVehiculo;

/**
 * Se encarga de mantener la consistencia en las variables que se van a utilizar para el modelo de vehiculo y proyecto
 * @author Alberto Emmanuel Esquivel Vega
 * 
 */
public class ManejadorVariablesVehiculo {

    private Map<String, Object> mapeoVariables;
    private List<PryVeh> listaProyectos;
    private Catalogueador catalogueador;
    private final int MATRIZ_TITULACION=3;
    private final int MATRIZ_EDIFICACION=1;
    private final int MATRIZ_VENTA=2;

    
    public ManejadorVariablesVehiculo(ManejadorVariablesVehiculo manejador){
        this.listaProyectos=manejador.listaProyectos;
        this.catalogueador=manejador.catalogueador;
        this.mapeoVariables=new HashMap<String, Object>();
    }
    
    public ManejadorVariablesVehiculo(List<PryVeh> listaProyectos) {
        this.listaProyectos = listaProyectos;
        this.catalogueador = new Catalogueador();
        mapeoVariables = new HashMap<String, Object>();
        llenarMapeo();
    }

    public Object obtenerVariable(String nombre) {
        return getMapeoVariables().get(nombre);
    }

    public void guardarVariable(String nombre, Object valor) {
        getMapeoVariables().put(nombre, valor);
    }

    private void llenarMapeo() {
        llenarDatosVehiculo();
        llenarDatosMatrices();
        llenarDatosEtapasTIR();
        llenarDatosEtapas();

    }

    private void llenarDatosVehiculo() {
        List<VarVeh> variablesVehiculo = catalogueador.getCatalogo(VarVeh.class.getName());
        for(VarVeh variable:variablesVehiculo){
            Object[] valoresProyectos=sacarDato(variable);
            guardarVariable(variable.getDesTca(), valoresProyectos);
        }
    }
    
    private void llenarDatosMatrices(){
        guardarVariable("veh_dis_edf", llenarMatrices(this.MATRIZ_EDIFICACION));
        guardarVariable("veh_dis_cmr", llenarMatrices(this.MATRIZ_VENTA));
        guardarVariable("veh_dis_tit", llenarMatrices(this.MATRIZ_TITULACION));
    }

    private MatrizBidimensional[] llenarMatrices(int tipoMAtriz) {
        MatrizBidimensional[] arreglo=new MatrizBidimensional[listaProyectos.size()];
        for(int indiceProyecto=0;indiceProyecto<listaProyectos.size();indiceProyecto++){
            arreglo[indiceProyecto]=sacarMatriz(listaProyectos.get(indiceProyecto),tipoMAtriz);
        }
        return arreglo;
    }

     private MatrizBidimensional sacarMatriz(PryVeh proyecto , int tipoMAtriz) {
        MatrizBidimensional matriz=new MatrizBidimensional();
        Set<MatVeh> matrices = proyecto.getMatVehs();
        Iterator<MatVeh> iterator = matrices.iterator();
        while(iterator.hasNext()){
            MatVeh renglonMatriz = iterator.next();
            if(renglonMatriz.getTipMatVeh().getId()==tipoMAtriz){
                matriz.agregarCelda(new CeldaFechaValor(renglonMatriz.getFch(), Double.parseDouble(String.valueOf(castearVAlor(renglonMatriz.getVal())))));
            }
        }
       return matriz;
     }
    
    private void llenarDatosEtapasTIR() {
        
    }

    private Object[] sacarDato(VarVeh variable) {
            Object[] arreglo=new Object[listaProyectos.size()];
        for(int t=0;t<listaProyectos.size();t++){
            arreglo[t]=sacarValor(listaProyectos.get(t),variable);
            
         }
        return arreglo;
    }

    private Object sacarValor(PryVeh proyecto, VarVeh variable) {
        Set<DatVeh> datos = proyecto.getDatVehs();
        for(DatVeh dato:datos){
            if(dato.getVarVeh()!=null && dato.getVarVeh().getDesTca().equals(variable.getDesTca())){
                return castearVAlor(dato.getVal());
            }
        }
        return null;
    }

    private Object castearVAlor(String val) {
        Object regreso;
        if(val.contains("%")){
            regreso=val.substring(0,val.length()-1);
        }else{
            regreso=val;
        }
        try{
            return Double.parseDouble(String.valueOf(regreso));
            
        }catch(NumberFormatException num){
            return val;
        }
        
    }
    
    public static void main(String[] args) {
        Dao dao=new Dao();
        PryVeh proyectoVehiculo = dao.getProyectoVehiculo(4);
        LinkedList<PryVeh> lista=new LinkedList<PryVeh>();
        lista.add(proyectoVehiculo);
        ManejadorVariablesVehiculo mane=new ManejadorVariablesVehiculo(lista);
        ListaEtapasVehiculo[] etapas=  (ListaEtapasVehiculo[]) mane.obtenerVariable("veh_fin_ctr_cpr_vta[0][0]");
         System.out.println(etapas[0].getListaEtapas().get(0).getFechaInicioEtapa());
        //        MatrizBidimensional[] obtenerVariable = (MatrizBidimensional[]) mane.obtenerVariable("veh_dis_edf");
        //        MatrizBidimensional m=obtenerVariable[0];
        //        List<CeldaFechaValor> celdas = m.getCeldas();
        //        for(CeldaFechaValor celda:celdas){
        //            System.out.println(celda.getFecha().toString()+" "+celda.getValor());
        //        }
    }

    private void llenarDatosEtapas() {
        ListaEtapasVehiculo[] arreglo=new ListaEtapasVehiculo[listaProyectos.size()];
        for(int indiceProyecto=0;indiceProyecto<listaProyectos.size();indiceProyecto++){
            
            List<EtapaCapturaVehiculo> etapas=llenarListaEtapas(listaProyectos.get(indiceProyecto));
            ListaEtapasVehiculo listaEtapas=new ListaEtapasVehiculo(etapas);
            
            arreglo[indiceProyecto]=listaEtapas;
        }
        guardarVariable("veh_eta",arreglo);
    }

    private List<EtapaCapturaVehiculo> llenarListaEtapas(PryVeh proyecto) {
        List<EtapaCapturaVehiculo> listaEtapas=new LinkedList<EtapaCapturaVehiculo>();
        Set<EtpVeh> etapasVehiculo = proyecto.getEtpVehs();
        Set<EtpVeh> copia=etapasVehiculo;
        int consecutivo=0;
        int limite=etapasVehiculo.size()/3;
        while(consecutivo<limite){
            EtapaCapturaVehiculo etapaVehiculo=new EtapaCapturaVehiculo(0, null, null);
            Iterator<EtpVeh> iterator = etapasVehiculo.iterator();
            while(iterator.hasNext()){
                EtpVeh fila = iterator.next();
                if(fila.getNumEta()==consecutivo){
                    if(fila.getVarVeh().getDesTca().equals("veh_viv_etp")){
                        etapaVehiculo.setViviendasEtapa(Integer.parseInt(fila.getVal()));
                    }
                    if(fila.getVarVeh().getDesTca().equals("veh_fec_ini_eta")){
                        etapaVehiculo.setFechaInicioEtapa(Funciones.StringToDate(fila.getVal()));
                    }
                    if(fila.getVarVeh().getDesTca().equals("veh_fec_fin_eta")){
                        etapaVehiculo.setFechaFinEtapa(Funciones.StringToDate(fila.getVal()));
                    }
                }
            }
            etapasVehiculo=copia;
            listaEtapas.add(etapaVehiculo);
            consecutivo++;
        }
        return listaEtapas;
    }

    /**
     * @return the mapeoVariables
     */
    public Map<String, Object> getMapeoVariables() {
        return mapeoVariables;
    }

    /**
     * @param mapeoVariables the mapeoVariables to set
     */
    public void setMapeoVariables(Map<String, Object> mapeoVariables) {
        this.mapeoVariables = mapeoVariables;
    }

   
}
