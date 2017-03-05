package org.usfirst.frc.team365.modules;

import org.usfirst.frc.team365.math.PIDOut;
import org.usfirst.frc.team365.net.MOETracker;
import org.usfirst.frc.team365.util.RobotModule;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Drivetrain extends RobotModule
{
	
	AnalogInput sonar = new AnalogInput(0);
	
	Value HI_GEAR = Value.kReverse;
	final Value LO_GEAR = Value.kForward;

	double direction;
	int teleopLoop;
	int autoLoop;
	int autoStep;
	int autoRoutine;
	int disabledLoop;
	int loopCount;
	int gearStep;
	double straightSum = 0;
	double turnSum = 0;
	double speedSum = 0;
	double currentDist;
	double currentYaw;
	double lastOffYaw;
	double delta;
	double setYaw;
//	double setSpeed;
	double loSpeed;
	double hiSpeed;
	double setShootPower;
	
	boolean lastButtonD12;
	boolean lastButtonD11;
	boolean lastButtonD10;
	boolean lastButtonD9;
	boolean lastButtonD8;
	boolean lastButtonD7;
	boolean lastButtonD3;
	boolean lastButtonF10;
	boolean lastButtonF11;
	boolean shiftPosition;
	boolean shooterOn;
	boolean upToSpeed;
	boolean leftOn;
	boolean rightOn;
	boolean redSide;
	
	double kProp = 0.05;
	double kInt = .0001;
	double kDer = 0;
	double autoKP;
	double autoKI;
	double autoKD;
	
	// conversion 107 pulses per inch

	double targetCoeff;
	
	PIDOut driveCorrection;
	PIDController driveStraight;
	MOETracker tracker;
	
	boolean lastDriveB8;

	public Drivetrain(RobotInputs inputs, RobotOutputs outputs, MOETracker tracker){
		super(inputs, outputs);
		this.tracker = tracker;
	}
	@Override
	public void robotInit()
	{
		outputs.setGearShift(LO_GEAR);
		driveCorrection = new PIDOut();
		driveStraight = new PIDController(0.04, 0.00005, 0.03, inputs.navx, driveCorrection); //
		driveStraight.setContinuous(); //
		driveStraight.setInputRange(-180.0, 180.0); //
		driveStraight.setOutputRange(-1.0, 1.0); //
	}
	@Override
	public void robotPeriodic(int loopCounter){
		targetCoeff = SmartDashboard.getNumber("auto target coefficient", 1);
	}
	@Override
	public void disabledInit()
	{
		if (driveStraight.isEnabled())
		{ //
			driveStraight.disable(); //
		} //
		driveRobot(0,0);
		outputs.setShooter(0);
		outputs.setIndexer(0);
		outputs.setCollector(0);
		outputs.setFeeder(0);
	}
	@Override
	public void disabledPeriodic(int loopCounter)
	{
		disabledLoop++;
		if (inputs.driveStick.getRawButton(2)) {  //  zero yaw
			inputs.navx.zeroYaw();
		}
		if (inputs.driveStick.getRawButton(4)) {   //  reset encoders
			inputs.leftEncoder.reset();
			inputs.rightEncoder.reset();
		}
		
		if (inputs.driveStick.getRawButton(6)) autoRoutine = 1;
		else if (inputs.driveStick.getRawButton(8)) autoRoutine = 2;
		else if (inputs.driveStick.getRawButton(10)) autoRoutine = 3;
		else if (inputs.driveStick.getRawButton(12)) autoRoutine = 4;
		else if (inputs.driveStick.getRawButton(11)) autoRoutine = 5;
		else if (inputs.driveStick.getRawButton(9)) autoRoutine = 6;
		
		if (inputs.funStick.getRawButton(8)) redSide = true;
		else if (inputs.funStick.getRawButton(9)) redSide = false;
		
		if (disabledLoop % 25 == 0) {
			SmartDashboard.putBoolean("leftLight", inputs.lightLeft.get());
			SmartDashboard.putBoolean("rightLight", inputs.lightRight.get());
			SmartDashboard.putNumber("shootPower", (inputs.driveStick.getRawAxis(2) + 1.0)/2.0);
			SmartDashboard.putNumber("leftEncoder", inputs.leftEncoder.getRaw());
			SmartDashboard.putNumber("rightEncoder", inputs.rightEncoder.getRaw());
			SmartDashboard.putNumber("autoRoutine", autoRoutine);
			SmartDashboard.putBoolean("redSide", redSide);
			SmartDashboard.putNumber("sonar", sonar.getAverageVoltage());

			if (!inputs.navx.isCalibrating()) {
				SmartDashboard.putNumber("yaw", inputs.navx.getYaw());
				SmartDashboard.putNumber("pitch", inputs.navx.getPitch());
				SmartDashboard.putNumber("roll", inputs.navx.getRoll());
			}
			SmartDashboard.putBoolean("navxConnection", inputs.navx.isConnected());
			
//			double wheelPeriod = shootSpeed.getPeriod();
//			SmartDashboard.putNumber("shooterSpeed", 60/(12*wheelPeriod));
			SmartDashboard.putNumber("shooterSpeed", inputs.shooterSpeed.getRate());
			double hood = outputs.getAzimuthPosition();
			SmartDashboard.putNumber("azimuth", hood);
		}

		autoKP = SmartDashboard.getNumber("DB/Slider 0", 0);
		autoKI = SmartDashboard.getNumber("DB/Slider 1", 0);
		autoKD = SmartDashboard.getNumber("DB/Slider 2", 0);
		
	
	}
	@Override
	public void autonomousInit()
	{
		autoLoop = 0;
		autoStep = 1;
		outputs.setGearShift(LO_GEAR);
		turnSum = 0;
		straightSum = 0;
		speedSum = 0;
		setShootPower = 0.81;
//		setShootPower = (driveStick.getRawAxis(2) + 1.0)/2.0;
	}
	@Override
	public void autonomousPeriodic(int loopCounter, int autoRoutine){
		currentDist = inputs.getDriveEncoderRawMax();
		currentYaw = inputs.navx.getYaw();
		switch (autoRoutine){
		case 1:
			autoCenterGear();	
			break;
		case 2:
			if (redSide) {
				autoHopperShootRed();
			}
			else autoHopperShootBlue();

			break;
		case 3:

			break;
		case 4:

			break;
		case 5:

			break;
		}
	}
	@Override

	public void teleopInit()
	{
		outputs.setGearShift(LO_GEAR);
		lastDriveB8=false;
		teleopLoop = 0;
	
		shiftPosition = true;
		lastButtonD12 = false;
		lastButtonD11 = false;
		lastButtonD10 = false;
		lastButtonD3 = false;
		lastButtonD7 = false;
		lastButtonD8 = false;
		lastButtonD9 = false;
		lastButtonF10 = false;
		lastButtonF11 = false;
		shooterOn = false;
		upToSpeed = false;
		loopCount = 0;
		leftOn = false;
		rightOn= false;
		straightSum = 0;
		turnSum = 0;
		speedSum = 0;
	}
	@Override
	public void teleopPeriodic(int loopCounter)
	{
		teleopLoop++;
	
		boolean driveButton3 = inputs.driveStick.getRawButton(3);
		boolean driveButton7 = inputs.driveStick.getRawButton(7);
		boolean driveButton12 = inputs.driveStick.getRawButton(12);
	
		double xJoy = inputs.driveStick.getX();
		double yJoy = -inputs.driveStick.getY();
		xJoy = Math.abs(xJoy)>0.1?xJoy:0;
		yJoy = Math.abs(yJoy)>0.1?yJoy:0;
		
		boolean changeGear = inputs.driveStick.getRawButton(8);
		outputs.setGearShift((changeGear && !lastDriveB8)?HI_GEAR:LO_GEAR);
		lastDriveB8=changeGear;
		
		double leftMotor;
		double rightMotor;
		if(inputs.driveStick.getTrigger()){   //drive straight with y joystick value only
			rightMotor = yJoy;
			leftMotor = yJoy;
		}else if (inputs.driveStick.getRawButton(2)){  //turn slowly left until sees right target
			if (inputs.lightRight.get() || rightOn) {
				driveRobot(0,0);
				rightOn = true;
			}
			else {
			driveRobot(-0.35,0.35);
			}	
		}else if (inputs.driveStick.getRawButton(4)){   //turn slowly right until sees left target
			if (inputs.lightLeft.get() || leftOn) {
				driveRobot(0,0);
				leftOn = true;
			}
			else {
				driveRobot(0.35,-0.35);
			}
		}else if (driveButton3) {   //  back up robot from boiler
			if (!lastButtonD3) {
				inputs.leftEncoder.reset();
				inputs.rightEncoder.reset();
			}
			if (inputs.leftEncoder.getRaw() > 2400 || inputs.rightEncoder.getRaw() > 2400) {
				driveRobot(0,0);
			}
			else driveRobot(0.35,0.35);
		
		}
	else if (inputs.driveStick.getRawButton(11))    {  // test turn to angle
			
			turnToAngle(0);
		}
		else if (driveButton12) {  // stay firm at set angle
			if (!lastButtonD12) {
				setYaw = currentYaw;
			}
			turnToAngle(setYaw);
		}
		else if (driveButton7) {
			if (!lastButtonD7) {
				inputs.leftEncoder.reset();
				inputs.rightEncoder.reset();
				straightSum = 0;
				setYaw = currentYaw;
				autoStep = 1;
			}
			reorientBot();
		}
		/*else if(inputs.funStick.getRawButton(7)){
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
		}
		*/else{
			rightMotor = limitMotor(yJoy - xJoy);
			leftMotor = limitMotor(yJoy + xJoy);
		
			driveRobot(leftMotor, rightMotor);
	}
		
		if (teleopLoop % 25 == 0) {   //print values to dashboard
			SmartDashboard.putBoolean("leftLight", inputs.lightLeft.get());
			SmartDashboard.putBoolean("rightLight", inputs.lightRight.get());
			SmartDashboard.putNumber("shootPower", (inputs.driveStick.getRawAxis(2) + 1.0)/2.0);
			SmartDashboard.putNumber("leftEncoder", inputs.leftEncoder.getRaw());
			SmartDashboard.putNumber("rightEncoder", inputs.rightEncoder.getRaw());
			SmartDashboard.putNumber("sonar", sonar.getAverageVoltage());
			
			double wheelPeriod = inputs.shooterSpeed.getPeriod();
			SmartDashboard.putNumber("shooterSpeed", 60/(12*wheelPeriod));
			SmartDashboard.putNumber("loSpeed", loSpeed);
			SmartDashboard.putNumber("hiSpeed", hiSpeed);
			SmartDashboard.putNumber("setShootPower", setShootPower);
			SmartDashboard.putBoolean("upToSpeed", upToSpeed);
			double hood = outputs.getAzimuthPosition();
			SmartDashboard.putNumber("azimuth", hood);
			
	//		SmartDashboard.putNumber("climberAmps", climber.getOutputCurrent());

			if (!inputs.navx.isCalibrating()) {
				SmartDashboard.putNumber("yaw", inputs.navx.getYaw());
				SmartDashboard.putNumber("pitch", inputs.navx.getPitch());
				SmartDashboard.putNumber("roll", inputs.navx.getRoll());
			}			
		}
		
		lastButtonD3 = driveButton3;
		lastButtonD7 = driveButton7;
		lastButtonD12 = driveButton12;
	}
	boolean targeted = false;
	@Override
	public void testInit()
	{
	}
	@Override
	public void testPeriodic(int loopCounter){
		autoTargetTest();
	}
	
	final double center_x=0.53;
	final double pixelsToAngle=.3;
	double targetBearing, targetBearing2;
	public void autoTargetTest(){
		double[]p = tracker.getCenter();
		double dx = center_x - p[0];
		double dTheta_x=dx * MOETracker.pixelsWide * pixelsToAngle;
		SmartDashboard.putNumber("dthetax", dTheta_x);
		if(!targeted){
			targetBearing = inputs.navx.getYaw()-dTheta_x;
			targetBearing2 = inputs.navx.getYaw()+(p[0]*92.829-52.786);
			targeted=true;
		}
		if(inputs.driveStick.getTrigger()){
			turnToAngle(targetBearing-3);
		}
		if(inputs.driveStick.getRawButton(12)){
			turnToAngle(targetBearing2-3);
		}
	}
	public void autoTarget(){
		
	}
	public void driveTest(){
		if(inputs.driveStick.getRawButton(5)){ // LA
			outputs.setDriveLA(1);
		}else if(inputs.driveStick.getRawButton(6)){
			outputs.setDriveLA(-1);
		}else{
			outputs.setDriveLA(0);
		}
		if(inputs.driveStick.getRawButton(7)){ // LB
			outputs.setDriveLB(1);
		}else if(inputs.driveStick.getRawButton(8)){
			outputs.setDriveLB(-1);
		}else{
			outputs.setDriveLB(0);
		}
		if(inputs.driveStick.getRawButton(9)){ // LC
			outputs.setDriveLC(1);
		}else if (inputs.driveStick.getRawButton(10)){
			outputs.setDriveLC(-1);
		}else{
			outputs.setDriveLC(0);
		}
		if(inputs.driveStick.getRawButton(11)){ // RA
			outputs.setDriveRA(1);
		}else if(inputs.driveStick.getRawButton(12)){
			outputs.setDriveRA(-1);
		}else{
			outputs.setDriveRA(0);
		}
		if(inputs.funStick.getRawButton(6)){ // RB
			outputs.setDriveRB(1);
		}else if(inputs.funStick.getRawButton(7)){
			outputs.setDriveRB(-1);
		}else{
			outputs.setDriveRB(0);
		}
		if(inputs.funStick.getRawButton(11)){ // RC
			outputs.setDriveRC(1);
		}else if(inputs.funStick.getRawButton(10)){
			outputs.setDriveRC(-1);
		}else{
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

	double limitMotor(double power){
		if (power > 1) return 1;
		else if (power < -1) return -1;
		else return power;
	}


	

	
	void goStraight (double setBearing, double speed) {
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
	
	void turnToAngle(double setBearing) {
//		currentYaw = navx.getYaw();
		double offYaw = setBearing - currentYaw;

		if (offYaw * lastOffYaw <= 0) {
			turnSum = 0;
		}
		if (offYaw > 0.6 || offYaw < -0.6) {
			if (offYaw < 20 && offYaw > -20) {
				if (offYaw > 0) turnSum = turnSum + 0.01;
				else turnSum = turnSum - 0.01;
			}
			double newPower = .03 * offYaw + turnSum + kDer * (offYaw - lastOffYaw) ;
			if (newPower > 0.7) newPower = 0.7;
			else if (newPower < -0.7) newPower = -0.7;

			driveRobot(newPower, - newPower);
			lastOffYaw = offYaw;
		}

		else {
			driveRobot(0,0);
			lastOffYaw = offYaw;
		}
	
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
	void reorientBot() {
		switch(autoStep) {
		case 1: if (inputs.leftEncoder.getRaw() < -1100 || inputs.rightEncoder.getRaw() < -1100) {
			autoStep = 2;
			driveRobot(0,0);

		}
		else goStraight(setYaw + 3., -0.4);
		break;
		case 2:  if (inputs.leftEncoder.getRaw()> 500 || inputs.rightEncoder.getRaw() > 500) {
			driveRobot(0,0);
			autoStep = 3;
		}
		else goStraight(setYaw,0.3);
		break;
		case 3:
			driveRobot(0,0);
		}
	}
	void autoCenterGear() {  //  place center gear and then go shoot
		switch(autoStep) {
		case 1:
			if (currentDist > 5200) {
				autoStep = 2;
				autoLoop = 0;
				}
			else goStraight(0,0.45);
				break;
		case 2:
			if (currentDist > 7900 || autoLoop > 100) { 
				if (/*sonar.getAverageVoltage() > 0.15 ||*/ autoLoop > 100) {
				
					autoStep = 8;
					setYaw = currentYaw;
					SmartDashboard.putNumber("autoDist", currentDist);
//					SmartDashboard.putNumber("autoSonar", sonar.getAverageVoltage());
					inputs.leftEncoder.reset();
					inputs.rightEncoder.reset();
				}
				else {
				autoStep = 3;
				driveRobot(0,0);				
				SmartDashboard.putNumber("autoDist", currentDist);
//				SmartDashboard.putNumber("autoSonar", sonar.getAverageVoltage());
				autoLoop = 0;
				}
			}
			else goStraight(0,0.3);
			break;
		case 3:
			driveRobot(0,0);
			outputs.setGearReleaser(Value.kForward);
			if (autoLoop > 25) {
				autoStep = 4;
				inputs.leftEncoder.reset();
				inputs.rightEncoder.reset();
			}
			break;
		case 4:
			if (currentDist < -2100) {
				autoStep = 5;
				driveRobot(0,0);
			}
			else driveRobot(-0.4,-0.4);
			break;
		case 5:
			if (redSide) {
			if (currentYaw < -64) {
				autoStep = 6;
				inputs.leftEncoder.reset();
				inputs.rightEncoder.reset();
			}
			else driveRobot(-0.4,0.4);
			}
			else {
				if (currentYaw > 64) {
					autoStep = 6;
					inputs.leftEncoder.reset();
					inputs.rightEncoder.reset();
				}
				else driveRobot(0.4,-0.4);
			}
			outputs.setGearReleaser(Value.kReverse);
			break;
		case 6:
			if (currentDist < -10400) {
				autoStep = 7;
				driveRobot(0,0);
			}
			else {
				if (redSide) goStraight(-67,-0.5);
				else goStraight(67,-0.5);
			}
			break;
		case 7: 
			driveRobot(0,0);
			break;
		case 8:
			if (currentDist < -1100) {
				autoStep = 9;
				driveRobot(0,0);
			}
			else goStraight(setYaw + 3, -0.4);
			break;
		case 9:
			if (currentDist > 700) {
				autoStep = 3;
				driveRobot(0,0);
			}
			else goStraight(setYaw, 0.3);
			break;
			
		}
			
		
	}
	void autoHopperShootBlue() {    //   Blue Side
		switch(autoStep) {
		case 1:
			if (currentDist > 7800) {    //first try 8100 but maybe wrong distance
				driveRobot(0,0);
				lastOffYaw = currentYaw;
				outputs.setBallFlap(true);
			}
			else goStraight(0,0.8);
			break;
		case 2:
			if (currentYaw < -89. && currentYaw > -91.) {
				autoStep = 3;
				inputs.leftEncoder.reset();
				inputs.rightEncoder.reset();
				autoLoop = 0;
			}
			else turnToAngle(-90);
			break;
		case 3:
			if (currentDist > 4400 || autoLoop > 100) {
				autoStep = 4;
				straightSum = 0;
				autoLoop = 0;
				SmartDashboard.putNumber("autoLoop", autoLoop);
				SmartDashboard.putNumber("autoDist", currentDist);
			}
			else goStraight(-90,0.7);
			break;
		case 4:
			if (autoLoop > 100) {
				autoStep = 5;
				inputs.leftEncoder.reset();
				inputs.rightEncoder.reset();
				straightSum = 0;
			}
			else driveRobot(0,0);
			break;
		case 5:
			if (currentDist < -1600) {
				autoStep = 6;
				turnSum = 0;
			}
			else goStraight(-90, -0.4);
			break;
		
			
		case 6:
			outputs.setShooter(setShootPower);
			if (currentYaw > 15) {
				autoStep = 7;
				inputs.leftEncoder.reset();
				inputs.rightEncoder.reset();
				straightSum = 0;
			}
			else turnToAngle(18);
			break;
		case 7:
			if (currentDist < -4200) {
				autoStep = 8;
				driveRobot(0,0);
				autoLoop = 0;
			}
			else {
				goStraight(18,-0.4);
				outputs.setShooter(setShootPower);
			}
			break;
		case 8:		
			outputs.setCollector(1.0);
			outputs.setFeeder(1.0);
			if (autoLoop > 10) {
				autoStep = 9;
			}
			turnToAngle(18);
			upToSpeed = true;
			//double newPower = setShooterSpeed(1950);
			//shooterWheelA.set(newPower);
			//shooterWheelB.set(newPower);
			outputs.setShooter(setShootPower);
			break;
		case 9:
			outputs.setIndexer(1.0);
			turnToAngle(18);		
		}
	}
	
	void autoHopperShootRed() {
		switch(autoStep) {
		case 1:
			if (currentDist > 7800) {    //first try 8100 but maybe wrong distance
				autoStep = 2;
				driveRobot(0,0);
				lastOffYaw = currentYaw;
				outputs.setBallFlap(true);
			}
			else goStraight(0,0.8);
			break;
		case 2:
			if (currentYaw > 89. && currentYaw < 91.) {
				autoStep = 3;
				inputs.leftEncoder.reset();
				inputs.rightEncoder.reset();
				autoLoop = 0;
			}
			else turnToAngle(90);
			break;
		case 3:
			if (currentDist > 4400 || autoLoop > 100) {
				autoStep = 4;
				straightSum = 0;
				autoLoop = 0;
				SmartDashboard.putNumber("autoLoop", autoLoop);
				SmartDashboard.putNumber("autoDist", currentDist);
			}
			else goStraight(90,0.7);
			break;
		case 4:
			if (autoLoop > 100) {
				autoStep = 5;
				inputs.leftEncoder.reset();
				inputs.rightEncoder.reset();
				straightSum = 0;
			}
			else driveRobot(0,0);
			break;
		case 5:
			if (currentDist < -1600) {
				autoStep = 6;
				turnSum = 0;
			}
			else goStraight(90, -0.4);
			break;
		
			
		case 6:
			if (currentYaw < -15) {
				autoStep = 7;
				inputs.leftEncoder.reset();
				inputs.rightEncoder.reset();
				outputs.setShooter(setShootPower);
				straightSum = 0;
			}
			else turnToAngle(-18);
			break;
		case 7:
			if (currentDist < -4200) {
				autoStep = 8;
				driveRobot(0,0);
				autoLoop = 0;
			}
			else {
				goStraight(-18,-0.4);
				outputs.setShooter(setShootPower);
			}
			break;
		case 8:	
			upToSpeed = true;
			/*
			double newPower = setShooterSpeed(1950);
			shooterWheelA.set(newPower);
			shooterWheelB.set(newPower);
			*/outputs.setShooter(setShootPower);
			outputs.setCollector(1.0);
			outputs.setFeeder(1.0);
			if (autoLoop > 10) {
				autoStep = 9;
			}
			turnToAngle(-18);
			break;
		case 9:
			outputs.setIndexer(1.0);
			turnToAngle(-18);		
		}
	}
}