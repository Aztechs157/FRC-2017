	package org.usfirst.frc157.ProtoBot2017.subsystems;

import java.util.ArrayList;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import edu.wpi.cscore.CvSink;
import edu.wpi.cscore.CvSource;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import org.usfirst.frc157.ProtoBot2017.visionPipeLines.*;
/**
 *
 */
public class Vision extends Subsystem {

    // Put methods for controlling this subsystem
    // here. Call these from Commands.

	private static final int CAM_WIDTH = 640;
	private static final int CAM_HEIGHT = 480;
	
	private static final int TARGET_CIRCLE_RADIUS = 150;
	
	private static final Scalar RED = new Scalar(new double[] {255, 0, 0});
	private static final Scalar YELLOW = new Scalar(new double[] {255, 255, 0});
	private static final Scalar GREEN = new Scalar(new double[] {0, 255, 0});
	private static final Scalar BLUE = new Scalar(new double[] {0, 0, 255});
	private static final Scalar MAGENTA = new Scalar(new double[] {255, 0, 255});

	private static final double BOILER_UPPER_TARGET_AR = 0;
	private static final double BOILER_LOWER_TARGET_AR = 0;

	private static final double GEAR_TARGET_AR = 0;
	
	
	public enum VisionMode
	{
		PASSTHROUGH,
		FIND_BOILER,
		FIND_GEAR
	}
	
	public enum CameraSelection
	{
		SHOT_CAMERA,
		GEAR_CAMERA
	}

	private Object VisionSetup;
	private VisionMode visionMode = VisionMode.PASSTHROUGH;
	private CameraSelection cameraSelection = CameraSelection.SHOT_CAMERA;
	
	public void setVisionMode(VisionMode newMode)
	{
		synchronized(VisionSetup)
		{
			visionMode = newMode;
		}
	}

	public void setCamera(CameraSelection newCamera)
	{
		synchronized(VisionSetup)
		{
			cameraSelection = newCamera;
		}
	}
	
	
	boolean visionInitialized = false;
	Thread visionThread;
	
	int loopCount = 0;
	
	LocateBoiler findBoiler;
	LocateGear findGear;

	public Vision()
	{
		setCamera(CameraSelection.SHOT_CAMERA);
		setVisionMode(VisionMode.PASSTHROUGH);
		InitializeVision();
		System.out.println("Starting Vision");
	}
	
	private void InitializeVision() {
		
	
		Point targetCenter = new Point(CAM_WIDTH/2, CAM_HEIGHT/2);

		
		if(visionInitialized == false)
		{
			visionThread = new Thread(() -> {
				
				Scalar targetColor;

				// Get the UsbCameras from CameraServer
				UsbCamera shotCamera = CameraServer.getInstance().startAutomaticCapture();
				UsbCamera gearCamera = CameraServer.getInstance().startAutomaticCapture();
				// Set the resolution
				shotCamera.setResolution(CAM_WIDTH, CAM_HEIGHT);
				gearCamera.setResolution(CAM_WIDTH, CAM_HEIGHT);

				// Get the CvSinks. This will capture Mats from the camera
				CvSink shotCvSink = CameraServer.getInstance().getVideo();
				CvSink gearCvSink = CameraServer.getInstance().getVideo();
				
				// set up a variable to hold the selected sink
				CvSink theSink = shotCvSink;  // initialize to something, we will be set before use
								
				// Setup a CvSource. This will send images back to the Dashboard
				CvSource outputStream = CameraServer.getInstance().putVideo("Processed", CAM_WIDTH, CAM_HEIGHT);

				// Mats are very memory expensive. Lets reuse this Mat.
				Mat mat = new Mat();

				// This cannot be 'true'. The program will never exit if it is. This
				// lets the robot stop this thread when restarting robot code or
				// deploying.
				while (!Thread.interrupted()) {
					loopCount ++;

					// local copies of the vision mode and camera selection
					CameraSelection threadCameraSelection;
					VisionMode threadVisionMode;

					synchronized(VisionSetup)
					{
						threadCameraSelection = cameraSelection;
						threadVisionMode = visionMode;							
					}				
					// Tell the CvSink to grab a frame from the camera and put it
					// in the source mat.  If there is an error notify the output.
					
					if(threadCameraSelection == CameraSelection.SHOT_CAMERA)
					{
						theSink = shotCvSink;
					}
					else
					{
						theSink = gearCvSink;
					}
					
					if (theSink.grabFrame(mat) == 0) {
						// Send the output the error.
						outputStream.notifyError(theSink.getError());
						// skip the rest of the current iteration
						continue;
					}
					
					if(threadVisionMode == VisionMode.FIND_BOILER)
					{
						targetCenter.x= CAM_WIDTH/2;
						targetCenter.y= CAM_HEIGHT/2;
						
						findBoiler.process(mat);
						
						ArrayList<MatOfPoint>  targetList = findBoiler.filterContoursOutput();
						
						if(targetList.isEmpty())
						{
							targetColor = Vision.RED; // RED for no target
						}
						else
						{
							// walk the list looking for the best target
							// best target is none
							
							targetList.forEach(target -> {
								// if target is better than best target
								// save best target
								
							});
						}
						
						targetColor = Vision.YELLOW; // Yellow for questionable target
						targetColor = Vision.GREEN;  // Green for good target
						
						drawBoilerTargetReticle(mat, targetCenter, targetColor);
					}
					else if(threadVisionMode == VisionMode.FIND_BOILER)
					{
						targetCenter.x= CAM_WIDTH/2;
						targetCenter.y= CAM_HEIGHT/2;
						
						findGear.process(mat);
						
						ArrayList<MatOfPoint>  targetList = findGear.filterContoursOutput();

						if(targetList.isEmpty())
						{
							targetColor = Vision.RED; // RED for no target
						}
						
						targetColor = Vision.YELLOW; // Yellow for questionable target
						targetColor = Vision.GREEN;  // Green for good target

						drawGearTargetReticle( mat,  targetCenter,  targetColor);						
					}
					else  // VisionMode.PASSTHROUGH
					{
						// do no image processing, 
						// Circular Target, Centered in Blue
						targetCenter.x= CAM_WIDTH/2;
						targetCenter.y= CAM_HEIGHT/2;
						
						targetColor = Vision.BLUE; // Blue for no target
						
						drawPassthroughTargetReticle(mat, targetCenter);
					}
					
					// Put a rectangle on the image
					// TODO - Put Rectangle on TARGET
					Imgproc.rectangle(mat, new Point(100, 100), new Point(400, 400),
							targetColor, 5);
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
	
	private void drawGearTargetReticle(Mat mat, Point center, Scalar color)
	{
		Imgproc.rectangle(mat, new Point(100, 100), new Point(400, 400),
				color, 5);
		Imgproc.line(mat, new Point(center.x-50, center.y+50), 
		                  new Point(center.x+50, center.y-50), 
		                  color, 3);
		Imgproc.line(mat, new Point(center.x-50, center.y+50), 
                          new Point(center.x+50, center.y-50), 
                          color, 3);

	}
	private void drawBoilerTargetReticle(Mat mat, Point center, Scalar color)
	{
		Imgproc.rectangle(mat, new Point(100, 100), new Point(400, 400),
				color, 5);
		Imgproc.line(mat, new Point(center.x-30, center.y), 
				          new Point(center.x+30, center.y), 
				          color, 3);
		Imgproc.line(mat, new Point(center.x-30, center.y), 
		                  new Point(center.x+30, center.y), 
		                  color, 3);
		
	}
	private void drawPassthroughTargetReticle(Mat mat, Point center)
	{
		Imgproc.circle(mat, center, TARGET_CIRCLE_RADIUS,
				Vision.BLUE, 3);
		Imgproc.line(mat, new Point(center.x-30, center.y), 
		                  new Point(center.x+30, center.y), 
		                  Vision.BLUE, 3);
		Imgproc.line(mat, new Point(center.x-30, center.y), 
                          new Point(center.x+30, center.y), 
                          Vision.BLUE, 3);
	}

	private double scoreBoilerTarget(MatOfPoint target)
	{
		double aspectRatio = target.height()/target.width();
		
		return 1;
	}
	
}