package org.usfirst.frc.team365.net;

import java.util.Arrays;

import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.tables.ITable;

public class GripTracker implements MOETracker{
	NetworkTable table;
	ITable contoursReport;
	public GripTracker(String tableName){
		table=NetworkTable.getTable(tableName);
	}
	public void updateReport(){
		contoursReport=table.getSubTable("myContoursReport");
	}
	protected double getLastX(){
		double[] xVals=contoursReport.getNumberArray("centerX", new double[]{});
		Arrays.sort(xVals);
		return xVals[0];
	}
	protected double getLastY(){
		double[] yVals=contoursReport.getNumberArray("centerY", new double[]{});
		Arrays.sort(yVals);
		return yVals[0];
	}
	public double[] getCenter(){
		//if we add this here:
	//	updateReport();
		return new double[]{getLastX(), getLastY()};
	}
	public void run(){
		//is this needed here?
		//could it just be here for comliance & be blank or something?
		while(true){
			updateReport();
		}
	}
}
