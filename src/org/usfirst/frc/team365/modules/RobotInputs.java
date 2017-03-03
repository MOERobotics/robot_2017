package org.usfirst.frc.team365.modules;

import org.usfirst.frc.team365.robot.IOPort;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.Counter;
import edu.wpi.first.wpilibj.CounterBase.EncodingType;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Joystick;

public class RobotInputs{
	public final Joystick driveStick = new Joystick(
		IOPort.DRIVE_STICK_PORT
	);
	public final Joystick funStick = new Joystick(
		IOPort.FUNCT_STICK_PORT
	);
	public final AHRS navx = new AHRS(
		IOPort.NAVX_SPI_PORT, 
		IOPort.NAVX_UPDATE_HZ
	);
	public final Encoder leftEncoder = new Encoder(
		IOPort.LEFT_ENCODER_CH_A, 
		IOPort.LEFT_ENCODER_CH_B,
		false,
		EncodingType.k1X
	);
	public final Encoder rightEncoder = new Encoder(
		IOPort.RIGHT_ENCODER_CH_A, 
		IOPort.RIGHT_ENCODER_CH_B,
		true,
		EncodingType.k1X
	);
	public final Counter shooterSpeed = new Counter(
		IOPort.COUNTER_SHOOT_SPEED
	);
	public final DigitalInput lightRight = new DigitalInput(
		IOPort.RIGHT_LIGHT
	);
	public final DigitalInput lightLeft = new DigitalInput(
		IOPort.LEFT_LIGHT
	);
	public boolean isDriveOverrided = false;
	
	public int getDriveEncoderRawMax(){
		return Math.max(Math.abs(leftEncoder.getRaw()), Math.abs(rightEncoder.getRaw()));
	}
}
