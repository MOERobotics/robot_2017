package org.usfirst.frc.team365.modules;

import org.usfirst.frc.team365.util.RobotModule;

import edu.wpi.first.wpilibj.SpeedController;

public class Shooter extends RobotModule
{
	SpeedController collector;
	SpeedController shooter;
	SpeedController chute;
	SpeedController turret;
	double shootSpeed;
	double collectSpeed;
	
	public Shooter(){
		super();
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
		collector.set(collectSpeed);
		shooter.set(shootSpeed);
	}
	void stopShooter(){
		collector.set(0);
		shooter.set(0);
	}
	
	@Override
	public void robotInit(){
		
	}
	@Override
	public void robotPeriodic(){
		
	}
	@Override
	public void disabledInit(){
		
	}
	@Override
	public void disabledPeriodic(){
		
	}
	@Override
	public void autonomousInit(){
		
	}
	@Override
	public void autonomousPeriodic(){
		
	}
	@Override
	public void teleopInit(){
		
	}
	@Override
	public void teleopPeriodic(){
		
	}
	@Override
	public void testInit(){
		
	}
	@Override
	public void testPeriodic(){
		
	}
}
