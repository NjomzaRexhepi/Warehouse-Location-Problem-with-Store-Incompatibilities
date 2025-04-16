package problem.src.models;

import java.util.HashSet;
import java.util.Set;

public class StoreClass {
    int id;
    int demand;
    Set<Integer> incompatibleStores;

    public StoreClass(int id, int demand) {
        this.id = id;
        this.demand = demand;
        this.incompatibleStores = new HashSet<>();
    }

    public void addIncompatibleStore(int storeId) {
        incompatibleStores.add(storeId);
    }

    // Add getters if needed
    public int getId() { return id; }
    public int getDemand() { return demand; }
    public Set<Integer> getIncompatibleStores() { return incompatibleStores; }
}