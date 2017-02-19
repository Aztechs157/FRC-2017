package org.usfirst.frc157.ProtoBot2017.commands;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.GenericHID.RumbleType;
import edu.wpi.first.wpilibj.Timer;
import org.usfirst.frc157.ProtoBot2017.Robot;
import org.usfirst.frc157.ProtoBot2017.subsystems.Gear;

public class GearStateCommand extends Command {
    
    private final Gear GEAR = Robot.gear;
    
    private double waitSeconds = 4.0;
    private double timeStart;
    
    private boolean finished = false;
    
    // states
    public enum GearState
    {
        OPENING,
        CLOSING,
        WAITING
    }
    
    private GearState currentState;
    
    public GearStateCommand() {
        //this.waitSeconds = waitSeconds;
        requires(Robot.gear);
    }
    
    // currentState set
    public void setState(GearState v) {
        currentState = v;
        if (v == GearState.WAITING) {
            timeStart = Timer.getFPGATimestamp();
        }
        finished = false;
        System.out.println("Set gear state to: " + v.toString());
    }
    
    // currentState get
    public GearState getState() {
        return currentState;
    }
    
    // Called just before this Command runs the first time
    @Override
    protected void initialize() {
        System.out.println("GearStateCommand: initialize()");
        finished = false;
        currentState = GearState.OPENING;
        setState(GearState.OPENING);
    }

    // Called repeatedly when this Command is scheduled to run
    @Override
    protected void execute() {
//        System.out.println("GearStateCommand: execute()");
        switch (currentState) {
            
            case OPENING: 
                if (GEAR.isOpen()) {
                    setState(GearState.WAITING);
                	System.out.println("GearStateCommand: Open Gear Holder");
                	Robot.oi.driver.setRumble(RumbleType.kLeftRumble, 1.0);
                	Robot.oi.driver.setRumble(RumbleType.kRightRumble, 1.0);
                } else {
                	GEAR.open();
                }
                break;
                
            case WAITING:
                if (Timer.getFPGATimestamp() >= (timeStart + waitSeconds)) {
                	System.out.println("GearStateCommand: Close Gear Holder");
                    setState(GearState.CLOSING);
                } else {
                    GEAR.idle();
                }
                
               break;
                
            case CLOSING:
                if (GEAR.isClosed()) {
                    finished = true;
                	System.out.println("GearStateCommand: Gear Holder CLOSED");
                	Robot.oi.driver.setRumble(RumbleType.kLeftRumble, 0.0);
                	Robot.oi.driver.setRumble(RumbleType.kRightRumble, 0.0);
                } else {
                    GEAR.close();
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
        GEAR.idle();
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    @Override
    protected void interrupted() {
        System.out.println("GearStateCommand: interrupted()");
        GEAR.idle();
    }
}