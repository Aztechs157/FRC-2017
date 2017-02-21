package org.usfirst.frc157.ProtoBot2017.commands;

import org.usfirst.frc157.ProtoBot2017.Robot;

import edu.wpi.first.wpilibj.command.Command;

/**
 *
 */
public class ToggleCollection extends Command {

	public enum State
	{
		ACTIVE,
		INACTIVE
	}


	private static State state= State.INACTIVE;
	
	

	public ToggleCollection(State state) {
	
		 requires(Robot.collect);
		 requires(Robot.helix);
		 requires(Robot.leftGate);
		 requires(Robot.rightGate);
	

		// This makes sure first use turns on collection (robot should be inactive at start)
	
	}

	// Called just before this Command runs the first time
	protected void initialize() {
		switch(state)
		{
		case INACTIVE:
		{
			// Set the next state
			state = State.INACTIVE;

			// if we were active - turn things off and go inactive
			Robot.collect.idle();
			Robot.helix.idleLeft();
			Robot.helix.idleRight();    		    		
		//	Robot.leftGate.open();
			//Robot.rightGate.open();
			System.out.println("ToggleCollection - Stop Cllection");
		}
		break;
		case ACTIVE:
		{
			// Set the next state
			state = State.ACTIVE;

			// if we were inactive - turn things on and go active
			Robot.collect.load();
			Robot.helix.loadLeft();
			Robot.helix.loadRight();
			Robot.leftGate.close();
			Robot.rightGate.close();
			System.out.println("ToggleCollection - Start Collection");
		}
		break;
		default:
		{
			System.out.println("ToggleCollection - Illegal Collection State");
		}
		break;
		}
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
	protected void interrupted()
	{
	    
	}
}
