package problem.src.models;

public class WarehouseClass {
    int id;
    int capacity;
    int openingCost;
    boolean isOpen;

    public WarehouseClass(int id, int capacity, int openingCost) {
        this.id = id;
        this.capacity = capacity;
        this.openingCost = openingCost;
        this.isOpen = false;
    }
}
