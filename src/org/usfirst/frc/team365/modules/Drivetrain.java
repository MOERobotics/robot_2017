package org.usfirst.frc.team365.modules;

import org.usfirst.frc.team365.math.PIDOut;
import org.usfirst.frc.team365.net.MOETracker;
import org.usfirst.frc.team365.util.RobotModule;

import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Drivetrain extends RobotModule
{
	Value HI_GEAR = Value.kReverse;
	final Value LO_GEAR = Value.kForward;

	double direction;
	int autoLoop;
	int autoStep;
	public int autoRoutine;
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
	public boolean redSide;
	
	double kProp = 0.05;
	double kInt = .0001;
	double kDer = 0;
	double autoKP;
	double autoKI;
	double autoKD;
	
	// conversion 107 pulses per inch
	
	PIDOut driveCorrection;
	PIDController driveStraight;
	MOETracker tracker;
	
	boolean lastDriveB8;

	public Drivetrain(RobotInputs inputs, RobotOutputs outputs, MOETracker tracker){
		super(inputs, outputs);
		this.tracker = tracker;
	}
	@Override
	public void robotInit(){
		outputs.setGearShift(LO_GEAR);
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
	public void disabledInit(){
		if (driveStraight.isEnabled())
			driveStraight.disable();
		driveRobot(0,0);
		autoLoop = 0;
		autoStep = 1;
		outputs.setGearShift(LO_GEAR);
		turnSum = 0;
		straightSum = 0;
		speedSum = 0;
	}
	@Override
	public void disabledPeriodic(int loopCounter){
		if (inputs.driveStick.getRawButton(2))//  zero yaw
			inputs.navx.zeroYaw();
		if (inputs.driveStick.getRawButton(4))//  reset encoders
			inputs.resetEncoders();
		
		if (inputs.driveStick.getRawButton(6)) autoRoutine = 1;
		else if (inputs.driveStick.getRawButton(8)) autoRoutine = 2;
		else if (inputs.driveStick.getRawButton(10)) autoRoutine = 3;
		else if (inputs.driveStick.getRawButton(12)) autoRoutine = 4;
		else if (inputs.driveStick.getRawButton(11)) autoRoutine = 5;
		else if (inputs.driveStick.getRawButton(9)) autoRoutine = 6;
		
		if (inputs.funStick.getRawButton(8)) redSide = true;
		else if (inputs.funStick.getRawButton(9)) redSide = false;

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
		
	//	setShootPower = 0.81;
//		setShootPower = (driveStick.getRawAxis(2) + 1.0)/2.0;
	}
	@Override
	public void autonomousPeriodic(int loopCounter, int notnow){
		currentDist = inputs.getDriveEncoderRawMax();
		currentYaw = inputs.navx.getYaw();
		autoLoop++;
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
			if (redSide) {
				autoShootFirstRed();
			}
			else autoShootFirstBlue();
			break;
		case 4:
			autoOuterGear();
			break;
		case 5:
			if (redSide) {
				autoBoilerGearRed();
			}
			else autoBoilerGearBlue();
			break;
		}
	}
	@Override

	public void teleopInit()
	{
		outputs.setGearShift(LO_GEAR);
		lastDriveB8=false;
	
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
		leftOn = false;
		rightOn= false;
		straightSum = 0;
		turnSum = 0;
		speedSum = 0;
		autoLoop = 0;
		autoStep = 1;
		outputs.setGearShift(LO_GEAR);
		turnSum = 0;
		straightSum = 0;
		speedSum = 0;
		
		targeted=false;
	}
	boolean wasLoGear=true;
	@Override
	public void teleopPeriodic(int loopCounter){
		boolean driveButton3 = inputs.driveStick.getRawButton(3);
		boolean driveButton7 = inputs.driveStick.getRawButton(7);
		boolean driveButton10 = inputs.driveStick.getRawButton(10);
		
		currentDist=inputs.getDriveEncoderRawMax();
		currentYaw=inputs.navx.getYaw();
	
		double xJoy = inputs.driveStick.getX();
		double yJoy = -inputs.driveStick.getY();
		xJoy = Math.abs(xJoy)>0.1?xJoy:0;
		yJoy = Math.abs(yJoy)>0.1?yJoy:0;
		
		autoTargetCheckAngle();	
		targeted=false;
		
		boolean changeGear = inputs.driveStick.getRawButton(8);
		if(changeGear && !lastDriveB8){
			if(wasLoGear){
				outputs.setGearShift(HI_GEAR);
				wasLoGear=false;
			}else{
				outputs.setGearShift(LO_GEAR);
				wasLoGear=true;
			}
		}lastDriveB8=changeGear;
		
		double leftMotor;
		double rightMotor;
		if(inputs.driveStick.getTrigger()){   //drive straight with y joystick value only
			rightMotor = yJoy;
			leftMotor = yJoy;
			leftOn=false;
			rightOn=false;
			driveRobot(leftMotor,rightMotor);
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
				inputs.resetEncoders();
			}
			if (currentDist>2400) {
				driveRobot(0,0);
			}
			else driveRobot(0.35,0.35);
		
		}
		else if (inputs.driveStick.getRawButton(11))    {  //do auto turning
			turnToAngle(targetBearing2);	targeted=true;		
		}
		else if (driveButton10) {  // stay firm at set angle
			if (!lastButtonD10) {
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
			leftOn=false;
			rightOn=false;
			driveRobot(leftMotor, rightMotor);
	}
		
		lastButtonD3 = driveButton3;
		lastButtonD7 = driveButton7;
		lastButtonD10 = driveButton10;
	}
	boolean targeted = false;
	@Override
	public void testInit()
	{
		targeted=false;
	}
	@Override
	public void testPeriodic(int loopCounter){
		autoTargetTest();
	}
	
	final double center_x=0.53;
	final double pixelsToAngle=.3;
	double targetBearing, targetBearing2;
	public void autoTargetCheckAngle(){
		outputs.setSignalLight(true);
		double[]p = tracker.getCenter();
		double dx = center_x - p[0];
		if(p[0]!=-1 && p[1]!=-1){
			if(!targeted){
				double dTheta_x=dx * MOETracker.pixelsWide * pixelsToAngle;
				SmartDashboard.putNumber("tracker x", p[0]);
				SmartDashboard.putNumber("tracker y", p[1]);
				SmartDashboard.putNumber("target yaw", dTheta_x);
				targetBearing2 = inputs.navx.getYaw()-((p[0]-0.5)*-69.344+0.64);
			}
		}
	}
	public void autoTargetMove(){
		turnToAngle(targetBearing2);
	}
	
	
	public void autoTargetTest(){
		outputs.setSignalLight(true);
		double[]p = tracker.getCenter();
		double dx = center_x - p[0];
		if(p[0]!=-1 && p[1]!=-1){
			if(!targeted){
				double dTheta_x=dx * MOETracker.pixelsWide * pixelsToAngle;
				SmartDashboard.putNumber("tracker x", p[0]);
				SmartDashboard.putNumber("tracker y", p[1]);
				SmartDashboard.putNumber("target yaw", dTheta_x);
				targetBearing2 = inputs.navx.getYaw()-((p[0]-0.5)*-69.344+0.64);
			}
			if(inputs.driveStick.getTrigger()){
				turnToAngle(targetBearing-2.5);
				targeted=true;
			}else if(inputs.driveStick.getRawButton(12)){
				turnToAngle(targetBearing2);
				targeted=true;
			}else{
				turnToAngle(inputs.navx.getYaw());
			}
		}else{
			return;
		}
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
		currentYaw = inputs.navx.getYaw();
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
		case 1: if (currentDist < -1100) {
			autoStep = 2;
			driveRobot(0,0);

		}
		else goStraight(setYaw + 3., -0.4);
		break;
		case 2:  if (currentDist > 500) {
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
			if (currentDist > 7800 || autoLoop > 100) { 
				if (/*inputs.sonar.getAverageVoltage() > 0.15 ||*/ autoLoop > 100) {
				
					autoStep = 8;
					setYaw = currentYaw;
					SmartDashboard.putNumber("autoDist", currentDist);
//					SmartDashboard.putNumber("autoSonar", inputs.sonar.getAverageVoltage());
					inputs.leftEncoder.reset();
					inputs.rightEncoder.reset();
				}
				else {
				autoStep = 3;
				driveRobot(0,0);				
				SmartDashboard.putNumber("autoDist", currentDist);
//				SmartDashboard.putNumber("autoSonar", inputs.sonar.getAverageVoltage());
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
			if (currentDist < -2200) {
				autoStep = 5;
				driveRobot(0,0);
			}
			else driveRobot(-0.4,-0.4);
			break;
		case 5:
			if (redSide) {
			if (currentYaw < -63) {
				autoStep = 6;
				inputs.leftEncoder.reset();
				inputs.rightEncoder.reset();
			}
			else driveRobot(-0.5,0.5);
			}
			else {
				if (currentYaw > 63) {
					autoStep = 6;
					inputs.leftEncoder.reset();
					inputs.rightEncoder.reset();
				}
				else driveRobot(0.5,-0.5);
			}
			outputs.setGearReleaser(Value.kReverse);
			break;
		case 6:
			if (currentDist < -11200) {
				autoStep = 10;
				setYaw=currentYaw;
				driveRobot(0,0);
			}
			else {
				if (redSide) goStraight(-67,-0.7);
				else goStraight(67,-0.7);
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
			if (currentDist > 500) {
				autoStep = 3;
				driveRobot(0,0);
			}
			else goStraight(setYaw, 0.3);
			break;
		case 10:
			outputs.setIndexer(1);
			turnToAngle(setYaw);
		}
			
		
	}
	void autoHopperShootBlue() {    //   Blue Side
		switch(autoStep) {
		case 1:
			if (currentDist > 7600) {    //first try 8100 but maybe wrong distance
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
			if (currentDist < -1800) {
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
			if (currentDist < -3100) {
				autoStep = 8;
				driveRobot(0,0);
				autoLoop = 0;
			}
			else {
				goStraight(18,-0.4);
				outputs.setShooter(setShootPower);
				outputs.setFeeder(1);
				outputs.setCollector(1);
				
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
			if (currentDist > 7600) {    //first try 8100 but maybe wrong distance
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
			if (currentDist < -1800) {
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
				outputs.setFeeder(1);
				outputs.setCollector(1);
				straightSum = 0;
			}
			else turnToAngle(-18);
			break;
		case 7:
			if (currentDist < -3100) {
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
	void autoOuterGear() {     
		switch(autoStep) {
		case 1:
			if (currentDist > 8800) {
				autoStep = 2;
				driveRobot(0,0);
			}
			else goStraight(0,0.7);
			break;
		case 2:
			if (redSide) {
				if (inputs.lightLeft.get()) {
					autoStep = 3;
					inputs.rightEncoder.reset();
					inputs.leftEncoder.reset();
					driveRobot(0,0);
					setYaw = currentYaw;
					autoLoop = 0;
				}
				else driveRobot(0.35,-0.35);
			}
			else {
				if (inputs.lightRight.get()) {
					autoStep = 3;
					inputs.rightEncoder.reset();
					inputs.leftEncoder.reset();
					driveRobot(0,0);
					setYaw = currentYaw;
					autoLoop = 0;
				}
				else driveRobot(-0.35,0.35);
			}
			break;
		case 3:
			if (currentDist > 2100 || autoLoop > 100) {
				if (/*inputs.sonar.getAverageVoltage() > 0.15 ||*/ autoLoop > 100) {					
					autoStep = 8;
					setYaw = currentYaw;
//					SmartDashboard.putNumber("autoDist", currentDist);
					SmartDashboard.putNumber("autoSonar", inputs.sonar.getAverageVoltage());
					inputs.rightEncoder.reset();
					inputs.leftEncoder.reset();
				}
				else {
				autoStep = 4;
				driveRobot(0,0);				
//				SmartDashboard.putNumber("autoDist", currentDist);
				SmartDashboard.putNumber("autoSonar", inputs.sonar.getAverageVoltage());
				autoLoop = 0;
				}			
			}
			else goStraight(setYaw,0.35);
			break;
		case 4:
			driveRobot(0,0);
			outputs.setGearReleaser(Value.kForward);
			if (autoLoop > 25) {
				autoStep = 5;
				inputs.rightEncoder.reset();
				inputs.leftEncoder.reset();
				setYaw = currentYaw;
			}
			break;
		case 5:
			if (currentDist < -2500) {
				autoStep = 6;
				driveRobot(0,0);
			}
			else goStraight(setYaw,-0.4);
			break;
		
		case 6:
			driveRobot(0,0);
			outputs.setGearReleaser(Value.kReverse);
			break;
		
	case 8:
		if (currentDist < -1100) {
			autoStep = 9;
			driveRobot(0,0);
		}
		else goStraight(setYaw + 3, -0.4);
		break;
	case 9:
		if (currentDist > 500) {
			autoStep = 4;
			driveRobot(0,0);
		}
		else goStraight(setYaw, 0.3);
		break;
		}
	}
		
	
	void autoShootFirstBlue() {    //   Blue Side
		switch(autoStep) {
		case 1:
			if (autoLoop > 100) {
				autoStep = 2;
				autoLoop = 0;
			}
			else {
				outputs.setShooter(0.81);
				outputs.setCollector(1.0);
				outputs.setFeeder(-1.0);
			}
			break;
		case 2:
			outputs.setIndexer(-1.0);
			if (autoLoop > 300) {
				autoStep = 3;
				outputs.setIndexer(0);
				outputs.setShooter(0);
				outputs.setCollector(0);
				outputs.setFeeder(0);				
			}
			break;
		case 3:
			outputs.setIndexer(0);
			outputs.setShooter(0);
			if (currentYaw < -81) {
				autoStep = 4;
				driveRobot(0,0);
				inputs.leftEncoder.reset();
				inputs.rightEncoder.reset();
			}
			else driveRobot(0.15,0.65);
			break;
		case 4:
			if (currentDist > 2700) {
				autoStep = 5;
				driveRobot(0,0);
			}
			else goStraight(-84,0.4);
			break;
		case 5:
//			if (currentYaw > -25) {
			if (inputs.lightLeft.get()) {
				autoStep = 9;
				driveRobot(0,0);
				inputs.leftEncoder.reset();
				inputs.rightEncoder.reset();
			}
			else driveRobot(0.35,-0.35);
			break;
		case 6:
			if (currentDist > 1400) {
				autoStep = 7;
				driveRobot(0,0);
			}
			else goStraight(-25,0.4);
			break;
		case 7:
			if (inputs.lightLeft.get())  {			
				autoStep = 9;
				driveRobot(0,0);
				inputs.leftEncoder.reset();
				inputs.rightEncoder.reset();
				setYaw = currentYaw;
			}		
			else driveRobot(0.35,-0.35);
			break;
		case 8:
			if (currentDist > 700) {
				autoStep = 9;
				driveRobot(0,0);
			}
			else goStraight(setYaw,0.35);
			
			break;
		case 9:        //need to add gear release
			driveRobot(0,0);
			break;
			}				
	}
	
	void autoShootFirstRed() {
		switch(autoStep) {
		case 1:
			if (autoLoop > 100) {
				autoStep = 2;
				autoLoop = 0;
			}
			else {
				outputs.setShooter(0.8);
				outputs.setCollector(1.0);
				outputs.setFeeder(-1.0);
			}
			break;
		case 2:
			outputs.setIndexer(-1.0);
			if (autoLoop > 300) {
				autoStep = 3;
				outputs.setIndexer(0);
				outputs.setShooter(0);
				outputs.setCollector(0);
				outputs.setFeeder(0);				
			}
			break;
		case 3:
			outputs.setIndexer(0);
			outputs.setShooter(0);
			if (currentYaw > 81) {
				autoStep = 4;
				driveRobot(0,0);
				inputs.leftEncoder.reset();
				inputs.rightEncoder.reset();
			}
			else driveRobot(0.65,0.15);
			break;
		case 4:
			if (currentDist > 2700) {
				autoStep = 5;
				driveRobot(0,0);
			}
			else goStraight(84,0.4);
			break;
		case 5:
//			if (currentYaw < 25) {
			if (inputs.lightRight.get()) {
				autoStep = 9;
				driveRobot(0,0);
				inputs.leftEncoder.reset();
				inputs.rightEncoder.reset();
			}
			else driveRobot(-0.35,0.35);
			break;
		case 6:
			if (currentDist > 1400) {
				autoStep = 7;
				driveRobot(0,0);
			}
			else goStraight(25,0.4);
			break;
		case 7:
			if (inputs.lightRight.get())  {			
				autoStep = 9;
				driveRobot(0,0);
				inputs.leftEncoder.reset();
				inputs.rightEncoder.reset();
				setYaw = currentYaw;
			}		
			else driveRobot(-0.35,0.35);
			break;
		case 8:
			if (currentDist > 700) {
				autoStep = 9;
				driveRobot(0,0);
			}
			else goStraight(setYaw,0.35);
			
			break;
		case 9:        //need to add gear release
			driveRobot(0,0);
			break;
			}				
	}
	void autoBoilerGearRed() {  //  red side
		switch(autoStep) {
		case 1:
			if (currentDist > 8500) {
				autoStep = 2;
				driveRobot(0,0);
			}
			else goStraight(0,0.6);
			break;
		case 2:
			if (inputs.lightRight.get()) {
				autoStep = 3;
				inputs.leftEncoder.reset();
				inputs.rightEncoder.reset();
				driveRobot(0,0);
				setYaw = inputs.navx.getYaw();
				autoLoop = 0;
			}
			else driveRobot(-0.33,0.33);
			break;
		case 3:
			
				if (currentDist > 2000 || autoLoop > 100) {
					if (/*inputs.sonar.getAverageVoltage() > 0.16 ||*/ autoLoop > 100) {
						
						autoStep = 9;
						setYaw = currentYaw;
						SmartDashboard.putNumber("autoDist", currentDist);
						SmartDashboard.putNumber("autoSonar", inputs.sonar.getAverageVoltage());
						inputs.leftEncoder.reset();
						inputs.rightEncoder.reset();
					}
					else {
					autoStep = 4;
					driveRobot(0,0);				
					SmartDashboard.putNumber("autoDist", currentDist);
					SmartDashboard.putNumber("autoSonar", inputs.sonar.getAverageVoltage());
					autoLoop = 0;
					}
				}
				else goStraight(setYaw - 3,0.35);
			
	
			break;
		case 4:
			driveRobot(0,0);
			outputs.setGearReleaser(Value.kForward);
			if (autoLoop > 25) {
				autoStep = 5;
				inputs.leftEncoder.reset();
				inputs.rightEncoder.reset();
				setYaw = currentYaw;
			}
			break;
		case 5:
			if (currentDist < -2500) {
				autoStep = 6;
				driveRobot(0,0);
			}
			else goStraight(setYaw,-0.4);
			break;
		
		case 6:
			outputs.setShooter(setShootPower);
			outputs.setCollector(1.0);
			outputs.setFeeder(-1.0);
			if (currentYaw < -40) {
				autoStep = 7;
				driveRobot(0,0);
				inputs.leftEncoder.reset();
				inputs.rightEncoder.reset();
			}
			else 
				turnToAngle(-42);
			
			outputs.setGearReleaser(Value.kReverse);
			break;
		case 7:
			outputs.setShooter(setShootPower);
			outputs.setCollector(1.0);
			outputs.setFeeder(-1.0);
			if (currentDist < -7400) {
				driveRobot(0,0);
				autoStep = 8;
			}
			else goStraight(-42,-0.4);
			break;
		case 8:
		//	double newPower = setShooterSpeed(1950);
		//	outputs.setShooter(newPower);
			outputs.setShooter(setShootPower);
			turnToAngle(-42);
			outputs.setIndexer(-1.0);
//			driveRobot(0,0);
			break;
		
	case 9:
		if (currentDist < -1100) {
			autoStep = 10;
			driveRobot(0,0);
		}
		else goStraight(setYaw + 3, -0.4);
		break;
	case 10:
		if (currentDist > 500) {
			autoStep = 4;
			driveRobot(0,0);
		}
		else goStraight(setYaw, 0.3);
		break;
		}
		
	}
	
	void autoBoilerGearBlue() {
		switch(autoStep) {
		case 1:
			if (currentDist > 8500) {
				autoStep = 2;
				driveRobot(0,0);
			}
			else goStraight(0,0.6);
			break;
		case 2:
			if (inputs.lightLeft.get()) {
				autoStep = 3;
				inputs.leftEncoder.reset();
				inputs.rightEncoder.reset();
				driveRobot(0,0);
				setYaw = inputs.navx.getYaw();
				autoLoop = 0;
			}
			else driveRobot(0.33,-0.33);
			break;
		case 3:
			
				if (currentDist > 2000 || autoLoop > 100) {
					if (/*inputs.sonar.getAverageVoltage() > 0.16 || */autoLoop > 100) {
						
						autoStep = 9;
						setYaw = currentYaw;
						SmartDashboard.putNumber("autoDist", currentDist);
						SmartDashboard.putNumber("autoSonar", inputs.sonar.getAverageVoltage());
						inputs.leftEncoder.reset();
						inputs.rightEncoder.reset();
					}
					else {
					autoStep = 4;
					driveRobot(0,0);				
					SmartDashboard.putNumber("autoDist", currentDist);
					SmartDashboard.putNumber("autoSonar", inputs.sonar.getAverageVoltage());
					autoLoop = 0;
					}
				}
				else goStraight(setYaw,0.35);
			
	
			break;
		case 4:
			driveRobot(0,0);
			outputs.setGearReleaser(Value.kForward);
			if (autoLoop > 25) {
				autoStep = 5;
				inputs.leftEncoder.reset();
				inputs.rightEncoder.reset();
				setYaw = currentYaw;
			}
			break;
		case 5:
			if (currentDist < -2500) {
				autoStep = 6;
				driveRobot(0,0);
			}
			else goStraight(setYaw,-0.4);
			break;
		
		case 6:
			outputs.setShooter(setShootPower);
			outputs.setCollector(1.0);
			outputs.setFeeder(-1.0);
			if (currentYaw > 40) {
				autoStep = 7;
				driveRobot(0,0);
				inputs.leftEncoder.reset();
				inputs.rightEncoder.reset();
			}
			else 
				turnToAngle(42);
			
			outputs.setGearReleaser(Value.kReverse);
			break;
		case 7:
			outputs.setShooter(setShootPower);
			outputs.setCollector(1.0);
			outputs.setFeeder(-1.0);
			if (currentDist < -7400) {
				driveRobot(0,0);
				autoStep = 8;
			}
			else goStraight(42,-0.4);
			break;
		case 8:
			//double newPower = setShooterSpeed(1950);
			//outputs.setShooter(newPower);
			outputs.setShooter(setShootPower);
			turnToAngle(42);
			outputs.setIndexer(-1.0);
//			driveRobot(0,0);
			break;
		
	case 9:
		if (currentDist < -1100) {
			autoStep = 10;
			driveRobot(0,0);
		}
		else goStraight(setYaw + 3, -0.4);
		break;
	case 10:
		if (currentDist > 500) {
			autoStep = 4;
			driveRobot(0,0);
		}
		else goStraight(setYaw, 0.3);
		break;
		}
		
	}
	
	void autoMoveShootCrossLine() {
		switch(autoStep) {
		case 1:
			if (currentDist > 3000) {
				autoStep = 2;
			}
			else goStraight(0,0.5);
			break;
		case 2:
			if (currentYaw < -61) {
				inputs.leftEncoder.reset();
				inputs.rightEncoder.reset();
				autoStep = 3;
			}
			else turnToAngle(-63);
			break;
		case 3:
			if (currentDist < -4700) {
				driveRobot(0,0);
				autoStep = 4;
				outputs.setShooter(setShootPower);
				outputs.setCollector(1.0);
				outputs.setFeeder(-1.0);
				autoLoop = 0;
			}
			else goStraight(-63,-0.4);
			break;
		case 4:
			outputs.setShooter(setShootPower);
			outputs.setCollector(1.0);
			outputs.setFeeder(-1.0);
			outputs.setIndexer(-1.0);
			if (autoLoop > 200) {
				autoStep = 5;
				inputs.leftEncoder.reset();
				inputs.rightEncoder.reset();
				driveRobot(0,0);
			}
			else turnToAngle(-60);
			break;
		case 5:
			if (currentDist > 7000) {
				autoStep = 6;
				driveRobot(0,0);
			}
			else goStraight(0,0.6);
			break;
		case 6:
			driveRobot(0,0);
		}
	}
}