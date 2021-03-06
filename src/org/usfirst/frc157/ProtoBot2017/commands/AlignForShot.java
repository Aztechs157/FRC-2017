/*package org.usfirst.frc157.ProtoBot2017.commands;

import org.opencv.core.Point;
import org.usfirst.frc157.ProtoBot2017.Robot;
import org.usfirst.frc157.ProtoBot2017.subsystems.Drive.DriveMode;
import org.usfirst.frc157.ProtoBot2017.subsystems.Vision;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/+*
 *
 /+
public class AlignForShot extends Command {

	private final double PID_P = 0.05;  //note currently no I or D
	
	public enum ShotRangeCommand
	{
		NEAR,
		FAR,
		AUTO
	}
	
	public enum AcquisitionType
	{
		LEFT,
		RIGHT,
		AUTO,
		NONE
	}
	
	private enum TargettingState
	{
		ROTATE,
		RANGE,
		ACQUIRE,
		STOP
	}
	
	ShotRangeCommand selectedRange;
	Point spot;
	DriveMode preCommandDriveMode;

	AcquisitionType acquisitionType;
	
	Vision.VisionTarget lastTarget;

	public AlignForShot(ShotRangeCommand inSelectedRange) {
    	selectedRange = inSelectedRange;
        // Use requires() here to declare subsystem dependencies
        requires(Robot.drive);
        acquisitionType = AcquisitionType.NONE;
    }

    public AlignForShot(ShotRangeCommand inSelectedRange, AcquisitionType acquisitionType) {
    	selectedRange = inSelectedRange;
        // Use requires() here to declare subsystem dependencies
        requires(Robot.drive);
        this.acquisitionType = acquisitionType;
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    	System.out.println("Starting AlignForShot(" + selectedRange.toString() + ")");
    	
    	switch(selectedRange)
    	{
    	case NEAR:
    	{
        	Robot.vision.setVisionMode(Vision.VisionMode.FIND_BOILER, Vision.BoilerRange.NEAR);
        	spot = Robot.vision.getBoilerTargetCenter(Vision.BoilerRange.NEAR);   		
    	} break;
    	case FAR:
    	{
        	Robot.vision.setVisionMode(Vision.VisionMode.FIND_BOILER, Vision.BoilerRange.FAR);
        	spot = Robot.vision.getBoilerTargetCenter(Vision.BoilerRange.FAR);   		    		
    	} break;
    	case AUTO:
    	{
        	Robot.vision.setVisionMode(Vision.VisionMode.FIND_BOILER, Robot.vision.getBoilerTargetRange());
        	spot = Robot.vision.getBoilerTargetCenter(Robot.vision.getBoilerTargetRange());   		
    	} break;
    	}

    	preCommandDriveMode = Robot.drive.getDriveMode();
    	Robot.vision.storePictures();
    	Robot.drive.setDriveMode(DriveMode.ROBOT_RELATIVE);
    	state = TargettingState.STOP;
    	
    	lastTarget = Robot.vision.getTarget();
		SmartDashboard.putString("state", state.toString());

    }

    private static double ROTATION_TOLERANCE = 3;
    private static double ROT_FRACTION = 0.0015;
    private static double ROT_SPEED = 0.125;

    private static double SHORT_ROT_LIMIT = 0.05; // rotation times less than this get more oomph
    private static double SHORT_ROT_BOOST = 2.0;  // how much more oomph...
    
    private static double DISTANCE_TOLERANCE = 3;
    private static double DIST_FRACTION = 0.015;
    private static double DIST_SPEED = 0.15;

    private static double ACQ_SPEED = 0.2;
    
    private double dRot;
    private double rotTime;
    
    private double dDist;
    private double distTime;
    
    private double acquireTime;
    
    private TargettingState state;
    double stateChangeTime;
    
    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    	Vision.VisionTarget target = Robot.vision.getTarget();

    	if((target.loopCount != lastTarget.loopCount) && 
    			(target.inRange == true) && 
    			((state == TargettingState.STOP) || (state == TargettingState.ACQUIRE)))
    	{
    		// Got new target update
    		
    		// sort out how far to move this time
    		dRot = spot.x - target.x;
    		dDist = spot.y - target.y;
    		    		
//    		dRot = -dRot;
    		
    		if(Math.abs(dRot) >= ROTATION_TOLERANCE)
    		{
    			state = TargettingState.ROTATE;
    			rotTime = Math.abs(dRot * ROT_FRACTION);
    		    stateChangeTime = Timer.getFPGATimestamp();
    		}
    		else if(Math.abs(dDist) > DISTANCE_TOLERANCE)
    		{
    			state = TargettingState.RANGE;
    			distTime = Math.abs(dDist * DIST_FRACTION);
    		    stateChangeTime = Timer.getFPGATimestamp();
    		}
    		else
    		{
    	   		state = TargettingState.STOP;    		
    		    stateChangeTime = Timer.getFPGATimestamp();
    		}
    		
    		lastTarget = target;
    	}
    	else if(target.inRange == false)
    	{
    		// new target is not found
    		state = TargettingState.ACQUIRE;
		    stateChangeTime = Timer.getFPGATimestamp();
    	}
    	else
    	{
    		// work with old target
    		//   so no changes to anything
     	}

		SmartDashboard.putDouble("dRot", dRot);
		SmartDashboard.putDouble("dDist", dDist);
		SmartDashboard.putDouble("distTime", distTime);
		SmartDashboard.putDouble("rotTime", rotTime);
		SmartDashboard.putString("state", state.toString());
		
		SmartDashboard.putDouble("stateChangeTime", stateChangeTime);
		SmartDashboard.putDouble("rotTime+stateChangeTime", stateChangeTime+rotTime);
		SmartDashboard.putDouble("NOW", Timer.getFPGATimestamp());
		

    	
    	switch(state)
    	{
    	case ROTATE:
    	{
    		if(Timer.getFPGATimestamp() < (stateChangeTime + rotTime))
    		{
    			double rotSpeed = ROT_SPEED;
    			if(rotTime < SHORT_ROT_LIMIT)
    			{
    				rotSpeed = SHORT_ROT_BOOST * ROT_SPEED;
    			}
 				Robot.drive.driveBot(0, 0, ROT_SPEED * Math.signum(dRot));
    		}
    		else
    		{
    	   		state = TargettingState.STOP;    		
    		    stateChangeTime = Timer.getFPGATimestamp();    			
    		}
    	} break;
    	case RANGE:
    	{
    		if(Timer.getFPGATimestamp() < (stateChangeTime + distTime))
    		{
    			Robot.drive.driveBot(0, DIST_SPEED * Math.signum(dDist), 0);    			
    		}
    		else
    		{
    	   		state = TargettingState.STOP;    		
    		    stateChangeTime = Timer.getFPGATimestamp();    			
    		}
    	} break;
    	case ACQUIRE:
    	{
    		double cmdRot = ACQ_SPEED;
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
   			if(target.inRange == true)
   			{
//   	   	   		state = TargettingState.STOP;    		
//    		    stateChangeTime = Timer.getFPGATimestamp();    			   				
   			}
    	} break;
    	case STOP:
    	{
    		Robot.drive.driveBot(0, 0, 0);  // stop - consider setting brake mode on for this    		
    	} break;
    	}
    	
     }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        return false;
    }

    // Called once after isFinished returns true
    protected void end() {
//    	state = TargettingState.STOP;    		
//    	Robot.drive.driveBot(0, 0, 0);  // stop - consider setting brake mode on for this    		
//    	Robot.drive.setDriveMode(preCommandDriveMode);
//    	System.out.println("AlignForShot.end()");
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
//    	state = TargettingState.STOP;    		
//    	Robot.drive.driveBot(0, 0, 0);  // stop - consider setting brake mode on for this    		
//    	Robot.drive.setDriveMode(preCommandDriveMode);
//    	System.out.println("AlignForShot.interrupted()");
    }
}
*/