package com.taurus.controller;

import com.taurus.swerve.SwerveVector;

import edu.wpi.first.wpilibj.GenericHID.Hand;

public class SwerveXbox implements SwerveController {

    private Xbox xbox;

    public static final double DEADBAND = 0.2;

    private boolean fieldRelative;
    private boolean fieldRelativeLast;
    
    /**
     * Create a new Xbox Controller object
     */
    public SwerveXbox()
    {
        xbox = new Xbox(0);

        fieldRelative = true;
        fieldRelativeLast = false;
    }

    /**
     * {@inheritDoc}
     */
    public double getX(Hand hand)
    {
        return xbox.getX(hand);
    }

    /**
     * {@inheritDoc}
     */
    public double getY(Hand hand)
    {
        return xbox.getY(hand);
    }

    /**
     * {@inheritDoc}
     */
    public double getMagnitude(Hand hand)
    {
        double value = 0;

        value = xbox.getMagnitude(hand);

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
        return xbox.getDirectionDegrees(hand);
    }

    /**
     * {@inheritDoc}
     */
    public double getDirectionRadians(Hand hand)
    {
        return xbox.getDirectionRadians(hand);
    }

    /**
     * {@inheritDoc}
     */
    public double getHaloDrive_Rotation()
    {
        double value = 0;

        value = xbox.getAxis(Xbox.AxisType.kRightX);

        if (Math.abs(value) < DEADBAND)
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

        value = new SwerveVector(xbox.getX(Hand.kLeft), xbox.getY(Hand.kLeft));

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
        if (xbox.getMagnitude(Hand.kRight) > 0.65)
        {
            Angle = xbox.getDirectionDegrees(Hand.kRight);
        }

        return Angle;
    }
    
    /**
     * {@inheritDoc}
     */
    public double getAngleDrive_Rotation()
    {
        double Rotation = 0;
        if (xbox.getPOV() == 90)
        {
            Rotation = .75;
        }
        else if (xbox.getPOV() == 270)
        {
            Rotation = -.75;
        }
        return Rotation;
    }

    /**
     * {@inheritDoc}
     */
    public SwerveVector getAngleDrive_Velocity()
    {
        SwerveVector value;

        value = new SwerveVector(xbox.getX(Hand.kLeft), xbox.getY(Hand.kLeft));

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
        return xbox.getRawButton(button);
    }

    /**
     * {@inheritDoc}
     */
    public boolean getHighGearEnable()
    {
        return xbox.getBumper(Hand.kRight);
    }

    /**
     * {@inheritDoc}
     */
    public boolean getTrigger(Hand hand)
    {
        return xbox.getTrigger(hand);
    }

    /**
     * {@inheritDoc}
     */
    public boolean getCarHome()
    {
        return xbox.getTrigger(Hand.kRight);
    }

    /**
     * {@inheritDoc}
     */
    public boolean getCarTop()
    {
        return xbox.getTrigger(Hand.kLeft);
    }    
    
    /**
     * {@inheritDoc}
     */
    public boolean getSwerveBrake()
    {
        return false;// xbox.getBumper(Hand.kLeft);
    }

    /**
     * {@inheritDoc}
     */
    public boolean getResetGyro()
    {
        if (xbox.getBack() && xbox.getStart())
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean getFieldRelative()
    {
        if (!fieldRelativeLast && xbox.getBumper(Hand.kLeft))
        {
            fieldRelative = !fieldRelative;
        }
        fieldRelativeLast = xbox.getBumper(Hand.kLeft);
        return fieldRelative;
    }

    /**
     * {@inheritDoc}
     */
    public double getDPad()
    {
        return xbox.getPOV();
    }

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
        return -1;
    }
}
