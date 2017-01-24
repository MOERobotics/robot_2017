package org.usfirst.frc.team365.modules;
import com.kauailabs.navx.frc.AHRS;


import com.ctre.CANTalon;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;



/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Drivetrain extends IterativeRobot{// implements PIDOutput {
	AHRS navX;
	
	CANTalon driveLA = new CANTalon(12);
	CANTalon driveLB = new CANTalon(13);
	CANTalon driveLC = new CANTalon(14);
	//CANTalon driveLD = new CANTalon(15);
	CANTalon driveRA = new CANTalon(1);
	CANTalon driveRB = new CANTalon(2);
	CANTalon driveRC = new CANTalon(3); 
	//CANTalon driveRD = new CANTalon(4);
	
	Joystick driveStick;
	Joystick funStick;
	Encoder leftEncoder;
	int autoLoopCounter;
	int autoStep;
	int teleopLoopCounter;
	
    /**
     * This function is run when the robot is first started up and should be
     * used for any initialization code.
     */
    public void robotInit() {
    	
    	driveRA.setInverted(true);
    	driveRB.setInverted(true);
    	driveRC.setInverted(true);
    	
    	driveLA.enableBrakeMode(true);
    	driveLB.enableBrakeMode(true);
    	driveLC.enableBrakeMode(true);
    	driveRA.enableBrakeMode(true);
    	driveRB.enableBrakeMode(true);
    	driveRC.enableBrakeMode(true);
    	driveStick = new Joystick(0);
    	funStick = new Joystick(1);
    }
    
    /**
     * This function is run once each time the robot enters autonomous mode
     */
    public void disabledInit () {
    	
    }
    
    public void disabledPeriodic () {
    	if(driveStick.getTrigger()) {
    		leftEncoder.reset();
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
    	
    }

    /**
     * This function is called periodically during operator control
     */
    public void teleopPeriodic() {
    	teleopLoopCounter ++;
        double xJoy = driveStick.getX();
        double yJoy = -driveStick.getY();
      
        double leftMotor = limitMotor(yJoy + xJoy);
        double rightMotor;
        if (funStick.getRawButton(2)) {
        	rightMotor = limitMotor(yJoy);
        }
        else {
        	rightMotor = limitMotor(yJoy - xJoy);
        }
        
       
        driveLA.set(leftMotor);
        driveLB.set(leftMotor);
        driveLC.set(leftMotor);
       // driveLD.set(leftMotor);
        driveRA.set(rightMotor);
        driveRB.set(rightMotor);
        driveRC.set(rightMotor);
        //driveRD.set(rightMotor); 
    	}
    
    /**
     * This function is called periodically during test mode
     */
    
    
    public void testInit() {
    	
    }
    
    public void testPeriodic() {
    	LiveWindow.run();
    }
   
    double limitMotor(double motorLimit) {
    	if (motorLimit > 1) return 1;
    	else if (motorLimit < -1) return -1;
    	else return motorLimit;
    }
}