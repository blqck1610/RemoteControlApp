package bus.rmi;

import java.awt.AWTException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

public class RmiServer {
    private String url;
    private boolean isBinding;

    public RmiServer() {
        this.url = null;
        this.isBinding = false;
    }

    public void startBindingOnRmiServer(String host, int port) throws RemoteException, MalformedURLException, AWTException {
        if(this.isBinding == false) {
            try {
                this.url = "rmi://" + host + ":" + port + "/remote";
                this.isBinding = true;
                System.setProperty("java.rmi.server.hostname", host);
                LocateRegistry.createRegistry(port);
                
                Naming.rebind(this.url, new RemoteDesktopImpl()); // only new object in here
            }
            catch(Exception e) {
                // TODO: rebind when port already bound
                Naming.rebind(this.url, new RemoteDesktopImpl());
            }
        }
    }

    public void stopBindingOnRmiServer() throws RemoteException, MalformedURLException, NotBoundException {
        if(this.isBinding == true) {
            Naming.unbind(this.url);
            this.url = null;
            this.isBinding = false;
        }
    }

    public boolean isBinding() {
        return this.isBinding;
    }
}
