	package org.usfirst.frc157.ProtoBot2017.subsystems;

import java.util.ArrayList;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.opencv.utils.Converters;

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
//	private static final int CAM_WIDTH = 320;
//	private static final int CAM_HEIGHT = 240;
//	private static final int CAM_WIDTH = 160;
//	private static final int CAM_HEIGHT = 120;
		                                                        // B,   G,   R
	private static final Scalar RED = new Scalar(new double[]     {0,   0,   255});
	private static final Scalar YELLOW = new Scalar(new double[]  {0,   255, 255});
	private static final Scalar GREEN = new Scalar(new double[]   {0,   255, 0});
	private static final Scalar BLUE = new Scalar(new double[]    {255, 0,   0});
	private static final Scalar MAGENTA = new Scalar(new double[] {255, 0,   255});

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

	private Object VisionSetup = new Object();
	private Object syncLoopCount = new Object();
	private VisionMode visionMode = VisionMode.PASSTHROUGH;
	private CameraSelection cameraSelection = CameraSelection.SHOT_CAMERA;
	
	public void setVisionMode(VisionMode newMode)
	{
		synchronized(VisionSetup)
		{
			visionMode = newMode;
		}
    	SmartDashboard.putString("VisionMode", visionMode.name());
		synchronized(syncLoopCount)
		{
			SmartDashboard.putDouble("VisionLoopCount", loopCount);
		}
	}
	
	public VisionMode getVisionMode()
	{
		return visionMode;
	}

	public void setCamera(CameraSelection newCamera)
	{
		synchronized(VisionSetup)
		{
			cameraSelection = newCamera;
		}
    	SmartDashboard.putString("Camera", cameraSelection.name());
		synchronized(syncLoopCount)
		{
			SmartDashboard.putDouble("VisionLoopCount", loopCount);
		}
	}
	
	public CameraSelection getCamera()
	{
		return cameraSelection;
	}
	
	
	boolean visionInitialized = false;
	Thread visionThread;
	
	int loopCount = 0;
	
	private static final LocateBoiler findBoiler = new LocateBoiler();
	private static final LocateGear findGear = new LocateGear();

	private static final String SHOT_CAM_NAME = new String("cam1");
	private static final String GEAR_CAM_NAME = new String("cam2"); //cam2
	
	private UsbCamera shotCamera = new UsbCamera(SHOT_CAM_NAME, 0);		// Shot Camera		
//	private UsbCamera gearCamera = new UsbCamera(GEAR_CAM_NAME, 0);		// Gear Camera		

	public Vision()
	{
		setCamera(CameraSelection.SHOT_CAMERA);
		setVisionMode(VisionMode.FIND_BOILER);
		shotCamera.setResolution(CAM_WIDTH, CAM_HEIGHT);
//		gearCamera.setResolution(CAM_WIDTH, CAM_HEIGHT);
		
		InitializeVision();
		System.out.println("Starting Vision");
	}
	
	
	private double minX = 1000;
	private double minY = 1000;
	private double maxX = -1;
	private double maxY = -1;

	private void InitializeVision() {
		
	
		Point targetCenter = new Point(CAM_WIDTH/2, CAM_HEIGHT/2);

		
		if(visionInitialized == false)
		{
			visionInitialized = true;
			visionThread = new Thread(() -> {
				System.out.println("Starting Vision Thread");
				
				Scalar targetColor;
				
				// Setup the Shot Cam
				CameraServer.getInstance().addCamera(shotCamera);
				shotCamera.setResolution(CAM_WIDTH, CAM_HEIGHT);
				CvSink shotSink = CameraServer.getInstance().getVideo(SHOT_CAM_NAME);
				
				// Setup the Gear Cam
//				CameraServer.getInstance().addCamera(gearCamera);
//				gearCamera.setResolution(CAM_WIDTH, CAM_HEIGHT);
//				CvSink gearSink = CameraServer.getInstance().getVideo(GEAR_CAM_NAME);
				CvSink gearSink = shotSink;

				// set up a variable to hold the selected sink
				CvSink theSink = shotSink;  // initialize to something, we will be set before use
								
				// Setup a CvSource. This will send images back to the Dashboard
				CvSource outputStream = CameraServer.getInstance().putVideo("Processed", CAM_WIDTH, CAM_HEIGHT);

				// Mats are very memory expensive. Lets reuse this Mat.
				Mat mat = new Mat();

				// This cannot be 'true'. The program will never exit if it is. This
				// lets the robot stop this thread when restarting robot code or
				// deploying.
				while (!Thread.interrupted()) {
					
					synchronized(syncLoopCount)
					{
						loopCount ++;
					}

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
					   	SmartDashboard.putString("Camera In Use", "Shot");
						theSink = shotSink;
					}
					else
					{
					   	SmartDashboard.putString("Camera In Use", "Gear");
						theSink = gearSink;
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
						
						ArrayList<MatOfPoint>  targetList = findBoiler.convexHullsOutput();
						
						if(targetList.isEmpty())
						{
							drawBoilerTargetReticle(mat, targetCenter, CAM_HEIGHT, CAM_WIDTH, Vision.RED);
						}
						else
						{
							// walk the list looking for the best target
							// best target is none
							targetList.forEach(target -> {
								// Show Targets
								java.util.List<Point> contour = target.toList(); 
								
								// find min/max
								minX = 1000;
								minY = 1000;
								maxX = -1;
								maxY = -1;

								contour.forEach(point -> {
									if((point.x > 0) && (point.y > 0)) {
										if(point.x < minX) minX = point.x;
										if(point.y < minY) minY = point.y;
										if(point.x > maxX) maxX = point.x;
										if(point.y > maxY) maxY = point.y;
									}
								});
								Point targetCenterX = new Point(minX + (maxX-minX)/2, minY + (maxY-minY)/2);
								drawBoilerTargetReticle(mat, targetCenterX, maxY-minY, maxX-minX,  Vision.YELLOW);
								// if target is better than best target
								// save best target
								
							});
						}
						
//						drawBoilerTargetReticle(mat, targetCenter, CAM_HEIGHT/3, CAM_WIDTH/3, Vision.MAGENTA);
					}
					else if(threadVisionMode == VisionMode.FIND_GEAR)
					{
						targetCenter.x= CAM_WIDTH/2;
						targetCenter.y= CAM_HEIGHT/2;
						
						findGear.process(mat);
						
						ArrayList<MatOfPoint>  targetList = findGear.filterContoursOutput();

						if(targetList.isEmpty())
						{
							drawGearTargetReticle(mat, targetCenter, CAM_WIDTH, CAM_HEIGHT, Vision.RED);
						}
						else
						{
							targetList.forEach(target -> {
								// Show Targets
								java.util.List<Point> contour = target.toList(); 
								
								// find min/max
								minX = 1000;
								minY = 1000;
								maxX = -1;
								maxY = -1;

								contour.forEach(point -> {
									if((point.x > 0) && (point.y > 0)) {
										if(point.x < minX) minX = point.x;
										if(point.y < minY) minY = point.y;
										if(point.x > maxX) maxX = point.x;
										if(point.y > maxY) maxY = point.y;
									}
								});
								Point targetCenterX = new Point(minX + (maxX-minX)/2, minY + (maxY-minY)/2);
								drawGearTargetReticle(mat, targetCenterX, maxY-minY, maxX-minX,  Vision.YELLOW);
								// if target is better than best target
								// save best target
								
							});
						}						
//						drawGearTargetReticle( mat,  targetCenter,  CAM_HEIGHT/3, CAM_WIDTH/3,  Vision.MAGENTA);						
					}
					else  // VisionMode.PASSTHROUGH
					{
						// do no image processing, 
						// Circular Target, Centered in Blue
						targetCenter.x= CAM_WIDTH/2;
						targetCenter.y= CAM_HEIGHT/2;						
						targetColor = Vision.BLUE; // Blue for no target
						
						drawPassthroughTargetReticle(mat, targetCenter, CAM_HEIGHT/3, CAM_WIDTH/3, targetColor);
					}
					
					// Put a rectangle on the image
//					// TODO - Put Rectangle on TARGET
//					Imgproc.rectangle(mat, new Point(100, 100), new Point(400, 400),
//							targetColor, 5);
					// Give the output stream a new image to display
					outputStream.putFrame(mat);
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
	
	private void drawGearTargetReticle(Mat mat, Point center, double height, double width, Scalar color)
	{
		double hheight = height/2;
		double hwidth = width/2;
		Imgproc.rectangle(mat, new Point(center.x-hwidth, center.y+hheight), new Point(center.x+hwidth, center.y-hheight),
				color, 5);
		Imgproc.line(mat, new Point(center.x-hwidth, center.y+hheight), 
		                  new Point(center.x+hwidth, center.y-hheight), 
		                  color, 3);
		Imgproc.line(mat, new Point(center.x+hwidth, center.y+hheight), 
                          new Point(center.x-hwidth, center.y-hheight), 
                          color, 3);

	}
	private void drawBoilerTargetReticle(Mat mat, Point center, double height, double width, Scalar color)
	{
		double hheight = height/2;
		double hwidth = width/2;
		Imgproc.rectangle(mat, new Point(center.x-hwidth, center.y+hheight), new Point(center.x+hwidth, center.y-hheight),
				color, 5);
		Imgproc.line(mat, new Point(center.x, center.y), 
				          new Point(center.x+hwidth, center.y-hheight), 
				          color, 3);
		Imgproc.line(mat, new Point(center.x, center.y), 
		                  new Point(center.x-hwidth, center.y-hheight), 
		                  color, 3);
		
	}
	private void drawPassthroughTargetReticle(Mat mat, Point center, double height, double width, Scalar color)
	{
		double hheight = height/2;
		double hwidth = width/2;
		int targetRadius = (int)(hheight/4);
		Imgproc.circle(mat, new Point(center.x, center.y), targetRadius, color);
		
//		Imgproc.rectangle(mat, new Point(center.x-hwidth, center.y+hheight), new Point(center.x+hwidth, center.y-hheight),
//				color, 10);
	}

	private double scoreBoilerTarget(MatOfPoint target)
	{
		double aspectRatio = target.height()/target.width();
		
		return 1;
	}
	
}