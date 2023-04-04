package gui.chat;

import bus.chat.ChatBus;
import bus.chat.Message;
import bus.chat.StringMessage;
import bus.common.CommonBus;
import gui.MainFrame;
import gui.client.ClientPanel;
import gui.common.CommonLabel;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import javax.swing.*;
import javax.swing.filechooser.FileSystemView;

public class ChatPanel extends JPanel implements Runnable {
    public final static String CONTENT_BACKGROUND = "0xFFFFFF";
	
    public final static int MAX_LENGTH_LINE = 20;

    private JPanel contentPanel;
    private JScrollPane scrollPanel;
    private JTextArea messageText;
    private JScrollPane scrollText;
    private CommonLabel sendLabel;
    private JLabel loaderLabel;

    private GroupLayout layout;
    private GroupLayout.ParallelGroup hParallel;
    private GroupLayout.SequentialGroup vSequential;

    private MainChatPanel root;

    private CommonBus commonBus;
    private ChatBus chatBus;

    private Thread recvThread;

    public ChatPanel(MainChatPanel mainChatPanel, CommonBus commonBus, ChatBus chatBus) {
        // TODO: style ChatPanel
        this.setLocation(0, MainFrame.HEIGHT_TASKBAR - 42);
        this.setSize(MainFrame.WIDTH_FRAME, MainFrame.HEIGHT_FRAME - MainFrame.HEIGHT_TASKBAR);
        this.setBackground(Color.decode(ClientPanel.BACKGROUND));
        this.setLayout(null);

        // TODO: class for handle events
        this.commonBus = commonBus;
        this.chatBus = chatBus;
        this.root = mainChatPanel;

        // TODO: add components
        this.initComponents();
        // TODO: start recv_thread
        this.recvThread = new Thread(this);
        this.recvThread.setDaemon(true);
        this.recvThread.start();
    }

    private void initComponents() {
        // TODO: constructor
        this.contentPanel = new JPanel();
        this.scrollPanel = new JScrollPane();
        this.messageText = new JTextArea();
        this.scrollText = new JScrollPane();
        this.sendLabel = new CommonLabel();
        this.loaderLabel = new JLabel();

        // TODO: create layout for content_panel
        this.layout = new GroupLayout(this.contentPanel);
        this.hParallel = this.layout.createParallelGroup();
        this.vSequential = this.layout.createSequentialGroup();
        this.layout.setHorizontalGroup(hParallel);
        this.layout.setVerticalGroup(vSequential);
        // TODO: style content_panel;
        this.contentPanel.setBackground(Color.decode(ChatPanel.CONTENT_BACKGROUND));
        this.contentPanel.setLayout(layout);
        // TODO: style scroll_panel
        this.scrollPanel.setViewportView(this.contentPanel);
        this.scrollPanel.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        this.scrollPanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        this.scrollPanel.setBounds(10, 15, MainFrame.WIDTH_FRAME - 20, MainFrame.HEIGHT_FRAME - 150);
        this.add(this.scrollPanel);
        // TODO: style message_text
        this.messageText.setFont(new Font("consolas", Font.PLAIN, 14));
        this.messageText.setLineWrap(true);
        this.messageText.getDocument().putProperty("filterNewlines", Boolean.TRUE);
        this.messageText.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                messageTextKeyPressed(e);
            }
        });

        // TODO: style scroll_text
        this.scrollText.setViewportView(this.messageText);
        this.scrollText.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        this.scrollText.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        this.scrollText = new JScrollPane(this.messageText);
        this.scrollText.setBounds(30, scrollPanel.getHeight() + 20, MainFrame.WIDTH_FRAME - 150, MainFrame.HEIGHT_FRAME - MainFrame.HEIGHT_TASKBAR - scrollPanel.getHeight() - 30);
        this.add(this.scrollText);
        // TODO: style send_label
        this.sendLabel.setText("Send");
//        this.sendLabel.setIcon(new ImageIcon(this.getClass().getClassLoader().getResource("send_icon.png")));
        this.sendLabel.setForeground(Color.decode(ClientPanel.FOREGROUND));
        this.sendLabel.setFont(new Font("segoe ui", Font.PLAIN, 13));
        this.sendLabel.setBounds(this.scrollText.getWidth() + 50, this.scrollText.getY(), 80, 30);
        this.sendLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                sendLabelMousePressed(e);
            }
        });
        this.add(this.sendLabel);

        // TODO: style loader_label
        this.loaderLabel.setVisible(false);
        this.add(this.loaderLabel);
    }

    public String getHostName() {
        return this.chatBus.getSocket().getInetAddress().getHostName();
    }

    @Override
    public void setEnabled(boolean b) {
        this.messageText.setEnabled(b);
        this.sendLabel.setEnabled(b);
    }

    @Override
    public void setVisible(boolean b) {
        // TODO: move scroll to bottom when show up
        this.scrollPanel.getViewport().setViewPosition(new Point(0, this.scrollPanel.getVerticalScrollBar().getMaximum()));
        this.scrollPanel.getViewport().setViewPosition(new Point(0, this.scrollPanel.getVerticalScrollBar().getMaximum()));
        super.setVisible(b);
    }

    private void messageTextKeyPressed(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_ENTER) {
            this.sendMessage();
        }
    }

    private void sendLabelMousePressed(MouseEvent e) {
        if(e.getButton() == MouseEvent.BUTTON1 && this.sendLabel.isEnabled()) {
            this.sendMessage();
        }
    }

    // TODO: send a string message
    private void sendMessage() {
        try {
            String content = this.messageText.getText();
            if(!content.trim().equals("")) {
                StringMessage str_message = new StringMessage(InetAddress.getLocalHost().getHostName(), content);
                this.chatBus.sendMessage(str_message);
                int gap = this.scrollPanel.getWidth() - 180;

                JLabel label = new JLabel("You send a message:" + content);
                label.setFont(new Font("consolas", Font.PLAIN, 14));
                label.setForeground(Color.BLACK);
                this.addMessageToPanel(label, gap, "blue");
            }
            this.messageText.setText("");
        }
        catch(Exception exception) {
            JOptionPane.showMessageDialog(this, "Can't send message:\n" + exception.getMessage());
        }
    }

   
    

    // TODO: show message in panel
    public void addMessageToPanel(JLabel label, int gap, String color_header) {
        EventQueue.invokeLater(() -> {
            label.setText(this.handleMessage(label.getText(), color_header));

            this.hParallel.addGroup(
                this.layout.createSequentialGroup()
                    .addContainerGap(gap, gap)
                    .addComponent(label)
            );
            this.vSequential.addComponent(label).addGap(10, 10, 10);
            this.scrollPanel.revalidate();

            // TODO: move scroll to bottom
            this.scrollPanel.getViewport().setViewPosition(new Point(0, this.scrollPanel.getVerticalScrollBar().getMaximum()));
            this.scrollPanel.getViewport().setViewPosition(new Point(0, this.scrollPanel.getVerticalScrollBar().getMaximum()));
        });
    }

    // TODO: format message
    private String handleMessage(String message, String color_header) {
        String formatted_message = "<html>";
        formatted_message += this.getHeaderName(message, color_header);
        formatted_message += this.multiLinesString(message);
        formatted_message += "</html>";
        return formatted_message;
    }

    private String getHeaderName(String message, String color_header) {
        if(message.contains(":")) {
            String header_name = "<font color='" + color_header + "'>";
            header_name += message.substring(0, message.indexOf(':') + 1) + "</font><br>";
            return header_name;
        }
        return "";
    }

    private String multiLinesString(String message) {
        message = message.substring(message.indexOf(':') + 1);
        if(message.length() > ChatPanel.MAX_LENGTH_LINE) {
            int loops = message.length() / ChatPanel.MAX_LENGTH_LINE;
            int index = 0;
            String content = "";
            for(int i = 0; i < loops; ++i) {
                content += message.substring(index, index + ChatPanel.MAX_LENGTH_LINE) + "<br>";
                index += ChatPanel.MAX_LENGTH_LINE;
            }
            content += message.substring(index);
            return content;
        }
        return message;
    }

    // TODO: recv messages
    @Override
    public void run() {
        while(true) {
            try {
                if(this.commonBus.getTcpServer().isHasPartner() || this.commonBus.getTcpClient().isConnectedServer()) {
                    Message obj_message = this.chatBus.recvMessage();
                    if(obj_message != null) {
                        if(obj_message.getCurrentType() == Message.STRING_MESSAGE) {
                            StringMessage str_message = (StringMessage) obj_message;

                            JLabel label = new JLabel(str_message.getSender() + " send a message:" + str_message.getContent());
                            label.setFont(new Font("consolas", Font.PLAIN, 14));
                            label.setForeground(Color.black);
                            this.addMessageToPanel(label, 0, "blue");
                        }
                        
                    }
                    continue; // TODO: pass sleep if connected
                }
                Thread.sleep(1000); // TODO: update status of client and server
            }
            catch(Exception e) {
                this.commonBus.getTcpServer().setHasPartner(false);
                this.commonBus.getTcpClient().setConnectedServer(false);

                this.root.remove(this);
                this.root.addCount(-1);
                this.root.getChatPanels().remove(this);
                this.root.validate();
                this.root.revalidate();
                this.root.repaint();

                try {
                    this.chatBus.getSocket().close();
                }
                catch(Exception exception) {}

                break;
            }
        }
    }

    private ConnectionItem item;
    public void setConnectionItem(ConnectionItem item) {
        this.item = item;
    }
}