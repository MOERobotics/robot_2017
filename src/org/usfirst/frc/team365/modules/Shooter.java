package org.usfirst.frc.team365.modules;

import org.usfirst.frc.team365.util.RobotModule;

public class Shooter extends RobotModule
{
	double collectSpeed=0.75;
	double azimSpeed=0.25;
	double feederSpeed=0.75;
	
	double closeEnough = 2;
	double dThetaCoeff = 1;
	
	boolean runFeeder;
	boolean runShooter;
	
	public Shooter(RobotInputs inputs, RobotOutputs outputs){
		super(inputs, outputs);
	}
	
	void turnAzimuthToAngle(double theta){
		double dTheta = theta - outputs.getAzimuthPosition();
		double turnPower = 0;
		if(Math.abs(dTheta)<closeEnough){
			return;
		}
		turnPower = azimSpeed * Math.tanh(dTheta * dThetaCoeff);
		outputs.setAzimuth(turnPower);
	}
	void startShootRoutine(){
		outputs.setCollector(collectSpeed);
		outputs.setFeeder(feederSpeed);
		outputs.setShooter(.80);
	}
	void startShooting(){
		outputs.setIndexer(1.0);
	}
	void stopShootRoutine(){
		outputs.setCollector(0);
		outputs.setFeeder(0);
		outputs.setShooter(0);
		outputs.setIndexer(0);
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
		outputs.setShooter(.75);
	}
	@Override
	public void autonomousPeriodic(int loopCounter){
		
	}
	@Override
	public void teleopInit(){
		outputs.setShooter(0.0);
	}
	@Override
	public void teleopPeriodic(int loopCounter){
		boolean runIndexer = inputs.funStick.getRawButton(1);
		boolean azimUp = inputs.funStick.getRawButton(2);
		boolean azimDown = inputs.funStick.getRawButton(3);
		boolean shooterOn = inputs.funStick.getRawButton(4);
		boolean shooterOff = inputs.funStick.getRawButton(5);
		boolean feederOn = inputs.funStick.getRawButton(8);
		boolean feederOff = inputs.funStick.getRawButton(9);
		boolean collectorIn = inputs.driveStick.getRawButton(5);
		boolean collectorOut = inputs.driveStick.getRawButton(6);
		double shootPow = (inputs.driveStick.getRawAxis(2)+1.0)/2.0;
		
		runShooter = shooterOn? true : shooterOff? false : runShooter;
		runFeeder = feederOn? true : feederOff? false : runFeeder;
		collectorIn = runShooter? true: collectorIn;

		outputs.setShooter(runShooter ? shootPow : 0.0);
		outputs.setIndexer(runIndexer ? 1.0 : 0.0);
		outputs.setFeeder(runFeeder ? feederSpeed : 0.0);
		outputs.setAzimuth( azimUp? -azimSpeed : azimDown? azimSpeed : 0.0);
		outputs.setCollector(collectorIn? collectSpeed : collectorOut? -collectSpeed : 0.0);
	}
	@Override
	public void testInit(){
		
	}
	@Override
	public void testPeriodic(int loopCounter){
		
	}
}
