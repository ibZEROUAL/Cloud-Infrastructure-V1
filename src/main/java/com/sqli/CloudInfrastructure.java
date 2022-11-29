package com.sqli;

import com.sqli.exceptions.CreateStoreException;
import com.sqli.exceptions.MachineStateException;
import com.sqli.machine.Machine;
import com.sqli.machine.MachineBuilder;
import com.sqli.machine.MachineState;
import com.sqli.store.Store;
import com.sqli.store.StoreFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class CloudInfrastructure {

    List<Store> stores ;
    List<Machine>  machines ;
    MachineBuilder machineBuilder = new MachineBuilder();

    public CloudInfrastructure() {
        this.stores = new ArrayList<>();
        this.machines = new ArrayList<>();
    }

    public void createStore(String storeName) {
        for (Store store: stores) {
            if (store.getStoreName().equals(storeName)){
                throw new CreateStoreException();
            }
        }
     Store newStore =  StoreFactory.createStoreFromFactory(storeName);
     stores.add(newStore);
    }

    public void uploadDocument(String storeName, String ...documents) {
        Store concernedStoreToUploadIn = getConcernedStore(storeName);
        concernedStoreToUploadIn.getDocuments().addAll(Arrays.asList(documents));
    }

    public String listStores() {
        StringBuilder result = new StringBuilder();

        Iterator<Store> storeIterator = stores.iterator();

        while (storeIterator.hasNext()){
            Store store = storeIterator.next();

            result.append(store.getStoreName()).append(":");
            if (store.getDocuments().size() == 0) result.append("empty");

            String docs = String.join(", ",store.getDocuments());
            result.append(docs);

            if (storeIterator.hasNext()) result.append("||");
        }
        return result.toString();
    }


    public void deleteStore(String storeName) {
        Store storeToDelete = getConcernedStore(storeName);
        stores.remove(storeToDelete);
    }

    public void emptyStore(String storeName) {
        Store storeToEmpty = getConcernedStore(storeName);
        storeToEmpty.deleteDocuments();
    }
    private Store getConcernedStore(String storeName) {
        return stores.parallelStream()
                .filter(store -> store.getStoreName().equals(storeName))
                .findAny()
                .get();
    }

    public void createMachine(String machineName, String operationSystem, String diskSize, String memory) {
      Machine machine = machineBuilder.setMachineName(machineName)
              .setOperatingSystem(operationSystem).setDiskSize(diskSize).setMemory(memory).createMachine();
      machines.add(machine);
    }

    public String listMachines() {
        StringBuilder result = new StringBuilder();
        Iterator<Machine> machineIterator = machines.iterator();

       while (machineIterator.hasNext()) {
            Machine machine = machineIterator.next();
            result.append(machine.getMachineName()).append(":").append(machine.getMachineState());
           if(machineIterator.hasNext()) result.append("||");
        }
        return result.toString();
    }

    public void startMachine(String machineName) {
       Machine concernedMachine = getConcernedMachine(machineName);
       if (concernedMachine.getMachineState().equals(String.valueOf(MachineState.running))){
           throw new MachineStateException();
       }
       concernedMachine.setMachineState(String.valueOf(MachineState.running));
    }

    public void stopMachine(String machineName) {
        Machine concernedMachine = getConcernedMachine(machineName);
        concernedMachine.setMachineState(String.valueOf(MachineState.stopped));
    }

    private Machine getConcernedMachine(String machineName) {
        return machines.parallelStream()
                .filter(machine -> machine.getMachineName().equals(machineName))
                .findAny()
                .get();
    }

    public double usedMemory(String machineName) {
        Machine concernedMachine = getConcernedMachine(machineName);

        boolean isNotRunning = concernedMachine.getMachineState().equals(String.valueOf(MachineState.inactive)) ||
                concernedMachine.getMachineState().equals(String.valueOf(MachineState.stopped));

        if (isNotRunning){
            return 0;
        }

        double machineMemory = Double.parseDouble(getDoublePartFromDiskSizeOrMemory(concernedMachine.getMemory()));

        return machineMemory;
    }

    public double usedDisk(String name) {

        boolean isMachinePresent = machines.parallelStream()
                .anyMatch(machine -> machine.getMachineName().equals(name));

        if (isMachinePresent){
           return usedDiskMachine(name);
        }else{
            return usedDiskStore(name);
        }
    }

    private double usedDiskStore(String StoreName) {
        Store concernedStore = getConcernedStore(StoreName);
        return concernedStore.getNumberOfDocuments() * 0.1;
    }

    private double usedDiskMachine(String machineName) {
        Machine concernedMachine = getConcernedMachine(machineName);

        String diskSizeString = getDoublePartFromDiskSizeOrMemory(concernedMachine.getDiskSize());
        double diskSizeDouble = Double.parseDouble(diskSizeString);

        return diskSizeDouble;
    }

    private static String getDoublePartFromDiskSizeOrMemory(String concernedMachine) {
        return concernedMachine.substring(0, concernedMachine.length() - 2);
    }

    public double globalUsedDisk() {
       double result =  machines.parallelStream()
                .map(machine -> Double.parseDouble(getDoublePartFromDiskSizeOrMemory(machine.getDiskSize())))
                .reduce(0.0,Double::sum);

       result +=  stores.parallelStream()
                .map(store -> usedDiskStore(store.getStoreName()))
                .reduce(0.0,Double::sum);

        return result;
    }

    public double globalUsedMemory() {

        double result = machines.parallelStream()
                .filter(machine -> machine.getMachineState().equals(String.valueOf(MachineState.running)))
                .map(machine -> Double.parseDouble(getDoublePartFromDiskSizeOrMemory(machine.getMemory())))
                .reduce(0.0,Double::sum);
        return result;
    }
}
