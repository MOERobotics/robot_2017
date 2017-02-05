package org.usfirst.frc.team365.util;

import org.usfirst.frc.team365.modules.RobotInputs;
import org.usfirst.frc.team365.modules.RobotOutputs;

public abstract class RobotModule{
	protected RobotInputs  inputs;
	protected RobotOutputs outputs;
	public RobotModule(RobotInputs inputs, RobotOutputs outputs){
		this.inputs=inputs;
		this.outputs=outputs;
	}
	public abstract void robotInit();
	public abstract void robotPeriodic(int loopCounter);
	public abstract void disabledInit();
	public abstract void disabledPeriodic(int loopCounter);
	public abstract void autonomousInit();
	public abstract void autonomousPeriodic(int loopCounter);
	public abstract void teleopInit();
	public abstract void teleopPeriodic(int loopCounter);
	public abstract void testInit();
	public abstract void testPeriodic(int loopCounter);
}
