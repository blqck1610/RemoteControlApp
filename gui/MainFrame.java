package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.util.concurrent.atomic.AtomicInteger;
import javax.imageio.ImageIO;
import javax.swing.*;

import bus.common.CommonBus;
import gui.chat.MainChatPanel;
import gui.client.ClientPanel;
import gui.common.CommonLabel;
import gui.server.ServerPanel;

public class MainFrame extends JFrame {
    public final static int WIDTH_FRAME = 400;
    public final static int HEIGHT_FRAME = 420;
    public final static int HEIGHT_TASKBAR = 50;

    public final static String TASKBAR_BACKGROUND = "0xFFFFFF";
    // TODO: class for handle events
    private CommonBus commonBus;

    private JPanel taskbarPanel;
    private CommonLabel clientLabel;
    private CommonLabel serverLabel;
    private CommonLabel chatLabel;
    private ClientPanel clientPanel;
    private ServerPanel serverPanel;
    private MainChatPanel mainChatPanel;
    private int focusKey;

    public MainFrame() throws IOException {
        // TODO: not using disk cache
        ImageIO.setUseCache(false);

        // TODO: set UI
        UIManager.put("Label.disabledForeground", Color.decode("0xD3D3D3"));
        UIManager.put("RadioButton.disabledText", Color.decode("0xD3D3D3"));

        // TODO: style main frame
        this.getContentPane().setPreferredSize(new Dimension(MainFrame.WIDTH_FRAME, MainFrame.HEIGHT_FRAME));
        this.pack();
        this.setLayout(null);
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        this.setTitle("remote screen monitoring app");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    mainFrameWindowClosing(e);
                }
                catch(Exception exception) {}
            }
        });

        // TODO: add components
        this.initComponents();
    }

    private void initComponents() {
        // TODO: constructor
        this.commonBus = new CommonBus();
        this.taskbarPanel = new JPanel();
        this.clientLabel = new CommonLabel();
        this.serverLabel = new CommonLabel();
        this.chatLabel = new CommonLabel();
        this.clientPanel = new ClientPanel(this.commonBus);
        this.serverPanel = new ServerPanel(this.commonBus);
        this.mainChatPanel = new MainChatPanel(this.commonBus);

        this.commonBus.setMainChatPanel(this.mainChatPanel);

        // TODO: set focus_key = 1 for client_panel
        this.focusKey = 1;

        // TODO: layout of taskbar_panel
        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[] {100, 100, 100};

        // TODO: style taskbar_panel
        this.taskbarPanel.setLayout(gridBagLayout);
        this.taskbarPanel.setBackground(Color.decode(MainFrame.TASKBAR_BACKGROUND));
        this.taskbarPanel.setBounds(0, 0, MainFrame.WIDTH_FRAME, MainFrame.HEIGHT_TASKBAR);
        this.add(this.taskbarPanel);

        // TODO: style client_label
        this.clientLabel.setText("Client");
        this.clientLabel.setBigFont();
        this.clientLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                tabLabelMouseClicked(e, clientLabel, 1);
            }
        });
        this.taskbarPanel.add(this.clientLabel);

        // TODO: style server_label
        this.serverLabel.setText("Server");
        this.serverLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                tabLabelMouseClicked(e, serverLabel, 2);
            }
        });
        this.taskbarPanel.add(this.serverLabel);

        // TODO: style chat_label
        this.chatLabel.setText("Chat");
        this.chatLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                tabLabelMouseClicked(e, chatLabel, 3);
            }
        });
        this.taskbarPanel.add(this.chatLabel);

        // TODO: default
        this.clientPanel.setVisible(true);
        this.serverPanel.setVisible(false);
        this.mainChatPanel.setVisible(false);
        this.add(this.clientPanel);
        this.add(this.serverPanel);
        this.add(this.mainChatPanel);
    }

    private void mainFrameWindowClosing(WindowEvent e) throws IOException, NotBoundException {
        this.commonBus.stopListeningOnServer();
    }

    private void tabLabelMouseClicked(MouseEvent e, CommonLabel commonLabel, int key) {
        if(e.getButton() == MouseEvent.BUTTON1) {
            if(key == focusKey) return;
            JPanel showPanel = (key == 1) ? this.clientPanel : (key == 2) ? this.serverPanel : this.mainChatPanel;
            JPanel hidePanel = (focusKey == 1) ? this.clientPanel : (focusKey == 2) ? this.serverPanel : this.mainChatPanel;
            if(key > focusKey) {
                this.showPanelsSlider(showPanel, hidePanel, true);
            }
            else {
                this.showPanelsSlider(showPanel, hidePanel, false);
            }

            // TODO: update status
            this.focusKey = key;
            this.clientLabel.setSmallFont();
            this.serverLabel.setSmallFont();
            this.chatLabel.setSmallFont();
            commonLabel.setBigFont();
        }
    }

    private void showPanelsSlider(JPanel showPanel, JPanel hidePanel, boolean isLeft) {
        showPanel.setVisible(true);

        // TODO: atomic integer for lambda expression
        AtomicInteger xHideLocation = new AtomicInteger(0);
        AtomicInteger xShowLocation = new AtomicInteger(0);
        AtomicInteger value = new AtomicInteger(0);

        if(isLeft) {
            xShowLocation.set(MainFrame.WIDTH_FRAME);
            value.set(-50);
        }
        else {
            xShowLocation.set(-MainFrame.WIDTH_FRAME);
            value.set(50);
        }

        Timer timer = new Timer(10, (e) -> {
            hidePanel.setLocation(xHideLocation.get(), MainFrame.HEIGHT_TASKBAR);
            showPanel.setLocation(xShowLocation.get(), MainFrame.HEIGHT_TASKBAR);
            if(xShowLocation.get() == 0) {
                ((Timer)e.getSource()).stop();
                hidePanel.setVisible(false);
            }
            xShowLocation.addAndGet(value.get());
            xHideLocation.addAndGet(value.get());
        });
        timer.start();
    }
}
