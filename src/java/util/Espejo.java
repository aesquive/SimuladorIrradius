package util;

import auxiliar.InversionSimulacionBean;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *  Clase que hace reflexion sobre cualquier objeto
 * @author Alberto
 */
public class Espejo {

  public static Method getMetodo(Class clase, String nombre) {
    Method[] methods = clase.getMethods();
    for (Method m : methods) {
      if (m.getName().contains(nombre)) {
        return m;
      }
    }
    return null;
  }

  public static LinkedList<Method> getMetodos(Class clase, String nombre) {
    LinkedList<Method> mets = new LinkedList<Method>();
    Method[] methods = clase.getDeclaredMethods();
    for (Method m : methods) {
      if (m.getName().contains(nombre)) {
        mets.add(m);
      }
    }
    if (mets.size() == 0) {
      return null;
    }
    return mets;
  }

  public static Method getMetodoExacto(Class clase, String nombre) {
    Method[] methods = clase.getMethods();
    for (Method m : methods) {
      if (m.getName().equals(nombre)) {
        return m;
      }
    }
    return null;
  }

  public static List<Method> getGetters(Class clase) {
    Method[] methods = clase.getDeclaredMethods();
    List<Method> metodos = new LinkedList<Method>();
    for (Method m : methods) {
      if (m.getName().contains("get")) {
        metodos.add(m);
      }
    }
    return metodos;
  }

  public static Object invocarGetter(Object target, Method getter) {

    try {
      return getter.invoke(target, null);
    } catch (IllegalAccessException ex) {
      Logger.getLogger(Espejo.class.getName()).log(Level.SEVERE, null, ex);
    } catch (IllegalArgumentException ex) {
      Logger.getLogger(Espejo.class.getName()).log(Level.SEVERE, null, ex);
    } catch (InvocationTargetException ex) {
      Logger.getLogger(Espejo.class.getName()).log(Level.SEVERE, null, ex);
    }
    return null;
  }

  public static void main(String[] args) {
    //VehiculoFamiliaParametricasBean bean = new VehiculoFamiliaParametricasBean();
    //List<Method> ters = Espejo.getGetters(bean.getClass());
    List<Method> metodos = Espejo.getMetodos(Eneada.class, "setDes");
    for(Method m : metodos)
    {
      System.out.println("el nombre es: " + m.getName());
    }
  }

  public static void invocaSetterVehiculoLista(String nombre, Object target, List<MatrizBidimensional> val) {
    try {
      System.out.println("nombresinsissisisisisisin " + nombre);
      Method metodo = Espejo.getMetodo(target.getClass(), "set" + nombre);
      metodo.invoke(target, val);
    } catch (IllegalAccessException ex) {
      Logger.getLogger(Espejo.class.getName()).log(Level.SEVERE, null, ex);
    } catch (IllegalArgumentException ex) {
      Logger.getLogger(Espejo.class.getName()).log(Level.SEVERE, null, ex);
    } catch (InvocationTargetException ex) {
      Logger.getLogger(Espejo.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  public static void invocarSetterMatriz(String nombreMetodo, Object target, MatrizBidimensional matriz) {
    try {
      Method metodo = Espejo.getMetodo(target.getClass(), "set" + nombreMetodo);
      metodo.invoke(target, matriz);
    } catch (IllegalAccessException ex) {
      Logger.getLogger(Espejo.class.getName()).log(Level.SEVERE, null, ex);
    } catch (IllegalArgumentException ex) {
      Logger.getLogger(Espejo.class.getName()).log(Level.SEVERE, null, ex);
    } catch (InvocationTargetException ex) {
      Logger.getLogger(Espejo.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  public static void invocarSetterInversion(InversionSimulacionBean inversion, double tirMinima, String metodo) {
    try {
      Method metodo1 = Espejo.getMetodo(inversion.getClass(), metodo);
      metodo1.invoke(inversion, tirMinima);
    } catch (IllegalAccessException ex) {
      Logger.getLogger(Espejo.class.getName()).log(Level.SEVERE, null, ex);
    } catch (IllegalArgumentException ex) {
      Logger.getLogger(Espejo.class.getName()).log(Level.SEVERE, null, ex);
    } catch (InvocationTargetException ex) {
      Logger.getLogger(Espejo.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  public static void invocarSetterString(Eneada actual, String metodo, String obtenerValorDistribuidor) {
    try {
      Method metodo1 = Espejo.getMetodo(actual.getClass(), metodo);
      metodo1.invoke(actual, obtenerValorDistribuidor);
    } catch (IllegalAccessException ex) {
      Logger.getLogger(Espejo.class.getName()).log(Level.SEVERE, null, ex);
    } catch (IllegalArgumentException ex) {
      Logger.getLogger(Espejo.class.getName()).log(Level.SEVERE, null, ex);
    } catch (InvocationTargetException ex) {
      Logger.getLogger(Espejo.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  public static void invocaSetterProyectoCadena(String nombre, Object target, String cadena) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
    try {
      Method metodo = Espejo.getMetodo(target.getClass(), "set" + nombre);
      metodo.invoke(target, cadena);
    } catch (IllegalAccessException ex) {
      Logger.getLogger(Espejo.class.getName()).log(Level.SEVERE, null, ex);
    } catch (IllegalArgumentException ex) {
      Logger.getLogger(Espejo.class.getName()).log(Level.SEVERE, null, ex);
    } catch (InvocationTargetException ex) {
      Logger.getLogger(Espejo.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  public static void invocaSetterProyectoLista(String nombre, Object target, LinkedList lista) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
    try {
      Method metodo = Espejo.getMetodoExacto(target.getClass(), "set" + nombre);
      metodo.invoke(target, lista);
    } catch (IllegalAccessException ex) {
      Logger.getLogger(Espejo.class.getName()).log(Level.SEVERE, null, ex);
    } catch (IllegalArgumentException ex) {
      Logger.getLogger(Espejo.class.getName()).log(Level.SEVERE, null, ex);
    } catch (InvocationTargetException ex) {
      Logger.getLogger(Espejo.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  public static void invocarSetterDouble(Object target, String nombre, int valor) {
      if(nombre==null){
          return;
      }
    try {
      Method metodo = Espejo.getMetodo(target.getClass(), nombre);
      metodo.invoke(target, valor);
    } catch (IllegalAccessException ex) {
      Logger.getLogger(Espejo.class.getName()).log(Level.SEVERE, null, ex);
    } catch (IllegalArgumentException ex) {
      Logger.getLogger(Espejo.class.getName()).log(Level.SEVERE, null, ex);
    } catch (InvocationTargetException ex) {
      Logger.getLogger(Espejo.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

    public static void invocarSetterDecimal(Object inversion, String nombreMetodo, double d) {
        try {
            Method metodo=Espejo.getMetodo(inversion.getClass(), nombreMetodo);
            metodo.invoke(inversion,d);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(Espejo.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(Espejo.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvocationTargetException ex) {
            Logger.getLogger(Espejo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void invocarSetterBoolean(InversionSimulacionBean inversion, boolean b, String nombreMetodo) {
        try {
            Method metodo=Espejo.getMetodo(inversion.getClass(), nombreMetodo);
            metodo.invoke(inversion,b);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(Espejo.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(Espejo.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvocationTargetException ex) {
            Logger.getLogger(Espejo.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
}
