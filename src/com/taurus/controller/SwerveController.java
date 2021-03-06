package com.taurus.controller;

import com.taurus.swerve.SwerveVector;

import edu.wpi.first.wpilibj.GenericHID.Hand;

/**
 * <pre>
 * Halo Drive: like driving a vehicle in Halo
 * Left joystick: movement l/r/f/b
 * Right joystick: rotation rate (CCW/CW)
 *          ^
 *          |
 *      <---+--->           <---+--->
 *          |
 *          v
 *       Movement            Rotation
 *       
 * Angle Drive: like a top-down shooter such
 * as Geometry Wars
 * Left joystick: movement l/r/f/b
 * Right joystick: robot heading l/r/f/b
 *          ^                   ^
 *          |                   |
 *      <---+--->           <---+--->
 *          |                   |
 *          v                   v
 *       Movement            Heading
 *    
 * Parts adapted from Team 3946 (https://github.com/frc3946)
 * </pre>
 * 
 * @author Team 4818 Taurus Robotics
 */
public interface SwerveController {

    public static final double DEADBAND = 0.0;

    /**
     * Get x axis value between -1 and 1 for specified hand
     * @param hand
     * @return
     */
    public double getX(Hand hand);

    /**
     * Get y axis value between -1 and 1 for specified hand
     * @param hand
     * @return
     */
    public double getY(Hand hand);

    /**
     * Get the magnitude of the direction vector formed by the joystick's
     * current position relative to its origin
     * 
     * @param hand
     *            Hand associated with the Joystick
     * @return the magnitude of the direction vector
     */
    public double getMagnitude(Hand hand);

    /**
     * Get the direction of the vector formed by the joystick and its origin in
     * degrees
     * 
     * @param hand
     *            Hand associated with the Joystick
     * @return The direction of the vector in degrees
     */
    public double getDirectionDegrees(Hand hand);

    /**
     * Get the direction of the vector formed by the joystick and its origin in
     * radians
     * 
     * @param hand
     *            Hand associated with the Joystick
     * @return The direction of the vector in radians
     */
    public double getDirectionRadians(Hand hand);

    /**
     * Get the Rotation value of the joystick for Halo Drive
     * 
     * @return The Rotation value of the joystick.
     */
    public double getHaloDrive_Rotation();

    /**
     * Get the swerve vector (mag & angle) of the velocity joystick for Halo
     * Drive
     * 
     * @return The vector of the joystick.
     */
    public SwerveVector getHaloDrive_Velocity();
    
    public double getHaloDrive_Heading45();
    
    /**
     * Get the heading/angle in degrees for Angle Drive
     * 
     * @return The angle in degrees of the joystick.
     */
    public double getAngleDrive_Heading();

    /**
     * Get the rotation for Angle Drive
     * 
     * @return The rotation rate in rad/s.
     */
    public double getAngleDrive_Rotation();

    /**
     * Get the swerve vector (mag & angle) of the velocity joystick for Angle
     * Drive
     * 
     * @return The vector of the joystick.
     */
    public SwerveVector getAngleDrive_Velocity();

    /**
     * Get a raw button state
     * @param button
     * @return
     */
    public boolean getRawButton(int button);
    
    /**
     * Get whether the high gear should be enabled
     * 
     * @return true if high gear, else low gear
     */
    public boolean getHighGearEnable();
    
    public boolean getTrigger(Hand hand);
    
    /**
     * Get the brake
     * 
     * @return
     */
    public boolean getSwerveBrake();

    /**
     * Get if the gyro should be reset
     * 
     * @return
     */
    public boolean getResetGyro();

    /**
     * get if in field relative
     * 
     * @return
     */
    public boolean getFieldRelative();

    /**
     * Get the dpad input
     * @return
     */
    public double getDPad();
    
    public boolean getHighSpeed();
    public double getLowSpeed();

    public boolean getWheelCal();
}
