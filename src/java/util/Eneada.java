/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

/**
 *
 * @author JOSELUIS
 */
public class Eneada {
    
    private String des;
    private String val0;

    
    private String val1;
    private String val2;
    private String val3;
    private String val4;
    private String val5;
    private String val6;
    private String val7;
    private String val8;
    private int col0;
    private int col1;
    private int col2;
    private int col3;
    private int col4;
    private int col5;
    private int col6;
    private int col7;
    private int col8;
    
    public Eneada(String des, String val0, String val1, String val2, String val3, String val4, String val5, String val6, String val7, String val8) {
        this.des = des;
        this.val0 = val0;
        this.val1 = val1;
        this.val2 = val2;
        this.val3 = val3;
        this.val4 = val4;
        this.val5 = val5;
        this.val6 = val6;
        this.val7 = val7;
        this.val8 = val8;
        this.col0=-3;
        this.col1=-3;
        this.col2=-3;
        this.col3=-3;
        this.col4=-3;
        this.col5=-3;
        this.col6=-3;
        this.col7=-3;
        this.col8=-3;
    }
    
    public Eneada(){
        
    }

    /**
     * @return the des
     */
    public String getDes() {
        return des;
    }

    /**
     * @param des the des to set
     */
    public void setDes(String des) {
        this.des = des;
    }

    /**
     * @return the val0
     */
    public String getVal0() {
        val0=val0.contains(",") ? val0.replace(",", "") :val0;
        Double valueOf = Double.valueOf(val0);
        if(valueOf>=1000){
            return Funciones.ponerComasCantidades(Double.parseDouble(Funciones.redondear(val0, 0)));
        }
        return val0;
    }

    /**
     * @param val0 the val0 to set
     */
    public void setVal0(String val0) {
        this.val0 = val0;
    }

    /**
     * @return the val1
     */
    public String getVal1() {
        val1=val1.contains(",") ? val1.replace(",", "") :val1;
        Double valueOf = Double.valueOf(val1);
        if(valueOf>=1000){
            return Funciones.ponerComasCantidades(Double.parseDouble(Funciones.redondear(val1, 0)));
        }
        return val1;
    }

    /**
     * @param val1 the val1 to set
     */
    public void setVal1(String val1) {
        this.val1 = val1;
    }

    /**
     * @return the val2
     */
    public String getVal2() {
        val2=val2.contains(",") ? val2.replace(",", "") :val2;
        Double valueOf = Double.valueOf(val2);
        if(valueOf>=1000){
            return Funciones.ponerComasCantidades(Double.parseDouble(Funciones.redondear(val2, 0)));
        }
        return val2;
    }

    /**
     * @param val2 the val2 to set
     */
    public void setVal2(String val2) {
        this.val2 = val2;
    }

    /**
     * @return the val3
     */
    public String getVal3() {
        val3=val3.contains(",") ? val3.replace(",", "") :val3;
        Double valueOf = Double.valueOf(val3);
        if(valueOf>=1000){
            return Funciones.ponerComasCantidades(Double.parseDouble(Funciones.redondear(val3, 0)));
        }
        return val3;
    }

    /**
     * @param val3 the val3 to set
     */
    public void setVal3(String val3) {
        this.val3 = val3;
    }

    /**
     * @return the val4
     */
    public String getVal4() {
        val4=val4.contains(",") ? val4.replace(",", "") :val4;
        Double valueOf = Double.valueOf(val4);
        if(valueOf>=1000){
            return Funciones.ponerComasCantidades(Double.parseDouble(Funciones.redondear(val4, 0)));
        }
        return val4;
    }

    /**
     * @param val4 the val4 to set
     */
    public void setVal4(String val4) {
        this.val4 = val4;
    }

    /**
     * @return the val5
     */
    public String getVal5() {
        val5=val5.contains(",") ? val5.replace(",", "") :val5;
        Double valueOf = Double.valueOf(val5);
        if(valueOf>=1000){
            return Funciones.ponerComasCantidades(Double.parseDouble(Funciones.redondear(val5, 0)));
        }
        return val5;
    }

    /**
     * @param val5 the val5 to set
     */
    public void setVal5(String val5) {
        this.val5 = val5;
    }

    /**
     * @return the val6
     */
    public String getVal6() {
        val6=val6.contains(",") ? val6.replace(",", "") :val6;
        Double valueOf = Double.valueOf(val6);
        if(valueOf>=1000){
            return Funciones.ponerComasCantidades(Double.parseDouble(Funciones.redondear(val6, 0)));
        }
        return val6;
    }

    /**
     * @param val6 the val6 to set
     */
    public void setVal6(String val6) {
        this.val6 = val6;
    }

    /**
     * @return the val7
     */
    public String getVal7() {
        val7=val7.contains(",") ? val7.replace(",", "") :val7;
        Double valueOf = Double.valueOf(val7);
        if(valueOf>=1000){
            return Funciones.ponerComasCantidades(Double.parseDouble(Funciones.redondear(val7, 0)));
        }
        return val7;
    }

    /**
     * @param val7 the val7 to set
     */
    public void setVal7(String val7) {
        this.val7 = val7;
    }

    /**
     * @return the val8
     */
    public String getVal8() {
        val8=val8.contains(",") ? val8.replace(",", "") :val8;
        Double valueOf = Double.valueOf(val8);
        if(valueOf>=1000){
            return Funciones.ponerComasCantidades(Double.parseDouble(Funciones.redondear(val8, 0)));
        }
        return val8;
    }

    /**
     * @param val8 the val8 to set
     */
    public void setVal8(String val8) {
        this.val8 = val8;
    }

    @Override
    public String toString() {
        return "Eneada{" + "des=" + getDes() + ", val0=" + getVal0() + ", val1=" + getVal1() + ", val2=" + getVal2() + ", val3=" + getVal3() + ", val4=" + getVal4() + ", val5=" + getVal5() + ", val6=" + getVal6() + ", val7=" + getVal7() + ", val8=" + getVal8() + '}';
    }

    /**
     * @return the col0
     */
    public int getCol0() {
        return col0;
    }

    /**
     * @param col0 the col0 to set
     */
    public void setCol0(int col0) {
        this.col0 = col0;
    }
    
    /**
     * @return the col1
     */
    public int getCol1() {
        return col1;
    }

    /**
     * @param col1 the col1 to set
     */
    public void setCol1(int col1) {
        this.col1 = col1;
    }

    /**
     * @return the col2
     */
    public int getCol2() {
        return col2;
    }

    /**
     * @param col2 the col2 to set
     */
    public void setCol2(int col2) {
        this.col2 = col2;
    }

    /**
     * @return the col3
     */
    public int getCol3() {
        return col3;
    }

    /**
     * @param col3 the col3 to set
     */
    public void setCol3(int col3) {
        this.col3 = col3;
    }

    /**
     * @return the col4
     */
    public int getCol4() {
        return col4;
    }

    /**
     * @param col4 the col4 to set
     */
    public void setCol4(int col4) {
        this.col4 = col4;
    }

    /**
     * @return the col5
     */
    public int getCol5() {
        return col5;
    }

    /**
     * @param col5 the col5 to set
     */
    public void setCol5(int col5) {
        this.col5 = col5;
    }

    /**
     * @return the col6
     */
    public int getCol6() {
        return col6;
    }

    /**
     * @param col6 the col6 to set
     */
    public void setCol6(int col6) {
        this.col6 = col6;
    }

    /**
     * @return the col7
     */
    public int getCol7() {
        return col7;
    }

    /**
     * @param col7 the col7 to set
     */
    public void setCol7(int col7) {
        this.col7 = col7;
    }

    /**
     * @return the col8
     */
    public int getCol8() {
        return col8;
    }

    /**
     * @param col8 the col8 to set
     */
    public void setCol8(int col8) {
        this.col8 = col8;
    }

    
}
