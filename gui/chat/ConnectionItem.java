package gui.chat;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;

public class ConnectionItem extends JMenuItem {
    private ChatPanel chatPanel;
    private ArrayList<ChatPanel> chatPanels;

    public ConnectionItem(ChatPanel chatPanel, ArrayList<ChatPanel> chat_panels) {
        this.setText(chatPanel.getHostName());
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                connectionItemMousePressed(e);
            }
        });

        this.chatPanel = chatPanel;
        this.chatPanels = chat_panels;
    }

    private void connectionItemMousePressed(MouseEvent e) {
        if(e.getButton() == MouseEvent.BUTTON1) {
            for(ChatPanel panel : this.chatPanels) {
                panel.setVisible(false);
            }
            this.chatPanel.setVisible(true);
        }
    }
}

