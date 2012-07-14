/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package guardadores;

import base.Dao;
import calculador.vehiculo.DistribuidorModeloVehiculo;
import com.sun.org.apache.xpath.internal.functions.FuncBoolean;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import pojos.DatVeh;
import pojos.EtpTirVeh;
import pojos.EtpVeh;
import pojos.MatVeh;
import pojos.MdlVeh;
import pojos.PryVeh;
import pojos.RelMdlVeh;
import pojos.UsuMdlVeh;
import pojos.UsuPryVeh;
import pojos.VarVeh;
import util.Funciones;
import util.vehiculo.EtapaCapturaVehiculo;
import util.vehiculo.ListaEtapasVehiculo;

/**
 *
 * @author alberto
 */
public class GuardadorModelo {

    private DistribuidorModeloVehiculo distribuidor;
    private MdlVeh modeloOriginal;
    private MdlVeh modeloNuevo;
    private Dao dao;
    private Set<Integer> porcentajes;
    private final Set<Integer> fechas;
    private final String nombreModelo;

    public GuardadorModelo(String nombreModelo, MdlVeh modeloOriginal, DistribuidorModeloVehiculo distribuidor) {
        this.nombreModelo=nombreModelo;
        this.distribuidor = distribuidor;
        this.modeloOriginal = modeloOriginal;
        this.dao = new Dao();
        this.porcentajes = llenarConjunto(1, 2, 3, 4, 5, 6, 7, 8, 12, 13, 14, 15, 16, 17, 18, 19, 22, 23);
        this.fechas = llenarConjunto(27, 28, 29, 30, 31, 32);

    }

    public String guardar() {
        guardarMdlVeh();
        guardarPryVeh();
        return modeloNuevo.getNomMdl();
    }

    private void guardarMdlVeh() {
        modeloNuevo = new MdlVeh();
        modeloNuevo.setFec(Calendar.getInstance());
        modeloNuevo.setMesMin(distribuidor.getTiempoMinistracion());
        modeloNuevo.setMesPag(distribuidor.getTiempoPago());
        modeloNuevo.setUsu(modeloOriginal.getUsu());
        modeloNuevo.setNomMdl(nombreModelo);
        dao.guardar(modeloNuevo);
        UsuMdlVeh usuMdlVeh = new UsuMdlVeh(modeloNuevo, modeloOriginal.getUsu(), dao.getEstatusProyecto(1));
        dao.guardar(usuMdlVeh);
    }

    private void guardarPryVeh() {
        List<PryVeh> proyectosOriginales = distribuidor.getProyectos();
        int indice = 0;
        for (PryVeh pry : proyectosOriginales) {
            PryVeh pryNuevo = guardarProyecto(pry);
            guardarDatVehs(indice, pry, pryNuevo);
            guardarEtpVehs(indice, pry, pryNuevo);
            guardarEtpTirs(indice, pry, pryNuevo);
            guardarMats(indice, pry, pryNuevo);
            UsuPryVeh usuPry = new UsuPryVeh(modeloNuevo.getUsu(), pryNuevo, dao.getEstatusProyecto(1));
            dao.guardar(usuPry);
            RelMdlVeh rel = new RelMdlVeh(modeloNuevo, pryNuevo);
            dao.guardar(rel);
            indice++;
        }
    }

    private PryVeh guardarProyecto(PryVeh pry) {
        PryVeh pryNuevo = new PryVeh();
        pryNuevo.setFch(Calendar.getInstance());
        pryNuevo.setNomPry(pry.getNomPry() + "-" + modeloNuevo.getNomMdl());
        pryNuevo.setTipMon(pry.getTipMon());
        dao.guardar(pryNuevo);
        return pryNuevo;
    }

    private void guardarDatVehs(int indice, PryVeh pry, PryVeh pryNuevo) {
        List<VarVeh> tabla = dao.getTabla(VarVeh.class);
        for (VarVeh variable : tabla) {
            System.out.println("guardando " + variable.getDesTca());
            Object[] datos = (Object[]) distribuidor.obtenerVariable(variable.getDesTca());
            if (datos != null && datos[indice]!=null) {
                DatVeh dato = new DatVeh(variable, pryNuevo, generarValor(variable.getId(), datos[indice]));
                dao.guardar(dato);
                System.out.println("guardado "+variable.getDesTca()+" con  "+dato.getVal());
            }
        }
    }

    private String generarValor(Integer id, Object object) {
        System.out.println("el objeto "+object);
        int tipoDato = buscarTipoDato(id);
        String valor = "";
        switch (tipoDato) {
            case 0:
                valor = Funciones.redondear(object.toString(), 2) + "%";
                break;
            case 1:
                valor = Funciones.DateToString(Funciones.StringToDate(object.toString()));
                break;
            default:
                valor = object.toString();
        }
        return valor;
    }

    private Set<Integer> llenarConjunto(int... numeros) {
        Set<Integer> conj = new HashSet<Integer>();
        for (int t = 0; t < numeros.length; t++) {
            conj.add(numeros[t]);
        }
        return conj;
    }

    private int buscarTipoDato(Integer id) {
        if (this.porcentajes.contains(id)) {
            return 0;
        }
        if (this.fechas.contains(id)) {
            return 1;
        }
        return 2;
    }

    private void guardarEtpTirs(int indice, PryVeh pry, PryVeh pryNuevo) {
        Set<EtpTirVeh> etpTirVehs = pry.getEtpTirVehs();
        for (EtpTirVeh etp : etpTirVehs) {
            EtpTirVeh nueva = new EtpTirVeh(etp.getVarVeh(), pryNuevo, etp.getNumEta(), etp.getVal());
            dao.guardar(nueva);
        }
    }

    private void guardarMats(int indice, PryVeh pry, PryVeh pryNuevo) {
        Set<MatVeh> matVehs = pry.getMatVehs();
        for (MatVeh mat : matVehs) {
            MatVeh nueva = new MatVeh(pryNuevo, mat.getTipMatVeh(), mat.getFch(), mat.getVal());
            dao.guardar(nueva);
        }
    }

    private void guardarEtpVehs(int indice, PryVeh pry, PryVeh pryNuevo) {
        ListaEtapasVehiculo[] obtenerVariable = (ListaEtapasVehiculo[]) distribuidor.obtenerVariable("veh_eta");
        ListaEtapasVehiculo etapas = obtenerVariable[indice];
        List<EtapaCapturaVehiculo> listaEtapas = etapas.getListaEtapas();
        VarVeh inicio = dao.getVarVeh(33);
        VarVeh fecFinal = dao.getVarVeh(34);
        VarVeh numCAs = dao.getVarVeh(35);
        int indiceEtapa = 0;
        for (EtapaCapturaVehiculo etapa : listaEtapas) {
            EtpVeh etapaInicio = new EtpVeh(inicio, pryNuevo, indiceEtapa, Funciones.DateToString(etapa.getFechaInicioEtapa()));
            EtpVeh etapaFin = new EtpVeh(fecFinal, pryNuevo, indiceEtapa, Funciones.DateToString(etapa.getFechaFinEtapa()));
            EtpVeh etapaCas = new EtpVeh(numCAs, pryNuevo, indiceEtapa, String.valueOf(etapa.getViviendasEtapa()));
            dao.guardar(etapaInicio);
            dao.guardar(etapaFin);
            dao.guardar(etapaCas);
            indiceEtapa++;
        }
    }
}
