package gui.chat;

import bus.chat.ChatBus;
import bus.common.CommonBus;
import gui.MainFrame;
import gui.client.ClientPanel;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

public class MainChatPanel extends JPanel {
	private JLabel connectionsLabel;
	private JMenuBar menuBar;
	private JPopupMenu popupMenu;

	private ArrayList<ChatPanel> chatPanels;

	private CommonBus commonBus;

	private int count;

	public MainChatPanel(CommonBus commonBus) {
		// TODO: style MainChatPanel
		this.setLocation(0, MainFrame.HEIGHT_TASKBAR);
		this.setSize(MainFrame.WIDTH_FRAME, MainFrame.HEIGHT_FRAME - MainFrame.HEIGHT_TASKBAR);
		this.setBackground(Color.decode(ClientPanel.BACKGROUND));
		this.setLayout(null);

		// TODO: class for handle events
		this.commonBus = commonBus;

		// TODO: add all components
		this.initComponents();
	}

	private void initComponents() {
		// TODO: constructors
		this.menuBar = new JMenuBar();
		this.connectionsLabel = new JLabel();
		this.popupMenu = new JPopupMenu();
		this.chatPanels = new ArrayList<>();

		// TODO: set value for count
		this.count = 0;

		// TODO: style connections_label
		this.connectionsLabel.setText("<html>All connections <font color='red'>(" + this.count + ")</font></html>");
		this.connectionsLabel.setBounds(0, 0, 100, 15);
		this.connectionsLabel.setFont(new Font("segoe ui", Font.PLAIN, 13));
		this.connectionsLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                popupMenuMousePressed(e);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                popupMenuMouseEntered(e);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                popupMenuMouseExited(e);
            }
        });

		// TODO: style menu_bar
		this.menuBar.add(this.connectionsLabel);
		this.menuBar.setBounds(0, 0, MainFrame.WIDTH_FRAME, 20);
		this.menuBar.setLayout(new GridBagLayout());
		this.add(this.menuBar);
	}
	

	public void addCount(int n) {
		this.count += n;
		this.connectionsLabel.setText("<html>All connections <font color='red'>(" + this.count + ")</font></html>");
	}

	public ArrayList<ChatPanel> getChatPanels() {
		return this.chatPanels;
	}
	 private void popupMenuMousePressed(MouseEvent e) {
	        if(e.getButton() == MouseEvent.BUTTON1) {
	            this.popupMenu.show(this.connectionsLabel, 130, 5);
	        }
	    }
	 private void popupMenuMouseEntered(MouseEvent e) {
	        this.connectionsLabel.setFont(new Font("segoe ui", Font.BOLD, 13));
	        this.setCursor(new Cursor(Cursor.HAND_CURSOR));
	    }
	 private void popupMenuMouseExited(MouseEvent e) {
	        this.connectionsLabel.setFont(new Font("segoe ui", Font.PLAIN, 13));
	        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
	    }
	 public JPopupMenu getPopupMenu() {
	        return this.popupMenu;
	    }
	public void addNewConnection(ChatBus chatBus) {
		ChatPanel chatPanel = new ChatPanel(this, commonBus, chatBus);
		ConnectionItem item = new ConnectionItem(chatPanel, this.chatPanels);
		chatPanel.setConnectionItem(item);
		this.add(chatPanel);
		this.addCount(1);
		this.chatPanels.add(chatPanel);
		this.popupMenu.add(item);
		this.validate();
		this.revalidate();
		this.repaint();
	}
}