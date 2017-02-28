package org.usfirst.frc.team365.modules;

import org.usfirst.frc.team365.robot.IOPort;

import com.ctre.CANTalon;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.PIDSourceType;
import edu.wpi.first.wpilibj.Solenoid;

public final class RobotOutputs{
	private CANTalon driveL1;
	private CANTalon driveL2;
	private CANTalon driveL3;
	private CANTalon driveR1;
	private CANTalon driveR2;
	private CANTalon driveR3;
	private CANTalon collector;
	private CANTalon shooterA;
	private CANTalon shooterB;
	private CANTalon indexer;
	private CANTalon feeder;
	private CANTalon azimuth;
	private CANTalon climber;
	
	private DoubleSolenoid gearShift;
	private DoubleSolenoid releaseGear;
	private Solenoid ballFlap;
	
	public RobotOutputs(){
		driveL1 = new CANTalon(IOPort.DRIVE_L1);//
		driveL2 = new CANTalon(IOPort.DRIVE_L2);
		driveL3 = new CANTalon(IOPort.DRIVE_L3);
		driveR1 = new CANTalon(IOPort.DRIVE_R1);
		driveR2 = new CANTalon(IOPort.DRIVE_R2);
		driveR3 = new CANTalon(IOPort.DRIVE_R3);
		collector = new CANTalon(IOPort.COLLECTOR);
		shooterA = new CANTalon(IOPort.SHOOT_SPIN_A);
		shooterB = new CANTalon(IOPort.SHOOT_SPIN_B);
		indexer = new CANTalon(IOPort.INDEXER);
		feeder = new CANTalon(IOPort.FEEDER);
		azimuth = new CANTalon(IOPort.AZIMUTH);
		climber = new CANTalon(IOPort.CLIMBER);
		
		gearShift = new DoubleSolenoid(IOPort.SHIFT_FWD, IOPort.SHIFT_BAK);
		releaseGear = new DoubleSolenoid(IOPort.GEAR_RELEASE_BAK, IOPort.GEAR_RELEASE_FWD);
		ballFlap = new Solenoid(IOPort.BALL_FLAP);
		
		motorInit();
	}
	public void motorInit(){
		driveR1.setInverted(true);
		driveR2.setInverted(true);
		driveR3.setInverted(true);
	//	shooterA.setInverted(true);
	//	shooterB.setInverted(true);
		indexer.setInverted(true);
		feeder.setInverted(true);
		climber.setInverted(true);
		
		driveL1.enableBrakeMode(true);
		driveL2.enableBrakeMode(true);
		driveL3.enableBrakeMode(true);
		driveR1.enableBrakeMode(true);
		driveR2.enableBrakeMode(true);
		driveR3.enableBrakeMode(true);
		climber.enableBrakeMode(true);
		
		shooterA.setPIDSourceType(PIDSourceType.kRate);
		
	}
	public void setDriveLA(double value){
		driveL1.set(value);
	}
	public void setDriveLB(double value){
		driveL2.set(value);
	}
	public void setDriveLC(double value){
		driveL3.set(value);
	}
	public void setDriveRA(double value){
		driveR1.set(value);
	}
	public void setDriveRB(double value){
		driveR2.set(value);
	}
	public void setDriveRC(double value){
		driveR3.set(value);
	}
	public void setCollector(double value){
		collector.set(value);
	}
	public void setShooter(double value){
		shooterA.set(value);
		shooterB.set(value);
	}
	public void setIndexer(double value){
		indexer.set(value);
	}
	public void setFeeder(double value){
		feeder.set(value);
	}
	public void setAzimuth(double value){
		azimuth.set(value);
	}
	public void setGearShift(Value value){
		gearShift.set(value);
	}
	public void setGearReleaser(Value value){
		releaseGear.set(value);
	}
	public void setBallFlap(boolean value){
		ballFlap.set(value);
	}
	final private double climberLoad = 1000;
	public void setClimber(double value){
		if(climber.getOutputCurrent()>climberLoad){
			value*=2;
		}climber.set(value);
	}
	public void setClimberRaw(double value){
		climber.set(value);
	}
	public double getClimberAmps(){
		return climber.getOutputCurrent();
	}
	public double getAzimuthPosition(){
		return azimuth.getPulseWidthPosition();
	}
}
