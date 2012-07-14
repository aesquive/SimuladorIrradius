/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package calculador.vehiculo;

import java.util.LinkedList;
import java.util.List;
import util.MatrizBidimensional;
import variables.ManejadorVariablesVehiculo;

/**
 *
 * @author alberto
 */
class CajaAnual {

    private ManejadorVariablesVehiculo manejador;
    
    public CajaAnual(ManejadorVariablesVehiculo manejador) {
        this.manejador=manejador;
    }
    
    public void procesar(){
    
        calcularOrigenAplicacionCapitalReparto();
        
       

    }

    private double sumarValoresMatriz(MatrizBidimensional m, int liminf, int limsup) {
        double suma = 0;
        for (int t = liminf; t <= limsup; t++) {
            suma += m.getCeldas().get(t).getValor();
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

    private void calcularOrigenAplicacionCapitalReparto() {
         String[] nombreMensual=new String[]{
            "veh_cet_ori","veh_cet_ing","veh_cet_fir_ctr_cpr_vta","veh_cet_des_ini",
            "veh_cet_cto_fin","veh_cet_ant_cre_pte","veh_cet_min_cre_pte",
            "veh_cet_apl","veh_cet_cto_vta","veh_cet_gav","veh_cet_gas_ind","veh_cet_adm_pry","veh_cet_cto_fin","veh_cet_pgo_cre_pte",
             "veh_cet_mov_net",
            "veh_cet_cap","veh_cet_apt","veh_cet_ret",
            "veh_cet_rep_uti","veh_cet_rep_uti_lim_prt","veh_cet_rep_uti_grl_prt",
        };
        for(int t=0;t<nombreMensual.length;t++){
            List<Double> lista=generarLista(nombreMensual[t]);
            manejador.guardarVariable(nombreMensual[t]+"_anu", lista);
        }
        
    }
}
