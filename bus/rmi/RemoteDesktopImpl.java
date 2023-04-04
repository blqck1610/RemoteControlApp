package bus.rmi;

import gui.monitoring.ComputerInfo;
import gui.monitoring.DriveInfo;
import com.sun.management.OperatingSystemMXBean;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.lang.management.ManagementFactory;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import javax.imageio.ImageIO;
import javax.swing.filechooser.FileSystemView;

public class RemoteDesktopImpl extends UnicastRemoteObject implements IRemoteDesktop {
    public final static int GB = 1024 * 1024 * 1024;

    private Robot mr_robot;
    private OperatingSystemMXBean os;

    public RemoteDesktopImpl() throws RemoteException, AWTException {
        super();
        this.mr_robot = new Robot();
        this.os = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
    }

    @Override
    public byte[] takeScreenshotServer(String quality) throws Exception {
        Dimension screen_size = Toolkit.getDefaultToolkit().getScreenSize();
        Rectangle bounds = new Rectangle(screen_size);
        BufferedImage screenshot = this.mr_robot.createScreenCapture(bounds);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ImageIO.setUseCache(false); // TODO: not using disk cache (using ram)
        ImageIO.write(screenshot, quality, bos);
        return bos.toByteArray();
    }


    // TODO: for get hardware info

    @Override
    public double getCpuLoadServer() throws RemoteException {
        return this.os.getCpuLoad();
    }

    @Override
    public double getRamUsageServer() throws RemoteException {
        double ratio = (double) (this.os.getTotalMemorySize() - this.os.getFreeMemorySize()) / this.os.getTotalMemorySize();
        return ratio;
    }

    @Override
    public long[] getRamMemories() throws RemoteException {
        return new long[] {
            1 + this.os.getTotalMemorySize() / RemoteDesktopImpl.GB,
            1 + this.os.getTotalSwapSpaceSize() / RemoteDesktopImpl.GB
        };
    }

    @Override
    public int getCpus() throws RemoteException {
        return this.os.getAvailableProcessors();
    }

    @Override
    public ComputerInfo getComputerInformation() throws RemoteException {
        ComputerInfo pcInfo = new ComputerInfo(this.os.getName());
        for(File file : File.listRoots()) {
            pcInfo.getDrives().add(
                new DriveInfo(
                    FileSystemView.getFileSystemView().getSystemDisplayName(file),
                    file.getFreeSpace() / RemoteDesktopImpl.GB,
                    file.getTotalSpace() / RemoteDesktopImpl.GB
                )
            );
        }
        return pcInfo;
    }
}
