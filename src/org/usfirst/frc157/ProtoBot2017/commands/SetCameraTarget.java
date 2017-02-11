package org.usfirst.frc157.ProtoBot2017.commands;

import org.usfirst.frc157.ProtoBot2017.Robot;
import org.usfirst.frc157.ProtoBot2017.subsystems.Vision;
import org.usfirst.frc157.ProtoBot2017.subsystems.Vision.BoilerRange;
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
			System.out.println("Setting Target Gear");
    		Robot.vision.setVisionMode(VisionMode.FIND_GEAR, Robot.vision.getBoilerTargetRange());
    		break;
    	case FIND_GEAR:
			System.out.println("Setting Target Passthru");
    		Robot.vision.setVisionMode(VisionMode.PASSTHROUGH, Robot.vision.getBoilerTargetRange());
    		break;
    	case PASSTHROUGH:
    		if (Robot.vision.getBoilerTargetRange() == BoilerRange.NEAR)
    		{
    			System.out.println("Setting Target Boiler Far");
    			Robot.vision.setVisionMode(VisionMode.FIND_BOILER, BoilerRange.FAR);
    		}
    		else
    		{
    			System.out.println("Setting Target Boiler Near");
    			Robot.vision.setVisionMode(VisionMode.FIND_BOILER, BoilerRange.NEAR);    			
    		}
    		break;
    	default:
			System.out.println("Setting Target Passthru");
    		Robot.vision.setVisionMode(VisionMode.PASSTHROUGH, Robot.vision.getBoilerTargetRange());
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
