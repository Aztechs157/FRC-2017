package org.usfirst.frc157.ProtoBot2017.subsystems;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import edu.wpi.cscore.CvSink;
import edu.wpi.cscore.CvSource;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 *
 */
public class Vision extends Subsystem {

    // Put methods for controlling this subsystem
    // here. Call these from Commands.

	boolean visionInitialized = false;
	Thread visionThread;
	
	int loopCount = 0;
	
	public Vision()
	{
		InitializeVision();
	}
	
	private void InitializeVision() {
		if(visionInitialized == false)
		{
			visionThread = new Thread(() -> {
				// Get the UsbCamera from CameraServer
				UsbCamera camera = CameraServer.getInstance().startAutomaticCapture();
				// Set the resolution
				camera.setResolution(640, 480);

				// Get a CvSink. This will capture Mats from the camera
				CvSink cvSink = CameraServer.getInstance().getVideo();
				// Setup a CvSource. This will send images back to the Dashboard
				CvSource outputStream = CameraServer.getInstance().putVideo("Rectangle", 640, 480);

				// Mats are very memory expensive. Lets reuse this Mat.
				Mat mat = new Mat();

				// This cannot be 'true'. The program will never exit if it is. This
				// lets the robot stop this thread when restarting robot code or
				// deploying.
				while (!Thread.interrupted()) {
					loopCount ++;
					// Tell the CvSink to grab a frame from the camera and put it
					// in the source mat.  If there is an error notify the output.
					if (cvSink.grabFrame(mat) == 0) {
						// Send the output the error.
						outputStream.notifyError(cvSink.getError());
						// skip the rest of the current iteration
						continue;
					}
					// Put a rectangle on the image
					Imgproc.rectangle(mat, new Point(100, 100), new Point(400, 400),
							new Scalar(255, 255, 255), 5);
					// Give the output stream a new image to display
					outputStream.putFrame(mat);
					SmartDashboard.putDouble("VisionLoopCount", loopCount);
				}
			});
			visionThread.setDaemon(true);
			visionThread.start();
		}
	}

	public void initDefaultCommand() {
        // Set the default command for a subsystem here.
        //setDefaultCommand(new MySpecialCommand());
    }
}

;