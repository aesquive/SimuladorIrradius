package calculador.vehiculo;

import auxiliar.EneadaBean;
import auxiliar.InversionSimulacionBean;
import auxiliar.SimulacionCapital;
import auxiliar.SimulacionMinistracion;
import base.Dao;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.primefaces.model.chart.CartesianChartModel;
import org.primefaces.model.chart.LineChartSeries;
import org.primefaces.model.chart.MeterGaugeChartModel;
import pojos.DatVeh;
import pojos.EtpTirVeh;
import pojos.MdlVeh;
import pojos.PryVeh;
import pojos.RelMdlVeh;
import util.Eneada;
import util.Espejo;
import util.Funciones;
import util.MatrizBidimensional;
import util.Parametros;
import util.ParametrosMatrizBid;
import util.Vector;
import util.vehiculo.Coordenada;
import util.vehiculo.EtapaCapturaVehiculo;
import util.vehiculo.ListaEtapasVehiculo;
import variables.ManejadorVariablesVehiculo;

/**
 *Clase que se encarga de distribuir el calculo de modelo de vehiculo en diferentes clases , repartiendo
 * por pestanas del excel de modelo espana
 * 
 * @author Alberto Emmanuel Esquivel Vega
 */
public class DistribuidorModeloVehiculo {

    /**
     * Manejador de las variables de vehiculo
     */
    private ManejadorVariablesVehiculo manejador;
    /**
     * Lista de proyectos que seran evaluados en el modelo
     */
    private List<PryVeh> proyectos;
    private boolean procesarIndividuales;
    private int tiempoMinistracion;
    private int tiempoPago;

    private double probabilidadIncumplimiento;
    private double porcentajeRecuperacion;
    
    
    public DistribuidorModeloVehiculo(int indiceProyecto ,List<PryVeh> proyectos , boolean procesarIndividuales , ManejadorVariablesVehiculo manejador , 
               int tiempoMinistracion , int tiempoPago){
        probabilidadIncumplimiento=-1;
        porcentajeRecuperacion=-1;
        this.tiempoMinistracion = tiempoMinistracion;
        this.tiempoPago = tiempoPago;
        this.procesarIndividuales = procesarIndividuales;
        this.proyectos = proyectos;
      //  this.manejador = Funciones.generarCopiaManejador(manejador,indiceProyecto);
    }
    /**
     * COnstructor
     * @param proyectos 
     */
    public DistribuidorModeloVehiculo(List<PryVeh> proyectos, boolean procesarIndividuales,int tiempoMinistracion , int tiempoPago) {
        probabilidadIncumplimiento=-1;
        porcentajeRecuperacion=-1;
        this.tiempoMinistracion = tiempoMinistracion;
        this.tiempoPago = tiempoPago;
        this.procesarIndividuales = procesarIndividuales;
        this.proyectos = proyectos;
        this.manejador = new ManejadorVariablesVehiculo(proyectos);
    }

    /**
     * metodo que modela individualmente los proyectos del vehiculo
     * @param respectivoManejador
     * @param proyecto 
     */
    protected void modelarIndividual(ManejadorVariablesVehiculo respectivoManejador, PryVeh proyecto) {
        System.out.println("inicio ind " + proyecto.getNomPry());
        CalculadorEtapas calculadorEtapas = new CalculadorEtapas(getProyectos(), respectivoManejador);
        calculadorEtapas.procesar();
        System.out.println("etapas ind " + proyecto.getNomPry());
        ConsolidadorEtapas consolidadorEtapas = new ConsolidadorEtapas(getTiempoMinistracion(), getTiempoPago(), getProyectos(), respectivoManejador);
        consolidadorEtapas.procesar();
        System.out.println("calctir ind " + proyecto.getNomPry());
        CalculadorTir calculadorTir = new CalculadorTir(tiempoMinistracion, tiempoPago, getProyectos(), respectivoManejador);
        calculadorTir.procesarIndividual();
        System.out.println("termino ind " + proyecto.getNomPry());
        System.out.println("la tir del proyecto " + proyecto.getNomPry() + "=" + manejador.obtenerVariable("veh_cet_ptj_tir_pry"));
    }

    /**
     * Metodo que mandara a hacer todos los calculos del modelo espana
     */
    public void modelarPrincipal() {
        Object[] capital = (Object[]) manejador.obtenerVariable("veh_cap_inv");
        for(int t=0;t<capital.length;t++){
            System.out.println("el capital al inicio de "+proyectos.get(t).getNomPry()+" es de "+capital[t]);
        }
        
        CalculadorEtapas calculadorEtapas = new CalculadorEtapas(getProyectos(), getManejador());
        calculadorEtapas.procesar();
        System.out.println("salio de aqui");
        ConsolidadorEtapas consolidadorEtapas = new ConsolidadorEtapas(getTiempoMinistracion(), getTiempoPago(), getProyectos(), getManejador());
        consolidadorEtapas.procesar();
        System.out.println("salio de aca");
        CalculadorTir calculadorTir = new CalculadorTir(tiempoMinistracion, tiempoPago, getProyectos(), getManejador());
        calculadorTir.procesarPrincipal(procesarIndividuales);

        System.out.println("la tir " + manejador.obtenerVariable("veh_cet_ptj_tir_pry"));
    
        Object[] capital2 = (Object[]) manejador.obtenerVariable("veh_cap_inv");
        for(int t=0;t<capital2.length;t++){
            System.out.println("el capital al final de "+proyectos.get(t).getNomPry()+" es de "+capital2[t]);
        }
    }

    public void generarEdoResultados() {
        EstadoResultados estadoResultados = new EstadoResultados(getProyectos(), getManejador());
        estadoResultados.procesar();

        Balance balance = new Balance(proyectos, getManejador());
        balance.procesar();
    }

    private List<MatrizBidimensional> sacarValoresManejador(String nombreLlave, int inProyecto) {
        int numEtapas = getProyectos().get(inProyecto).getEtpVehs().size() / 3;
        List<MatrizBidimensional> matrices = new LinkedList<MatrizBidimensional>();
        int contador = 0;
        while (contador < numEtapas) {
            MatrizBidimensional actual = (MatrizBidimensional) getManejador().obtenerVariable(nombreLlave.toLowerCase() + "[" + inProyecto + "][" + contador + "]");
            matrices.add(actual);
            contador++;
        }
        return matrices;
    }

    /**
     * @return the manejador
     */
    public ManejadorVariablesVehiculo getManejador() {
        return manejador;
    }

    /**
     * @param manejador the manejador to set
     */
    public void setManejador(ManejadorVariablesVehiculo manejador) {
        this.manejador = manejador;
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

    private Object sacarValorEscalonTir(int idVar, int indice, Set<EtpTirVeh> etpTirVehs) {
        Iterator<EtpTirVeh> iterator = etpTirVehs.iterator();
        while (iterator.hasNext()) {
            EtpTirVeh next = iterator.next();
            if (next.getNumEta() == indice && next.getVarVeh().getId() == idVar) {
                return castearVAlor(next.getVal());
            }
        }
        return null;
    }

    private Object castearVAlor(String val) {
        Object regreso=null;
        if (val.contains("%")) {
            regreso = val.substring(0, val.length() - 1);
        }  else if (val.contains(",")) {
            regreso = regreso!=null ? regreso.toString().replace(",",""):val.replace(",", "");
        } else {
            regreso = val;
        }
        try {
            return Double.parseDouble(String.valueOf(regreso));

        } catch (NumberFormatException num) {
            DistribuidorModeloVehiculo dis = new DistribuidorModeloVehiculo(proyectos, false,tiempoMinistracion,tiempoPago);
            dis.modelarPrincipal();
            return val;
        }

    }

    private double sacarCrecimiento(String primerParametro) {
        if (primerParametro.equals("Precio Promedio")) {
            return .05;
        }
        if (primerParametro.equals("Tasa Interes")) {
            return .5;
        }
        if (primerParametro.equals("Porcentaje Apalancamiento")) {
            return 2;
        }
        return 0;
    }

    private void calcularEneadas(List<Double> proporcionesPrecio, List<Eneada> eneadas, String primerParametro, String segundoParametro, Double tir) {
        Eneada primefaFila = eneadas.get(0);
        for (int indicePrimera = 0; indicePrimera < 9; indicePrimera++) {
            Eneada actual = eneadas.get(indicePrimera + 1);
            for (int indiceSegunda = 0; indiceSegunda < 9; indiceSegunda++) {
                modificarProyectos(proporcionesPrecio, primerParametro, String.valueOf(Espejo.invocarGetter(primefaFila, Espejo.getMetodo(primefaFila.getClass(), "getVal" + indiceSegunda))));
                modificarProyectos(proporcionesPrecio, segundoParametro, String.valueOf(Espejo.invocarGetter(actual, Espejo.getMetodo(actual.getClass(), "getDes"))));
                String tirTmp = obtenerValorDistribuidor();
                Espejo.invocarSetterString(actual, "setVal" + indiceSegunda, tirTmp);
                double copia = Double.parseDouble(tirTmp);
                int valor = Funciones.colorearCeldaMapaCalor(copia, tir);
                Espejo.invocarSetterDouble(actual, "setCol" + indiceSegunda, valor);
            }
        }
    }

    public static void main(String[] args) {
//        String cad="26,784";
//        String replace = cad.replace(",","");
//        System.out.println(replace);
        Dao dao = new Dao();
        List<PryVeh> proyectos = new LinkedList<PryVeh>();
        MdlVeh mdl=dao.getModelo(171);
        Set<RelMdlVeh> relMdlVehs = mdl.getRelMdlVehs();
        for(RelMdlVeh rel:relMdlVehs){
            System.out.println(rel.getPryVeh().getNomPry());
            proyectos.add(rel.getPryVeh());
        }
        DistribuidorModeloVehiculo dis = new DistribuidorModeloVehiculo(proyectos, false,18,12);
        
        dis.modelarPrincipal();
        System.out.println("aqui que pasa");
        System.out.println(dis.obtenerVariable("veh_cet_uni_edf"));
//        dis.generarEdoResultados();
//                dis.modelarPrincipal();
//                System.out.println("-----------------------------");
//        double sacarValorVivienda = dis.sacarValorVivienda();
//                dis.llenarBeanMapa(null, "veh_val_pro_viv", "veh_tsa_anu_crd_pte", sacarValorVivienda, 11.5);
    }

    public String obtenerValorDistribuidor() {
        this.modelarPrincipal();
        Double obtenerVariable = (Double) manejador.obtenerVariable("veh_cet_ptj_tir_pry") * 100;
        String cadena2 = String.valueOf(Funciones.redondearDecimales(obtenerVariable, 2));
        return cadena2;
    }

    private void modificarProyectos(List<Double> proporcionesPrecio, String parametro, String valor) {
        String clave = parametro.equals("Precio Promedio") ? "veh_val_pro_viv" : parametro.equals("Tasa Interes") ? "veh_tsa_anu_crd_pte" : "veh_por_apa";
        if (clave.equals("veh_val_pro_viv")) {
            modificarPrecioCasas(proporcionesPrecio, clave, valor);
            return;
        }
        Object[] arreglo = (Object[]) manejador.obtenerVariable(clave);
        for (int t = 0; t < proyectos.size(); t++) {
            arreglo[t] = valor;
        }
        manejador.guardarVariable(clave, arreglo);
    }

    private void modificarPrecioCasas(List<Double> proporcionesPrecio, String clave, String valor) {
        for (int indiceProyecto = 0; indiceProyecto < proyectos.size(); indiceProyecto++) {
            valor = valor.replace(",", "");
            double nuevo = Double.parseDouble(valor) * proporcionesPrecio.get(indiceProyecto);
            guardarValorVivienda(indiceProyecto, nuevo);
        }
    }

    private double sacarPrecioPromedioViejo() {
        double suma = 0;
        for (int indiceProyecto = 0; indiceProyecto < proyectos.size(); indiceProyecto++) {
            Object[] valoresViviendas = (Object[]) manejador.obtenerVariable("veh_val_pro_viv");
            suma += Double.parseDouble(String.valueOf(valoresViviendas[indiceProyecto]));
        }
        return suma / proyectos.size();
    }

    private double sacarValorVivienda(int indiceProyecto) {
        Object[] valoresViviendas = (Object[]) manejador.obtenerVariable("veh_val_pro_viv");
        return Double.parseDouble(String.valueOf(valoresViviendas[indiceProyecto]));
    }

    private void guardarValorVivienda(int indiceProyecto, double nuevo) {

        Object[] valoresViviendas = (Object[]) manejador.obtenerVariable("veh_val_pro_viv");
        valoresViviendas[indiceProyecto] = nuevo;
    }

    private Date obtenerFechaInicial(int indiceProyecto) {
        ListaEtapasVehiculo[] listaEtapas = (ListaEtapasVehiculo[]) manejador.obtenerVariable("veh_eta");
        Date fechaMenor = null;
        int numeroEtapas = listaEtapas[indiceProyecto].getListaEtapas().size();
        for (int indiceEtapa = 0; indiceEtapa < numeroEtapas; indiceEtapa++) {
            Date fechaActual = (listaEtapas[indiceProyecto].getListaEtapas().get(indiceEtapa).getFechaInicioEtapa());
            fechaMenor = fechaMenor == null ? fechaActual : fechaMenor.compareTo(fechaActual) < 0 ? fechaMenor : fechaActual;
        }

        return (Date) fechaMenor.clone();
    }

    private Date obtenerFechaFinal(int indiceProyecto) {
        ListaEtapasVehiculo[] listaEtapas = (ListaEtapasVehiculo[]) manejador.obtenerVariable("veh_eta");
        Date fechaMayor = null;
        int numeroEtapas = listaEtapas[indiceProyecto].getListaEtapas().size();
        for (int indiceEtapa = 0; indiceEtapa < numeroEtapas; indiceEtapa++) {
            Date fechaActual = (listaEtapas[indiceProyecto].getListaEtapas().get(indiceEtapa).getFechaFinEtapa());
            fechaMayor = fechaMayor == null ? fechaActual : fechaMayor.compareTo(fechaActual) < 0 ? fechaActual : fechaMayor;
        }
        return (Date) fechaMayor.clone();
    }

    private Date obtenerFechaInicialTodo() {
        ListaEtapasVehiculo[] etapasProyectos = (ListaEtapasVehiculo[]) manejador.obtenerVariable("veh_eta");
        Date fechaMenor = null;
        for (ListaEtapasVehiculo listaEtapas : etapasProyectos) {
            int numeroEtapas = listaEtapas.getListaEtapas().size();
            for (int indiceEtapa = 0; indiceEtapa < numeroEtapas; indiceEtapa++) {
                Date fechaActual = (listaEtapas.getListaEtapas().get(indiceEtapa).getFechaInicioEtapa());
                fechaMenor = fechaMenor == null ? fechaActual : fechaMenor.compareTo(fechaActual) < 0 ? fechaMenor : fechaActual;
            }
        }
        return (Date) fechaMenor.clone();
    }

    private HashMap<String, String> generarMapeoGraficas() {
        String[] nombres = new String[]{"veh_bal_roe", "veh_bal_roa", "veh_bal_ebi", "veh_pal_ope", "veh_pal_apa", "veh_liq_anu", "veh_cap_tra_anu",
            "veh_cob_deu", "veh_rot_inv", "veh_dia_cta_por_cob", "veh_dia_cta_por_pag", "veh_cob_ser_deu", "veh_mar_ope", "veh_mar_ant_imp", "veh_mar_net",
            "veh_bal_efe_anu", "veh_bal_cta_cob_anu", "veh_bal_inv_viv_anu", "veh_bal_tot_act_anu", "veh_bal_cta_por_pag_anu", "veh_bal_deu_anu", "veh_bal_tot_pas_anu",
            "veh_bal_cap_anu", "veh_utl_per_anu", "veh_utl_ret_anu", "veh_bal_tot_pat_anu",
            "veh_cet_uni_edf_anu", "veh_cet_uni_edf_acu_anu", "veh_cet_uni_dis_anu",
            "veh_cet_uni_dis_acu_anu", "veh_cet_uni_ven_anu", "veh_cet_uni_ven_acu_anu",
            "veh_edo_vta_viv_anu", "veh_edo_cto_vta_anu", "veh_total_utl_oprt_anu",
            "veh_gst_opt_anu", "veh_cet_int_cre_pte_anu", "veh_utl_a_ipt_anu", "veh_imp_sob_rta_anu",
            "veh_utl_net_anu"};


        String[] valores = new String[]{"ROE", "ROA", "EBITDA", "Palanca de Operacion", "Apalancamiento", "Liquidez", "Capital de Trabajo",
            "Cobertura de Deuda", "Rotacion de Inventarios", "Dias de Cuentas por Cobrar", "Dias de Cuenta por Pagar", "Cobertura de Serivicio de Deuda", "Margen Operativo", "Margen Antes de Impuestos", "Margen Neto",
            "Efectivo", "Cuentas por Cobrar", "Inventario de Vivienda", "Total de Activos", "Cuentas por pagar", "Deuda Credito", "Total Pasivos",
            "Capital", "Utilidad del Periodo", "Utilidad Retenida", "Total Patrimonio",
            "Unidades Edificadas", "Unidades Edificadas Acumuladas", "Unidades Disponibles",
            "Unidades Disponibles Acumuladas", "Unidades Vendidas", "Unidades Vendidas Acumuladas",
            "Venta de Vivienda", "Costo de Venta", "Utilidad Operativa", "Gastos Operativos", "Intereses", "Utilidad Antes de Impuestos", "Impuestos Sobre la Renta", "Utilidad Neta"};

        HashMap<String, String> mapeo = new HashMap<String, String>();
        for (int t = 0; t < nombres.length; t++) {
            mapeo.put(nombres[t], valores[t]);
        }
        return mapeo;
    }

    /**
     * @return the tiempoMinistracion
     */
    public int getTiempoMinistracion() {
        return tiempoMinistracion;
    }

    /**
     * @param tiempoMinistracion the tiempoMinistracion to set
     */
    public void setTiempoMinistracion(int tiempoMinistracion) {
        this.tiempoMinistracion = tiempoMinistracion;
    }

    /**
     * @return the tiempoPago
     */
    public int getTiempoPago() {
        return tiempoPago;
    }

    /**
     * @param tiempoPago the tiempoPago to set
     */
    public void setTiempoPago(int tiempoPago) {
        this.tiempoPago = tiempoPago;
    }

    private void generarTiempoProyectos(List<String> nombres, List<List<Coordenada>> valores) {
        Date inicialTodo = obtenerFechaInicialTodo();
        int consecutivo = 1;
        System.out.println("entre");
        for (int indiceProyecto = 0; indiceProyecto < proyectos.size(); indiceProyecto++) {
            List<Coordenada> lista = new LinkedList<Coordenada>();
            Date obtenerFechaInicial = obtenerFechaInicial(indiceProyecto);
            Date obtenerFechaFinal = obtenerFechaFinal(indiceProyecto);
            nombres.add(proyectos.get(indiceProyecto).getNomPry());
            int diferenciaPrincipio = Funciones.diferenciaMeses(inicialTodo, obtenerFechaInicial);
            int tiempo = Funciones.diferenciaMeses(obtenerFechaInicial, obtenerFechaFinal);
            Coordenada c1 = new Coordenada(diferenciaPrincipio - 1, consecutivo);
            Coordenada c2 = new Coordenada((diferenciaPrincipio - 1) + tiempo, consecutivo);
            lista.add(c1);
            lista.add(c2);
            consecutivo++;
            valores.add(lista);
        }
        System.out.println(valores);
    }

    private List<Parametros> obtenerTirsProyectos() {
        List<Parametros> lista = new LinkedList<Parametros>();
        lista.add(new Parametros("Nombre", "TIR"));
        for (int t = 0; t < proyectos.size(); t++) {
            ManejadorVariablesVehiculo mane = ((DistribuidorModeloVehiculo) manejador.obtenerVariable("dis" + t)).getManejador();
            Double tir = (Double) mane.obtenerVariable("veh_cet_ptj_tir_pry");
            lista.add(new Parametros(proyectos.get(t).getNomPry(), Funciones.redondear(String.valueOf(tir * 100), 2) + "%"));
            System.out.println("meti " + proyectos.get(t).getNomPry() + "  " + Funciones.redondear(String.valueOf(tir * 100), 2) + "%");
        }
        return lista;
    }

    public void llenarBeanMapa(EneadaBean bean, String primerParametro, String segundoParametro, double primerValor, double segundoValor) {
        Double tir = (Double) manejador.obtenerVariable("veh_cet_ptj_tir_pry") * 100;
        List<Double> obtenerPrecioCasas = obtenerPrecioCasas();
        List<Double> obtenerNumeroCasas = obtenerNumeroCasas();
        double valorSumaProducto = sacarValorVivienda();
        List<Double> proporcionesCapital = sacarProporcionesCapital();
        List<Double> proporciones = sacarProporciones(obtenerPrecioCasas, obtenerNumeroCasas, valorSumaProducto);
        List<Eneada> eneadas = new LinkedList<Eneada>();
        double crecimiento = sacarCrecimiento(primerParametro);
        double crecimientoSegunda = sacarCrecimiento(segundoParametro);
        Eneada eneada = null;

        if (primerParametro.equals("Precio Promedio")) {
            eneada = new Eneada("    ", String.valueOf(primerValor - (primerValor * 4 * crecimiento)), String.valueOf(primerValor - (primerValor * 3 * crecimiento)), String.valueOf(primerValor - (primerValor * 2 * crecimiento)), String.valueOf(primerValor - (primerValor * crecimiento)), String.valueOf(primerValor), String.valueOf(primerValor + (primerValor * crecimiento)), String.valueOf(primerValor + (primerValor * 2 * crecimiento)), String.valueOf(primerValor + (primerValor * 3 * crecimiento)), String.valueOf(primerValor + (primerValor * 4 * crecimiento)));

        } else {
            eneada = new Eneada("     ", String.valueOf(primerValor - (4 * crecimiento)), String.valueOf(primerValor - (3 * crecimiento)), String.valueOf(primerValor - (2 * crecimiento)), String.valueOf(primerValor - (1 * crecimiento)), String.valueOf(primerValor - (0 * crecimiento)), String.valueOf(primerValor + (1 * crecimiento)), String.valueOf(primerValor + (2 * crecimiento)), String.valueOf(primerValor + (3 * crecimiento)), String.valueOf(primerValor + (4 * crecimiento)));
        }

        eneadas.add(eneada);

        for (int t = 4; t >= 0; t--) {
            eneada = new Eneada(String.valueOf(segundoValor - (t * crecimientoSegunda)), "", "", "", "", "", "", "", "", "");
            eneadas.add(eneada);
        }
        for (int t = 1; t < 5; t++) {
            eneada = new Eneada(String.valueOf(segundoValor + (t * crecimientoSegunda)), "", "", "", "", "", "", "", "", "");
            eneadas.add(eneada);
        }

        calcularEneadas(proporciones, eneadas, primerParametro, segundoParametro, tir);
        bean.setListaEneada(eneadas);
        bean.setTirProyecto(eneadas.get(5).getVal4());

        if (primerParametro.equals("Precio Promedio")) {
            eneada = new Eneada("    ", Funciones.ponerComasCantidades(Math.round(primerValor - (primerValor * 4 * crecimiento))), Funciones.ponerComasCantidades(Math.round(primerValor - (primerValor * 3 * crecimiento))), Funciones.ponerComasCantidades(Math.round(primerValor - (primerValor * 2 * crecimiento))), Funciones.ponerComasCantidades(Math.round(primerValor - (primerValor * crecimiento))), Funciones.ponerComasCantidades(Math.round(primerValor)),
                    Funciones.ponerComasCantidades(Math.round(primerValor + (primerValor * crecimiento))), Funciones.ponerComasCantidades(Math.round(primerValor + (primerValor * 2 * crecimiento))), Funciones.ponerComasCantidades(Math.round(primerValor + (primerValor * 3 * crecimiento))), Funciones.ponerComasCantidades(Math.round(primerValor + (primerValor * 4 * crecimiento))));

        }

        modificarProyectos(proporciones, primerParametro, String.valueOf(primerValor));
        modificarProyectos(proporciones, segundoParametro, String.valueOf(segundoValor));
        modelarPrincipal();

    }

    public Object obtenerVariable(String variable) {
        return manejador.obtenerVariable(variable);
    }

    /**
     * @param procesarIndividuales the procesarIndividuales to set
     */
    public void setProcesarIndividuales(boolean procesarIndividuales) {
        this.procesarIndividuales = procesarIndividuales;
    }

    public List<ParametrosMatrizBid> obtenerGraficas(String idModelo, List<String> atributos) {
        HashMap<String, String> mapeo = generarMapeoGraficas();
        List<ParametrosMatrizBid> rutas = new LinkedList<ParametrosMatrizBid>();
        for (String s : atributos) {
            ParametrosMatrizBid parm = new ParametrosMatrizBid();
            List<Vector> vectores = new LinkedList<Vector>();
            parm.setNombre(mapeo.get(s));
            int inicial = 1;
            parm.setMinX(inicial);
            List<Double> lista = (List<Double>) manejador.obtenerVariable(s);
            System.out.println("llllllllllllllllllllllllllllllllllllll");
            System.out.println(s);
            for (Double d : lista) {
                vectores.add(new Vector(String.valueOf(inicial), Funciones.redondear(String.valueOf(d), 2)));
                inicial++;
            }
            parm.setMaxX(inicial - 1);
            parm.setMatriz(ponerComitasPorcentajes(s, vectores));
            parm.setGrafica(generarCartesianModel(parm, mapeo.get(s), s));
            rutas.add(parm);
        }
        return rutas;
    }

    private CartesianChartModel generarCartesianModel(ParametrosMatrizBid parm, String nombre, String llave) {
        CartesianChartModel carte = new CartesianChartModel();
        LineChartSeries series = new LineChartSeries(nombre);
        MatrizBidimensional obtenerVariable = (MatrizBidimensional) manejador.obtenerVariable(llave + "_graf");
        if (obtenerVariable == null) {

            return generarCarte(carte, series, llave);
        }

        parm.setMinX(0);
        parm.setMaxX(60);
        int consecutivo = 1;

        for (int t = 0; t < obtenerVariable.getCeldas().size() - 1; t++) {
            if (llave.equals("veh_bal_ebi") && obtenerVariable.getCeldas().get(t).getValor() < 0) {
            } else {

                series.set(consecutivo++, obtenerVariable.getCeldas().get(t).getValor());
            }
        }

        carte.addSeries(series);
        return carte;


    }

    private List<Vector> ponerComitasPorcentajes(String s, List<Vector> vectores) {
        List<Vector> vecs = new LinkedList<Vector>();
        String[] variablesComas = new String[]{"veh_bal_roe", "veh_bal_roa", "veh_bal_ebi", "veh_pal_ope", "veh_pal_apa", "veh_mar_ope", "veh_mar_ant_imp", "veh_mar_net"};
        boolean porcentaje = false;
        for (String var : variablesComas) {
            if (var.equals(s)) {
                porcentaje = true;
            }
        }
        for (Vector v : vectores) {
            String numero = "";
            if (!porcentaje) {
                numero = Funciones.ponerComasCantidades(Math.round(Double.valueOf(v.getY())));
            } else {
                numero = Funciones.redondear(v.getY(), 2) + "%";
            }
            vecs.add(new Vector(v.getX(), numero));
        }
        return vecs;
    }

    public void llenarBeanInversion(InversionSimulacionBean inversion) {
        PryVeh proyectoActual = proyectos.get(0);
        Set<EtpTirVeh> etpTirVehs = proyectoActual.getEtpTirVehs();
        int maximos=etpTirVehs.size()/2;
        for (int t = 0; t < 4; t++) {
            double tirMinima = t<maximos? Double.valueOf(String.valueOf(sacarValorEscalonTir(25, t, etpTirVehs))) : 0.0;
            System.out.println("la tir minima es "+tirMinima);
            double disInv = t<maximos ? Double.valueOf(String.valueOf(sacarValorEscalonTir(26, t, etpTirVehs))) :0.0;
            System.out.println("la dis al inv es"+disInv);
            double disGrl = t<maximos ? (100 - disInv) : 0.0;
            Espejo.invocarSetterInversion(inversion, Funciones.redondearDecimales(Math.round(tirMinima), 0), "setTir" + t);
            Espejo.invocarSetterInversion(inversion, Funciones.redondearDecimales(Math.round(disInv), 0), "setInv" + t);
            Espejo.invocarSetterInversion(inversion, Funciones.redondearDecimales(Math.round(disGrl), 0), "setGrl" + t);
            Espejo.invocarSetterBoolean(inversion ,t<maximos ,"setAct"+t);
        }
        inversion.setTirPry(Funciones.redondearDecimales((Double) manejador.obtenerVariable("veh_cet_ptj_tir_pry") * 100, 2));
        inversion.setPorGrl(Funciones.redondearDecimales((Double) manejador.obtenerVariable("veh_cet_rep_uti_grl_prt_ptj"), 2));
        inversion.setPorInv(Funciones.redondearDecimales((Double) manejador.obtenerVariable("veh_cet_rep_uti_lim_prt_ptj"), 2));
        inversion.setTirInv(Funciones.redondearDecimales((Double) manejador.obtenerVariable("veh_cet_ptj_tir_inv") * 100, 2));
        inversion.setTacometro(generarTacometro(inversion, inversion.getTirInv()));
    }

    private MeterGaugeChartModel generarTacometro(Object inversion, double tirInv) {
        List<Number> intervalos = new ArrayList<Number>();
        for (int t = 0; t < 3; t++) {
            String nombreMetodo = "";
            switch (t) {
                case 0:
                    nombreMetodo = "setPorcentajeTirMinima";
                    break;
                case 1:
                    nombreMetodo = "setPorcentajeTirPrimera";
                    break;
                case 2:
                    nombreMetodo = "setPorcentajeTirMeta";
                    break;
            }
            Double obtenerVariable = (Double) manejador.obtenerVariable("veh_cet_uti" + t);
            intervalos.add(obtenerVariable * 100);

            Espejo.invocarSetterDecimal(inversion, nombreMetodo, Funciones.redondearDecimales(obtenerVariable * 100, 3));
        }
        intervalos.add(100);
        return new MeterGaugeChartModel("IRR Inversionista", tirInv, intervalos);
    }

    public void llenarBeanGrafica(SimulacionMinistracion simulacion) {

        simulacion.setMesesMinistracion(tiempoMinistracion);
        simulacion.setMesesPago(tiempoPago);
        simulacion.setMesesRevolvencia(60 - (tiempoMinistracion + tiempoPago));
        simulacion.setTirInv(Funciones.redondearDecimales(((Double) obtenerVariable("veh_cet_ptj_tir_inv")) * 100, 2));
        simulacion.setTirPry(Funciones.redondearDecimales(((Double) obtenerVariable("veh_cet_ptj_tir_pry")) * 100, 2));
        simulacion.setPorInv(Funciones.redondearDecimales(((Double) obtenerVariable("veh_cet_rep_uti_lim_prt_ptj")), 2));
        simulacion.setPorGrl(Funciones.redondearDecimales(((Double) obtenerVariable("veh_cet_rep_uti_grl_prt_ptj")), 2));
        simulacion.setGrafica(generarGraficaProyectos(simulacion));
        simulacion.setTacometro(generarTacometro(simulacion, simulacion.getTirInv()));
        simulacion.setTirProyectos(generarTirProyectos());
    }

    private List<Parametros> generarTirProyectos() {
        List<Parametros> lista = new LinkedList<Parametros>();
        for (int t = 0; t < proyectos.size(); t++) {
            DistribuidorModeloVehiculo distProye = (DistribuidorModeloVehiculo) manejador.obtenerVariable("dis" + t);
            double tir = ((Double) distProye.obtenerVariable("veh_cet_ptj_tir_pry")) * 100;
            lista.add(new Parametros(proyectos.get(t).getNomPry(), Funciones.redondear(String.valueOf(tir), 3) + "%"));
        }
        Collections.sort(lista);
        return lista;
    }

    private int[] ponerProyectos(CartesianChartModel carteSian) {
        int[] arreglo = new int[2];
        List<Double> tirs = new LinkedList<Double>();
        Date fechaInicialTodo = obtenerFechaInicialTodo();
        for (int t = 0; t < proyectos.size(); t++) {
            LineChartSeries series = new LineChartSeries(proyectos.get(t).getNomPry());
            DistribuidorModeloVehiculo distTmp = (DistribuidorModeloVehiculo) manejador.obtenerVariable("dis" + t);
            double tir = ((Double) distTmp.obtenerVariable("veh_cet_ptj_tir_pry")) * 100;
            tirs.add(tir);
            Date fechaInicial = obtenerFechaInicial(t);
            Date fechaFinal = obtenerFechaFinal(t);
            int mesesDuracion = Funciones.diferenciaMeses(fechaInicial, fechaFinal) - 1;
            int mesesPrincipio = Funciones.diferenciaMeses(fechaInicialTodo, fechaInicial) - 1;
            series.set(mesesPrincipio, tir);
            series.set(mesesPrincipio + mesesDuracion + 1, tir);
            carteSian.addSeries(series);
        }
        Collections.sort(tirs);
        arreglo[0] = tirs.get(0).intValue();
        arreglo[1] = tirs.get(tirs.size() - 1).intValue();
        return arreglo;
    }

    private CartesianChartModel generarGraficaProyectos(SimulacionMinistracion simulacion) {
        CartesianChartModel carteSian = new CartesianChartModel();
        int[] minimoMaximo = ponerProyectos(carteSian);
        simulacion.setMinX(minimoMaximo[0]);
        simulacion.setMaxX(minimoMaximo[1] + 1);
        ponerCasaGrafica(minimoMaximo, carteSian);

        return carteSian;
    }

    private void ponerCasaGrafica(int[] minimoMaximo, CartesianChartModel carteSian) {
        Double altura = (Double) obtenerVariable("veh_cet_ptj_tir_pry");
        LineChartSeries series = new LineChartSeries("Distribucion del Credito");
        series.set(0, 0);
        series.set(tiempoMinistracion, altura * 100);
        series.set(tiempoMinistracion + ((60) - (tiempoMinistracion + tiempoPago)), altura * 100);
        series.set(60, 0);
        carteSian.addSeries(series);
    }

    private CartesianChartModel generarCarte(CartesianChartModel carte, LineChartSeries series, String llave) {
        List<Double> obtenerVariable = (List<Double>) manejador.obtenerVariable(llave);
        for (int t = 1; t <= obtenerVariable.size(); t++) {
            if (llave.equals("veh_bal_ebi") && obtenerVariable.get(t - 1) < 0) {
            } else {

                series.set(t, obtenerVariable.get(t - 1));
            }
        }
        carte.addSeries(series);
        return carte;
    }

    private List<Double> obtenerNumeroCasas() {
        List<Double> lista = new LinkedList<Double>();
        ListaEtapasVehiculo[] etapasProyectos = (ListaEtapasVehiculo[]) obtenerVariable("veh_eta");
        for (int t = 0; t < getProyectos().size(); t++) {
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
        for (int t = 0; t < getProyectos().size(); t++) {
            Object[] obtenerVariable = (Object[]) manejador.obtenerVariable("veh_val_pro_viv");
            precios.add(Double.parseDouble(obtenerVariable[t].toString()));

        }
        return precios;
    }

    private double sacarValorVivienda() {
        List<Double> casas = obtenerNumeroCasas();
        List<Double> precioCasas = obtenerPrecioCasas();
        return Funciones.sumaProducto(precioCasas, casas);
    }

    private List<Double> sacarProporciones(List<Double> obtenerPrecioCasas, List<Double> obtenerNumeroCasas, double valorSumaProducto) {
        List<Double> proporciones = new LinkedList<Double>();
        for (int t = 0; t < obtenerPrecioCasas.size(); t++) {
            double multi = obtenerPrecioCasas.get(t);

            System.out.println("la suma producto es " + valorSumaProducto + " el valor de la casa " + multi);
            double prop = multi / valorSumaProducto;
            proporciones.add(prop);
        }
        System.out.println("las proporciones son " + proporciones);
        return proporciones;
    }

    private List<Double> sacarProporcionesCapital() {
        List<Double> precios = new LinkedList<Double>();
        for (int t = 0; t < getProyectos().size(); t++) {
            PryVeh proyecto = getProyectos().get(t);
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

    public void llenarBeanSimulacionCapital(SimulacionCapital simulacion) {
        simulacion.setLineaCredito(Funciones.ponerComasCantidades(Double.parseDouble(Funciones.redondear(obtenerVariable("d35").toString(), 0))));
        simulacion.setTasaInteres(Double.valueOf(Funciones.redondear(String.valueOf(sacarSuma("veh_tsa_anu_crd_pte") / proyectos.size()), 3)));
        
        simulacion.setPorcentajeApalancamiento(Double.valueOf(Funciones.redondear(String.valueOf(sacarSuma("veh_por_apa") / proyectos.size()), 3)));
        simulacion.setPorcentajeCajaMinima(Double.valueOf(Funciones.redondear(String.valueOf(sacarSuma("veh_fac_cja_min") / proyectos.size()), 3)));
        simulacion.setPorcentajeCreditoPuente(Double.valueOf(Funciones.redondear(String.valueOf(sacarSuma("veh_por_crd_pte") / proyectos.size()), 3)));
        simulacion.setTirInv(Funciones.redondearDecimales(((Double) obtenerVariable("veh_cet_ptj_tir_inv")) * 100, 2));
        simulacion.setTirPry(Funciones.redondearDecimales(((Double) obtenerVariable("veh_cet_ptj_tir_pry")) * 100, 2));
        simulacion.setPorInv(Funciones.redondearDecimales(((Double) obtenerVariable("veh_cet_rep_uti_lim_prt_ptj")), 2));
        simulacion.setPorGrl(Funciones.redondearDecimales(((Double) obtenerVariable("veh_cet_rep_uti_grl_prt_ptj")), 2));
        simulacion.setTacometro(generarTacometro(simulacion, simulacion.getTirInv()));
        ponerValoresViviendas(simulacion);
        ponerCapital(simulacion);
        double valProbInc = probabilidadIncumplimiento==-1 ? 0.0 : probabilidadIncumplimiento;
        double valPorRec=porcentajeRecuperacion==-1? 0.0 :porcentajeRecuperacion;
        simulacion.setProbabilidadIncumplimiento(valProbInc);
        simulacion.setPorcentajeCasasIncumplimiento(valPorRec);
    }

    private double sacarSuma(String llave) {
        Object[] obtenerVariable = (Object[]) obtenerVariable(llave);
        double suma = 0;
        for (Object o : obtenerVariable) {
            suma += (Double) o;
        }
        return suma;
    }

    public void modificarSimulacionCapital(SimulacionCapital simulacion) {
        modificarVariable("veh_tsa_anu_crd_pte", simulacion.getTasaInteres());
        modificarVariable("veh_por_apa", simulacion.getPorcentajeApalancamiento());
        modificarVariable("veh_por_crd_pte", simulacion.getPorcentajeCreditoPuente());
        modificarVariable("veh_fac_cja_min", simulacion.getPorcentajeCajaMinima());
        modificarCapitalInversionista(simulacion);
        modificarPrecioVivienda(simulacion);
        this.probabilidadIncumplimiento=simulacion.getProbabilidadIncumplimiento();
        this.porcentajeRecuperacion=simulacion.getPorcentajeCasasIncumplimiento();
        modificarProbabilidadIncumplimiento(simulacion);
    }

    private void ponerValoresViviendas(SimulacionCapital simulacion) {
        Object[] obtenerVariable = (Object[]) obtenerVariable("veh_val_pro_viv");
        List<Parametros> parametros = new LinkedList<Parametros>();
        for (int t = 0; t < proyectos.size(); t++) {
            System.out.println("meto " + obtenerVariable[t]);
            String valor = Funciones.redondear(String.valueOf(obtenerVariable[t]), 2);
            parametros.add(new Parametros(proyectos.get(t).getNomPry(), Funciones.ponerComasCantidades(Double.parseDouble(valor))));
        }
        System.out.println("al final " + parametros.size());
        simulacion.setValorPromedioVivienda(Funciones.ponerComasCantidades(Math.round(sacarValorVivienda())));
        simulacion.setValorPromedioProyecto(parametros);
    }

    private void ponerCapital(SimulacionCapital simulacion) {
        double total = sacarSuma("veh_cap_inv");
        Object[] obtenerVariable = (Object[]) obtenerVariable("veh_cap_inv");
        double suma = 0;
        List<Parametros> parametros = new LinkedList<Parametros>();
        for (int t = 0; t < proyectos.size(); t++) {
            double actual = (Double) obtenerVariable[t];
            String valor = Funciones.redondear(String.valueOf((actual / total) * 100), 2);
            String cadena=proyectos.get(t).getTipMon().getTip();
            cadena= cadena.length()>5 ? "$"  : cadena;
            parametros.add(new Parametros(proyectos.get(t).getNomPry() +" ("+cadena+")", (Double.parseDouble(valor)) + "%"));
        }
        simulacion.setCapitalProyecto(parametros);
        simulacion.setCapitalInversionista(Funciones.ponerComasCantidades(Math.round(total)));
    }

    private void modificarVariable(String variable, Object valor) {
        Object[] arreglo = (Object[]) manejador.obtenerVariable(variable);
        for (int t = 0; t < proyectos.size(); t++) {
            arreglo[t] = valor;
        }
        manejador.guardarVariable(variable, arreglo);
    }

    private void modificarCapitalInversionista(SimulacionCapital simulacion) {
        double total = (Double) castearVAlor(simulacion.getCapitalInversionista());
        Object[] capital = (Object[]) manejador.obtenerVariable("veh_cap_inv");
        for (int t = 0; t < capital.length; t++) {
            
            double prop = simulacion.getCapitalProyecto().get(t).getRegistroIngreso().equals("") ? 0.0 : ((Double) castearVAlor(simulacion.getCapitalProyecto().get(t).getRegistroIngreso()) / 100);
            System.out.println("la proporcion es " + prop);
            capital[t] = prop * total;
            System.out.println("el nuevo capital al modificar "+proyectos.get(t).getNomPry() +  " es "+capital[t]);
        }
        manejador.guardarVariable("veh_cap_inv", capital);
    }

    private void modificarPrecioVivienda(SimulacionCapital simulacion) {
        if (simulacion.isValorGeneralModificado()) {
            Object[] obtenerVariable = (Object[]) manejador.obtenerVariable("veh_val_pro_viv");
            double total = sacarValorVivienda();
            for (int t = 0; t < obtenerVariable.length; t++) {
                double proporcion = Double.parseDouble(String.valueOf(castearVAlor(simulacion.getValorPromedioProyecto().get(t).getRegistroIngreso()))) / total;
                obtenerVariable[t] = Funciones.redondear(String.valueOf(Double.parseDouble(String.valueOf(castearVAlor(simulacion.getValorPromedioVivienda()))) * proporcion), 0);
                System.out.println("el nuevo valor de la vivienda en " + t + " es " + obtenerVariable[t]);
            }
            manejador.guardarVariable("veh_val_pro_viv", obtenerVariable);
        } else {
            Object[] obtenerVariable = (Object[]) manejador.obtenerVariable("veh_val_pro_viv");
            for (int t = 0; t < simulacion.getValorPromedioProyecto().size(); t++) {
                obtenerVariable[t] = castearVAlor(simulacion.getValorPromedioProyecto().get(t).getRegistroIngreso());
            }
            manejador.guardarVariable("veh_val_pro_viv", obtenerVariable);
        }
    }

    
    private void modificarProbabilidadIncumplimiento(SimulacionCapital simulacion) {
        System.out.println("jaz");
        Object[] obtenerVariable = (Object[]) manejador.obtenerVariable("veh_val_pro_viv");
        for(int t=0;t<simulacion.getValorPromedioProyecto().size();t++){
            double valorEsperanza=Funciones.calcularValorViviendaEsperanza(obtenerVariable[t],simulacion.getProbabilidadIncumplimiento(),simulacion.getPorcentajeCasasIncumplimiento());
            System.out.println("el valor de la esperanza es "+valorEsperanza+" la prob inc= "+probabilidadIncumplimiento+" por rec="+porcentajeRecuperacion);
            obtenerVariable[t]=Funciones.redondear(String.valueOf(valorEsperanza), 0);
        }
        manejador.guardarVariable("veh_val_pro_viv", obtenerVariable);
    }
    
}
