package gui.common;

import gui.MainFrame;
import gui.client.ClientPanel;
import java.awt.Color;
import java.awt.Font;
import javax.swing.*;
import javax.swing.border.TitledBorder;

// TODO: default for server panel
public class CommonPanel extends JPanel {
    private JLabel hostLabel;
    private JComboBox<String> hostCombo;
    private JTextField hostText;
    private JLabel portLabel;
    private JLabel passLabel;
    private JTextField portText;
    private JTextField passText;
    private JPasswordField passField;

    public CommonPanel() {
        // TODO: style common panel
        this.setBorder(BorderFactory.createTitledBorder(null, "Server Listening",
            TitledBorder.DEFAULT_JUSTIFICATION,
            TitledBorder.DEFAULT_POSITION,
            new Font("segoe ui", Font.BOLD, 16),
            Color.decode(ClientPanel.FOREGROUND))
        );
        this.setBounds(50, 30, MainFrame.WIDTH_FRAME - 100, 230);
        this.setBackground(Color.decode(ClientPanel.BACKGROUND));
        this.setForeground(Color.decode(ClientPanel.FOREGROUND));
        this.setLayout(null);

        // TODO: add components
        this.initcomponents();
    }

    private void initcomponents() {
        // TODO: constructor
        this.hostLabel = new JLabel();
        this.hostCombo = new JComboBox<>();
        this.hostText = new JTextField();
        this.portLabel = new JLabel();
        this.passLabel = new JLabel();
        this.portText = new JTextField();
        this.passText = new JTextField();
        this.passField = new JPasswordField();

        // TODO: style host_label
        this.hostLabel.setText("Server IP:");
        this.hostLabel.setBounds(30, 30, 100, 30);
        this.hostLabel.setFont(new Font("segoe ui", Font.BOLD, 14));
        this.add(this.hostLabel);

        // TODO: style host_combo for server panel
        this.hostCombo.setBounds(140, 35, 130, 20);
        this.add(this.hostCombo);

        // TODO: style host_text for client panel
        this.hostText.setBounds(140, 35, 130, 20);
        this.hostText.setVisible(false);
        this.add(this.hostText);

        // TODO: style port_label
        this.portLabel.setText("Server port:");
        this.portLabel.setBounds(30, 60, 100, 30);
        this.portLabel.setFont(new Font("segoe ui", Font.BOLD, 14));
        this.add(this.portLabel);

        // TODO: style port_text
        this.portText.setBounds(140, 65, 130, 20);
        this.portText.setText("8080");
        this.add(this.portText);

        // TODO: style pass_label
        this.passLabel.setText("Set password:");
        this.passLabel.setBounds(30, 90, 100, 30);
        this.passLabel.setFont(new Font("segoe ui", Font.BOLD, 14));
        this.add(this.passLabel);

        // TODO: style pass_text for server panel
        this.passText.setBounds(140, 95, 130, 20);
        this.add(this.passText);

        // TODO: style pass_field for client panel
        this.passField.setBounds(140, 95, 130, 20);
        this.passField.setVisible(false);
        this.add(this.passField);

       
    }



    @Override
    public void setEnabled(boolean b) {
        this.hostCombo.setEnabled(b);
        this.hostText.setEnabled(b);
        this.passField.setEnabled(b);
        this.passText.setEnabled(b);
        this.portText.setEnabled(b);
    }

    public JLabel getHostLabel() {
        return hostLabel;
    }

    public JComboBox<String> getHostCombo() {
        return hostCombo;
    }

    public JTextField getHostText() {
        return hostText;
    }

    public JLabel getPortLabel() {
        return portLabel;
    }

    public JLabel getPassLabel() {
        return passLabel;
    }

    public JTextField getPortText() {
        return portText;
    }

    public JTextField getPassText() {
        return passText;
    }

    public JPasswordField getPassField() {
        return passField;
    }

    
}