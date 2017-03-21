/*package org.usfirst.frc157.ProtoBot2017.subsystems;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.utils.Converters;

import edu.wpi.cscore.CvSink;
import edu.wpi.cscore.CvSource;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import org.usfirst.frc157.ProtoBot2017.visionPipeLines.*;
/+*
 *
 -/
public class Vision extends Subsystem {

	// Put methods for controlling this subsystem
	// here. Call these from Commands.

	// Target Centers for desired target locations
	///////////////////////////////////////////////////////////
	// Use the smart dashboard target locations to set the target.
	private static final int BOILER_NEAR_TARGET_CENTER_X = 320;
	private static final int BOILER_NEAR_TARGET_CENTER_Y = 120;

	private static final int BOILER_FAR_TARGET_CENTER_X = 320;
	private static final int BOILER_FAR_TARGET_CENTER_Y = 60;

	private static final int GEAR_TARGET_CENTER_X = 440;
	private static final int GEAR_TARGET_CENTER_Y = 440;
	///////////////////////////////////////////////////////////

	
	public static final int CAM_WIDTH = 640;
	public static final int CAM_HEIGHT = 480;

	//private static final int CAM_EXPOSURE = 25;
	private static final int CAM_EXPOSURE = 50;
	// B,   G,   R
	private static final Scalar RED = new Scalar(new double[]     {0,   0,   255});
	private static final Scalar YELLOW = new Scalar(new double[]  {0,   255, 255});
	private static final Scalar GREEN = new Scalar(new double[]   {0,   255, 0});
	private static final Scalar BLUE = new Scalar(new double[]    {255, 0,   0});
	private static final Scalar MAGENTA = new Scalar(new double[] {255, 0,   255});
	private static final Scalar BLACK = new Scalar(new double[]   {0,   0,   0});
	private static final Scalar CYAN = new Scalar(new double[]    {255, 255, 0});

	private static final double BOILER_UPPER_TARGET_AR = 0;
	private static final double BOILER_LOWER_TARGET_AR = 0;

	private static final double GEAR_TARGET_AR = 0;

	private static final double ANGLE_TOLERANCE = 20;

	private static final int BOILER_TARGET_WIDTH = 120;
	private static final int BOILER_TARGET_HEIGHT = 40;

	private static final int GEAR_TARGET_WIDTH = 80;
	private static final int GEAR_TARGET_HEIGHT = 80;

	private static final Rect NEAR_BOILER_TARGET = new Rect(BOILER_NEAR_TARGET_CENTER_X, BOILER_NEAR_TARGET_CENTER_Y, BOILER_TARGET_WIDTH, BOILER_TARGET_HEIGHT);
	private static final Rect FAR_BOILER_TARGET = new Rect(BOILER_FAR_TARGET_CENTER_X, BOILER_FAR_TARGET_CENTER_Y, BOILER_TARGET_WIDTH, BOILER_TARGET_HEIGHT);
	
	private static final Rect GEAR_TARGET = new Rect(new Point(GEAR_TARGET_CENTER_X - GEAR_TARGET_WIDTH/2, GEAR_TARGET_CENTER_Y - GEAR_TARGET_HEIGHT/2), 
			new Point(GEAR_TARGET_CENTER_X + GEAR_TARGET_WIDTH/2, GEAR_TARGET_CENTER_Y + GEAR_TARGET_HEIGHT/2));

	private Object pictureSync = new Object();
	private boolean takePicture = false;
	int loopCount = 0;



	public enum TargetID
	{
		BOILER,
		GEAR,
		NONE
	}
	
	public enum BoilerRange
	{
		NEAR,
		FAR
	}
	
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
	private BoilerRange boilerRange = BoilerRange.NEAR;
	private CameraSelection cameraSelection = CameraSelection.SHOT_CAMERA;

	public Rect getBoilerTargetRect(BoilerRange range)
	{
		switch(range)
		{
		case NEAR:
			return NEAR_BOILER_TARGET;
		case FAR:
			return FAR_BOILER_TARGET;
		default:
			return NEAR_BOILER_TARGET;
		}
	
	}
	
	public Point getBoilerTargetCenter(BoilerRange range)
	{
		switch(range)
		{
		case NEAR:
			return new Point(BOILER_NEAR_TARGET_CENTER_X, BOILER_NEAR_TARGET_CENTER_Y);
		case FAR:
			return new Point(BOILER_FAR_TARGET_CENTER_X, BOILER_FAR_TARGET_CENTER_Y);
		default:
			return new Point(BOILER_NEAR_TARGET_CENTER_X, BOILER_NEAR_TARGET_CENTER_Y);
		}
	}

	public Point getGearTargetCenter()
	{
		return new Point(GEAR_TARGET_CENTER_X, GEAR_TARGET_CENTER_Y);
	}
	public void setVisionMode(VisionMode newMode, BoilerRange newRange)
	{
		synchronized(VisionSetup)
		{
			visionMode = newMode;
			boilerRange = newRange;
		}
		SmartDashboard.putString("VisionMode", visionMode.name());
		synchronized(syncLoopCount)
		{
			SmartDashboard.putDouble("VisionLoopCount", loopCount);
		}
	}

	public BoilerRange getBoilerTargetRange()
	{
			return boilerRange;
	}
	
	public VisionMode getVisionMode()
	{
		return visionMode;
	}

	public void storePictures()
	{
		synchronized(pictureSync)
		{
			takePicture = true;
		}		
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


	public class VisionTarget
	{
		public double x;
		public double y;
		public int loopCount;
		public boolean recentTarget;
		public TargetID target;
		public double crossTrack;
		public boolean inRange;

		VisionTarget(){
			x = 0;
			y = 0;
			crossTrack = 0;
			inRange = false;

			loopCount = 0;
			recentTarget = false;
			target = TargetID.NONE;
		}
	}

	VisionTarget visionResults = new VisionTarget();

	public VisionTarget getTarget()
	{
		VisionTarget result = new VisionTarget();

		// 
		synchronized(visionResults)
		{
			result.x = visionResults.x;
			result.y = visionResults.y;
			result.crossTrack = visionResults.crossTrack;
			result.target = visionResults.target;
			result.loopCount = visionResults.loopCount;
			result.inRange = visionResults.inRange;
			result.recentTarget = false;
		}		
		synchronized(syncLoopCount)
		{
			if (Math.abs(loopCount - result.loopCount) < 3)
			{
				result.recentTarget = true;
			}
		}
		return result;
	}

	boolean visionInitialized = false;
	Thread visionThread;

	private static final LocateBoiler findBoiler = new LocateBoiler();
	private static final LocateGear findGear = new LocateGear();

	private static final String SHOT_CAM_NAME = new String("cam1");
	private static final String GEAR_CAM_NAME = new String("cam2"); //cam2

	private UsbCamera shotCamera = new UsbCamera(SHOT_CAM_NAME, 0);		// Shot Camera		
	//	private UsbCamera gearCamera = new UsbCamera(GEAR_CAM_NAME, 0);		// Gear Camera		

	private String NameBase = "NoTime";
	
	public Vision()
	{
		setCamera(CameraSelection.SHOT_CAMERA);
		setVisionMode(VisionMode.FIND_BOILER, BoilerRange.NEAR);
		shotCamera.setResolution(CAM_WIDTH, CAM_HEIGHT);
		//		gearCamera.setResolution(CAM_WIDTH, CAM_HEIGHT);

		InitializeVision();
		System.out.println("Vision Started");
	}


	private void InitializeVision() {

		Point targetCenter = new Point(CAM_WIDTH/2, CAM_HEIGHT/2);
		NameBase = "/home/lvuser/images/"+ LocalDateTime.now() + "-";
		
		if(visionInitialized == false)
		{
			visionInitialized = true;
			visionThread = new Thread(() -> {
				System.out.println("Starting Vision Thread");

						
				shotCamera.setResolution(CAM_WIDTH, CAM_HEIGHT);
				shotCamera.setExposureManual(CAM_EXPOSURE);

				CvSink shotSink = null; 
				try
				{
					// getting video will throw an exception if the camera has not been added
					System.out.println("Getting USB Cam - " + SHOT_CAM_NAME);
					shotSink = CameraServer.getInstance().getVideo(SHOT_CAM_NAME);
				}
				catch(Exception e)
				{
					// so if the camera has not been added, add it.
					System.out.println("Needed to add camera before using - " + SHOT_CAM_NAME + " all set");
					CameraServer.getInstance().addCamera(shotCamera);
					shotSink = CameraServer.getInstance().getVideo(SHOT_CAM_NAME);
				}
				// Setup the Shot Cam
//				CameraServer.getInstance().addCamera(shotCamera);
//				shotCamera.setResolution(CAM_WIDTH, CAM_HEIGHT);
//				shotCamera.setExposureManual(CAM_EXPOSURE);
//				CvSink shotSink = CameraServer.getInstance().getVideo(SHOT_CAM_NAME);

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

				/////////////////////////////////////////////////////////////////////////////////////////////////////////////////
				/////////////////////////////////////////////////////////////////////////////////////////////////////////////////
				//  Beginning of vision thread loop
				/////////////////////////////////////////////////////////////////////////////////////////////////////////////////
				/////////////////////////////////////////////////////////////////////////////////////////////////////////////////				
				while (!Thread.interrupted()) {
					boolean loopTakePicture = false;
					synchronized(syncLoopCount)
					{
						loopCount ++;
					}
					synchronized(pictureSync)
					{
						if(takePicture == true)
						{
							takePicture = false;
							loopTakePicture = true;
						}
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

					if(loopTakePicture == true)
					{
						Imgcodecs.imwrite(NameBase + loopCount + "_base.jpg", mat);
						System.out.println("Saved Base Image");
					}

					////////////////////////////////////////////////////////////////////////////////////////
					////////////////////////////////////////////////////////////////////////////////////////
					//  LOOK FOR BOILER TARGET
					////////////////////////////////////////////////////////////////////////////////////////
					////////////////////////////////////////////////////////////////////////////////////////
					// BOILER BOILER BOILER BOILER BOILER BOILER BOILER BOILER BOILER BOILER BOILER BOILER 
					if(threadVisionMode == VisionMode.FIND_BOILER)
					{
						targetCenter.x= CAM_WIDTH/2;
						targetCenter.y= CAM_HEIGHT/2;

						findBoiler.process(mat);

						ArrayList<MatOfPoint>  targetList = findBoiler.convexHullsOutput();

						drawTargetCrosshair(mat, getBoilerTargetRect(boilerRange), Vision.MAGENTA);

						if(targetList.isEmpty())
						{
							//drawBoilerTargetReticle(mat, targetCenter, CAM_HEIGHT, CAM_WIDTH, Vision.RED);
						}
						else
						{
							// Show the detected contours
							Imgproc.drawContours(mat, targetList, -1, Vision.BLACK, 1);

							ArrayList<RotatedRect> candidateList = new ArrayList<RotatedRect>();							
							// Preprocess the contours
							targetList.forEach(contour -> {
								// convert contour to rect compatible change
								MatOfPoint2f contourMat = new MatOfPoint2f();
								contour.convertTo(contourMat, CvType.CV_32F);								
								candidateList.add(Imgproc.minAreaRect(contourMat));
							});

							// Walk the candidate rectangles and sort out if they are nice
							CandidateLoop:
							for(RotatedRect candidate : candidateList) {
								//							candidateList.forEach(candidate -> {

								// show candidate target aligined rectangle info
								drawRotRectangle(mat, candidate, Vision.MAGENTA);

								// figure out which target is the Boiler Upper Stripe
								double aspectRatio = candidate.size.width / candidate.size.height;
								if(aspectRatio < 1) {aspectRatio = 1/aspectRatio;}

								double fixedCAngle = 0;

								if(candidate.size.width < candidate.size.height){
									fixedCAngle = (90 - candidate.angle) - 180;
								}else{
									fixedCAngle = 0  - candidate.angle;
								}

								if(     ((2 < aspectRatio) && (aspectRatio < 5)) &&
										((-ANGLE_TOLERANCE < fixedCAngle) && (fixedCAngle < ANGLE_TOLERANCE))
										)
								{
									//				    				drawGearTargetReticle(mat, candidate.center, candidate.size.height, candidate.size.width,  Vision.BLUE);

									// look at the targets and see if any are a good seconodary verification for the candidate
									for(RotatedRect secondary : candidateList) {
										//											candidateList.forEach(secondary -> {
										// Check Aspect Ratio & Orientation

										double aspectRatioSecondary = secondary.size.width / secondary.size.height;
										if(aspectRatioSecondary < 1) {aspectRatioSecondary = 1/aspectRatioSecondary;}

										double fixedSAngle = 0;

										if(secondary.size.width < secondary.size.height){
											fixedSAngle = (90 - secondary.angle) - 180;
										}else{
											fixedSAngle = -secondary.angle;
										}
										// basic OKness of secondary
										if(     ((3 < aspectRatioSecondary) && (aspectRatioSecondary < 12)) &&
												((-ANGLE_TOLERANCE < fixedSAngle) && (fixedSAngle < ANGLE_TOLERANCE))
												)
										{

											// draw a yellow box
											drawBoilerTargetReticle(mat, candidate.center, candidate.size.height, candidate.size.width,  Vision.YELLOW);

											double candidateHeight = (candidate.size.height < candidate.size.width) ? candidate.size.height : candidate.size.width;
											double candidateWidth = (candidate.size.height < candidate.size.width) ? candidate.size.width : candidate.size.height;

											// OKness of secondary with respect to target
											if(((candidate.center.y - secondary.center.y) < (candidateHeight * 2.5))
													&&
													(secondary.center.x != candidate.center.x) &&
													(secondary.center.y != candidate.center.y) &&
													(candidate.center.x - secondary.center.x) < (candidateWidth * 1.5) 
													)
											{
												drawCandidateCrosshair(mat, candidate, Vision.YELLOW);
												Imgproc.line(mat, candidate.center, secondary.center,  Vision.YELLOW, 2);
												// output target info to smart dashboard
												SmartDashboard.putDouble("TargetWidth", candidate.size.width);
												SmartDashboard.putDouble("TargetHeight", candidate.size.height);
												SmartDashboard.putDouble("TargetX", candidate.center.x);
												SmartDashboard.putDouble("TargetY", candidate.center.y);
												// save target info
												// x,y,loopCount
												synchronized(visionResults)
												{
													visionResults.x = candidate.center.x;
													visionResults.y = candidate.center.y;
													visionResults.inRange = true;
													visionResults.crossTrack = 0;
													visionResults.loopCount = loopCount;
													visionResults.target = TargetID.BOILER;							    					
												}
												if(takePicture == true)
												{
													Imgcodecs.imwrite(NameBase + loopCount + "_boiler.jpg", mat);
													System.out.println("Saved Boiler Image");
												}
												break CandidateLoop;
											}
										}
										//											} );
									}
								}

								//								drawBoilerTargetReticle(mat, candidate.center, candidate.size.height, candidate.size.width,  Vision.YELLOW);
								if(takePicture == true)
								{
									Imgcodecs.imwrite(NameBase +  loopCount + "_boiler_final.jpg", mat);
									System.out.println("Saved Boiler final Image");
								}
							}
							//							});

						}

						//						drawBoilerTargetReticle(mat, targetCenter, CAM_HEIGHT/3, CAM_WIDTH/3, Vision.MAGENTA);
					}
					////////////////////////////////////////////////////////////////////////////////////////
					////////////////////////////////////////////////////////////////////////////////////////
					//  LOOK FOR GEAR TARGET
					////////////////////////////////////////////////////////////////////////////////////////
					////////////////////////////////////////////////////////////////////////////////////////
					// GEAR GEAR GEAR GEAR GEAR GEAR GEAR GEAR GEAR GEAR GEAR GEAR GEAR GEAR GEAR GEAR GEAR GEAR 
					else if(threadVisionMode == VisionMode.FIND_GEAR)
					{
						targetCenter.x= CAM_WIDTH/2;
						targetCenter.y= CAM_HEIGHT/2;

						findGear.process(mat);

						ArrayList<MatOfPoint> targetList = findGear.convexHullsOutput();

						drawTargetCrosshair(mat, GEAR_TARGET, Vision.CYAN);

						if(targetList.isEmpty())
						{
							//							drawGearTargetReticle(mat, targetCenter, CAM_HEIGHT, CAM_WIDTH, Vision.RED);
						}
						else
						{
							boolean inRange = false;
							
							// Show the detected contours
							Imgproc.drawContours(mat, targetList, -1, Vision.BLACK, 1);

							ArrayList<RotatedRect> candidateList = new ArrayList<RotatedRect>();							
							// Preprocess the contours
							targetList.forEach(contour -> {
								// convert contour to rect compatible change
								MatOfPoint2f contourMat = new MatOfPoint2f();
								contour.convertTo(contourMat, CvType.CV_32F);								
								candidateList.add(Imgproc.minAreaRect(contourMat));
							});

							// Walk the candidate rectangles and sort out if they are nice
							CandidateLoop:
							for(RotatedRect candidate : candidateList) {
								//							candidateList.forEach(candidate -> {

								// show candidate target aligined rectangle info
								drawRotRectangle(mat, candidate, Vision.CYAN);

								// figure out which target is the Boiler Upper Stripe
								double aspectRatio = candidate.size.width / candidate.size.height;
								if(aspectRatio < 1) {aspectRatio = 1/aspectRatio;}

								double fixedCAngle = 0;

								if(candidate.size.width < candidate.size.height){
									fixedCAngle = (90 - candidate.angle);
								}else{
									fixedCAngle = 0  - candidate.angle;
								}
								SmartDashboard.putDouble("TargetAngle", fixedCAngle);

								if(     ((1 < aspectRatio) && (aspectRatio < 3)) 
										&&
										(((90-ANGLE_TOLERANCE) < fixedCAngle) && (fixedCAngle < (90+ANGLE_TOLERANCE)))
										)
								{
									//				    				drawGearTargetReticle(mat, candidate.center, candidate.size.height, candidate.size.width,  Vision.BLUE);

									for(RotatedRect secondary : candidateList) {
										//									candidateList.forEach(secondary -> {
										// Check Aspect Ratio & Orientation

										double aspectRatioSecondary = secondary.size.width / secondary.size.height;
										if(aspectRatioSecondary < 1) {aspectRatioSecondary = 1/aspectRatioSecondary;}

										double fixedSAngle = 0;

										if(secondary.size.width < secondary.size.height){
											fixedSAngle = (90 - secondary.angle);
										}else{
											fixedSAngle = 0 - secondary.angle;
										}
										// basic OKness of secondary
										if(     //((1 < aspectRatioSecondary) && (aspectRatioSecondary < 4)) 
												//&&
												(((90-ANGLE_TOLERANCE) < fixedSAngle) && (fixedSAngle < (90+ANGLE_TOLERANCE)))
												)
										{

											// draw a yellow box
											drawGearTargetReticle(mat, candidate.center,  candidate.size.width,  candidate.size.height, Vision.YELLOW);

											double candidateHeight = (candidate.size.height > candidate.size.width) ? candidate.size.height : candidate.size.width;
											double candidateWidth = (candidate.size.height > candidate.size.width) ? candidate.size.width : candidate.size.height;

											// OKness of secondary with respect to target
											if(((candidate.center.x - secondary.center.x) < (candidateWidth * 4))
													&&
													(secondary.center.x != candidate.center.x)
													&& (Math.abs(candidate.center.y - secondary.center.y) < (Math.max(candidate.size.height, candidate.size.width) * 0.5))
													)
											{
												drawCandidateCrosshair(mat, candidate, secondary, Vision.YELLOW);

												
												// compute crosstrack & apect
												double secondaryHeight = (secondary.size.height > secondary.size.width) ? secondary.size.height : secondary.size.width;
												double secondaryWidth = (secondary.size.height > secondary.size.width) ? secondary.size.width : secondary.size.height;
												double hheight = Math.max(candidateHeight, secondaryHeight)/2;
												double left = Math.min(candidate.center.x - candidateWidth/2, secondary.center.x - secondaryWidth/2);
												double right = Math.max(candidate.center.x + candidateWidth/2, secondary.center.x + secondaryWidth/2);

												double aspect = (right-left)/hheight;

												
												double crossTrack = candidateHeight - secondaryHeight;
												if(candidate.center.x > secondary.center.x)
												{
													crossTrack = -crossTrack;
												}
												if(aspect > 3.8)
												{
													crossTrack = 0;
												}
												
												if(Math.max(candidateHeight, secondaryHeight) > 25)
												{
													inRange = true;
												}
												

												Imgproc.line(mat, candidate.center, secondary.center,  Vision.YELLOW, 2);
												// output target info to smart dashboard
												SmartDashboard.putDouble("TargetWidth", candidateWidth);
												SmartDashboard.putDouble("TargetHeight", candidateHeight);
												SmartDashboard.putDouble("ConfirmWidth", secondaryWidth);
												SmartDashboard.putDouble("ConfirmHeight", secondaryHeight);
												SmartDashboard.putDouble("TargetX", candidate.center.x);
												SmartDashboard.putDouble("TargetY", candidate.center.y);
												
												SmartDashboard.putDouble("GTargetAspect", aspect);
												SmartDashboard.putDouble("GTargetCross", crossTrack);
												// save target info
												// x,y,loopCount
												synchronized(visionResults)
												{
													visionResults.x = candidate.center.x;
													visionResults.y = candidate.center.y;
													visionResults.crossTrack = crossTrack;
													visionResults.loopCount = loopCount;
													visionResults.target = TargetID.GEAR;
													visionResults.inRange = inRange;
												}
												if(takePicture == true)
												{
													Imgcodecs.imwrite(NameBase + loopCount + "_gear.jpg", mat);
													System.out.println("Saved Gear Image");
												}
												break CandidateLoop;
											}
										}
									}
									//									});
								}

							}
							if(takePicture == true)
							{
								Imgcodecs.imwrite(NameBase + loopCount + "_gear_final.jpg", mat);
								System.out.println("Saved Gear final Image");
							}
							//							});

						}

					}
					////////////////////////////////////////////////////////////////////////////////////////
					////////////////////////////////////////////////////////////////////////////////////////
					//  PASSTHROUGH
					////////////////////////////////////////////////////////////////////////////////////////
					////////////////////////////////////////////////////////////////////////////////////////
					// PASSTHROUGH PASSTHROUGH PASSTHROUGH PASSTHROUGH PASSTHROUGH PASSTHROUGH PASSTHROUGH  
					else  // VisionMode.PASSTHROUGH
					{
						// do no image processing, 
						// Circular Target, Centered in Blue
						targetCenter.x= CAM_WIDTH/2;
						targetCenter.y= CAM_HEIGHT/2;						
						if(takePicture == true)
						{
							Imgcodecs.imwrite(NameBase +  loopCount + "_passthru.jpg", mat);
							System.out.println("Saved Passthrough Image");
						}
						drawPassthroughTargetReticle(mat, targetCenter, CAM_HEIGHT/3, CAM_WIDTH/3, Vision.BLUE);
					}

					// Put a rectangle on the image
					//					// TODO - Put Rectangle on TARGET
					//					Imgproc.rectangle(mat, new Point(100, 100), new Point(400, 400),
					//							targetColor, 5);
					// Give the output stream a new image to display
					outputStream.putFrame(mat);
					if(loopTakePicture == true)
					{
						loopTakePicture = false;
						Imgcodecs.imwrite(NameBase +  loopCount + "_passthru_final.jpg", mat);
					}
					//
				}
			});
			visionThread.setDaemon(true);
			visionThread.start();
		}
		System.out.println("Vision Thread Initialized");
	}


	public void initDefaultCommand() {
		// Set the default command for a subsystem here.
		//setDefaultCommand(new MySpecialCommand());
	}


	private void drawRotRectangle(Mat mat, RotatedRect rect, Scalar color) {

		double width = (rect.size.width > rect.size.height) ? rect.size.width :rect.size.height;
		double height = (rect.size.width > rect.size.height) ? rect.size.height : rect.size.width;

		double hheight = height/2;
		double hwidth = width/2;	
		// Vertical Extent
		Imgproc.line(mat, new Point(rect.center.x, rect.center.y - hheight),
				new Point(rect.center.x, rect.center.y + hheight),
				color, 2);
		// Horizontal Extent
		Imgproc.line(mat, new Point(rect.center.x - hwidth, rect.center.y),
				new Point(rect.center.x + hwidth, rect.center.y),
				color, 2);
	}

	private void drawGearTargetReticle(Mat mat, Point center, double in_height, double in_width, Scalar color)
	{
		double width = (in_width < in_height) ? in_width :in_height;
		double height = (in_width < in_height) ? in_height : in_width;

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
	private void drawBoilerTargetReticle(Mat mat, Point center, double in_height, double in_width, Scalar color)
	{
		double width = (in_width > in_height) ? in_width :in_height;
		double height = (in_width > in_height) ? in_height : in_width;

		double hheight = height/2;
		double hwidth = width/2;
		Imgproc.rectangle(mat, new Point(center.x-hwidth, center.y+hheight), new Point(center.x+hwidth, center.y-hheight),
				color, 2);
		Imgproc.line(mat, new Point(center.x, center.y), 
				new Point(center.x+hwidth, center.y-hheight), 
				color, 2);
		Imgproc.line(mat, new Point(center.x, center.y), 
				new Point(center.x-hwidth, center.y-hheight), 
				color, 2);

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

	private void drawTargetCrosshair(Mat mat, Rect target, Scalar targetColor)
	{
		
		// V
		Imgproc.line(mat, new Point(target.x, 0), new Point(target.x, CAM_HEIGHT), targetColor, 3);
		// H
		Imgproc.line(mat, new Point(0, target.y ), new Point(CAM_WIDTH, target.y), targetColor, 3);
		
//		// Draw Target Lines
//		double hheight = target.height/2;
//		double hwidth = target.width/2;
//		if(hheight > hwidth)
//		{
//			hheight = target.width/2;
//			hwidth = target.height/2;
//		}
//		// Target Lines
//		// vertical lines
//		// left
//		Imgproc.line(mat, new Point(target.x - hwidth, 0), 
//				          new Point(target.x - hwidth, CAM_HEIGHT), 
//				targetColor, 3);
//		// right
//		Imgproc.line(mat, new Point(target.x + hwidth, 0), 
//				          new Point(target.x + hwidth, CAM_HEIGHT), 
//				targetColor, 3);
//
//		// horizontal lines
//		// upper
//		Imgproc.line(mat, new Point(0,         target.y - hheight), 
//				new Point(CAM_WIDTH, target.y - hheight), 
//				targetColor, 3);
//		//lower
//		Imgproc.line(mat, new Point(0,         target.y + hheight), 
//				new Point(CAM_WIDTH, target.y + hheight), 
//				targetColor, 3);
	}

	private void drawCandidateCrosshair(Mat mat, RotatedRect candidate, Scalar candidateColor)
	{
		//H
		Imgproc.line(mat, new Point(0,         candidate.center.y), 
				new Point(CAM_WIDTH, candidate.center.y), 
				candidateColor, 2);
		
		//V
		Imgproc.line(mat, new Point(candidate.center.x, 0), 
				new Point(candidate.center.x, CAM_HEIGHT), 
				candidateColor, 2);
		
//		// Draw Candidate Lines
//		double hheight = candidate.size.height/2;
//		double hwidth = candidate.size.width/2;
//		if(hheight > hwidth)
//		{
//			hheight = candidate.size.width/2;
//			hwidth = candidate.size.height/2;
//
//		}
//		// Target Lines
//		// vertical lines
//		// left
//		Imgproc.line(mat, new Point(candidate.center.x - hwidth, 0), 
//				new Point(candidate.center.x - hwidth, CAM_HEIGHT), 
//				candidateColor, 2);
//		// right
//		Imgproc.line(mat, new Point(candidate.center.x + hwidth, 0), 
//				new Point(candidate.center.x + hwidth, CAM_HEIGHT), 
//				candidateColor, 2);
//
//		// horizontal lines
//		// upper
//		Imgproc.line(mat, new Point(0,         candidate.center.y - hheight), 
//				new Point(CAM_WIDTH, candidate.center.y - hheight), 
//				candidateColor, 2);
//		//lower
//		Imgproc.line(mat, new Point(0,         candidate.center.y + hheight), 
//				new Point(CAM_WIDTH, candidate.center.y + hheight), 
//				candidateColor, 2);  	
	}
	private void drawCandidateCrosshair(Mat mat, RotatedRect candidate, RotatedRect secondary, Scalar candidateColor)
	{


		// Draw Candidate Lines
		double candidateHeight = Math.max(candidate.size.width, candidate.size.height);
		double secondaryHeight = Math.max(secondary.size.width, secondary.size.height);

		double hheight = Math.max(candidateHeight, secondaryHeight)/2;

		double candidateWidth = Math.min(candidate.size.width, candidate.size.height);
		double secondaryWidth = Math.min(secondary.size.width, secondary.size.height);

		double left = Math.min(candidate.center.x - candidateWidth/2, secondary.center.x - secondaryWidth/2);
		double right = Math.max(candidate.center.x + candidateWidth/2, secondary.center.x + secondaryWidth/2);

		double hwidth = Math.abs(right-left)/2;

		Point center = new Point((candidate.center.x + secondary.center.x)/2,(candidate.center.y + secondary.center.y)/2);
		// Target Lines
		// vertical lines
		// left
//		Imgproc.line(mat, new Point(center.x - hwidth, 0), 
//				new Point(center.x - hwidth, CAM_HEIGHT), 
//				candidateColor, 2);
//		// right
//		Imgproc.line(mat, new Point(center.x + hwidth, 0), 
//				new Point(center.x + hwidth, CAM_HEIGHT), 
//				candidateColor, 2);
//
//		// horizontal lines
//		// upper
//		Imgproc.line(mat, new Point(0,         center.y - hheight), 
//				new Point(CAM_WIDTH, center.y - hheight), 
//				candidateColor, 2);
//		//lower
//		Imgproc.line(mat, new Point(0,         center.y + hheight), 
//				new Point(CAM_WIDTH, center.y + hheight), 
//				candidateColor, 2);  	

		//H
		Imgproc.line(mat, new Point(0,        center.y), 
				new Point(CAM_WIDTH, center.y), 
				candidateColor, 2);
		
		//V
		Imgproc.line(mat, new Point(center.x, 0), 
				new Point(center.x, CAM_HEIGHT), 
				candidateColor, 2);

	}


	private double scoreBoilerTarget(MatOfPoint target)
	{
		double aspectRatio = target.height()/target.width();

		return 1;
	}

}
*/