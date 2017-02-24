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
	
	double currentYaw;
	double lastOffYaw;
	double turnSum;
	double kDer;
	double straightSum;
	double kProp;
	
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
		autoStep = 1;
	}
	@Override
	public void autonomousPeriodic(int loopCounter)
	{
		distance = inputs.getDriveEncoderRawMax();
		
		switch (autoRoutine)
		{
			case 1:
				turnToAngle(-10);
				auto1(currentYaw);
				
				break;
			case 2:
				if (distance > 1500)
				{
					autoStep = 3;
					inputs.navx.zeroYaw();
				}
				else
				{
					driveRobot(0.5, 0.5);
				}
				break;
			case 3:
				if (distance > 1500)
				{
					autoStep = 4;
					inputs.navx.zeroYaw();
				}
				else
				{
					driveRobot(0.5, 0.5);
				}
				break;
			case 4:
				if (distance > 1500)
				{
					autoStep = 5;
					inputs.navx.zeroYaw();
				}
				else
				{
					driveRobot(0.5, 0.5);
				}
				break;
			case 5:
				if (distance > 1500)
				{
					autoStep = 6;
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

		
		if(inputs.driveStick.getTrigger()){
			rightMotor = yJoy;
			leftMotor = yJoy;
		}else if (inputs.driveStick.getRawButton(2)){
			leftMotor = 0.35;
			rightMotor = -0.35;
		}else if (inputs.driveStick.getRawButton(4)){ 
			leftMotor = -0.35;
			rightMotor = 0.35;
		}else if (inputs.driveStick.getRawButton(3) && distance < 1500){
			leftMotor=0.3;
			rightMotor=0.3;
		}else if(inputs.funStick.getRawButton(7)){
			loopCounter%=48;
			if(loopCounter<=12){
				leftMotor = -.4;
				rightMotor = .4;
			}else if(loopCounter>=36){
				leftMotor=.4;
				rightMotor=-4;
			}else{
				leftMotor=0;
				rightMotor=0;
			}
		}else{
			rightMotor = limitMotor(yJoy - xJoy);
			leftMotor = limitMotor(yJoy + xJoy);
		}
		if (inputs.driveStick.getRawButton(8)){
			leftMotor = 0.5 * leftMotor;
			rightMotor = 0.5 * rightMotor;
		}
		
		
		if(!inputs.isDriveOverrided)
			driveRobot(leftMotor, rightMotor);
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

	public void auto1(double currentYaw) 
	
	{
		if (distance > 5350)
		{
			
			
			while (currentYaw < 10) {					
				driveRobot(-.3, 0.3);
			}
			for (inputs.lightLeft.get(); inputs.lightRight.get() == false;)
			{
				driveRobot(.3, -0.3);
			}
		
			
			
		
		}
		else
		{
			driveRobot(0.5, 0.5);
			distance++;
		}
	}
	
	public void turnToAngle (double setBearing)
	{
		currentYaw = inputs.navx.getYaw();
		double offYaw = setBearing - currentYaw;
		
		if (offYaw * lastOffYaw <= 0)
		{
			turnSum = 0;
		}
		if(offYaw > 0.6 || offYaw < -0.6)
		{
			if (offYaw < 20 && offYaw > -20) 
			{
				if (offYaw > 0) turnSum = turnSum + 0.01;
				else 			turnSum = turnSum - 0.01;
			}
			double newPower = .02 * offYaw + turnSum + kDer * (offYaw - lastOffYaw);
			if (newPower > 0.6) newPower = 0.6;
			else if (newPower < -0.6) newPower = -0.6;
			
			driveRobot(newPower, -newPower);
			lastOffYaw = offYaw;
		}
		else {
			driveRobot(0,0);
			lastOffYaw = offYaw;
		}
	}
	
	void goStraight (double setBearing, double speed) 
	{
	currentYaw = inputs.navx.getYaw();
	double offYaw = setBearing - currentYaw;
	
	if (offYaw > 0.7 || offYaw < -0.7) {
		if (offYaw > 0 && offYaw < 6) straightSum = straightSum + 0.0005;
		else if (offYaw < 0 && offYaw > -6) straightSum = straightSum - 0.0005;
	}
	else offYaw = 0;
	
	double newPower = kProp * offYaw + straightSum;
	double leftSide = speed + newPower;
	double rightSide = speed - newPower;
	driveRobot(leftSide, rightSide);
	}
	
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