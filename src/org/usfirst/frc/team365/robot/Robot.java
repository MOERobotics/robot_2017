
package org.usfirst.frc.team365.robot;

import java.util.ArrayList;
import java.util.List;

import org.usfirst.frc.team365.modules.Climber;
import org.usfirst.frc.team365.modules.Drivetrain;
import org.usfirst.frc.team365.modules.GearMechanism;
import org.usfirst.frc.team365.modules.Shooter;
import org.usfirst.frc.team365.util.RobotModule;

import edu.wpi.first.wpilibj.IterativeRobot;

public class Robot extends IterativeRobot {
	List<RobotModule>modules;
	
	
	
	public Robot(){
		GlobalShared.init();
		modules = new ArrayList<RobotModule>();
		modules.add(new Climber());
		modules.add(new Drivetrain());
		modules.add(new GearMechanism());
		modules.add(new Shooter());
	}

	@Override
	public void robotInit() {
		modules.forEach((x)->x.robotInit());
	}
	@Override
	public void robotPeriodic() {
		modules.forEach((x)->x.robotPeriodic());
	}
	@Override
	public void disabledInit() {
		modules.forEach((x)->x.disabledInit());
	}
	@Override
	public void disabledPeriodic() {
		modules.forEach((x)->x.disabledPeriodic());
	}
	@Override
	public void autonomousInit() {
		modules.forEach((x)->x.autonomousInit());
	}
	@Override
	public void autonomousPeriodic() {
		modules.forEach((x)->x.autonomousPeriodic());
	}
	@Override
	public void teleopInit() {
		modules.forEach((x)->x.teleopInit());
	}
	@Override
	public void teleopPeriodic() {
		modules.forEach((x)->x.teleopPeriodic());
	}
	@Override
	public void testInit() {
		modules.forEach((x)->x.testInit());
	}
	@Override
	public void testPeriodic() {
		modules.forEach((x)->x.testPeriodic());
	}
}
