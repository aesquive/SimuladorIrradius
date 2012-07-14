/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package auxiliar;

import base.Dao;
import calculador.vehiculo.DistribuidorModeloVehiculo;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import pojos.*;
import util.Eneada;
import util.Espejo;
import util.Funciones;
import util.vehiculo.EtapaCapturaVehiculo;
import util.vehiculo.ListaEtapasVehiculo;
import variables.ManejadorVariablesVehiculo;

/**
 *
 * @author JOSELUIS
 */
public class EneadaBean {

    private Dao dao;
    private DistribuidorModeloVehiculo distribuidor;
    private MdlVeh modelo;
    private List<Eneada> listaEneada;
    private String tirProyecto;
    private String parm1;
    private String parm2;
    private double input1;
    private double input2;
    private double tirNumero;
    
    

    public EneadaBean(MdlVeh modelo, DistribuidorModeloVehiculo distribuidor, String parm1, String parm2, double input1, double input2) {
        this.modelo = modelo;
        this.distribuidor = distribuidor;
        this.parm1 = parm1;
        this.parm2 = parm2;
        this.input1 = input1;
        this.input2 = input2;
        dao = new Dao();
        llenarMapa();
    }

    /**
     * Creates a new instance of EneadaBean
     */
    public EneadaBean(String parm1, String parm2, double input1, double input2, List<Eneada> eneada) {
        this.parm1 = parm1;
        this.parm2 = parm2;
        this.input1 = input1;
        this.input2 = input2;
        this.listaEneada = eneada;
    }

    /**
     * @return the listaEneada
     */
    public List<Eneada> getListaEneada() {
        return listaEneada;
    }

    /**
     * @param listaEneada the listaEneada to set
     */
    public void setListaEneada(List<Eneada> listaEneada) {
        this.listaEneada = listaEneada;
    }

    /**
     * @return the tirProyecto
     */
    public String getTirProyecto() {
        return tirProyecto;
    }

    /**
     * @param tirProyecto the tirProyecto to set
     */
    public void setTirProyecto(String tirProyecto) {
        this.tirProyecto = tirProyecto;
    }

    /**
     * @return the parm1
     */
    public String getParm1() {
        return parm1;
    }

    /**
     * @param parm1 the parm1 to set
     */
    public void setParm1(String parm1) {
        this.parm1 = parm1;
    }

    /**
     * @return the parm2
     */
    public String getParm2() {
        return parm2;
    }

    /**
     * @param parm2 the parm2 to set
     */
    public void setParm2(String parm2) {
        this.parm2 = parm2;
    }

    /**
     * @return the input1
     */
    public double getInput1() {
        return input1;
    }

    /**
     * @param input1 the input1 to set
     */
    public void setInput1(double input1) {
        this.input1 = input1;
    }

    /**
     * @return the input2
     */
    public double getInput2() {
        return input2;
    }

    /**
     * @param input2 the input2 to set
     */
    public void setInput2(double input2) {
        this.input2 = input2;
    }

    /**
     * @return the tirNumero
     */
    public double getTirNumero() {
        this.tirNumero = Double.parseDouble(tirProyecto);
        return tirNumero;
    }

    /**
     * @param tirNumero the tirNumero to set
     */
    public void setTirNumero(double tirNumero) {
        this.tirNumero = tirNumero;
    }

    private void llenarMapa() {
        String descripcionTecnicaParm1 = obtenerDescripcionTecnica(parm1);
        String descripcionTecnicaParm2 = obtenerDescripcionTecnica(parm2);
        this.input1 = Double.valueOf(Funciones.redondear(String.valueOf(sacarValor(descripcionTecnicaParm1)), 3));
        this.input2 = Double.valueOf(Funciones.redondear(String.valueOf(sacarValor(descripcionTecnicaParm2)), 3));
        this.tirProyecto = Funciones.redondear(String.valueOf((((Double) sacarValorIndividual("veh_cet_ptj_tir_pry")) * 100)), 3);
        TipMapCalVeh tipoMatriz = obtenerTipoMatriz(descripcionTecnicaParm1, descripcionTecnicaParm2);
        if (estaMapaBase(tipoMatriz, modelo)) {
            llenarListaEneadas(modelo, tipoMatriz);
        } else {
            distribuidor.llenarBeanMapa(this, parm1, parm2, input1, input2);
            distribuidor.setProcesarIndividuales(true);
            guardarMapas(modelo, tipoMatriz);

        }
    }
    
    

    private String obtenerDescripcionTecnica(String parametro) {
        if (parametro.equals("Precio Promedio")) {
            return "veh_val_pro_viv";
        }
        if (parametro.equals("Tasa Interes")) {
            return "veh_tsa_anu_crd_pte";
        }
        if (parametro.equals("Porcentaje Apalancamiento")) {
            return "veh_por_apa";
        }
        if (parametro.equals("Capital")) {
            return "veh_cap_inv";
        }
        return null;
    }

    private double sacarValor(String clave) {
        if (clave.equals("veh_val_pro_viv")) {
            return sacarValorVivienda();
        }
        if (clave.equals("veh_cap_inv")) {
            return sumaCapital("veh_cap_inv");
        }
        double valor = 0;
        for (int t = 0; t < distribuidor.getProyectos().size(); t++) {
            PryVeh proyecto = distribuidor.getProyectos().get(t);
            Set<DatVeh> datVehs = proyecto.getDatVehs();
            for (DatVeh dato : datVehs) {
                if (dato.getVarVeh()!=null && dato.getVarVeh().getDesTca().equals(clave)) {
                    valor += (Double) castearVAlor(dato.getVal());
                }
            }
        }
        return valor / distribuidor.getProyectos().size();
    }

    private double sacarValorVivienda() {
        List<Double> casas = obtenerNumeroCasas();
        List<Double> precioCasas = obtenerPrecioCasas();
        return Funciones.sumaProducto(precioCasas, casas);
    }

    private Object castearVAlor(String val) {
        Object regreso;
        if (val.contains("%")) {
            regreso = val.substring(0, val.length() - 1);
        } else {
            regreso = val;
        }
        try {
            return Double.parseDouble(String.valueOf(regreso));

        } catch (NumberFormatException num) {
            return val;
        }

    }

    private Object sacarValorIndividual(String variable) {
        return this.distribuidor.obtenerVariable(variable);
    }

    private TipMapCalVeh obtenerTipoMatriz(String descripcionTecnicaParm1, String descripcionTecnicaParm2) {
        int tipo = -1;
        if (descripcionTecnicaParm1.equals("veh_val_pro_viv")) {
            if (descripcionTecnicaParm2.equals("veh_tsa_anu_crd_pte")) {
                tipo = 1;
            } else {
                tipo = 2;
            }
        }else if(descripcionTecnicaParm1.equals("veh_cap_inv")) {
            tipo=4;
        }
        else {
            tipo = 3;
        }
        return dao.getTip_map(tipo);
    }

    private void guardarMapas(MdlVeh modelo, TipMapCalVeh tipoMatriz) {
        for (int t = 0; t < listaEneada.size(); t++) {
            Eneada actual = listaEneada.get(t);
            MapCalVeh linea = new MapCalVeh(modelo, tipoMatriz, 0, t, actual.getDes(), -2);
            dao.guardar(linea);
            for (int indiceValores = 0; indiceValores < 9; indiceValores++) {
                Method metodoVal = Espejo.getMetodo(actual.getClass(), "getVal" + indiceValores);
                Method metodoCol = Espejo.getMetodo(actual.getClass(), "getCol" + indiceValores);
                linea = new MapCalVeh(modelo, tipoMatriz, indiceValores + 1, t,
                        Espejo.invocarGetter(actual, metodoVal).toString(), Integer.parseInt(String.valueOf(Espejo.invocarGetter(actual, metodoCol))));
                dao.guardar(linea);
                System.out.println("guardando mapa");
            }
        }
        System.out.println("mapa guardado");
    }

    private boolean estaMapaBase(TipMapCalVeh tipoMatriz, MdlVeh modelo) {
        Set<MapCalVeh> mapCalVehs = modelo.getMapCalVehs();
        for (MapCalVeh mapa : mapCalVehs) {
            if (mapa.getMdlVeh().equals(modelo)) {
                return true;
            }
        }
        return false;
    }

    private void llenarListaEneadas(MdlVeh modelo, TipMapCalVeh tipoMatriz) {
        Set<MapCalVeh> mapCalVehs = modelo.getMapCalVehs();
        HashMap<Integer, Eneada> mapeoEneadas = new HashMap<Integer, Eneada>();
        for (MapCalVeh fila : mapCalVehs) {
            if (fila.getTipMapCalVeh().equals(tipoMatriz)) {
                Eneada eneadaMapa = mapeoEneadas.get(fila.getCoordY()) == null ? new Eneada() : mapeoEneadas.get(fila.getCoordY());
                String metodoValor = fila.getCoordX() == 0 ? "setDes" : "setVal" + (fila.getCoordX() - 1);
                String metodoColor = metodoValor.equals("setDes") ? null : "setCol" + (fila.getCoordX() - 1);
                Espejo.invocarSetterString(eneadaMapa, metodoValor, fila.getVal());
                Espejo.invocarSetterDouble(eneadaMapa, metodoColor, fila.getColor());
                mapeoEneadas.put(fila.getCoordY(), eneadaMapa);
            }
        }
        listaEneada = new LinkedList<Eneada>();
        for (int t = 0; t < 10; t++) {
            listaEneada.add(mapeoEneadas.get(t));
        }

    }

    private List<Double> obtenerNumeroCasas() {
        List<Double> lista = new LinkedList<Double>();
        ListaEtapasVehiculo[] etapasProyectos = (ListaEtapasVehiculo[]) distribuidor.obtenerVariable("veh_eta");
        for (int t = 0; t < distribuidor.getProyectos().size(); t++) {
            ListaEtapasVehiculo etapa = etapasProyectos[t];
            double numeroCasas = 0;
            List<EtapaCapturaVehiculo> listaEtapas = etapa.getListaEtapas();
            for (EtapaCapturaVehiculo etp : listaEtapas) {
                numeroCasas += etp.getViviendasEtapa();
            }
            lista.add(numeroCasas);
        }
        return lista;
    }

    private List<Double> obtenerPrecioCasas() {
        List<Double> precios = new LinkedList<Double>();
        for (int t = 0; t < distribuidor.getProyectos().size(); t++) {
            PryVeh proyecto = distribuidor.getProyectos().get(t);
            Set<DatVeh> datVehs = proyecto.getDatVehs();
            for (DatVeh dato : datVehs) {
                if (dato.getVarVeh()!=null && dato.getVarVeh().getDesTca().equals("veh_val_pro_viv")) {
                    String replace = dato.getVal().replace(",", "");
                    precios.add(Double.parseDouble(replace));
                }
            }
        }
        return precios;
    }

    private double sumaCapital(String clave) {
        double valor = 0;
        for (int t = 0; t < distribuidor.getProyectos().size(); t++) {
            PryVeh proyecto = distribuidor.getProyectos().get(t);
            Set<DatVeh> datVehs = proyecto.getDatVehs();
            for (DatVeh dato : datVehs) {
                if (dato.getVarVeh().getDesTca().equals(clave)) {
                    valor += (Double) castearVAlor(dato.getVal());
                }
            }
        }
        return valor;
    }

    
}
