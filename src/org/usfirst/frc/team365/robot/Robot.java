
package org.usfirst.frc.team365.robot;

import java.util.ArrayList;
import java.util.List;

import org.usfirst.frc.team365.modules.AutoTargeting;
import org.usfirst.frc.team365.modules.Climber;
import org.usfirst.frc.team365.modules.Drivetrain;
import org.usfirst.frc.team365.modules.GearMechanism;
import org.usfirst.frc.team365.modules.RobotInputs;
import org.usfirst.frc.team365.modules.RobotOutputs;
import org.usfirst.frc.team365.modules.Shooter;
import org.usfirst.frc.team365.net.MOETracker;
import org.usfirst.frc.team365.net.UDPTracker;
import org.usfirst.frc.team365.util.RobotModule;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Robot extends IterativeRobot {
	List<RobotModule>modules;
	RobotInputs inputs;
	RobotOutputs outputs;
	
	volatile MOETracker tracker;
	Thread trackingThread;
	
	int robotLoopCounter;
	int disabledLoopCounter;
	int autoLoopCounter; 
	int teleopLoopCounter;
	int testLoopCounter;
	int autoRoutine;
	
	Drivetrain d;
	
	boolean isRedSide;
	public Robot(){
		//tracker = new GripTracker("GRIP");
		tracker = UDPTracker.getTracker("Tracker7", 5801);
		trackingThread = new Thread(tracker);
		trackingThread.start();
		
		inputs = new RobotInputs(tracker);
		outputs = new RobotOutputs();
		
		d=new Drivetrain(inputs, outputs);

		
		modules=new ArrayList<>();
		modules.add(new Climber(inputs, outputs));
		modules.add(d);
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
		if(inputs.driveStick.getTrigger()){
			inputs.leftEncoder.reset();
			inputs.rightEncoder.reset();
		}
		/*
		if(inputs.driveStick.getRawButton(6))
			autoRoutine = 1;
		else if(inputs.driveStick.getRawButton(8))
			autoRoutine = 2;
		else if (inputs.driveStick.getRawButton(10))
			autoRoutine = 3;
		else if (inputs.driveStick.getRawButton(12))
			autoRoutine = 4;
		else if (inputs.driveStick.getRawButton(11))
			autoRoutine = 5;
		else if (inputs.driveStick.getRawButton(9))
			autoRoutine = 6;
		*/
		modules.forEach((x)->x.disabledPeriodic(disabledLoopCounter));
		smartDashboardLog(disabledLoopCounter);
	}
	@Override
	public void autonomousInit() {
		autoLoopCounter = 0;
		modules.forEach((x)->x.autonomousInit());
	}
	@Override
	public void autonomousPeriodic() {
		autoLoopCounter++;
		modules.forEach((x)->x.autonomousPeriodic(autoLoopCounter, autoRoutine));
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
		smartDashboardLog(teleopLoopCounter);
	}
	@Override
	public void testInit() {
		testLoopCounter = 0;
		modules.forEach((x)->x.testInit());
	}
	@Override
	public void testPeriodic() {
		testLoopCounter++;
		modules.forEach((x)->x.testPeriodic(testLoopCounter));
	}
	void targetTest(){
		double[] p = tracker.getCenter();
		SmartDashboard.putNumber("target x", p[0]*MOETracker.pixelsWide);
		SmartDashboard.putNumber("target y", p[1]*MOETracker.pixelsHigh);
		System.out.println(p[0]+", "+p[1]);
	}
	public void smartDashboardLog(int loopCounter){
		if(loopCounter%25==0){
			SmartDashboard.putBoolean("left light", inputs.lightLeft.get());
			SmartDashboard.putBoolean("right light", inputs.lightRight.get());
			SmartDashboard.putNumber("shoot power", (inputs.driveStick.getRawAxis(2)+1)/2);
			SmartDashboard.putNumber("left encoder", inputs.leftEncoder.getDistance());
			SmartDashboard.putNumber("right encoder", inputs.rightEncoder.getDistance());
			SmartDashboard.putNumber("sonar", inputs.sonar.getAverageVoltage());
			SmartDashboard.putNumber("auto routine", d.autoRoutine);
			SmartDashboard.putBoolean("red side", d.redSide);
			SmartDashboard.putNumber("azimuth", outputs.getAzimuthPosition());
			SmartDashboard.putNumber("climber amps", outputs.getAvgClimberAmps());
			SmartDashboard.putBoolean("is navX connected", inputs.navx.isConnected());
			SmartDashboard.putNumber("shooter speed", 60/(12*inputs.shooterSpeed.getPeriod()));
			if(!inputs.navx.isCalibrating()){
				SmartDashboard.putNumber("yaw", inputs.navx.getYaw());
				SmartDashboard.putNumber("pitch", inputs.navx.getPitch());
				SmartDashboard.putNumber("roll", inputs.navx.getRoll());
			}
		}
	}
}