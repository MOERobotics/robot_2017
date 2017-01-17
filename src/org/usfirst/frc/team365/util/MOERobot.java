package org.usfirst.frc.team365.util;

import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.hal.FRCNetComm.tInstances;
import edu.wpi.first.wpilibj.hal.FRCNetComm.tResourceType;
import edu.wpi.first.wpilibj.hal.HAL;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;

public abstract class MOERobot extends RobotBase {
	private byte initialisationFlags=0;
	private final static byte disabled	 = 0b0001;
	private final static byte autonomous = 0b0010;
	private final static byte teleop	 = 0b0100;
	private final static byte test		 = 0b1000;
	public void startCompetition() {
		HAL.report(tResourceType.kResourceType_Framework, tInstances.kFramework_Iterative);
		robotInit();
		// Tell the DS that the robot is ready to be enabled
		HAL.observeUserProgramStarting();
		// loop forever, calling the appropriate mode-dependent function
		LiveWindow.setEnabled(false);
		m_ds.waitForData();
		do competitionLoop();
		while (true);
	}
	public void competitionLoop(){
		if (isDisabled()) {
			if ((initialisationFlags&disabled)==0)
				initialise(false, this::disabledInit, disabled);
			periodic(HAL::observeUserProgramDisabled, this::disabledPeriodic, 0);
		} else if (isTest()) {
			if ((initialisationFlags&test)==0)
				initialise(true, this::testInit, test);
			periodic(HAL::observeUserProgramTest, this::testPeriodic, 0);
		} else if (isAutonomous()) {
			if ((initialisationFlags&autonomous)==0)
				initialise(false, this::autonomousInit, autonomous);
			periodic(HAL::observeUserProgramAutonomous, this::autonomousPeriodic, 0.2);
		} else {
			if ((initialisationFlags&teleop)==0)
				initialise(false, this::teleopInit, teleop);
			periodic(HAL::observeUserProgramTeleop, this::teleopPeriodic, 0);
		}
		robotPeriodic();
	}
	public void initialise(boolean liveWindowStatus, Action action, byte mask){
		LiveWindow.setEnabled(liveWindowStatus);
		action.execute();
		initialisationFlags=0;
		initialisationFlags|=mask;
	}
	public void periodic(Action observe, Action action, double timeout){
		observe.execute();
		action.execute();
		m_ds.waitForData(timeout);
	}
	abstract void robotInit();
	abstract void teleopInit();
	abstract void testInit();
	abstract void autonomousInit();
	abstract void disabledInit();
	abstract void robotPeriodic();
	abstract void teleopPeriodic();
	abstract void testPeriodic();
	abstract void autonomousPeriodic();
	abstract void disabledPeriodic();
}