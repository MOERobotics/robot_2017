package org.usfirst.frc.team365.modules;

import org.usfirst.frc.team365.net.MOETracker;
import org.usfirst.frc.team365.util.RobotModule;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class AutoTargeting{
	/**
	 * 
	 * @param center
	 * @return distance to boiler
	 */
	MOETracker tracker;
	public AutoTargeting(MOETracker tracker){
		this.tracker = tracker;
		
	}
	public double getDistToBoiler(){
		double tracker_y=tracker.getCenter()[1];		//center val - the read tracker val from min dist
		double distance = tracker_y*91.3+2.46+9.5;				//tracker y scaled + min dist; is in inches
		SmartDashboard.putNumber("tracker dist", distance);	//log
		return distance;
	}
	
	public void distanceToAzimuth(){
		double distance = getDistToBoiler();

	}
	
	
	
}
