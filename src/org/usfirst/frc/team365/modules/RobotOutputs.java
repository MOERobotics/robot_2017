package org.usfirst.frc.team365.modules;

import org.usfirst.frc.team365.robot.IOPortPage;

import com.ctre.CANTalon;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.Solenoid;

public final class RobotOutputs{
	private static CANTalon driveL1;
	private static CANTalon driveL2;
	private static CANTalon driveL3;
	private static CANTalon driveR1;
	private static CANTalon driveR2;
	private static CANTalon driveR3;
	private static CANTalon collector;
	private static CANTalon shooterA;
	private static CANTalon shooterB;
	private static CANTalon indexer;
	private static CANTalon feeder;
	private static CANTalon azimuth;
	private static CANTalon climber;
	
	private static DoubleSolenoid gearShift;
	private static DoubleSolenoid releaseGear;
	private static Solenoid collectGear;
	
	private static boolean notInit = true;
	private static RobotOutputs out;
	public static RobotOutputs init(){
		return notInit?(out = new RobotOutputs()):out;
	}
	
	private RobotOutputs(){
		driveL1 = new CANTalon(IOPortPage.DRIVE_L1);
		driveL2 = new CANTalon(IOPortPage.DRIVE_L2);
		driveL3 = new CANTalon(IOPortPage.DRIVE_L3);
		driveR1 = new CANTalon(IOPortPage.DRIVE_R1);
		driveR2 = new CANTalon(IOPortPage.DRIVE_R2);
		driveR3 = new CANTalon(IOPortPage.DRIVE_R3);
		shooterA = new CANTalon(IOPortPage.SHOOT_SPIN_A);
		shooterB = new CANTalon(IOPortPage.SHOOT_SPIN_B);
		indexer = new CANTalon(IOPortPage.INDEXER);
		feeder = new CANTalon(IOPortPage.FEEDER);
		azimuth = new CANTalon(IOPortPage.AZIMUTH);
		climber = new CANTalon(IOPortPage.CLIMBER);
		gearShift = new DoubleSolenoid(IOPortPage.SHIFT_FWD_CH,IOPortPage.SHIFT_BAK_CH);
		
		motorInit();
	}
	public void motorInit(){
		driveL1.enableBrakeMode(true);
		driveL2.enableBrakeMode(true);
		driveL3.enableBrakeMode(true);
		driveR1.enableBrakeMode(true);
		driveR2.enableBrakeMode(true);
		driveR3.enableBrakeMode(true);
		driveR1.setInverted(true);
		driveR2.setInverted(true);
		driveR3.setInverted(true);
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
	public void setGearCollector(boolean value){
		collectGear.set(value);
	}
	public void setClimber(double value){
		climber.set(value);
	}
}
