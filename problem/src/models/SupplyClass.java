package problem.src.models;

public class SupplyClass {
    int warehouseId;
    int storeId;
    int quantity;
    int cost;

    public SupplyClass(int warehouseId, int storeId, int quantity, int cost) {
        this.warehouseId = warehouseId;
        this.storeId = storeId;
        this.quantity = quantity;
        this.cost = cost;
    }

    // Add getters if needed
    public int getWarehouseId() { return warehouseId; }
    public int getStoreId() { return storeId; }
    public int getQuantity() { return quantity; }
    public int getCost() { return cost; }
}