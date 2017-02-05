package org.usfirst.frc.team365.robot;

import com.ctre.CANTalon;
import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.*;

/**
 * Created by kevok on 1/30/17.
 */
public class GlobalShared {
    public static void init() {
        SharedVariables.init();
        SharedVariables.registerKeyspace("Global");
        SharedVariables.registerKey("Global","driveLA",    new CANTalon(1));
        SharedVariables.registerKey("Global","driveLB",    new CANTalon(2));
        SharedVariables.registerKey("Global","driveLC",    new CANTalon(3));
        SharedVariables.registerKey("Global","driveRA",    new CANTalon(12));
        SharedVariables.registerKey("Global","driveRB",    new CANTalon(13));
        SharedVariables.registerKey("Global","driveRC",    new CANTalon(14));
        SharedVariables.registerKey("Global","driveStick", new Joystick(0));
        SharedVariables.registerKey("Global","funStick",   new Joystick(1));
        SharedVariables.registerKey("Global","gearShift",  new DoubleSolenoid(0,1));
        SharedVariables.registerKey("Global","pidControl");
        SharedVariables.registerKey("Global","navx", new AHRS(SPI.Port.kMXP, (byte)50));
        SharedVariables.registerKey("Global","leftEncoder",  new Encoder(0,1));
        SharedVariables.registerKey("Global","rightEncoder", new Encoder(2,3));
    }

}
