package org.usfirst.frc.team365.modules;

import org.usfirst.frc.team365.robot.IOPortPage;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Joystick;

public class RobotInputs{
	public final Joystick driveStick = new Joystick(IOPortPage.DRIVE_STICK_PORT);
	public final Joystick funStick = new Joystick(IOPortPage.FUNCT_STICK_PORT);
	public final AHRS navx = new AHRS(IOPortPage.NAVX_SPI_PORT, IOPortPage.NAVX_UPDATE_HZ);
	public final Encoder leftEncoder = new Encoder(IOPortPage.LEFT_ENCODER_CH_A, IOPortPage.LEFT_ENCODER_CH_B);
	public final Encoder rightEncoder = new Encoder(IOPortPage.RIGHT_ENCODER_CH_A, IOPortPage.RIGHT_ENCODER_CH_B);
}
