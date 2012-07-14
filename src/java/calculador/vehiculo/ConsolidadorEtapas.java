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
 *
 * @author alberto
 */
class ConsolidadorEtapas {

    private List<PryVeh> proyectos;
    private ManejadorVariablesVehiculo manejador;
    private Date fechaInicial;
    private Date fechaFinal;
    private int tiempoMinistracion;
    private int tiempoPago;

    public ConsolidadorEtapas(int tiempoMinistracion, int tiempoPago, List<PryVeh> proyectos, ManejadorVariablesVehiculo manejador) {
        this.tiempoMinistracion = tiempoMinistracion;
        this.tiempoPago = tiempoPago;
        this.proyectos = proyectos;
        this.manejador = manejador;
        this.fechaInicial = obtenerFechaInicial();
        this.fechaFinal = obtenerFechaFinal();
    }

    void procesar() {
        //parte de matrices
        manejador.guardarVariable("veh_cet_uni_edf", calcularMatrizSumasTiempo("veh_uni_edf_pry"));
        manejador.guardarVariable("veh_cet_uni_edf_acu", calcularMatrizSumasTiempoAcumuladas("veh_uni_edf_acu"));

        manejador.guardarVariable("veh_cet_uni_dis", calcularMatrizSumasTiempo("veh_uni_dis"));
        manejador.guardarVariable("veh_cet_uni_dis_acu", calcularMatrizSumasTiempoAcumuladas("veh_uni_dis_acu"));
        manejador.guardarVariable("veh_cet_uni_ven", calcularMatrizSumasTiempo("veh_uni_ven"));

        manejador.guardarVariable("veh_cet_uni_ven_acu", calcularMatrizSumasTiempoAcumuladas("veh_uni_ven_acu"));
        //ingresos
        manejador.guardarVariable("veh_cet_fir_ctr_cpr_vta", calcularMatrizSumasTiempo("veh_fir_ctr_cpr_vta"));
        //System.out.println(manejador.obtenerVariable("veh_cet_fir_ctr_cpr_vta"));
        manejador.guardarVariable("veh_cet_des_ini", calcularMatrizSumasTiempo("veh_des_ini"));
        manejador.guardarVariable("veh_cet_ing", sumarMatrices("veh_cet_des_ini", "veh_cet_fir_ctr_cpr_vta"));

//        System.out.println(manejador.obtenerVariable("veh_cet_ing"));
        manejador.guardarVariable("veh_cet_cto_vta", calcularMatrizSumasTiempo("veh_cto_vta"));
        manejador.guardarVariable("veh_cet_gav", calcularMatrizSumasTiempo("veh_cto_gav"));
        manejador.guardarVariable("veh_cet_gas_ind", calcularMatrizSumasTiempo("veh_cto_gas_ind"));
        manejador.guardarVariable("veh_cet_app", calcularMatrizSumasTiempo("veh_adm_pry"));
        manejador.guardarVariable("veh_cet_sapi", calcularSapi());
        manejador.guardarVariable("veh_cet_adm_pry", sumarMatrices("veh_cet_app", "veh_cet_sapi"));
        manejador.guardarVariable("veh_cet_com_cre_pte", sacarMatrizComisionCreditoPuente());

//         System.out.println(manejador.obtenerVariable("veh_cet_ant_cre_pte"));

        //financiamiento
        manejador.guardarVariable("veh_cet_ant_cre_pte", sacarAnticipoCreditoPuente());
        manejador.guardarVariable("veh_cet_min_cre_pte", sacarMinistracionCreditoPuente(tiempoMinistracion));
        manejador.guardarVariable("veh_cet_pgo_cre_pte", sacarPagoCreditoPuente(tiempoPago));

        manejador.guardarVariable("veh_cet_sal_cre", sacarSaldoCreditoPuente());

        //calculamos los intereses del credito puente
        manejador.guardarVariable("veh_cet_int_cre_pte", sacarMatrizInteresCreditoPuente());

        //calculamos el costo del financiamiento
        manejador.guardarVariable("veh_cet_cto_fin", guardarCostoFinanciero(sumarMatrices("veh_cet_int_cre_pte", "veh_cet_com_cre_pte")));


        //calculas los egresos
        manejador.guardarVariable("veh_cet_egr", sumarMatrices("veh_cet_cto_vta", "veh_cet_gav", "veh_cet_gas_ind", "veh_cet_adm_pry", "veh_cet_cto_fin"));


        manejador.guardarVariable("veh_cet_mar_res_veh", restarMatrices("veh_cet_ing", "veh_cet_egr"));


        //flujo de caja primera parte . la parte de la reparticion al inversionista se vera en otra clase
        manejador.guardarVariable("veh_cet_ing_ope", sumarMatrices("veh_cet_fir_ctr_cpr_vta", "veh_cet_des_ini"));
        manejador.guardarVariable("veh_cet_apl", sumarMatrices("veh_cet_cto_vta", "veh_cet_gav", "veh_cet_gas_ind", "veh_cet_adm_pry", "veh_cet_cto_fin", "veh_cet_pgo_cre_pte"));
        manejador.guardarVariable("veh_cet_fin", sumarMatrices("veh_cet_ant_cre_pte", "veh_cet_min_cre_pte"));
        manejador.guardarVariable("veh_cet_ori", sumarMatrices("veh_cet_ing_ope", "veh_cet_fin"));
        manejador.guardarVariable("veh_cet_mov_net", restarMatrices("veh_cet_ori", "veh_cet_apl"));
        manejador.guardarVariable("veh_cet_apt", ponerAportaciones());
        manejador.guardarVariable("veh_cet_ret", ponerRetiros(tiempoPago));
        manejador.guardarVariable("veh_cet_cap", restarMatrices("veh_cet_apt", "veh_cet_ret"));
    }

    private List<MatrizBidimensional> obtenerMatrices(String propiedad) {
        List<MatrizBidimensional> matrices = new LinkedList<MatrizBidimensional>();
        for (int indiceProyecto = 0; indiceProyecto < proyectos.size(); indiceProyecto++) {
            PryVeh pry = proyectos.get(indiceProyecto);
            int numEtapas = pry.getEtpVehs().size() / 3;
            for (int contador = 0; contador < numEtapas; contador++) {
                matrices.add((MatrizBidimensional) manejador.obtenerVariable(propiedad + "[" + indiceProyecto + "][" + contador + "]"));
            }
        }
        return matrices;
    }

    private MatrizBidimensional calcularMatrizSumasTiempo(String propiedad) {
        this.fechaInicial = obtenerFechaInicial();
        List<MatrizBidimensional> listaMatrices = obtenerMatrices(propiedad);
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
            CeldaFechaValor nueva = new CeldaFechaValor(instancia, (suma));
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

    private CeldaFechaValor generarUltimaCeldaAcumuladas(Calendar instancia, List<MatrizBidimensional> listaMatrices, int[] indices) {
        double suma = 0;
        for (int indiceMatriz = 0; indiceMatriz < listaMatrices.size(); indiceMatriz++) {
            MatrizBidimensional actual = listaMatrices.get(indiceMatriz);
            if (actual.getCeldas().size() == indices[indiceMatriz]) {
                suma += actual.getCeldas().get(indices[indiceMatriz] - 1).getValor();
            } else if (actual.getCeldas().size() > indices[indiceMatriz]
                    && actual.getCeldas().get(indices[indiceMatriz]).getFecha().getTime().getMonth() == fechaFinal.getMonth()
                    && actual.getCeldas().get(indices[indiceMatriz]).getFecha().getTime().getYear() == fechaFinal.getYear()) {
                suma += actual.getCeldas().get(indices[indiceMatriz]).getValor();
            }
        }
        return new CeldaFechaValor(instancia, (suma));
    }

    private Object calcularMatrizSumasTiempoAcumuladas(String propiedad) {
        this.fechaInicial = obtenerFechaInicial();
        List<MatrizBidimensional> listaMatrices = obtenerMatrices(propiedad);
        int[] indices = new int[listaMatrices.size()];
        Date copiaInicial = this.fechaInicial;
        List<CeldaFechaValor> lista = new LinkedList<CeldaFechaValor>();
        while ((copiaInicial.getMonth() != (fechaFinal.getMonth()) || copiaInicial.getYear() != fechaFinal.getYear())) {
            double suma = 0;
            for (int indiceMatrices = 0; indiceMatrices < listaMatrices.size(); indiceMatrices++) {
                MatrizBidimensional actual = listaMatrices.get(indiceMatrices);
                if (actual.getCeldas().size() == indices[indiceMatrices]) {
                    suma += actual.getCeldas().get(indices[indiceMatrices] - 1).getValor();
                } else if (actual.getCeldas().size() > indices[indiceMatrices]
                        && actual.getCeldas().get(indices[indiceMatrices]).getFecha().getTime().getMonth() == fechaInicial.getMonth()
                        && actual.getCeldas().get(indices[indiceMatrices]).getFecha().getTime().getYear() == fechaInicial.getYear()) {
                    suma += actual.getCeldas().get(indices[indiceMatrices]).getValor();
                    indices[indiceMatrices] = indices[indiceMatrices] + 1;
                }
            }
            Calendar instancia = Calendar.getInstance();
            instancia.setTime(copiaInicial);
            CeldaFechaValor nueva = new CeldaFechaValor(instancia, (suma));
            copiaInicial.setMonth(copiaInicial.getMonth() + 1);
            lista.add(nueva);
        }
        Calendar instancia = Calendar.getInstance();
        instancia.setTime(fechaFinal);
        CeldaFechaValor celda = generarUltimaCeldaAcumuladas(instancia, listaMatrices, indices);
        lista.add(celda);
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

    private MatrizBidimensional calcularSapi() {
        double porcentajeSapi = (sumarValores("veh_sapi") / proyectos.size()) * .01;
        double creditoTotal = sumarValores("veh_cap_inv");
        double valorSapi = porcentajeSapi * creditoTotal / 4;
        int consecutivo = 0;
        this.fechaInicial = obtenerFechaInicial();
        Date copiaInicial = fechaInicial;
        List<CeldaFechaValor> lista = new LinkedList<CeldaFechaValor>();
        while ((copiaInicial.getMonth() != (fechaFinal.getMonth()) || copiaInicial.getYear() != fechaFinal.getYear())) {
            double valor = consecutivo % 3 == 0 ? valorSapi : 0.0;
            Calendar c = Calendar.getInstance();
            c.setTime(copiaInicial);
            lista.add(new CeldaFechaValor(c, Funciones.redondearDecimales(valor, 3)));
            copiaInicial.setMonth(copiaInicial.getMonth() + 1);
            consecutivo++;
        }
        double valor = consecutivo % 3 == 0 ? valorSapi : 0.0;
        Calendar c = Calendar.getInstance();
        c.setTime(fechaFinal);
        lista.add(new CeldaFechaValor(c, Funciones.redondearDecimales(valor, 3)));
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

    private Object sacarMatrizComisionCreditoPuente() {
        double premisaD35 = (Double) manejador.obtenerVariable("d35");
        double porcentajeCredPte = (sumarValores("veh_com_crd_pte") / proyectos.size()) * .01;
        double valorF146 = premisaD35 * porcentajeCredPte / 2;

        int consecutivo = 0;
        this.fechaInicial = obtenerFechaInicial();
        Date copiaInicial = fechaInicial;
        List<CeldaFechaValor> lista = new LinkedList<CeldaFechaValor>();
        while ((copiaInicial.getMonth() != (fechaFinal.getMonth()) || copiaInicial.getYear() != fechaFinal.getYear())) {
            double valor = consecutivo == 0 ? valorF146 : consecutivo == 2 || consecutivo == 4 ? valorF146 / 2 : 0;
            Calendar c = Calendar.getInstance();
            c.setTime(copiaInicial);
            lista.add(new CeldaFechaValor(c, Funciones.redondearDecimales(valor, 3)));
            copiaInicial.setMonth(copiaInicial.getMonth() + 1);
            consecutivo++;
        }
        Calendar c = Calendar.getInstance();
        c.setTime(fechaFinal);
        lista.add(new CeldaFechaValor(c, 0.0));
        MatrizBidimensional m = new MatrizBidimensional();
        m.setCeldas(lista);
        return m;

    }

    private Object sacarAnticipoCreditoPuente() {
        double premisaD35 = (Double) manejador.obtenerVariable("d35");
        double porcentajeAnticipoCredPuente = (sumarValores("veh_ant_crd_pte") / proyectos.size()) * .01;
        this.fechaInicial = obtenerFechaInicial();
        List<CeldaFechaValor> lista = new LinkedList<CeldaFechaValor>();
        Calendar instance = Calendar.getInstance();
        instance.setTime(fechaInicial);
        CeldaFechaValor unica = new CeldaFechaValor(instance, Funciones.redondearDecimales(premisaD35 * porcentajeAnticipoCredPuente, 3));
        lista.add(unica);
        MatrizBidimensional m = new MatrizBidimensional();
        m.setCeldas(lista);
        return m;
    }

    private Object sacarMinistracionCreditoPuente(int mesesMinistracion) {
        double d35 = (Double) manejador.obtenerVariable("d35");
        MatrizBidimensional matrizCostoVentas = matrizCostoVenta(mesesMinistracion);
        double sumaCostoVenta = Funciones.autoSumar(matrizCostoVentas);
        double porcentajeAnticipoCredPuente = (100 - (sumarValores("veh_ant_crd_pte") / proyectos.size())) * .01;
        List<CeldaFechaValor> lista = new LinkedList<CeldaFechaValor>();
        System.out.println("checando la ministracion");
        for (CeldaFechaValor c : matrizCostoVentas.getCeldas()) {
            System.out.println("d "+d35);
            System.out.println("c "+c.getValor());
            System.out.println("scvta "+sumaCostoVenta);
            double valor=d35 * c.getValor() * porcentajeAnticipoCredPuente / sumaCostoVenta;
            CeldaFechaValor nueva = new CeldaFechaValor(c.getFecha(), valor);
            System.out.println("agregando en la ministracion "+valor);
            lista.add(nueva);
        }
        MatrizBidimensional m = new MatrizBidimensional();
        m.setCeldas(lista);
        return m;
    }

    private MatrizBidimensional matrizCostoVenta(int limiteMeses) {
        String[] arregloNombres = obtenerMatrizUnionEtapas("veh_cto_vta");
        MatrizBidimensional sumaCostoVentas = sumarMatrices(arregloNombres);
        List<CeldaFechaValor> lista = new LinkedList<CeldaFechaValor>();
        Calendar fecha = null;
        for (int indice = 0; indice < limiteMeses; indice++) {
            CeldaFechaValor nueva;
            if (indice < sumaCostoVentas.getCeldas().size()) {
                fecha=sumaCostoVentas.getCeldas().get(indice).getFecha();
                nueva = new CeldaFechaValor(fecha, sumaCostoVentas.getCeldas().get(indice).getValor());
            }else{
                fecha.set(Calendar.MONTH, fecha.get(Calendar.MONTH)+1);
                nueva=new CeldaFechaValor(fecha, 0);
            }
            lista.add(nueva);
        }
        MatrizBidimensional m = new MatrizBidimensional();
        m.setCeldas(lista);
        return m;
    }

    private String[] obtenerMatrizUnionEtapas(String llave) {
        List<String> nombres = new LinkedList<String>();
        ListaEtapasVehiculo[] arr = (ListaEtapasVehiculo[]) manejador.obtenerVariable("veh_eta");
        for (int indiceProyecto = 0; indiceProyecto < proyectos.size(); indiceProyecto++) {
            ListaEtapasVehiculo proyecto = arr[indiceProyecto];
            int numeroEtapas = proyecto.getListaEtapas().size();
            for (int indiceEtapa = 0; indiceEtapa < numeroEtapas; indiceEtapa++) {
                nombres.add(llave + "[" + indiceProyecto + "][" + indiceEtapa + "]");
            }

        }
        String[] arreglo = new String[nombres.size()];
        nombres.toArray(arreglo);
        return arreglo;
    }

    private Object sacarSaldoCreditoPuente() {
        MatrizBidimensional anticipo = (MatrizBidimensional) manejador.obtenerVariable("veh_cet_ant_cre_pte");
        MatrizBidimensional ministracion = (MatrizBidimensional) manejador.obtenerVariable("veh_cet_min_cre_pte");
        MatrizBidimensional pagoMatriz = (MatrizBidimensional) manejador.obtenerVariable("veh_cet_pgo_cre_pte");
        List<CeldaFechaValor> lista = new LinkedList<CeldaFechaValor>();
        Date copiaInicial = obtenerFechaInicial();
        Calendar instance = Calendar.getInstance();
        instance.setTime(copiaInicial);
        CeldaFechaValor primera = new CeldaFechaValor(instance, anticipo.getCeldas().get(0).getValor() + ministracion.getCeldas().get(0).getValor() - pagoMatriz.getCeldas().get(0).getValor());
        lista.add(primera);
        copiaInicial.setMonth(copiaInicial.getMonth() + 1);
        int indice = 1;
        while ((copiaInicial.getMonth() != (fechaFinal.getMonth()) || copiaInicial.getYear() != fechaFinal.getYear())) {
            Calendar instancia = Calendar.getInstance();
            instancia.setTime(copiaInicial);
            double anticipoParcial = indice < anticipo.getCeldas().size() ? anticipo.getCeldas().get(indice).getValor() : 0;
            double ministracionParcial = indice < ministracion.getCeldas().size() ? ministracion.getCeldas().get(indice).getValor() : 0;
            double pago = pagoMatriz.getCeldas().get(indice).getValor();
            System.out.println("anticipo parcial "+anticipoParcial);
            System.out.println("ministracion "+ministracionParcial);
            System.out.println("pago "+pago);
            CeldaFechaValor nueva = new CeldaFechaValor(instancia, Funciones.redondearDecimales(lista.get(indice - 1).getValor() + anticipoParcial + ministracionParcial - pago, 3));
            lista.add(nueva);
            copiaInicial.setMonth(copiaInicial.getMonth() + 1);
            indice++;
        }
        Calendar instancia = Calendar.getInstance();
        instancia.setTime(fechaFinal);
        double anticipoParcial = indice < anticipo.getCeldas().size() ? anticipo.getCeldas().get(indice).getValor() : 0;
        double ministracionParcial = indice < ministracion.getCeldas().size() ? ministracion.getCeldas().get(indice).getValor() : 0;
        double pago = indice < pagoMatriz.getCeldas().size() ? pagoMatriz.getCeldas().get(indice).getValor() : 0;
        CeldaFechaValor nueva = new CeldaFechaValor(instancia, Funciones.redondearDecimales(lista.get(indice - 1).getValor() + anticipoParcial + ministracionParcial - pago, 3));
        lista.add(nueva);
        MatrizBidimensional m = new MatrizBidimensional();
        m.setCeldas(lista);
        return m;
    }

    private MatrizBidimensional restarMatrices(String primero, String segundo) {
        MatrizBidimensional m1 = (MatrizBidimensional) manejador.obtenerVariable(primero);
        MatrizBidimensional m2 = (MatrizBidimensional) manejador.obtenerVariable(segundo);
        List<CeldaFechaValor> lista = new LinkedList<CeldaFechaValor>();
        for (int t = 0; t < m1.getCeldas().size(); t++) {
            double se = t < m2.getCeldas().size() ? m2.getCeldas().get(t).getValor() : 0.0;
            lista.add(new CeldaFechaValor(m1.getCeldas().get(t).getFecha(), m1.getCeldas().get(t).getValor() - se));
        }
        MatrizBidimensional m = new MatrizBidimensional();
        m.setCeldas(lista);
        return m;
    }

    private MatrizBidimensional sacarMatrizInteresCreditoPuente() {
        double porcentajeAnualidad = (sumarValores("veh_tsa_anu_crd_pte") / proyectos.size()) * .01;
        MatrizBidimensional saldo = (MatrizBidimensional) manejador.obtenerVariable("veh_cet_sal_cre");
        List<CeldaFechaValor> lista = new LinkedList<CeldaFechaValor>();
        for (CeldaFechaValor c : saldo.getCeldas()) {
            lista.add(new CeldaFechaValor(c.getFecha(), c.getValor() * porcentajeAnualidad / 12));
        }
        MatrizBidimensional m = new MatrizBidimensional();
        m.setCeldas(lista);
        return m;
    }

    private Object sacarPagoCreditoPuente(int numeroMesesPago) {
        double d35 = (Double) manejador.obtenerVariable("d35");
        MatrizBidimensional ingresos = volverCerosMatriz(numeroMesesPago, (MatrizBidimensional) manejador.obtenerVariable("veh_cet_ing"));
        double sumaIngresos = sumarValoresMatriz(ingresos);
        List<CeldaFechaValor> lista = new LinkedList<CeldaFechaValor>();
        for (CeldaFechaValor c : ingresos.getCeldas()) {
            lista.add(new CeldaFechaValor(c.getFecha(), d35 * c.getValor() / sumaIngresos));
        }
        MatrizBidimensional m = new MatrizBidimensional();
        m.setCeldas(lista);
        return m;
    }

    private MatrizBidimensional volverCerosMatriz(int numeroMesesPago, MatrizBidimensional matrizBidimensional) {
        List<CeldaFechaValor> lista = new LinkedList<CeldaFechaValor>();

        for (int t = matrizBidimensional.getCeldas().size() - 1; t >= 0; t--) {
            double valor = t >= matrizBidimensional.getCeldas().size() - numeroMesesPago ? matrizBidimensional.getCeldas().get(t).getValor() : 0;
            lista.add(new CeldaFechaValor(matrizBidimensional.getCeldas().get(t).getFecha(), valor));
        }
        MatrizBidimensional m = new MatrizBidimensional();
        m.setCeldas(lista);
        return m;
    }

    private double sumarValoresMatriz(MatrizBidimensional ingresos) {
        double suma = 0;
        for (CeldaFechaValor c : ingresos.getCeldas()) {
            suma += c.getValor();
        }
        return suma;
    }

    private MatrizBidimensional ponerAportaciones() {
        MatrizBidimensional m = new MatrizBidimensional();
        List<CeldaFechaValor> celdas = new LinkedList<CeldaFechaValor>();
        Date inicial = (Date) obtenerFechaInicial().clone();
        for (int t = 0; t <= 60; t++) {
            Calendar instance = Calendar.getInstance();
            instance.setTime(inicial);
            celdas.add(new CeldaFechaValor(instance, 0));
            inicial.setMonth(inicial.getMonth() + 1);
        }
        m.setCeldas(celdas);
        return m;
    }

    private Object ponerRetiros(int mesesPagarCredito) {
        double partes = mesesPagarCredito < 12 ? mesesPagarCredito / 3 : (mesesPagarCredito / 12) * 3;
        partes = partes == 0 ? 1 : partes;
        double modulo = mesesPagarCredito < 12 ? mesesPagarCredito / 3 : 6;
        double creditoTotal = sumarValores("veh_cap_inv");
        Date fecFinal = (Date) obtenerFechaFinal().clone();
        List<CeldaFechaValor> celdas = new LinkedList<CeldaFechaValor>();
        double suma = 0.0;
        for (int t = 60; t >= 0; t--) {
            Calendar instance = Calendar.getInstance();
            instance.setTime(fecFinal);
            double valor = suma < creditoTotal ? t % Math.round(modulo) == 0 ? creditoTotal / partes : 0 : 0;
            celdas.add(new CeldaFechaValor(instance, valor));
            fecFinal.setMonth(fecFinal.getMonth() - 1);
            suma += valor;
        }
        MatrizBidimensional m = new MatrizBidimensional();
        m.setCeldas(celdas);
        return m;
    }

    private MatrizBidimensional guardarCostoFinanciero(MatrizBidimensional sumarMatrices) {
        Date fecFinal = obtenerFechaFinal();
        Calendar instancia = Calendar.getInstance();
        instancia.setTime(fecFinal);
        sumarMatrices.getCeldas().set(sumarMatrices.getCeldas().size() - 1, new CeldaFechaValor(instancia, 0));
        return sumarMatrices;
    }
}
