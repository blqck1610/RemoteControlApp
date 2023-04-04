package bus.rmi;

import gui.monitoring.ComputerInfo;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IRemoteDesktop extends Remote {
    // TODO: for capture screen to share
    byte[] takeScreenshotServer(String quality) throws Exception;


    // TODO: for get hardware info of server
    double getCpuLoadServer() throws RemoteException;
    double getRamUsageServer() throws RemoteException;
    long[] getRamMemories() throws RemoteException;
    int getCpus() throws RemoteException;
    ComputerInfo getComputerInformation() throws RemoteException;
}

