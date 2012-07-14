/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util.vehiculo;

/**
 *
 * @author alberto
 */
public class Coordenada {

    private int x;
    private double y;
    
    public Coordenada(int x , double y){
        this.x=x;
        this.y=y;
    }

    /**
     * @return the x
     */
    public int getX() {
        return x;
    }

    /**
     * @param x the x to set
     */
    public void setX(int x) {
        this.x = x;
    }

    /**
     * @return the y
     */
    public double getY() {
        return y;
    }

    /**
     * @param y the y to set
     */
    public void setY(double y) {
        this.y = y;
    }

    @Override
    public String toString() {
        return "Coordenada{" + "x=" + x + ", y=" + y + '}';
    }
    
}
