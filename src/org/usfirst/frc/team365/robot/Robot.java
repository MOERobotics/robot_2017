
package org.usfirst.frc.team365.robot;

import java.util.ArrayList;
import java.util.List;

import org.usfirst.frc.team365.modules.Climber;
import org.usfirst.frc.team365.modules.Drivetrain;
import org.usfirst.frc.team365.modules.GearMechanism;
import org.usfirst.frc.team365.modules.RobotInputs;
import org.usfirst.frc.team365.modules.RobotOutputs;
import org.usfirst.frc.team365.modules.Shooter;
import org.usfirst.frc.team365.util.RobotModule;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Robot extends IterativeRobot {
	List<RobotModule>modules;
	RobotInputs inputs;
	RobotOutputs outputs;
	int robotLoopCounter;
	int disabledLoopCounter;
	int autoLoopCounter; 
	int teleopLoopCounter;
	int testLoopCounter;
	public Robot(){
		inputs = new RobotInputs();
		outputs = new RobotOutputs();
		
		modules=new ArrayList<>();
		modules.add(new Climber(inputs, outputs));
		modules.add(new Drivetrain(inputs, outputs));
		modules.add(new GearMechanism(inputs, outputs));
		modules.add(new Shooter(inputs, outputs));
	}

	@Override
	public void robotInit() {
		robotLoopCounter = 0;
		modules.forEach((x)->x.robotInit());
	}
	@Override
	public void robotPeriodic() {
		robotLoopCounter++;
		SmartDashboard.putBoolean("left light", inputs.ligtLeft.get());
		SmartDashboard.putBoolean("right light", inputs.lightRight.get());
		SmartDashboard.putNumber("right encoder", inputs.rightEncoder.getDistance());
		SmartDashboard.putNumber("left encoder", inputs.leftEncoder.getDistance());
		SmartDashboard.putNumber("shoot power", (inputs.driveStick.getRawAxis(2)+1)/2);
		SmartDashboard.putNumber("azimuth", outputs.getAzimuthPosition());
		SmartDashboard.putNumber("climberAmps", outputs.getClimberAmps());
		SmartDashboard.putNumber("yaw", inputs.navx.getYaw());
		SmartDashboard.putNumber("pitch", inputs.navx.getPitch());
		SmartDashboard.putNumber("roll", inputs.navx.getRoll());
		modules.forEach((x)->x.robotPeriodic(robotLoopCounter));
		
	}
	@Override
	public void disabledInit() {
		disabledLoopCounter = 0;
		modules.forEach((x)->x.disabledInit());
	}
	@Override
	public void disabledPeriodic() {
		disabledLoopCounter++;
		modules.forEach((x)->x.disabledPeriodic(disabledLoopCounter));
	}
	@Override
	public void autonomousInit() {
		autoLoopCounter = 0;
		modules.forEach((x)->x.autonomousInit());
	}
	@Override
	public void autonomousPeriodic() {
		autoLoopCounter++;
		modules.forEach((x)->x.autonomousPeriodic(autoLoopCounter));
	}
	@Override
	public void teleopInit() {
		teleopLoopCounter = 0;
		modules.forEach((x)->x.teleopInit());
	}
	@Override
	public void teleopPeriodic() {
		teleopLoopCounter++;
		modules.forEach((x)->x.teleopPeriodic(teleopLoopCounter));
	}
	@Override
	public void testInit() {
		testLoopCounter = 0;
		modules.forEach((x)->x.testInit());
	}
	@Override
	public void testPeriodic() {
		LiveWindow.run();
		testLoopCounter++;
		modules.forEach((x)->x.testPeriodic(testLoopCounter));
	}
}
