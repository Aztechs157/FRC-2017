package org.usfirst.frc157.ProtoBot2017.commands;

import org.opencv.core.Point;
import org.usfirst.frc157.ProtoBot2017.Robot;
import org.usfirst.frc157.ProtoBot2017.subsystems.Vision;

import edu.wpi.first.wpilibj.command.Command;

/**
 *
 */
public class AutoMoveToGear extends Command {

	private final static double  CROSSTRACK_TOLERANCE = 25.0;
	private final static double  DROPOFF_ALIGNMENT_TOLERANCE = 25;
	
	private final static double  GEARDROP_SPEED = 0.25;

	private final double PID_P = 0.05;  //note currently no I or D

	private enum State
	{
		MOVE_TO_DROPOFF,
		ALIGN_ON_DROPOFF,
		ELIMINATE_CROSSTRACK,
		COMPLETE
	}
	
	State state;
	
	Point spot;

    public AutoMoveToGear() {
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    	Robot.vision.storePictures();
    	state = State.ELIMINATE_CROSSTRACK;
    	spot = Robot.vision.getGearTargetCenter();
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    	// get the targeting information
    	Vision.VisionTarget target = Robot.vision.getTarget();

    	// crab to eliminate crosstrack
    	// turn to align on gear dropoff
    	// move forward to gear dropoff (maintain alignment)
    	//   (Drop back to eliminating crosstrack if our motion increases it) 	
    	switch(state)
    	{
    	case ELIMINATE_CROSSTRACK:
    		Robot.drive.driveBot(0, target.crossTrack * PID_P, 0);
    		if(Math.abs(target.crossTrack) < CROSSTRACK_TOLERANCE)  // vision 0s crosstrack when it is small
    		{
    			state = State.ALIGN_ON_DROPOFF;       // if crosstrack error is lowered enough
    		}
    		break;
    	case ALIGN_ON_DROPOFF:
    		double alignmentError = target.x - spot.x;
    		Robot.drive.driveBot(0, 0, alignmentError * PID_P);
    		if(Math.abs(target.crossTrack) > CROSSTRACK_TOLERANCE)  // if crosstrack has gotten too high
    		{
    			state = State.ELIMINATE_CROSSTRACK;   // if crosstrack error gets high enough
    		}
    		else if(Math.abs(alignmentError) < DROPOFF_ALIGNMENT_TOLERANCE)
    		{
    			state = State.MOVE_TO_DROPOFF;        // if Aligned well enough
    		}
    		break;
    	case MOVE_TO_DROPOFF:
    		Robot.drive.driveBot(0, GEARDROP_SPEED, 0);
    		if(Math.abs(target.crossTrack) > CROSSTRACK_TOLERANCE)  // if crosstrack has gotten too high
    		{
    			state = State.ELIMINATE_CROSSTRACK;   // if crosstrack error gets high enough
    		}
    		else if(Math.abs(target.x - spot.x) > DROPOFF_ALIGNMENT_TOLERANCE)
    		{
    			state = State.ALIGN_ON_DROPOFF;       // if alignment goes off
    		} else if (target.recentTarget == false)  // if we lose track of the target declarc complete and move forward
    		{
    		state = State.COMPLETE;               // if reach the dropoff point
    		}
    		break;
    	case COMPLETE:
    		// drive forward blindly until the command ends
    		Robot.drive.driveBot(0, GEARDROP_SPEED, 0);
    		break;
    	default:
    		break;
    	}
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        return false;
    }

    // Called once after isFinished returns true
    protected void end() {
    	Robot.drive.driveBot(0, 0, 0);
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    	Robot.drive.driveBot(0, 0, 0);
    }
}
