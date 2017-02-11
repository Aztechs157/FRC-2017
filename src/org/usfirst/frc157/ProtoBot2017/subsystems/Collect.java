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
import com.ctre.CANTalon;

import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.DigitalInput;


/**
 *
 */
public class Collect extends Subsystem {

    private final CANTalon collectMotor = RobotMap.collectMotor;

    // Put methods for controlling this subsystem
    // here. Call these from Commands.



        
    public Collect()
    {
        System.out.println("Collect: Collect()");
        
        collectMotor.changeControlMode(CANTalon.TalonControlMode.PercentVbus);
    }        
   
    
    public void unload()
    {
        collectMotor.set(-0.3);
    }

    public void load()
    {
        collectMotor.set(0.3);
        
    }

    public void idle()
    {
        collectMotor.set(0.0);        
    }

    
    @Override
    protected void initDefaultCommand()
    {
        // TODO Auto-generated method stub
        
    }
}
    