package com.sqli;

import java.util.ArrayList;
import java.util.List;

public class Store {

    private String storeName;
    private List<String> documents ;

    private double diskSize;

    private long numberOfDocuments;

    private Store(String storeName) {
        this.storeName = storeName;
        this.documents = new ArrayList<>();
    }

    public static Store createStoreFromFactory(String storeName) {
        return new Store(storeName);
    }

    public String getStoreName() {
        return storeName;
    }

    public void deleteDocuments() {
        this.numberOfDocuments = 0;
        this.documents = new ArrayList<>();
    }

    public List<String> getDocuments() {
        this.numberOfDocuments = this.documents.parallelStream().count();
        return documents;
    }

    public long getNumberOfDocuments(){
        return this.numberOfDocuments;
    }

    public double getDiskSize() {
        return diskSize;
    }

    public void setDiskSize() {
        this.diskSize = this.numberOfDocuments * 0.1;
    }
}
