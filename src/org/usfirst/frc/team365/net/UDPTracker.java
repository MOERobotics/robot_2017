/*package org.usfirst.frc.team365.net;

import java.awt.geom.Rectangle2D;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.Arrays;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class UDPTracker implements MOETracker {
	static final short noneFound = 1, oneFound = 2, twoFound = 3;
	//static final int port = 5801;
	static final double width = 320, height = 240;
	int lastID;
	DatagramSocket socket;
	protected volatile boolean more = true;
	protected String name;
	protected volatile Rectangle2D[] boxes = new Rectangle2D[2];

	public UDPTracker(String name, final int port) throws SocketException {
		this.name = name;
		socket = new DatagramSocket(port);
	}
	
	public static UDPTracker getTracker(String name, final int port){
		try{
			return new UDPTracker(name, port);
		}catch (SocketException e){
			e.printStackTrace();
			return null;
		}
	}

	public void run() {
		ByteBuffer buf = ByteBuffer.allocate(72);
		DatagramPacket packet = new DatagramPacket(buf.array(), buf.limit());
		while (true) {
			try {
				buf.position(0);
				packet.setLength(buf.limit());
				System.out.println("preRecieve");
				socket.receive(packet);
				System.out.print("postRecieve ");
				final int id = buf.getInt();
				if (id <= lastID)
					continue;
				lastID = id;
				System.out.print(id+" ");
				SmartDashboard.putNumber("Tracking ID", id);
				final short status = buf.getShort();
				System.out.println(status);
				buf.getShort();
				Rectangle2D[] data = new Rectangle2D[2];
				// doubles as l,r,w,h
				double x, y, w, h;
				switch (status) {
					case twoFound:
						x = buf.getDouble();
						y = buf.getDouble();
						w = buf.getDouble();
						h = buf.getDouble();
						data[1] = new Rectangle2D.Double((x + w / 2), (y + h / 2), w, h);
					case oneFound:
						x = buf.getDouble();
						y = buf.getDouble();
						w = buf.getDouble();
						h = buf.getDouble();
						data[0] = new Rectangle2D.Double((x + w / 2), (y + h / 2), w, h);
					case noneFound:
						break;
				}
				this.boxes = data;
				for (byte n = 0; n < boxes.length; n++)
					if (boxes[n] != null){
						SmartDashboard.putNumber("x-" + n, boxes[n].getX());
						SmartDashboard.putNumber("y-" + n, boxes[n].getY());
					}
			} catch (Exception e) {
				e.printStackTrace();
				break;
			}
		}
		socket.close();
	}

	public synchronized double[] getCenter() {
		Arrays.sort(boxes);
		return new double[]{boxes[0].getCenterX(), boxes[0].getCenterY()};
	}
	
}*/

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
			SmartDashboard.putString("tracker works", "yes - initialised correctly");
			return new UDPTracker(name, port);
		} catch (SocketException e) {
			e.printStackTrace();
			SmartDashboard.putString("tracker works", "no - bad initialisation: "+e.getMessage());
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
				final int status = buf.getShort() & 0xFFFF;
				System.out.println("Got packet, status=0x" + Integer.toHexString(status));
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
				} else if (id <= lastId) {
					//Stale packet
					continue;
				}
				
				//Update lastID
				lastId = id;
				
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
	
	protected double[] doGetCenter() {
		Box[] boxes = this.boxes;
		if (boxes == null || boxes.length == 0) {
			//We have no boxes
			return null;
		}
		//TODO average them all or something
		Box box = boxes[0];
		return new double[]{box.x, box.y};
	}
	
	@Override
	public double[] getCenter() {
		try {
			double[] center = doGetCenter();
			if (center == null){
				SmartDashboard.putString("tracker works", "no - data is null " );
				return new double[]{-1, -1};
			} else {
				SmartDashboard.putNumber("raw x target", center[0]);
				SmartDashboard.putNumber("raw y target", center[1]);
				//I... give up. iIi can'T TAke it anymORE
				SmartDashboard.putString("tracker works", "yes - target recieved, no problem");
				return new double[]{center[0], center[1]};
			}
		} catch(Exception e) {
			e.printStackTrace();
			SmartDashboard.putString("tracker works", "no - this should NEVER happen, but this is the errmsg: " + e.getMessage());
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