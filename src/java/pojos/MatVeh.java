package pojos;
// Generated 29-mar-2012 18:27:14 by Hibernate Tools 3.2.1.GA


import java.util.Calendar;

/**
 * MatVeh generated by hbm2java
 */
public class MatVeh  implements java.io.Serializable {


     private Integer id;
     private PryVeh pryVeh;
     private TipMatVeh tipMatVeh;
     private Calendar fch;
     private String val;

    public MatVeh() {
    }

    public MatVeh(PryVeh pryVeh, TipMatVeh tipMatVeh, Calendar fch, String val) {
       this.pryVeh = pryVeh;
       this.tipMatVeh = tipMatVeh;
       this.fch = fch;
       this.val = val;
    }
   
    public Integer getId() {
        return this.id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    public PryVeh getPryVeh() {
        return this.pryVeh;
    }
    
    public void setPryVeh(PryVeh pryVeh) {
        this.pryVeh = pryVeh;
    }
    public TipMatVeh getTipMatVeh() {
        return this.tipMatVeh;
    }
    
    public void setTipMatVeh(TipMatVeh tipMatVeh) {
        this.tipMatVeh = tipMatVeh;
    }
    public Calendar getFch() {
        return this.fch;
    }
    
    public void setFch(Calendar fch) {
        this.fch = fch;
    }
    public String getVal() {
        return this.val;
    }
    
    public void setVal(String val) {
        this.val = val;
    }




}

