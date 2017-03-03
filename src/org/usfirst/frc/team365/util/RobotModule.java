package org.usfirst.frc.team365.util;

import org.usfirst.frc.team365.modules.RobotInputs;
import org.usfirst.frc.team365.modules.RobotOutputs;

public abstract class RobotModule{
	protected static RobotInputs  inputs;
	protected static RobotOutputs outputs;
	public RobotModule(RobotInputs inputs, RobotOutputs outputs){
		RobotModule.inputs=inputs;
		RobotModule.outputs=outputs;
	}
	public abstract void robotInit();
	public abstract void robotPeriodic(int loopCounter);
	public abstract void disabledInit();
	public abstract void disabledPeriodic(int loopCounter);
	public abstract void autonomousInit();
	public abstract void autonomousPeriodic(int loopCounter, int autoRoutine);
	public abstract void teleopInit();
	public abstract void teleopPeriodic(int loopCounter);
	public abstract void testInit();
	public abstract void testPeriodic(int loopCounter);
	public double pulsesToInch(double pulses){
		return pulses / 107.0 ;
	}
}