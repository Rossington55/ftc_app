// Created by taldonde on 4/12/16.
 package org.firstinspires.ftc.teamcode;
 import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
 import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
 import com.qualcomm.robotcore.hardware.DcMotor;
 import com.qualcomm.robotcore.hardware.Servo;
//declare opmode name
@TeleOp(name="velocityvortex10496", group= "testing")
//declare class
public class velocityvortex10496 extends LinearOpMode {
    //declare motors
    private DcMotor motorRight;
    private DcMotor motorLeft;
    private DcMotor brush;
    //declare servo
    private Servo armServo;
    //declare check brush mode variable
    private boolean brushIsReversed;
    //declare servo positions
    private static final double ARM_RETRACTED_POSITION = 0.1;
    private static final double ARM_EXTENDED_POSITION = 0.9;

    //define power and direction
    private float rawPower;
    private float rawDirection;
    private float leftPower;
    private float rightPower;
    private boolean selectRight;


    @Override public void runOpMode() throws InterruptedException {
        //init motors
        motorRight = hardwareMap.dcMotor.get(motorRight);
        motorLeft = hardwareMap.dcMotor.get(motorLeft);
        brush = hardwareMap.dcMotor.get(brush);


        //reverse so that the robot doesn't spin
        motorLeft.setDirection(DcMotor.Direction.REVERSE);

        //init servo
        armServo = hardwareMap.servo.get(armServo);

        //retract servo to minimal position
        armServo.setPosition(ARM_RETRACTED_POSITION);

        //wait until start is pressed
        waitForStart();

        //loop till stopped
        while (opModeIsActive()) {

            // Take the value of the gamepad Left Stick X(direction) and Right Trigger(power)

            rawPower = (gamepad1.right_trigger);
            rawDirection = (gamepad1.left_stick_x);

            // Select the motor that we will be subtracting power from: Note that this is different to the original program
            // as it doesn't "literally" select it, so it takes a few more lines of code.
            if (rawDirection >= 0) { // If you are trying to turn right, "select" the right motor.
                selectRight = true;
            } else { // Otherwise, don't select it.
                selectRight = false;
            }

            if (rawPower < 0 & selectRight) { // If you are trying to go backwards but also right...
                rawDirection = -rawDirection;
            } else if (rawPower > 0 & !selectRight) { // If you are trying to go forward but left..
                rawDirection = -rawDirection;
            }
            if (selectRight) {
                rightPower = rawPower - rawDirection;
            } else {
                rightPower = rawPower;
            }
            if (!selectRight) {
                leftPower = rawPower - rawDirection;
            } else {
                leftPower = rawPower;
            }

            motorLeft.setPower(leftPower);
            motorRight.setPower(rightPower);


            //brush pick up mode
            if (gamepad1.a) {
                if (brushIsReversed = false) {
                    brush.setDirection(DcMotor.Direction.REVERSE);
                    brushIsReversed = !brushIsReversed;
                    brush.setPower(.5);
                }
                else {
                    brush.setPower(.5);
                }
            }
            else if (gamepad1.b) { // Brush "shooting" setting
                if (brushIsReversed = true) {
                    brush.setDirection(DcMotor.Direction.REVERSE);
                    brushIsReversed = !brushIsReversed;
                    brush.setPower(.5);
                }
                else {
                    brush.setPower(.5);
                }
            }
            else {
                brush.setPower(0);
            }



            if (gamepad1.dpad_up) {
                armServo.setPosition(ARM_EXTENDED_POSITION);
            }
            if (gamepad1.dpad_down) {
                armServo.setPosition(ARM_RETRACTED_POSITION);
            }
            //add telemetries
            telemetry.addData("rawPower: ", rawPower);
            telemetry.addData("rawDirection: ", rawDirection);
            telemetry.addData("selectRight?: ", selectRight);
            telemetry.addData("rightPower ", rightPower);
            telemetry.addData("leftPower: ", leftPower);
            telemetry.addData("brushReversed? ", brushIsReversed);
            telemetry.update();


        }


    }
}
