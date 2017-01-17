package org.usfirst.frc.team365.math;

public class Integrator
{
	double xOld,xNew;
	public Integrator(double xInit){
		xOld=xInit;
	}
	/**
	 * trapezoidal approximation of an integral
	 */
	public double integrate(double xNow, double dt){
		xOld = xNew;
		xNew = xNow;
		return (xOld+xNew)*dt/2.0;
	}
}
