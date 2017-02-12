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
	
	public static final Port NAVX_SPI_PORT = SPI.Port.kMXP;
	public static final byte NAVX_UPDATE_HZ = 50;
	
	public static final int DRIVE_L1_PORT = 13;
	public static final int DRIVE_L2_PORT = 14;
	public static final int DRIVE_L3_PORT = 15;
	public static final int DRIVE_R1_PORT = 0;
	public static final int DRIVE_R2_PORT = 1;
	public static final int DRIVE_R3_PORT = 2;
	public static final int SHOOT_SPIN_A = 11;
	public static final int SHOOT_SPIN_B = 12;
	public static final int SHOOT_INDEXER = 5;
	public static final int SHOOT_CONVEYER = 4;
	public static final int SHOOT_AZIMUTH = 10;
	
	public static final int SHIFT_FWD_CH  = 00;
	public static final int SHIFT_BAK_CH  = 01;
}
