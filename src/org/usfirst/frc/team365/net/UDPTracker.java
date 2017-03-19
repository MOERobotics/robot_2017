package org.usfirst.frc.team365.net;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.Vector;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class UDPTracker implements MOETracker {
	static final short NONE_FOUND = 1;
	static final short ONE_FOUND  = 2;
	static final short TWO_FOUND  = 3;
	
	public static UDPTracker getTracker(String name, final int port) {
		try {
			SmartDashboard.putString("is great", "yes");
			return new UDPTracker(name, port);
		} catch (SocketException e) {
			e.printStackTrace();
			SmartDashboard.putString("is great", "no: "+e.getMessage());
			return null;
		}
	}
	
	/**
	 * Name of this tracker
	 */
	protected final String name;
	/**
	 * Socket that this tracker listens on
	 */
	protected final DatagramSocket socket;
	protected final List<Box> boxes = new ArrayList<>(2);

	public UDPTracker(String name, final int port) throws SocketException {
		this.name = name;
		this.socket = new DatagramSocket(port);
		
	}
	
	@Override
	public void run() {
		ByteBuffer buf = ByteBuffer.allocate(72);
		DatagramPacket packet = new DatagramPacket(buf.array(), buf.limit());
		int lastId = Integer.MIN_VALUE;
		while (!Thread.interrupted()) {
			try {
				buf.position(0);
				packet.setLength(buf.limit());
				socket.receive(packet);
				final int id = buf.getInt();
				if (id <= lastID)
					continue;
				lastID = id;
				final short status = buf.getShort();
				int boxAmnt = status - 1;
				synchronized (this) {
					boxes.clear();
					for(int n = 0; n < boxAmnt; n++)
						boxes.add(readBox(buf));
				}
			} catch (Exception e) {
				e.printStackTrace();
				break;
			}
		}
		socket.close();
	}
	
	private Box readBox(ByteBuffer buf) {
		double x = buf.getDouble();
		double y = buf.getDouble();
		double w = buf.getDouble();
		double h = buf.getDouble();
		return new Box((x + w / 2), (y + h / 2), w, h);
	}
	
	public synchronized double[] getCenter() {
		try {
			Box box;
			synchronized (this) {
				box = boxes.get(0);
			}
			SmartDashboard.putNumber("raw x target", box.x);
			SmartDashboard.putNumber("raw y target", box.y);
			//I... give up. iIi can'T TAke it anymORE
			SmartDashboard.putString("is great", "definitlee");
			return new double[]{box.x, box.y};
		} catch(Exception e) {
			e.printStackTrace();
			SmartDashboard.putString("is great", "needs to be made so again: "+e.getMessage());
			return new double[]{-1,-1};
		}
	}
	
	private class Box {
		final double x;
		final double y;
		final double area;
		
		public Box(double x, double y, double width, double height){
			this.x = x;
			this.y = y;
			this.area = width * height;
		}
	}
}
