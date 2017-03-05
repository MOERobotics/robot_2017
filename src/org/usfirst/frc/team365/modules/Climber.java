package org.usfirst.frc.team365.modules;

import org.usfirst.frc.team365.util.RobotModule;

public class Climber extends RobotModule
{
	public Climber(RobotInputs inputs, RobotOutputs outputs){
		super(inputs, outputs);
	}
	@Override
	public void robotInit(){
		outputs.setClimberRaw(0);
	}
	@Override
	public void robotPeriodic(int loopCounter){
		
	}

	/*only here for compliance reasons*/
	@Override
	public void disabledInit(){}
	@Override
	public void disabledPeriodic(int loopCounter){}
	@Override
	public void autonomousInit(){}
	@Override
	public void autonomousPeriodic(int loopCounter, int autoRoutine){}
	@Override
	public void teleopInit(){
		
	}
	@Override
	public void teleopPeriodic(int loopCounter){
		if(inputs.funStick.getY()>.5)
			outputs.setClimberRaw(0.5);
	}
	@Override
	public void testInit(){
		
	}
	@Override
	public void testPeriodic(int loopCounter){
		
	}
}