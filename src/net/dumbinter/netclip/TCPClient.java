package net.dumbinter.netclip;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TCPClient {
	private static Logger logger = Logger.getLogger("net.dumbinter.netclip.tcpclient");

	public static byte[] fetchClipboard(InetAddress adr, int port) {
		try {
			Socket clientSocket = new Socket(adr.getHostAddress(), port);

			InputStream is = clientSocket.getInputStream();

			ByteArrayOutputStream buffer = new ByteArrayOutputStream();

			int nRead;
			byte[] data = new byte[16384];

			while ((nRead = is.read(data, 0, data.length)) != -1) {
				buffer.write(data, 0, nRead);
			}

			buffer.flush();

			is.close();
			clientSocket.close();

			byte[] content = Crypto.decrypt(buffer.toByteArray());
			logger.info("TCP: " + new String(content));
			return content;
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Unexpected exception", e);
		}
		return null;
	}
}
