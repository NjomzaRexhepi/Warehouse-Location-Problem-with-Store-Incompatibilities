package problem.src.models;

public class WarehouseClass {
    int id;
    int capacity;
    int openingCost;
    public boolean isOpen;

    public WarehouseClass(int id, int capacity, int openingCost) {
        this.id = id;
        this.capacity = capacity;
        this.openingCost = openingCost;
        this.isOpen = false;
    }

    // Add getters if needed
    public int getId() { return id; }
    public int getCapacity() { return capacity; }
    public int getOpeningCost() { return openingCost; }
}