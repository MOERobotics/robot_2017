package org.usfirst.frc.team365.modules;
import org.usfirst.frc.team365.robot.SharedVariables;
import org.usfirst.frc.team365.util.RobotModule;
import com.ctre.CANTalon;
import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.CounterBase.EncodingType;
//import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import java.util.Timer;
import java.util.TimerTask;



/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */

public class Drivetrain extends RobotModule  implements PIDOutput 
{

	public Drivetrain(RobotInputs inputs, RobotOutputs outputs) 
	{
		super(inputs, outputs);
		// TODO Auto-generated constructor stub
	}

	AHRS navX;
	
	CANTalon driveLA = new CANTalon(0); // Left 1 
	CANTalon driveLB = new CANTalon(1); // Left 2
	CANTalon driveLC = new CANTalon(2); // Left 3
	//CANTalon driveLD = new CANTalon(15);
	CANTalon driveRA = new CANTalon(13); // Right 1
	CANTalon driveRB = new CANTalon(14); // Right 2
	CANTalon driveRC = new CANTalon(15);  // Right 3
	//CANTalon driveRD = new CANTalon(4);
	
	
	
	Joystick driveStick; // the drive stick
	Joystick funStick; // the function stick
	Encoder leftEncoder = new Encoder(0,1,false,EncodingType.k1X);
	Encoder rightEncoder = new Encoder(2,3,true,EncodingType.k1X);
	int autoLoopCounter; // counts the loops of autonomous
	int autoStep; 
	int teleopLoopCounter; // 
	double direction; //
	
	int autoRoutine;
	
	Timer timer;
	
	DoubleSolenoid gearShift = new DoubleSolenoid(0,1); //
	
	
	PIDController driveStraight;
	
    /**
     * This function is run when the robot is first started up and should be
     * used for any initialization code.
     */
    public void robotInit() 
    {
    	
    	driveLA.setInverted(true); // 
    	driveLB.setInverted(true); // 
    	driveLC.setInverted(true); // 
    	
    	driveLA.enableBrakeMode(true); // 
    	driveLB.enableBrakeMode(true); // 
    	driveLC.enableBrakeMode(true); // 
    	driveRA.enableBrakeMode(true); // 
    	driveRB.enableBrakeMode(true); // 
    	driveRC.enableBrakeMode(true); // 
    	driveStick = new Joystick(0); // 
    	funStick = new Joystick(1);
    	
    	gearShift.set(Value.kReverse); // 
    	
    	SharedVariables.registerKeyspace("DriveTrain"); // 
    	SharedVariables.registerKey("DriveTrain","Turn"); // 
    	
    	driveStraight = new PIDController(0.04, 0.00005, 0.03, navX, this); // 
    	driveStraight.setContinuous(); // 
    	driveStraight.setInputRange(-180.0, 180.0); // 
    	driveStraight.setOutputRange(-1.0, 1.0); // 
    }
    
    /**
     * This function is run once each time the robot enters autonomous mode
     */
    public void disabledInit () {
    	if (driveStraight.isEnabled()) 
    	{ // 
    		driveStraight.disable(); // 
    	} // 
    }
    
    public void disabledPeriodic () 
    {
    	if(driveStick.getTrigger()) 
    	{ // 
    		leftEncoder.reset(); // 
    		rightEncoder.reset();
    		
    	} //
    	if (driveStick.getRawButton(6))
    	{
    		autoRoutine = 1;
    	}
    	else if (driveStick.getRawButton(8))
    	{
    		autoRoutine = 2;
    	}
    	else if (driveStick.getRawButton(10))
    	{
    		autoRoutine = 3;
    	}
    	else if (driveStick.getRawButton(12))
    	{
    		autoRoutine = 4;
    	}
    	else if (driveStick.getRawButton(11))
    	{
    		autoRoutine = 5;
    	}
    	}
   
    
    public void autonomousInit() 
    {
    	autoLoopCounter = 0;
    	autoStep = 1;
    }

    public void autonomousPeriodic() 
    {
    switch(autoRoutine)
    
    {
    case 1: 
    	if (leftEncoder.getRaw() > 1500) {
			autoStep = 2;
			navX.zeroYaw();
		}
		else {
			driveRobot(0.5,0.5);
		}
		break;
	case 2:
		break;
	case 3:
		break;
	case 4:
		break;
	case 5:
		break;
    }
    	
    }

    
   
    /**
     * This function is called once each time the robot enters tele-operated mode
     */
    public void teleopInit()
    {
    	teleopLoopCounter = 0; // 
    	gearShift.set(Value.kForward); // 
    	
    }

    /**
     * This function is called periodically during operator control
     */
    public void teleopPeriodic() {
    	teleopLoopCounter ++; // 
        double xJoy = driveStick.getX(); // 
        double yJoy = -driveStick.getY(); // 
      
        double leftMotor; // 
        double rightMotor; // 
        if (driveStick.getRawButton(6))  // 
        { // 
        	gearShift.set(Value.kReverse); // 
        } // 
        else // 
        { // 
        	gearShift.set(Value.kForward); // 
        } // 
        
        if (driveStick.getTrigger()) 
        { // 
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
        
        if (driveStick.getRawButton(2)) // 
        { // 
        	leftMotor = 0.35; // 
            rightMotor = -0.35; // 
        } // 
        
        else if (driveStick.getRawButton(4)) // 
        { // 
        	leftMotor = -0.35; // 
            rightMotor = 0.35; // 
        } // 
        driveRobot(leftMotor, rightMotor); // 
        //recommended
        /* 
        if (driveStick.getRawButton(8))
        {
        	leftMotor *= .5;
        	rightMotor *= .5
        }
         */
       
    }
    
    
    /**
     * This function is called periodically during test mode
     */
    
    public void testInit() {
    
    }
    
    public void testPeriodic() 
    {
    	LiveWindow.run();
    }
    //ball advance 5
    //collector 10
    public void pidWrite(double output) 
    { // 
    	double right = direction - output; // 
    	double left = direction + output; // 
    	driveRobot(left, right); // 
    } // 
    
    public void driveRobot(double leftMotor, double rightMotor)
    	{
        driveLA.set(leftMotor);
        driveLB.set(leftMotor);
        driveLC.set(leftMotor);
       // driveLD.set(leftMotor);
        driveRA.set(rightMotor);
        driveRB.set(rightMotor);
        driveRC.set(rightMotor);
        //driveRD.set(rightMotor); 
    	}
   
    double limitMotor(double motorLimit) 	
    { // 
    	if (motorLimit > 1) return 1; // 
    	else if (motorLimit < -1) return -1; // 
    	else return motorLimit; // 
    } // 

	@Override
	public void robotPeriodic()
	{
		// TODO Auto-generated method stub
		
	}
}