package calculador.vehiculo;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import pojos.PryVeh;
import util.CeldaFechaValor;
import util.Funciones;
import util.MatrizBidimensional;
import util.vehiculo.ListaEtapasVehiculo;
import variables.ManejadorVariablesVehiculo;

/**
 *Clase que nos hace la pestana del Estado de Resultados
 * @author alberto
 */
public class EstadoResultados {

    /**
     * Manejador de variables 
     */
    private final ManejadorVariablesVehiculo manejador;
    private final List<PryVeh> proyectos;
    private Date fechaFinal;
    private Date fechaInicial;

    public EstadoResultados(List<PryVeh> proyectos, ManejadorVariablesVehiculo manejador) {
        this.manejador = manejador;
        this.proyectos = proyectos;
        this.fechaInicial = obtenerFechaInicial();
        this.fechaFinal = obtenerFechaFinal();

    }

    /**
     * metodo que procesa el estado de resultados
     */
    public void procesar() {
        manejador.guardarVariable("veh_total_utl_oprt", restarMatrices("veh_cet_ing", "veh_cet_cto_vta"));
        manejador.guardarVariable("veh_gst_opt", sumarMatrices("veh_cet_gav", "veh_cet_gas_ind", "veh_cet_adm_pry"));
        manejador.guardarVariable("tmp_edo_gas_ing", restarMatrices("veh_total_utl_oprt", "veh_gst_opt"));
        manejador.guardarVariable("veh_utl_a_ipt", restarMatrices("tmp_edo_gas_ing", "veh_cet_int_cre_pte"));
        manejador.guardarVariable("veh_imp_sob_rta", calcularImpuestoSobreRenta());
        manejador.guardarVariable("veh_utl_net", restarMatrices("veh_utl_a_ipt", "veh_imp_sob_rta"));
        manejador.guardarVariable("veh_utl_ret", calcularUtilidadRetenida());
        manejador.guardarVariable("veh_gst_opt_+_ins_/_ing_tot", calcularGasOp());
        calcularDatosAnuales();
        calcularDatosGraficacion();
    }

    private void calcularDatosGraficacion() {
        //a+b+c significa que el resultado es la suma de esas cosas
        //a#b a-b
        //]a significa que a se repetira todo el tiempo
        //<a significa que si tenemos la lista={a,b,c} el resultado sera {a , a+b ,a+b+c}
        String[] nombreVars = new String[]{"<veh_cet_ing", "<veh_cet_cto_vta", "<veh_total_utl_oprt",
            "<veh_gst_opt", "<veh_cet_int_cre_pte", "<veh_utl_a_ipt", "<veh_imp_sob_rta",
            "<veh_utl_net"};

        String[] nombreGraficas = new String[]{"veh_edo_vta_viv_anu", "veh_edo_cto_vta_anu", "veh_total_utl_oprt_anu",
            "veh_gst_opt_anu", "veh_cet_int_cre_pte_anu", "veh_utl_a_ipt_anu", "veh_imp_sob_rta_anu",
            "veh_utl_net_anu"};
        //falta retenida y patrimonio 
        for (int t = 0; t < nombreVars.length; t++) {
            String normal = nombreVars[t];
            String grafi = nombreGraficas[t] + "_graf";
            if (normal.contains("%")) {
                manejador.guardarVariable(grafi, sumarMatrices(normal.split("%")));
            } else if (normal.contains("<")) {
                manejador.guardarVariable(grafi, sumaAcumulada(normal.split("<")[1]));
                System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
                manejador.obtenerVariable(grafi);
                System.out.println(manejador.obtenerVariable(grafi));
            } else {
                manejador.guardarVariable(grafi, manejador.obtenerVariable(normal));
            }
        }
    }

    private MatrizBidimensional sumaAcumulada(String llave) {
        List<CeldaFechaValor> celdas = new LinkedList<CeldaFechaValor>();
        MatrizBidimensional obtenerVariable = (MatrizBidimensional) manejador.obtenerVariable(llave);
        double suma = 0;
        int consecutivo=0;
        for (CeldaFechaValor c : obtenerVariable.getCeldas()) {
            suma=consecutivo%12==0 ? 0.0 : suma;
            suma += c.getValor();
            celdas.add(new CeldaFechaValor(c.getFecha(), suma));
           // consecutivo++;
        }
        MatrizBidimensional regreso = new MatrizBidimensional();
        regreso.setCeldas(celdas);
        return regreso;
    }

    private MatrizBidimensional restarMatrices(String primero, String segundo) {
        MatrizBidimensional m1 = (MatrizBidimensional) manejador.obtenerVariable(primero);
        MatrizBidimensional m2 = (MatrizBidimensional) manejador.obtenerVariable(segundo);
        List<CeldaFechaValor> lista = new LinkedList<CeldaFechaValor>();
        for (int t = 0; t < m1.getCeldas().size(); t++) {
            lista.add(new CeldaFechaValor(m1.getCeldas().get(t).getFecha(), m1.getCeldas().get(t).getValor() - m2.getCeldas().get(t).getValor()));
        }
        MatrizBidimensional m = new MatrizBidimensional();
        m.setCeldas(lista);
        return m;
    }

    private MatrizBidimensional sumarMatrices(String... llaves) {
        this.fechaInicial = obtenerFechaInicial();
        List<MatrizBidimensional> listaMatrices = new LinkedList<MatrizBidimensional>();
        for (String s : llaves) {

            listaMatrices.add((MatrizBidimensional) manejador.obtenerVariable(s));
        }
        int[] indices = new int[listaMatrices.size()];
        Date copiaInicial = this.fechaInicial;
        List<CeldaFechaValor> lista = new LinkedList<CeldaFechaValor>();
        while ((copiaInicial.getMonth() != (fechaFinal.getMonth()) || copiaInicial.getYear() != fechaFinal.getYear())) {
            double suma = 0;
            for (int indiceMatrices = 0; indiceMatrices < listaMatrices.size(); indiceMatrices++) {
                MatrizBidimensional actual = listaMatrices.get(indiceMatrices);
                if (actual.getCeldas().size() > indices[indiceMatrices]
                        && actual.getCeldas().get(indices[indiceMatrices]).getFecha().getTime().getMonth() == fechaInicial.getMonth()
                        && actual.getCeldas().get(indices[indiceMatrices]).getFecha().getTime().getYear() == fechaInicial.getYear()) {
                    suma += actual.getCeldas().get(indices[indiceMatrices]).getValor();
                    indices[indiceMatrices] = indices[indiceMatrices] + 1;
                }
            }
            Calendar instancia = Calendar.getInstance();
            instancia.setTime(copiaInicial);
            CeldaFechaValor nueva = new CeldaFechaValor(instancia, Funciones.redondearDecimales(suma, 3));
            copiaInicial.setMonth(copiaInicial.getMonth() + 1);
            lista.add(nueva);
        }
        Calendar instancia = Calendar.getInstance();
        instancia.setTime(fechaFinal);
        CeldaFechaValor celda = generarUltimaCelda(instancia, listaMatrices, indices);
        lista.add(celda);
        MatrizBidimensional m = new MatrizBidimensional();
        m.setCeldas(lista);
        return m;
    }

    private CeldaFechaValor generarUltimaCelda(Calendar instancia, List<MatrizBidimensional> listaMatrices, int[] indices) {
        double suma = 0;
        for (int indiceMatriz = 0; indiceMatriz < listaMatrices.size(); indiceMatriz++) {
            MatrizBidimensional actual = listaMatrices.get(indiceMatriz);

            if (actual.getCeldas().size() > indices[indiceMatriz]
                    && actual.getCeldas().get(indices[indiceMatriz]).getFecha().getTime().getMonth() == fechaFinal.getMonth()
                    && actual.getCeldas().get(indices[indiceMatriz]).getFecha().getTime().getYear() == fechaFinal.getYear()) {
                suma += actual.getCeldas().get(indices[indiceMatriz]).getValor();
            }
        }
        return new CeldaFechaValor(instancia, (suma));
    }

    private Date obtenerFechaInicial() {
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

    private Date obtenerFechaFinal() {
        ListaEtapasVehiculo[] etapasProyectos = (ListaEtapasVehiculo[]) manejador.obtenerVariable("veh_eta");
        Date fechaMayor = null;
        for (ListaEtapasVehiculo listaEtapas : etapasProyectos) {
            int numeroEtapas = listaEtapas.getListaEtapas().size();
            for (int indiceEtapa = 0; indiceEtapa < numeroEtapas; indiceEtapa++) {
                Date fechaActual = (listaEtapas.getListaEtapas().get(indiceEtapa).getFechaFinEtapa());
                fechaMayor = fechaMayor == null ? fechaActual : fechaMayor.compareTo(fechaActual) < 0 ? fechaActual : fechaMayor;
            }
        }
        return (Date) fechaMayor.clone();
    }

    private MatrizBidimensional calcularImpuestoSobreRenta() {
        List<CeldaFechaValor> celdas = new LinkedList<CeldaFechaValor>();
        MatrizBidimensional utilidadAntesImpuestos = (MatrizBidimensional) manejador.obtenerVariable("veh_utl_a_ipt");
        double primerValor = utilidadAntesImpuestos.getCeldas().get(0).getValor() > 0 ? utilidadAntesImpuestos.getCeldas().get(0).getValor() : 0;
        CeldaFechaValor c = new CeldaFechaValor(utilidadAntesImpuestos.getCeldas().get(0).getFecha(), Funciones.redondearDecimales(primerValor * .28, 3));
        double sumaAntesImpuestos = utilidadAntesImpuestos.getCeldas().get(0).getValor();
        double sumaImpuestosRenta = primerValor;
        celdas.add(c);
        for (int t = 1; t < utilidadAntesImpuestos.getCeldas().size(); t++) {
            double utiAntImp = utilidadAntesImpuestos.getCeldas().get(t).getValor();
            sumaAntesImpuestos += utiAntImp;
            double impRen = sumaAntesImpuestos > 0 ? ((sumaAntesImpuestos * .28) - sumaImpuestosRenta) : 0;
            sumaImpuestosRenta += impRen;

            CeldaFechaValor celda = new CeldaFechaValor(utilidadAntesImpuestos.getCeldas().get(t).getFecha(), impRen);
            celdas.add(celda);
        }
        MatrizBidimensional m = new MatrizBidimensional();
        m.setCeldas(celdas);
        return m;
    }

    private MatrizBidimensional calcularGasOp() {
        MatrizBidimensional gastos = (MatrizBidimensional) manejador.obtenerVariable("veh_gst_opt");
        MatrizBidimensional intereses = (MatrizBidimensional) manejador.obtenerVariable("veh_cet_int_cre_pte");
        MatrizBidimensional ingresos = (MatrizBidimensional) manejador.obtenerVariable("veh_cet_ing");
        List<CeldaFechaValor> celdas = new LinkedList<CeldaFechaValor>();
        for (int t = 0; t < gastos.getCeldas().size(); t++) {
            double valor = (gastos.getCeldas().get(t).getValor() + intereses.getCeldas().get(t).getValor()) / ingresos.getCeldas().get(t).getValor();
            CeldaFechaValor celda = new CeldaFechaValor(gastos.getCeldas().get(t).getFecha(), valor * 100);
            celdas.add(celda);
        }
        MatrizBidimensional m = new MatrizBidimensional();
        m.setCeldas(celdas);
        return m;
    }

    private Object calcularUtilidadRetenida() {
        MatrizBidimensional reparto = (MatrizBidimensional) manejador.obtenerVariable("veh_cet_rep_uti");
        MatrizBidimensional utilidad = (MatrizBidimensional) manejador.obtenerVariable("veh_utl_net");
        List<CeldaFechaValor> celdas = new LinkedList<CeldaFechaValor>();
        celdas.add(new CeldaFechaValor(reparto.getCeldas().get(0).getFecha(), 0));
        for (int t = 1; t < reparto.getCeldas().size(); t++) {
            celdas.add(new CeldaFechaValor(reparto.getCeldas().get(t).getFecha(), celdas.get(t - 1).getValor() + utilidad.getCeldas().get(t - 1).getValor() - reparto.getCeldas().get(t).getValor()));
        }
        MatrizBidimensional m = new MatrizBidimensional();
        m.setCeldas(celdas);
        return m;
    }

    private void calcularDatosAnuales() {
        String[] nombreAnual = new String[]{"veh_edo_vta_viv_anu", "veh_edo_cto_vta_anu", "veh_total_utl_oprt_anu",
            "veh_gst_opt_anu", "veh_cet_int_cre_pte_anu", "veh_utl_a_ipt_anu", "veh_imp_sob_rta_anu",
            "veh_utl_net_anu"};
        String[] nombreMensual = new String[]{"veh_cet_ing", "veh_cet_cto_vta", "veh_total_utl_oprt", "veh_gst_opt", "veh_cet_int_cre_pte", "veh_utl_a_ipt", "veh_imp_sob_rta",
            "veh_utl_net"};
        for (int t = 0; t < nombreAnual.length; t++) {
            List<Double> lista = generarLista(nombreMensual[t]);
            manejador.guardarVariable(nombreAnual[t], lista);
        }
    }

    private double sumarValoresMatriz(MatrizBidimensional m, int liminf, int limsup) {
        double suma = 0;
        for (int t = liminf; t <= limsup; t++) {
            if (t < m.getCeldas().size()) {
                suma += m.getCeldas().get(t).getValor();

            }
        }
        return suma;
    }

    private List<Double> generarLista(String nombreMensual) {
        List<Double> utilidadPeriodo = new LinkedList<Double>();
        utilidadPeriodo.add(sumarValoresMatriz((MatrizBidimensional) manejador.obtenerVariable(nombreMensual), 0, 11));
        utilidadPeriodo.add(sumarValoresMatriz((MatrizBidimensional) manejador.obtenerVariable(nombreMensual), 12, 23));
        utilidadPeriodo.add(sumarValoresMatriz((MatrizBidimensional) manejador.obtenerVariable(nombreMensual), 24, 35));
        utilidadPeriodo.add(sumarValoresMatriz((MatrizBidimensional) manejador.obtenerVariable(nombreMensual), 36, 47));
        utilidadPeriodo.add(sumarValoresMatriz((MatrizBidimensional) manejador.obtenerVariable(nombreMensual), 48, 59));
        return utilidadPeriodo;
    }
}
