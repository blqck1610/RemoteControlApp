package gui.monitoring;

import java.io.Serializable;
import java.util.ArrayList;

public class ComputerInfo implements Serializable {
    private String osName;
    private ArrayList<DriveInfo> drives;

    public ComputerInfo(String os_name) {
        this.osName = os_name;
        this.drives = new ArrayList<>();
    }

    public String getOsName() {
        return this.osName;
    }

    public ArrayList<DriveInfo> getDrives() {
        return this.drives;
    }
}
