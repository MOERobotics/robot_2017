package org.usfirst.frc.team365.net;

import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Class to receive packets from the Pi.
 *
 */
public class AutoTracker implements Runnable {
	/**
	 * Denotes a packet that should be ignored. No idea why we would need to use
	 * this, though.
	 */
	public static final short STATUS_NOP = 0;
	/**
	 * Denotes a packet telling the Rio that no target(s) were found.
	 */
	public static final short STATUS_NONE_FOUND = 1;
	/**
	 * Denotes a packet telling the Rio that one target has been detected. The
	 * position data MUST be included in the packet.
	 */
	public static final short STATUS_ONE_FOUND = 2;
	/**
	 * Denotes a packet telling the Rio that two or more targets have been
	 * found. The position data of the two largest targets found (by area) MUST
	 * be included in the packet.
	 */
	public static final short STATUS_TWO_FOUND = 3;
	/**
	 * A packet that contains the 
	 */
	public static final short STATUS_ERROR = 4;
	/**
	 * A packet that notifies the receiver that the sender has just connected.
	 * If this packet is received, the receiver should reset its last-received packet id
	 * to the id of this packet.
	 */
	public static final short STATUS_HELLO = 5;
	/**
	 * Signals that the sender is terminating in an expected manner.
	 */
	public static final short STATUS_GOODBYE = 6;
	
	protected final DatagramSocket socket;
	
	protected final AtomicInteger lastPacketId = new AtomicInteger(Integer.MIN_VALUE);
	
	protected volatile Rectangle2D[] rectangles = new Rectangle2D[0];
	
	public AutoTracker() {
		this.socket = null;
	}
	
	public List<Rectangle2D> latest() {
		synchronized (this) {
			return Arrays.asList(rectangles);
		}
	}
	
	public List<Rectangle2D> latest(int timeout) throws InterruptedException {
		synchronized (this) {
			this.wait(timeout);
			return Arrays.asList(rectangles);
		}
	}
	
	protected void update(Rectangle2D...rectangles) {
		synchronized (this) {
			this.rectangles = rectangles;
		}
	}
	
	protected Rectangle2D readNextRectangle(ByteBuffer data) {
		double x = data.getDouble();
		double y = data.getDouble();
		double w = data.getDouble();
		double h = data.getDouble();
		return new Rectangle2D.Double(x, y, w, h);
	}
	
	protected void handlePacket(ByteBuffer data) {
		int packetId = data.getInt();
		int status = data.getShort() & 0xFFFF;//uint16 fields
		@SuppressWarnings("unused")
		int flag = data.getShort() & 0xFFFF;
		
		//If we have a HELLO packet, we have to reset the last packet id
		if (status == STATUS_HELLO) {
			lastPacketId.set(packetId);
			return;
		}
		
		if (lastPacketId.accumulateAndGet(packetId, Integer::max) > packetId)
			//We received this packet out of order, so drop it
			return;
		
		switch (status) {
			case STATUS_NOP:
			case STATUS_GOODBYE:
				break;
			case STATUS_NONE_FOUND:
				update();
				break;
			case STATUS_ONE_FOUND:
				update(readNextRectangle(data));
				break;
			case STATUS_TWO_FOUND:
				update(readNextRectangle(data), readNextRectangle(data));
				break;
			default:
				System.err.println("Unknown packet type: " + status);
				break;
		}
	}
	
	@Override
	public void run() {
		ByteBuffer buffer = ByteBuffer.allocate(72);
		DatagramPacket packet = new DatagramPacket(buffer.array(), buffer.position(), buffer.limit());
		
		while (!Thread.interrupted()) {
			buffer.reset();
			
			try {
				//Block until receiving a packet
				socket.receive(packet);
			} catch (IOException e) {
				e.printStackTrace();
				//Is this recoverable from?
				break;
			}
			
			try {
				this.handlePacket(buffer);
			} catch (BufferUnderflowException e) {
				e.printStackTrace();
			}
		}
	}
}
