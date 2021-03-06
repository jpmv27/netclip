package net.dumbinter.netclip;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.lang.IllegalStateException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClipboardObserver implements Runnable {
	private static Logger logger = Logger.getLogger("net.dumbinter.netclip.clipboardobserver");
	UDPServer broadcaster = null;

	public ClipboardObserver(UDPServer broadcaster) {
		this.broadcaster = broadcaster;
	}

	public void run() {
		logger.info("Observer running");
		while (true) {
			try {
				if (!NetClipboard.isListenOnly()) {
					final Transferable clipboardRaw = NetClipboard.getClipboard().getContents(null);
					String clipboardData = null;

					if ((clipboardRaw == null) || (clipboardRaw.getTransferDataFlavors().length == 0)) {
						clipboardData = "";
					} else if (clipboardRaw.isDataFlavorSupported(DataFlavor.stringFlavor)) {
						clipboardData = (String) clipboardRaw.getTransferData(DataFlavor.stringFlavor);
					}

					if (clipboardData != null) {
						if (NetClipboard.getLastData().hashCode() != clipboardData.hashCode()) {
							NetClipboard.setLastData(clipboardData);
							NetClipboard.setData(clipboardData.getBytes("UTF-8"));
							logger.info("Local change detected: \"" + clipboardData + "\"");
							broadcaster.broadcast();
						}
					}
				}
			} catch (UnsupportedFlavorException | IOException e) {
				logger.log(Level.WARNING, "Couldn't read clipboard data", e);
			} catch (IllegalStateException e) {
				// The clipboard is busy at the moment
				// Ignore it, we'll try again next time
			} catch (Exception e) {
				logger.log(Level.SEVERE, "Unexpected exception", e);
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// who cares?
			}
		}
	}
}
