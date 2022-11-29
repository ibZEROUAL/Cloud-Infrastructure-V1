package com.sqli.store;

public class StoreFactory {
    public static Store createStoreFromFactory(String storeName) {
        return new Store(storeName);
    }
}
