package problem.src.models;

import java.util.ArrayList;
import java.util.List;

public class InstanceData {
    private List<WarehouseClass> warehouseList;
    private List<StoreClass> storeList;
    private List<SupplyClass> supplyList;
    private int incompatibilities;
    private List<int[]> incompatiblePairs;

    // Constructor
    public InstanceData() {
        this.warehouseList = new ArrayList<>();
        this.storeList = new ArrayList<>();
        this.supplyList = new ArrayList<>();
        this.incompatiblePairs = new ArrayList<>();
    }

    // Getters and Setters
    public List<WarehouseClass> getWarehouseList() {
        return warehouseList;
    }

    public void setWarehouseList(List<WarehouseClass> warehouseList) {
        this.warehouseList = warehouseList;
    }

    public List<StoreClass> getStoreList() {
        return storeList;
    }

    public void setStoreList(List<StoreClass> storeList) {
        this.storeList = storeList;
    }

    public List<SupplyClass> getSupplyList() {
        return supplyList;
    }

    public void setSupplyList(List<SupplyClass> supplyList) {
        this.supplyList = supplyList;
    }

    public int getIncompatibilities() {
        return incompatibilities;
    }

    public void setIncompatibilities(int incompatibilities) {
        this.incompatibilities = incompatibilities;
    }

    public List<int[]> getIncompatiblePairs() {
        return incompatiblePairs;
    }

    public void setIncompatiblePairs(List<int[]> incompatiblePairs) {
        this.incompatiblePairs = incompatiblePairs;
    }
}