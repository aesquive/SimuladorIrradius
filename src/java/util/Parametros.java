package util;

import java.util.LinkedList;
import java.util.List;

/**
 * Clase que genera los registros para los bean de vehiculo y de proyecto
 * @author Hector Daniel Gonzalez Teran
 */
public class Parametros implements Comparable {
  
  private String registroDescripcion;
  private String registroIngreso;

  /**
   * Crea una nueva instancia de la clase parametros inicializandola
   * con los atributos dados.
   * @param registroDescripcion La descripcion del parametro. 
   * @param registroIngreso El ingreso con el cual cuenta el parametro.
   */
  public Parametros(String registroDescripcion, String registroIngreso) {
    this.registroDescripcion = registroDescripcion;
    this.registroIngreso = registroIngreso;
  }

    @Override
    public String toString() {
        return "Parametros{" + "registroDescripcion=" + registroDescripcion + ", registroIngreso=" + registroIngreso + '}';
    }

  /**
   * @return the registroDescripcion
   */
   public String getRegistroDescripcion() {
    return registroDescripcion;
  }

  /**
   * @param registroDescripcion the registroDescripcion to set
   */
  public void setRegistroDescripcion(String registroDescripcion) {
    this.registroDescripcion = registroDescripcion;
  }

  /**
   * @return the registroIngreso
   */
  public String getRegistroIngreso() {
    return registroIngreso;
  }

  /**
   * @param registroIngreso the registroIngreso to set
   */
  public void setRegistroIngreso(String registroIngreso) {
    this.registroIngreso = registroIngreso;
  }

    @Override
    public int compareTo(Object t) {
        Parametros p=(Parametros)t;
        Double valor1=registroIngreso.contains("%") ? Double.valueOf(registroIngreso.substring(0,registroIngreso.length()-1)) 
                : Double.valueOf(registroIngreso);
        Double valor2=p.registroIngreso.contains("%") ? Double.valueOf(p.registroIngreso.substring(0,p.registroIngreso.length()-1)) 
                : Double.valueOf(p.registroIngreso);
        try{
            return valor2.compareTo(valor1);
        }catch(NumberFormatException num){
            return 0;
        }
    }
  
  
    public static void main(String[] args) {
        List<Parametros> param=new LinkedList<Parametros>();
        param.add(new Parametros("D","33.9"));
    }
  
}
