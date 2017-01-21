package org.usfirst.frc157.ProtoBot2017.commands;

import org.usfirst.frc157.ProtoBot2017.Robot;
import org.usfirst.frc157.ProtoBot2017.subsystems.Vision;
import org.usfirst.frc157.ProtoBot2017.subsystems.Vision.VisionMode;

import edu.wpi.first.wpilibj.command.Command;

/**
 *
 */
public class SetCameraTarget extends Command {

    public SetCameraTarget() {
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    	Vision.VisionMode mode = Robot.vision.getVisionMode();
    		
    	// Walk through the vision modes (target types)
    	switch(mode)
    	{
    	case FIND_BOILER:
    		Robot.vision.setVisionMode(VisionMode.FIND_GEAR);
    		break;
    	case FIND_GEAR:
    		Robot.vision.setVisionMode(VisionMode.PASSTHROUGH);
    		break;
    	case PASSTHROUGH:
    		Robot.vision.setVisionMode(VisionMode.FIND_BOILER);
    		break;
    	default:
    		Robot.vision.setVisionMode(VisionMode.PASSTHROUGH);
    		break;
    	}
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        return true;
    }

    // Called once after isFinished returns true
    protected void end() {
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    }
}
