package net.dumbinter.netclip;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UDPServer {
	private static Logger logger = Logger.getLogger("net.dumbinter.netclip.udpserver");
	private InetAddress host;
	private DatagramSocket socket;
	int port;

	public UDPServer(String broadcast, int port) throws SocketException, UnknownHostException {
		this.port = port;
		socket = new DatagramSocket(null);
		socket.setBroadcast(true);
		host = InetAddress.getByName(broadcast);
	}

	public void broadcast() {
		try {
			DatagramPacket packet;
			byte[] buf = "NETCLIP_NOTIFY".getBytes("US-ASCII");
			packet = new DatagramPacket(buf, buf.length, host, port);
			socket.send(packet);
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Unexpected exception", e);
		}
	}
}
