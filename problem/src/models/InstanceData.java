package problem.src.models;

import java.util.List;

public class InstanceData {
    int warehouses;
    int stores;
    List<Integer> capacity;
    List<Integer> fixedCost;
    List<Integer> goods;
    int[][] supplyCost;

    public InstanceData(int warehouses, int stores, List<Integer> capacity, List<Integer> fixedCost, List<Integer> goods, int[][] supplyCost, int incompatibilities, List<int[]> incompatiblePairs) {
        this.warehouses = warehouses;
        this.stores = stores;
        this.capacity = capacity;
        this.fixedCost = fixedCost;
        this.goods = goods;
        this.supplyCost = supplyCost;
        this.incompatibilities = incompatibilities;
        this.incompatiblePairs = incompatiblePairs;
    }

    public InstanceData() {

    }

    public int getWarehouses() {
        return warehouses;
    }

    public void setWarehouses(int warehouses) {
        this.warehouses = warehouses;
    }

    public int getStores() {
        return stores;
    }

    public void setStores(int stores) {
        this.stores = stores;
    }

    public List<Integer> getCapacity() {
        return capacity;
    }

    public void setCapacity(List<Integer> capacity) {
        this.capacity = capacity;
    }

    public List<Integer> getFixedCost() {
        return fixedCost;
    }

    public void setFixedCost(List<Integer> fixedCost) {
        this.fixedCost = fixedCost;
    }

    public List<Integer> getGoods() {
        return goods;
    }

    public void setGoods(List<Integer> goods) {
        this.goods = goods;
    }

    public int[][] getSupplyCost() {
        return supplyCost;
    }

    public void setSupplyCost(int[][] supplyCost) {
        this.supplyCost = supplyCost;
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

    int incompatibilities;
    List<int[]> incompatiblePairs;

}
