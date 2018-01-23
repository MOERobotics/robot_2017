package org.usfirst.frc.team365.net;

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
	
}