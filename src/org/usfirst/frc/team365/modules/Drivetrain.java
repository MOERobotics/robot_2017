package org.usfirst.frc.team365.modules;
import org.usfirst.frc.team365.math.PIDOut;
import org.usfirst.frc.team365.util.RobotModule;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.PIDController;
//import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */

public class Drivetrain extends RobotModule{

	public Drivetrain(RobotInputs inputs, RobotOutputs outputs) {
		super(inputs, outputs);
		// TODO Auto-generated constructor stub
	}

	AHRS navX;
	
	Encoder leftEncoder;
	int autoLoopCounter; // counts the loops of autonomous
	int autoStep; 
	int teleopLoopCounter; // 
	double direction; //
	
	DoubleSolenoid gearShift = new DoubleSolenoid(0,1); //
	
	PIDOut driveCorrection;
	
	PIDController driveStraight;
	
	@Override
    public void robotInit(){    	
    	outputs.setGearShift(Value.kReverse);
    	driveCorrection = new PIDOut();
    	driveStraight = new PIDController(0.04, 0.00005, 0.03, inputs.navx, driveCorrection);
    	driveStraight.setContinuous();
    	driveStraight.setInputRange(-180.0, 180.0);
    	driveStraight.setOutputRange(-1.0, 1.0);
    }
	@Override
	public void robotPeriodic(int loopCounter){
		
	}
	@Override
    public void disabledInit () {
    	if (driveStraight.isEnabled()) { // 
    		driveStraight.disable(); // 
    	} // 
    }
	@Override
    public void disabledPeriodic (int loopCounter) {
    	if(inputs.driveStick.getTrigger()) {
    		//leftEncoder.reset();
    	}
    }
	@Override
    public void autonomousInit() {
    	autoLoopCounter = 0;
    	autoStep = 1;
    }
	@Override
    public void autonomousPeriodic(int loopCounter) {
    	
    }
	@Override
    public void teleopInit(){
    	teleopLoopCounter = 0;
    	outputs.setGearShift(Value.kForward);
    }
	@Override
    public void teleopPeriodic(int loopCounter) {
    	teleopLoopCounter ++;
        double xJoy = inputs.driveStick.getX();
        double yJoy = -inputs.driveStick.getY();
      
        double leftMotor;
        double rightMotor;
        if (inputs.driveStick.getRawButton(6)) {
        	outputs.setGearShift(Value.kReverse);
        } else {
        	outputs.setGearShift(Value.kForward);
        }
        
        if (inputs.driveStick.getTrigger()) {
        	rightMotor = yJoy;
        	leftMotor = yJoy;
        } else {
        	rightMotor = limitMotor(yJoy - xJoy);
        	leftMotor = limitMotor(yJoy + xJoy);
        }
        
        if (inputs.driveStick.getRawButton(8)) {
        	leftMotor = 0.5 * leftMotor;
            rightMotor = 0.5 * rightMotor;
        }
        driveRobot(leftMotor, rightMotor);
    }
	@Override
    public void testInit(){
    	
    }
	@Override
    public void testPeriodic(int loopCounter){
    	LiveWindow.run();
    }
	
    public void pidDrive(){
    	double output = driveCorrection.getOutput();
    	double right = direction - output;
    	double left = direction + output;
    	driveRobot(left, right);
    }
    
    public static void driveRobot(double leftMotor, double rightMotor){
    	outputs.setDriveL1(leftMotor);
    	outputs.setDriveL2(leftMotor);
    	outputs.setDriveL3(leftMotor);
    	outputs.setDriveR1(rightMotor);
    	outputs.setDriveR2(rightMotor);
    	outputs.setDriveR3(rightMotor);
    }
    double limitMotor(double motorLimit) {
    	if (motorLimit > 1) return 1;
    	else if (motorLimit < -1) return -1;
    	else return motorLimit;
    }
}