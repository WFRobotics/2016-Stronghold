package com.taurus.commands;

import com.taurus.Utilities;
import com.taurus.robot.OI;
import com.taurus.robot.Robot;
import com.taurus.vision.Vision;

import edu.wpi.first.wpilibj.command.Command;

public class ShooterRev extends Command {
  
    public ShooterRev() {
        // Use requires() here to declare subsystem dependencies
        requires(Robot.shooterSubsystem);
    }

    // Called just before this Command runs the first time
    protected void initialize() {
        // We want a constant fire speed, thus set the speed only once
        // If we have a vision, move this to execute and do calculation based on vision
        double speedTop = .65 + .2 * OI.getShooterSpeedAdjust();
        double speedBottom = .8 + .2 * OI.getShooterSpeedAdjust();
        Robot.shooterSubsystem.setSpeed(speedTop, speedBottom);
        
        //Vision.getInstance().enableBackCamera(false);
        //setTimeout(4);
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
        Utilities.PrintCommand("Shooter", this);
        // We want a constant fire speed, thus set the speed only once
        // If we have a vision, move this to execute and do calculation based on vision
        double speedTop = .7 + .2 * OI.getShooterSpeedAdjust();
        double speedBottom = .8 + .2 * OI.getShooterSpeedAdjust();
        Robot.shooterSubsystem.setSpeed(speedTop, speedBottom);
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        return false;//isTimedOut();
    }

    // Called once after isFinished returns true
    protected void end() {
        Robot.shooterSubsystem.setSpeed(0,0);
        //Vision.getInstance().enableBackCamera(true);
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
        end();
    }
}
