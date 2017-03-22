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
	static public double getDistToBoiler(MOETracker tracker){
		double tracker_y=tracker.getCenter()[1];		//center val - the read tracker val from min dist
		double distance = tracker_y*91.3+2.46+9.5;				//tracker y scaled + min dist; is in inches
		SmartDashboard.putNumber("tracker dist", distance);	//log
		return distance;
	}
	
	static public void distanceToAzimuth(MOETracker tracker){
		double distance = getDistToBoiler(tracker);
		//enter regression here

	}
	
	public static double getAngleToBoiler(MOETracker tracker){
		double[]p = tracker.getCenter();
		double deltaTheta;
		if(p[0]!=-1 && p[1]!=-1){
				SmartDashboard.putNumber("tracker x", p[0]);
				SmartDashboard.putNumber("tracker y", p[1]);
//				SmartDashboard.putNumber("target yaw", dTheta_x);
				deltaTheta = ((p[0]-0.5)*-69.344+0.64);
				return deltaTheta;
			}
		return 0;
		}
	
	
	
	
}
	
	
	

