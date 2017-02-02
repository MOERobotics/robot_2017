package org.usfirst.frc.team365.modules;
import org.usfirst.frc.team365.math.PIDOut;
import org.usfirst.frc.team365.util.RobotModule;

import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;

public class Drivetrain extends RobotModule{	
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
    
    /**
     * This function is run once each time the robot enters autonomous mode
     */
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

    /**
     * This function is called periodically during autonomous
     */
    
    public void autonomousPeriodic() {
    	
    }
   
    /**
     * This function is called once each time the robot enters tele-operated mode
     */
    public void teleopInit(){
    	teleopLoopCounter = 0;
    	outputs.gearShift.set(Value.kForward);
    	
    }

    /**
     * This function is called periodically during operator control
     */
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
        
        if (inputs.driveStick.getTrigger()) {
        	rightMotor = yJoy;
        	leftMotor = yJoy;
        }
        else {
        	rightMotor = limitMotor(yJoy - xJoy);
        	leftMotor = limitMotor(yJoy + xJoy);
        	
        }
        if (inputs.driveStick.getRawButton(8))
        {
        	driveRobot(leftMotor * 0.5, rightMotor * 0.5);
        }
        else 
        {
        driveRobot(leftMotor, rightMotor);
        }
        
       
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
   
    double limitMotor(double motorLimit) {
    	if (motorLimit > 1) return 1;
    	else if (motorLimit < -1) return -1;
    	else return motorLimit;
    }

	@Override
	public void robotPeriodic()
	{
		
	}
}