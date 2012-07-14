/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import pojos.MdlVeh;
import pojos.PryVeh;
import pojos.RelMdlVeh;
import variables.ManejadorVariablesVehiculo;

/**
 *
 * @author Alberto
 */
public class Funciones {

    public static String DateToString(Object fecha) {

        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        String string = format.format(fecha);
        return string;
    }
    
    
    public static double sumaProducto(List<Double> precio , List<Double> numeroCasas){
        double numeroCasasTotales=0;
        double producto=0;
        for(int t=0;t<precio.size();t++){
            double casasLocales=numeroCasas.get(t);
            producto+= precio.get(t)*casasLocales;
            numeroCasasTotales+=casasLocales;
        }
        return producto/numeroCasasTotales;
    }

    public static Date StringToDate(String fecha) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
            Date date = format.parse(fecha);
            return date;
        } catch (ParseException ex) {
            Logger.getLogger(Funciones.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static String castearMatrizMes(Date time) {
        SimpleDateFormat sdf = new SimpleDateFormat("MMMMM-yyyy");
        String format = sdf.format(time);
        return format;
    }

    public static int diferenciaMeses(Date fechaInicial, Date fechaFinal) {
        int mesInicial = fechaInicial.getMonth();
        int mesFinal = fechaFinal.getMonth();
        int anioInicial = fechaInicial.getYear();
        int anioFinal = fechaFinal.getYear();
        int contador = 1;
        while ((mesInicial != mesFinal) || (anioInicial != anioFinal)) {
            int aux = (mesInicial + 1);
            mesInicial = aux % 12;
            if (aux > 11) {
                anioInicial++;
            }
            contador++;
        }
        return contador;
    }

//    public static double redondear(double numero) {
//        Double copia = new Double(numero);
//        String numeroString = copia.toString();
//        String[] split = numeroString.split("\\.");
//        double izquierda=Double.parseDouble(split[0]);
//        boolean sumar=false;
//        if (split.length == 2) {
//            int derecha=Integer.parseInt(String.valueOf(split[1].charAt(0)));
//            if(derecha>=5){
//                sumar=true;
//            }
//        }
//        
//        if(sumar){
//            izquierda+=1.0;
//        }
//        return izquierda;
//    }
    public static double redondearDecimales(double numero, int decimales) {
        DecimalFormat num = new DecimalFormat(".###");
        String format = num.format(numero);
        String replace = format.replace(",", ".");
        return Double.parseDouble(replace);
    }

    public static String redondear(String numero, int decimales) {
        BigDecimal big = new BigDecimal(numero);
        big = big.setScale(decimales, RoundingMode.HALF_UP);
        return big.toString();
    }

    static Date StringToDateMatriz(String fecha) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("MMMMM-yyyy");
            return sdf.parse(fecha);
        } catch (ParseException ex) {
            Logger.getLogger(Funciones.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static double autoSumar(MatrizBidimensional matrizCostoVentas) {
        double suma = 0;
        for (CeldaFechaValor c : matrizCostoVentas.getCeldas()) {
            suma += c.getValor();
        }
        return suma;
    }

    public static String ponerComasCantidades(double round) {
        DecimalFormat format = new DecimalFormat("###,###,###,###,###.###");
        String format1 = format.format(round);
        return format1.replace('.', ',');
    }

   
    public static String cambiarPorcentajes(String val) {
        val=val.replace("%", "");
        String[] split = val.split("\\.");
        boolean sigue = true;
        while (sigue) {
            if (split[0].charAt(0) == '0') {
                split[0] = split[0].substring(1, split[0].length());
            } else {
                sigue = false;
            }
        }
        if (split.length == 2) {

            sigue = true;
            while (sigue) {
                if (split[1].length()>0 && split[1].charAt(split[1].length()-1) == '0') {
                    split[1] = split[1].substring(0, split[1].length()-1);
                } else {
                    sigue = false;
                }
            }
            return split[1].length()>0 ? split[0]+"."+split[1]+"%" : split[0]+"%";
        }
        return split[0]+"%";
    }

    
    public static List<String> cambiarTipoArreglo(List<Double> list) {
        List<String> nueva=new LinkedList<String>();
        for(Double o:list){
            nueva.add(redondear(String.valueOf(o), 2).toString());
        }
        return nueva;
    }

    public static List<PryVeh> sacarProyectos(MdlVeh modelo) {
        List<PryVeh> proyectos=new LinkedList<PryVeh>();
        Set<RelMdlVeh> relMdlVehs = modelo.getRelMdlVehs();
        for(RelMdlVeh relacion:relMdlVehs){
            proyectos.add(relacion.getPryVeh().clone());
        }
        return proyectos;
    }

    public static void mandarMensaje(String string, String string0) {
        FacesContext.getCurrentInstance().addMessage(string, new FacesMessage(string0));
    }
    
    
    
    
    /**
     * -2 es rojo
     * -1 es naranja
     * 0 es amarillo
     * 1 es verde
     * 2 es azul
     * -3 es transparente
     * @param tirObtenida
     * @param tirReal
     * @return 
     */
    public static int colorearCeldaMapaCalor(double tirObtenida , double tirReal){
        double porcentajePrimero=tirReal*.025;
        double porcentajeSegundo=tirReal*.075;
        System.out.println("el primer porcentaje es "+porcentajePrimero);
        System.out.println("el segundo porcentaje es "+porcentajeSegundo);
        if(tirObtenida<tirReal){
            if(tirObtenida>=(tirReal-porcentajePrimero)){
                return 0;
            }else if(tirObtenida< (tirReal-porcentajeSegundo)){
                return -2;
            }
            return -1;
        }
        if(tirObtenida>tirReal){
            if(tirObtenida<=(tirReal+porcentajePrimero)){
                return 0;
            }else if(tirObtenida>= (tirReal+porcentajeSegundo)){
                return 2;
            }
            return 1;
        }
        return 0;
    }
    
    
    public static void main(String[] args) {
        
    }

    public static double calcularValorViviendaEsperanza(Object valorVivienda, double probabilidadIncumplimiento, double porcentajeRecuperacion) {
        double valorViv = Double.parseDouble(valorVivienda.toString());
        double probInc=probabilidadIncumplimiento/100;
        double porRec=porcentajeRecuperacion/100;
        return valorViv*((1-probInc)+(probInc*porRec));
    }
    

    
    public static Object castearVAlor(String val) {
        Object regreso;
        if (val.contains("%")) {
            regreso = val.substring(0, val.length() - 1);
        } else if (val.contains(",")) {
            regreso = val.replace(",", "");
        } else {
            regreso = val;
        }
        try {
            return Double.parseDouble(String.valueOf(regreso));

        } catch (NumberFormatException num) {
            return null;
        }

    }

//    public static ManejadorVariablesVehiculo generarCopiaManejador(ManejadorVariablesVehiculo original,
//                                                    int indiceProyecto) {
//        
//             ManejadorVariablesVehiculo manejador=new ManejadorVariablesVehiculo(original);
//             llenarMapeoIndividual(manejador,original);
//        
//    }

    private static void llenarMapeoIndividual(ManejadorVariablesVehiculo manejador, ManejadorVariablesVehiculo original) {
        Map<String, Object> mapeoOriginal = original.getMapeoVariables();
        Set<String> llavesOriginales = mapeoOriginal.keySet();
        
        for(String llave:llavesOriginales){
            Object valor = mapeoOriginal.get(llave);
            
        }
    }

    
    
}
