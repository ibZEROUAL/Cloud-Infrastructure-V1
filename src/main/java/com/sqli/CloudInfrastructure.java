package com.sqli;

import com.sqli.exceptions.CreateStoreException;
import com.sqli.exceptions.MachineStateException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CloudInfrastructure {

    List<Store> stores = new ArrayList<>();
    List<Machine>  machines = new ArrayList<>();
    long numberOfStores = 0;
    long numberOfMachines = 0;
    long numberOfDocumentsInStore;

    MachineBuilder machineBuilder = new MachineBuilder();


    public void createStore(String storeName) {
        for (Store store: stores) {
            if (store.getStoreName().equals(storeName)){
                throw new CreateStoreException();
            }
        }
     Store newStore =  Store.createStoreFromFactory(storeName);
     stores.add(newStore);
     this.numberOfStores++;
    }

    public void uploadDocument(String storeName, String ...documents) {
        Store concernedStoreToUploadIn = stores.parallelStream()
                .filter(store -> store.getStoreName().equals(storeName))
                .findAny()
                .get();
        concernedStoreToUploadIn.getDocuments().addAll(Arrays.asList(documents));
    }

    public String listStores() {
        StringBuilder result = new StringBuilder();

        for (Store store:stores) {

            result.append(store.getStoreName()).append(":");
            if (store.getDocuments().size() == 0) result.append("empty");
            numberOfDocumentsInStore = store.getNumberOfDocuments();

            for (String doc:store.getDocuments()) {
                result.append(doc);
                numberOfDocumentsInStore--;
                if (numberOfDocumentsInStore > 0 ){
                    result.append(", ");
                }
            }
            this.numberOfStores--;
            if (this.numberOfStores>0){
                result.append("||");
            }
        }
        return String.valueOf(result);
    }

    public void deleteStore(String storeName) {
        Store storeToDelete = stores.parallelStream()
                .filter(store -> store.getStoreName().equals(storeName))
                .findAny()
                .get();
        stores.remove(storeToDelete);
        this.numberOfStores--;
    }

    public void emptyStore(String storeName) {
        Store storeToEmpty = stores.parallelStream()
                .filter(store -> store.getStoreName().equals(storeName))
                .findAny()
                .get();
        storeToEmpty.deleteDocuments();
    }

    public void createMachine(String machineName, String operationSystem, String diskSize, String memory) {
      Machine machine = machineBuilder.setMachineName(machineName)
              .setOperatingSystem(operationSystem).setDiskSize(diskSize).setMemory(memory).createMachine();
      machines.add(machine);
      this.numberOfMachines++;
    }

    public String listMachines() {
        StringBuilder result = new StringBuilder();
        long temp = this.numberOfMachines;
        for (Machine machine: machines) {
            result.append(machine.getMachineName()).append(":").append(machine.getMachineState());
            temp--;
            if (temp>0) {
                result.append("||");
            }

        }
        return String.valueOf(result);
    }

    public void startMachine(String machineName) {
       Machine concernedMachine =  machines.parallelStream()
               .filter(machine -> machine.getMachineName().equals(machineName))
               .findAny()
               .get();
       if (concernedMachine.getMachineState().equals(String.valueOf(MachineState.running))){
           throw new MachineStateException();
       }

       concernedMachine.setMachineState(String.valueOf(MachineState.running));
    }

    public void stopMachine(String machineName) {
        Machine concernedMachine =  machines.parallelStream()
                .filter(machine -> machine.getMachineName().equals(machineName))
                .findAny()
                .get();
        concernedMachine.setMachineState(String.valueOf(MachineState.stopped));
    }

    public double usedMemory(String machineName) {
        Machine concernedMachine =  machines.parallelStream()
                .filter(machine -> machine.getMachineName().equals(machineName))
                .findAny()
                .get();
        if (concernedMachine.getMachineState().equals(String.valueOf(MachineState.inactive)) || concernedMachine.getMachineState().equals(String.valueOf(MachineState.stopped))){
            return 0;
        }

        double machineMemory = Double.parseDouble(concernedMachine.getMemory().substring(0,concernedMachine.getMemory().length()-2));

        return machineMemory;
    }

    public double usedDisk(String name) {

        if (machines.parallelStream().anyMatch(machine -> machine.getMachineName().equals(name))){
           return usedDiskMachine(name);
        }else {
            return usedDiskStore(name);
        }
    }

    private double usedDiskStore(String name) {
        Store concernedStore = stores.parallelStream()
                .filter(store -> store.getStoreName().equals(name))
                .findAny()
                .get();
        return concernedStore.getNumberOfDocuments() * 0.1;
    }

    private double usedDiskMachine(String machineName) {
        Machine concernedMachine =  machines.parallelStream()
                .filter(machine -> machine.getMachineName().equals(machineName))
                .findAny()
                .get();

        String diskSizeString =  concernedMachine.getDiskSize().substring(0,concernedMachine.getDiskSize().length()-2);
        double diskSizeDouble = Double.parseDouble(diskSizeString);
        return diskSizeDouble;
    }

    public double globalUsedDisk() {
       double result =  machines.parallelStream()
                .map(machine -> Double.parseDouble(machine.getDiskSize().substring(0,machine.getDiskSize().length()-2)))
                .reduce(0.0,Double::sum);

       result +=  stores.parallelStream()
                .map(store -> usedDiskStore(store.getStoreName()))
                .reduce(0.0,Double::sum);

        return result;
    }

    public double globalUsedMemory() {

        double result = machines.parallelStream()
                .filter(machine -> machine.getMachineState().equals(String.valueOf(MachineState.running)))
                .map(machine -> Double.parseDouble(machine.getMemory().substring(0,machine.getMemory().length()-2)))
                .reduce(0.0,Double::sum);
        return result;
    }
}
