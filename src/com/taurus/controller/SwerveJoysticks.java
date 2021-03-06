package com.taurus.controller;

import com.taurus.swerve.SwerveVector;

import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.Joystick;

public class SwerveJoysticks implements SwerveController {

    private Joystick left;
    private Joystick right;

    private boolean fieldRelative;
    private boolean fieldRelativeLast;

    public static final double DEADBAND = 0.05;

    /**
     * Create a new Joysticks Controller object
     */
    public SwerveJoysticks()
    {
        left = new Joystick(0);
        right = new Joystick(1);

        fieldRelative = true;
        fieldRelativeLast = false;

    }

    /**
     * {@inheritDoc}
     */
    public double getX(Hand hand)
    {
        return GetJoy(hand).getX(hand);
    }

    /**
     * {@inheritDoc}
     */
    public double getY(Hand hand)
    {
        return GetJoy(hand).getY(hand);
    }

    /**
     * Get the joystick object for the specified hand
     * 
     * @param hand
     * @return left or right Joystick
     */
    private Joystick GetJoy(Hand hand)
    {
        if (hand == Hand.kLeft)
        {
            return left;
        }
        else
        {
            return right;
        }
    }

    /**
     * {@inheritDoc}
     */
    public double getMagnitude(Hand hand)
    {
        double value = 0;

        value = GetJoy(hand).getMagnitude();

        if (value < DEADBAND)
        {
            value = 0;
        }
        return value;
    }
    
    /**
     * {@inheritDoc}
     */
    public double getDirectionDegrees(Hand hand)
    {
        return GetJoy(hand).getDirectionDegrees();
    }

    /**
     * {@inheritDoc}
     */
    public double getDirectionRadians(Hand hand)
    {
        return GetJoy(hand).getDirectionRadians();
    }

    /**
     * {@inheritDoc}
     */
    public double getHaloDrive_Rotation()
    {
        double value = 0;

        value = right.getAxis(Joystick.AxisType.kX);

        if (value < DEADBAND)
        {
            value = 0;
        }
        return value;
    }

    /**
     * {@inheritDoc}
     */
    public SwerveVector getHaloDrive_Velocity()
    {
        SwerveVector value;

        value = new SwerveVector(left.getX(), left.getY());

        if (value.getMag() < DEADBAND)
        {
            value.setMag(0);
        }

        return value;
    }
    
    /**
     * {@inheritDoc}
     */
    public double getAngleDrive_Heading()
    {
        double Angle = -1;
        if (right.getMagnitude() > 0.65)
        {
            Angle = right.getDirectionDegrees();
        }

        return Angle;
    }
    
    /**
     * {@inheritDoc}
     */
    public SwerveVector getAngleDrive_Velocity()
    {
        SwerveVector value;

        value = new SwerveVector(left.getX(), left.getY());

        if (value.getMag() < DEADBAND)
        {
            value.setMag(0);
        }
        return value;
    }

    /**
     * {@inheritDoc}
     */
    public boolean getRawButton(int button)
    {
        return left.getRawButton(button);
    }

    /**
     * {@inheritDoc}
     */
    public boolean getHighGearEnable()
    {
        // shift to high gear if the button is not held down
        return left.getRawButton(2);
    }

    /**
     * {@inheritDoc}
     */
    public boolean getSwerveBrake()
    {
        return left.getRawButton(3);
    }

    /**
     * {@inheritDoc}
     */
    public boolean getResetGyro()
    {
        return left.getRawButton(7);
    }

    /**
     * {@inheritDoc}
     */
    public boolean getFieldRelative()
    {
        if (!fieldRelativeLast && left.getTop())
        {
            fieldRelative = !fieldRelative;
        }
        fieldRelativeLast = left.getTop();
        return fieldRelative;
    }

    /**
     * {@inheritDoc}
     */
    public double getDPad()
    {
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    public double getAngleDrive_Rotation()
    {
        return 0;
    }

    @Override
    public boolean getTrigger(Hand hand)
    {
        return false;
    }

    @Override
    public boolean getWheelCal()
    {
        return false;
    }

    @Override
    public boolean getHighSpeed()
    {
        return false;
    }

    @Override
    public double getLowSpeed()
    {
        return 0;
    }

    @Override
    public double getHaloDrive_Heading45()
    {
        return 0;
    }
}
