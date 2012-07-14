package base;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import pojos.VarPryInd;
import pojos.VarVeh;

/**
 *
 * @author Alberto
 */
public class Catalogueador {

  private Map<String, List> catalogo;
  private Dao dao;

  public Catalogueador() {
    this.catalogo = new HashMap<String, List>();
    dao = new Dao();
  }

  public List getCatalogo(String nombre) {
    try {
      return catalogo.get(nombre) == null ? dao.getTabla(Class.forName(nombre)) : catalogo.get(nombre);
    } catch (ClassNotFoundException ex) {
      Logger.getLogger(Catalogueador.class.getName()).log(Level.SEVERE, null, ex);
    }
    return null;
  }

  public static void main(String[] args) {
    Catalogueador c = new Catalogueador();
    List<VarPryInd> catalogo1 = c.getCatalogo(VarPryInd.class.getName());
    for (VarPryInd v : catalogo1) {
      System.out.println("des_tec"+v.getDesLar());
    }
    //System.out.println(catalogo1.get(0).getDesTca());
  }
}
