package org.usfirst.frc.team365.robot;

import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.SPI.Port;

public final class IOPortPage
{
	public static final int DRIVE_STICK_PORT = 0;
	public static final int FUNCT_STICK_PORT = 1;
	
	public static final int LEFT_ENCODER_CH_A = 0;
	public static final int LEFT_ENCODER_CH_B = 1;
	public static final int RIGHT_ENCODER_CH_A = 2;
	public static final int RIGHT_ENCODER_CH_B = 3;
	
	public static final int COUNTER_SHOOT_SPEED=8;
	
	public static final Port NAVX_SPI_PORT = SPI.Port.kMXP;
	public static final byte NAVX_UPDATE_HZ = 50;
	
	public static final int DRIVE_L1 = 13;
	public static final int DRIVE_L2 = 14;
	public static final int DRIVE_L3 = 15;
	public static final int DRIVE_R1 = 0;
	public static final int DRIVE_R2 = 1;
	public static final int DRIVE_R3 = 2;
	public static final int CLIMBER = 3;
	public static final int FEEDER = 4;
	public static final int INDEXER = 5;
	public static final int COLLECTOR = 9;
	public static final int AZIMUTH = 10;
	public static final int SHOOT_SPIN_A = 11;
	public static final int SHOOT_SPIN_B = 12;
	
	public static final int SHIFT_FWD_CH  = 00;
	public static final int SHIFT_BAK_CH  = 01;
}
