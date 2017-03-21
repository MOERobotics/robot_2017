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
	private CANTalon climberA;
	private CANTalon climberB;
	
	private DoubleSolenoid gearShift;
	private DoubleSolenoid releaseGear;
	private Solenoid ballFlap;
	private Solenoid signalLight;
	
	public RobotOutputs(){
		driveL1 = new CANTalon(IOPort.DRIVE_L1);
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
		climberA = new CANTalon(IOPort.CLIMBER_A);
		climberB = new CANTalon(IOPort.CLIMBER_B);
		
		gearShift = new DoubleSolenoid(IOPort.SHIFT_FWD, IOPort.SHIFT_BAK);
		releaseGear = new DoubleSolenoid(IOPort.GEAR_RELEASE_BAK, IOPort.GEAR_RELEASE_FWD);
		ballFlap = new Solenoid(IOPort.BALL_FLAP);
		signalLight = new Solenoid(3);
		
		motorInit();
	}
	public void motorInit(){
		driveL1.setInverted(true);
		driveL2.setInverted(true);
		driveL3.setInverted(true);
		indexer.setInverted(true);
		feeder.setInverted(true);
		//for LawnMOEr climbers are inverted, not for LocoMOEtive
		//climberA.setInverted(true);
		//climberB.setInverted(true);
		
		driveL1.enableBrakeMode(true);
		driveL2.enableBrakeMode(true);
		driveL3.enableBrakeMode(true);
		driveR1.enableBrakeMode(true);
		driveR2.enableBrakeMode(true);
		driveR3.enableBrakeMode(true);
		climberA.enableBrakeMode(true);
		climberB.enableBrakeMode(true);
		azimuth.enableBrakeMode(true);
		
		
		
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
	public void setClimberRaw(double value){
		climberA.set(value);
		climberB.set(value);
	}
	public void setSignalLight(boolean value){
		signalLight.set(value);
	}
	public double getAvgClimberAmps(){
		return (climberA.getOutputCurrent()+climberB.getOutputCurrent())/2.0;
	}
	public double getAzimuthPosition(){
		return azimuth.getPulseWidthPosition()%4096;
	}
}
