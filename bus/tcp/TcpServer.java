package bus.tcp;

import bus.chat.ChatBus;
import gui.chat.MainChatPanel;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;
import java.util.Enumeration;
import java.util.Vector;

public class TcpServer {
    private MainChatPanel mainChatPanel;

    private ServerSocket server;
    private Socket client;
    private String password;

    private boolean isListening;
    private boolean isHasPartner;


    public TcpServer(MainChatPanel mainChatPanel) {
        this.server = null;
        this.client = null;
        this.password = null;
        this.isListening = false;
        this.isHasPartner = false;
        this.mainChatPanel = mainChatPanel;
    }

    public void startListeningOnTcpServer(String host, int port, String password) throws IOException {
        if(this.isListening == false) {
            InetSocketAddress endpoint = new InetSocketAddress(host, port);
            this.password = password;
            this.server = new ServerSocket();
            this.server.bind(endpoint);
            this.isListening = true;
          
        }
    }

    public void stopListeningOnTcpServer() throws IOException {
        if(this.isListening == true) {
            this.server.close();
            if(this.client != null) this.client.close();
            this.isListening = false;
            this.isHasPartner = false;
        }
    }

    public void waitingConnectionFromClient() throws IOException {
        this.client = this.server.accept();
        DataOutputStream dos = new DataOutputStream(this.client.getOutputStream());
        DataInputStream dis = new DataInputStream(this.client.getInputStream());
        String password = dis.readUTF();
        String result = null;
        if(this.password.equals(password)) {
            result = "true";
            ChatBus chatBus = new ChatBus(this.client);
            this.mainChatPanel.addNewConnection(chatBus);
            this.isHasPartner = true;
        }
        else result = "false";
        dos.writeUTF(result);
    }

    

    public boolean isListening() {
        return this.isListening;
    }

    public boolean isHasPartner() {
        return this.isHasPartner;
    }

    public void setHasPartner(boolean b) {
        this.isHasPartner = b;
    }

    public ServerSocket getServer() {
        return this.server;
    }
}
