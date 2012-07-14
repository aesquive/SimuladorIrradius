/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Beans;

import java.util.Map;
import javax.faces.context.FacesContext;

/**
 * Clase que nos ayuda a buscar Beans n kasjdsd
 * @author Alberto
 */
public class Bean {
    /**
     * metodo que limpia beans
     * @param beans 
     */
    public static void limpiaBeans(String ... beans){
        Map<String, Object> sessionMap=FacesContext.getCurrentInstance().
                                        getExternalContext().getSessionMap();
        for(String bean:beans){
            sessionMap.remove(bean);
        }
    }
    
    public static Bean getBean(String nombre){
        Map<String, Object> sessionMap=FacesContext.getCurrentInstance().
                                        getExternalContext().getSessionMap();
        return (Bean)sessionMap.get(nombre);
    }
    
    public static void ponerSesion(String nombre , Bean bean){
        Map<String, Object> sessionMap=FacesContext.getCurrentInstance().
                                        getExternalContext().getSessionMap();
        sessionMap.put(nombre, bean);
    }
}
