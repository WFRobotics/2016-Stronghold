package com.taurus.commands;

import com.taurus.Utilities;
import com.taurus.robot.Robot;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Command;

public class AimerContinuous extends Command 
{
    final double totalTime = 2;
    final double speedMin = .2;
    final double speedMax = .5;
    final boolean clockwise;
    
    double startTime;
    double speed;
    
    public AimerContinuous(boolean clockwise) 
    {
        requires(Robot.aimerSubsystem);        
        this.clockwise = clockwise;
    }
    
    protected void initialize() 
    {
        speed = speedMin;
        startTime = Timer.getFPGATimestamp();
    }
    
    protected void execute() 
    {        
        Utilities.PrintCommand("Aimer", this);
        
        // increase speed over a set amount of time to reach max speed while button is held
        double percent = ((Timer.getFPGATimestamp() - startTime) / totalTime);
        speed = percent * (speedMax - speedMin) + speedMin;
        
        if(clockwise)
        {
            Robot.aimerSubsystem.setSpeed(-speed);
        }
        else
        {
            Robot.aimerSubsystem.setSpeed(speed);
        }
    }

    protected boolean isFinished()
    {
        return false;
    }
    
    protected void end() 
    {
       Robot.aimerSubsystem.setSpeed(0);      
    }
    
    protected void interrupted() 
    {
        
    }
}