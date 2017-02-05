package org.usfirst.frc.team365.modules;

import org.usfirst.frc.team365.util.RobotModule;

public class Shooter extends RobotModule
{
	double shootSpeed;
	double collectSpeed;
	
	public Shooter(RobotInputs inputs, RobotOutputs outputs){
		super(inputs, outputs);
	}
	
	void turnChuteToAngle(double theta){
		
	}
	void turnChuteDegrees(double theta){
		
	}
	void turnTurretToAngle(double theta){
		
	}
	void turnTurretDegrees(double theta){
		
	}
	void runShooter(){
		outputs.setCollector(collectSpeed);
		outputs.setShooter(shootSpeed);
	}
	void stopShooter(){
		outputs.setCollector(0);
		outputs.setShooter(0);
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
		
	}
	@Override
	public void testInit(){
		
	}
	@Override
	public void testPeriodic(int loopCounter){
		
	}
}
