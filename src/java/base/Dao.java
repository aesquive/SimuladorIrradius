package base;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import pojos.EstPryInd;
import pojos.EstPryVeh;
import pojos.MapCalVeh;
import pojos.MdlPryInd;
import pojos.MdlVeh;
import pojos.PryPryInd;
import pojos.PryVeh;
import pojos.TipMapCalVeh;
import pojos.TipMatPryInd;
import pojos.Usu;
import pojos.UsuPryPryInd;
import pojos.UsuPryVeh;
import pojos.VarVeh;

/**
 * 29/12/2011
 * Clase que interactua con la base de datos  
 * @author Alberto Emmanuel Esquivel Vega
 */
public class Dao {

  /**
   * sesion en base de datos , estatica para solo mantener una sesion
   */
  protected static Session sesion;

  /**
   * @return the sesion
   */
  public static Session getSesion() {
    return sesion;
  }

  public Dao() {
    sesion = sesion == null ? HibernateUtil.getSessionFactory().openSession() : sesion;
  }

  /**
   * regresa el contenido en una tabla de la base en forma de lista
   * @param tabla , tabla deseada
   * @return 
   */
  public List getTabla(Class tabla) {
    List list = getSesion().createCriteria(tabla).list();
    return list;
  }

  /**
   * guarda el objeto en base de datos y regresa el id con el que se guardo
   * @param objeto a guardar en base
   * @return 
   */
  public void guardar(Object objeto) {
    Transaction transaction = getSesion().beginTransaction();
    getSesion().save(objeto);
    transaction.commit();
  }

  /**
   * reinicia la sesion del dao
   */
  public void reiniciarSesion() {
    sesion.close();
    sesion = HibernateUtil.getSessionFactory().openSession();

  }

  /**
   * actualiza un objeto en base de datos 
   * @param objeto a actualizar
   */
  public void actualizar(Object objeto) {
    Transaction transaction = getSesion().beginTransaction();
    getSesion().update(objeto);
    transaction.commit();

  }

  public static void main(String[] args) {

    Dao dao = new Dao();
        MdlVeh modelo = dao.getModelo(55);
        System.out.println(modelo.getMesMin());
  }

  /**
   * nos devuelve los proyectos del usuario , en caso de que el usuario sea super usuario devuelve todos los proyectos
   * salvados
   * @param usuario
   * @return 
   */
  public List<PryVeh> getProyectos(Usu usuario) {
    Set<UsuPryVeh> proyectosUsuario = usuario.getUsuPryVehs();
    if (usuario.getTipUsu().getId() == 3) {
      return buscarTodosProyectos();
    }
    List<PryVeh> proyectos = new LinkedList<PryVeh>();
    for (UsuPryVeh proyectoUsuario : proyectosUsuario) {
      if (proyectoUsuario.getEstPryVeh() != null && proyectoUsuario.getEstPryVeh().getId() != 2) {
        proyectos.add(proyectoUsuario.getPryVeh());
      }
    }
    return proyectos;
  }

  public List<MapCalVeh> getMapa(MdlVeh vehiculo) {
    System.out.println("para beto");
    MdlVeh veh = (MdlVeh) sesion.createCriteria(MdlVeh.class).add(Restrictions.eq("id", vehiculo.getId())).uniqueResult();
    Set<MapCalVeh> mapasCalor =  veh.getMapCalVehs();  
    if(mapasCalor != null)
    {
      List<MapCalVeh> mapa = new LinkedList<MapCalVeh>(); 
      for(MapCalVeh map: mapasCalor)
      {
        mapa.add(map);
      }
      return mapa;
    }
    return null;
  }

  private List<PryVeh> buscarTodosProyectos() {

    return getTabla(PryVeh.class);
  }

  public EstPryVeh getEstatusProyecto(int estatus) {
    EstPryVeh est = (EstPryVeh) sesion.createCriteria(EstPryVeh.class).add(
            Restrictions.eq("id", estatus)).uniqueResult();

    return est;
  }

  public EstPryInd getEstatusProyectoInd(int estatus) {
    EstPryInd est = (EstPryInd) sesion.createCriteria(EstPryInd.class).add(
            Restrictions.eq("id", estatus)).uniqueResult();

    return est;
  }

  public void remove(PryVeh proyectoSeleccionado) {
    Transaction transaction = getSesion().beginTransaction();
    getSesion().delete(proyectoSeleccionado);
    transaction.commit();

  }

  public PryVeh getProyectoVehiculo(int id) {
    PryVeh pry = (PryVeh) sesion.createCriteria(PryVeh.class).add(Restrictions.eq("id", id)).uniqueResult();
    return pry;
  }

  public List<MdlVeh> getModelos(Usu usu) {
    Usu usuario = (Usu) sesion.createCriteria(Usu.class).add(Restrictions.eq("id", usu.getId())).uniqueResult();
    Set<MdlVeh> mdlVehs = usuario.getMdlVehs();
    List<MdlVeh> modelos = new LinkedList<MdlVeh>();
    for (MdlVeh m : mdlVehs) {
      modelos.add(m);
    }
    return modelos;
  }

  public TipMatPryInd obtenerTipoMatriz(String att) {
    return (TipMatPryInd) sesion.createCriteria(TipMatPryInd.class).add(Restrictions.eq("desTipMat", att)).uniqueResult();
  }

  public PryPryInd getPryInd(int i) {
    return (PryPryInd) sesion.createCriteria(PryPryInd.class).add(Restrictions.eq("id", i)).uniqueResult();
  }

  public MdlVeh buscarUltimoModelo() {
    List list = sesion.createCriteria(MdlVeh.class).list();
    return (MdlVeh) list.get(list.size() - 1);
  }

  public List<PryPryInd> getProyectosIndividual(Usu usuario) {
    Set<UsuPryPryInd> proyectosUsuario = usuario.getUsuPryPryInds();
    if (usuario.getTipUsu().getId() == 3) {
      return buscarTodosProyectosInd();
    }
    List<PryPryInd> proyectos = new LinkedList<PryPryInd>();
    for (UsuPryPryInd proyectoUsuario : proyectosUsuario) {
      PryPryInd pryInd = proyectoUsuario.getPryPryInd();
      if (pryInd.getEstPryInd() != null && pryInd.getEstPryInd().getId() != 2) {
        proyectos.add(pryInd);
      }
    }
    return proyectos;
  }

  private List<PryPryInd> buscarTodosProyectosInd() {
    return getTabla(PryPryInd.class);
  }

  public List<MdlPryInd> getModelosPryInd(Usu usuario) {
    Set<MdlPryInd> mdlPryInd = usuario.getMdlPryInds();
    List<MdlPryInd> proyectos = new LinkedList<MdlPryInd>();
    for (MdlPryInd m : mdlPryInd) {
      proyectos.add(m);
    }
    return proyectos;
  }

  public PryVeh getPryVeh(int proyectoActual) {
    return (PryVeh) sesion.createCriteria(PryVeh.class).add(Restrictions.eq("id", proyectoActual)).uniqueResult();
  }

  public TipMapCalVeh getTip_map(int tipo_num) {
    return (TipMapCalVeh) sesion.createCriteria(TipMapCalVeh.class).add(Restrictions.eq("id", tipo_num)).uniqueResult();
  }

    public UsuPryVeh obtenerUsuPryVeh(Usu usu, PryVeh pry) {
       return (UsuPryVeh) sesion.createCriteria(UsuPryVeh.class).add(
                Restrictions.eq("usu", usu)).add(
                Restrictions.eq("pryVeh", pry)).uniqueResult();
    }

    public MdlVeh getModelo(int numeroModelo) {
        return (MdlVeh) sesion.createCriteria(MdlVeh.class).add(Restrictions.eq("id", numeroModelo)).list().get(0);
    }

    public VarVeh getVarVeh(int i) {
        return (VarVeh) sesion.createCriteria(VarVeh.class).add(Restrictions.eq("id", i)).list().get(0);
    }

}
