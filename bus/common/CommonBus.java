package bus.common;

import bus.rmi.RmiClient;
import bus.rmi.RmiServer;
import bus.tcp.TcpClient;
import bus.tcp.TcpServer;
import gui.chat.MainChatPanel;
import java.awt.AWTException;
import java.io.IOException;
import java.rmi.NotBoundException;

public class CommonBus {
	// TODO: for server
	private TcpServer tcpServer;
    private RmiServer rmiServer;

	// TODO: for client
	private TcpClient tcpClient;
    private RmiClient rmiClient;

	public CommonBus() {
        this.rmiServer = new RmiServer();
        this.rmiClient = new RmiClient();
	}

	public void setMainChatPanel(MainChatPanel main_chat_panel) {
		this.tcpServer = new TcpServer(main_chat_panel);
		this.tcpClient = new TcpClient(main_chat_panel);
	}

	public TcpServer getTcpServer() {
		return this.tcpServer;
	}

    public RmiServer getRmiServer() {
        return this.rmiServer;
    }

	public TcpClient getTcpClient() {
		return this.tcpClient;
	}

    public RmiClient getRmiClient() {
        return this.rmiClient;
    }

	// TODO: handle events of server
	public void startListeningOnServer(String host, int port, String password) throws IOException, AWTException {
        if(!this.tcpServer.isListening() && !this.rmiServer.isBinding()) {
		// Port rmi = port tcp + 1
			this.tcpServer.startListeningOnTcpServer(host, port, password);
            this.rmiServer.startBindingOnRmiServer(host, port + 1);

		}
	}

	public void stopListeningOnServer() throws IOException, NotBoundException {
        if(this.tcpServer.isListening() && this.rmiServer.isBinding()) {
			this.tcpServer.stopListeningOnTcpServer();
            this.rmiServer.stopBindingOnRmiServer();
		}
	}

	public void startConnectingToServer(String host, int port, String password) throws Exception {
		// TODO: check server is listening?
		if (this.tcpServer.isListening()) {
			String ip_server = this.tcpServer.getServer().getInetAddress().getHostAddress();
			if (host.equals(ip_server))
				throw new Exception("Can't remote yourself!");
			System.out.println(ip_server);
			System.out.println(host);
		}
		if (this.tcpClient.isConnectedServer())
			throw new Exception("You are remoting!");
		this.tcpClient.startConnectingToTcpServer(host, port, password);
        this.rmiClient.startConnectingToRmiServer(host, port + 1);
	}
}
