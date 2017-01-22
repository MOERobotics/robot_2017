package org.usfirst.frc.team365.util;

public abstract class RobotModule{
	public RobotModule(){
		
	}
	public abstract void robotInit();
	public abstract void robotPeriodic();
	public abstract void disabledInit();
	public abstract void disabledPeriodic();
	public abstract void autonomousInit();
	public abstract void autonomousPeriodic();
	public abstract void teleopInit();
	public abstract void teleopPeriodic();
	public abstract void testInit();
	public abstract void testPeriodic();
}
