package pojos;
// Generated 29-mar-2012 18:27:14 by Hibernate Tools 3.2.1.GA


import java.util.HashSet;
import java.util.Set;

/**
 * TipMon generated by hbm2java
 */
public class TipMon  implements java.io.Serializable {


     private Integer id;
     private String tip;
     private Set pryVehs = new HashSet(0);

    public TipMon() {
    }

    public TipMon(String tip, Set pryVehs) {
       this.tip = tip;
       this.pryVehs = pryVehs;
    }
   
    public Integer getId() {
        return this.id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    public String getTip() {
        return this.tip;
    }
    
    public void setTip(String tip) {
        this.tip = tip;
    }
    public Set getPryVehs() {
        return this.pryVehs;
    }
    
    public void setPryVehs(Set pryVehs) {
        this.pryVehs = pryVehs;
    }




}


