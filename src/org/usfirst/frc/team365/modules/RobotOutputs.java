package org.usfirst.frc.team365.modules;

import com.ctre.CANTalon;

import edu.wpi.first.wpilibj.DoubleSolenoid;

public final class RobotOutputs{
	/*
	 * consts for ports
	 */
	//
	private static final int DRIVE_LF_PORT = 12;
	private static final int DRIVE_LM_PORT = 13;
	private static final int DRIVE_LR_PORT = 14;
	private static final int DRIVE_RF_PORT = 01;
	private static final int DRIVE_RM_PORT = 02;
	private static final int DRIVE_RR_PORT = 03;
	//PCMs
	private static final int SHIFT_FWD_CH  = 00;
	private static final int SHIFT_BAK_CH  = 01;
	
	/*
	 * the actual output objects
	 */
	public final CANTalon driveLF = new CANTalon(DRIVE_LF_PORT);
	public final CANTalon driveLM = new CANTalon(DRIVE_LM_PORT);
	public final CANTalon driveLR = new CANTalon(DRIVE_LR_PORT);
	public final CANTalon driveRF = new CANTalon(DRIVE_RF_PORT);
	public final CANTalon driveRM = new CANTalon(DRIVE_RM_PORT);
	public final CANTalon driveRR = new CANTalon(DRIVE_RR_PORT);
	public final DoubleSolenoid gearShift = new DoubleSolenoid(SHIFT_FWD_CH,SHIFT_BAK_CH);
	
	public RobotOutputs(){
		driveLF.enableBrakeMode(true);
		driveLM.enableBrakeMode(true);
		driveLR.enableBrakeMode(true);
		driveRF.enableBrakeMode(true);
		driveRM.enableBrakeMode(true);
		driveRR.enableBrakeMode(true);
		driveRF.setInverted(true);
		driveRM.setInverted(true);
		driveRR.setInverted(true);
	}
}
