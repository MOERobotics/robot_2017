package org.usfirst.frc.team365.modules;

import org.usfirst.frc.team365.math.LinearRegression;
import org.usfirst.frc.team365.net.MOETracker;
import org.usfirst.frc.team365.util.RobotModule;

import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Shooter extends RobotModule
{
	double collectSpeed=0.75;
	double azimSpeed=0.25;
	double feederSpeed=0.75;
	
	double closeEnough = 2;
	double dThetaCoeff = 1;
	
	boolean runFeeder;
	boolean runShooter;
	
	//double P=1, I=1, D=1, F=1;
	//PIDController shooterpid;
	
	MOETracker tracker;
	
	LinearRegression lg;
	
	public Shooter(RobotInputs inputs, RobotOutputs outputs, MOETracker tracker){
		super(inputs, outputs);
		this.tracker=tracker;
		lg = new LinearRegression();
	//	shooterpid = new PIDController(P, I, D, F, inputs.shooterSpeed, outputs::setShooter);
	//	shooterpid.setInputRange(-1400, 0);
	//	shooterpid.setOutputRange(-1, 1);
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
		
	//	SmartDashboard.putNumber("P", 0);
	//	SmartDashboard.putNumber("I", 0);
	//	SmartDashboard.putNumber("D", 0);
	//	SmartDashboard.putNumber("F", 0);
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
		distanceToAzimuth();
		
		
		runShooter = shooterOn? true : shooterOff? false : runShooter;
		runFeeder = feederOn? true : feederOff? false : runFeeder;
		collectorIn = runShooter? true: collectorIn;
		
	//	shooterpid.setPID(
	//		SmartDashboard.getNumber("P", 0),
	//		SmartDashboard.getNumber("I", 0),
	//		SmartDashboard.getNumber("D", 0),
	//		SmartDashboard.getNumber("F", 0)
	//	);
		SmartDashboard.putNumber("Shoot rate", inputs.shooterSpeed.getRate());
		//shooterpid.setSetpoint(runShooter ? 0.8 : 0.0);
		outputs.setShooter(runShooter ? 0.81 : 0.0);
		outputs.setIndexer(runIndexer ? 1.0 : 0.0);
		outputs.setFeeder(runFeeder ? feederSpeed : 0.0);
		outputs.setAzimuth(azimUp? -azimSpeed : azimDown? azimSpeed : 0.0);
		outputs.setCollector(collectorIn? collectSpeed : collectorOut? -collectSpeed : 0.0);
	}
	@Override
	public void testInit(){
		
	}
	@Override
	public void testPeriodic(int loopCounter){
		distanceToAzimuth();
	}
	public void distanceToAzimuth(){
		double distance = getTrackerDist();
		if(inputs.driveStick.getRawButton(10)){
			lg.addPoint(distance, outputs.getAzimuthPosition());
			lg.calculateLinReg();
			System.out.println("\n********************DIST2AZIM****\n");
		}else if(inputs.driveStick.getRawButton(9)){
			lg.clear();
		}
		
		double azimuth = lg.predict(distance);
		SmartDashboard.putNumber("tracker azim", azimuth);
		SmartDashboard.putString("dist2azim", lg.toString());
	}
	/**
	 * +- 1.5 in
	 */
	double cetnerHigh_y = 0.14, pixelsToDist=1;
	public double getTrackerDist(){
		double tracker_y=tracker.getCenter()[1];		//center val - the read tracker val from min dist
		double distance = tracker_y*91.3+2.46;				//tracker y scaled + min dist; is in inches
		SmartDashboard.putNumber("tracker dist", distance);	//log
		return distance;
	}
	
	public double limit(double x, double m, double M){
		if(x<m) return m;
		if(x>M) return M;
		else	return x;
	}
}
