package base;

import java.util.List;
import org.hibernate.criterion.Restrictions;
import pojos.TipUsu;
import pojos.Usu;

/**
 * Clase que interactua con el manejo de la sesion para los usuarios
 * @author Alberto Emmanuel Esquivel Vega
 */
public class DaoUsuario extends Dao {

  public DaoUsuario() {
    super();
  }

  /**
   * Verifica si un usuario esta dado o no de alta en el sistema , nos regresa el objeto usuario
   * @param login del usuario
   * @param password del usuario
   * @return 
   */
  public Usu verificarUsuario(String login, String password) {
      List list = super.sesion.createQuery("from Usu where log_usu='" + login + "' and pas=MD5('" + password + "')").list();
    if (list.isEmpty()) {
      return null;
    }
    return (Usu) list.get(0);
  }

  /**
   * 
   * @param args 
   */
  public static void main(String[] args) {
      
      
      
  }

    public void guardarUsuario(String login , String password , int tipoUsuario) {
        Usu usu=new Usu();
        usu.setLogUsu(login);
        usu.setPas(password);
        usu.setTipUsu(getTipUsu(tipoUsuario));
        guardar(usu);
        
    }

    private TipUsu getTipUsu(int tipoUsuario) {
        TipUsu tipo=(TipUsu) sesion.createCriteria(TipUsu.class).add(Restrictions.eq("id", tipoUsuario)).uniqueResult();
            return tipo;
    }

}
