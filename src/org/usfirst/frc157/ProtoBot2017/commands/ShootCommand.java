// RobotBuilder Version: 2.0
//
// This file was generated by RobotBuilder. It contains sections of
// code that are automatically generated and assigned by robotbuilder.
// These sections will be updated in the future when you export to
// Java from RobotBuilder. Do not put any code or make any change in
// the blocks indicating autogenerated code or it will be lost on an
// update. Deleting the comments indicating the section will prevent
// it from being updated in the future.


package org.usfirst.frc157.ProtoBot2017.commands;
import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import org.usfirst.frc157.ProtoBot2017.Robot;
import org.usfirst.frc157.ProtoBot2017.subsystems.Shoot;

/**
 *
 */
public class ShootCommand extends Command {
    
    private Shoot.ShootCommand shotPower; 

    private boolean finished = false;
    
    double speedTimeoutTime;
    final double INITIAL_SPINUP_TIMEOUT_SEC = 2.0;
    final double RESHOOT_SPINUP_TIMEOUT_SEC = 0.5;
    
    double pulseStartTime;
    final double GATE_PULSE_OPEN_TIME = 0.200; // 200 ms
    final double GATE_PULSE_CLOSE_TIME = 0.200; // 200 ms
    
    private enum ShotCommandState 
    {
    	SPINNING_UP,
    	PULSING_LEFT_GATE,
    	PULSING_RIGHT_GATE,
    }
    
    ShotCommandState state;
    
    private enum PulseState
    {
    	WAIT_OPENED,
    	WAIT_CLOSED,
    	WAIT_SPINUP
    }
    
    PulseState pulseState;
    
    public ShootCommand(Shoot.ShootCommand shotPower) {
    	this.shotPower = shotPower;
        requires(Robot.shoot);
        
    }
    
    // Called just before this Command runs the first time
    protected void initialize() {
        System.out.println("********************    ShootCommandinitialize(" + shotPower + ") " + Timer.getFPGATimestamp());
        finished = false;
        state = ShotCommandState.SPINNING_UP;
        
        // Start the shooter & helixes (helii?)
        shootAtPowerLevel(shotPower);
		Robot.helix.idleLeft();
		Robot.helix.idleRight();
		speedTimeoutTime = Timer.getFPGATimestamp() + INITIAL_SPINUP_TIMEOUT_SEC;
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
//    	System.out.println("shooter: execute(" + shotPower + ") state = " + finished);
//		System.out.print("ShootCommand - " + state);
		
    	switch(state)
    	{
    	case SPINNING_UP:
    	{
    		if(shooterAtSpeed() || (Timer.getFPGATimestamp() > speedTimeoutTime))
    		{
    			System.out.println("ShootCommand.execute(" + shotPower + ") - " + Timer.getFPGATimestamp() + state);
    			// if we are at speed or if we have taken too long to spinup start shooting with Left Gate
    			Robot.gateLeft.open();
    			state = ShotCommandState.PULSING_LEFT_GATE;
    			pulseState = PulseState.WAIT_OPENED;
    			pulseStartTime = Timer.getFPGATimestamp();
    			System.out.println("ShootCommand.execute(" + shotPower + ") - " + Timer.getFPGATimestamp() + state);
    		}
    	} break;
    	case PULSING_LEFT_GATE:
    	{
    		switch(pulseState)
    		{
    		case WAIT_OPENED:
    		{
    			// wait for opened time to pass
    			if(Timer.getFPGATimestamp() > (pulseStartTime + GATE_PULSE_OPEN_TIME))
    			{
        			System.out.println("ShootCommand.execute(" + shotPower + ") - " + Timer.getFPGATimestamp() + state + " " + pulseState);
    				// then open the gate and switch to the opened state
    				Robot.gateLeft.close();
    				pulseState = PulseState.WAIT_CLOSED;
    				pulseStartTime = Timer.getFPGATimestamp();
        			System.out.println("ShootCommand.execute(" + shotPower + ") - " + Timer.getFPGATimestamp() + state + " " + pulseState);
    			}
    		} break;
    		case WAIT_CLOSED:
    		{
    			// wait for closed time to pass
    			if(Timer.getFPGATimestamp() > (pulseStartTime + GATE_PULSE_OPEN_TIME))
    			{
        			System.out.println("ShootCommand.execute(" + shotPower + ") - " + Timer.getFPGATimestamp() + state + " " + pulseState);
    				// then make sure shooter is spun back up
    				speedTimeoutTime = Timer.getFPGATimestamp() + RESHOOT_SPINUP_TIMEOUT_SEC;
    				pulseState = PulseState.WAIT_SPINUP;
        			System.out.println("ShootCommand.execute(" + shotPower + ") - " + Timer.getFPGATimestamp() + state + " " + pulseState);
   			}    			
    		} break;    		
    		case WAIT_SPINUP:
    		{
    			// then open the other gate and switch to the opened state
    			if(shooterAtSpeed() || (Timer.getFPGATimestamp() > speedTimeoutTime))
    			{
        			System.out.println("ShootCommand.execute(" + shotPower + ") - " + Timer.getFPGATimestamp() + state + " " + pulseState);
    				Robot.gateRight.open();
    				state = ShotCommandState.PULSING_RIGHT_GATE;
    				pulseState = PulseState.WAIT_OPENED;
    				pulseStartTime = Timer.getFPGATimestamp();
        			System.out.println("ShootCommand.execute(" + shotPower + ") - " + Timer.getFPGATimestamp() + state + " " + pulseState);
    			} break;
    		}
    		}
    	} break;
    	case PULSING_RIGHT_GATE:
    	{
    		switch(pulseState)
    		{
    		case WAIT_OPENED:
    		{
    			// wait for opened time to pass
    			if(Timer.getFPGATimestamp() > (pulseStartTime + GATE_PULSE_OPEN_TIME))
    			{
        			System.out.println("ShootCommand.execute(" + shotPower + ") - " + Timer.getFPGATimestamp() + state + " " + pulseState);
    				// then open the gate and switch to the opened state
    				Robot.gateRight.close();
    				pulseState = PulseState.WAIT_CLOSED;
    				pulseStartTime = Timer.getFPGATimestamp();
        			System.out.println("ShootCommand.execute(" + shotPower + ") - " + Timer.getFPGATimestamp() + state + " " + pulseState);
    			}
    		} break;
       		case WAIT_CLOSED:
    		{
    			// wait for closed time to pass
    			if(Timer.getFPGATimestamp() > (pulseStartTime + GATE_PULSE_OPEN_TIME))
    			{
        			System.out.println("ShootCommand.execute(" + shotPower + ") - " + Timer.getFPGATimestamp() + state + " " + pulseState);
    				// then make sure shooter is spun back up
    				speedTimeoutTime = Timer.getFPGATimestamp() + RESHOOT_SPINUP_TIMEOUT_SEC;
    				pulseState = PulseState.WAIT_SPINUP;
        			System.out.println("ShootCommand.execute(" + shotPower + ") - " + Timer.getFPGATimestamp() + state + " " + pulseState);
    			}    			
    		} break;    		
    		case WAIT_SPINUP:
    		{
    			// then open the other gate and switch to the opened state
    			if(shooterAtSpeed() || (Timer.getFPGATimestamp() > speedTimeoutTime))
    			{
        			System.out.println("ShootCommand.execute(" + shotPower + ") - " + Timer.getFPGATimestamp() + state + " " + pulseState);
    				Robot.gateLeft.open();
    				state = ShotCommandState.PULSING_LEFT_GATE;
    				pulseState = PulseState.WAIT_OPENED;
    				pulseStartTime = Timer.getFPGATimestamp();
        			System.out.println("ShootCommand.execute(" + shotPower + ") - " + Timer.getFPGATimestamp() + state + " " + pulseState);
    			} break;
    		}
    		}
    	} break;
    	}
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        return  finished;
    }

    // Called once after isFinished returns true
    protected void end() {
        stopShooter();
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
        stopShooter();
    }
    
    private void stopShooter()
    {
    	Robot.shoot.idle();
		Robot.helix.idleLeft();
		Robot.helix.idleRight();
		Robot.gateLeft.close();
		Robot.gateRight.close();
    }
    
    private void shootAtPowerLevel(Shoot.ShootCommand powerLevel)
    {
        if (powerLevel == Shoot.ShootCommand.FAR )
        {
            Robot.shoot.far();
        }
        else if (powerLevel == Shoot.ShootCommand.NEAR)
        {
            Robot.shoot.near();
        }
        else if(powerLevel == Shoot.ShootCommand.IDLE)
        {
            Robot.shoot.idle();
        }
    }
    
    private boolean shooterAtSpeed()
    {
    	System.out.println("FIXME FIXME FIXME - Not using Robot.shoot.atSpeed()");
    	return true;
    }
    
}

