package org.usfirst.frc.team365.net;

import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.tables.ITable;

public class GripTracker implements MOETracker{
	NetworkTable table;
	ITable contoursReport;
	double xScale, yScale;
	public GripTracker(String tableName){
		table=NetworkTable.getTable(tableName);
		ITable size = table.getSubTable("mySize");
		xScale = size.getNumber("x", -1);
		yScale = size.getNumber("y", -1);
	}
	public void updateReport(){
		contoursReport=table.getSubTable("myContoursReport");
	}
	public synchronized double[] getCenter(){
		//if we add this here:
		updateReport();
		double[] areas=contoursReport.getNumberArray("area", new double[]{});
		double[] xVals=contoursReport.getNumberArray("centerX", new double[]{});
		double[] yVals=contoursReport.getNumberArray("centerY", new double[]{});
		int index=0;
		double max=0;
		if(areas.length<=0){
			return new double[]{-1,-1};
		}else{
			for(int n=0;n<areas.length;n++){
				if(areas[n]>max){
					max=areas[n];
					index=n;
				}
			}
		}return new double[]{xVals[index]/xScale, yVals[index]/yScale};
	}
	public void run(){
		//is this needed here?
		//could it just be here for comliance & be blank or something?
		while(true){
			//updateReport();
		}
	}
}
