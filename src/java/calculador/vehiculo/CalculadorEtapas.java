package calculador.vehiculo;

import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import pojos.PryVeh;
import util.CeldaFechaValor;
import util.Funciones;
import util.MatrizBidimensional;
import util.vehiculo.EtapaCapturaVehiculo;
import util.vehiculo.ListaEtapasVehiculo;
import variables.ManejadorVariablesVehiculo;

/**
 * 
 * @author alberto
 */
public class CalculadorEtapas {

    private List<PryVeh> proyectos;
    private final ManejadorVariablesVehiculo manejador;

    CalculadorEtapas(List<PryVeh> proyectos, ManejadorVariablesVehiculo manejador) {
        this.proyectos = proyectos;
        this.manejador = manejador;

    }

    public void procesar() {
        meterPremisaD35();
        calcularVehUniEdfMes();
        calcularMatrizUnidadesEdificadas();
        calcularMatrizUnidadesEdificadasAcumuladas();
        calcularMatrizUnidadesDisponibles();
        calcularMatrizUnidadesDisponiblesAcumuladas();
        calcularMatrizUnidadesVendidas();
        calcularMatrizUnidadesVendidasAcumuladas();
        calcularMatrizFirmaContratoCompraVenta();
        calcularMatrizEnganche();
        calcularMatrizCostoDeVenta();
        calcularMatrizGAV();
        calcularMatrizGastosIndividuales();
        calcularMatrizAdministracionProyecto();
        calcularMatrizIngresos();
        calcularMatrizEgresos();
        calcularMatrizMargenReserva();
    }

    private void calcularVehUniEdfMes() {
        for (int indice = 0; indice < proyectos.size(); indice++) {
            manejador.guardarVariable("veh_uni_edf_mes[" + indice + "]", obtenerVentasMeses(proyectos.get(indice), indice));
            MatrizBidimensional[] obtenerVariable = (MatrizBidimensional[]) manejador.obtenerVariable("veh_uni_edf_mes[" + indice + "]");
            MatrizBidimensional[] arr=(MatrizBidimensional[]) manejador.obtenerVariable("veh_uni_edf_mes[" + indice + "]");
            
        }

    }

    private MatrizBidimensional[] obtenerVentasMeses(PryVeh proyecto, int indiceProyecto) {
        MatrizBidimensional[] arregloMatrices = new MatrizBidimensional[proyecto.getEtpVehs().size() / 3];
        int consecutivo = 0;
        while (consecutivo < arregloMatrices.length) {
            arregloMatrices[consecutivo] = generarMatrizVentasMeses((ListaEtapasVehiculo[]) manejador.obtenerVariable("veh_eta"), consecutivo, indiceProyecto);
            consecutivo++;
        }
        return arregloMatrices;
    }

    public void imprimirFechas(){
        ListaEtapasVehiculo[] arr=(ListaEtapasVehiculo[]) manejador.obtenerVariable("veh_eta");
        EtapaCapturaVehiculo get = arr[0].getListaEtapas().get(0);
    }
    
    private MatrizBidimensional generarMatrizVentasMeses(ListaEtapasVehiculo[] listaEtapas, int numeroEtapa, int indiceProyecto) {
        EtapaCapturaVehiculo etapaActual = listaEtapas[indiceProyecto].getListaEtapas().get(numeroEtapa);
        Date fechaInicial = (Date) (etapaActual.getFechaInicioEtapa()).clone();
        Date fechaFinal = (etapaActual.getFechaFinEtapa());
        int diferenciaMeses = Funciones.diferenciaMeses(fechaInicial, fechaFinal);
        MatrizBidimensional matriz = new MatrizBidimensional();
        int suma = 0;
        int tmpDiferencia = diferenciaMeses;
        while (tmpDiferencia > 1) {
            Calendar instance = Calendar.getInstance();
            instance.setTime(fechaInicial);
            fechaInicial.setMonth(fechaInicial.getMonth() + 1);
            double valor = Math.round((double)etapaActual.getViviendasEtapa() / (double)diferenciaMeses);
            
            matriz.agregarCelda(new CeldaFechaValor(instance, Math.round(valor)));
            tmpDiferencia--;
            suma += Math.round(valor);
        }
        Calendar instance = Calendar.getInstance();
        instance.setTime(fechaFinal);
        matriz.agregarCelda(new CeldaFechaValor(instance, etapaActual.getViviendasEtapa() - suma));
        return matriz;
    }

    private void calcularMatrizUnidadesEdificadas() {
        for (int indiceProyecto = 0; indiceProyecto < proyectos.size(); indiceProyecto++) {
            int numeroEtapas = proyectos.get(indiceProyecto).getEtpVehs().size() / 3;
            for (int indiceEtapa = 0; indiceEtapa < numeroEtapas; indiceEtapa++) {
                manejador.guardarVariable("veh_uni_edf_pry[" + indiceProyecto + "][" + indiceEtapa + "]", generarEdificacionMatrices(indiceProyecto, indiceEtapa));
            }
        }
    }

    private MatrizBidimensional generarEdificacionMatrices(int indiceProyecto, int indiceEtapa) {
        ListaEtapasVehiculo[] etapasVehiculo = (ListaEtapasVehiculo[]) manejador.obtenerVariable("veh_eta");
        MatrizBidimensional[] matricesEdificadasMes = (MatrizBidimensional[]) manejador.obtenerVariable("veh_uni_edf_mes[" + indiceProyecto + "]");
        MatrizBidimensional[] matricesPorcentajeEdificacion = (MatrizBidimensional[]) manejador.obtenerVariable("veh_dis_edf");
        EtapaCapturaVehiculo etapa = etapasVehiculo[indiceProyecto].getListaEtapas().get(indiceEtapa);
        MatrizBidimensional matrizEdificacionMes = matricesEdificadasMes[indiceEtapa];
        MatrizBidimensional matrizPorcentajeEdificacion = matricesPorcentajeEdificacion[indiceProyecto];
        return sacarMatrizEdificacionProyectoEtapa(etapa, matrizEdificacionMes, matrizPorcentajeEdificacion);
    }

    private MatrizBidimensional sacarMatrizEdificacionProyectoEtapa(EtapaCapturaVehiculo etapa, MatrizBidimensional matrizEdificacionMes, MatrizBidimensional matrizPorcentajeEdificacion) {

        int tamColumnas = matrizEdificacionMes.getCeldas().size() + matrizPorcentajeEdificacion.getCeldas().size();
        int tamRenglones = matrizEdificacionMes.getCeldas().size();
        List<Double[]> listaArreglos = new LinkedList<Double[]>();
        int indiceMovido = 0;
        for (int indiceRenglon = 0; indiceRenglon < tamRenglones; indiceRenglon++) {
            Double[] array = new Double[tamColumnas];
            int indicePorcentaje = 0;
            for (int indiceArreglo = 0; indiceArreglo < tamColumnas; indiceArreglo++) {
                if (indicePorcentaje < matrizPorcentajeEdificacion.getCeldas().size() && indiceArreglo >= indiceMovido) {
                    double numeroCasas = matrizEdificacionMes.getCeldas().get(indiceRenglon).getValor();
                    double porcentaje = matrizPorcentajeEdificacion.getCeldas().get(indicePorcentaje).getValor();
                    array[indiceArreglo] = (numeroCasas * (porcentaje * .01));
                    indicePorcentaje++;
                } else {
                    array[indiceArreglo] = 0.0;
                }
            }
            listaArreglos.add(array);
            indiceMovido++;
        }
        return sumarMatricesVendidas(etapa, listaArreglos);
    }

    private MatrizBidimensional sumarMatricesVendidas(EtapaCapturaVehiculo etapaActual, List<Double[]> listaArreglos) {
        Date fechaInicial = (Date) (etapaActual.getFechaInicioEtapa()).clone();
        Date fechaFinal = (etapaActual.getFechaFinEtapa());
        MatrizBidimensional matriz = new MatrizBidimensional();
        for (int t = 0; t < listaArreglos.get(0).length; t++) {
            double suma = 0;
            for (Double[] arreglo : listaArreglos) {
                suma += arreglo[t];
            }
            Calendar instacia = Calendar.getInstance();
            instacia.setTime(fechaInicial);
            CeldaFechaValor celda = new CeldaFechaValor(instacia, (suma));
            matriz.agregarCelda(celda);
            fechaInicial.setMonth(fechaInicial.getMonth() + 1);
        }
        return matriz;
    }

    private void calcularMatrizUnidadesEdificadasAcumuladas() {
        for (int indiceProyecto = 0; indiceProyecto < proyectos.size(); indiceProyecto++) {
            int numeroEtapas = proyectos.get(indiceProyecto).getEtpVehs().size() / 3;
            for (int indiceEtapa = 0; indiceEtapa < numeroEtapas; indiceEtapa++) {
                manejador.guardarVariable("veh_uni_edf_acu[" + indiceProyecto + "][" + indiceEtapa + "]", generarEdificacionMatricesAcumuladas(indiceProyecto, indiceEtapa));
            }
        }
    }

    private void calcularMatrizUnidadesDisponibles() {
        for (int indice = 0; indice < proyectos.size(); indice++) {
            MatrizBidimensional[] edificacionMes = (MatrizBidimensional[]) manejador.obtenerVariable("veh_uni_edf_mes[" + indice + "]");
            PryVeh proyecto = proyectos.get(indice);
            for (int indiceEtapa = 0; indiceEtapa < (proyecto.getEtpVehs().size() / 3); indiceEtapa++) {
                manejador.guardarVariable("veh_uni_dis[" + indice + "][" + indiceEtapa + "]", sacarMatrizUnidadesDisponibles(indice, indiceEtapa, edificacionMes[indiceEtapa]));
                
            }
        }
    }

    private MatrizBidimensional sacarMatrizUnidadesDisponibles(int indiceProyecto, int indiceEtapa, MatrizBidimensional matrizBidimensionalEdificacionMeses) {
        MatrizBidimensional[] matricesPorcentajeEdificacion = (MatrizBidimensional[]) manejador.obtenerVariable("veh_dis_edf");
        MatrizBidimensional matrizPorcentajeEdificacion = matricesPorcentajeEdificacion[indiceProyecto];
        int mesesAdelanto = matrizPorcentajeEdificacion.getCeldas().size() - 1;
        List<CeldaFechaValor> celdasEdificacionMeses = matrizBidimensionalEdificacionMeses.getCeldas();
        List<CeldaFechaValor> nuevaLista = new LinkedList<CeldaFechaValor>();
        Date fecha = celdasEdificacionMeses.get(0).getFecha().getTime();
        
        //fecha.setMonth(fecha.getMonth() + mesesAdelanto);
        for (CeldaFechaValor c : celdasEdificacionMeses) {
            Calendar instancia = Calendar.getInstance();
            instancia.setTime(fecha);
            fecha.setMonth(fecha.getMonth()+1);
            CeldaFechaValor nueva = new CeldaFechaValor(instancia, Math.round(c.getValor()));
            nuevaLista.add(nueva);
        }
        MatrizBidimensional retorno = new MatrizBidimensional();
        retorno.setCeldas(nuevaLista);
        return retorno;
    }

    private void calcularMatrizUnidadesDisponiblesAcumuladas() {
        for (int indiceProyecto = 0; indiceProyecto < proyectos.size(); indiceProyecto++) {
            PryVeh proyecto = proyectos.get(indiceProyecto);
            for (int indiceEtapa = 0; indiceEtapa < (proyecto.getEtpVehs().size() / 3); indiceEtapa++) {
                manejador.guardarVariable("veh_uni_dis_acu[" + indiceProyecto + "][" + indiceEtapa + "]", generarMatrizDisponiblesAcumuladas(indiceProyecto, indiceEtapa));
            }
        }
    }

    private MatrizBidimensional generarMatrizDisponiblesAcumuladas(int indiceProyecto, int indiceEtapa) {
        MatrizBidimensional disponibles = (MatrizBidimensional) manejador.obtenerVariable("veh_uni_dis[" + indiceProyecto + "][" + indiceEtapa + "]");
        List<CeldaFechaValor> nuevaLista = new LinkedList<CeldaFechaValor>();
        nuevaLista.add(disponibles.getCeldas().get(0));
        for (int t = 1; t < disponibles.getCeldas().size(); t++) {
            double valorSumar = disponibles.getCeldas().get(t).getValor();
            double valorAnterior = nuevaLista.get(t - 1).getValor();
            nuevaLista.add(new CeldaFechaValor(disponibles.getCeldas().get(t).getFecha(),Math.round( valorAnterior + valorSumar)));
        }
        MatrizBidimensional matriz = new MatrizBidimensional();
        matriz.setCeldas(nuevaLista);
        return matriz;
    }

    private void calcularMatrizUnidadesVendidas() {
        for (int indiceProyecto = 0; indiceProyecto < proyectos.size(); indiceProyecto++) {
            PryVeh proyecto = proyectos.get(indiceProyecto);
            for (int indiceEtapa = 0; indiceEtapa < (proyecto.getEtpVehs().size() / 3); indiceEtapa++) {
                manejador.guardarVariable("veh_uni_ven[" + indiceProyecto + "][" + indiceEtapa + "]", generarMatrizVentas(indiceProyecto, indiceEtapa));
            }
        }
    }

    private Object generarMatrizVentas(int indiceProyecto, int indiceEtapa){
        EtapaCapturaVehiculo etapaActual = ((ListaEtapasVehiculo[])manejador.obtenerVariable("veh_eta"))[indiceProyecto].getListaEtapas().get(indiceEtapa);
        
        MatrizBidimensional[] matricesComercializacion=(MatrizBidimensional[])manejador.obtenerVariable("veh_dis_cmr");
        
        MatrizBidimensional comercializacionProyecto=matricesComercializacion[indiceProyecto];
        
        List<CeldaFechaValor> listaFechaValor=new LinkedList<CeldaFechaValor>();
        
        Date fecha=(Date) (etapaActual.getFechaInicioEtapa()).clone();
        fecha.setMonth(fecha.getMonth()+diferenciaFechasVentasConstruccion(indiceProyecto,indiceEtapa));
        
        for(CeldaFechaValor celda:comercializacionProyecto.getCeldas()){
            Calendar instancia=Calendar.getInstance();
            instancia.setTime(fecha);
            CeldaFechaValor nuevaCeldaFechaValor=new CeldaFechaValor(instancia, Math.round((celda.getValor()*.01)*etapaActual.getViviendasEtapa()));
            listaFechaValor.add(nuevaCeldaFechaValor);
            fecha.setMonth(fecha.getMonth()+1);
        }
        MatrizBidimensional matriz=new MatrizBidimensional();
        matriz.setCeldas(listaFechaValor);
        return matriz;
    }

    private void calcularMatrizUnidadesVendidasAcumuladas() {
        for (int indiceProyecto = 0; indiceProyecto < proyectos.size(); indiceProyecto++) {
            PryVeh proyecto = proyectos.get(indiceProyecto);
            for (int indiceEtapa = 0; indiceEtapa < (proyecto.getEtpVehs().size() / 3); indiceEtapa++) {
                manejador.guardarVariable("veh_uni_ven_acu[" + indiceProyecto + "][" + indiceEtapa + "]", generarMatrizVentasAcumuladas(indiceProyecto, indiceEtapa));
            }
        }
    }

    private Object generarMatrizVentasAcumuladas(int indiceProyecto, int indiceEtapa) {
        MatrizBidimensional matrizVentas = (MatrizBidimensional) manejador.obtenerVariable("veh_uni_ven["+indiceProyecto+"]["+indiceEtapa+"]");
        List<CeldaFechaValor> listaCeldaFechaValor=new LinkedList<CeldaFechaValor>();
        listaCeldaFechaValor.add(new CeldaFechaValor(matrizVentas.getCeldas().get(0).getFecha(), matrizVentas.getCeldas().get(0).getValor()));
        for(int t=1;t<matrizVentas.getCeldas().size();t++){
            CeldaFechaValor nueva=new CeldaFechaValor(matrizVentas.getCeldas().get(t).getFecha(),Math.round(listaCeldaFechaValor.get(t-1).getValor()+matrizVentas.getCeldas().get(t).getValor()));
            listaCeldaFechaValor.add(nueva);
        }
        MatrizBidimensional matriz=new MatrizBidimensional();
        matriz.setCeldas(listaCeldaFechaValor);
        return matriz;
    }

    private void calcularMatrizFirmaContratoCompraVenta() {
        for (int indiceProyecto = 0; indiceProyecto < proyectos.size(); indiceProyecto++) {
            PryVeh proyecto = proyectos.get(indiceProyecto);
            for (int indiceEtapa = 0; indiceEtapa < (proyecto.getEtpVehs().size() / 3); indiceEtapa++) {
                manejador.guardarVariable("veh_fir_ctr_cpr_vta[" + indiceProyecto + "][" + indiceEtapa + "]", generarMatrizFirmaContratoCompraVenta(indiceProyecto, indiceEtapa));
            }
        }
    }

    private Object generarMatrizFirmaContratoCompraVenta(int indiceProyecto, int indiceEtapa) {
        double miles=.001;
        Object[] valoresViviendas=(Object[])manejador.obtenerVariable("veh_val_pro_viv");
        Double precioPromedio= Double.parseDouble(String.valueOf(valoresViviendas[indiceProyecto]));
        Object[] anticiposVentas=(Object[])manejador.obtenerVariable("veh_ant_vta");
        Double porcentajeAnticipo= Double.parseDouble(String.valueOf(anticiposVentas[indiceProyecto]));
        double precioSinAnticipo=precioPromedio*((100-porcentajeAnticipo)*.01);
        MatrizBidimensional matrizVentas = (MatrizBidimensional) manejador.obtenerVariable("veh_uni_ven["+indiceProyecto+"]["+indiceEtapa+"]");
        List<CeldaFechaValor> listaFechaValor=new LinkedList<CeldaFechaValor>();
        for(int t=0;t<matrizVentas.getCeldas().size();t++){
            double sinMiles=(matrizVentas.getCeldas().get(t).getValor()*precioSinAnticipo);
            CeldaFechaValor nuevaCelda=new CeldaFechaValor(matrizVentas.getCeldas().get(t).getFecha(), Funciones.redondearDecimales(sinMiles*miles,3));
            listaFechaValor.add(nuevaCelda);
        }
        MatrizBidimensional m=new MatrizBidimensional();
        m.setCeldas(listaFechaValor);
        return m;
    }

    private void calcularMatrizEnganche() {
        for (int indiceProyecto = 0; indiceProyecto < proyectos.size(); indiceProyecto++) {
            PryVeh proyecto = proyectos.get(indiceProyecto);
            for (int indiceEtapa = 0; indiceEtapa < (proyecto.getEtpVehs().size() / 3); indiceEtapa++) {
                manejador.guardarVariable("veh_des_ini[" + indiceProyecto + "][" + indiceEtapa + "]", generarMatrizEnganche(indiceProyecto, indiceEtapa));
            }
        }
    }

    private Object generarMatrizEnganche(int indiceProyecto, int indiceEtapa) {
        Object[] valoresViviendas=(Object[])manejador.obtenerVariable("veh_val_pro_viv");
        Double precioPromedio= Double.parseDouble(String.valueOf(valoresViviendas[indiceProyecto]));
        Object[] anticiposVentas=(Object[])manejador.obtenerVariable("veh_ant_vta");
        Double porcentajeAnticipo= Double.parseDouble(String.valueOf(anticiposVentas[indiceProyecto]));
        Object[] mesesAnticipo=(Object[])manejador.obtenerVariable("veh_mes_cnf_ant");
        Double mesAnticipo= Double.parseDouble(String.valueOf(mesesAnticipo[indiceProyecto]));
        double anticipo=precioPromedio*((porcentajeAnticipo)*.01);
        return crearMatrizEnganche(anticipo,mesAnticipo,indiceProyecto,indiceEtapa);
    }

      private MatrizBidimensional crearMatrizEnganche(double anticipo,double mesesAnticipo,int indiceProyecto, int indiceEtapa) {
        MatrizBidimensional ventas = (MatrizBidimensional) manejador.obtenerVariable("veh_uni_ven["+indiceProyecto+"]["+indiceEtapa+"]");
          List<CeldaFechaValor> lista=new LinkedList<CeldaFechaValor>();
          CeldaFechaValor primerCelda = ventas.getCeldas().get(0);
          Date fechaPrimera = primerCelda.getFecha().getTime();
          fechaPrimera.setMonth(fechaPrimera.getMonth()-(int)mesesAnticipo);
          for(int indiceCelda=0-(int)mesesAnticipo+1;indiceCelda<ventas.getCeldas().size();indiceCelda++){
            double primerValor=indiceCelda<0 ? 0 : ventas.getCeldas().get(indiceCelda).getValor();
            indiceCelda++;
            double segundoValor=indiceCelda<0 ? 0 : indiceCelda==ventas.getCeldas().size() ? 0 : ventas.getCeldas().get(indiceCelda).getValor();
            Calendar instancia=Calendar.getInstance();
            instancia.setTime(fechaPrimera);
            CeldaFechaValor nueva=formarCeldaEnganche(instancia,anticipo,primerValor,segundoValor,mesesAnticipo);
            lista.add(nueva);
            fechaPrimera.setMonth(fechaPrimera.getMonth()+1);
            indiceCelda--;
          }
          MatrizBidimensional m=new MatrizBidimensional();
          m.setCeldas(lista);
          return m;
      }
      
       private CeldaFechaValor formarCeldaEnganche(Calendar instancia, double anticipo, double prim, double segundoValor, double mesesAnticipo) {
        double primeraParte=anticipo*prim/mesesAnticipo/1000;
        
        double segundaParte=anticipo*segundoValor/mesesAnticipo/1000;
        
        CeldaFechaValor celda=new CeldaFechaValor(instancia, (Funciones.redondearDecimales(primeraParte,3))+(Funciones.redondearDecimales(segundaParte,3)) );
        return celda;
       }
    
    
    private void calcularMatrizCostoDeVenta() {
        for (int indiceProyecto = 0; indiceProyecto < proyectos.size(); indiceProyecto++) {
            PryVeh proyecto = proyectos.get(indiceProyecto);
            for (int indiceEtapa = 0; indiceEtapa < (proyecto.getEtpVehs().size() / 3); indiceEtapa++) {
                manejador.guardarVariable("veh_cto_vta[" + indiceProyecto + "][" + indiceEtapa + "]", generarMatrizCostoVenta(indiceProyecto, indiceEtapa));
            }
        }
    }

    private MatrizBidimensional generarMatrizCostoVenta(int indiceProyecto, int indiceEtapa) {
        Object[] valoresTierras=(Object[])manejador.obtenerVariable("veh_trr");
        Double porcentajeTierra= Double.parseDouble(String.valueOf(valoresTierras[indiceProyecto]))*.01;
        Object[] valoresUrbanizacion=(Object[])manejador.obtenerVariable("veh_urb");
        Double porcentajeUrbanizacion= Double.parseDouble(String.valueOf(valoresUrbanizacion[indiceProyecto]))*.01;
        Object[] valoresEdificacion=(Object[])manejador.obtenerVariable("veh_edf");
        Double porcentajeEdificacion= Double.parseDouble(String.valueOf(valoresEdificacion[indiceProyecto]))*.01;
        Object[] valoresViviendas=(Object[])manejador.obtenerVariable("veh_val_pro_viv");
        Double precioPromedio= Double.parseDouble(String.valueOf(valoresViviendas[indiceProyecto]));
        Double valorCostoVenta=(precioPromedio*porcentajeTierra)+(precioPromedio*porcentajeUrbanizacion)+(precioPromedio*porcentajeEdificacion);
        return calcularMatrizCostoVenta(valorCostoVenta,indiceProyecto,indiceEtapa);
    }

    private MatrizBidimensional calcularMatrizCostoVenta(Double valorCostoVenta, int indiceProyecto, int indiceEtapa) {
         MatrizBidimensional matrizEdificacion = (MatrizBidimensional) manejador.obtenerVariable("veh_uni_edf_pry["+indiceProyecto+"]["+indiceEtapa+"]");
         List<CeldaFechaValor> listaCeldaFechaValor=new LinkedList<CeldaFechaValor>();
         for(CeldaFechaValor c:matrizEdificacion.getCeldas()){
              CeldaFechaValor nueva=new CeldaFechaValor(c.getFecha(),Funciones.redondearDecimales(c.getValor()*valorCostoVenta*.001,3));
             listaCeldaFechaValor.add(nueva);
         }
         MatrizBidimensional m=new MatrizBidimensional();
         m.setCeldas(listaCeldaFechaValor);
         return m;
    }

    private void calcularMatrizGAV() {
        for (int indiceProyecto = 0; indiceProyecto < proyectos.size(); indiceProyecto++) {
            PryVeh proyecto = proyectos.get(indiceProyecto);
            for (int indiceEtapa = 0; indiceEtapa < (proyecto.getEtpVehs().size() / 3); indiceEtapa++) {
                manejador.guardarVariable("veh_cto_gav[" + indiceProyecto + "][" + indiceEtapa + "]", generarMatrizCostoGav(indiceProyecto, indiceEtapa));
            }
        }
    }

    private Object generarMatrizCostoGav(int indiceProyecto, int indiceEtapa) {
        Object[] valoresTierras=(Object[])manejador.obtenerVariable("veh_gav");
        Double porcentajeTierra= Double.parseDouble(String.valueOf(valoresTierras[indiceProyecto]))*.01;
        Object[] valoresViviendas=(Object[])manejador.obtenerVariable("veh_val_pro_viv");
        Double precioPromedio= Double.parseDouble(String.valueOf(valoresViviendas[indiceProyecto]));
        Double valorCostoVenta=(precioPromedio*porcentajeTierra);
        return calcularMatrizGAV(valorCostoVenta,indiceProyecto,indiceEtapa);
    }

    private Object calcularMatrizGAV(Double valorCostoVenta, int indiceProyecto, int indiceEtapa) {
        MatrizBidimensional matrizEdificacion = (MatrizBidimensional) manejador.obtenerVariable("veh_uni_ven["+indiceProyecto+"]["+indiceEtapa+"]");
         List<CeldaFechaValor> listaCeldaFechaValor=new LinkedList<CeldaFechaValor>();
         for(CeldaFechaValor c:matrizEdificacion.getCeldas()){
             CeldaFechaValor nueva=new CeldaFechaValor(c.getFecha(),Funciones.redondearDecimales(c.getValor()*valorCostoVenta*.001, 3));
             listaCeldaFechaValor.add(nueva);
         }
         MatrizBidimensional m=new MatrizBidimensional();
         m.setCeldas(listaCeldaFechaValor);
         return m;
    }

    private void calcularMatrizGastosIndividuales() {
        for (int indiceProyecto = 0; indiceProyecto < proyectos.size(); indiceProyecto++) {
            PryVeh proyecto = proyectos.get(indiceProyecto);
            for (int indiceEtapa = 0; indiceEtapa < (proyecto.getEtpVehs().size() / 3); indiceEtapa++) {
                manejador.guardarVariable("veh_cto_gas_ind[" + indiceProyecto + "][" + indiceEtapa + "]", generarMatrizCostoGasInd(indiceProyecto, indiceEtapa));
            }
        }
    }

    private Object generarMatrizCostoGasInd(int indiceProyecto, int indiceEtapa) {
        Object[] valoresTierras=(Object[])manejador.obtenerVariable("veh_gts_ind");
        Double porcentajeTierra= Double.parseDouble(String.valueOf(valoresTierras[indiceProyecto]))*.01;
        Object[] valoresViviendas=(Object[])manejador.obtenerVariable("veh_val_pro_viv");
        Double precioPromedio= Double.parseDouble(String.valueOf(valoresViviendas[indiceProyecto]));
        Double valorCostoVenta=(precioPromedio*porcentajeTierra);
        return calcularMatrizGAV(valorCostoVenta,indiceProyecto,indiceEtapa);
    }

    private void calcularMatrizAdministracionProyecto() {
        for (int indiceProyecto = 0; indiceProyecto < proyectos.size(); indiceProyecto++) {
            PryVeh proyecto = proyectos.get(indiceProyecto);
            for (int indiceEtapa = 0; indiceEtapa < (proyecto.getEtpVehs().size() / 3); indiceEtapa++) {
                manejador.guardarVariable("veh_adm_pry[" + indiceProyecto + "][" + indiceEtapa + "]", generarMatrizAdministracionProyecto(indiceProyecto, indiceEtapa));
            }
        }
    }

    private Object generarMatrizAdministracionProyecto(int indiceProyecto, int indiceEtapa) {
        Object[] valoresTierras=(Object[])manejador.obtenerVariable("veh_app");
        Double adminProyecto= Double.parseDouble(String.valueOf(valoresTierras[indiceProyecto]))*.01;
        MatrizBidimensional matriz = (MatrizBidimensional) manejador.obtenerVariable("veh_cto_vta["+indiceProyecto+"]["+indiceEtapa+"]");
        List<CeldaFechaValor> lista=new LinkedList<CeldaFechaValor>();
        for(CeldaFechaValor c:matriz.getCeldas()){
            CeldaFechaValor nueva=new CeldaFechaValor(c.getFecha(),Funciones.redondearDecimales(c.getValor()*adminProyecto,3));
            lista.add(nueva);
        }
        MatrizBidimensional m=new MatrizBidimensional();
        m.setCeldas(lista);
        return m;
    }

    private void calcularMatrizEgresos() {
        for (int indiceProyecto = 0; indiceProyecto < proyectos.size(); indiceProyecto++) {
            PryVeh proyecto = proyectos.get(indiceProyecto);
            for (int indiceEtapa = 0; indiceEtapa < (proyecto.getEtpVehs().size() / 3); indiceEtapa++) {
                manejador.guardarVariable("veh_egr[" + indiceProyecto + "][" + indiceEtapa + "]", generarMatrizEgresos(indiceProyecto, indiceEtapa));
            }
        }
    }

    private Object generarMatrizEgresos(int indiceProyecto, int indiceEtapa) {
          MatrizBidimensional costoVenta = (MatrizBidimensional) manejador.obtenerVariable("veh_cto_vta["+indiceProyecto+"]["+indiceEtapa+"]");
       MatrizBidimensional costoGAV=(MatrizBidimensional) manejador.obtenerVariable("veh_cto_gav["+indiceProyecto+"]["+indiceEtapa+"]");
       MatrizBidimensional costoGasInd=(MatrizBidimensional) manejador.obtenerVariable("veh_cto_gas_ind["+indiceProyecto+"]["+indiceEtapa+"]");
       MatrizBidimensional costoAdmin=(MatrizBidimensional) manejador.obtenerVariable("veh_adm_pry["+indiceProyecto+"]["+indiceEtapa+"]");
       MatrizBidimensional[] todas=new MatrizBidimensional[]{costoVenta,costoGAV,costoGasInd,costoAdmin};
       Date primerFecha=costoAdmin.getCeldas().get(0).getFecha().getTime();
       int[] indices=new int[todas.length];
       List<CeldaFechaValor> lista=new LinkedList<CeldaFechaValor>();
       for(int t=0;t<costoAdmin.getCeldas().size();t++){
           double suma=0;
           for(int indiceArreglo=0;indiceArreglo<todas.length;indiceArreglo++){
               MatrizBidimensional matrizAcual=todas[indiceArreglo];
               if(matrizAcual.getCeldas().size()>indices[indiceArreglo] &&
                       matrizAcual.getCeldas().get(indices[indiceArreglo]).getFecha().getTime().getMonth()==primerFecha.getMonth()){
                   suma+=matrizAcual.getCeldas().get(indices[indiceArreglo]).getValor();
                   indices[indiceArreglo]=indices[indiceArreglo]+1;
               }
           }
           Calendar instancia=Calendar.getInstance();
           instancia.setTime(primerFecha);
           primerFecha.setMonth(primerFecha.getMonth()+1);
           CeldaFechaValor celda=new CeldaFechaValor(instancia,suma);
           lista.add(celda);
       }
       MatrizBidimensional m=new MatrizBidimensional();
       m.setCeldas(lista);
       return m;
    }


    private int diferenciaFechasVentasConstruccion(int indiceProyecto, int indiceEtapa) {
        Object[] iniEdf = (Object[]) manejador.obtenerVariable("veh_mes_ini_edf");
        Object[] iniVta = (Object[]) manejador.obtenerVariable("veh_mes_ini_com");
        
        int diferencia= Funciones.diferenciaMeses(Funciones.StringToDate(String.valueOf(iniEdf[indiceProyecto])),Funciones.StringToDate(String.valueOf(iniVta[indiceProyecto])));
         return diferencia-1;
    }


    private void calcularMatrizIngresos() {
        for (int indiceProyecto = 0; indiceProyecto < proyectos.size(); indiceProyecto++) {
            PryVeh proyecto = proyectos.get(indiceProyecto);
            for (int indiceEtapa = 0; indiceEtapa < (proyecto.getEtpVehs().size() / 3); indiceEtapa++) {
                manejador.guardarVariable("veh_ing[" + indiceProyecto + "][" + indiceEtapa + "]", generarMatrizIngresos(indiceProyecto, indiceEtapa));
            }
        }
    }

    private MatrizBidimensional generarMatrizIngresos(int indiceProyecto, int indiceEtapa) {
        MatrizBidimensional enganche = (MatrizBidimensional) manejador.obtenerVariable("veh_des_ini["+indiceProyecto+"]["+indiceEtapa+"]");
        MatrizBidimensional compraVenta = (MatrizBidimensional) manejador.obtenerVariable("veh_fir_ctr_cpr_vta["+indiceProyecto+"]["+indiceEtapa+"]");
        EtapaCapturaVehiculo etapaActual = ((ListaEtapasVehiculo[])manejador.obtenerVariable("veh_eta"))[indiceProyecto].getListaEtapas().get(indiceEtapa);
        Date fechaInicial=(Date) (etapaActual.getFechaInicioEtapa()).clone();
        Date fechaFinal=(etapaActual.getFechaFinEtapa());
        MatrizBidimensional[] todas=new MatrizBidimensional[]{enganche,compraVenta};
        int[] indices=new int[todas.length];
        List<CeldaFechaValor> lista=new LinkedList<CeldaFechaValor>();
        while(fechaInicial.getMonth()!=(fechaFinal.getMonth()) || fechaInicial.getYear()!=fechaFinal.getYear()){
            double suma=0;
            for(int indiceMatrices=0;indiceMatrices<todas.length;indiceMatrices++){
                MatrizBidimensional actual=todas[indiceMatrices];
                if(actual.getCeldas().size()>indices[indiceMatrices] && actual.getCeldas().get(indices[indiceMatrices]).getFecha().getTime().getMonth()==fechaInicial.getMonth()){
                    suma+=actual.getCeldas().get(indices[indiceMatrices]).getValor();
                    indices[indiceMatrices]=indices[indiceMatrices]+1;
                }
            }
            Calendar instancia=Calendar.getInstance();
            instancia.setTime(fechaInicial);
            CeldaFechaValor nueva=new CeldaFechaValor(instancia, suma);
            fechaInicial.setMonth(fechaInicial.getMonth()+1);
            lista.add(nueva);
        }
        Calendar instancia=Calendar.getInstance();
        instancia.setTime(fechaFinal);
        CeldaFechaValor celda=new CeldaFechaValor(instancia,compraVenta.getCeldas().get(compraVenta.getCeldas().size()-1).getValor());
        lista.add(celda);
        MatrizBidimensional m=new MatrizBidimensional();
        m.setCeldas(lista);
        return m;
    }

    private void calcularMatrizMargenReserva() {
        for (int indiceProyecto = 0; indiceProyecto < proyectos.size(); indiceProyecto++) {
            PryVeh proyecto = proyectos.get(indiceProyecto);
            for (int indiceEtapa = 0; indiceEtapa < (proyecto.getEtpVehs().size() / 3); indiceEtapa++) {
                manejador.guardarVariable("veh_mar_res[" + indiceProyecto + "][" + indiceEtapa + "]", generarMatrizMargenReserva(indiceProyecto, indiceEtapa));
            }
        }
    }

    private Object generarMatrizMargenReserva(int indiceProyecto, int indiceEtapa) {
        MatrizBidimensional ingresos = (MatrizBidimensional) manejador.obtenerVariable("veh_ing["+indiceProyecto+"]["+indiceEtapa+"]");
        MatrizBidimensional egresos=(MatrizBidimensional) manejador.obtenerVariable("veh_egr["+indiceProyecto+"]["+indiceEtapa+"]");
        List<CeldaFechaValor> lista=new LinkedList<CeldaFechaValor>();
        for(int t=0;t<ingresos.getCeldas().size();t++){
            if(ingresos.getCeldas().size()<t && egresos.getCeldas().size()<t){
                CeldaFechaValor celda=new CeldaFechaValor(ingresos.getCeldas().get(t).getFecha(),ingresos.getCeldas().get(t).getValor()-egresos.getCeldas().get(t).getValor());
                lista.add(celda);
            }
            
        }
        MatrizBidimensional m=new MatrizBidimensional();
        m.setCeldas(lista);
        return m;
    }

    private Object generarEdificacionMatricesAcumuladas(int indiceProyecto, int indiceEtapa) {
        List<CeldaFechaValor> lista=new LinkedList<CeldaFechaValor>();
        MatrizBidimensional edificadasProyecto = (MatrizBidimensional) manejador.obtenerVariable("veh_uni_edf_pry["+indiceProyecto+"]["+indiceEtapa+"]");
        lista.add(edificadasProyecto.getCeldas().get(0));
        for(int t=1;t<edificadasProyecto.getCeldas().size();t++){
            CeldaFechaValor celda=edificadasProyecto.getCeldas().get(t);
            CeldaFechaValor nueva=new CeldaFechaValor(celda.getFecha(),(celda.getValor()+lista.get(t-1).getValor()));
            lista.add(nueva);
        }
        MatrizBidimensional m=new MatrizBidimensional();
        m.setCeldas(lista);
        return m;
    }

    private void meterPremisaD35() {
        manejador.guardarVariable("d35", sacarPremisaD35());
    }

    private double sacarPremisaD35() {
        double porcentajeCreditoPuente = sumarValores("veh_por_crd_pte")/proyectos.size()*.01;
        double porcentajeApalancamiento= sumarValores("veh_por_apa")/proyectos.size()*.01;
        double promedioValorCasas=sumarValores("veh_val_pro_viv")/proyectos.size();
        double numeroDeCasas=calcularNumeroCasasEtapa();
        double c34=promedioValorCasas*numeroDeCasas/1000;
        double d34=c34*porcentajeApalancamiento;
        return porcentajeCreditoPuente*d34;
    }
   

  private double sumarValores(String llave) {
        Object[] valoresLlave=(Object[])manejador.obtenerVariable(llave);
        double suma=0;
        for(Object ob:valoresLlave){
            Double numero=Double.parseDouble(String.valueOf(ob));
            suma+=numero;
        }
        return suma;
    }

    private double sacarTotalCasasConstruidas() {
         ListaEtapasVehiculo[] arr=(ListaEtapasVehiculo[]) manejador.obtenerVariable("veh_eta");
         double suma=0;
         for(int indiceProyecto=0;indiceProyecto<proyectos.size();indiceProyecto++){
             ListaEtapasVehiculo proyecto=arr[indiceProyecto];
             int numeroEtapas=proyecto.getListaEtapas().size();
             for(int indiceEtapa=0;indiceEtapa<numeroEtapas;indiceEtapa++){
                int viviendasEtapa = proyecto.getListaEtapas().get(indiceEtapa).getViviendasEtapa();
                suma+=viviendasEtapa;
             }
             
         }
         return suma;
    }

    private double calcularNumeroCasasEtapa() {
         double totalCasas=0;
         ListaEtapasVehiculo[] arr=(ListaEtapasVehiculo[]) manejador.obtenerVariable("veh_eta");
         for(int indiceProyecto=0;indiceProyecto<arr.length;indiceProyecto++){
             int numeroEtapas=proyectos.get(indiceProyecto).getEtpVehs().size()/3;
             for(int indiceEtapa=0;indiceEtapa<numeroEtapas;indiceEtapa++){
                EtapaCapturaVehiculo actual=arr[indiceProyecto].getListaEtapas().get(indiceEtapa);
                totalCasas+=actual.getViviendasEtapa();
             }
         }
         double casasAlMes=totalCasas/60;
         double mesesNecesarios=totalCasas/casasAlMes;
         double mesesEtapa=mesesNecesarios/4;
         return Math.round(casasAlMes*mesesEtapa);
    }

    public static void main(String[] args) {
        double valor=(double)557/(double)13;
    }
    
}
