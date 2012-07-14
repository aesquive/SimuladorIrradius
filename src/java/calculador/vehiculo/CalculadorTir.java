package calculador.vehiculo;

import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import pojos.EtpTirVeh;
import pojos.PryVeh;
import satpathy.financial.XIRR;
import satpathy.financial.XIRRData;
import util.CeldaFechaValor;
import util.Funciones;
import util.MatrizBidimensional;
import util.vehiculo.ListaEtapasVehiculo;
import variables.ManejadorVariablesVehiculo;

/**
 *
 * @author alberto
 */
public class CalculadorTir {

    private List<PryVeh> proyectos;
    private ManejadorVariablesVehiculo manejador;
    private Date fechaInicial;
    private Date fechaFinal;
    private int tiempoMinistracion;
    private int tiempoPago;

    public CalculadorTir(int tiempoMinistracion, int tiempoPago, List<PryVeh> proyectos, ManejadorVariablesVehiculo manejador) {
        this.proyectos = proyectos;
        this.manejador = manejador;
        this.tiempoMinistracion = tiempoMinistracion;
        this.tiempoPago = tiempoPago;
    }

    /**
     * procesa cuando hay 1 o mas proyectos
     */
    public void procesarPrincipal(boolean procesarIndividual) {
        manejador.guardarVariable("veh_cet_tir_inv", calcularTirInversionista("veh_cet_gav", "veh_cet_adm_pry"));
        manejador.guardarVariable("veh_cet_sal_cap_inv", calcularSaldoCapitalInversionista());
        if (procesarIndividual) {
            modelarIndividualmente();

        }
        manejador.guardarVariable("veh_cet_tir_inv", calcularTirInversionista("veh_cet_gav", "veh_cet_adm_pry"));
        manejador.guardarVariable("veh_cet_sal_cap_inv", calcularSaldoCapitalInversionista());
        manejador.guardarVariable("veh_cet_flu_tir", calcularFlujoTir());
        calcularEscalonesTirGeneral();
        continuacionProcesoGeneral();
    }

    private MatrizBidimensional calcularTirInversionista(String primero, String segundo) {
        MatrizBidimensional m1 = (MatrizBidimensional) manejador.obtenerVariable(primero);
        MatrizBidimensional m2 = (MatrizBidimensional) manejador.obtenerVariable(segundo);
        List<CeldaFechaValor> lista = new LinkedList<CeldaFechaValor>();
        for (int t = 0; t < m1.getCeldas().size(); t++) {
            lista.add(new CeldaFechaValor(m1.getCeldas().get(t).getFecha(), -m1.getCeldas().get(t).getValor() - m2.getCeldas().get(t).getValor()));
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
        fechaFinal = obtenerFechaFinal();
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
        return new CeldaFechaValor(instancia, Funciones.redondearDecimales(suma, 3));
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

    private Object calcularSaldoCapitalInversionista() {
        double nuevoSaldoPrimero = (-1) * sumarValores("veh_cap_inv");
        MatrizBidimensional aportacion = voltearSignos((MatrizBidimensional) manejador.obtenerVariable("veh_cet_apt"));
        MatrizBidimensional retiro = (MatrizBidimensional) manejador.obtenerVariable("veh_cet_ret");
        List<CeldaFechaValor> lista = new LinkedList<CeldaFechaValor>();
        for (int indice = 0; indice < aportacion.getCeldas().size(); indice++) {
            double val = indice < retiro.getCeldas().size() ? retiro.getCeldas().get(indice).getValor() : 0.0;
            double valor = aportacion.getCeldas().get(indice).getValor() + retiro.getCeldas().get(indice).getValor() + nuevoSaldoPrimero;
            lista.add(new CeldaFechaValor(aportacion.getCeldas().get(indice).getFecha(), valor));
            nuevoSaldoPrimero = valor;
        }
        MatrizBidimensional m = new MatrizBidimensional();
        m.setCeldas(lista);
        return m;
    }

    private double sumarValores(String llave) {
        Object[] valoresLlave = (Object[]) manejador.obtenerVariable(llave);
        double suma = 0;
        for (Object ob : valoresLlave) {
            Double numero = Double.parseDouble(String.valueOf(ob));
            suma += numero;
        }
        return suma;
    }

    private MatrizBidimensional voltearSignos(MatrizBidimensional m) {
        List<CeldaFechaValor> celdas = new LinkedList<CeldaFechaValor>();
        for (CeldaFechaValor c : m.getCeldas()) {
            celdas.add(new CeldaFechaValor(c.getFecha(), (-1) * c.getValor()));
        }
        MatrizBidimensional ret = new MatrizBidimensional();
        ret.setCeldas(celdas);
        return ret;
    }

    private void modelarIndividualmente() {
        int indice = 0;
        for (PryVeh proyecto : proyectos) {
            List<PryVeh> lproyecto = new LinkedList<PryVeh>();
            lproyecto.add(proyecto);
            DistribuidorModeloVehiculo nuevo = new DistribuidorModeloVehiculo(lproyecto, false, tiempoMinistracion, tiempoPago);
            nuevo.setTiempoMinistracion(tiempoMinistracion);
            nuevo.setTiempoPago(tiempoPago);
            nuevo.modelarIndividual(nuevo.getManejador(), proyecto);
            manejador.guardarVariable("dis" + indice, nuevo);
            indice++;
        }
    }

    /**
     * se encarga de modelar la parte de los escalones de tir individualmente
     */
    public void procesarIndividual() {
        manejador.guardarVariable("veh_cet_tir_inv", calcularTirInversionista("veh_cet_gav", "veh_cet_adm_pry"));
        manejador.guardarVariable("veh_cet_sal_cap_inv", calcularSaldoCapitalInversionista());
        manejador.guardarVariable("veh_cet_flu_tir", calcularFlujoTir());
        calcularEscalonesTir();
        continuacionProcesoGeneral();
    }

    private void calcularEscalonesTir() {
        PryVeh proyectoActual = proyectos.get(0);
        Set<EtpTirVeh> etpTirVehs = proyectoActual.getEtpTirVehs();
        int numeroEscalones = etpTirVehs.size() / 2;
        for (int t = 0; t < numeroEscalones; t++) {
            double tirMinima = Double.valueOf(String.valueOf(sacarValorEscalonTir(25, t, etpTirVehs))) * .01;
            double disInv = Double.valueOf(String.valueOf(sacarValorEscalonTir(26, t, etpTirVehs))) * .01;
            manejador.guardarVariable("veh_cet_pag_tir_min" + t, calcularTirMinima(t, tirMinima));

            manejador.guardarVariable("veh_cet_par_uti_lp" + t, calcularParticipacionUtilidades(t, numeroEscalones, disInv));
            manejador.guardarVariable("veh_cet_par_uti_gp" + t, calcularPaticipacionGeneralPartner(t, numeroEscalones, (1 - disInv)));
            manejador.guardarVariable("veh_cet_pag_req_tir" + t, sumarMatrices("veh_cet_pag_tir_min" + t, "veh_cet_apt", "veh_cet_ret"));
        }
        calcularRetornoInversionista(numeroEscalones);
        calcularRetornoGeneralPartner(numeroEscalones);
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

    private MatrizBidimensional calcularTirMinima(int indice, double tirMinima) {
        MatrizBidimensional flujoTir = (MatrizBidimensional) manejador.obtenerVariable("veh_cet_flu_tir");
        List<CeldaFechaValor> lista = new LinkedList<CeldaFechaValor>();
        for (CeldaFechaValor c : flujoTir.getCeldas()) {
            lista.add(new CeldaFechaValor(c.getFecha(), tirMinima * c.getValor()));
        }
        MatrizBidimensional m = new MatrizBidimensional();
        m.setCeldas(lista);
        return m;
    }

    private MatrizBidimensional calcularParticipacionUtilidades(int indice, int numeroEscalones, double disInv) {
        MatrizBidimensional pagoTir;
        if (indice == 0) {
            pagoTir = (MatrizBidimensional) manejador.obtenerVariable("veh_cet_pag_tir_min" + indice);
        } else if (indice == (numeroEscalones - 1)) {
            pagoTir = restarMatrices("veh_cet_flu_tir", "veh_cet_pag_tir_min" + (indice - 1));
        } else {
            int indiceMenor = indice - 1;
            pagoTir = restarMatrices("veh_cet_pag_tir_min" + indice, "veh_cet_pag_tir_min" + indiceMenor);
        }
        List<CeldaFechaValor> celdas = new LinkedList<CeldaFechaValor>();
        for (CeldaFechaValor c : pagoTir.getCeldas()) {
            celdas.add(new CeldaFechaValor(c.getFecha(), c.getValor() * disInv));
        }
        MatrizBidimensional m = new MatrizBidimensional();
        m.setCeldas(celdas);
        return m;

    }

    private void calcularRetornoInversionista(int numeroEscalonesTir) {
        String[] arreglo = new String[numeroEscalonesTir];
        for (int t = 0; t < numeroEscalonesTir; t++) {
            arreglo[t] = "veh_cet_par_uti_lp" + t;
        }
        manejador.guardarVariable("veh_cet_rep_uti_lim_prt", sumarMatrices(arreglo));
    }

    private void calcularRetornoGeneralPartner(int numeroEscalonesTir) {
        String[] arreglo = new String[numeroEscalonesTir];
        for (int t = 0; t < numeroEscalonesTir; t++) {
            arreglo[t] = "veh_cet_par_uti_gp" + t;
        }
        manejador.guardarVariable("veh_cet_rep_uti_grl_prt", sumarMatrices(arreglo));
    }

    private MatrizBidimensional calcularPaticipacionGeneralPartner(int indice, int numeroEscalones, double disInv) {
        MatrizBidimensional pagoTir;
        if (indice == 0) {
            pagoTir = restarMatrices("veh_cet_pag_tir_min" + indice, "veh_cet_par_uti_lp" + indice);
            disInv = 1;
        } else if (indice == (numeroEscalones - 1)) {
            pagoTir = restarMatrices("veh_cet_flu_tir", "veh_cet_pag_tir_min" + (indice - 1));
        } else {
            int indiceMenor = indice - 1;
            pagoTir = restarMatrices("veh_cet_pag_tir_min" + indice, "veh_cet_pag_tir_min" + indiceMenor);
        }
        List<CeldaFechaValor> celdas = new LinkedList<CeldaFechaValor>();
        for (CeldaFechaValor c : pagoTir.getCeldas()) {
            celdas.add(new CeldaFechaValor(c.getFecha(), c.getValor() * disInv));
        }
        MatrizBidimensional m = new MatrizBidimensional();
        m.setCeldas(celdas);
        return m;
    }

    private void continuacionProcesoGeneral() {
        manejador.guardarVariable("veh_cet_rep_uti", sumarMatrices("veh_cet_rep_uti_lim_prt", "veh_cet_rep_uti_grl_prt"));
        calcularRepartoUtilidades();
        manejador.guardarVariable("veh_cet_res_liq", calcularLiquidez());
        calcularCajaInicialFinal();
//        manejador.guardarVariable("tmp",sumarMatrices("veh_cet_cja_ini","veh_cet_mov_net","veh_cet_cap"));
//        manejador.guardarVariable("veh_cet_cja_fin", restarMatrices("tmp","veh_cet_rep_uti"));
        manejador.guardarVariable("veh_cet_tir_pry", calcularMatrizTirProyecto());
        manejador.guardarVariable("veh_cet_ptj_tir_pry", sacarTir((MatrizBidimensional) manejador.obtenerVariable("veh_cet_tir_pry")));
    }

    private MatrizBidimensional calcularLiquidez() {
        Object[] cajaMinima = (Object[]) manejador.obtenerVariable("veh_fac_cja_min");
        Double cjaMin = Double.parseDouble(String.valueOf(cajaMinima[0])) / 100;
        MatrizBidimensional saldo = (MatrizBidimensional) manejador.obtenerVariable("veh_cet_sal_cre");
        List<CeldaFechaValor> celdas = new LinkedList<CeldaFechaValor>();
        for (CeldaFechaValor c : saldo.getCeldas()) {
            celdas.add(new CeldaFechaValor(c.getFecha(), c.getValor() * cjaMin));
        }
        MatrizBidimensional m = new MatrizBidimensional();
        m.setCeldas(celdas);
        return m;
    }

    private MatrizBidimensional calcularMatrizTirProyecto() {
        MatrizBidimensional capital = moverIzquierda1((MatrizBidimensional) manejador.obtenerVariable("veh_cet_cap"));
        MatrizBidimensional utilidades = (MatrizBidimensional) manejador.obtenerVariable("veh_cet_rep_uti");
        List<CeldaFechaValor> celdas = new LinkedList<CeldaFechaValor>();
        for (int t = 0; t < capital.getCeldas().size(); t++) {
            double valUtilidades = t < utilidades.getCeldas().size() ? utilidades.getCeldas().get(t).getValor() : 0.0;
            celdas.add(new CeldaFechaValor(capital.getCeldas().get(t).getFecha(), (-1 * capital.getCeldas().get(t).getValor()) + valUtilidades));
        }
        MatrizBidimensional m = new MatrizBidimensional();
        m.setCeldas(celdas);
        return m;
    }

    private MatrizBidimensional calcularFlujoTir() {
        MatrizBidimensional pago = (MatrizBidimensional) manejador.obtenerVariable("veh_cet_pgo_cre_pte");
        List<CeldaFechaValor> celdas = new LinkedList<CeldaFechaValor>();
        for (int t = 0; t < pago.getCeldas().size() - 1; t++) {
            celdas.add(new CeldaFechaValor(pago.getCeldas().get(t).getFecha(), pago.getCeldas().get(t).getValor()));
        }
        double sumaMargenReserva = sumarValoresMatriz((MatrizBidimensional) manejador.obtenerVariable("veh_cet_mar_res_veh"));
        MatrizBidimensional tmp = new MatrizBidimensional();
        tmp.setCeldas(celdas);
        double sumaFlujo = sumarValoresMatriz(tmp);
        Date fecha = obtenerFechaFinal();
        Calendar instance = Calendar.getInstance();
        instance.setTime(fecha);
        celdas.add(new CeldaFechaValor(instance, sumaMargenReserva - sumaFlujo));
        tmp.setCeldas(celdas);
        return tmp;
    }

    private double sumarValoresMatriz(MatrizBidimensional string) {
        double suma = 0;
        for (CeldaFechaValor c : string.getCeldas()) {
            suma += c.getValor();
        }
        return suma;
    }

    private MatrizBidimensional moverIzquierda1(MatrizBidimensional matrizBidimensional) {
        List<CeldaFechaValor> celdas = new LinkedList<CeldaFechaValor>();
        for (int t = 1; t < matrizBidimensional.getCeldas().size(); t++) {
            celdas.add(new CeldaFechaValor(matrizBidimensional.getCeldas().get(t - 1).getFecha(), matrizBidimensional.getCeldas().get(t).getValor()));
        }
        MatrizBidimensional m = new MatrizBidimensional();
        m.setCeldas(celdas);
        return m;
    }

    private double sacarTir(MatrizBidimensional matrizBidimensional) {
        double creditoTotal = sumarValores("veh_cap_inv");
        double[] valores = new double[matrizBidimensional.getCeldas().size() + 1];
        double[] fechas = new double[matrizBidimensional.getCeldas().size() + 1];
        valores[0] = (-1) * creditoTotal;
        Date fecInicial = obtenerFechaInicial();
        fecInicial.setMonth(fecInicial.getMonth() - 1);
        Calendar instance = Calendar.getInstance();
        instance.setTime(fecInicial);
        fechas[0] = XIRRData.getExcelDateValue(instance);
        for (int t = 0; t < matrizBidimensional.getCeldas().size(); t++) {
            CeldaFechaValor actual = matrizBidimensional.getCeldas().get(t);
            valores[t + 1] = actual.getValor();
            fechas[t + 1] = XIRRData.getExcelDateValue(actual.getFecha());
        }
        XIRRData data = new XIRRData(matrizBidimensional.getCeldas().size() + 1, .3, valores, fechas);
        double xirrValue = XIRR.xirr(data);
        return xirrValue;
    }

    private Object sumarDistribuidores(String llave) {
        List<MatrizBidimensional> matrices = new LinkedList<MatrizBidimensional>();
        for (int t = 0; t < proyectos.size(); t++) {
            DistribuidorModeloVehiculo distribuidor = (DistribuidorModeloVehiculo) manejador.obtenerVariable("dis" + t);
            matrices.add((MatrizBidimensional) distribuidor.getManejador().obtenerVariable(llave));
        }
        return sumarMatrices(matrices);
    }

    public MatrizBidimensional sumarMatrices(List<MatrizBidimensional> listaMatrices) {

        int[] indices = new int[listaMatrices.size()];
        this.fechaInicial = obtenerFechaInicial();
        Date copiaInicial = this.fechaInicial;
        List<CeldaFechaValor> lista = new LinkedList<CeldaFechaValor>();
        fechaFinal = obtenerFechaFinal();
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

    private double sumarDistribuidoresTir(String string) {
        double suma = 0;
        for (int indiceProyecto = 0; indiceProyecto < proyectos.size(); indiceProyecto++) {
            DistribuidorModeloVehiculo dist = (DistribuidorModeloVehiculo) manejador.obtenerVariable("dis" + indiceProyecto);
            suma += (Double) dist.getManejador().obtenerVariable(string);
        }
        return suma / proyectos.size();
    }

    private void calcularEscalonesTirGeneral() {
        PryVeh proyectoActual = proyectos.get(0);
        Set<EtpTirVeh> etpTirVehs = proyectoActual.getEtpTirVehs();
        int numeroEscalones = etpTirVehs.size() / 2;
        for (int t = 0; t < numeroEscalones; t++) {
            double tirReal = Double.valueOf(String.valueOf(sacarValorEscalonTir(25, t, etpTirVehs))) * .01;
            System.out.println(" la tir real es " + tirReal);
            double tirObtenida = 0;
            double tirMinima = .01;
            while ((tirReal - tirObtenida) > .001) {
                double disInv = Double.valueOf(String.valueOf(sacarValorEscalonTir(26, t, etpTirVehs))) * .01;
                manejador.guardarVariable("veh_cet_pag_tir_min" + t, calcularTirMinima(t, tirMinima));
                manejador.guardarVariable("veh_cet_par_uti_lp" + t, calcularParticipacionUtilidades(t, numeroEscalones, disInv));
                manejador.guardarVariable("veh_cet_par_uti_gp" + t, calcularPaticipacionGeneralPartner(t, numeroEscalones, (1 - disInv)));
                manejador.guardarVariable("tmpEsc", moverDerecha((MatrizBidimensional) manejador.obtenerVariable("veh_cet_ret")));
                manejador.guardarVariable("veh_cet_pag_req_tir" + t, sumarWaterFall("veh_cet_pag_tir_min" + t, "veh_cet_apt", "veh_cet_ret"));
                tirObtenida = sacarTir((MatrizBidimensional) manejador.obtenerVariable("veh_cet_pag_req_tir" + t));
                tirMinima = tirMinima * (tirReal / tirObtenida);
                System.out.println("la tir minima es " + tirMinima + " en t =" + t);
                manejador.guardarVariable("veh_cet_uti" + t, tirMinima);
                if(tirMinima==0.0){
                    System.out.println("llllllllllllllllllllllllllllllllllllllllllllllll");
                    break;
                }
            }
        }
        calcularRetornoInversionista(numeroEscalones);
        calcularRetornoGeneralPartner(numeroEscalones);
        calcularTirInversionista(numeroEscalones);
    }

    private void calcularRepartoUtilidades() {
        MatrizBidimensional limprt = (MatrizBidimensional) manejador.obtenerVariable("veh_cet_rep_uti_lim_prt");
        MatrizBidimensional grl = (MatrizBidimensional) manejador.obtenerVariable("veh_cet_rep_uti_grl_prt");
        double sumaLim = 0;
        double sumaGrl = 0;
        for (CeldaFechaValor c : limprt.getCeldas()) {
            sumaLim += c.getValor();
        }
        for (CeldaFechaValor c : grl.getCeldas()) {
            sumaGrl += c.getValor();
        }
        double total = sumaLim + sumaGrl;
        manejador.guardarVariable("veh_cet_rep_uti_grl_prt_ptj", (sumaGrl / total) * 100);
        manejador.guardarVariable("veh_cet_rep_uti_lim_prt_ptj", (100 - (sumaGrl / total) * 100));
    }

    private void calcularCajaInicialFinal() {
        double capital = sumarValores("veh_cap_inv");
        fechaInicial = obtenerFechaInicial();
        MatrizBidimensional capitalInversionista = moverIzquierda1((MatrizBidimensional) manejador.obtenerVariable("veh_cet_cap"));
        MatrizBidimensional movimientoNeto = (MatrizBidimensional) manejador.obtenerVariable("veh_cet_mov_net");
        MatrizBidimensional reparto = (MatrizBidimensional) manejador.obtenerVariable("veh_cet_rep_uti");

        List<CeldaFechaValor> cajaInicial = new LinkedList<CeldaFechaValor>();
        List<CeldaFechaValor> cajaFinal = new LinkedList<CeldaFechaValor>();
        cajaInicial.add(new CeldaFechaValor(reparto.getCeldas().get(0).getFecha(), capital));
        for (int t = 0; t < reparto.getCeldas().size(); t++) {
            double capInv = t >= capitalInversionista.getCeldas().size() ? 0.0 : capitalInversionista.getCeldas().get(t).getValor();
            double valor = cajaInicial.get(t).getValor() + movimientoNeto.getCeldas().get(t).getValor()
                    + capInv - reparto.getCeldas().get(t).getValor();
            valor = valor > 0 ? valor : 0;
            CeldaFechaValor nueva = new CeldaFechaValor(reparto.getCeldas().get(t).getFecha(), valor);
            cajaFinal.add(nueva);
            cajaInicial.add(nueva);
        }
        cajaInicial.remove(cajaInicial.size() - 1);
        MatrizBidimensional inicial = new MatrizBidimensional();
        inicial.setCeldas(cajaInicial);
        MatrizBidimensional cfinal = new MatrizBidimensional();
        cfinal.setCeldas(cajaFinal);
        manejador.guardarVariable("veh_cet_cja_ini", inicial);
        manejador.guardarVariable("veh_cet_cja_fin", cfinal);
    }

    private MatrizBidimensional sumarWaterFall(String pagoTir, String veh_cet_apt, String veh_cet_ret) {
        MatrizBidimensional mpago = (MatrizBidimensional) manejador.obtenerVariable(pagoTir);
        MatrizBidimensional mapt = (MatrizBidimensional) manejador.obtenerVariable(veh_cet_apt);
        MatrizBidimensional mret = (MatrizBidimensional) manejador.obtenerVariable(veh_cet_ret);
        List<CeldaFechaValor> celdas = new LinkedList<CeldaFechaValor>();
//        for(int t=0;t<mpago.getCeldas().size();t++){
//            double val1=mpago.getCeldas().get(t).getValor();
//            double val2=mapt.getCeldas().get(t).getValor();
//            double val3=mret.getCeldas().get(t+1).getValor();
//            celdas.add(new CeldaFechaValor(mpago.getCeldas().get(t).getFecha(),val1+val2+val3));
//        }
        for (int t = 0; t < mpago.getCeldas().size(); t++) {
            double val1 = mpago.getCeldas().get(t).getValor();
            double val2 = mapt.getCeldas().get(t).getValor();
            celdas.add(new CeldaFechaValor(mpago.getCeldas().get(t).getFecha(), val1 + val2));
        }
        int contador = 0;
        for (int s = celdas.size() - 1; s >= 0; s--) {
            double val3 = contador < mret.getCeldas().size() ? mret.getCeldas().get(mret.getCeldas().size() - 1 - contador).getValor() : 0.0;
            CeldaFechaValor actual = celdas.get(s);
            actual.setValor(actual.getValor() + val3);
            contador++;
            celdas.set(s, actual);
        }

        MatrizBidimensional m = new MatrizBidimensional();
        m.setCeldas(celdas);
        return m;
    }

    private void calcularTirInversionista(int numeroEscalones) {
        String[] arreglo = new String[numeroEscalones];
        for (int i = 0; i < numeroEscalones; i++) {
            arreglo[i] = "veh_cet_par_uti_lp" + i;
        }

        manejador.guardarVariable("tmp_pag_tir", sumarMatrices(arreglo));
        MatrizBidimensional sumarWaterFall = sumarWaterFall("tmp_pag_tir", "veh_cet_apt", "veh_cet_ret");
        System.out.println(manejador.obtenerVariable("tmp_pag_tir"));
        double tirInv = sacarTir(sumarWaterFall);
        tirInv = tirInv > 1 ? tirInv / 100 : tirInv;
        manejador.guardarVariable("veh_cet_ptj_tir_inv", tirInv);
        System.out.println("tir inv " + manejador.obtenerVariable("veh_cet_ptj_tir_inv"));
    }

    private Object moverDerecha(MatrizBidimensional matrizBidimensional) {
        List<CeldaFechaValor> celdas = new LinkedList<CeldaFechaValor>();
        for (int t = 1; t < matrizBidimensional.getCeldas().size(); t++) {
            celdas.add(new CeldaFechaValor(matrizBidimensional.getCeldas().get(t).getFecha(), matrizBidimensional.getCeldas().get(t - 1).getValor()));
        }
        MatrizBidimensional m = new MatrizBidimensional();
        m.setCeldas(celdas);
        return m;
    }
}
