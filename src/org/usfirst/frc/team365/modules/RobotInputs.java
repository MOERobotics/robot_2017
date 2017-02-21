package org.usfirst.frc.team365.modules;

import org.usfirst.frc.team365.robot.IOPortPage;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.Counter;
import edu.wpi.first.wpilibj.CounterBase.EncodingType;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Joystick;

public class RobotInputs{
	public final Joystick driveStick = new Joystick(
		IOPortPage.DRIVE_STICK_PORT
	);
	public final Joystick funStick = new Joystick(
		IOPortPage.FUNCT_STICK_PORT
	);
	public final AHRS navx = new AHRS(
		IOPortPage.NAVX_SPI_PORT, 
		IOPortPage.NAVX_UPDATE_HZ
	);
	public final Encoder leftEncoder = new Encoder(
		IOPortPage.LEFT_ENCODER_CH_A, 
		IOPortPage.LEFT_ENCODER_CH_B,
		false,
		EncodingType.k1X
	);
	public final Encoder rightEncoder = new Encoder(
		IOPortPage.RIGHT_ENCODER_CH_A, 
		IOPortPage.RIGHT_ENCODER_CH_B,
		true,
		EncodingType.k1X
	);
	public final Counter shooterSpeed = new Counter(
		IOPortPage.COUNTER_SHOOT_SPEED
	);
	public final DigitalInput lightRight = new DigitalInput(
		IOPortPage.RIGHT_LIGHT_CH
	);
	public final DigitalInput lightLeft = new DigitalInput(
		IOPortPage.LEFT_LIGHT_CH
	);
	public boolean isDriveOverrided = false;
	
	public int getDriveEncoderRawMax(){
		return Math.max(Math.abs(leftEncoder.getRaw()), Math.abs(rightEncoder.getRaw()));
	}
}
