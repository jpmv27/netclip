package net.dumbinter.netclip;

import java.awt.AWTException;
import java.awt.CheckboxMenuItem;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.UIManager;

public class TrayMenu implements ItemListener, ActionListener {
	private static Logger logger = Logger.getLogger("net.dumbinter.netclip.traymenu");

	public static void init(boolean shareClip) {
		if (!SystemTray.isSupported()) {
			logger.warning("System tray not supported. Not displaying quicksettings icon");
			return;
		}

		try {
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
		} catch (Exception e) {
			logger.log(Level.WARNING, "Couldn't set system look & feel", e);
		}

		PopupMenu popMenu = new PopupMenu();

		CheckboxMenuItem mnuRcvonly = new CheckboxMenuItem("Share Clipboard");
		MenuItem mnuClose = new MenuItem("Close");

		TrayMenu listeners = new TrayMenu();

		mnuRcvonly.addItemListener(listeners);
		mnuClose.addActionListener(listeners);

		mnuRcvonly.setState(shareClip);

		popMenu.add(mnuRcvonly);
		popMenu.addSeparator();
		popMenu.add(mnuClose);

		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		InputStream stream = loader.getResourceAsStream("netclip.png");

		Image trayimg;

		try {
			trayimg = ImageIO.read(stream);
			TrayIcon trayIcon = new TrayIcon(trayimg, "netclip", popMenu);
			trayIcon.setImageAutoSize(true);
			SystemTray.getSystemTray().add(trayIcon);
		} catch (AWTException e) {
			logger.log(Level.WARNING, "Couldn't add tray image", e);
		} catch (IOException e) {
			logger.log(Level.WARNING, "Couldn't read tray image", e);
		}
	}

	public void itemStateChanged(ItemEvent e) {
		if (e.getStateChange() == ItemEvent.SELECTED) {
			NetClipboard.setListenOnly(false);
		} else {
			NetClipboard.setListenOnly(true);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		System.exit(0);
	}
}
