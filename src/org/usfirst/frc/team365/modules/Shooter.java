package org.usfirst.frc.team365.modules;

import org.usfirst.frc.team365.util.RobotModule;

public class Shooter extends RobotModule
{
	double collectSpeed;
	double dAngleCoefficientChute;
	double dAngleCoefficientTurret;
	double angleDeadBand;
	
	boolean runConveyer;
	boolean runShooter;
	boolean adjAzimuth;
	
	public Shooter(RobotInputs inputs, RobotOutputs outputs){
		super(inputs, outputs);
	}
	
	/* for when we add in the chute
	 * 
	void turnChuteToAngle(double theta){
		double dTheta = theta;// - inputs.ChuteAngleSensor.get()
		double chuteTurnSpeed = 0;
		if(Math.abs(dTheta)>angleDeadBand)
			chuteTurnSpeed = sigmoid(dTheta, dAngleCoefficientChute);
		outputs.setChute(chuteTurnSpeed);
	}
	void turnChuteDegrees(double theta){
		
	}
	*/
	
	/* for when the turret is implemented (if ever)
	 * 
	void turnTurretToAngle(double theta){
		double dTheta = theta;// - inputs.TurretAngleSensor.get()
		double turretTurnSpeed = 0;
		if(Math.abs(dTheta)>angleDeadBand)
			turretTurnSpeed = sigmoid(dTheta, dAngleCoefficientTurret);
		outputs.setTurret(turretTurnSpeed);
	}
	void turnTurretDegrees(double theta){
		
	}
	*/
	double sigmoid(double t, double k){
		return 2/(1+Math.pow(Math.E, -t))-1;
	}
	
	@Override
	public void robotInit(){
		
	}
	@Override
	public void robotPeriodic(int loopCounter){
		
	}
	@Override
	public void disabledInit(){
		
	}
	@Override
	public void disabledPeriodic(int loopCounter){
		
	}
	@Override
	public void autonomousInit(){
		
	}
	@Override
	public void autonomousPeriodic(int loopCounter){
		
	}
	@Override
	public void teleopInit(){
		
	}
	@Override
	public void teleopPeriodic(int loopCounter){
		boolean runIndexer = inputs.funStick.getRawButton(1);
		boolean shooterOn = inputs.funStick.getRawButton(4);
		boolean shooterOff = inputs.funStick.getRawButton(5);
		boolean azimUp = inputs.funStick.getRawButton(6);
		boolean azimDown = inputs.funStick.getRawButton(7);
		boolean conveyerOn = inputs.funStick.getRawButton(8);
		double shootPow = (inputs.funStick.getRawAxis(2)+1.0)/2.0;
		
		runShooter = shooterOn? true : shooterOff? false : runShooter;
		runConveyer = conveyerOn? true : shooterOff? false : runConveyer;
		adjAzimuth = azimUp | azimDown;

		outputs.setShooter(runShooter ? shootPow : 0.0);
		outputs.setIndexer(runIndexer ? 1.0 : 0.0);
		outputs.setConveyer(runConveyer ? 1.0 : 0.0);
		outputs.setAzimuth(adjAzimuth? azimUp? 0.2 : -0.2 : 0.0);
	}
	@Override
	public void testInit(){
		
	}
	@Override
	public void testPeriodic(int loopCounter){
		
	}
}
