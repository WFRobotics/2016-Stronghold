package com.taurus.swerve;

//import com.sun.glass.ui.Timer;
import com.taurus.controller.*;
//import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Application implements com.taurus.Application {

    private final int TEST_MODE_NORMAL = 0;
    private final int TEST_MODE_WHEEL = 1;
    private final int TEST_MODE_CALIBRATION_1 = 2;
    private final int TEST_MODE_CALIBRATION_2 = 3;
    private final int TEST_MODE_CALIBRATION_3 = 4;

    private SendableChooser testChooser = new SendableChooser();
    private SendableChooser testWheelChooser = new SendableChooser();
    
    private SwerveChassis drive;
    private SwerveController controller;
    
    public Application()
    {
        testWheelChooser = new SendableChooser();
        testWheelChooser.addDefault("Front Left", Integer.valueOf(0));
        testWheelChooser.addObject("Front Right", Integer.valueOf(1));
        testWheelChooser.addObject("Back Right", Integer.valueOf(2));
        testWheelChooser.addObject("Back Left", Integer.valueOf(3));
        SmartDashboard.putData("Test Wheel", testWheelChooser);
        
        // set up the choosers for running tests while in teleop mode
        testChooser = new SendableChooser();
        testChooser.addDefault("Normal", Integer.valueOf(TEST_MODE_NORMAL));
        testChooser.addObject("Wheel Test Full", Integer.valueOf(TEST_MODE_WHEEL));
        testChooser.addObject("Wheel Spin Raw", Integer.valueOf(TEST_MODE_CALIBRATION_1));
        testChooser.addObject("Wheel Set Angle", Integer.valueOf(TEST_MODE_CALIBRATION_2));
        testChooser.addObject("Wheel To Heading", Integer.valueOf(TEST_MODE_CALIBRATION_3));

        SmartDashboard.putData("Test Mode", testChooser);


        
        controller = new SwerveXbox();
        
        drive = new SwerveChassis(controller);
    }
    
    public void TeleopInit()
    {

    }

    public void TeleopPeriodic()
    {
        int i = ((Integer) testWheelChooser.getSelected()).intValue();

        switch (((Integer) testChooser.getSelected()).intValue())
        {
            case TEST_MODE_WHEEL:
             // use the left joystick to control the wheel module
                SwerveVector WheelActual = drive.getWheel(i).setDesired(
                        controller.getHaloDrive_Velocity(), controller.getHighGearEnable(), controller.getSwerveBrake());

                // display in SmartDashboard
                SmartDashboard.putNumber("Test Wheel Mag Actual", WheelActual.getMag());
                SmartDashboard.putNumber("Test Wheel Angle Actual",
//                        WheelActual.getAngle());
                       // drive.getWheel(i).getAnglePotValue());
                        
                        controller.getHaloDrive_Velocity().getAngle());

                
                
//                WheelActual.setAngle(drive.getWheel(i).MotorAngle.get());
                        
                        //                        Timer.getFPGATimestamp());
                // if the button is not held down, we're in high gear
                drive.setGearHigh(controller.getHighGearEnable());
                drive.UpdateShifter();
                break;

            case TEST_MODE_CALIBRATION_1:
                drive.getWheel(i).MotorAngle
                        .set(controller.getX(Hand.kRight) * .4);
                SmartDashboard.putNumber("motor set",
                        drive.getWheel(i).MotorAngle.get());
                SmartDashboard.putNumber("pot read", drive.getWheel(i).getAnglePotValue());

                break;

            case TEST_MODE_CALIBRATION_2:
                drive.getWheel(i).updateAngleMotor(
                        controller.getDirectionDegrees(Hand.kRight), 1.0);

                break;

            case TEST_MODE_CALIBRATION_3:
                SwerveVector TestVector = new SwerveVector();
                TestVector.setMagAngle(.5, controller.getDPad());
                if (controller.getDPad() != -1)
                {
                    drive.UpdateHaloDrive(TestVector,
                            controller.getHaloDrive_Rotation());
                }

                break;
            case TEST_MODE_NORMAL:
            default:
                SmartDashboard.putNumber("Gyro Angle", drive.getGyro().getYaw());
                SmartDashboard.putNumber("Last Heading", drive.getLastHeading());
                drive.run();
                break;
        }
    }

    public void TeleopDeInit()
    {

    }

    public void AutonomousInit()
    {

    }

    public void AutonomousPeriodic()
    {

    }

    public void AutonomousDeInit()
    {

    }

    public void TestModeInit()
    {

    }

    public void TestModePeriodic()
    {
    }

    public void TestModeDeInit()
    {

    }

    public void DisabledInit()
    {

    }

    public void DisabledPeriodic()
    {

    }

    public void DisabledDeInit()
    {

    }
}
