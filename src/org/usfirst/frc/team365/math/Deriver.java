package org.usfirst.frc.team365.math;

public class Deriver
{
	double xOld,xNew;
	public Deriver(double xInit){
		xOld=xInit;
	}
	/**
	 * linear appoximation of a derivative
	 */
	public double derive(double xNow, double dt){
		xOld = xNew;
		xNew = xNow;
		return (xOld-xNew)/dt;
	}
}