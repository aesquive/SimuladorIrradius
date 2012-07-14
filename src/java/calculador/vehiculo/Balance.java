package calculador.vehiculo;

import antlr.debug.LLkDebuggingParser;
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
public class Balance {

    private List<PryVeh> proyectos;
    private ManejadorVariablesVehiculo manejador;
    private Date fechaInicial;
    private Date fechaFinal;

    public Balance(List<PryVeh> proyectos, ManejadorVariablesVehiculo manejador) {
        this.proyectos = proyectos;
        this.manejador = manejador;
        this.fechaFinal = obtenerFechaFinal();
        this.fechaInicial = obtenerFechaInicial();
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

    public void procesar() {
        manejador.guardarVariable("veh_bal_efe", manejador.obtenerVariable("veh_cet_cja_fin"));
        manejador.guardarVariable("veh_bal_cta_cob", guardarCuentasCobrar());
        manejador.guardarVariable("veh_bal_inv_viv", guardarInventarioVivienda());
        manejador.guardarVariable("veh_bal_tot_act", sumarMatrices("veh_bal_efe", "veh_bal_cta_cob", "veh_bal_inv_viv"));

        //calculamos la parte del patrimonio
        manejador.guardarVariable("veh_bal_tot_pat", guardarPatrimonio());


        manejador.guardarVariable("veh_bal_cta_por_pag", calcularCuentasPorPagar());

        manejador.guardarVariable("veh_tot_pas", cancelarUltima(sumarMatrices("veh_bal_cta_por_pag", "veh_cet_sal_cre")));

        manejador.guardarVariable("tmp", sumarMatrices("veh_tot_pas", "veh_bal_tot_pat"));
        manejador.guardarVariable("veh_tot_bal", restarMatrices("veh_bal_tot_act", "tmp"));

        calcularDatosAnuales();

        calcularRatiosBalance();
        
        calcularDatosGraficacion();
    }
    
    private void calcularRatiosBalanceGraficacion() {
        manejador.guardarVariable("veh_bal_roe_graf", dividirMatrices(new String[]{"veh_utl_per_anu_graf","veh_bal_tot_pat_anu_graf"}));
        manejador.guardarVariable("veh_bal_roa_graf", dividirMatrices(new String[]{"veh_utl_per_anu_graf","veh_bal_tot_act_anu_graf"}));
        manejador.guardarVariable("tmpcalc", restar1a1((MatrizBidimensional)manejador.obtenerVariable("veh_total_utl_oprt_anu_graf"), (MatrizBidimensional)manejador.obtenerVariable("veh_gst_opt_anu_graf")));
        manejador.guardarVariable("veh_bal_ebi_graf", dividirMatrices(new String[]{"tmpcalc","veh_edo_vta_viv_anu_graf"}));
        manejador.guardarVariable("veh_cob_deu_graf", dividirMatrices(new String[]{"veh_bal_tot_act_anu_graf","veh_bal_tot_pas_anu_graf"}));
        manejador.guardarVariable("veh_rot_inv_graf", dividirMatrices(new String[]{"veh_bal_inv_viv_anu_graf","veh_edo_cto_vta_anu_graf"}));
        manejador.guardarVariable("veh_pal_ope_graf", dividirMatrices(new String[]{"veh_bal_tot_pas_anu_graf","veh_bal_tot_act_anu_graf"}));
        manejador.guardarVariable("veh_pal_apa_graf", dividirMatrices(new String[]{"veh_bal_tot_pas_anu_graf","veh_bal_tot_pat_anu_graf"}));
        manejador.guardarVariable("veh_cap_tra_anu_graf", restar1a1((MatrizBidimensional)manejador.obtenerVariable("veh_bal_tot_act_anu_graf"),(MatrizBidimensional)manejador.obtenerVariable("veh_bal_cta_por_pag_anu_graf")));
        manejador.guardarVariable("veh_cob_ser_deu_graf", dividirMatrices(new String[]{"veh_bal_efe_anu_graf","veh_cet_int_cre_pte_anu_graf"}));
        manejador.guardarVariable("veh_dia_cta_por_cob_graf", multiplicarFactorMatriz(360,dividirMatrices(new String[]{"veh_bal_cta_cob_anu_graf","veh_edo_vta_viv_anu_graf"})));
        manejador.guardarVariable("veh_dia_cta_por_pag_graf", multiplicarFactorMatriz(360,dividirMatrices(new String[]{"veh_bal_cta_por_pag_anu_graf","veh_edo_cto_vta_anu_graf"})));
    }

    private MatrizBidimensional multiplicarFactorMatriz(int factor, MatrizBidimensional dividirMatrices) {
        List<CeldaFechaValor> celdas=new LinkedList<CeldaFechaValor>();
        for(CeldaFechaValor c:dividirMatrices.getCeldas()){
            celdas.add(new CeldaFechaValor(c.getFecha(), c.getValor()*factor));
        }
        MatrizBidimensional m=new MatrizBidimensional();
        m.setCeldas(celdas);
        return m;
    }
    
    private void calcularDatosGraficacion() {
        //a+b+c significa que el resultado es la suma de esas cosas
        //@significa division
        //]a significa que a se repetira todo el tiempo
        //)a significa que si tenemos la lista={a,b,c} el resultado sera {a , a+b ,a+b+c}
        String[] nombreVars=new String[]{"veh_bal_efe","veh_bal_cta_cob","veh_bal_inv_viv","veh_bal_efe%veh_bal_cta_cob%veh_bal_inv_viv","veh_bal_cta_por_pag",
                                        "veh_cet_sal_cre","veh_bal_cta_por_pag%veh_cet_sal_cre","]veh_cap_inv","veh_utl_net_anu_graf","veh_utl_ret",
                                        "veh_cet_rep_uti%veh_utl_net%veh_utl_ret%veh_bal_cap_anu_graf","veh_total_utl_oprt_anu_graf@veh_edo_vta_viv_anu_graf",
                                        "veh_utl_a_ipt_anu_graf@veh_edo_vta_viv_anu_graf","veh_utl_net_anu_graf@veh_edo_vta_viv_anu_graf"};
        
        
        String[] nombreGraficas=new String[]{"veh_bal_efe_anu","veh_bal_cta_cob_anu","veh_bal_inv_viv_anu","veh_bal_tot_act_anu","veh_bal_cta_por_pag_anu",
                                            "veh_bal_deu_anu","veh_bal_tot_pas_anu","veh_bal_cap_anu","veh_utl_per_anu","veh_utl_ret_anu",
                                            "veh_bal_tot_pat_anu","veh_mar_ope",
                                            "veh_mar_ant_imp","veh_mar_net"};
        //falta retenida y patrimonio 
        for(int t=0;t<nombreVars.length;t++){
            String normal=nombreVars[t];
            String grafi=nombreGraficas[t]+"_graf";
            if(normal.contains("%")){
                manejador.guardarVariable(grafi, sumar1a1(normal.split("%")));
            }else if(normal.contains("]")){
                manejador.guardarVariable(grafi, repetirValores(sumarValores(normal.split("]")[1]),((MatrizBidimensional)manejador.obtenerVariable("veh_bal_efe")).getCeldas()));
            }
            else if(normal.contains("<")){
                manejador.guardarVariable(grafi, sumaAcumulada(normal.split("<")[1]));
            }else if(normal.contains("@")){
                manejador.guardarVariable(grafi, dividirMatrices(normal.split("@")));
            }
            else{
                System.out.println("procesando "+grafi);
                manejador.guardarVariable(grafi, manejador.obtenerVariable(normal));
                System.out.println("kkkkkkkkkkkkkkkkkkkkkkkkkkk");
                System.out.println(manejador.obtenerVariable(grafi));
            }
        }
        
        calcularPatrimonio();
        
        calcularRatiosBalanceGraficacion();
    }
    
    private void calcularPatrimonio() {
        MatrizBidimensional activo = (MatrizBidimensional) manejador.obtenerVariable("veh_bal_tot_act_anu_graf");
        MatrizBidimensional pasivo=(MatrizBidimensional) manejador.obtenerVariable("veh_bal_tot_pas_anu_graf");
        manejador.guardarVariable("veh_bal_tot_pat_anu_graf", restar1a1(activo,pasivo));
        sacarUtilidadRetenida();
    }
    
    
    private MatrizBidimensional restar1a1(MatrizBidimensional activo, MatrizBidimensional pasivo) {
        List<CeldaFechaValor> celdas=new LinkedList<CeldaFechaValor>();
        for(int t=0;t<activo.getCeldas().size();t++){
            double seg=t<pasivo.getCeldas().size() ? pasivo.getCeldas().get(t).getValor() : 0.0;
            celdas.add(new CeldaFechaValor(activo.getCeldas().get(t).getFecha(), activo.getCeldas().get(t).getValor()-seg));
       }
        MatrizBidimensional m=new MatrizBidimensional();
        m.setCeldas(celdas);
        return m;
    }
    
    private void sacarUtilidadRetenida() {
        MatrizBidimensional patrimonio = (MatrizBidimensional) manejador.obtenerVariable("veh_bal_tot_pat_anu_graf");
        String[] nombres=new String[]{"veh_bal_cap_anu_graf","veh_utl_per_anu_graf"};
        MatrizBidimensional sumaCapUti= sumar1a1(nombres);
        manejador.guardarVariable("veh_utl_ret_anu_graf", restar1a1(patrimonio, sumaCapUti));
    }
    
    
    private MatrizBidimensional repetirValores(double valor, List<CeldaFechaValor> guia) {
        List<CeldaFechaValor> celdas=new LinkedList<CeldaFechaValor>();
        for(CeldaFechaValor c:guia){
            celdas.add(new CeldaFechaValor(c.getFecha(),valor));
        }
        MatrizBidimensional m=new MatrizBidimensional();
        m.setCeldas(celdas);
        return m;
    }

    private MatrizBidimensional sumaAcumulada(String llave) {
        List<CeldaFechaValor> celdas=new  LinkedList<CeldaFechaValor>();
        MatrizBidimensional obtenerVariable = (MatrizBidimensional) manejador.obtenerVariable(llave);
        double suma=0;
        int consecutivo=0;
        for(CeldaFechaValor c:obtenerVariable.getCeldas()){
            suma= consecutivo%12 ==0 ? c.getValor() : suma;
            celdas.add(new CeldaFechaValor(c.getFecha(), suma));
            suma+=c.getValor();
    //        consecutivo++;
        }
        MatrizBidimensional regreso=new MatrizBidimensional();
        regreso.setCeldas(celdas);
        return regreso;
    }

    private MatrizBidimensional dividirMatrices(String[] split) {
        MatrizBidimensional primera=(MatrizBidimensional) manejador.obtenerVariable(split[0]);
        MatrizBidimensional segunda=(MatrizBidimensional) manejador.obtenerVariable(split[1]);
        
        List<CeldaFechaValor> celdas=new LinkedList<CeldaFechaValor>();
        for(int t=0;t<primera.getCeldas().size();t++){
            CeldaFechaValor celda1 = primera.getCeldas().get(t);
            CeldaFechaValor celda2= t< segunda.getCeldas().size() ? segunda.getCeldas().get(t) : null;
            double valor = celda2==null||celda2.getValor()==0 ? 0 : celda1.getValor()/celda2.getValor();
            
            celdas.add(new CeldaFechaValor(celda1.getFecha(), Double.parseDouble(Funciones.redondear(String.valueOf(valor*100),3))));
             
        }
        MatrizBidimensional m=new MatrizBidimensional();
        m.setCeldas(celdas);
        return m;
    }

    private MatrizBidimensional guardarCuentasCobrar() {
        MatrizBidimensional ingOpe = (MatrizBidimensional) manejador.obtenerVariable("veh_cet_ing_ope");
        List<CeldaFechaValor> celdas = new LinkedList<CeldaFechaValor>();
        for (CeldaFechaValor c : ingOpe.getCeldas()) {
            celdas.add(new CeldaFechaValor(c.getFecha(), c.getValor() * .05));
        }
        CeldaFechaValor get = celdas.get(celdas.size() - 1);
        get.setValor(0);
        celdas.set(celdas.size() - 1, get);
        MatrizBidimensional m = new MatrizBidimensional();

        m.setCeldas(celdas);
        return m;
    }

    private MatrizBidimensional guardarInventarioVivienda() {
        MatrizBidimensional ingOpe = (MatrizBidimensional) manejador.obtenerVariable("veh_cet_uni_dis");
        Object[] valoresViviendas = (Object[]) manejador.obtenerVariable("veh_val_pro_viv");
        double precioPromedio = 0;
        for (int t = 0; t < proyectos.size(); t++) {
            precioPromedio += Double.parseDouble(String.valueOf(valoresViviendas[t]));
        }
        precioPromedio = precioPromedio / proyectos.size();
        List<CeldaFechaValor> celdas = new LinkedList<CeldaFechaValor>();
        for (CeldaFechaValor c : ingOpe.getCeldas()) {
            celdas.add(new CeldaFechaValor(c.getFecha(), c.getValor() * precioPromedio / 1000));
        }
        MatrizBidimensional m = new MatrizBidimensional();
        m.setCeldas(celdas);
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

    private MatrizBidimensional guardarPatrimonio() {
        MatrizBidimensional utilidadPeriodo = (MatrizBidimensional) manejador.obtenerVariable("veh_utl_net");
        MatrizBidimensional reparto = (MatrizBidimensional) manejador.obtenerVariable("veh_cet_rep_uti");
        MatrizBidimensional utilidadRetenida = (MatrizBidimensional) manejador.obtenerVariable("veh_utl_ret");
        double creditoTotal = sumarValores("veh_cap_inv");
        List<CeldaFechaValor> celdas = new LinkedList<CeldaFechaValor>();
        for (int indice = 0; indice < utilidadPeriodo.getCeldas().size(); indice++) {
            celdas.add(new CeldaFechaValor(utilidadPeriodo.getCeldas().get(indice).getFecha(),
                    creditoTotal + utilidadPeriodo.getCeldas().get(indice).getValor() + reparto.getCeldas().get(indice).getValor()
                    + utilidadRetenida.getCeldas().get(indice).getValor()));
        }
        CeldaFechaValor get = celdas.get(celdas.size() - 1);
        get.setValor(0);
        celdas.set(celdas.size() - 1, get);
        MatrizBidimensional m = new MatrizBidimensional();
        m.setCeldas(celdas);
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

    private double sumarValoresMatriz(MatrizBidimensional m, int liminf, int limsup) {
        double suma = 0;
        for (int t = liminf; t <= limsup; t++) {
            if (t < m.getCeldas().size()) {
                suma += m.getCeldas().get(t).getValor();

            }
        }
        return suma;
    }

    private MatrizBidimensional calcularCuentasPorPagar() {
        MatrizBidimensional saldo = (MatrizBidimensional) manejador.obtenerVariable("veh_cet_sal_cre");
        MatrizBidimensional patrimonio = (MatrizBidimensional) manejador.obtenerVariable("veh_bal_tot_pat");
        MatrizBidimensional activos = (MatrizBidimensional) manejador.obtenerVariable("veh_bal_tot_act");
        List<CeldaFechaValor> celdas = new LinkedList<CeldaFechaValor>();
        for (int t = 0; t < saldo.getCeldas().size(); t++) {
            ;
            celdas.add(new CeldaFechaValor(saldo.getCeldas().get(t).getFecha(),
                    activos.getCeldas().get(t).getValor() - saldo.getCeldas().get(t).getValor() - patrimonio.getCeldas().get(t).getValor()));
        }
        MatrizBidimensional m = new MatrizBidimensional();
        m.setCeldas(celdas);
        return m;
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

    private Object cancelarUltima(MatrizBidimensional matriz) {
        CeldaFechaValor get = matriz.getCeldas().get(matriz.getCeldas().size() - 1);
        get.setValor(0);
        matriz.getCeldas().set(matriz.getCeldas().size() - 1, get);
        return matriz;
    }

    private void calcularDatosAnuales() {
        manejador.guardarVariable("veh_bal_efe_anu", obtenerLista("veh_bal_efe", 11, 23, 35, 47));
        manejador.guardarVariable("veh_bal_cta_cob_anu", obtenerLista("veh_bal_cta_cob", 11, 23, 35, 47));
        manejador.guardarVariable("veh_bal_inv_viv_anu", obtenerLista("veh_bal_inv_viv", 11, 23, 35, 47));
        manejador.guardarVariable("veh_bal_tot_act_anu", sumarListas("veh_bal_efe_anu", "veh_bal_cta_cob_anu", "veh_bal_inv_viv_anu"));

        manejador.guardarVariable("veh_bal_cta_por_pag_anu", obtenerLista("veh_bal_cta_por_pag", 11, 23, 35, 47));
        manejador.guardarVariable("veh_bal_deu_anu", obtenerLista("veh_cet_sal_cre", 11, 23, 35, 47));
        manejador.guardarVariable("veh_bal_tot_pas_anu", sumarListas("veh_bal_cta_por_pag_anu", "veh_bal_deu_anu"));
        double creditoTotal = sumarValores("veh_cap_inv");
        List<Double> capital = new LinkedList<Double>();
        capital.add(creditoTotal);
        capital.add(creditoTotal);
        capital.add(creditoTotal);
        capital.add(creditoTotal);
        capital.add(creditoTotal);
        manejador.guardarVariable("veh_bal_cap_anu", capital);
        List<Double> utilidadPeriodo = new LinkedList<Double>();
        utilidadPeriodo.add(sumarValoresMatriz((MatrizBidimensional) manejador.obtenerVariable("veh_utl_net"), 0, 11));
        utilidadPeriodo.add(sumarValoresMatriz((MatrizBidimensional) manejador.obtenerVariable("veh_utl_net"), 12, 23));
        utilidadPeriodo.add(sumarValoresMatriz((MatrizBidimensional) manejador.obtenerVariable("veh_utl_net"), 24, 35));
        utilidadPeriodo.add(sumarValoresMatriz((MatrizBidimensional) manejador.obtenerVariable("veh_utl_net"), 36, 47));
        utilidadPeriodo.add(0.0);
        manejador.guardarVariable("veh_utl_per_anu", utilidadPeriodo);
        manejador.guardarVariable("veh_utl_ret_anu", calcularUtilidadRetenida(utilidadPeriodo, creditoTotal));

        manejador.guardarVariable("veh_bal_tot_pat_anu", sumarListas("veh_bal_cap_anu", "veh_utl_per_anu", "veh_utl_ret_anu"));

    }

    private List<Double> obtenerLista(String nombre, int... valores) {
        MatrizBidimensional matriz = (MatrizBidimensional) manejador.obtenerVariable(nombre);
        List<Double> vals = new LinkedList<Double>();
        for (int i : valores) {
            try {
                vals.add(matriz.getCeldas().get(i).getValor());

            } catch (IndexOutOfBoundsException e) {
                vals.add(0.0);
            }
        }
        return vals;
    }

    private List sumarListas(String... datos) {
        List<List<Double>> listas = new LinkedList<List<Double>>();
        for (String s : datos) {
            listas.add((List<Double>) manejador.obtenerVariable(s));
        }
        List<Double> sumas = new LinkedList<Double>();
        for (int t = 0; t < listas.get(0).size(); t++) {
            double suma = 0;
            for (List<Double> sublista : listas) {
                suma += sublista.get(t);
            }
            sumas.add(suma);
        }
        return sumas;
    }

    private List calcularUtilidadRetenida(List<Double> utilidadPeriodo, double creditoTotal) {
        List<Double> nueva = new LinkedList<Double>();
        nueva.add(0.0);
        double suma = utilidadPeriodo.get(0);
        for (int t = 1; t < utilidadPeriodo.size() - 1; t++) {
            nueva.add(suma);
            suma += utilidadPeriodo.get(t);
        }
        nueva.add((-1) * creditoTotal);
        return nueva;
    }

    private void calcularRatiosBalance() {
        manejador.guardarVariable("veh_bal_roe", dividirListas("veh_utl_per_anu", "veh_bal_tot_pat_anu",true));
        manejador.guardarVariable("veh_bal_roa", dividirListas("veh_utl_per_anu", "veh_bal_tot_act_anu",true));
        manejador.guardarVariable("tmp", restarListas("veh_total_utl_oprt_anu", "veh_gst_opt_anu"));
        manejador.guardarVariable("veh_bal_ebi", dividirListas("tmp", "veh_edo_vta_viv_anu", true));
        manejador.guardarVariable("veh_pal_ope", dividirListas("veh_bal_tot_pas_anu", "veh_bal_tot_act_anu",true));
        manejador.guardarVariable("veh_pal_apa", dividirListas("veh_bal_tot_pas_anu", "veh_bal_tot_pat_anu",true));
        manejador.guardarVariable("veh_liq_anu", dividirListas("veh_bal_tot_act_anu", "veh_bal_cta_por_pag_anu",false));
        manejador.guardarVariable("veh_cap_tra_anu", restarListas("veh_bal_tot_act_anu", "veh_bal_cta_por_pag_anu"));
        manejador.guardarVariable("veh_cob_deu", dividirListas("veh_bal_tot_act_anu", "veh_bal_tot_pas_anu",false));
        manejador.guardarVariable("veh_rot_inv", dividirListas("veh_edo_cto_vta_anu", "veh_bal_inv_viv_anu",false));
        manejador.guardarVariable("veh_dia_cta_por_cob", multiplicarLista(360,dividirListas("veh_bal_cta_cob_anu", "veh_edo_vta_viv_anu",false)));
        manejador.guardarVariable("veh_dia_cta_por_pag", multiplicarLista(360,dividirListas("veh_bal_cta_por_pag_anu", "veh_edo_cto_vta_anu",false)));
        manejador.guardarVariable("veh_cob_ser_deu", dividirListas("veh_bal_efe_anu", "veh_cet_int_cre_pte_anu",false));
       manejador.guardarVariable("veh_mar_ope", dividirListas("veh_total_utl_oprt_anu", "veh_edo_vta_viv_anu",true));
       manejador.guardarVariable("veh_mar_ant_imp", dividirListas("veh_utl_a_ipt_anu", "veh_edo_vta_viv_anu",true));
       manejador.guardarVariable("veh_mar_net", dividirListas("veh_utl_net_anu", "veh_edo_vta_viv_anu",true));
       
       calcularParametrosAnuales();
       calcularParametrosGraficacion();
    }

    
    private void calcularParametrosGraficacion(){
        String[] nombreAnual=new String[]{"veh_cet_uni_edf_anu","veh_cet_uni_edf_acu_anu","veh_cet_uni_dis_anu"
        ,"veh_cet_uni_dis_acu_anu","veh_cet_uni_ven_anu","veh_cet_uni_ven_acu_anu"};
        String[] nombreMensual=new String[]{"veh_cet_uni_edf","veh_cet_uni_edf_acu","veh_cet_uni_dis"
        ,"veh_cet_uni_dis_acu","veh_cet_uni_ven","veh_cet_uni_ven_acu"};
        for(int t=0;t<nombreAnual.length;t++){
            manejador.guardarVariable(nombreAnual[t]+"_graf", manejador.obtenerVariable(nombreMensual[t]));
        }
    }
    private List<Double> dividirListas(String dividendo, String divisor , boolean porcentaje) {
        List<Double> div = (List<Double>) manejador.obtenerVariable(dividendo);
        List<Double> diviso = (List<Double>) manejador.obtenerVariable(divisor);
        List<Double> nueva = new LinkedList<Double>();
        for (int t = 0; t < div.size(); t++) {
            try {
                if (diviso.get(t) == 0) {
                    nueva.add(0.0);
                } else {
                    if(porcentaje){
                        
                    nueva.add(div.get(t) / diviso.get(t) * 100);
                    }else{
                        
                    nueva.add(div.get(t) / diviso.get(t) );
                    }
                }
            }catch(IndexOutOfBoundsException e){
                nueva.add(0.0);
            }
            
        }
        return nueva;
    }

    private Object restarListas(String primera , String segunda ) {
       List<Double> uno=(List<Double>) manejador.obtenerVariable(primera);
       List<Double> dos=(List<Double>) manejador.obtenerVariable(segunda);
       List<Double> listas = new LinkedList <Double>();
       for(int t=0;t<uno.size();t++){
           double primero=0;
           double segundo=0;
           try{
               primero=uno.get(t);
               segundo=dos.get(t);
           }catch(IndexOutOfBoundsException e){
               
           }
           finally{
               listas.add(primero-segundo);
           }
       }
       return listas;
    }

    private List<Double> multiplicarLista(double i, List<Double> dividirListas) {
        List<Double> nueva=new LinkedList<Double>();
        for(Double d:dividirListas){
            nueva.add(d*i);
        }
        return nueva;
    }

    private void calcularParametrosAnuales() {
        
        String[] nombreAnual=new String[]{"veh_cet_uni_edf_anu","veh_cet_uni_edf_acu_anu","veh_cet_uni_dis_anu"
        ,"veh_cet_uni_dis_acu_anu","veh_cet_uni_ven_anu","veh_cet_uni_ven_acu_anu"};
        String[] nombreMensual=new String[]{"veh_cet_uni_edf","veh_cet_uni_edf_acu","veh_cet_uni_dis"
        ,"veh_cet_uni_dis_acu","veh_cet_uni_ven","veh_cet_uni_ven_acu"};
        for(int t=0;t<nombreAnual.length;t++){
            List<Double> lista=generarListaParametros(nombreMensual[t]);
            manejador.guardarVariable(nombreAnual[t], lista);
            System.out.println(nombreAnual[t]+" "+manejador.obtenerVariable(nombreAnual[t]));
        }
    }
    
    private List<Double> generarListaParametros(String nombreMensual) {
        List<Double> utilidadPeriodo = new LinkedList<Double>();
        utilidadPeriodo.add(sumarValoresMatriz((MatrizBidimensional) manejador.obtenerVariable(nombreMensual), 0, 11));
        utilidadPeriodo.add(sumarValoresMatriz((MatrizBidimensional) manejador.obtenerVariable(nombreMensual), 12, 23));
        utilidadPeriodo.add(sumarValoresMatriz((MatrizBidimensional) manejador.obtenerVariable(nombreMensual), 24, 35));
        utilidadPeriodo.add(sumarValoresMatriz((MatrizBidimensional) manejador.obtenerVariable(nombreMensual), 36, 47));
        utilidadPeriodo.add(sumarValoresMatriz((MatrizBidimensional) manejador.obtenerVariable(nombreMensual), 48, 59));
        return utilidadPeriodo;
    }

    private MatrizBidimensional sumar1a1(String[] split) {
        List<MatrizBidimensional> lista=new LinkedList<MatrizBidimensional>();
        for(String s:split){
            lista.add((MatrizBidimensional)manejador.obtenerVariable(s));
            System.out.println("matriz "+s+" "+manejador.obtenerVariable(s));
        }
        List<CeldaFechaValor> celdas=new LinkedList<CeldaFechaValor>();
        for(int t=0;t<lista.get(0).getCeldas().size();t++){
        double suma=0;
            for(MatrizBidimensional m:lista){
                suma+=m.getCeldas().get(t).getValor();
            }
            celdas.add(new CeldaFechaValor(lista.get(0).getCeldas().get(t).getFecha(),suma));
        }
        MatrizBidimensional m=new MatrizBidimensional();
        m.setCeldas(celdas);
        return m;
    }

    

    



    
}
