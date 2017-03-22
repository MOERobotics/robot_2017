package org.usfirst.frc.team365.robot;

import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.SPI.Port;

public final class IOPort
{
	public static final int DRIVE_STICK_PORT = 0;
	public static final int FUNCT_STICK_PORT = 1;
	
	public static final int LEFT_ENCODER_CH_A = 0;
	public static final int LEFT_ENCODER_CH_B = 1;
	public static final int RIGHT_ENCODER_CH_A = 2;
	public static final int RIGHT_ENCODER_CH_B = 3;
	public static final int RIGHT_LIGHT = 9;
	public static final int LEFT_LIGHT = 8;
	public static final int COUNTER_SHOOT_SPEED=7;
	
	public static final Port NAVX_SPI_PORT = SPI.Port.kMXP;
	public static final byte NAVX_UPDATE_HZ = 50;
	
	public static final int DRIVE_L1 = 0;
	public static final int DRIVE_L2 = 1;
	public static final int DRIVE_L3 = 2;
	public static final int DRIVE_R1 = 13;
	public static final int DRIVE_R2 = 14;
	public static final int DRIVE_R3 = 15;
	public static final int CLIMBER_A = 3;
	public static final int CLIMBER_B = 12;
	public static final int FEEDER = 4;
	public static final int INDEXER = 5;
	public static final int COLLECTOR = 9;
	public static final int AZIMUTH = 10;
	public static final int SHOOT_SPIN_A = 11;
	public static final int SHOOT_SPIN_B = 6;
	
	public static final int SHIFT_FWD = 5;
	public static final int SHIFT_BAK = 7;
	public static final int BALL_FLAP = 0;
	public static final int GEAR_RELEASE_FWD = 4;
	public static final int GEAR_RELEASE_BAK = 6;
}
