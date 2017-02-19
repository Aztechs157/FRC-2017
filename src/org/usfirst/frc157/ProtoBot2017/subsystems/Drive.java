// RobotBuilder Version: 2.0
//
// This file was generated by RobotBuilder. It contains sections of
// code that are automatically generated and assigned by robotbuilder.
// These sections will be updated in the future when you export to
// Java from RobotBuilder. Do not put any code or make any change in
// the blocks indicating autogenerated code or it will be lost on an
// update. Deleting the comments indicating the section will prevent
// it from being updated in the future.


package org.usfirst.frc157.ProtoBot2017.subsystems;

import org.usfirst.frc157.ProtoBot2017.ADIS16448_IMU;
import org.usfirst.frc157.ProtoBot2017.RobotMap;
import org.usfirst.frc157.ProtoBot2017.commands.*;
import com.ctre.CANTalon;

import edu.wpi.first.wpilibj.CANSpeedController;
import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Sendable;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;


/**
 *
 */
public class Drive extends Subsystem {

    // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CONSTANTS

    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CONSTANTS

    // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=DECLARATIONS
    private final CANTalon fL_Motor = RobotMap.driveFL_Motor;
    private final CANTalon fR_Motor = RobotMap.driveFR_Motor;
    private final CANTalon rL_Motor = RobotMap.driveRL_Motor;
    private final CANTalon rR_Motor = RobotMap.driveRR_Motor;
    private final RobotDrive mechDrive = RobotMap.driveMechDrive;

    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=DECLARATIONS

	private static ADIS16448_IMU imu;
	private double initialHeading;
    
    // Put methods for controlling this subsystem
    // here. Call these from Commands.
    
    public enum DriveMode
    {
    	FIELD_RELATIVE,
    	ROBOT_RELATIVE
    }

    private DriveMode robotDriveMode = DriveMode.ROBOT_RELATIVE;
   
    public void setDriveMode(DriveMode mode)
    {
    	robotDriveMode = mode;
    	SmartDashboard.putString("DriveMode", mode.name());    	
    }
    
    public DriveMode getDriveMode()
    {
    	return robotDriveMode;
    }
    
    public void driveBot(double x, double y, double rotation)
    {
    	double gyroAngle = 0;
    	
    	if(robotDriveMode == DriveMode.ROBOT_RELATIVE)
    	{
    		gyroAngle = 0;                   // straight robot relative
    	}
    	else
    	{
    		gyroAngle = getZeroedHeading();  // field relative zeroing
    	}
    	mechDrive.mecanumDrive_Cartesian(x, y, rotation, gyroAngle);
    }
    
     
    public Drive()
    {
    	if(imu == null)
    	{	
    		imu = new ADIS16448_IMU();
    		imu.calibrate();   		
    		resetZeroHeading();
    		System.out.println("IMU Initialized");
    		LiveWindow.addSensor("Drive", "Gyro", imu);
    		SmartDashboard.putData("DRIVE-IMU", imu);
    	}

    	// set up controllers
    	configureControllers();
    	setDriveMode(DriveMode.ROBOT_RELATIVE);
    	updateHeading();
   }
    
    private void configureControllers()
    {
    	// Set Drive Control Mode ...
    	CANTalon.TalonControlMode controlMode = CANTalon.TalonControlMode.PercentVbus;
    	fL_Motor.changeControlMode(controlMode);
    	fR_Motor.changeControlMode(controlMode);
    	rL_Motor.changeControlMode(controlMode);
    	rR_Motor.changeControlMode(controlMode);    	
    	
    	// Set Break Mode ...
    	boolean brakeMode = false;   // false is coast
    	fL_Motor.enableBrakeMode(brakeMode);
    	fR_Motor.enableBrakeMode(brakeMode);
    	rL_Motor.enableBrakeMode(brakeMode);
    	rR_Motor.enableBrakeMode(brakeMode);    	
    	
    	// Set ramp rate
    	double rampRate = 48.0;  // V/s (note: greater than 12 implies will reach max voltage in less than 1 second) e.g.
    	//  6.0 -> 0 to full speed in 2.0s
    	// 12.0 -> 0 to full speed in 1.0s
    	// 24.0 -> 0 to full speed in 0.5s
    	// 48.0 -> 0 to full speed in 0.25s
    	// 96.0 -> 0 to full speed in 0.125s
    	fL_Motor.setVoltageRampRate(rampRate);
       	fR_Motor.setVoltageRampRate(rampRate);
    	rL_Motor.setVoltageRampRate(rampRate);
    	rR_Motor.setVoltageRampRate(rampRate);   	
    }
    
    public void initDefaultCommand() {
         setDefaultCommand(new OperatorDrive());
    }

    public void resetZeroHeading()
    {
    	initialHeading = imu.getAngle(); 
    }
    
    public double getZeroedHeading()
    {
    	return imu.getYaw() - initialHeading;
    }
    
    public void updateHeading()
    {
		SmartDashboard.putData("DRIVE-IMU", imu);
    }

	public Sendable getImu() {
		// TODO Auto-generated method stub
		return imu;
	}

}


