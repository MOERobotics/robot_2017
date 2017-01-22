package org.usfirst.frc.team365.math;

public class PIDControl{
	double Kp,Ki,Kd;
	Deriver d;
	Integrator i;
	public PIDControl(double Kp, double Ki, double Kd, double xInit){
		this.Kp=Kp;
		this.Ki=Ki;
		this.Kd=Kd;
		d=new Deriver(xInit);
		i=new Integrator(xInit);
	}
	public double get(double xNow, double dt){
		return Kp*xNow+Ki*i.integrate(xNow, dt)+Kd*d.derive(xNow, dt);
	}
}