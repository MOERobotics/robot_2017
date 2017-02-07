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