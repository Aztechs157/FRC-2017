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


	private static State state = State.INACTIVE;
	
	

	public ToggleCollection(State state)
	{
	
	    
		// This makes sure first use turns on collection (robot should be inactive at start)
	
	}

	// Called just before this Command runs the first time
	protected void initialize() 
	{
	    if(state == State.INACTIVE)
	    {
	        state = State.ACTIVE;
            Robot.collect.load();
            Robot.leftHelix.load();
            Robot.rightHelix.load();
            Robot.leftGate.close();
            Robot.rightGate.close();
            System.out.println("ToggleCollection - Start Collection");
	    }
	    else
	    {
	        state = state.INACTIVE;
            // if we were active - turn things off and go inactive
	        Robot.collect.idle();
	        Robot.leftHelix.idle();
	        Robot.rightHelix.idle();                        
	        Robot.leftGate.close();
	        Robot.rightGate.close();
            System.out.println("ToggleCollection - Stop Cllection");
	    }
       // requires(Robot.collect);
        //requires(Robot.helix);
        //requires(Robot.leftGate);
        //requires(Robot.rightGate);
	}

	// Called repeatedly when this Command is scheduled to run
	protected void execute() 
	{
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
