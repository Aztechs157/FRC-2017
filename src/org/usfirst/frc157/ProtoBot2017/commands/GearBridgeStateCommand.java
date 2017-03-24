package org.usfirst.frc157.ProtoBot2017.commands;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.GenericHID.RumbleType;
import edu.wpi.first.wpilibj.Timer;
import org.usfirst.frc157.ProtoBot2017.Robot;
import org.usfirst.frc157.ProtoBot2017.subsystems.Gear;
import org.usfirst.frc157.ProtoBot2017.subsystems.GearBridge;

public class GearBridgeStateCommand extends Command {
    
    private final GearBridge GEARBRIDGE = Robot.gearBridge;
    private double waitSeconds = 1.0;
    private double timeStart;

    private boolean finished = false;
    
    // states
    public enum GearBridgeState
    {
        RAISING,
        LOWERING,
        WAITING,
   
    }
    
    
    private GearBridgeState currentState;
    
    public GearBridgeStateCommand() {
        //this.waitSeconds = waitSeconds;
        requires(Robot.gearBridge);
    }
    
    // currentState set
    public void setState(GearBridgeState v) {
        currentState = v;
        System.out.println("Set gear state to: " + v.toString());
    }
    
    // currentState get
    public GearBridgeState getState() {
        return currentState;
    }
    
    // Called just before this Command runs the first time
    @Override
    protected void initialize() {
        System.out.println("GearBridgeStateCommand: initialize()");
        currentState = GearBridgeState.RAISING;
        finished = false;
    }

    // Called repeatedly when this Command is scheduled to run
    @Override
    protected void execute() {
//        System.out.println("GearBridgeStateCommand: execute()");
        switch (currentState) {
            
            case RAISING: 
                if (GEARBRIDGE.isUp()) {
                    setState(GearBridgeState.WAITING);
                    timeStart = Timer.getFPGATimestamp();
                	System.out.println("GearBridgeStateCommand: UP");
                	Robot.oi.driver.setRumble(RumbleType.kLeftRumble, 1.0);
                	Robot.oi.driver.setRumble(RumbleType.kRightRumble, 1.0);
                } else {
                	GEARBRIDGE.raise();
                }
                break;
                
            case WAITING:
                if (Timer.getFPGATimestamp() >= (timeStart + waitSeconds)) {
                    System.out.println("GearBridgeStateCommand: Wait Over");
                    setState(GearBridgeState.LOWERING);
                } else {
                    GEARBRIDGE.idle();
                }
                
               break;
                
                
            case LOWERING:
                if (GEARBRIDGE.isDown()) {
                    finished = true;
                	System.out.println("GearBridgeStateCommand: Down");
                	Robot.oi.driver.setRumble(RumbleType.kLeftRumble, 0.0);
                	Robot.oi.driver.setRumble(RumbleType.kRightRumble, 0.0);
                } else {
                    GEARBRIDGE.lower();
                }
                break;
        }
    }

    // Make this return true when this Command no longer needs to run execute()
    @Override
    protected boolean isFinished() {
        System.out.println("GearBridgeStateCommand: finished()");
        return  finished;
    }

    // Called once after isFinished returns true
    @Override
    protected void end() {
        System.out.println("GearBridgeStateCommand: end()");
        GEARBRIDGE.idle();
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    @Override
    protected void interrupted() {
        System.out.println("GearBridgeStateCommand: interrupted()");
        GEARBRIDGE.idle();
    }
}