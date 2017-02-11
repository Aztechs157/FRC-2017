package org.usfirst.frc157.ProtoBot2017.commands;

import edu.wpi.first.wpilibj.command.Command;

/**
 *
 */
public class AutoMoveToGear extends Command {

	private enum State
	{
		MOVE_TO_DROPOFF,
		ALIGN_ON_DROPOFF,
		ELIMINATE_CROSSTRACK
	}
	
	State state;
	
    public AutoMoveToGear() {
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    	state = State.ELIMINATE_CROSSTRACK;
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    	// crab to eliminate crosstrack
    	// turn to align on gear dropoff
    	// move forward to gear dropoff (maintain alignment)
    	//   (Drop back to eliminating crosstrack if our motion increases it) 	
    	switch(state)
    	{
    	case ELIMINATE_CROSSTRACK:
    		state = State.ALIGN_ON_DROPOFF;       // if crosstrack error is lowered enough
    		break;
    	case ALIGN_ON_DROPOFF:
    		state = State.MOVE_TO_DROPOFF;        // if Aligned well enough
    		state = State.ELIMINATE_CROSSTRACK;   // if crosstrack error gets high enough
    		break;
    	case MOVE_TO_DROPOFF:
    		state = State.ELIMINATE_CROSSTRACK;   // if crosstrack error gets high enough
    		state = State.ALIGN_ON_DROPOFF;       // if alignment goes off
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
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    }
}
