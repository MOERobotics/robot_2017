package org.usfirst.frc.team365.modules;
import org.usfirst.frc.team365.math.PIDOut;
import org.usfirst.frc.team365.util.RobotModule;

import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;

<<<<<<< HEAD




/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */

public class Drivetrain extends RobotModule  implements PIDOutput {

	AHRS navX;
	
	CANTalon driveLA = new CANTalon(0); // 
	CANTalon driveLB = new CANTalon(1); //
	CANTalon driveLC = new CANTalon(2); //
	//CANTalon driveLD = new CANTalon(15);
	CANTalon driveRA = new CANTalon(13); //
	CANTalon driveRB = new CANTalon(14); //
	CANTalon driveRC = new CANTalon(15);  //
	//CANTalon driveRD = new CANTalon(4);
	
	
	
	Joystick driveStick; //
	Joystick funStick;
	Encoder leftEncoder;
=======
public class Drivetrain extends RobotModule{
>>>>>>> da620aac756436d30c93ddb38c85afc6d63fcfb3
	int autoLoopCounter;
	int autoStep;
	int teleopLoopCounter;
	double direction;
	
	PIDOut driveCorrection;
	PIDController driveStraight;
	
    public Drivetrain(RobotInputs inputs, RobotOutputs outputs){
    	super(inputs, outputs);
    }
	
    public void robotInit(){    	
    	outputs.gearShift.set(Value.kReverse);
    	driveCorrection=new PIDOut();
    	driveStraight = new PIDController(0.04, 0.00005, 0.03, inputs.navx, driveCorrection);
    	driveStraight.setContinuous();
    	driveStraight.setInputRange(-180.0, 180.0);
    	driveStraight.setOutputRange(-1.0, 1.0);
    }
    public void disabledInit () {
    	if (driveStraight.isEnabled()) {
    		driveStraight.disable();
    	}
    }
    public void disabledPeriodic () {
    	if(inputs.driveStick.getTrigger()) {
    		//leftEncoder.reset();
    	}
    }
    public void autonomousInit() {
    	autoLoopCounter = 0;
    	autoStep = 1;
    }
    public void autonomousPeriodic() {
    	
    }

    
    public void teleopInit(){
    	teleopLoopCounter = 0;
    	outputs.gearShift.set(Value.kForward);
    }
    public void teleopPeriodic() {
    	teleopLoopCounter ++;
        double xJoy = inputs.driveStick.getX();
        double yJoy = -inputs.driveStick.getY();
      
        double leftMotor;
        double rightMotor;
        if (inputs.driveStick.getRawButton(6)) {
        	outputs.gearShift.set(Value.kReverse);
        } else {
        	outputs.gearShift.set(Value.kForward);
        }
        
<<<<<<< HEAD
        if (driveStick.getTrigger()) { // 
        	rightMotor = yJoy; // 
        	leftMotor = yJoy; // 
        } // 
        else { // 
        	rightMotor = limitMotor(yJoy - xJoy); // 
        	leftMotor = limitMotor(yJoy + xJoy); // 
        	
        } // 
        if (driveStick.getRawButton(8)) // 
        { // 
        	leftMotor = 0.5 * leftMotor; // 
            rightMotor = 0.5 * rightMotor; // 
        } // 
        driveRobot(leftMotor, rightMotor); // 
        //recommended
        /* 
        if (driveStick.getRawButton(8))
        {
        	leftMotor *= .5;
        	rightMotor *= .5
=======
        if (inputs.driveStick.getTrigger()) {
        	rightMotor = yJoy;
        	leftMotor = yJoy;
        } else {
        	rightMotor = limitMotor(yJoy - xJoy);
        	leftMotor = limitMotor(yJoy + xJoy);
>>>>>>> da620aac756436d30c93ddb38c85afc6d63fcfb3
        }
        if (inputs.driveStick.getRawButton(8)){
        	leftMotor = 0.5 * leftMotor;
            rightMotor = 0.5 * rightMotor;
        }
        driveRobot(leftMotor, rightMotor);
    }
    public void testInit() {
    	
    }
    public void testPeriodic() {
    	LiveWindow.run();
    }
    public void pidDrive() {
    	double output = driveCorrection.getOutput();
    	double right = direction - output;
    	double left = direction + output;
    	driveRobot(left, right);
    }
    
    public void driveRobot(double leftMotor, double rightMotor){
        outputs.driveLF.set(leftMotor);
        outputs.driveLM.set(leftMotor);
        outputs.driveLR.set(leftMotor);
        outputs.driveRF.set(rightMotor);
        outputs.driveRM.set(rightMotor);
        outputs.driveRR.set(rightMotor);
    }
   
    double limitMotor(double motorLimit) { // 
    	if (motorLimit > 1) return 1; // 
    	else if (motorLimit < -1) return -1; // 
    	else return motorLimit; // 
    } // 

	@Override
	public void robotPeriodic()
	{
		
	}
}