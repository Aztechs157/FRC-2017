// RobotBuilder Version: 2.0
//
// This file was generated by RobotBuilder. It contains sections of
// code that are automatically generated and assigned by robotbuilder.
// These sections will be updated in the future when you export to
// Java from RobotBuilder. Do not put any code or make any change in
// the blocks indicating autogenerated code or it will be lost on an
// update. Deleting the comments indicating the section will prevent
// it from being updated in the future.


package org.usfirst.frc157.ProtoBot2017.commands;
import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import org.usfirst.frc157.ProtoBot2017.Robot;
import org.usfirst.frc157.ProtoBot2017.subsystems.Gear;

/**
 *
 */
public class OperatorGear extends Command {
    
    private Gear.GearCommand gearCommand;

    private boolean finished = false;
    
    public OperatorGear(Gear.GearCommand command) {
        gearCommand = command;
        requires(Robot.gear);
    }
    
    // Called just before this Command runs the first time
    protected void initialize() {
        System.out.println("OperatorGear: initialize()");
        finished = false;
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
        System.out.println("OperatorGear: execute(), Command: " + gearCommand);
        if (gearCommand == Gear.GearCommand.OPEN) {
            if (Robot.gear.isOpen())
            {
                Robot.gear.idle();
                finished = true;
            }
            else
            {
                Robot.gear.open();
            }
        }
        else if (gearCommand == Gear.GearCommand.CLOSE)
        {
            if (Robot.gear.isClosed())
            {
                Robot.gear.idle();
                finished = true;
            }
            else
            {
                Robot.gear.close();
            }
        }
        else if (gearCommand == Gear.GearCommand.IDLE)
        {
            Robot.gear.idle();
            finished = true;
        }
        else
        {
            Robot.gear.idle();
            finished = true;
            System.out.println("GearCommand undefined!");
        }
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        System.out.println("OperatorGear: finished()");
        return  finished;
    }

    // Called once after isFinished returns true
    protected void end() {
        System.out.println("OperatorGear: end()");
        Robot.gear.idle();
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
        System.out.println("OperatorGear: interrupted()");
        Robot.gear.idle();
    }
}

;