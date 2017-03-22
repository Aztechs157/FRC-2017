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

import org.usfirst.frc157.ProtoBot2017.RobotMap;  
import org.usfirst.frc157.ProtoBot2017.commands.*;
import com.ctre.CANTalon;

import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Sendable;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.DigitalInput;


/**
 *
 */
public class GearPickup extends Subsystem {

    private final double upSPEED  = 0.5;
    private final double downSPEED  = 0.5;
    private final CANTalon gearPickupMotor = RobotMap.gearPickupMotor;
 

    // Put methods for controlling this subsystem
    // here. Call these from Commands.

    // Limit Switches
    protected DigitalInput upLimitSwitch;
    protected DigitalInput downLimitSwitch;


    public GearPickup()
    {
        System.out.println("Gearpickup: Gearpickup()");
        upLimitSwitch = new DigitalInput(8); 
        downLimitSwitch = new DigitalInput(9); 
      
        
        gearPickupMotor.changeControlMode(CANTalon.TalonControlMode.PercentVbus);
        gearPickupMotor.changeControlMode(CANTalon.TalonControlMode.PercentVbus);

    }        
   
    

    public boolean isUp() 
    {
        boolean testUp;
      
        boolean answer;
     //   System.out.println("Gear: isopen()");
        testUp= upLimitSwitch.get();
       
      
        if (testUp == true  )
        {
           answer = true;
           gearPickupMotor.set(0);
          // System.out.println("is Up: true");
        }
        else
        {
            answer = false;
            gearPickupMotor.set(downSPEED);
            //System.out.println("is open: false");
        }
        return answer;
    }
    public boolean isDown()
    {
        boolean testDown;
        boolean awnser;
        testDown=downLimitSwitch.get();
        if(testDown==true)
        {
            awnser = true;
            gearPickupMotor.set(0);
        }
        else
        {
            awnser=false;
            gearPickupMotor.set(upSPEED);
        }
            
        
        return awnser;
    
    }
    public void moveUp()
    {
        if (isUp()==true)
        {
            gearPickupMotor.set(0);
        }
        else 
        {
            gearPickupMotor.set(upSPEED);
        }
        
        
    }
    public void moveDown()
    {
        if (isDown()==true)
        {
            gearPickupMotor.set(0);
        }
        else 
        {
            gearPickupMotor.set(downSPEED);
        }
        
    }



    @Override
    protected void initDefaultCommand()
    {
        // TODO Auto-generated method stub
        
    }
}
    