package org.usfirst.frc.team365.math;

public class Vector {
    private double x,y;
    public Vector(double x, double y){
        this.x=x;
        this.y=y;
    }
    public double getX() {
        return x;
    }
    public void setX(double x) {
        this.x = x;
    }
    public double getY() {
        return y;
    }
    public void setY(double y) {
        this.y = y;
    }
    public void setV(double x, double y){
        this.x=x;
        this.y=y;
    }
}