package com.sqli.store;

import java.util.ArrayList;
import java.util.List;

public class Store {

    private String storeName;
    private List<String> documents ;

    private double diskSize;

    private long numberOfDocuments;

    Store(String storeName) {
        this.storeName = storeName;
        this.documents = new ArrayList<>();
    }

    public String getStoreName() {
        return storeName;
    }

    public void deleteDocuments() {
        this.numberOfDocuments = 0;
        this.documents.clear();
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
