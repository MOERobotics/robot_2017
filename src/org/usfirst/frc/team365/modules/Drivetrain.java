package org.usfirst.frc.team365.modules;
import org.usfirst.frc.team365.math.PIDOut;
import org.usfirst.frc.team365.util.RobotModule;
import com.ctre.CANTalon;
import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.CounterBase.EncodingType;
//import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import java.util.Timer;
import java.util.TimerTask;

public class Drivetrain extends RobotModule
{
	final Value HI_GEAR = Value.kReverse;
	final Value LO_GEAR = Value.kForward;
	
	int autoStep; 
	double direction; //
	int autoRoutine;
	
	PIDOut driveCorrection;
	PIDController driveStraight;
	
	public Drivetrain(RobotInputs inputs, RobotOutputs outputs) 
	{
		super(inputs, outputs);
	}

    public void robotInit() 
    {
    	outputs.setGearShift(Value.kReverse);
    	driveCorrection = new PIDOut();
    	driveStraight = new PIDController(0.04, 0.00005, 0.03, inputs.navx, driveCorrection); // 
    	driveStraight.setContinuous(); // 
    	driveStraight.setInputRange(-180.0, 180.0); // 
    	driveStraight.setOutputRange(-1.0, 1.0); // 
    }
	@Override
	public void robotPeriodic(int loopCounter){
		
	}
	@Override
    public void disabledInit () {
    	if (driveStraight.isEnabled()) 
    	{ // 
    		driveStraight.disable(); // 
    	} // 
    }
    public void disabledPeriodic (int loopCounter) 
    {
    	if(inputs.driveStick.getTrigger()) 
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
   
    
    public void autonomousInit() 
    {
    	autoStep = 1;
    }

    public void autonomousPeriodic(int loopCounter) 
    {
	    switch(autoRoutine) {
		    case 1: 
		    	if (inputs.leftEncoder.getRaw() > 1500) {
					autoStep = 2;
					inputs.navx.zeroYaw();
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

    
   
    public void teleopInit()
    {
    	int teleopLoopCounter = 0; // 
    	outputs.setGearShift(Value.kForward); // 
    }
	@Override
    public void teleopPeriodic(int loopCounter) {
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
        if (inputs.driveStick.getTrigger()) 
        { // 
        	rightMotor = yJoy; // 
        	leftMotor = yJoy; // 
        } // 
        else { // 
        	rightMotor = limitMotor(yJoy - xJoy); // 
        	leftMotor = limitMotor(yJoy + xJoy); // 
        	
        } // 
        if (inputs.driveStick.getRawButton(8)) // 
        { // 
        	leftMotor = 0.5 * leftMotor; // 
            rightMotor = 0.5 * rightMotor; // 
        } // 
        
        if (inputs.driveStick.getRawButton(2)) // 
        { // 
        	leftMotor = 0.35; // 
            rightMotor = -0.35; // 
        } // 
        
        else if (inputs.driveStick.getRawButton(4)) // 
        { // 
        	leftMotor = -0.35; // 
            rightMotor = 0.35; // 
        } // 
        driveRobot(leftMotor, rightMotor); // 
	}
	@Override
    public void testInit(){
    	
    }
	@Override
    public void testPeriodic(int loopCounter){    	
    	if (inputs.driveStick.getRawButton(5)) { //LA
    		outputs.setDriveLA(1);
    	}
    	else if (inputs.driveStick.getRawButton(6)) {
    		outputs.setDriveLA(-1);
    	}
    	else {
    		outputs.setDriveLA(0);
    	}
    	
    	if (inputs.driveStick.getRawButton(7)) { //LB
    		outputs.setDriveLB(1);
    	}
    	else if (inputs.driveStick.getRawButton(8)) {
    		outputs.setDriveLB(-1);
    	}
    	else {
    		outputs.setDriveLB(0);
    	}
    	
    	if (inputs.driveStick.getRawButton(9)) { //LC
    		outputs.setDriveLC(1);
    	}
    	else if (inputs.driveStick.getRawButton(10)) {
    		outputs.setDriveLC(-1);
    	}
    	else {
    		outputs.setDriveLC(0);
    	}
    	
    	if (inputs.driveStick.getRawButton(11)) { //RA
    		outputs.setDriveRA(1);
    	}
    	else if (inputs.driveStick.getRawButton(12)) {
    		outputs.setDriveRA(-1);
    	}
    	else {
    		outputs.setDriveRA(0);
    	}
    	
    	if (inputs.funStick.getRawButton(6)) { //RB
    		outputs.setDriveRB(1);
    	}
    	else if (inputs.funStick.getRawButton(7)) {
    		outputs.setDriveRB(-1);
    	}
    	else {
    		outputs.setDriveRB(0);
    	}
    	
    	if (inputs.funStick.getRawButton(11)) { //RC
    		outputs.setDriveRC(1);
    	}
    	else if (inputs.funStick.getRawButton(10)) {
    		outputs.setDriveRC(-1);
    	}
    	else {
    		outputs.setDriveRC(0);
    	}
    }
	
    public void pidDrive(){
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
    
    public static void driveRobot(double leftMotor, double rightMotor){
    	outputs.setDriveLA(leftMotor);
    	outputs.setDriveLB(leftMotor);
    	outputs.setDriveLC(leftMotor);
    	outputs.setDriveRA(rightMotor);
    	outputs.setDriveRB(rightMotor);
    	outputs.setDriveRC(rightMotor);
    }
}