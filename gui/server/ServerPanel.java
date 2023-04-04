package gui.server;

import bus.common.CommonBus;
import gui.MainFrame;
import gui.client.ClientPanel;
import gui.common.CommonLabel;
import gui.common.CommonPanel;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Vector;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

public class ServerPanel extends JPanel implements Runnable {
    public final static String STOPPED_FOREGROUND = "0xF50016";
    public final static String LISTENING_FOREGROUND = "0x0042A7";

    private CommonPanel mainPanel;
    private JLabel statusLabel;
    private CommonLabel listenLabel;
    private CommonLabel stopLabel;

    private CommonBus commonBus;

    private Thread listenThread;

    public ServerPanel(CommonBus commonBus) {
        // TODO: style ClientPanel
        this.setLocation(0, MainFrame.HEIGHT_TASKBAR);
        this.setSize(MainFrame.WIDTH_FRAME, MainFrame.HEIGHT_FRAME - MainFrame.HEIGHT_TASKBAR);
        this.setBackground(Color.decode(ClientPanel.BACKGROUND));
        this.setLayout(null);

        // TODO: class for handle events
        this.commonBus = commonBus;

        // TODO: add components
        this.initComponents();
    }

    private void initComponents() {
        this.mainPanel = new CommonPanel();
        this.statusLabel = new JLabel();
        this.listenLabel = new CommonLabel();
        this.stopLabel = new CommonLabel();

        this.add(this.mainPanel);

        this.mainPanel.getHostCombo().addPopupMenuListener(new PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                    mainPanel.getHostCombo().removeAllItems();
                    InetAddress inetAddress;
					try {
						inetAddress = InetAddress.getLocalHost();
						mainPanel.getHostCombo().addItem(inetAddress.getHostAddress());
						mainPanel.getHostCombo().addItem("127.0.0.1");
					} catch (UnknownHostException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {}
            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {}
        });

        // TODO: style status_label
        this.statusLabel.setText("Status: Stopped");
        this.statusLabel.setBounds(30, 200, 150, 20);
        this.statusLabel.setFont(new Font("segoe ui", Font.ITALIC | Font.BOLD, 13));
        this.statusLabel.setForeground(Color.decode(ServerPanel.STOPPED_FOREGROUND));
        this.mainPanel.add(this.statusLabel);

        // TODO: style listen_label
        this.listenLabel.setText("Start listening");
        this.listenLabel.setBounds(50, 290, 150, 50);
        this.listenLabel.setForeground(Color.decode(ClientPanel.FOREGROUND));
        this.listenLabel.setFont(new Font("segoe ui", Font.PLAIN, 15));
        this.listenLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                listenLabelMousePressed(e);
            }
        });
        this.add(this.listenLabel);

        // TODO: style stop_label
        this.stopLabel.setText("Stop listening");
        this.stopLabel.setBounds(220, 290, 150, 50);
        this.stopLabel.setForeground(Color.decode(ClientPanel.FOREGROUND));
        this.stopLabel.setFont(new Font("segoe ui", Font.PLAIN, 15));
        this.stopLabel.setEnabled(false);
        this.stopLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                stopLabelMousePressed(e);
            }
        });
        this.add(this.stopLabel);
    }

    // TODO: handle events of listen_label
    private void listenLabelMousePressed(MouseEvent e) {
        if(e.getButton() == MouseEvent.BUTTON1 && this.listenLabel.isEnabled()) {
            try {
                String host = this.mainPanel.getHostCombo().getSelectedItem().toString().trim();
                int port = Integer.parseInt(this.mainPanel.getPortText().getText().trim());
                String password = this.mainPanel.getPassText().getText().trim();
                this.commonBus.startListeningOnServer(host, port, password);

                // TODO: start listen_thread
                this.listenThread = new Thread(this);
                this.listenThread.setDaemon(true);
                this.listenThread.start();

                // TODO: set status
                this.mainPanel.setEnabled(false);
                this.listenLabel.resetFont();
                this.listenLabel.setEnabled(false);
                this.stopLabel.setEnabled(true);
                this.statusLabel.setText("Status: Listening...");
                this.statusLabel.setForeground(Color.decode(ServerPanel.LISTENING_FOREGROUND));
            }
            catch(Exception exception) {
                JOptionPane.showMessageDialog(this, "Can't start listening on server:\n" + exception.getMessage());
            }
        }
    }

    // TODO: handle events of stop_label
    private void stopLabelMousePressed(MouseEvent e) {
        if(e.getButton() == MouseEvent.BUTTON1 && this.stopLabel.isEnabled()) {
            try {
                this.commonBus.stopListeningOnServer();

                // TODO: stop listen_thread
                this.listenThread.interrupt();

                // TODO: set status
                this.mainPanel.setEnabled(true);
                this.stopLabel.resetFont();
                this.stopLabel.setEnabled(false);
                this.listenLabel.setEnabled(true);
                this.statusLabel.setText("Status: Stopped");
                this.statusLabel.setForeground(Color.decode(ServerPanel.STOPPED_FOREGROUND));
            }
            catch(Exception exception) {
                System.out.println(exception.getMessage());
                JOptionPane.showMessageDialog(this, "Can't stop listening on server:\n" + exception.getMessage());
            }
        }
    }

    @Override
    public void run() {
        while(this.commonBus.getTcpServer().isListening()) {
            try {
                this.commonBus.getTcpServer().waitingConnectionFromClient();
            }
            catch(Exception e) {}
        }
    }
}

