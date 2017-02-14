package org.usfirst.frc.team365.modules;

import org.usfirst.frc.team365.util.RobotModule;

import edu.wpi.first.wpilibj.DoubleSolenoid.Value;

public class GearMechanism extends RobotModule {
	
	final Value GEAR_DN = Value.kForward;
	final Value GEAR_UP = Value.kReverse;
	final boolean GEAR_GRAB = true;
	final boolean GEAR_DROP = false;
	
	public GearMechanism(RobotInputs inputs, RobotOutputs outputs){
		super(inputs, outputs);
	}
	@Override
	public void robotInit(){
		outputs.setGearCollector(GEAR_DROP);
		outputs.setGearReleaser(GEAR_UP);
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
			outputs.setGearCollector(GEAR_GRAB);
		}
		else {
			outputs.setGearCollector(GEAR_DROP);
		}
		
		if (inputs.funStick.getRawButton(11)) {
			outputs.setGearReleaser(GEAR_UP);
		}
		else if (inputs.funStick.getRawButton(10)) {
			outputs.setGearReleaser(GEAR_DN);
		}
		
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
	
	int gearRoutineStep;
	int loopCounterInit;
	public void gearRoutine(int loopCounter){
		if(loopCounterInit==-1)
			loopCounterInit = loopCounter;
		switch(gearRoutineStep){
			case 1: // release gear for 1 sec : 50
				
				break;
			case 2:
				
				break;
			case 3:
				
				break;
			default:
				
				break;
		}
	}
}
