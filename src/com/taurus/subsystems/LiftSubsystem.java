package com.taurus.subsystems;

import com.taurus.PIDController;
import com.taurus.Utilities;
import com.taurus.commands.LiftHold;
import com.taurus.commands.LiftStop;
import com.taurus.hardware.Brake;
import com.taurus.hardware.MagnetoPotSRX;
import com.taurus.hardware.Brake.BRAKE_STATE;
import com.taurus.robot.RobotMap;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class LiftSubsystem extends Subsystem{
    public final double LIMIT_UPPER = 50;
    public final double LIMIT_LOWER = 9.6;
    public final double LIMIT_TOLERANCE = .75;
    public final double LENGTH_SCISSOR_STEP = 26;
    public final double BRAKE_ANGLE_PAWN_DOWNWARD = 10;  // TODO - DRL determine angle first pawn is engaged thru testing
    public final double BRAKE_ANGLE_PAWN_UPWARD = 20;  // TODO - DRL determine angle second pawn is engaged thru testing
    public final double BRAKE_DISENGAGE_MOTOR_SPEED = .1;  // TODO - DRL determine safe small speed to help release brake pawns

    private CANTalon motorLeft;
    private CANTalon motorRight;
    private MagnetoPotSRX potLeft;
    private MagnetoPotSRX potRight;
    private Brake brakes;
    private boolean disableAutoLift;
    
    /** array of size 2, {right side, left side} */
    private PIDController[] heightPIDs;
    
    /**
     * Constructor
     */
    public LiftSubsystem() {
        motorLeft = new CANTalon(RobotMap.CAN_LIFT_TALON_L);
        motorRight = new CANTalon(RobotMap.CAN_LIFT_TALON_R);

        motorLeft.ConfigFwdLimitSwitchNormallyOpen(true);
        motorLeft.ConfigRevLimitSwitchNormallyOpen(false);
        motorRight.ConfigFwdLimitSwitchNormallyOpen(true);
        motorRight.ConfigRevLimitSwitchNormallyOpen(false);

        potLeft = new MagnetoPotSRX(motorLeft, -360);
        potRight = new MagnetoPotSRX(motorRight, 360);

        potLeft.setAverage(true,6);
        potRight.setAverage(true,6);

        heightPIDs = new PIDController[]
                        {new PIDController(.5, 0, 0, 1),
                         new PIDController(.5, 0, 0, 1)};
        
        brakes = new Brake(new int[]{RobotMap.PIN_SERVO_LIFT_BRAKE_L, RobotMap.PIN_SERVO_LIFT_BRAKE_R},
                           new double[]{BRAKE_ANGLE_PAWN_DOWNWARD, BRAKE_ANGLE_PAWN_UPWARD});
        
        disableAutoLift = false;
    }

    /**
     * set the default command
     */
    protected void initDefaultCommand()
    {
//      setDefaultCommand(new LiftStop());
        setDefaultCommand(new LiftHold());
    }

    /**
     * Set lift height and send the values to the motors
     * @param height we want to be at in inches from the bottom of the lift
     * @return whether at desired height or not: will be a true/false and move accordingly
     */
    public boolean setHeightFromLiftBottom(double height) {
        return setHeightFromFloor(height+LIMIT_LOWER);
    }

    /**
     * Set lift height and send the values to the motors
     * @param height we want to be at in inches from the floor
     * @return whether at desired height or not: will be a true/false and move accordingly
     */
    public boolean setHeightFromFloor(double height) {

        double[] motorOutput;
        boolean arrivedL;
        boolean arrivedR;

        // Error check height, set to within bounds if needed
        if(height >= LIMIT_UPPER)
        {
            height = LIMIT_UPPER;
        } 
        else if(height <= LIMIT_LOWER)
        {
            height = LIMIT_LOWER;
        }

        // subtract the distance from the floor to the bottom of the lift
        // this lets the rest of the calculations all be relative to the same point
        height -= LIMIT_LOWER;

        SmartDashboard.putNumber("Lift Desired Height", height);

        motorOutput = getPIDSpeed(height);

        // determine if each side is at the desired height
        
        
        arrivedL = Math.abs(getHeightFromLiftBottomL()-height) <= LIMIT_TOLERANCE;
        arrivedR = Math.abs(getHeightFromLiftBottomR()-height) <= LIMIT_TOLERANCE;

        if(arrivedL && arrivedR)
        {
            // Set the brake so that the lift cannot move
            setSpeed(0, 0);
            brakes.setState(BRAKE_STATE.ONE);
        }
        else if (!brakes.isSafeToMove())
        {
            // We don't want to brake, but the brake is not in a state that we can set the motor yet
            // We need to run the motors up slightly to take pressure off of the brake pawns so the
            // servos can move the pawns off of the ratchets.
            // TODO - DRL do we instead want to try to hold our position? Might be dangerous.            
            setSpeed(BRAKE_DISENGAGE_MOTOR_SPEED, BRAKE_DISENGAGE_MOTOR_SPEED);
            brakes.setState(BRAKE_STATE.NONE);
        }
        else
        {
            setSpeed(motorOutput[0]* Preferences.getInstance().getDouble("LiftSpeedLimit", .85),
                    motorOutput[1]* Preferences.getInstance().getDouble("LiftSpeedLimit", .85));            
        }

        return arrivedL && arrivedR;
    }
    
    public void printSensors()
    {

        SmartDashboard.putNumber("Lift Total Height", getHeightFromFloorAverage());
        //SmartDashboard.putNumber("Lift Height", getHeightFromLiftBottomAverage());
        SmartDashboard.putNumber("Lift Height L", getHeightFromLiftBottomL());
        SmartDashboard.putNumber("Lift Height R", getHeightFromLiftBottomR());
        SmartDashboard.putNumber("Lift Pot L", getAngleL());
        SmartDashboard.putNumber("Lift Pot R", getAngleR());
    

        updatePotOffsets();
    }

    /**
     * helper function: sets the motor speeds for the lift to the same value
     * @param right speed between -1 to 1
     * @param right speed between -1 to 1
     */
    public void setSpeed(double right, double left) {

        printSensors();
        double leftHeight= getHeightFromLiftBottomL();
        double rightHeight= getHeightFromLiftBottomR();
        
        left = speedDamper(left, leftHeight);
        right = speedDamper(right, rightHeight);

        SmartDashboard.putNumber("Lift Speed L", left);
        SmartDashboard.putNumber("Lift Speed R", right);

        if (!brakes.isSafeToMove())
        {
            motorRight.set(0);
            motorLeft.set(0);
            brakes.setState(BRAKE_STATE.NONE);
        }
        else if (Math.abs(leftHeight - rightHeight) > 6)
        {
            motorRight.set(0);
            motorLeft.set(0);
        }
        else
        {
            double dir = (double) Math.signum((right + left)/2.0);
            
            double heightDif = rightHeight - leftHeight;
            if (heightDif >= 2)
            {
                if (dir > 0.0)
                {
                    right = 0;
                }
                else
                {
                    left = 0;
                }
            }
            else if (heightDif <= -2)
            {
                if (dir > 0.0)
                {
                    left = 0;
                }
                else
                {
                    right = 0;
                }
            }
            else if (heightDif >= 1)
            {
                if (dir > 0.0)
                {
                    right *= .5;
                }
                else
                {
                    left *= .5; 
                }
            }
            else if (heightDif <= -1)
            {
                if (dir > 0.0)
                {
                    left *= .5;
                }
                else
                {
                    right *= .5; 
                }
            }
            
            if(left < 0.01 && left > -0.01 &&
                    right < 0.01 && right > -0.01)
            {
                enableBrakeMode(false);
            }
            
            motorRight.set(right);
            motorLeft.set(left);
        }
    }

    /**
     * Dampen the passed in speed based on height, only when going down
     * @param speed input speed, -1 to 1
     * @param height current height, in inches
     * @return dampened speed
     */
    private double speedDamper(double speed, double height)
    {
        if (speed < 0)
        {
            // alternately
            // double adjust = - 0.000007 * Math.pow(height, 4)
            //                 + 0.00051 * Math.pow(height, 3)
            //                 - 0.0071 * Math.pow(height, 2)
            //                 + 0.0436 * height;
            // speed *= adjust;
            // speed = Math.signum(speed) * Math.min(Math.abs(speed), 1);
            
            if (height == 0)
            {
                speed = 0;
            }
            else if (height <= 5)
            {
                speed = speed * .05;
            }
            else if (height <= 10)
            {
                speed = speed * .1;
            }
            else if (height <= 15)
            {
                speed = speed * .2;
            }
            else if (height <= 20)
            {
                speed = speed * .5;
            }
        }
        else
        {
            if (height <= 20)
            {
                speed = speed * .7;
            }
        }
        
        return speed;
    }

    /**
     * get the latest PID values from Dashboard
     */
    private void updatePIDConstants()
    {
        for (int i = 0; i < heightPIDs.length; i++)
        {
            heightPIDs[i].setP(Preferences.getInstance().getDouble("LiftPID_P", .3));
            heightPIDs[i].setI(Preferences.getInstance().getDouble("LiftPID_I", 0.1));
            heightPIDs[i].setD(Preferences.getInstance().getDouble("LiftPID_D", 0));
        }
    }

    /**
     * Calculate the desirable speeds to keep the two lift motors in sync.
     * This is advantageous because it corrects position drift due to one motor being faster than the other.
     * Author: David Lindner
     * @param height desired, inches from bottom of lift
     * @return speed calculated (right, left)
     */
    private double[] getPIDSpeed(double height)
    {
        updatePIDConstants();

        // Array convention -> (right side, left side)
        double[] heightActual = {getHeightFromLiftBottomR(), getHeightFromLiftBottomL()};
        double[] speedCompensated = {0,0};
        double[] speedCompensatedAbs = {0,0};
        
        double heightActualAverage = (heightActual[0] + heightActual[1]) / 2;

        // calculate compensated speed for each side
        for (int i = 0; i < heightPIDs.length; i++)
        {
            double heightError = heightActual[i] - heightActualAverage;  // Ex: 2 - 1.5 = .5 error
        
            double speedPID = heightPIDs[i].update(height, heightActual[i]);
            double direction = Math.signum(speedPID);  // Up: 1, Down: -1
    
            // If we are doing down, have error percentage be based on how far we are ahead/behind in that desired direction.
            // Ex: 10% ahead -> 10% behind, .1 error * -1 / 1 average + 1 = .91 rather than 1.1
            double heightErrorPercent = (direction * heightError / (heightActualAverage + LIMIT_LOWER) + 1); // Ex: 2 vs 1.5 -> 1.33 
            
            speedCompensated[i] = speedPID / heightErrorPercent; // Ex: 1 / 1.33 = .75 speed
    
            // At this point, speed may be greater than one because we corrected for error.
            // Ex: Told one motor to go 110% of speed desired, the other to go 91% of speed desired. But speed desired is 1.
            // Scale it back to within range -1 to 1.        
            speedCompensatedAbs[i] = Math.abs(speedCompensated[0]);
        }

        // lower the compensated speeds to -1 to 1 by dividing by the larger one
        for (int i = 0; i < heightPIDs.length; i++)
        {
            if (speedCompensatedAbs[i] > 1)
            {                
                // This is the side with positive error, and after compensation, happens to be above max magnitude of 1.
                // Scale back both speeds proportionally to how far the faster side is above max magnitude of 1.
                //double percentAbove = speedCompensatedAbs[index] - 1;

                // we can't subtract 1 from this, as when the sides become close,
                // the values will be much larger than 1. By dividing by the largest,
                // we will scale everything to a max of 1.
                double percentAbove = speedCompensatedAbs[i]; 

                for (int side = 0; side < heightPIDs.length; side++)
                {
                    speedCompensated[side] /= percentAbove;
                }
                break;
            }
        }

        return speedCompensated;
    }

    /**
     * Grab the latest pot offsets from Dashboard
     * TODO: add limit switch checks
     */
    private void updatePotOffsets()
    {
//        double angleBottom = Preferences.getInstance().getDouble("LiftPotMinimum", 5.5);
//        
//        if(false && !motorLeft.isRevLimitSwitchClosed())
//        {
//            double offset = angleBottom - potLeft.getWithoutOffset();
//            potLeft.setOffset(offset);
//            Preferences.getInstance().putDouble("LiftPotOffsetL", offset);
//        }
//        else
//        {
            potLeft.setOffset(Preferences.getInstance().getDouble("LiftPotOffsetL", 87.952));
//        }
        
//        if(false && !motorRight.isRevLimitSwitchClosed())
//        {
//            double offset = angleBottom - potRight.getWithoutOffset();
//            potRight.setOffset(offset);
//            Preferences.getInstance().putDouble("LiftPotOffsetR", offset);
//        }
//        else
//        {
            potRight.setOffset(Preferences.getInstance().getDouble("LiftPotOffsetR", 148.0));
//        } 
//        
        potRight.setFullRange(Preferences.getInstance().getDouble("LiftPotScaleR", 360));
        potLeft.setFullRange(Preferences.getInstance().getDouble("LiftPotScaleL", -360));
    }
    
    /**
     * Grabs the latest offsets from Dashboard
     */
    public void setCalibration() {
        
        double angleBottom = Preferences.getInstance().getDouble("LiftPotMinimum", 5.5);
        
        double offset = angleBottom - potLeft.getWithoutOffset();
        potLeft.setOffset(offset);
        Preferences.getInstance().putDouble("LiftPotOffsetL", offset);
        
        offset = angleBottom - potRight.getWithoutOffset();
        potRight.setOffset(offset);
        Preferences.getInstance().putDouble("LiftPotOffsetR", offset);
        
    }

    /**
     * get the angle of the left sensor
     * @return angle in degrees
     */
    private double getAngleR() {
        // by wrapping this to -180 and 180, we fix going below 0 degrees
        // and thinking we're all the way at the top 
        return Utilities.wrapToRange(potRight.get(), -180, 180);
    }

    /**
     * get the angle of the left sensor
     * @return angle in degrees
     */
    private double getAngleL() {
        // by wrapping this to -180 and 180, we fix going below 0 degrees
        // and thinking we're all the way at the top
        return Utilities.wrapToRange(potLeft.get(), -180, 180);
    }

    /**
     * get the height of the right side from the bottom of the lift
     * @return height in inches
     */
    private double getHeightFromLiftBottomR() {
        return (Math.sin(Math.toRadians(getAngleR()))*LENGTH_SCISSOR_STEP)*2;
    }

    /**
     * get the height of the left side from the bottom of the lift
     * @return height in inches
     */
    private double getHeightFromLiftBottomL() {
        return (Math.sin(Math.toRadians(getAngleL()))*LENGTH_SCISSOR_STEP)*2;
    }

    /**
     * get the average height of the two sides from the bottom of the lift
     * @return height, in inches
     */
    public double getHeightFromLiftBottomAverage() {
        return (getHeightFromLiftBottomL() + getHeightFromLiftBottomR()) / 2;
    }

    /**
     * get the average height of the two sides from the floor
     * @return height, in inches
     */
    public double getHeightFromFloorAverage(){
        return getHeightFromLiftBottomAverage() + LIMIT_LOWER;
    }

    public void enableBrakeMode(boolean b)
    {
        motorLeft.enableBrakeMode(b);
        motorRight.enableBrakeMode(b);
        
    }

    public boolean isLevel()
    {
        return Math.abs(getHeightFromLiftBottomL() - getHeightFromLiftBottomR()) < LIMIT_TOLERANCE;
    }

    public boolean getSWBrake()
    {
        // TODO Auto-generated method stub
        return motorLeft.getBrakeEnableDuringNeutral() && motorRight.getBrakeEnableDuringNeutral();
    }
}