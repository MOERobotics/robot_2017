package org.usfirst.frc.team365.math;

import edu.wpi.first.wpilibj.PIDOutput;

public class PIDOut  implements PIDOutput
{
	private double output;
	@Override
	public void pidWrite(double output){
		this.output=output;
	}
	public double getOutput(){
		return output;
	}
}
