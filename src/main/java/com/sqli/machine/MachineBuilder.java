package com.sqli.machine;

public class MachineBuilder {
    private String machineName;
    private String operatingSystem;
    private String diskSize;
    private String memory;

    public MachineBuilder setMachineName(String machineName) {
        this.machineName = machineName;
        return this;
    }

    public MachineBuilder setOperatingSystem(String operatingSystem) {
        this.operatingSystem = operatingSystem;
        return this;
    }

    public MachineBuilder setDiskSize(String diskSize) {
        this.diskSize = diskSize;
        return this;
    }

    public MachineBuilder setMemory(String memory) {
        this.memory = memory;
        return this;
    }

    public Machine createMachine() {
        return new Machine(machineName, operatingSystem, diskSize, memory);
    }
}
