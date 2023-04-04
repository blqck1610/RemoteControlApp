package bus.tcp;

import bus.chat.ChatBus;
import gui.chat.MainChatPanel;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class TcpClient extends Thread {
    private MainChatPanel mainChatPanel;

    private Socket client;
    private boolean isConnectedServer;

    public TcpClient(MainChatPanel mainChatPanel) {
        this.client = null;
        this.isConnectedServer = false;
        this.mainChatPanel = mainChatPanel;
    }

    public void startConnectingToTcpServer(String host, int port, String password) throws IOException {
        if(this.isConnectedServer == false) {
            this.client = new Socket(host, port);
            DataOutputStream dos = new DataOutputStream(this.client.getOutputStream());
            DataInputStream dis = new DataInputStream(this.client.getInputStream());
            
            dos.writeUTF(password);
            
            String result = dis.readUTF();
            if(result.equals("true")) {
                ChatBus chat_bus = new ChatBus(this.client);
                this.mainChatPanel.addNewConnection(chat_bus);
                this.isConnectedServer = true;
            }
            else if(result.equals("false")) {
                this.client.close();
                throw new IOException("Wrong password of server");
            }
        }
    }

    public void stopConnectingToTcpServer() throws IOException {
        if(this.isConnectedServer = true) {
            this.client.close();
            //this.chat_bus.setSocket(null);
            this.isConnectedServer = false;
        }
    }

    public boolean isConnectedServer() {
        return this.isConnectedServer;
    }

    public void setConnectedServer(boolean b) {
        this.isConnectedServer = b;
    }

    public Socket getClient() {
        return this.client;
    }
}

