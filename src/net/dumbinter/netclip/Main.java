package net.dumbinter.netclip;

import java.awt.Toolkit;
import java.io.UnsupportedEncodingException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
	private static Handler handler = new ConsoleHandler();
	private static Logger logger = Logger.getLogger("net.dumbinter.netclip");

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			logger.addHandler(handler);
			logger.setUseParentHandlers(false);

			Level logLevel = Level.parse(Config.get().getProperty("netclip.loglevel"));
			handler.setLevel(logLevel);
			logger.setLevel(logLevel);

			Integer aesKeysize = new Integer(Config.get().getProperty("netclip.aeskeysize"));

			Crypto.init(aesKeysize);

			Integer port = new Integer(Config.get().getProperty("netclip.port"));
			String broadcast = Config.get().getProperty("netclip.broadcast");

			String shareClipboard = Config.get().getProperty("netclip.shareclipboard");

			NetClipboard.setListenOnly(!"true".equals(shareClipboard));
			TrayMenu.init("true".equals(shareClipboard));

			logger.config("Broadcasting changes to: " + broadcast);
			logger.config("Listening on port: " + String.valueOf(port));
			logger.config("Using AES key size of (bit): " + String.valueOf(aesKeysize));

			NetClipboard.setClipboard(Toolkit.getDefaultToolkit().getSystemClipboard());

			new Thread(new ClipboardObserver(new UDPServer(broadcast, port))).start();
			new Thread(new UDPReceiver(port)).start();
			new Thread(new TCPServer(port)).start();
		} catch (NullPointerException | IllegalArgumentException | UnsupportedEncodingException |
			         SocketException | UnknownHostException e) {
			logger.log(Level.SEVERE, "Configuration error", e);
			System.exit(1);
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Unexpected exception", e);
			System.exit(1);
		}
	}
}
