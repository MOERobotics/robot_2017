package org.usfirst.frc.team365.modules;

import org.usfirst.frc.team365.robot.IOPortPage;

import com.ctre.CANTalon;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.SpeedController;

public final class RobotOutputs{
	private CANTalon driveL1;
	private CANTalon driveL2;
	private CANTalon driveL3;
	private CANTalon driveR1;
	private CANTalon driveR2;
	private CANTalon driveR3;
	private SpeedController collector;
	private SpeedController shooter;
	private SpeedController chute;
	private SpeedController turret;
	
	private DoubleSolenoid gearShift;
	
	public RobotOutputs(){
		driveL1 = new CANTalon(IOPortPage.DRIVE_L1_PORT);
		driveL2 = new CANTalon(IOPortPage.DRIVE_L2_PORT);
		driveL3 = new CANTalon(IOPortPage.DRIVE_L3_PORT);
		driveR1 = new CANTalon(IOPortPage.DRIVE_R1_PORT);
		driveR2 = new CANTalon(IOPortPage.DRIVE_R2_PORT);
		driveR3 = new CANTalon(IOPortPage.DRIVE_R3_PORT);
		gearShift = new DoubleSolenoid(IOPortPage.SHIFT_FWD_CH,IOPortPage.SHIFT_BAK_CH);
	}
	public void init(){
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
	public void setDriveL1(double value){
		driveL1.set(value);
	}
	public void setDriveL2(double value){
		driveL2.set(value);
	}
	public void setDriveL3(double value){
		driveL3.set(value);
	}
	public void setDriveR1(double value){
		driveR1.set(value);
	}
	public void setDriveR2(double value){
		driveR2.set(value);
	}
	public void setDriveR3(double value){
		driveR3.set(value);
	}
	public void setCollector(double value){
		collector.set(value);
	}
	public void setShooter(double value){
		shooter.set(value);
	}
	public void setChute(double value){
		chute.set(value);
	}
	public void setTurret(double value){
		turret.set(value);
	}
	public void setGearShift(Value value){
		gearShift.set(value);
	}
}
