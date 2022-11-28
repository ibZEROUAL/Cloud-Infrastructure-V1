package com.sqli;

public class Machine {
    // create a new machine takes 4 parameters : name, operating system, disk size, memory.
    private String machineName;
    private String operatingSystem;
    private String diskSize;
    private String memory;

    private String machineState;

    Machine(String machineName, String operatingSystem, String diskSize, String memory) {
        this.machineName = machineName;
        this.operatingSystem = operatingSystem;
        this.diskSize = diskSize;
        this.memory = memory;
        this.machineState = String.valueOf(MachineState.inactive);
    }

    public String getMachineName() {
        return machineName;
    }

    public void setMachineName(String machineName) {
        this.machineName = machineName;
    }

    public String getOperatingSystem() {
        return operatingSystem;
    }

    public void setOperatingSystem(String operatingSystem) {
        this.operatingSystem = operatingSystem;
    }

    public String getDiskSize() {
        return diskSize;
    }

    public void setDiskSize(String diskSize) {
        this.diskSize = diskSize;
    }

    public String getMemory() {
        return memory;
    }

    public void setMemory(String memory) {
        this.memory = memory;
    }

    public String getMachineState() {
        return machineState;
    }

    public void setMachineState(String machineState) {
        this.machineState = machineState;
    }
}
