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

	private VisionMode mode;
	private BoilerRange range;
	
    public SetCameraTarget(VisionMode mode) {
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
    	this.mode = mode;
    	this.range = BoilerRange.NEAR;
    }

    public SetCameraTarget(VisionMode mode, BoilerRange range) {
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
    	this.mode = mode;
    	this.range = range;
    }
    // Called just before this Command runs the first time
    protected void initialize() {
		Robot.vision.setVisionMode(mode, range);
   }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
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
