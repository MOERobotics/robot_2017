package org.usfirst.frc.team365.modules;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.SPI.Port;

public class RobotInputs{
	/*
	 * consts for ports
	 */
	private static final int DRIVE_STICK_PORT = 0;
	private static final int FUNCT_STICK_PORT = 1;
	private static final Port NAVX_SPI_PORT = SPI.Port.kMXP;
	private static final byte NAVX_UPDATE_HZ = 50;
	
	/*
	 * the actual input objects
	 */
	public final Joystick driveStick = new Joystick(DRIVE_STICK_PORT);
	public final Joystick funcStick = new Joystick(FUNCT_STICK_PORT);
	public final AHRS navx = new AHRS(NAVX_SPI_PORT, NAVX_UPDATE_HZ);
	//public final Encoder leftEncoder = new Encoder();
}
