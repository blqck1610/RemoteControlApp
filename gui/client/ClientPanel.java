package gui.client;

import bus.common.CommonBus;
import gui.MainFrame;
import gui.common.CommonLabel;
import gui.common.CommonPanel;
import gui.monitoring.MonitoringFrame;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import javax.swing.border.TitledBorder;

public class ClientPanel extends JPanel {
//    public final static String BACKGROUND = "0x00A571";
    public final static String BACKGROUND = "0xFFFFFF";
	
    public final static String FOREGROUND = "0x003927";

    private CommonPanel mainPanel;
    private CommonLabel connectLabel;
    private ButtonGroup buttonGroup;
    
    private JLabel loaderLabel;

    private CommonBus commonBus;

    public ClientPanel(CommonBus commonBus) {
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
        // TODO: constructor
        this.mainPanel = new CommonPanel();
        this.connectLabel = new CommonLabel();
        this.buttonGroup = new ButtonGroup();
        this.loaderLabel = new JLabel();

        // TODO: style main panel
        this.mainPanel.setBorder(BorderFactory.createTitledBorder(null, "Connect To Server",
            TitledBorder.DEFAULT_JUSTIFICATION,
            TitledBorder.DEFAULT_POSITION,
            new Font("segoe ui", Font.BOLD, 16),
            Color.decode(ClientPanel.FOREGROUND))
        );
        this.add(this.mainPanel);

        // TODO: style host_label
        this.mainPanel.getHostLabel().setText("IP:");

        // TODO: style host_text
        this.mainPanel.getHostCombo().setVisible(false);
        this.mainPanel.getHostText().setVisible(true);

        // TODO: style port_label
        this.mainPanel.getPortLabel().setText("port:");

        // TODO: style pass_label
        this.mainPanel.getPassLabel().setText("Password:");

        // TODO: style pass_field
        this.mainPanel.getPassText().setVisible(false);
        this.mainPanel.getPassField().setVisible(true);


        // TODO: style connect_label
        this.connectLabel.setText("Connect");
        this.connectLabel.setBounds(220, 290, 150, 50);
        this.connectLabel.setForeground(Color.decode(ClientPanel.FOREGROUND));
        this.connectLabel.setFont(new Font("segoe ui", Font.PLAIN, 15));
        this.connectLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                connectLabelMousePressed(e);
            }
        });
        this.add(this.connectLabel);

        // TODO: style low_radio
       

        

        // TODO: style loader_label
        this.loaderLabel.setBounds(340, 307, 16, 16);
        this.loaderLabel.setVisible(false);
        this.add(this.loaderLabel);
    }

    @Override
    public void setEnabled(boolean b) {
        this.mainPanel.setEnabled(b);
        this.connectLabel.setEnabled(b);
    }

    private boolean isFormatIpv4(String host) {
        int count = 0;
        for(int i = 0; i < host.length(); ++i) {
            if(host.charAt(i) == '.') ++count;
        }
        // TODO: count = 3 for ipv4
        // TODO: count = 0 for host name
        return count == 3 || count == 0;
    }

    // TODO: handle events of connect_label
    private void connectLabelMousePressed(MouseEvent e) {
        if(e.getButton() == MouseEvent.BUTTON1 && this.connectLabel.isEnabled()) {
            this.setEnabled(false);
            this.loaderLabel.setVisible(true);

            Thread connectThread = new Thread(() -> {
                try {
                    String host = this.mainPanel.getHostText().getText().trim();
                    int port = Integer.parseInt(this.mainPanel.getPortText().getText().trim());
                    String password = this.mainPanel.getPassField().getText().trim();
                    // TODO: check format ipv4
                    if(!this.isFormatIpv4(host)) throw new Exception("Wrong format IPV4");

                    // TODO: start connect
                    this.commonBus.startConnectingToServer(host, port, password);
                    // TODO: show remote screen
                    EventQueue.invokeLater(() -> {
                        try {
                                new MonitoringFrame(this, this.commonBus, "png");
                            
                        }
                        catch(Exception exception) {
                            JOptionPane.showMessageDialog(this, "Can't start monitoring to server:\n" + exception.getMessage());
                        }
                    });
                }
                catch(Exception exception) {
                    JOptionPane.showMessageDialog(this, "Can't connect to server:\n" + exception.getMessage());
                }
                this.setEnabled(true);
                this.loaderLabel.setVisible(false);
            });
            connectThread.setDaemon(true);
            connectThread.start();
        }
    }
}

