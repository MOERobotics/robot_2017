package org.usfirst.frc.team365.modules;

import org.usfirst.frc.team365.math.LinearRegression;
import org.usfirst.frc.team365.net.MOETracker;
import org.usfirst.frc.team365.util.RobotModule;
import org.usfirst.frc.team365.modules.AutoTargeting;

import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Shooter extends RobotModule
{
	double collectSpeed=1;
	double azimSpeed=0.25;
	double feederSpeed=0.75;
	
	double closeEnough = 2;
	double dThetaCoeff = 1;
	
	double addToShootSpeed=0;
	
	boolean runFeeder;
	boolean runShooter;
	
	//double P=1, I=1, D=1, F=1;
	//PIDController shooterpid;
	
	
	LinearRegression lg;
	
	boolean lastfstick10, lastfstick11;
	
	public Shooter(RobotInputs inputs, RobotOutputs outputs){
		super(inputs, outputs);
		lg = new LinearRegression();
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
		outputs.setShooter(0);
		outputs.setIndexer(0);
		outputs.setFeeder(0);
		outputs.setAzimuth(0);
		outputs.setCollector(0);
		outputs.setSignalLight(false);
	}
	@Override
	public void disabledPeriodic(int loopCounter){
		
	}
	@Override
	public void autonomousInit(){
		outputs.setShooter(0);
		outputs.setIndexer(0);
		outputs.setFeeder(0);
		outputs.setAzimuth(0);
		outputs.setCollector(0);
		outputs.setSignalLight(true);
	}
	@Override
	public void autonomousPeriodic(int loopCounter, int autoRoutine){
		
	}
	@Override
	public void teleopInit(){
		outputs.setShooter(0);
		outputs.setIndexer(0);
		outputs.setFeeder(0);
		outputs.setAzimuth(0);
		outputs.setCollector(0);
		outputs.setSignalLight(false);
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
//		distanceToAzimuth();
		
		runShooter = shooterOn? true : shooterOff? false : runShooter;
		runFeeder = feederOn? true : feederOff? false : runFeeder;
		//collectorIn = runShooter? true: collectorIn;
		
		outputs.setShooter(runShooter ? shootPow : 0.0);
		outputs.setSignalLight(runShooter);
		outputs.setIndexer(runIndexer ? 1.0 : 0.0);
		if(!shooterOff)
			outputs.setFeeder(runFeeder ? feederSpeed : 0.0);
		else{
			outputs.setFeeder(0);
			runFeeder=false;
		}
		outputs.setAzimuth(azimUp? -azimSpeed : azimDown? azimSpeed : 0.0);
		if(runShooter){
			outputs.setCollector(1);
		}else{
			outputs.setCollector(collectorIn? 1 : collectorOut? -1 : 0.0);
		}
		
		if(inputs.funStick.getRawButton(10)){
			addToShootSpeed+=0.03;
		}else if(inputs.funStick.getRawButton(11)){
			addToShootSpeed-=0.03;
		}
		lastfstick10=inputs.funStick.getRawButton(10);
		lastfstick11=inputs.funStick.getRawButton(11);
	}
	@Override
	public void testInit(){
		
	}
	@Override
	public void testPeriodic(int loopCounter){
//		distanceToAzimuth();
	}
	
	/**
	 * +- 1.5 in
	 */
	double cetnerHigh_y = 0.14, pixelsToDist=1;
	
	
	public double limit(double x, double m, double M){
		if(x<m) return m;
		if(x>M) return M;
		else	return x;
	}
	
	double Kp, Ki, Kd, Kf, inputMin, inputMax;
	PIDController shooterController;
	public void fragment(){
		shooterController=new PIDController(Kp, Ki, Kd, Kf, inputs.shooterSpeed, outputs::setShooter);
		shooterController.setContinuous(false);
		shooterController.setInputRange(inputMin, inputMax);
		shooterController.setOutputRange(0, 1);
		
		//shooterController.setSetpoint(setpoint);
	}
}
