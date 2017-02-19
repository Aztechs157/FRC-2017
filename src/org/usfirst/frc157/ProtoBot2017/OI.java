// RobotBuilder Version: 2.0 
//
// This file was generated by RobotBuilder. It contains sections of
// code that are automatically generated and assigned by robotbuilder.
// These sections will be updated in the future when you export to
// Java from RobotBuilder. Do not put any code or make any change in
// the blocks indicating autogenerated code or it will be lost on an
// update. Deleting the comments indicating the section will prevent
// it from being updated in the future.


package org.usfirst.frc157.ProtoBot2017;

import org.usfirst.frc157.ProtoBot2017.commands.*;
import org.usfirst.frc157.ProtoBot2017.commands.DebugPrintInfo.DebugSelection;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.buttons.*;
import org.usfirst.frc157.ProtoBot2017.subsystems.*;


/**
 * This class is the glue that binds the controls on the physical operator
 * interface to the commands and command groups that allow control of the robot.
 */
public class OI {
    //// CREATING BUTTONS
    // One type of button is a joystick button which is any button on a joystick.
    // You create one by telling it which joystick it's on and which button
    // number it is.
    // Joystick stick = new Joystick(port);
    // Button button = new JoystickButton(stick, buttonNumber);

    // There are a few additional built in buttons you can use. Additionally,
    // by subclassing Button you can create custom triggers and bind those to
    // commands the same as any other Button.

    //// TRIGGERING COMMANDS WITH BUTTONS
    // Once you have a button, it's trivial to bind it to a button in one of
    // three ways:

    // Start the command when the button is pressed and let it run the command
    // until it is finished as determined by it's isFinished method.
    // button.whenPressed(new ExampleCommand());

    // Run the command while the button is being held down and interrupt it once
    // the button is released.
    // button.whileHeld(new ExampleCommand());

    // Start the command when the button is released  and let it run the command
    // until it is finished as determined by it's isFinished method.
    // button.whenReleased(new ExampleCommand());


    // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=DECLARATIONS
 
    public JoystickButton joystickButtonA;  // Controller A
    public JoystickButton joystickButtonB;  // Controller B
    public JoystickButton joystickButtonX;  // Controller X
    public JoystickButton joystickButtonY;  // Controller Y
    public JoystickButton joystickButton7;
    public JoystickButton joystickButton8;
    public JoystickButton joystickButton12;

    public JoystickButton joystickButton5;// left bumper
    public JoystickButton joystickButton6;// right bumper

    public JoystickButton joystickButtonRightStickPress; // Press Right Stick
    

    public XboxController driver;
    public Joystick operatorJoystick = new Joystick(1);// instantiates thye operator joystick
    public Button trigger ;
    public Button operatorButton2;    
    public Button operatorButton8;
    public Button operatorButton10;
    public Button operatorButton12;
    public Button operatorButton5;
    public Button operatorButton3;
    public Button operatorButton4;    
    public Button operatorButton6;    

    // for gate
    public Button operatorButton7;
    public Button operatorButton9;
    public Button operatorButton11;
    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=DECLARATIONS

    public OI() {
        // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CONSTRUCTORS

        driver = new XboxController(0);
        
        joystickButtonA = new JoystickButton(driver, 1);
//        joystickButtonA.whileHeld(new AlignForShot(Vision.BoilerRange.FAR));
        joystickButtonA.whileHeld(new ShootCommand(Shoot.ShootCommand.FAR));

        joystickButtonB = new JoystickButton(driver, 2);
        joystickButtonB.whenPressed(new SetCameraTarget());

        joystickButtonX = new JoystickButton(driver, 3);
//        joystickButtonX.whileHeld(new AlignForShot(Vision.BoilerRange.FAR));
        joystickButtonX.whenPressed(new ToggleCollection());
        
        joystickButtonY = new JoystickButton(driver, 4);
		joystickButtonY.whenPressed(new StoreImages());
       
		joystickButton7 = new JoystickButton(driver, 7);
        joystickButton7.whenPressed(new OperatorGear(Gear.GearCommand.CLOSE));
     
        joystickButton8 = new JoystickButton(driver, 8);
        joystickButton8.whenPressed(new OperatorGear(Gear.GearCommand.CLOSE));
     
        joystickButtonRightStickPress = new JoystickButton(driver, 10);
        joystickButtonRightStickPress.whenPressed(new AlignToField());

        
        trigger = new JoystickButton(operatorJoystick, 1);
        trigger.whileHeld(new CollectorCommand(Collect.collectorCommand.LOAD));

        operatorButton2 = new JoystickButton(operatorJoystick, 2);
        operatorButton2.whileHeld(new ClimbCommand(Climb.ClimbCommand.MATCH));
        
        operatorButton8 = new  JoystickButton(operatorJoystick, 8);
        operatorButton8.whenPressed(new ShootCommand(Shoot.ShootCommand.FAR));
        operatorButton10 = new  JoystickButton(operatorJoystick, 10);
        operatorButton10.whenPressed(new ShootCommand(Shoot.ShootCommand.NEAR));
        operatorButton12 = new  JoystickButton(operatorJoystick, 12);
        operatorButton12.whenPressed(new ShootCommand(Shoot.ShootCommand.IDLE));
        
        operatorButton4 = new  JoystickButton(operatorJoystick, 4);
        operatorButton4.whenPressed(new HelixCommand(Helix.helixCommand.LOADRIGHT));
        operatorButton6 = new  JoystickButton(operatorJoystick, 6);
        operatorButton6.whenPressed(new HelixCommand(Helix.helixCommand.IDLERIGHT));

        operatorButton3 = new  JoystickButton(operatorJoystick, 3);
        operatorButton3.whenPressed(new HelixCommand(Helix.helixCommand.LOADLEFT));
        operatorButton5 = new  JoystickButton(operatorJoystick, 5);
        operatorButton5.whenPressed(new HelixCommand(Helix.helixCommand.IDLELEFT));
        
        operatorButton7 = new JoystickButton(operatorJoystick, 7);
        operatorButton7.whenPressed(new GateCommand(Gate.gateCommand.OPEN));
        operatorButton9 = new JoystickButton(operatorJoystick, 9);
        operatorButton9.whenPressed(new GateCommand(Gate.gateCommand.OFF));
        operatorButton11 = new JoystickButton(operatorJoystick, 11);
        operatorButton11.whenPressed(new GateCommand(Gate.gateCommand.CLOSED));
        
        
                // SmartDashboard Buttons
         SmartDashboard.putData("AlignToField", new AlignToField());
        SmartDashboard.putData("Toggle Camera Targeting", new SetCameraTarget());
        SmartDashboard.putData("Toggle Camera Targeting", new SetCamera());
        SmartDashboard.putData("Store Camera Images", new StoreImages());
        SmartDashboard.putData("Autonomous Command", new AutonomousCommand());
        SmartDashboard.putData("DebugButton", new DebugPrintInfo("ScreenButton", DebugSelection.NOPRINT));
        SmartDashboard.putData("Align for Near Shot", new AlignForShot(Vision.BoilerRange.NEAR));
        SmartDashboard.putData("Align for Far Shot", new AlignForShot(Vision.BoilerRange.FAR));
        
        // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CONSTRUCTORS
    }

    // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=FUNCTIONS
    public XboxController getDriver() {
        return driver;
    }


    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=FUNCTIONS
}

