package org.firstinspires.ftc.teamcode;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp; // TeleOP, I believe, is required to declare Opmodes?
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

// Declare the OpMode name:
@TeleOp(name="ExpoTest", group="testing")

/**
 * Created by Ari on 08-12-16.
 */

// Declare the class. You need to add 'extends' TypeofOpMode.

public class ExpoTest extends LinearOpMode{ // 'extends' means that it creates a sub-class, effectively adding on to LinearOpMode


    // Define a *few* variables that we will require...
    // We use double here as math.exp requires a double.

    private double rawPower;
    private double rawDirection;
    private double leftPower;
    private double rightPower;
    private boolean selectRight;

    @Override
    public void runOpMode() throws InterruptedException {

        // Declare and map the motors.
        DcMotor motorRight = hardwareMap.dcMotor.get("motorRight");
        motorRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER); // Make sure you change the mode back to something else.

        DcMotor motorLeft = hardwareMap.dcMotor.get("motorLeft");
        motorLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorLeft.setDirection(DcMotorSimple.Direction.REVERSE);

        // Wait until the "start" button is pressed.
        waitForStart();

        // Continue looping until the program is stopped.
        while (opModeIsActive()) {
            // Take the value of the Right Stick X for direction, and the Right Stick Y for power.
            // The main idea of this program is to use Math.exp for a curved acceleration.

            rawPower = (Math.exp(gamepad1.right_stick_y));
            rawDirection = (Math.exp(gamepad1.right_stick_x));

            if (rawDirection >= 0){
                selectRight = true;
            }
            else {
                selectRight = false;
            }

            if (rawPower < 0 && selectRight){
                rawDirection = -rawDirection;
            }
            else if (rawPower > 0 && !selectRight){
                rawDirection = -rawDirection;
            }

            // Tiny bit of optimisation here.
            if (selectRight){
                rightPower = rawPower - rawDirection;
                leftPower = rawPower;
            }
            else{
                rightPower = rawPower;
                leftPower = rawPower - rawDirection;
            }

            telemetry.addData("rawPower: ", rawPower);
            telemetry.addData("rawDirection: ", rawDirection);
            telemetry.addData("selectRight?: ", selectRight);
            telemetry.addData("rightPower ", rightPower);
            telemetry.addData("leftPower: ", leftPower);
            telemetry.update();



        }
    }
}
