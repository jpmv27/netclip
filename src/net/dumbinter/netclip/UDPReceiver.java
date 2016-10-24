package net.dumbinter.netclip;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UDPReceiver implements Runnable {
	private static Logger logger = Logger.getLogger("net.dumbinter.netclip.udpreceiver");
	DatagramSocket socket;
	int port;

	public UDPReceiver(int port) throws SocketException, UnknownHostException {
		this.port = port;
		socket = new DatagramSocket(port, InetAddress.getByName("0.0.0.0"));
		socket.setBroadcast(true);
	}

	public void run() {
		logger.info("Listening for input");
		while (true) {
			byte[] recvBuf = new byte[14];
			DatagramPacket packet = new DatagramPacket(recvBuf, recvBuf.length);
			try {
				socket.receive(packet);
				final InetAddress addr = packet.getAddress();
				logger.info("Received from: " + addr.getHostAddress() + " [" + new String(packet.getData()) + "]");
				// Only process the message if we didn't send it
				if (NetworkInterface.getByInetAddress(addr) == null) {
					if ("NETCLIP_NOTIFY".equals(new String(packet.getData()))) {
						logger.info("Fetching Clipboard Data via TCP");
						NetClipboard.setData(TCPClient.fetchClipboard(packet.getAddress(), port));
						NetClipboard.update();
					}
				}
			} catch (Exception e) {
				logger.log(Level.SEVERE, "Unexpected exception", e);
			}
		}
	}
}
