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
}

