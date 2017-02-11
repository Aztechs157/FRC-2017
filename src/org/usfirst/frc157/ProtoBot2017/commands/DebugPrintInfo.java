package org.usfirst.frc157.ProtoBot2017.commands;

import java.time.LocalDateTime;

import edu.wpi.first.wpilibj.command.Command;

/**
 *
 */
public class DebugPrintInfo extends Command {

	public enum DebugSelection
	{
		DATE_TEME,
		NOPRINT
	}
	
	private String baseString;
    private DebugSelection toPrint;
	
	public DebugPrintInfo(String inBaseString, DebugSelection inToPrint) {
    	baseString = inBaseString;
    	toPrint = inToPrint;
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    	System.out.println(baseString + ":");
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    	switch(toPrint)
    	{
    	case NOPRINT:
    		System.out.println("Nothing is Printed Here");
    		break;
    	case DATE_TEME:
    		System.out.println("Now is " + LocalDateTime.now());
    		break;
    	default:
    		System.out.println("ERROR - DebugPrintInfo - Unsupported DebugSelection");
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
