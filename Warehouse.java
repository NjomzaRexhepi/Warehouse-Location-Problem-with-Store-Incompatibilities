public class Warehouse {
    int id;
    int capacity;
    int openingCost;
    boolean isOpen;

    public Warehouse(int id, int capacity, int openingCost) {
        this.id = id;
        this.capacity = capacity;
        this.openingCost = openingCost;
        this.isOpen = false;
    }
}
