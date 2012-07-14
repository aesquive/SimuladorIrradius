package util;

import java.util.Date;

/**
 *
 * @author Hector Daniel Gonzalez Teran
 */
public class RegistroFecha implements Comparable {
  
  
  private String fecha;
  private String porcentaje;

  /**
   *
   */
  public RegistroFecha(String fecha) {
    this.fecha = fecha;
    this.porcentaje = "";
  }
  
  public RegistroFecha(String fecha , String porcentaje){
      this.fecha=fecha;
      this.porcentaje=porcentaje;
  }

  /**
   * @return the fecha
   */
  public String getFecha() {
    return fecha;
  }

  /**
   * @param fecha the fecha to set
   */
  public void setFecha(String fecha) {
    this.fecha = fecha;
  }

  /**
   * @return the porcentaje
   */
  public String getPorcentaje() {
    return porcentaje;
  }

  /**
   * @param porcentaje the porcentaje to set
   */
  public void setPorcentaje(String porcentaje) {
    this.porcentaje = porcentaje;
  }

    @Override
    public int compareTo(Object t) {
        RegistroFecha comparado=(RegistroFecha)t;
        Date fechaThis = Funciones.StringToDateMatriz(fecha);
        Date fechaComp=Funciones.StringToDateMatriz(comparado.fecha);
        System.out.println("comparando "+fechaThis+" con "+fechaComp);
        return fechaThis.compareTo(fechaComp);
    }

}
