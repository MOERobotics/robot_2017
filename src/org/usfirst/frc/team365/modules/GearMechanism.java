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
		outputs.setBallFlap(GEAR_DROP);
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
	public void autonomousPeriodic(int loopCounter, int autoRoutine){
		
	}
	@Override
	public void teleopInit(){
		
	}
	@Override
	public void teleopPeriodic(int loopCounter){
		if (inputs.funStick.getY()<-0.5) {
			outputs.setBallFlap(GEAR_GRAB);
		}else {
			outputs.setBallFlap(GEAR_DROP);
		}
		
		if (inputs.funStick.getRawButton(6)) {
			outputs.setGearReleaser(GEAR_UP);
		}else if (inputs.funStick.getRawButton(7)) {
			outputs.setGearReleaser(GEAR_DN);
		}
	}
	@Override
	public void testInit(){
		
	}
	@Override
	public void testPeriodic(int loopCounter){
		
	}
	
	int gearRoutineStep = 1;
	int loopCounterInit = -1;
	public void gearRoutine(int loopCounter){
		if(loopCounterInit==-1)
			loopCounterInit = loopCounter;
		int dLoopCounter = loopCounter-loopCounterInit;
		inputs.isDriveOverrided=true;
		switch(gearRoutineStep){
			case 1: // release gear for 1 sec : 50
				if(dLoopCounter<50){
					outputs.setGearReleaser(GEAR_DN);
				}else{
					outputs.setGearReleaser(GEAR_UP);
					gearRoutineStep = 2;
					inputs.leftEncoder.reset();
					inputs.rightEncoder.reset();
				}
				break;
			case 2: // back up for 50 iterations
				int dist = inputs.getDriveEncoderRawMax();
				if(dist<50){
					Drivetrain.driveRobot(-0.4, -0.4);
				}else{
					Drivetrain.driveRobot(0, 0);
					gearRoutineStep = 1;
					inputs.isDriveOverrided=false;
					inputs.leftEncoder.reset();
					inputs.rightEncoder.reset();
				}
				break;
			default:
				System.err.println("a bad thing happeded");
				break;
		}
	}
}
