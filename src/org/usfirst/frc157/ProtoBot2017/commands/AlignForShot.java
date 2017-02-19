package org.usfirst.frc157.ProtoBot2017.commands;

import org.opencv.core.Point;
import org.usfirst.frc157.ProtoBot2017.Robot;
import org.usfirst.frc157.ProtoBot2017.subsystems.Drive.DriveMode;
import org.usfirst.frc157.ProtoBot2017.subsystems.Vision;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 *
 */
public class AlignForShot extends Command {

	public enum AcquisitionType
	{
		LEFT,
		RIGHT,
		AUTO,
		NONE
	}
	
	Vision.BoilerRange selectedRange;
	Point spot;
	DriveMode preCommandDriveMode;

	AcquisitionType acquisitionType;
	
    public AlignForShot(Vision.BoilerRange inSelectedRange) {
    	selectedRange = inSelectedRange;
        // Use requires() here to declare subsystem dependencies
        requires(Robot.drive);
        acquisitionType = AcquisitionType.NONE;
        spot = Robot.vision.getBoilerTargetCenter(selectedRange);
    }

    public AlignForShot(Vision.BoilerRange inSelectedRange, AcquisitionType acquisitionType) {
    	selectedRange = inSelectedRange;
        // Use requires() here to declare subsystem dependencies
        requires(Robot.drive);
        this.acquisitionType = acquisitionType;
        spot = Robot.vision.getBoilerTargetCenter(selectedRange);
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    	System.out.println("Starting AlignForShot(" + selectedRange.toString() + ")");
    	Robot.vision.setVisionMode(Vision.VisionMode.FIND_BOILER, selectedRange);
    	preCommandDriveMode = Robot.drive.getDriveMode();
    	Robot.vision.storePictures();
    	Robot.drive.setDriveMode(DriveMode.ROBOT_RELATIVE);
    }



    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    	Vision.VisionTarget target = Robot.vision.getTarget();
    	
    	// if the target isn't visible and acquisition is desired, start turning
    	if((target.inRange == false) &&
    			(acquisitionType != AcquisitionType.NONE))
    	{
    		double cmdRot = 0.125;
    		switch(acquisitionType)
    		{
    		case LEFT:
    		{
    			// turn left
    			cmdRot = cmdRot;
    		}
    		break;
    		case RIGHT:
    		{
    			// turn right
    			cmdRot = -cmdRot;
    		}
    		break;
    		case AUTO:
    		{
    			switch(DriverStation.getInstance().getAlliance())
    			{
    			case Red:
    			{
    				// turn right
        			cmdRot = -cmdRot;
    			} break;
    			case Blue:
    			{
    				//turn left
        			cmdRot = cmdRot;
    			} break;
    			case Invalid:
    			{
    				// it's not valid, but we should do something.
        			// turn left
        			cmdRot = cmdRot;
    			} break;
    			}
    		}
    		break;
    		case NONE:
    		{
    			// Should never get here but turn left at half speed anyway
    			cmdRot = cmdRot/2;
    		}
    		break;
    		}

    		Robot.drive.driveBot(0, 0, cmdRot);
    	}
    	
    	// assuming we can see the target, line up on it
    	if((target.target == Vision.TargetID.BOILER) &&
    			(target.recentTarget == true))
    	{
    		// Figure out Angle Change (X ~ Angle)
    		double dx = target.x - spot.x;    	
    		double cmdRot = (dx/Vision.CAM_WIDTH) * 60;

    		SmartDashboard.putNumber("dx", dx);
    		SmartDashboard.putNumber("cmdRot", cmdRot);
    		// Figure out Range Change (Y ~ Range)
    		double dy = target.y - spot.y;
    		double cmdY = dy/Vision.CAM_HEIGHT;

    		SmartDashboard.putNumber("dy", dy);
    		SmartDashboard.putNumber("cmdY", cmdY);
    		
    		// Apply Change to Drive
    		double cmdX = 0;
    		Robot.drive.driveBot(cmdX, cmdY, cmdRot);
    	}
    	else
    	{
    		Robot.drive.driveBot(0, 0, 0);  // if not good stop    		
    	}
     }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        return false;
    }

    // Called once after isFinished returns true
    protected void end() {
    	Robot.drive.setDriveMode(preCommandDriveMode);
    	System.out.println("AlignForShot.end()");
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    	Robot.drive.setDriveMode(preCommandDriveMode);
    	System.out.println("AlignForShot.interrupted()");
    }
}
