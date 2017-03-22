package org.usfirst.frc.team365.net;

public interface MOETracker extends Runnable {
	//virtual screen dimensions
	public static final double pixelsWide = 320, pixelsHigh = 240;
	/**
	 * @return the center of main target with coordinates as screen percentages ranged [0,1]
	 */
	public double[] getCenter();
}
