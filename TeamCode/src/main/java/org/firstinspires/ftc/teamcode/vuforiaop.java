package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.*;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.vuforia.HINT;
import com.vuforia.Vuforia;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.matrices.VectorF;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackableDefaultListener;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackables;

/**
 * Created by taldonde and arifeld on 7/12/16.
 */

@Autonomous // Define this opmode as an autonomous one.
public class VuforiaOP extends LinearOpMode {

    @Override
    public void runOpMode() throws InterruptedException {
        // Init our two motors. hardwareMap.dcMotor.get("motorname") inits it.
        DcMotor motorRight = hardwareMap.dcMotor.get("motorRight");
        motorRight.setDirection(DcMotorSimple.Direction.REVERSE);
        motorRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        DcMotor motorLeft = hardwareMap.dcMotor.get("motorLeft");
        motorLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);


        //display what vuforia is seeing on the phone
        VuforiaLocalizer.Parameters params = new VuforiaLocalizer.Parameters(R.id.cameraMonitorViewId);
        //declare what camera we want Vuforia to use, either "FRONT" or "BACK".
        params.cameraDirection = VuforiaLocalizer.CameraDirection.BACK;
        //vuforia license key - THIS LICENSE KEY IS VALID PLEASE DO NOT CHANGE
        params.vuforiaLicenseKey = "Ac2ZR6T/////AAAAGdIyuKX5yU1WhsL+sBQlI9QhbPH4vz8oCEvf34gr7LGyWt0mzfDJahzBJldwHZZZ/SfMij+6i19yz3xkhQ03sTVqAcrlFwAxPLfU6SWVGub0SKiCPzVVB53l+RruAGNUPRL2jDjBg5LccPCWnFBW5R9ISdxzOo1diqV0uMjIlT46GNuPBXIW56uWkOhtZQLk/dm/0f7TRdsoyFoeE/2E4NIzLH7W/tDfm/q3dlwedS1lVdLXPQ/3dQHDOxf++hECRwuSSOPRfoxKlxr1e31nomJFQN2i/KegOBWV4FmaQpKPx9hj33GNeOHW/I4ode7KeEJaUEijd8HQUncF9dwry7YSoCGF7WiYnAPOvM+eZ17s";
        // What to display on the phone - axes, teapot, buildings etc.
        params.cameraMonitorFeedback = VuforiaLocalizer.Parameters.CameraMonitorFeedback.AXES;

        VuforiaLocalizer vuforia = ClassFactory.createVuforiaLocalizer(params);
        //setHint tells the program how many targets their are on the field.
        //this is used incase the camera sees two different targets, so it doesn't rapidly switch between targets.
        Vuforia.setHint(HINT.HINT_MAX_SIMULTANEOUS_IMAGE_TARGETS, 4);

        VuforiaTrackables beacons = vuforia.loadTrackablesFromAsset("FTC_2016-17");
        // I'm almost certain this
        beacons.get(0).setName("Wheels");
        beacons.get(1).setName("Tools");
        beacons.get(2).setName("Legos");
        beacons.get(3).setName("Gears");

        waitForStart();

        VuforiaTrackableDefaultListener wheels = (VuforiaTrackableDefaultListener) beacons.get(0).getListener();
        beacons.activate();

        motorLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        motorRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        motorLeft.setPower(0.2);
        motorRight.setPower(0.2);

        while (opModeIsActive() && wheels.getRawPose() == null){
            idle(); // idle resets the program back to the start.
        }

        motorLeft.setPower(0);
        motorRight.setPower(0);

        // Analyse beacons here!


        VectorF angles = anglesFromTarget(wheels);

        VectorF trans = navOffWall(wheels.getPose().getTranslation(), Math.toDegrees(angles.get(0)) - 90, new VectorF(500, 0, 0));

        if (trans.get(0) > 0){
            motorLeft.setPower(0.02);
            motorRight.setPower(-0.02);
        }
        else{
            motorLeft.setPower(-0.02);
            motorRight.setPower(0.02);
        }

        do{
            if(wheels.getPose() != null){
                trans = navOffWall(wheels.getPose().getTranslation(), Math.toDegrees(angles.get(0)) - 90, new VectorF(500, 0, 0));
            }
            idle();
        }
        while (opModeIsActive() && Math.abs(trans.get(0)) > 30); // I don't know why { isn't used here.

        motorLeft.setPower(0);
        motorRight.setPower(0);

        motorLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        motorRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        // vvv You may need to add a + (in mm) to the end of the math.hypot depending on where the centre of turning is on from the robot compared to the phone.
        // Ticks per rotation: AndyMark 20's = 560, AndyMark 40's = 1120
        motorLeft.setTargetPosition((int)(motorLeft.getCurrentPosition() + ((Math.hypot(trans.get(0), trans.get(2)) /* +  */ ) /  /* wheel circumfrance */ 409.575 *  /* ticks per rotation*/ 757)));
        motorRight.setTargetPosition((int)(motorRight.getCurrentPosition() + ((Math.hypot(trans.get(0), trans.get(2)) /* +  */ ) /  /* wheel circumfrance */ 409.575 *  /* ticks per rotation*/ 757)));

        motorLeft.setPower(0.3);
        motorRight.setPower(0.3);

        while (opModeIsActive() && motorLeft.isBusy() && motorRight.isBusy()){
            idle();
        }

        motorLeft.setPower(0);
        motorRight.setPower(0);

        motorLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        motorRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        while (opModeIsActive() && (wheels.getPose() == null || Math.abs(wheels.getPose().getTranslation().get(0)) > 10 )){
            if(wheels.getPose() != null){
                if(wheels.getPose().getTranslation().get(0) > 0){
                    motorLeft.setPower(-0.3);
                    motorRight.setPower(0.3);
                }
                else{
                    motorLeft.setPower(0.3);
                    motorRight.setPower(-0.3);
                }
            }
            else {
                motorLeft.setPower(-0.3);
                motorRight.setPower(0.3);
            }
        }

        motorLeft.setPower(0);
        motorRight.setPower(0);




        /*
        // This is telemetry.. probably don't need this in the final program (we don't)
        while (opModeIsActive()) {
            for (VuforiaTrackable beac:beacons) {
                OpenGLMatrix pose = ((VuforiaTrackableDefaultListener) beac.getListener()).getPose();

                if (pose != null) { // Display a bit of telemetry.
                    VectorF translation = pose.getTranslation();
                    telemetry.addData(beac.getName() + "-Translation", translation);

                    double degreesToTurn = Math.toDegrees(Math.atan2(translation.get(1), translation.get(2)));

                    telemetry.addData(beac.getName() + "-Degrees", degreesToTurn);

                }
            }
            telemetry.update()
        }
        */





    }
    public VectorF navOffWall(VectorF trans, double robotAngle, VectorF offWall){ return new VectorF((float) (trans.get(0) - offWall.get(0) * Math.sin(Math.toRadians(robotAngle)) - offWall.get(2) * Math.cos(Math.toRadians(robotAngle))), trans.get(1), (float) (trans.get(2) + offWall.get(0) * Math.cos(Math.toRadians(robotAngle)) - offWall.get(2) * Math.sin(Math.toRadians(robotAngle))));
    }

    public VectorF anglesFromTarget(VuforiaTrackableDefaultListener image){ float [] data = image.getRawPose().getData(); float [] [] rotation = {{data[0], data[1]}, {data[4], data[5], data[6]}, {data[8], data[9], data[10]}}; double thetaX = Math.atan2(rotation[2][1], rotation[2][2]); double thetaY = Math.atan2(-rotation[2][0], Math.sqrt(rotation[2][1] * rotation[2][1] + rotation[2][2] * rotation[2][2])); double thetaZ = Math.atan2(rotation[1][0], rotation[0][0]); return new VectorF((float)thetaX, (float)thetaY, (float)thetaZ);
    }
}
