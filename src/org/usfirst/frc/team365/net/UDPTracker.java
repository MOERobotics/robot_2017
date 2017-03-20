package org.usfirst.frc.team365.net;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.ByteBuffer;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * Tracker that recieves coordinates via UDP packets
 */
public class UDPTracker implements MOETracker {
	//packet status codes
	static final short NONE_FOUND = 1;
	static final short ONE_FOUND  = 2;
	static final short TWO_FOUND  = 3;
	static final short ERROR = (short) 0x8000;
	static final short HELLO = (short) 0x8001;
	static final short GOODBYE=(short) 0x8002;
	
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
	
	/**
	 * Array of boxes from the most recent packet
	 */
	protected volatile Box[] boxes;

	public UDPTracker(String name, final int port) throws SocketException {
		this.name = name;
		this.socket = new DatagramSocket(port);
	}
	
	@Override
	public void run() {
		ByteBuffer buf = ByteBuffer.allocate(256);
		DatagramPacket packet = new DatagramPacket(buf.array(), buf.capacity());
		int lastId = Integer.MIN_VALUE;
		
		try {
			while (!Thread.interrupted()) {
				buf.clear();
				packet.setLength(buf.capacity());
				socket.receive(packet);
				
				final int id = buf.getInt();
				final short status = buf.getShort();
				if (status == 0) {
					//Ignore
					continue;
				} else if (status >= 0x8000) {
					//Metadata packet (never stale)
					switch (status) {
						case HELLO:
							//Reset id
							lastId = id;
							continue;
						case ERROR:
						case GOODBYE:
						default:
							//TODO handle
							continue;
					}
				} else if (id <= lastID) {
					//Stale packet
					continue;
				}
				
				//Update lastID
				lastID = id;
				
				//Read boxes
				int numBoxes = status - 1;
				Box[] boxes = new Box[numBoxes];
				for (int i = 0; i < numBoxes; i++)
					boxes[i] = readBox(buf);
				
				//Update value
				this.boxes = boxes;
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			socket.close();
		}
	}
	
	private Box readBox(ByteBuffer buf) {
		double x = buf.getDouble();
		double y = buf.getDouble();
		double w = buf.getDouble();
		double h = buf.getDouble();
		return new Box((x + w / 2), (y + h / 2), w, h);
	}
	
	protected Point2D doGetCenter() {
		Box[] boxes = this.boxes;
		if (boxes == null || boxes.length == 0) {
			//We have no boxes
			return null;
		}
		//TODO average them all or something
		Box box = boxes[0];
		return new Point2D.Double(box.x, box.y);
	}
	
	@Override
	public double[] getCenter() {
		try {
			Point2D center = doGetCenter();
			SmartDashboard.putNumber("raw x target", center.getX());
			SmartDashboard.putNumber("raw y target", center.getY());
			//I... give up. iIi can'T TAke it anymORE
			SmartDashboard.putString("is great", "definitlee");
			return new double[]{center.getX(), center.getY()};
		} catch(Exception e) {
			e.printStackTrace();
			SmartDashboard.putString("is great", "needs to be made so again: " + e.getMessage());
			return new double[]{-1, -1};
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
