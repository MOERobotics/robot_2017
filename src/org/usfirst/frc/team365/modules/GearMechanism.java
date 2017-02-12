package org.usfirst.frc.team365.modules;

import org.usfirst.frc.team365.util.RobotModule;

import edu.wpi.first.wpilibj.DoubleSolenoid.Value;

public class GearMechanism extends RobotModule {
	
	public GearMechanism(RobotInputs inputs, RobotOutputs outputs){
		super(inputs, outputs);
	}
	@Override
	public void robotInit(){
		outputs.setGearCollector(false);
		outputs.setGearReleaser(Value.kReverse);
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
		if (inputs.funStick.getRawButton(9)) {
			outputs.setGearCollector(true);
		}
		else {
			outputs.setGearCollector(false);
		}
		
		if (inputs.funStick.getRawButton(11)) {
			outputs.setGearReleaser(Value.kForward);
		}
		else if (inputs.funStick.getRawButton(10)) {
			outputs.setGearReleaser(Value.kReverse);
		}
		/*
		if (inputs.funStick.getRawButton(2)) { //turns [ball] collector on
			collector.set(1);
		}
		else if (inputs.funStick.getRawButton(3)) { //turns [ball] collector off
			collector.set(0);
		}*/
		
		/*if (funStick.getRawButton(10)) { //need to change from button 10
			switch (teleopStep) {
			
			case 1: //release gear for 1.5 seconds (75 loops)
				if (teleopLoopCounter > 75) {
					releaseGear.set(false); //do I need this?
					teleopStep = 2;
					driveEncoder.reset();
				}
				else {
					releaseGear.set(true);
					teleopLoopCounter++;
				}
				break;
				
			case 2: //back up for 2 seconds (100 loops)
				if (driveEncoder.getRaw() < -100) { //teleopLoopCounter > 175
					teleopStep = 3;
					driveEncoder.reset();
				}
				else {
					driveRobot(-0.4, -0.4);
				}
				break;
				
			case 3: //stop
				driveRobot(0, 0);
				break;
			}
		}
		else {
			teleopStep = 1;
			teleopLoopCounter = 0;
			driveEncoder.reset();
		}*/
	}
	@Override
	public void testInit(){
		
	}
	@Override
	public void testPeriodic(int loopCounter){
		
	}
}
