/*package org.usfirst.frc157.ProtoBot2017.commands;

import org.usfirst.frc157.ProtoBot2017.Robot;
import org.usfirst.frc157.ProtoBot2017.subsystems.Vision;
import org.usfirst.frc157.ProtoBot2017.subsystems.Vision.CameraSelection;
import org.usfirst.frc157.ProtoBot2017.subsystems.Vision.VisionMode;

import edu.wpi.first.wpilibj.command.Command;

/+*
 *
 -/
public class SetCamera extends Command {

    public SetCamera() {
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    	Vision.CameraSelection cam = Robot.vision.getCamera();
    		
    	// Walk through the vision modes (target types)
    	switch(cam)
    	{
    	case GEAR_CAMERA:
    		Robot.vision.setCamera(CameraSelection.SHOT_CAMERA);
    		break;
    	case SHOT_CAMERA:
    		Robot.vision.setCamera(CameraSelection.GEAR_CAMERA);
    		break;
    	default:
    		Robot.vision.setCamera(CameraSelection.SHOT_CAMERA);
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
*/