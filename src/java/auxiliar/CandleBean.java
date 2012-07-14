package auxiliar;

import calculador.vehiculo.DistribuidorModeloVehiculo;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import org.primefaces.model.chart.OhlcChartModel;
import org.primefaces.model.chart.OhlcChartSeries;
import util.CeldaFechaValor;
import util.MatrizBidimensional;

/**
 *
 * @author alberto
 */
public class CandleBean {
    
    private DistribuidorModeloVehiculo distribuidor;
    private OhlcChartModel graficaVela;
    private boolean noViable;

    public CandleBean(DistribuidorModeloVehiculo distribuidor) {
        noViable=false;
        this.distribuidor=distribuidor;
        generarCandleStick();
    }

    //Open reserva de liquidez
    //high caja final
    //low reserva de liquidez
    //close caja disponible
    private void generarCandleStick() {
        graficaVela =new OhlcChartModel();
        MatrizBidimensional matrizCjaFin = (MatrizBidimensional) distribuidor.obtenerVariable("veh_cet_cja_fin");
        MatrizBidimensional matrizResLiq = (MatrizBidimensional) distribuidor.obtenerVariable("veh_cet_res_liq");
        
        for(int t=0;t<matrizCjaFin.getCeldas().size()-1;t++){
            double cjaFin=matrizCjaFin.getCeldas().get(t).getValor();
            double resliq=matrizResLiq.getCeldas().get(t).getValor();
            System.out.println("metiendo indice = "+(t+1)+" open= "+resliq+" high= "+cjaFin+" low="+resliq+" close="+(cjaFin-resliq));
            if(!noViable && (cjaFin-resliq)<0){
                noViable=true;
            }
            this.graficaVela.addRecord(new OhlcChartSeries(t+1, Math.round(resliq), Math.round(cjaFin), Math.round(resliq), Math.round(cjaFin-resliq)));
        }
    }

    /**
     * @return the distribuidor
     */
    public DistribuidorModeloVehiculo getDistribuidor() {
        return distribuidor;
    }

    /**
     * @param distribuidor the distribuidor to set
     */
    public void setDistribuidor(DistribuidorModeloVehiculo distribuidor) {
        this.distribuidor = distribuidor;
    }

    /**
     * @return the graficaVela
     */
    public OhlcChartModel getGraficaVela() {
        return graficaVela;
    }

    /**
     * @param graficaVela the graficaVela to set
     */
    public void setGraficaVela(OhlcChartModel graficaVela) {
        this.graficaVela = graficaVela;
    }

    /**
     * @return the noViable
     */
    public boolean isNoViable() {
        return noViable;
    }

    /**
     * @param noViable the noViable to set
     */
    public void setNoViable(boolean noViable) {
        this.noViable = noViable;
    }

}
