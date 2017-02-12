// RobotBuilder Version: 2.0
//
// This file was generated by RobotBuilder. It contains sections of
// code that are automatically generated and assigned by robotbuilder.
// These sections will be updated in the future when you export to
// Java from RobotBuilder. Do not put any code or make any change in
// the blocks indicating autogenerated code or it will be lost on an
// update. Deleting the comments indicating the section will prevent
// it from being updated in the future.


package org.usfirst.frc157.ProtoBot2017;

// BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=IMPORTS
import com.ctre.CANTalon; 

import edu.wpi.first.wpilibj.RobotDrive;

// END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=IMPORTS
import edu.wpi.first.wpilibj.livewindow.LiveWindow;

/**
 * The RobotMap is a mapping from the ports sensors and actuators are wired into
 * to a variable name. This provides flexibility changing wiring, makes checking
 * the wiring easier and significantly reduces the number of magic numbers
 * floating around.
 */
public class RobotMap {
    // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=DECLARATIONS
    public static CANTalon driveFL_Motor;
    public static CANTalon driveFR_Motor;
    public static CANTalon driveRL_Motor;
    public static CANTalon driveRR_Motor;
    public static RobotDrive driveMechDrive;
    public static CANTalon gearMotor;
    public static CANTalon collectMotor;
    public static CANTalon helixMotorRight;
    public static CANTalon helixMotorLeft;
    public static CANTalon shootMotor;
    

    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=DECLARATIONS

    public static void init() {
        // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CONSTRUCTORS
        driveFL_Motor = new CANTalon(4);
        LiveWindow.addActuator("Drive", "FL_Motor", driveFL_Motor);
        
        driveFR_Motor = new CANTalon(5);
        LiveWindow.addActuator("Drive", "FR_Motor", driveFR_Motor);
        
        driveRL_Motor = new CANTalon(6);
        LiveWindow.addActuator("Drive", "RL_Motor", driveRL_Motor);
        
        driveRR_Motor = new CANTalon(7);
        LiveWindow.addActuator("Drive", "RR_Motor", driveRR_Motor);
        
        driveMechDrive = new RobotDrive(driveFL_Motor, driveRL_Motor,
              driveFR_Motor, driveRR_Motor);
        
        driveMechDrive.setSafetyEnabled(true);
        driveMechDrive.setExpiration(0.5);
        driveMechDrive.setSensitivity(0.5);
        driveMechDrive.setMaxOutput(1.0);

        gearMotor = new CANTalon(8);
        LiveWindow.addActuator("Gear", "gearMotor", gearMotor);
        
        collectMotor = new CANTalon(12);
        LiveWindow.addActuator("Collect", "collectMotor", collectMotor);
        
        helixMotorRight = new CANTalon(11);
        helixMotorLeft = new CANTalon(10);
        shootMotor = new CANTalon(9); 
        
        // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CONSTRUCTORS
    }
}
