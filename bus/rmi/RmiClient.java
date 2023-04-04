package bus.rmi;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class RmiClient {
    private IRemoteDesktop remoteObj;
    private boolean isRemoteServer;

    public RmiClient() {
        this.remoteObj = null;
        this.isRemoteServer = false;
    }

    public void startConnectingToRmiServer(String host, int port) throws RemoteException, NotBoundException, MalformedURLException {
        if(this.isRemoteServer == false) {
            String url = "rmi://" + host + ":" + port + "/remote";
            this.remoteObj = (IRemoteDesktop) Naming.lookup(url);
            this.isRemoteServer = true;
        }
    }

    public void stopConnectingToRmiServer() {
        if(this.isRemoteServer == true) {
            this.remoteObj = null;
            this.isRemoteServer = false;
        }
    }

    public IRemoteDesktop getRemoteObject() {
        return this.remoteObj;
    }

    public boolean isRemoteServer() {
        return this.isRemoteServer;
    }

    public void setRemoteServer(boolean b) {
        this.isRemoteServer = b;
    }
}
