package org.usfirst.frc.team365.modules;

import org.usfirst.frc.team365.math.PIDOut;
import org.usfirst.frc.team365.util.RobotModule;

import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.PIDController;

public class Drivetrain extends RobotModule
{
	final Value HI_GEAR = Value.kReverse;
	final Value LO_GEAR = Value.kForward;

	int autoStep;
	double direction; //
	int autoRoutine;
	double distance;
	
	PIDOut driveCorrection;
	PIDController driveStraight;

	public Drivetrain(RobotInputs inputs, RobotOutputs outputs)
	{
		super(inputs, outputs);
	}
	@Override
	public void robotInit()
	{
		outputs.setGearShift(HI_GEAR);
		driveCorrection = new PIDOut();
		driveStraight = new PIDController(0.04, 0.00005, 0.03, inputs.navx, driveCorrection); //
		driveStraight.setContinuous(); //
		driveStraight.setInputRange(-180.0, 180.0); //
		driveStraight.setOutputRange(-1.0, 1.0); //
	}
	@Override
	public void robotPeriodic(int loopCounter)
	{

	}
	@Override
	public void disabledInit()
	{
		if (driveStraight.isEnabled())
		{ //
			driveStraight.disable(); //
		} //
	}
	@Override
	public void disabledPeriodic(int loopCounter)
	{
		if (inputs.driveStick.getTrigger())
		{ //
			inputs.leftEncoder.reset(); //
			inputs.rightEncoder.reset();

		} //
		if (inputs.driveStick.getRawButton(6))
		{
			autoRoutine = 1;
		}
		else if (inputs.driveStick.getRawButton(8))
		{
			autoRoutine = 2;
		}
		else if (inputs.driveStick.getRawButton(10))
		{
			autoRoutine = 3;
		}
		else if (inputs.driveStick.getRawButton(12))
		{
			autoRoutine = 4;
		}
		else if (inputs.driveStick.getRawButton(11))
		{
			autoRoutine = 5;
		}
	}
	@Override
	public void autonomousInit()
	{
		//autoStep = 1;
		
	}
	@Override
	public void autonomousPeriodic(int loopCounter)
	{
		distance = inputs.getDriveEncoderRawMax();
		
		switch (autoRoutine)
		{
			case 1:
				if (distance > 1500)
				{
					autoStep = 2;
					inputs.navx.zeroYaw();
				}
				else
				{
					driveRobot(0.5, 0.5);
				}
				break;
			case 2:
				if (distance > 1501)
				{
					autoStep = 2;
					inputs.navx.zeroYaw();
				}
				else
				{
					driveRobot(0.5, 0.5);
				}
				break;
			case 3:
				if (distance > 1502)
				{
					autoStep = 2;
					inputs.navx.zeroYaw();
				}
				else
				{
					driveRobot(0.5, 0.5);
				}
				break;
			case 4:
				if (distance > 1503)
				{
					autoStep = 2;
					inputs.navx.zeroYaw();
				}
				else
				{
					driveRobot(0.5, 0.5);
				}
				break;
			case 5:
				if (distance > 1504)
				{
					autoStep = 2;
					inputs.navx.zeroYaw();
				}
				else
				{
					driveRobot(0.5, 0.5);
				}
				break;
		}
	}
	@Override

	public void teleopInit()
	{
		outputs.setGearShift(LO_GEAR); //
	}
	@Override
	public void teleopPeriodic(int loopCounter)
	{
		double xJoy = inputs.driveStick.getX();
		double yJoy = -inputs.driveStick.getY();

		double leftMotor;
		double rightMotor;
		if (inputs.driveStick.getRawButton(8)){
			outputs.setGearShift(HI_GEAR);
		}else{
			outputs.setGearShift(LO_GEAR);
		}

		if (inputs.driveStick.getTrigger()){
			rightMotor = yJoy;
			leftMotor = yJoy;
		}else{
			rightMotor = limitMotor(yJoy - xJoy);
			leftMotor = limitMotor(yJoy + xJoy);
		}
		/* is no longeer needed per specs
		if (inputs.driveStick.getRawButton(8)){ //
			leftMotor = 0.5 * leftMotor; //
			rightMotor = 0.5 * rightMotor; //
		} //
		*/
		if (inputs.driveStick.getRawButton(2)) //
		{ //
			leftMotor = 0.35; //
			rightMotor = -0.35; //
		} else if (inputs.driveStick.getRawButton(4)) { //
			leftMotor = -0.35; //
			rightMotor = 0.35; //
		} //
		
		if(!inputs.isDriveOverrided)
			driveRobot(leftMotor, rightMotor); //
	}
	@Override
	public void testInit()
	{
	}
	@Override
	public void testPeriodic(int loopCounter)
	{
		if (inputs.driveStick.getRawButton(5))
		{ // LA
			outputs.setDriveLA(1);
		}
		else if (inputs.driveStick.getRawButton(6))
		{
			outputs.setDriveLA(-1);
		}
		else
		{
			outputs.setDriveLA(0);
		}

		if (inputs.driveStick.getRawButton(7))
		{ // LB
			outputs.setDriveLB(1);
		}
		else if (inputs.driveStick.getRawButton(8))
		{
			outputs.setDriveLB(-1);
		}
		else
		{
			outputs.setDriveLB(0);
		}

		if (inputs.driveStick.getRawButton(9))
		{ // LC
			outputs.setDriveLC(1);
		}
		else if (inputs.driveStick.getRawButton(10))
		{
			outputs.setDriveLC(-1);
		}
		else
		{
			outputs.setDriveLC(0);
		}

		if (inputs.driveStick.getRawButton(11))
		{ // RA
			outputs.setDriveRA(1);
		}
		else if (inputs.driveStick.getRawButton(12))
		{
			outputs.setDriveRA(-1);
		}
		else
		{
			outputs.setDriveRA(0);
		}

		if (inputs.funStick.getRawButton(6))
		{ // RB
			outputs.setDriveRB(1);
		}
		else if (inputs.funStick.getRawButton(7))
		{
			outputs.setDriveRB(-1);
		}
		else
		{
			outputs.setDriveRB(0);
		}

		if (inputs.funStick.getRawButton(11))
		{ // RC
			outputs.setDriveRC(1);
		}
		else if (inputs.funStick.getRawButton(10))
		{
			outputs.setDriveRC(-1);
		}
		else
		{
			outputs.setDriveRC(0);
		}
	}

	public void pidDrive()
	{
		double output = driveCorrection.getOutput();
		double right = direction - output;
		double left = direction + output;
		driveRobot(left, right);
	}

	double limitMotor(double motorLimit)
	{ //
		if (motorLimit > 1) return 1; //
		else if (motorLimit < -1) return -1; //
		else return motorLimit; //
	} //

	public static void driveRobot(double leftMotor, double rightMotor)
	{
		outputs.setDriveLA(leftMotor);
		outputs.setDriveLB(leftMotor);
		outputs.setDriveLC(leftMotor);
		outputs.setDriveRA(rightMotor);
		outputs.setDriveRB(rightMotor);
		outputs.setDriveRC(rightMotor);
	}
}