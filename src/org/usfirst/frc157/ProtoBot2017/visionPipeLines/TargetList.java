package org.usfirst.frc157.ProtoBot2017.visionPipeLines;

import java.util.ArrayList;

import org.opencv.core.MatOfPoint;

public abstract interface TargetList {
	

	public abstract ArrayList<MatOfPoint> getTargets();
	public abstract boolean targetAvailable();
}
