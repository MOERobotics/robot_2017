package org.usfirst.frc.team365.robot;

import org.usfirst.frc.team365.net.MOETracker;
import org.usfirst.frc.team365.net.UDPTracker;

import com.ctre.CANTalon;
import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.Counter;
import edu.wpi.first.wpilibj.CounterBase.EncodingType;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;


/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends IterativeRobot implements PIDOutput {
	AHRS navX;
	
	CANTalon driveLA = new CANTalon(0);
	CANTalon driveLB = new CANTalon(1);
	CANTalon driveLC = new CANTalon(2);
	CANTalon driveRA = new CANTalon(13);
	CANTalon driveRB = new CANTalon(14);
	CANTalon driveRC = new CANTalon(15);
	
	CANTalon collector = new CANTalon(9);
	CANTalon climberA = new CANTalon(3);//3 old
	CANTalon climberB = new CANTalon(12);//6 old  12 for locoMOEtive & now same for lawnMOEr 
	CANTalon shooterWheelA = new CANTalon(11);//11 old
	CANTalon shooterWheelB = new CANTalon(6);//12 old  6 for locoMOEtive & now same for lawnMOEr
	CANTalon shootBall = new CANTalon(5);
	CANTalon conveyer = new CANTalon(4);
	CANTalon azimuth = new CANTalon(10);
	
	Joystick driveStick = new Joystick(0);
	Joystick funStick = new Joystick(1);
	
	Encoder distanceL = new Encoder(0,1,true,EncodingType.k1X);      //old 2,3   false for lawnMOEr   true for locoMOEtive
	Encoder distanceR = new Encoder(2,3,false,EncodingType.k1X);       //old 0,1   true for lawnMOEr   false for locoMOEtive
	
	Counter shootSpeed = new Counter(7);                 //old 6
	
	AnalogInput sonar = new AnalogInput(0);
	
	DigitalInput leftLight = new DigitalInput(8);        //old 5
	DigitalInput rightLight = new DigitalInput(9);       //old 4
//	DigitalInput gearDown = new DigitalInput(4);
	DigitalInput gearSensor = new DigitalInput(6);
															//old #s
	DoubleSolenoid shifter = new DoubleSolenoid(5,7);		//0,1
	Solenoid ballFlap = new Solenoid(0);					//4
	DoubleSolenoid gearRelease = new DoubleSolenoid(4,6);	//2,3
	Solenoid blinker = new Solenoid(3);
	Solenoid gearInBot = new Solenoid(1);
	Solenoid ballGate = new Solenoid(2);
	Compressor compressor = new Compressor();
	
	PIDController driveStraight;
	
	MOETracker tracker;
	Thread trackingThread;
	
	double direction;
	int teleopLoop;
	int autoLoop;
	int autoStep;
	int autoRoutine;
	int disabledLoop;
	int loopCount;
	int speedCount;
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
	double setShootSpeed;
	double autoShootSpeed;
	double teleopShootSpeed;
	
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
	boolean ballInFront;
	
	double kProp = 0.03;  // original .05
	double kInt = .0001;
	double kDer = 0;
	double autoKP;
	double autoKI;
	double autoKD;
	
	// conversion 107 pulses per inch

	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	@Override
	public void robotInit() {
		compressor.start();
		navX= new AHRS(SPI.Port.kMXP, (byte) 50);
		
		driveLA.setInverted(true);
		driveLB.setInverted(true);
		driveLC.setInverted(true);
		
		driveLA.enableBrakeMode(true);
		driveLB.enableBrakeMode(true);
		driveLC.enableBrakeMode(true);
		driveRA.enableBrakeMode(true);
		driveRB.enableBrakeMode(true);
		driveRC.enableBrakeMode(true); 
		
		azimuth.enableBrakeMode(true);
		
		shooterWheelA.enableBrakeMode(false);
		shooterWheelB.enableBrakeMode(false);
		
		climberA.enableBrakeMode(true);
		climberB.enableBrakeMode(true);
		
		shootSpeed.setSamplesToAverage(6);
		
		
		driveStraight = new PIDController(0.04,0.00005, 0.03, navX, this);
		driveStraight.setContinuous();
		driveStraight.setInputRange(-180.0,180.0);
		driveStraight.setOutputRange(-1.0, 1.0);
		
//		SmartDashboard.putNumber("kPropPID", 0.04);
//		SmartDashboard.putNumber("kIntPID", 0.00005);
//		SmartDashboard.putNumber("kDervPID", 0.01);
		
		shootSpeed.setDistancePerPulse(.0833);
	}
	void initialiseTracker(){
		if(tracker==null){
			tracker=UDPTracker.getTracker("tracker", 5801);
			System.out.println("reinit tracker");
			if(tracker!=null){
				trackingThread=new Thread(tracker);
				if(trackingThread!=null){
					trackingThread.start();
				}
			}
		}
	}
	
	public void disabledInit() {
		disabledLoop = 0;
		if (driveStraight.isEnabled()) {
			driveStraight.disable();
		}
		shooterWheelA.set(0);
		shooterWheelB.set(0);
		driveRobot(0,0);
		conveyer.set(0);
		shootBall.set(0);
		collector.set(0);
	}

	public void disabledPeriodic() {
		disabledLoop++;
		initialiseTracker();
		if (driveStick.getRawButton(2)) {  //  zero yaw
			navX.zeroYaw();
		}
		if (driveStick.getRawButton(4)) {   //  reset encoders
			distanceL.reset();
			distanceR.reset();
		}
		
		if (driveStick.getRawButton(6)) autoRoutine = 1;
		else if (driveStick.getRawButton(8)) autoRoutine = 2;
		else if (driveStick.getRawButton(10)) autoRoutine = 3;
		else if (driveStick.getRawButton(12)) autoRoutine = 4;
		else if (driveStick.getRawButton(11)) autoRoutine = 5;
		else if (driveStick.getRawButton(9)) autoRoutine = 6;
		else if (driveStick.getRawButton(7)) autoRoutine = 7;
		
		if (funStick.getRawButton(8)) redSide = true;
		else if (funStick.getRawButton(9)) redSide = false;
		
		if (disabledLoop % 25 == 0) {
			SmartDashboard.putBoolean("left light", leftLight.get());
			SmartDashboard.putBoolean("right light", rightLight.get());
			SmartDashboard.putNumber("shoot power", (driveStick.getRawAxis(2) + 1.0)/2.0);
			SmartDashboard.putNumber("left encoder", distanceL.getRaw());
			SmartDashboard.putNumber("right encoder", distanceR.getRaw());
			SmartDashboard.putNumber("auto routine", autoRoutine);
			SmartDashboard.putBoolean("red side", redSide);
//			SmartDashboard.putNumber("sonar", sonar.getAverageVoltage());
//			SmartDashboard.putBoolean("gear down", gearDown.get());

			if (!navX.isCalibrating()) {
				SmartDashboard.putNumber("yaw", navX.getYaw());
				SmartDashboard.putNumber("pitch", navX.getPitch());
				SmartDashboard.putNumber("roll", navX.getRoll());
			}
			SmartDashboard.putBoolean("is navX connected", navX.isConnected());
			
			double wheelPeriod = shootSpeed.getPeriod();
			SmartDashboard.putNumber("shooter speed", 60/(12*wheelPeriod));
//			SmartDashboard.putNumber("shooter speed", shootSpeed.getRate());
			double hood = (azimuth.getPulseWidthPosition() % 4096 );
			SmartDashboard.putNumber("azimuth", hood);
		}

//		autoKP = SmartDashboard.getNumber("DB/Slider 0", 0);
//		autoKI = SmartDashboard.getNumber("DB/Slider 1", 0);
//		autoKD = SmartDashboard.getNumber("DB/Slider 2", 0);
		
	}
	/**
	 * This function is run once each time the robot enters autonomous mode
	 */
	int bridgeBreakerTimer;
	@Override
	public void autonomousInit() {
		if(!compressor.enabled())
			compressor.start();
		autoLoop = 0;
		autoStep = 1;
		loopCount = 0;
		bridgeBreakerTimer=0;
		distanceL.reset();
		distanceR.reset();
		navX.zeroYaw();
		shifter.set(Value.kForward);
		turnSum = 0;
		straightSum = 0;
		speedSum = 0;
		setShootPower = 0.52;  // 0.75 originally for real bot
		autoShootSpeed = 1370;
		leftOn = false;
		rightOn = false;
		blinker.set(true);
//		setShootPower = (driveStick.getRawAxis(2) + 1.0)/2.0;
	}

	/**
	 * This function is called periodically during autonomous
	 */
	@Override
	public void autonomousPeriodic() {
		bridgeBreakerTimer++;
		autoLoop++;
		int distL = distanceL.getRaw();
		int distR = distanceR.getRaw();
		if (Math.abs(distL) > Math.abs(distR)) {
			currentDist = distL * 1.25;//  need to multiply by 1.25 for locoMOEtive
		}
		else currentDist = distR * 1.25;  //  need to multiply by 1.25 for locoMOEtive
		currentYaw = navX.getYaw();
		
//		autoRoutine = 7;  // for demos -do nothing routine
		
	switch(autoRoutine) {
	case 1:
		autoCenterGear();
		break;
	case 2:
		if (redSide) autoHopperShootRed();
		else autoHopperShootBlue();
		break;
	case 3:
		if (redSide) autoShootFirstRed();
		else  autoShootFirstBlue();		
		break;
	case 4:
		autoOuterGear();
	
		break;
	case 5:
		if (redSide)  autoBoilerGearRed();
		else autoBoilerGearBlue();
//		autoBoilerGear();
		break;
	case 6:
		if (redSide) {
		autoHopperTestRed();
		}
		else autoHopperTestBlue();
//		autoMoveShootCrossLine();
//		autoDriveStraightTest();
		break;
	case 7:
		autoDriveAndTurnTest();
		break;
	case 8:
		break;
	}
	
	}

	/**
	 * This function is called once each time the robot enters tele-operated
	 * mode
	 */
	@Override
	public void teleopInit() {
		if(!compressor.enabled())
			compressor.start();
		teleopLoop = 0;
		shifter.set(Value.kForward);  //start in low gear
		shiftPosition = true;
		lastButtonD12 = false;
		lastButtonD11 = false;
		lastButtonD10 = false;
		lastButtonD3 = false;
		lastButtonD8 = false;
		lastButtonF10 = false;
		lastButtonF11 = false;
		shooterOn = false;
		upToSpeed = false;
		loopCount = 0;
		leftOn = false;
		rightOn= false;
		straightSum = 0;
		turnSum = 0;
//		speedSum = 0;
		blinker.set(false);
	}
	

	/**
	 * This function is called periodically during operator control
	 */
	@Override
	public void teleopPeriodic() {
		teleopLoop++;
		
		boolean driveButton12 = driveStick.getRawButton(12);
		boolean driveButton2 = driveStick.getRawButton(2);
		boolean driveButton3 = driveStick.getRawButton(3);
		boolean driveButton4 = driveStick.getRawButton(4);
		boolean driveButton11 = driveStick.getRawButton(11);
		boolean driveButton10 = driveStick.getRawButton(10);
		boolean driveButton9 = driveStick.getRawButton(9);
		boolean driveButton8 = driveStick.getRawButton(8);
		boolean driveButton7 = driveStick.getRawButton(7);
		boolean funButton10 = funStick.getRawButton(10);
		boolean funButton11 = funStick.getRawButton(11);
		
		int distL = distanceL.getRaw();
		int distR = distanceR.getRaw();
		if (Math.abs(distL) > Math.abs(distR)) {
			currentDist = distL* 1.25;//  need to multiply by 1.25 for locoMOEtive
		}
		else currentDist = distR* 1.25;  // need to multiply by 1.25 for locoMOEtive
		currentYaw = navX.getYaw();
		
		gearInBot.set(gearSensor.get());

		double leftSide;
		double rightSide;
		
		if (!shooterOn) {
			controlCollector();
		}
		controlBallFeed();
		controlAzimuth();
		controlShooter();
		controlClimber();
		if (!driveButton12) {
			controlGear();
		}
/*		
		if (driveButton8 && !lastButtonD8) {    // toggle button for changing drive gear
			if (shiftPosition) {
				shifter.set(Value.kReverse);   //high gear
				gearRelease.set(Value.kReverse);  //make sure gear holder is up when driving in high gear
				shiftPosition = false;
			}
			else {
				shifter.set(Value.kForward);  //  low gear
				shiftPosition = true;
			}
		}
	*/	
		
		//   for demos must press button to get high gear - can replace with toggle code above for future events
		if (driveButton8) {
			shifter.set(Value.kReverse);
		}
		else shifter.set(Value.kForward);
		
		if (funButton11 && !lastButtonF11) {     //  buttons to increase or decrease shooter speed and power
			teleopShootSpeed += 20;
			setShootPower = setShootPower + .015;
		}
		else if (funButton10 && !lastButtonF10) {
			teleopShootSpeed -= 20;
			setShootPower = setShootPower - .015;
		}
/*		
		if (driveStick.getRawButton(8)) {   //shift gears
			shifter.set(Value.kReverse);  //shifts to high gear when button 8 is pushed
			
		}
		else {
			shifter.set(Value.kForward);
		}
		*/
		
		autoTargetCheckAngle();
		targeted = driveButton11;
		
		if (driveButton2) {
			turnLeftSlowly();
		}
		else if (driveButton4) {
			turnRightSlowly();
		}
		else if (driveButton9) {
			if (!lastButtonD9) {
				distanceL.reset();
				distanceR.reset();
			}
			if (currentDist < -230) {
				driveRobot(0,0);
			}
			else goStraight(0,-0.3);
		}
		else if (driveButton7) {
			if (!lastButtonD7) {
				distanceL.reset();
				distanceR.reset();
				straightSum = 0;
				setYaw = currentYaw;
				autoStep = 1;
			}
			reorientBot();
		}
		else if (driveStick.getRawButton(3)) {   //  back up robot from boiler
			if (!lastButtonD3) {
				ballInFront = false;
				distanceL.reset();
				distanceR.reset();
				autoLoop = 0;
			}

			if (currentDist > 2400) {
				autoLoop++;
				if (autoLoop > 2) 
					driveRobot(0,0);
				else driveRobot(-0.2,-0.2);
			}
			else driveRobot(0.35,0.35);
		}
		
		
/*		
		
		else if (driveStick.getRawButton(12))    {  // test go straight
			goStraight(0,0.5);
//			stayFirm(0);
		} */
		else if (driveStick.getRawButton(10)) {
			if (!lastButtonD10) {
				setYaw = currentYaw;
			}
			stayFirm(setYaw);
		}
		
		else if (driveButton11){
			autoTargetTurnToAngle(currentYaw, 45);
		}else {     //  drive robot with joystick

			double yJoy = -driveStick.getY();
			double xJoy = driveStick.getX();
			if (yJoy > -.1 && yJoy < 0.1) yJoy = 0;
			if (xJoy > -0.1 && xJoy < 0.1) xJoy = 0;

			if (driveStick.getTrigger()) {
				leftSide = yJoy;
				rightSide = yJoy;
			}
			else {		
				leftSide = limitValue(yJoy + xJoy);
				rightSide = limitValue(yJoy - xJoy);
			}

			driveRobot(leftSide,rightSide);
			leftOn = false;
			rightOn = false;
		}
		
		if (teleopLoop % 25 == 0) {   //print values to dashboard
			SmartDashboard.putBoolean("left light", leftLight.get());
			SmartDashboard.putBoolean("right light", rightLight.get());
			SmartDashboard.putNumber("shoot power", (driveStick.getRawAxis(2) + 1.0)/2.0);
			SmartDashboard.putNumber("left encoder", distanceL.getRaw());
			SmartDashboard.putNumber("right encoder", distanceR.getRaw());
			SmartDashboard.putNumber("sonar", sonar.getAverageVoltage());
			
			double wheelPeriod = shootSpeed.getPeriod();
			SmartDashboard.putNumber("shooter speed", 60/(12*wheelPeriod));
			SmartDashboard.putNumber("loSpeed", loSpeed);
			SmartDashboard.putNumber("hiSpeed", hiSpeed);
			SmartDashboard.putNumber("setShootPower", setShootPower);
			SmartDashboard.putBoolean("upToSpeed", upToSpeed);
						
			double hood = (azimuth.getPulseWidthPosition() % 4096);
			SmartDashboard.putNumber("azimuth", hood);
			
	//		SmartDashboard.putNumber("climberAmps", climber.getOutputCurrent());

			if (!navX.isCalibrating()) {
				SmartDashboard.putNumber("yaw", navX.getYaw());
				SmartDashboard.putNumber("pitch", navX.getPitch());
				SmartDashboard.putNumber("roll", navX.getRoll());
			}			
		}
		lastButtonD12 = driveButton12;
		lastButtonD11 = driveButton11;
		lastButtonD10 = driveButton10;
		lastButtonD9 = driveButton9;
		lastButtonD8 = driveButton8;
		lastButtonD7 = driveButton7;
		lastButtonD3 = driveButton3;
		lastButtonF10 = funButton10;
		lastButtonF11 = funButton11;
		
	}/*
	int watchLoop=0;
	boolean joystickWatch(){
		watchLoop++;
		boolean hasChanged=false;
		//watch for button changes
		for(int i=0;i<12;i++){
			hasChanged|=funStick.getRawButton(i)||driveStick.getRawButton(i);
		}
		//look for the joysticks having been moved
		hasChanged|=funStick.getMagnitude()>0.05||driveStick.getMagnitude()>0.05;
		if(hasChanged){
			gearInBot.set((watchLoop&2)==2);
		}return hasChanged;
	}*/
	
	void controlCollector() {
			if (funStick.getY() < -0.5) {   // cover for ball coming in
				ballFlap.set(true);	
			//	collector.set(1.0);
			}
			else  {
				ballFlap.set(false);
	//			collector.set(0);
			}
			
			if(funStick.getY() < -0.5){
				collector.set(1.0);
			}else if (driveStick.getRawButton(5)){
				collector.set(-0.75);
			}else if(driveStick.getRawButton(6)){
				collector.set(1.0);
			}else{
				collector.set(0.0);
			}
		}
	public void testInit(){
		if(!compressor.enabled())
			compressor.start();
	}
	/**
	 * This function is called periodically during test mode
	 */
	@Override
	public void testPeriodic() {
		testShooter();
		testBannerTurn();
	}

	void driveRobot(double leftPower, double rightPower) {
		driveLA.set(leftPower);
		driveLB.set(leftPower);
		driveLC.set(leftPower);
		driveRA.set(rightPower);
		driveRB.set(rightPower);
		driveRC.set(rightPower);
	}
	double limitValue(double powerValue) {
		if (powerValue > 1) {
			return 1;
		}
		else if (powerValue < -1) {
			return -1;
		}else return powerValue;
		
	}
	
	void controlGear() {
		if (funStick.getRawButton(7)) {   //  down - release gear
			gearRelease.set(Value.kForward);
		}
		else if (funStick.getRawButton(6)) {   //  gear holder up
			gearRelease.set(Value.kReverse);
		}
		
	
	}
	
	void controlBallFeed() {
		if (funStick.getRawButton(9))
			ballGate.set(true);
		else ballGate.set(false);
		//   run conveyer/elevator            take out for demos; put with ball shoot trigger
//		if (funStick.getRawButton(8)) {
//			conveyer.set(-1);
//		}
	}
	
	void testShooter(){
		double shooterPower = (driveStick.getRawAxis(2) + 1.0)/2.0;
		double shooterSpeed = 60./(shootSpeed.getPeriod() * 12);
		shooterPower = driveStick.getTrigger()?shooterPower:0;
		shooterWheelA.set(shooterPower);
		shooterWheelB.set(shooterPower);
		SmartDashboard.putNumber("Test Shooter Power", shooterPower);
		SmartDashboard.putNumber("Test Shooter Speed", shooterSpeed);
	}
	void testBannerTurn(){
		boolean drive2=driveStick.getRawButton(2);
		boolean drive4=driveStick.getRawButton(4);
		if(drive2 && !rightLight.get()){//left
			driveRobot(-0.3,0.3);
		}else if(drive4 && !leftLight.get()){//right
			driveRobot(0.3,-0.3);
		}else{
			driveRobot(0,0);
		}
	}
	
	void controlShooter() {
//		double startShootPower = (driveStick.getRawAxis(2) + 1.0)/2.0;
		double startShootPower = 0.5;
//		double shootPower = setShooterSpeed(1900);
//  turn shooter wheel on
		if (funStick.getRawButton(4)) {
			setShootPower = startShootPower;
			shooterWheelA.set(setShootPower);
			shooterWheelB.set(setShootPower);
			collector.set(1.0);
			shooterOn = true;
			upToSpeed = false;
			loopCount = 0;
			speedCount = 0;
			hiSpeed = 0;
			loSpeed =5000.;
			blinker.set(true);
			ballFlap.set(true);
			//TODO: PUsh Change
			teleopShootSpeed = 1370 - 20;
		}
		
		else if (funStick.getRawButton(5)) {
			shooterWheelA.set(0);
			shooterWheelB.set(0);
			conveyer.set(0);
			collector.set(0);
			shooterOn = false;
			blinker.set(false);
		}
		else if (shooterOn) {
//			loopCount++;
		if (driveStick.getRawButton(12))  {
			shooterWheelA.set(setShootPower);
			shooterWheelB.set(setShootPower);
		}
		else autoSpeedControl(teleopShootSpeed);   // 1950 for original shooter sprockets;  1400 for boiler spot
//			double newPower = setShooterSpeed(1950);
//			shooterWheelA.set(newPower);
//			shooterWheelB.set(newPower);
		
		}

		
//  shoot balls
		if (funStick.getTrigger()) {
			conveyer.set(-1);
			shootBall.set(-1);
//			double currentSpeed = shootSpeed.getRate(); 
			double currentSpeed = 60./(shootSpeed.getPeriod() * 12);
			if (currentSpeed > hiSpeed && currentSpeed < 3000) hiSpeed = currentSpeed;
			else if (currentSpeed < loSpeed) loSpeed = currentSpeed;
			
		}
		else /* if (!driveStick.getRawButton(12)) */ {
			shootBall.set(0);
			conveyer.set(0);
		}
	}
	
void controlAzimuth() {		
//  adjust azimuth
		if (funStick.getRawButton(3)) {
			azimuth.set(0.4);
		}
		else if (funStick.getRawButton(2)) {
			azimuth.set(-0.4);
		}
		else azimuth.set(0);
	}

	
	
	void autoSpeedControl2(double setSpeed) {
		
		double newPower;
		double currentSpeed = 60./(12.*shootSpeed.getPeriod());
//		double offSpeed = setSpeed - 60./(12.*shootSpeed.getPeriod());
		loopCount++;
		
		if (currentSpeed > 1 && currentSpeed < 2500) {
			if (loopCount < 75) {
				if (currentSpeed >= setSpeed) {
					speedCount++;
					if (speedCount > 2) upToSpeed = true;
				}
				newPower = setShootPower;
			}
			else if (loopCount < 1000) {
				if (currentSpeed > setSpeed + 50  )
				setShootPower = setShootPower - .0025;
				else if (currentSpeed < setSpeed - 50) 
					setShootPower = setShootPower + .0025;
				if (setShootPower > 0.98)   setShootPower = 0.98;
			
				if (currentSpeed >= setSpeed) {
					speedCount++;
					if (speedCount > 2) upToSpeed = true;
				}
				newPower = setShootPower;
			}
			else {
				if (currentSpeed < (setSpeed - 100)) {
					newPower = setShootPower * 1.05;
				}
				else newPower = setShootPower;
			}
		}
		else newPower = setShootPower;
		shooterWheelA.set(newPower);
		shooterWheelB.set(newPower);
	}
	
	void autoSpeedControl(double setSpeed) {
		double newPower;
		double currentSpeed = 60./(12.*shootSpeed.getPeriod());
//		double offSpeed = setSpeed - 60./(12.*shootSpeed.getPeriod());
		loopCount++;
		
		if (currentSpeed > 1 && currentSpeed < 2500) {
			if (loopCount < 105) {
				if (currentSpeed >= setSpeed) {
					speedCount++;
					if (speedCount > 2) upToSpeed = true;
				}
				newPower = setShootPower;
			}
			else if (loopCount < 155) {
				if (currentSpeed > setSpeed + 45) {
					setShootPower = setShootPower - .0013;
				}
				else if (currentSpeed < setSpeed - 45) {
				setShootPower = setShootPower + .0013;
				}
				if (setShootPower > 0.98)   setShootPower = 0.98;
			
	//			if (currentSpeed >= setSpeed) {
	//				speedCount++;
	//				if (speedCount > 2) upToSpeed = true;
	//			}
				newPower = setShootPower;
			}
			else {
				double offSpeed = setSpeed - currentSpeed;
				if (offSpeed > 50) {
//				if (currentSpeed < (setSpeed - 100)) {
//					newPower = 0.8;
					newPower = setShootPower * 1.03;
				}
				else if (offSpeed < -50)
					newPower = setShootPower * 0.95;
				else newPower = setShootPower;
//					newPower = setShootPower * (1.0 + offSpeed/1500);
//				}
//				else newPower = setShootPower;
			}
		}
		
		else newPower = setShootPower;
		
		if (newPower > 0.98) newPower = 0.98;
		
		shooterWheelA.set(newPower);
		shooterWheelB.set(newPower);
	}
	
	void controlClimber() {
		if (funStick.getY() > 0.5) {
			if(compressor.enabled())
				compressor.stop();
			climberA.set(1);  // just changed to positive for both //need positive power for locoMOEtive and negative for lawnMOEr
			climberB.set(1);
		}
		
		else {
			if(!compressor.enabled())
				compressor.start();
			climberA.set(0);
			climberB.set(0);
		}
	}
	
	
	public void pidWrite(double output) {
		double right = direction - output;
		double left = direction + output;
		driveRobot(left,right);
	}
	
	void turnLeftSlowly() {
		if (rightLight.get() || rightOn) {
			driveRobot(0,0);
			rightOn = true;
		}
		else 
		driveRobot(-0.3,0.3);
	}
	
	void turnRightSlowly() {
		if (leftLight.get() || leftOn) {
			driveRobot(0,0);
			leftOn = true;
		}
		else 
		driveRobot(0.3,-0.3);
	}
	
	void turnOnLeftSide() {
		driveRobot(0,-0.45);
	}
	
	void turnOnRightSide() {
		driveRobot(0.45,0);
	}
	
	void goStraight(double setBearing, double speed) {
		currentYaw = navX.getYaw();
		double offYaw = setBearing - currentYaw;
		
		if (offYaw > 0.7 || offYaw < -0.7) {
			if (offYaw > 0 && offYaw < 6) straightSum = straightSum + .0005;
			else if (offYaw < 0 && offYaw > -6)  straightSum = straightSum - .0005;
		}
		else offYaw = 0;
		
		double newPower = kProp * offYaw + straightSum;
		double leftSide = speed + newPower;
		double rightSide = speed - newPower;
		driveRobot(leftSide,rightSide);
	}
	
	
	void stayFirm(double setBearing) {
//		currentYaw = navX.getYaw();
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
	
	void reorientBot() {
		switch(autoStep) {
		case 1:
			if (currentDist < -1100) {
//			if (distanceL.getRaw() < -1100 || distanceR.getRaw() < -1100) {
			autoStep = 2;
			driveRobot(0,0);

		}
		else goStraight(setYaw + 3., -0.4);
		break;
		case 2:
			if (currentDist > 500) {
//			if (distanceL.getRaw()> 500 || distanceR.getRaw() > 500) {
			driveRobot(0,0);
			autoStep = 3;
		}
		else goStraight(setYaw,0.3);
		break;
		case 3:
			driveRobot(0,0);
		}
	}
	
	void autoGearRelease() {
		loopCount++;
		switch(gearStep) {
		case 1:
			gearRelease.set(Value.kForward);
			if (loopCount > 25) gearStep = 2;
			break;
		case 2:
			driveRobot(-0.4,-0.4);
			if (loopCount > 90) gearStep = 3;
			break;
		case 3:
			driveRobot(0,0);
			gearRelease.set(Value.kReverse);
			gearStep = 4;
			break;
		case 4:
			driveRobot(0,0);
			break;
		}
	}
	
	void autoDriveAndTurnTest() {
		switch(autoStep) {
		case 1:
			if (currentDist > 6000) {
				autoStep = 2;
				loopCount = 0;
			}
			else goStraight(0,0.5);
			break;
		case 2:
			if (loopCount > 1) {
				autoStep = 3;
				distanceL.reset();
				distanceR.reset();
				driveRobot(0,0);
			}
			else {
				if (currentYaw > 58 && currentYaw < 62) {
					loopCount++;
				}
				else stayFirm(60);
			}
			break;
		case 3:
			if (currentDist > 6000) {
				autoStep = 4;
			}
			else goStraight(60,0.5);
			break;
		case 4:
			driveRobot(0,0);
		}
	}
	
	void autoDriveStraightTest() {
		switch(autoStep) {
		case 1:
			driveStraight.setPID(autoKP, autoKI, autoKD);
			direction = 0.45;
			driveStraight.setSetpoint(0);
			driveStraight.enable();
			autoStep = 2;
			break;
		case 2:
			if (currentDist > 5500) {
//			if (autoLoop > 50) {
				autoStep = 3;
				driveStraight.disable();
				driveRobot(0,0);}
				break;
		case 3:
			driveRobot(0,0);
				
			
		}
	}
	
	void autoCenterGear() {  //  red side right now
		switch(autoStep) {
		case 1:            //  drive straight toward peg
			if (currentDist > 5200) {
				autoStep = 2;
				autoLoop = 0;
				SmartDashboard.putBoolean("autoLeft", leftLight.get());
				SmartDashboard.putBoolean("autoRight", rightLight.get());
				SmartDashboard.putNumber("autoYaw", currentYaw);
				}
			else goStraight(0,0.45);
				break;
		case 2:          //  slow down as approach peg
			if (currentDist > 7900 || autoLoop > 125) { 
				if (/*sonar.getAverageVoltage() > 0.15 ||*/ autoLoop > 125) {
				
					autoStep = 8;
					setYaw = currentYaw;
					SmartDashboard.putNumber("autoDist", currentDist);
					SmartDashboard.putNumber("autoLoop", autoLoop);
//					SmartDashboard.putNumber("autoSonar", sonar.getAverageVoltage());
					distanceL.reset();
					distanceR.reset();
				}
				else {
				autoStep = 3;
				driveRobot(0,0);				
				SmartDashboard.putNumber("autoDist", currentDist);
				SmartDashboard.putNumber("autoLoop", autoLoop);
//				SmartDashboard.putNumber("autoSonar", sonar.getAverageVoltage());
				autoLoop = 0;
				}
			}
			else goStraight(0,0.3);
			break;
		case 3:        //   release gear
			driveRobot(0,0);
			gearRelease.set(Value.kForward);
			if (autoLoop > 25) {
				distanceL.reset();
				distanceR.reset();
//				if(gearDown.get()){
//					autoStep=4;
//				}else{
					autoStep=4;  // go to step 11 only if light sensor added to bot
					autoLoop=0;
//				}
			}
			break;
		case 4:       //   back up from peg
			autoSpeedControl(autoShootSpeed);
			if (currentDist < -2200) {
				autoStep = 5;  // 5 originally, switch to 7 to avoid shooting
				driveRobot(0.2,0.2);
				autoLoop = 0;
			}
			else driveRobot(-0.4,-0.4);
			break;
		case 5:       //   turn to go to boiler
			if (redSide) {
				if (currentYaw < -66 && currentYaw > -70) {
					autoLoop++;
					if (autoLoop > 1) {
					autoStep = 6;
					distanceL.reset();
					distanceR.reset();
					speedCount = 0;
					loopCount = 0;
					}
				}
				else stayFirm(-68);
//				else driveRobot(-0.6,0.6);
			}
			else {
				if (currentYaw > 66 && currentYaw < 70) {
					autoLoop++;
					if (autoLoop > 1) {
					autoStep = 6;
					distanceL.reset();
					distanceR.reset();
					loopCount = 0;
					speedCount = 0;
					}
				}
//				else driveRobot(0.6,-0.6);
				else stayFirm(70);
			}
			gearRelease.set(Value.kReverse);
			break;
		case 6:       //   drive backward to boiler
			if (redSide) {
				if (currentDist < -10000) {
					targeted=false;
					autoTargetCheckAngle();
					autoStep = 12;
					driveRobot(0.2,0.2);
					setYaw = currentYaw;
					autoLoop = 0;
				}
				else goStraight(-68, -0.6);
			}
			else  {
				if (currentDist < -10000) {

					targeted=false;
					autoTargetCheckAngle();
					autoStep = 12;
					driveRobot(0.2,0.2);
					setYaw = currentYaw;
					autoLoop = 0;
				}
				else {  goStraight(68,-0.6);
				}
			}
				autoSpeedControl(autoShootSpeed);
//			collector.set(1.0);
//			conveyer.set(-1.0);
			ballFlap.set(true);
			break;
		case 7: 
			driveRobot(0,0);
			break;
		case 8:           //  not used 
			if (currentDist < -1100) {
				autoStep = 9;
				driveRobot(0,0);
			}
			else goStraight(setYaw + 3, -0.4);
			break;
		case 9:       //   not used
			if (currentDist > 500) {
				autoStep = 3;
				driveRobot(0,0);
			}
			else goStraight(setYaw, 0.3);
			break;
		case 10:    //    start shooting
			autoSpeedControl(autoShootSpeed);
			if(autoLoop>15)
				conveyer.set(-1);
			if(autoLoop>75)
				collector.set(.8);
			if(bridgeBreakerTimer>400)
				ballGate.set(true);
			shootBall.set(-1);
			autoTargetTurnToAngle(setYaw, 3);
			break;
		case 11:     //   not used - was meant to help if gear release was not working
			if((autoLoop%50)<25){
				gearRelease.set(Value.kReverse);
			}else{
				gearRelease.set(Value.kForward);
			}
//			if(gearDown.get()){
				autoStep=4;
//			}
				break;
		case 12:     //   get camera data
			autoSpeedControl(autoShootSpeed);
			if (autoLoop > 1) {
				driveRobot(0,0);
			}
			if(autoLoop>5){
				autoLoop=0;
				autoStep=10;
			}
		}
			
		
	}

	void autoHopperShootBlue() {    //   Blue Side
		switch(autoStep) {
		case 1:    //   go forward
			if (currentDist > 6900) {    //first try 8100 but maybe wrong distance
				driveRobot(0,0);
				lastOffYaw = currentYaw;
				ballFlap.set(true);
				autoStep = 2;
			}
			else goStraight(0,0.8);
			break;
		case 2:    //  turn toward hopper
			if (currentYaw < -89. && currentYaw > -91.) {
				autoStep = 3;
				distanceL.reset();
				distanceR.reset();
				autoLoop = 0;
			}
			else stayFirm(-90);
			break;
		case 3:    //  run into hopper to release balls
			if (currentDist > 3950 || autoLoop > 100) {
				autoStep = 4;
				straightSum = 0;
				
				SmartDashboard.putNumber("autoLoop", autoLoop);
				SmartDashboard.putNumber("autoDist", currentDist);
				autoLoop = 0;
			}
			else goStraight(-90,0.7);
			break;
		case 4:    //  wait to accumulate balls
			if (autoLoop > 55) {
				autoStep = 5;
				distanceL.reset();
				distanceR.reset();
				straightSum = 0;
			}
			else driveRobot(0,0);
			break;
		case 5:   //    back up from hopper
			if (currentDist < -1600) {
				autoStep = 6;
				turnSum = 0;
				loopCount = 0;
				speedCount = 0;
			}
			else goStraight(-90, -0.4);
			break;
		
			
		case 6:   //  turn to go to boiler
			autoSpeedControl(autoShootSpeed);
//			shooterWheelA.set(setShootPower);
//			shooterWheelB.set(setShootPower);
			collector.set(1.0);
//			conveyer.set(-1.0);
			ballFlap.set(true);
			if (currentYaw > 17) {
				autoStep = 7;
				distanceL.reset();
				distanceR.reset();
				straightSum = 0;
			}
			else stayFirm(20);
			break;
		case 7:    //   back up to boiler to shoot
			if (currentDist < -3700) {
				autoStep = 8;
				driveRobot(0.2,0.2);
				autoLoop = 0;
			}
			else {
				goStraight(20,-0.4);
				autoSpeedControl(autoShootSpeed);
//				shooterWheelA.set(setShootPower);
//				shooterWheelB.set(setShootPower);
			}
			break;
		case 8:   //   start shooter and collector	
			autoSpeedControl(autoShootSpeed);
			collector.set(1.0);
//			conveyer.set(-1.0);
			if (autoLoop > 1) {
				autoStep = 10;
				autoLoop = 0;
				driveRobot(0,0);
			}
			
			
//			upToSpeed = true;
//			double newPower = setShooterSpeed(1950);
//			shooterWheelA.set(newPower);
//			shooterWheelB.set(newPower);
			break;
		case 9:    //   shoot balls
			autoSpeedControl(autoShootSpeed);
			shootBall.set(-1.0);
			if (autoLoop > 15) {
				conveyer.set(-1.0);
			}
			//autoTargetCheckAngle();
			autoTargetTurnToAngle(20, 3);
			break;
		case 10:   //  get camera data
			autoSpeedControl(autoShootSpeed);
			autoTargetCheckAngle();
			if(autoLoop>5){
				autoLoop=0;
				autoStep=9;
			}
		}
	}
	
	void autoHopperShootRed() {
		switch(autoStep) {
		case 1:    //     go forward
			if (currentDist > 6900) {    //first try 8100 but maybe wrong distance
				autoStep = 2;
				driveRobot(0,0);
				lastOffYaw = currentYaw;
				ballFlap.set(true);
			}
			else goStraight(0,0.8);
			break;
		case 2:    //   turn toward hopper
			if (currentYaw > 89. && currentYaw < 91.) {
				autoStep = 3;
				distanceL.reset();
				distanceR.reset();
				autoLoop = 0;
			}
			else stayFirm(90);
			break;
		case 3:    //  drive toward hopper to release balls
			if (currentDist > 3950 || autoLoop > 100) {
				autoStep = 4;
				straightSum = 0;
				
				SmartDashboard.putNumber("autoLoop", autoLoop);
				SmartDashboard.putNumber("autoDist", currentDist);
				autoLoop = 0;
			}
			else goStraight(90,0.7);
			break;
		case 4:    //  wait to accumulate balls
			if (autoLoop > 55) {
				autoStep = 5;
				distanceL.reset();
				distanceR.reset();
				straightSum = 0;
				loopCount = 0;
				speedCount = 0;
			}
			else driveRobot(0,0);
			break;
		case 5:    //   back up from hopper
			if (currentDist < -1600) {
				autoStep = 6;
				turnSum = 0;
			}
			else goStraight(90, -0.4);
			break;
		
			
		case 6:    //   turn to go to boiler
			if (currentYaw < -17) {
				autoStep = 7;
				distanceL.reset();
				distanceR.reset();
				collector.set(1.0);
//				conveyer.set(-1.0);
				ballFlap.set(true);
				autoSpeedControl(autoShootSpeed);
//				shooterWheelA.set(setShootPower);
//				shooterWheelB.set(setShootPower);
				straightSum = 0;
			}
			else stayFirm(-20);
			break;
		case 7:    // back up to boiler ready to shoot
			if (currentDist < -3700) {
				autoStep = 8;
				driveRobot(0,0);
				autoLoop = 0;
				autoTargetCheckAngle();
			}
			else {
				goStraight(-20,-0.4);
				autoSpeedControl(autoShootSpeed);
//				shooterWheelA.set(setShootPower);
//				shooterWheelB.set(setShootPower);
			}
			break;
		case 8:	    //  get camera data
//			upToSpeed = true;
			autoSpeedControl(autoShootSpeed);
//			double newPower = setShooterSpeed(1950);
//			shooterWheelA.set(setShootPower);
//			shooterWheelB.set(setShootPower);
			collector.set(1.0);
//			conveyer.set(-1.0);
			if (autoLoop > 3) {
				autoStep = 10;
				autoLoop = 0;
				autoTargetCheckAngle();
			}
			if (autoLoop > 3)
			stayFirm(-20);
			break;
		case 9:    //   shoot balls
			autoSpeedControl(autoShootSpeed);
			shootBall.set(-1.0);
			if (autoLoop > 15) {
				conveyer.set(-1.0);
			}
			autoTargetTurnToAngle(-20, 3);
			break;
		case 10:   //  get best camera data
			autoSpeedControl(autoShootSpeed);
			autoTargetCheckAngle();
			if(autoLoop>5){
				autoLoop=0;
				autoStep=9;
			}
			break;
		
		
		}
	}
	void autoOuterGear() {     
		switch(autoStep) {
		case 1:    //   drive straight
			if (redSide) {
				if (currentDist > 8800) {
					autoStep = 2;
					driveRobot(0,0);
				}
				else goStraight(0,0.7);
			}
			else  {
				if (currentDist > 8800) {
					autoStep = 2;
					driveRobot(0,0);
				}
				else goStraight(0,0.7);	
			}
			break;
		case 2:   //    turn toward peg
			if (redSide) {
				if (leftLight.get()) {
					autoStep = 3;
					distanceL.reset();
					distanceR.reset();
					driveRobot(0,0);
					setYaw = currentYaw + 2.5;
					SmartDashboard.putNumber("autoyaw", setYaw);
					autoLoop = 0;
				}
				else driveRobot(0.35,-0.35);
			}
			else {
				if (rightLight.get() || currentYaw < -58.0) {
					autoStep = 3;
					distanceL.reset();
					distanceR.reset();
					driveRobot(0,0);
					setYaw = currentYaw - 2.5;
					SmartDashboard.putNumber("autoyaw", setYaw);
					autoLoop = 0;
				}
				else driveRobot(-0.35,0.35);
			}
			break;
		case 3:    //  go forward to peg
			if (currentDist > 2700 || autoLoop > 100) {
				if (/*sonar.getAverageVoltage() > 0.15 ||*/ autoLoop > 100) {					
					autoStep = 8;
					setYaw = currentYaw;
					SmartDashboard.putNumber("autoDist", currentDist);
					SmartDashboard.putNumber("autoLoop", autoLoop);
//					SmartDashboard.putNumber("autoSonar", sonar.getAverageVoltage());
					distanceL.reset();
					distanceR.reset();
				}
				else {
				autoStep = 4;
				driveRobot(0,0);				
				SmartDashboard.putNumber("autoDist", currentDist);
				SmartDashboard.putNumber("autoLoop", autoLoop);
//				SmartDashboard.putNumber("autoSonar", sonar.getAverageVoltage());
				autoLoop = 0;
				}			
			}
			else goStraight(setYaw,0.3);
			break;
		case 4:    //  release gear
			driveRobot(0,0);
			gearRelease.set(Value.kForward);
			if (autoLoop > 25) {
				distanceL.reset();
				distanceR.reset();
				setYaw = currentYaw;
//				if(gearDown.get()){
//					autoStep=5;
//				}else{
					autoStep=5;  // move to step 11 only if light sensor added to bot
					autoLoop=0;
//				}
			}
			break;
		case 5:   //    back up from peg
			if (currentDist < -2500) {
				autoLoop = 0;
				autoStep = 12;
				driveRobot(0,0);
			}
			else goStraight(setYaw,-0.4);
			break;
			
		case 6:    //   stop
			driveRobot(0,0);
			gearRelease.set(Value.kReverse);
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
	case 11:
		if((autoLoop%50)<25){
			gearRelease.set(Value.kReverse);
		}else{
			gearRelease.set(Value.kForward);
		}
//		if(gearDown.get()){
			autoStep=5;
//		}
		break;
		
		// turn straight
	case 12:
		if(currentYaw > -2 && currentYaw < 2) {
			autoLoop++;
			if (autoLoop > 1) {
			autoStep = 13;
			distanceL.reset();
			distanceR.reset();
			}
		} else{
			stayFirm(0); 
		}
		break;
		
		//go forward 20 feet
	case 13:
		if (currentDist > 24000){
			autoStep = 6;
		} else {
			goStraight(0,.5);
		}
		break;
		}
	}
		
	
	void autoShootFirstBlue() {    //   Blue Side
		switch(autoStep) {
		case 1:
			if (autoLoop > 125) {
				autoStep = 2;
				autoLoop = 0;
			}
			else {
				autoSpeedControl(autoShootSpeed);
//				shooterWheelA.set(0.81);
//				shooterWheelB.set(0.81);
//				collector.set(1.0);
//				conveyer.set(-1.0);
				ballFlap.set(true);
//				autoLoop = 0;
			}
			break;
		case 2:
			autoSpeedControl(autoShootSpeed);
			if(autoLoop>15)
				conveyer.set(-1);
			if(autoLoop>75)
				collector.set(.8);
	
			shootBall.set(-1.0);
			if (autoLoop > 300) {
				autoStep = 3;
				shootBall.set(0);
				shooterWheelA.set(0);
				shooterWheelB.set(0);
				collector.set(0);
				conveyer.set(0);				
			}
			break;
		case 3:
			shootBall.set(0);
			shooterWheelA.set(0);
			shooterWheelB.set(0);
			if (currentDist > 3400) {
				autoStep = 4;
				driveRobot(0,0);
				
			}
			else goStraight(0, 0.4);
			break;
		case 4:
			if (currentYaw < -73) {
				autoStep = 10;
				distanceL.reset();
				distanceR.reset();
				driveRobot(0,0);
			}
			else driveRobot(-0.35,0.35);
			break;
		case 5:
//			if (currentYaw > -25) {
			if (leftLight.get()) {
				autoStep = 6;
				driveRobot(0,0);
				distanceL.reset();
				distanceR.reset();
				autoLoop = 0;
				setYaw = currentYaw + 6;
			}
			else driveRobot(0.35,-0.35);
			break;
		case 6:
			if (currentDist > 2600 && autoLoop > 70 ) {
				autoStep = 7;
				driveRobot(0,0);
				autoLoop = 0;
			}
			else goStraight(setYaw,0.4);
			break;
		case 7:
			driveRobot(0,0);
			gearRelease.set(Value.kForward);
			if (autoLoop > 25) {
				distanceL.reset();
				distanceR.reset();
				setYaw = currentYaw;

					autoStep=8; 
					autoLoop=0;
//				}
			}
			break;
		case 8:
			if (currentDist < -2500) {
				autoStep = 9;
				driveRobot(0,0);
			}
			else goStraight(setYaw,-0.4);
			
			break;
		case 9:       
			driveRobot(0,0);
			break;
		case 10:
			if (currentDist > 7600) {
				autoStep = 5;
				driveRobot(0,0);
			}
			else goStraight(-75,0.5);
			break;
			}				
	}
	
	void autoShootFirstRed() {
		switch(autoStep) {
		case 1:
			if (autoLoop > 125) {
				autoStep = 2;
				autoLoop = 0;
			}
			else {
				autoSpeedControl(autoShootSpeed);
//				shooterWheelA.set(0.81);
//				shooterWheelB.set(0.81);
//				collector.set(1.0);
//				conveyer.set(-1.0);
				ballFlap.set(true);
//				autoLoop = 0;
			}
			break;
		case 2:
			autoSpeedControl(autoShootSpeed);
			if(autoLoop>15)
				conveyer.set(-1);
			if(autoLoop>75)
				collector.set(.8);
	
			shootBall.set(-1.0);
			if (autoLoop > 300) {
				autoStep = 3;
				shootBall.set(0);
				shooterWheelA.set(0);
				shooterWheelB.set(0);
				collector.set(0);
				conveyer.set(0);				
			}
			break;
		case 3:
			shootBall.set(0);
			shooterWheelA.set(0);
			shooterWheelB.set(0);
			if (currentDist > 3400) {
				autoStep = 4;
				driveRobot(0,0);
				
			}
			else goStraight(0, 0.4);
			break;
		case 4:
			if (currentYaw > 73) {
				autoStep = 10;
				distanceL.reset();
				distanceR.reset();
				driveRobot(0,0);
			}
			else driveRobot(0.35,-0.35);
			break;
		case 5:
//			if (currentYaw > -25) {
			if (rightLight.get()) {
				autoStep = 6;
				driveRobot(0,0);
				distanceL.reset();
				distanceR.reset();
				autoLoop = 0;
				setYaw = currentYaw - 3;
			}
			else driveRobot(-0.35,0.35);
			break;
		case 6:
			if (currentDist > 2600 && autoLoop > 70 ) {
				autoStep = 7;
				driveRobot(0,0);
				autoLoop = 0;
			}
			else goStraight(setYaw,0.4);
			break;
		case 7:
			driveRobot(0,0);
			gearRelease.set(Value.kForward);
			if (autoLoop > 25) {
				distanceL.reset();
				distanceR.reset();
				setYaw = currentYaw;

					autoStep=8; 
					autoLoop=0;
//				}
			}
			break;
		case 8:
			if (currentDist < -2500) {
				autoStep = 9;
				driveRobot(0,0);
			}
			else goStraight(setYaw,-0.4);
			
			break;
		case 9:       
			driveRobot(0,0);
			break;
		case 10:
			if (currentDist > 7600) {
				autoStep = 5;
				driveRobot(0,0);
			}
			else goStraight(75,0.5);
			break;
			}				
	}

	
	
	void autoBoilerGearRed() {  //  red side
		switch(autoStep) {
		case 1:   //     drive straight
			if (currentDist > 8800) {
				autoStep = 2;
				driveRobot(0,0);
			}
			else goStraight(0,0.6);
			break;
		case 2:   //     turn toward the peg
			if (rightLight.get()||currentYaw<-58) {
				autoStep = 3;
				distanceL.reset();
				distanceR.reset();
				driveRobot(0,0);
				setYaw = navX.getYaw() - 2.5;
				SmartDashboard.putNumber("autoyaw", setYaw);
				autoLoop = 0;
			}
			else driveRobot(-0.33,0.33);
			break;
		case 3:   //   drive toward the peg
			
				if (currentDist > 2700 || autoLoop > 100) {
					if (/*sonar.getAverageVoltage() > 0.16 ||*/ autoLoop > 100) {
						
						autoStep = 9;
						setYaw = currentYaw;
						SmartDashboard.putNumber("autoDist", currentDist);
						SmartDashboard.putNumber("autoLoop", autoLoop);
//						SmartDashboard.putNumber("autoSonar", sonar.getAverageVoltage());
						distanceL.reset();
						distanceR.reset();
					}
					else {
					autoStep = 4;
					driveRobot(0,0);				
					SmartDashboard.putNumber("autoDist", currentDist);
					SmartDashboard.putNumber("autoLoop", autoLoop);
//					SmartDashboard.putNumber("autoSonar", sonar.getAverageVoltage());
					autoLoop = 0;
					}
				}
				else goStraight(setYaw,0.3);
			
	
			break;
		case 4:   //     release gear
			driveRobot(0,0);
			gearRelease.set(Value.kForward);
			if (autoLoop > 25) {
				distanceL.reset();
				distanceR.reset();
				setYaw = currentYaw;
//				if(gearDown.get()){
//					autoStep = 5;
//				}else{
					autoStep = 5;   // move to 11 only if light sensor added to bot
					autoLoop = 0;
//				}
			}
			break;
		case 5:   //    back up from peg
			autoSpeedControl(autoShootSpeed);
			if (currentDist < -2500) {
				autoStep = 6;
				autoLoop=0;
				driveRobot(0,0);
			}
			else goStraight(setYaw,-0.4);
			break;
		
		case 6:    //    turn to go to boiler
			autoSpeedControl(autoShootSpeed);
//			shooterWheelA.set(setShootPower);
//			shooterWheelB.set(setShootPower);
			//collector.set(1.0);
			if (currentYaw < -38 && currentYaw > -42) {
				autoLoop++;
				if (autoLoop > 1) {
				autoStep = 7;
				driveRobot(0,0);
				distanceL.reset();
				distanceR.reset();
				}
			}
			else 
				stayFirm(-40);
			
			gearRelease.set(Value.kReverse);
			break;
		case 7:    //    back up to boiler
			ballFlap.set(true);
			autoSpeedControl(autoShootSpeed);
//			shooterWheelA.set(setShootPower);
//			shooterWheelB.set(setShootPower);
			//collector.set(1.0);
			if (currentDist < -7000) {
				driveRobot(0.2,0.2);
				targeted=false;
				autoTargetCheckAngle();
				autoLoop=0;
				autoStep = 12;
			}
			else goStraight(-40,-0.4);
			break;
		case 8:   //  shoot balls
//			double newPower = setShooterSpeed(1950);
			autoSpeedControl(autoShootSpeed);
//			shooterWheelA.set(setShootPower);
//			shooterWheelB.set(setShootPower);
			if(autoLoop>15)
				conveyer.set(-1);
			if(autoLoop>75)
				collector.set(0.8);
			if(bridgeBreakerTimer>475)
				ballGate.set(true);
			autoTargetTurnToAngle(-41, 3);
			shootBall.set(-1.0);
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
	case 11:
		if((autoLoop%50)<25){
			gearRelease.set(Value.kReverse);
		}else{
			gearRelease.set(Value.kForward);
		}
//		if(gearDown.get()){
			autoStep=5;
//		}
		break;
	case 12:   //   get camera data
		autoSpeedControl(autoShootSpeed);
		if(autoLoop > 5){
			autoStep=8;
			autoLoop=0;
		}else{
			driveRobot(0,0);
		}
		break;
		}	
	}
	
	void autoBoilerGearBlue() {
		switch(autoStep) {
		case 1:   //    drive straight
			if (currentDist > 8900) {
				autoStep = 2;
				driveRobot(0,0);
			}
			else goStraight(0,0.6);
			break;
		case 2:   //   turn toward peg
			// if(leftLight.get() && currentYaw>50-variance && currentYaw<50+variance)
			if (leftLight.get()) {// || currentYaw > 50
				autoStep = 3;
				distanceL.reset();
				distanceR.reset();
				driveRobot(0,0);
				setYaw = navX.getYaw()+2.5;
				SmartDashboard.putNumber("autoyaw", setYaw);
				autoLoop = 0;
			}
			else driveRobot(0.33,-0.33);
			break;
		case 3:    //    drive toward peg
			
				if (currentDist > 2720 || autoLoop > 100) {
					if (/*sonar.getAverageVoltage() > 0.16 ||*/ autoLoop > 100) {
						
						autoStep = 9;
						setYaw = currentYaw;
						SmartDashboard.putNumber("autoDist", currentDist);
						SmartDashboard.putNumber("autoLoop", autoLoop);
//						SmartDashboard.putNumber("autoSonar", sonar.getAverageVoltage());
						distanceL.reset();
						distanceR.reset();
					}
					else {
					autoStep = 4;
					driveRobot(0,0);				
					SmartDashboard.putNumber("autoDist", currentDist);
					SmartDashboard.putNumber("autoLoop", autoLoop);
//					SmartDashboard.putNumber("autoSonar", sonar.getAverageVoltage());
					autoLoop = 0;
					}
				}
				else goStraight(setYaw,0.3);
			
	
			break;
		case 4:    //    release gear
			driveRobot(0,0);
			gearRelease.set(Value.kForward);
			if (autoLoop > 25) {
				autoStep = 5;
				distanceL.reset();
				distanceR.reset();
				setYaw = currentYaw;
//				if(gearDown.get()){
//					autoStep=5;
//				}else{
					autoStep=5;  // move to step 11 only if light sensor added to bot
					autoLoop=0;
//				}
			}
			break;
		case 5:    //    back up from peg
			autoSpeedControl(autoShootSpeed);
			if (currentDist < -2600) {
				autoStep = 6;
				driveRobot(0,0);
				autoLoop = 0;
			}
			else goStraight(setYaw,-0.4);
			break;
		
		case 6:    //  turn to go to boiler
			ballFlap.set(true);
			autoSpeedControl(autoShootSpeed);
//			shooterWheelA.set(setShootPower);
//			shooterWheelB.set(setShootPower);
			//collector.set(1.0);
			if (currentYaw > 39 && currentYaw < 43) {
				autoLoop++;
				if (autoLoop > 1) {
				autoStep = 7;
				driveRobot(0,0);
				distanceL.reset();
				distanceR.reset();
				}
			}
			else 
				stayFirm(41);
			
			gearRelease.set(Value.kReverse);
			break;
		case 7:    //    back up to boiler
			autoSpeedControl(autoShootSpeed);
//			shooterWheelA.set(setShootPower);
//			shooterWheelB.set(setShootPower);
			//collector.set(1.0);
			if (currentDist < -7000) {
				driveRobot(0.2,0.2);
				autoLoop=0;
				targeted=false;
				autoTargetCheckAngle();
				autoStep = 8;
			}
			else goStraight(41,-0.4);
			break;
		case 8:   //   shoot balls
//			double newPower = setShooterSpeed(1950);
			autoSpeedControl(autoShootSpeed);
//			shooterWheelA.set(setShootPower);
//			shooterWheelB.set(setShootPower);
			if(autoLoop>15)
				conveyer.set(-1);
			if(autoLoop>75)
				collector.set(.8);
			if(bridgeBreakerTimer>475)
				ballGate.set(true);
			autoTargetTurnToAngle(40, 3);
			shootBall.set(-1.0);
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
	case 11:
		if((autoLoop%50)<25){
			gearRelease.set(Value.kReverse);
		}else{
			gearRelease.set(Value.kForward);
		}
//		if(gearDown.get()){
			autoStep=5;
//		}
		break;
	case 12:
		autoSpeedControl(autoShootSpeed);
		if(autoLoop > 5){
			autoStep=8;
			autoLoop=0;
		}else{
			driveRobot(0,0);
		}
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
				distanceL.reset();
				distanceR.reset();
				autoStep = 3;
			}
			else stayFirm(-63);
			break;
		case 3:
			if (currentDist < -4700) {
				driveRobot(0,0);
				autoStep = 4;
				shooterWheelA.set(setShootPower);
				shooterWheelB.set(setShootPower);
				collector.set(1.0);
				conveyer.set(-1.0);
				autoLoop = 0;
			}
			else goStraight(-63,-0.4);
			break;
		case 4:
			shooterWheelA.set(setShootPower);
			shooterWheelB.set(setShootPower);
			collector.set(1.0);
			conveyer.set(-1.0);
			shootBall.set(-1.0);
			if (autoLoop > 200) {
				autoStep = 5;
				distanceL.reset();
				distanceR.reset();
				driveRobot(0,0);
			}
			else stayFirm(-60);
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
	
	void autoHopperTestRed() {  //
		switch(autoStep) {
		case 1:
			if (currentYaw > 43) {
				autoStep = 2;
				distanceL.reset();
				distanceR.reset();
				
			}
			else driveRobot(0.5,0.1);
			break;
		case 2:
			if (currentDist > 900) {
				autoStep = 3;
			
			}
			else goStraight(45,0.4);
			break;
		case 3:
			if (currentYaw < 3) {
				autoStep = 4;
				distanceL.reset();
				distanceR.reset();
			}
			else driveRobot(0.1,0.5);
			break;
		case 4:
			if (currentDist > 500) {
				autoStep = 5;
				driveRobot(0,0);
				autoLoop = 0;
			}
			else goStraight(0,0.4);
			break;
		case 5:
			if (autoLoop > 50) {
				autoStep = 6;
			}
			else stayFirm(15);
			break;
		case 6:
			stayFirm(-10);
//			driveRobot(0,0);
		}
	}
	
	void autoHopperTestBlue() {
		
			switch(autoStep) {
			case 1:
				if (currentYaw < -43) {
					autoStep = 2;
					distanceL.reset();
					distanceR.reset();
					
				}
				else driveRobot(0.1,0.5);
				break;
			case 2:
				if (currentDist > 900) {
					autoStep = 3;
				
				}
				else goStraight(-45,0.4);
				break;
			case 3:
				if (currentYaw > -3) {
					autoStep = 4;
					distanceL.reset();
					distanceR.reset();
				}
				else driveRobot(0.5,0.1);
				break;
			case 4:
				if (currentDist > 500) {
					autoStep = 5;
					driveRobot(0,0);
					autoLoop = 0;
				}
				else goStraight(0,0.4);
				break;
			case 5:
				if (autoLoop > 50) {
					autoStep = 6;
				}
				else stayFirm(-15);
				break;
			case 6:
				stayFirm(10);
//				driveRobot(0,0);
			}
	}
	
	boolean targeted = false;
	Double targetBearing2;
	public void autoTargetCheckAngle(){
		if(tracker!=null){
			double p[] = tracker.getCenter();
			if(p[0]!=-1 && p[1]!=-1){
				if(!targeted){
					SmartDashboard.putNumber("tracker x", p[0]);
					SmartDashboard.putNumber("tracker y", p[1]);
					targetBearing2 = navX.getYaw()-((p[0]-0.5)*-69.344+0.64);
					SmartDashboard.putNumber("targetBearing2", targetBearing2);
				}
			} else { targetBearing2 = null;}
		}else{
			targetBearing2=null;
		}
	}
	public void autoTargetTurnToAngle(double myYaw, double variance){
		if(targetBearing2!=null){
			if(targetBearing2 < myYaw+variance && targetBearing2 > myYaw-variance){
				SmartDashboard.putString("Auto-Targeted", "Yes; value = "+targetBearing2);
				stayFirm(targetBearing2);
			}else{
				SmartDashboard.putString("Auto-Targeted", "No; value out of range: "+targetBearing2);
				stayFirm(myYaw);
			}
		}else{
			SmartDashboard.putString("Auto-Targeted", "No; value not present");
			stayFirm(myYaw);
		}
	}
	/*public void autoTargetMove(){
		stayFirm(targetBearing2);
	}*/
	/*public double getTrackerDist(){
		if(tracker!=null){
			double tracker_y=tracker.getCenter()[1];
			double distance=tracker_y*91.3+2.46+9.5+5;
			SmartDashboard.putNumber("tracker distance", distance);
			return distance;
		}else{
			return -1;
		}
	}*/
	long t_old;
	long t_now;
	public void zeroClock(){
		t_old=System.currentTimeMillis();
	}
	public void rawTimimg(){
		t_now = System.currentTimeMillis();
		SmartDashboard.putNumber("loop time", t_now-t_old);
		t_old=t_now;
	}
}
