package util;

import java.util.List;
import org.primefaces.model.chart.CartesianChartModel;

/**
 * Clase que genera los registros para los bean de vehiculo y de proyecto
 * @author Hector Daniel Gonzalez Teran
 */
public class ParametrosMatrizBid  {
  
  private String nombre;
  private CartesianChartModel grafica;
  private List<Vector> matriz;
  private int minX;
  private int maxX;
    /**
     * @return the nombre
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * @param nombre the nombre to set
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * @return the grafica
     */
    public CartesianChartModel getGrafica() {
        return grafica;
    }

    /**
     * @param grafica the grafica to set
     */
    public void setGrafica(CartesianChartModel grafica) {
        this.grafica = grafica;
    }

    /**
     * @return the matriz
     */
    public List<Vector> getMatriz() {
        return matriz;
    }

    /**
     * @param matriz the matriz to set
     */
    public void setMatriz(List<Vector> matriz) {
        this.matriz = matriz;
    }

    /**
     * @return the minX
     */
    public int getMinX() {
        return minX;
    }

    /**
     * @param minX the minX to set
     */
    public void setMinX(int minX) {
        this.minX = minX;
    }

    /**
     * @return the maxX
     */
    public int getMaxX() {
        return maxX;
    }

    /**
     * @param maxX the maxX to set
     */
    public void setMaxX(int maxX) {
        this.maxX = maxX;
    }
  
  
  
}
