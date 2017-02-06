package org.usfirst.frc.team365.modules;

import org.usfirst.frc.team365.util.RobotModule;

import com.ctre.CANTalon;
import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Jaguar;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Solenoid;

public class GearMechanism extends RobotModule {
	
	AHRS navX;
	
	CANTalon driveLA = new CANTalon(12);
	CANTalon driveLB = new CANTalon(13);
	CANTalon driveLC = new CANTalon(14);
	//CANTalon driveLD = new CANTalon(15);
	CANTalon driveRA = new CANTalon(1);
	CANTalon driveRB = new CANTalon(2);
	CANTalon driveRC = new CANTalon(3); 
	//CANTalon driveRD = new CANTalon(4);
	
	Jaguar leftSide;
	Jaguar rightSide;
	
	Solenoid collectGear;
	Solenoid releaseGear;
	Joystick funStick;
	Encoder driveEncoder;
	
	int teleopStep;
	int teleopLoopCounter;
	
	public GearMechanism(RobotInputs inputs, RobotOutputs outputs){
		super(inputs, outputs);
	}
	@Override
	public void robotInit(){
		driveRA.setInverted(true);
    	driveRB.setInverted(true);
    	driveRC.setInverted(true);
    	
    	driveLA.enableBrakeMode(true);
    	driveLB.enableBrakeMode(true);
    	driveLC.enableBrakeMode(true);
    	driveRA.enableBrakeMode(true);
    	driveRB.enableBrakeMode(true);
    	driveRC.enableBrakeMode(true);
		
		funStick = new Joystick(1);
		collectGear = new Solenoid(2);
		releaseGear = new Solenoid(3);
		collectGear.set(false);
		releaseGear.set(false);
		//driveEncoder = new Encoder(2, 3, true, EncodingType.k2X);
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
		teleopLoopCounter = 0;
		teleopStep = 1;
	}
	@Override
	public void teleopPeriodic(int loopCounter){
		if (funStick.getRawButton(7)) {
			collectGear.set(true);
		}
		else {
			collectGear.set(false);
		}
		
		if (funStick.getRawButton(8)) {
			releaseGear.set(true);
		}
		else if (!funStick.getRawButton(10)) {
			releaseGear.set(false);
		}
		
		/*if (funStick.getRawButton(10)) {
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
	
	/*public void driveRobot(double leftMotor, double rightMotor){
        driveLA.set(leftMotor);
        driveLB.set(leftMotor);
        driveLC.set(leftMotor);
        driveRA.set(rightMotor);
        driveRB.set(rightMotor);
        driveRC.set(rightMotor);
    }*/
}
