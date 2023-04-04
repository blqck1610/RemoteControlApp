package gui.monitoring;

import bus.common.CommonBus;
import bus.rmi.IRemoteDesktop;
import gui.client.ClientPanel;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.rmi.RemoteException;
import javax.imageio.ImageIO;
import javax.swing.*;

public class MonitoringFrame extends JFrame implements Runnable {
	private ClientPanel clientPanel;
	private CommonBus commonBus;
	private IRemoteDesktop remoteObj;

	private JLabel screenLabel;
	private JMenuBar menuBar;
	private JMenu menu_monitor;
	private HardwareDialog hardwareDialog;

	private Dimension screenSize;
	private Insets frameInsets;
	private Insets taskbarInsets;
	private String quality;

	// TODO: properties for remote larger screen
	private float dx;
	private float dy;

	private volatile Thread screenThread;

	public MonitoringFrame(ClientPanel client_panel, CommonBus commonBus, String quality) throws Exception {
		this.setTitle("You are monitoring " + commonBus.getTcpClient().getClient().getLocalAddress().getHostName());
		this.getContentPane().setBackground(Color.BLACK);
		this.setExtendedState(JFrame.MAXIMIZED_BOTH);
		this.setLocationRelativeTo(null);
		this.setLayout(null);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				try {
					remoteFrameWindowClosing(e);
				} catch (Exception exception) {
					dispose();
					JOptionPane.showMessageDialog(null, "Can't close connection");
				}
			}

			@Override
			public void windowOpened(WindowEvent e) {
				remoteFrameWindowOpened(e);
			}
		});
		this.setVisible(true);
		this.quality = quality;
		this.clientPanel = client_panel;
		this.commonBus = commonBus;
		this.remoteObj = this.commonBus.getRmiClient().getRemoteObject();
		this.screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		this.frameInsets = this.getInsets();
		this.taskbarInsets = Toolkit.getDefaultToolkit().getScreenInsets(this.getGraphicsConfiguration());

		// TODO: add components
		this.initComponents();
		// TODO: setup window
		this.setupWindow();
		// TODO: start thread to share partner's screen
		this.screenThread = new Thread(this);
		this.screenThread.setDaemon(true);
		this.screenThread.start();
	}

	private void initComponents() throws RemoteException {
		// TODO: constructor
		this.screenLabel = new JLabel();
		this.menuBar = new JMenuBar();
		this.menu_monitor = new JMenu();
		this.hardwareDialog = new HardwareDialog(this, this.remoteObj);
		// TODO: set minimum size of remote frame
		this.setMinimumSize(this.hardwareDialog.getPreferredSize());
		
		this.add(this.screenLabel);
		// TODO: style menu_bar						
		this.menuBar.setLayout(new GridBagLayout());
		this.menuBar.setBackground(Color.decode("0x9A9A9A"));
		this.menuBar.setPreferredSize(new Dimension(0, 25));
		this.setJMenuBar(this.menuBar);

		// TODO: style menu
		this.menu_monitor.setText("Show monitor");
		this.menuBar.setFont(new Font("segoe ui", Font.PLAIN, 14));
		this.menu_monitor.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				menuMonitorMousePressed(e);
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				menuMonitorMouseEntered(e);
			}

			@Override
			public void mouseExited(MouseEvent e) {
				menuMonitorMouseExited(e);
			}
		});
		this.menuBar.add(menu_monitor);
	}

	private void setupWindow() throws Exception {
		ImageIO.setUseCache(false);
		byte[] dgram = this.remoteObj.takeScreenshotServer(quality);
		ByteArrayInputStream bis = new ByteArrayInputStream(dgram);
		BufferedImage screenshot = ImageIO.read(bis);

		this.screenSize.width -= (this.taskbarInsets.left + this.taskbarInsets.right);
		this.screenSize.height -= (this.taskbarInsets.top + this.taskbarInsets.bottom + this.frameInsets.top
				+ this.menuBar.getPreferredSize().height);

		// TODO: your screen lager than partner's screen
		if (this.screenSize.width > screenshot.getWidth() && this.screenSize.height > screenshot.getHeight()) {
			int h_gap = (this.screenSize.width - screenshot.getWidth()) / 2;
			int v_gap = (this.screenSize.height - screenshot.getHeight()) / 2;

			this.dx = 1;
			this.dy = 1;
			this.screenLabel.setBounds(h_gap, v_gap, screenshot.getWidth(), screenshot.getHeight());
		}
		// TODO: your screen smaller than partner's screen
		else {
			float ratio = (float) screenshot.getWidth() / screenshot.getHeight();
			int tmp_width = this.screenSize.width;
			this.screenSize.width = (int) (ratio * this.screenSize.height);

			int h_gap = (tmp_width - this.screenSize.width) / 2;

			this.dx = (float) screenshot.getWidth() / this.screenSize.width;
			this.dy = (float) screenshot.getHeight() / this.screenSize.height;
			this.screenLabel.setBounds(h_gap, 0, this.screenSize.width, this.screenSize.height);
		}
	}

	@Override
	public void run() {
		int width = this.screenLabel.getWidth();
		int height = this.screenLabel.getHeight();
		try {
			while (this.commonBus.getTcpClient().isConnectedServer()) {
				byte[] dgram = this.remoteObj.takeScreenshotServer(quality);
				ByteArrayInputStream bis = new ByteArrayInputStream(dgram);
				Image screenshot = ImageIO.read(bis).getScaledInstance(width, height, Image.SCALE_SMOOTH);
				this.screenLabel.setIcon(new ImageIcon(screenshot));
			}
			this.dispose();
		} catch (Exception e) {
			this.dispose();
		}
	}

	@Override
	public void dispose() {
		try {
			super.setVisible(false);
			super.dispose();
			this.clientPanel.setEnabled(true);
//			this.commonBus.getRmiClient().setRemoteServer(false);
			this.commonBus.getTcpClient().setConnectedServer(false);
			
			this.commonBus.getTcpClient().getClient().close();
			if (!this.screenThread.isInterrupted())
				this.screenThread.isInterrupted();
		} catch (Exception exception) {
			JOptionPane.showMessageDialog(null, "Can't close connection");
		}
	}

	private void remoteFrameWindowClosing(WindowEvent e) {
		this.dispose();
	}

	private void remoteFrameWindowOpened(WindowEvent e) {
		this.clientPanel.setEnabled(false);
	}


	// TODO: get hardware info of server
	private void menuMonitorMousePressed(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON1) {
			this.hardwareDialog.setVisible(true);
		}
	}

	private void menuMonitorMouseEntered(MouseEvent e) {
		this.menu_monitor.setFont(new Font("segoe ui", Font.BOLD, 16));
		this.setCursor(new Cursor(Cursor.HAND_CURSOR));
	}

	private void menuMonitorMouseExited(MouseEvent e) {
		this.menu_monitor.setFont(new Font("segoe ui", Font.PLAIN, 14));
		this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
	}
}