package org.usfirst.frc157.ProtoBot2017.commands;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.GenericHID.RumbleType;
import edu.wpi.first.wpilibj.Timer;
import org.usfirst.frc157.ProtoBot2017.Robot;
import org.usfirst.frc157.ProtoBot2017.subsystems.Gear;
import org.usfirst.frc157.ProtoBot2017.subsystems.GearBridge;

public class GearBridgeStateCommand extends Command {
    
    private final GearBridge GEARBRIDGE = Robot.gearBridge;
    private double waitSeconds = 0.5;
    private double timeStart;

    private boolean finished = false;
    
    // states
    public enum GearBridgeState
    {
        OPENING,
        CLOSING,
        WAITING,
   
    }
    
    
    private GearBridgeState currentState;
    
    public GearBridgeStateCommand() {
        //this.waitSeconds = waitSeconds;
        requires(Robot.gear);
    }
    
    // currentState set
    public void setState(GearBridgeState v) {
        currentState = v;
        if (v == GearBridgeState.WAITING ) {
            
        }
        finished = false;
        System.out.println("Set gear state to: " + v.toString());
    }
    
    // currentState get
    public GearBridgeState getState() {
        return currentState;
    }
    
    // Called just before this Command runs the first time
    @Override
    protected void initialize() {
        System.out.println("GearStateCommand: initialize()");
        finished = false;

        if(GEARBRIDGE.isOpen())
        {
        	currentState = GearBridgeState.CLOSING;
        	GEARBRIDGE.close();
        	setState(GearBridgeState.CLOSING);

        }
        else if(GEARBRIDGE.isClosed())
        {
        	currentState = GearBridgeState.OPENING;
        	GEARBRIDGE.open();
        	setState(GearBridgeState.OPENING);

        }
        else
        {
        	currentState = GearBridgeState.OPENING;
        	GEARBRIDGE.open();
        	setState(GearBridgeState.OPENING);
        }
    }

    // Called repeatedly when this Command is scheduled to run
    @Override
    protected void execute() {
//        System.out.println("GearStateCommand: execute()");
        switch (currentState) {
            
            case OPENING: 
                if (GEARBRIDGE.isOpen()) {
                    setState(GearBridgeState.WAITING);
                	System.out.println("GearStateCommand: Open Gear Holder");
                	Robot.oi.driver.setRumble(RumbleType.kLeftRumble, 1.0);
                	Robot.oi.driver.setRumble(RumbleType.kRightRumble, 1.0);
                } else {
                	GEARBRIDGE.open();
                }
                break;
                
            case WAITING:
                if (Timer.getFPGATimestamp() >= (timeStart + waitSeconds)) {
                    System.out.println("GearStateCommand: Close Gear Holder");
                    setState(GearBridgeState.CLOSING);
                } else {
                    GEARBRIDGE.idle();
                }
                
               break;
                
                
            case CLOSING:
                if (GEARBRIDGE.isClosed()) {
                    finished = true;
                	System.out.println("GearStateCommand: Gear Holder CLOSED");
                	Robot.oi.driver.setRumble(RumbleType.kLeftRumble, 0.0);
                	Robot.oi.driver.setRumble(RumbleType.kRightRumble, 0.0);
                } else {
                    GEARBRIDGE.close();
                }
                break;
        }
    }

    // Make this return true when this Command no longer needs to run execute()
    @Override
    protected boolean isFinished() {
//        System.out.println("GearStateCommand: finished()");
        return  finished;
    }

    // Called once after isFinished returns true
    @Override
    protected void end() {
        System.out.println("GearStateCommand: end()");
        GEARBRIDGE.idle();
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    @Override
    protected void interrupted() {
        System.out.println("GearStateCommand: interrupted()");
        GEARBRIDGE.idle();
    }
}