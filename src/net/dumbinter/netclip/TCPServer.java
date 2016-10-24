package net.dumbinter.netclip;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TCPServer implements Runnable {
	private static Logger logger = Logger.getLogger("net.dumbinter.netclip.tcpserver");
	ServerSocket welcomeSocket = null;

	public TCPServer(Integer port) throws IOException {
		welcomeSocket = new ServerSocket(port);
	}

	public void run() {
		while (true) {
			try {
				Socket connectionSocket = welcomeSocket.accept();
				DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
				byte[] data;
				if (NetClipboard.isListenOnly()) {
					data = new String("").getBytes("US-ASCII");
				} else {
					data = Crypto.encrypt(NetClipboard.getData());
				}
				outToClient.write(data);
				outToClient.close();
				connectionSocket.close();
			} catch (Exception e) {
				logger.log(Level.SEVERE, "Unexpected exception", e);
			}
		}
	}
}
