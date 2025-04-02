package problem.src.solutions;

import problem.src.models.InstanceData;
import problem.src.models.StoreClass;
import problem.src.models.WarehouseClass;

import java.util.*;

public class InitialSolution {
    Map<Integer, Map<Integer, Integer>> assignments = new HashMap<>(); // w -> (s -> q)
    int totalCost;
    int violations;

    public InitialSolution() {
        this.totalCost = 0;
        this.violations = 0;
    }

    List<WarehouseClass> warehouses = new ArrayList<>();
    List<StoreClass> stores = new ArrayList<>();
    int[][] supplyCost;
    List<int[]> incompatiblePairs = new ArrayList<>();

    public void generateInitialSolution(InstanceData problemInstance) {
        Random random = new Random();
        int[] usedCapacity = new int[problemInstance.getWarehouses()];

        for (int store = 0; store < problemInstance.getStores(); store++) {
            int demand = problemInstance.getGoods().get(store);

            while (demand > 0) {
                int warehouse = random.nextInt(problemInstance.getWarehouses());
                int availableCapacity = problemInstance.getCapacity().get(warehouse) - usedCapacity[warehouse];

                if (availableCapacity > 0) {
                    int assignedGoods = Math.min(demand, availableCapacity);

                    assignments.putIfAbsent(warehouse, new HashMap<>());
                    assignments.get(warehouse).put(store, assignedGoods);

                    usedCapacity[warehouse] += assignedGoods;
                    demand -= assignedGoods;
                } else {
                    violations++;
                }
            }
        }
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Initial Solution:\n");
        sb.append("Total Cost: ").append(totalCost).append("\n");
        sb.append("Violations: ").append(violations).append("\n");
        sb.append("Assignments:\n");

        for (Map.Entry<Integer, Map<Integer, Integer>> warehouseEntry : assignments.entrySet()) {
            int warehouse = warehouseEntry.getKey();
            sb.append("  Warehouse ").append(warehouse).append(" supplies:\n");

            for (Map.Entry<Integer, Integer> storeEntry : warehouseEntry.getValue().entrySet()) {
                int store = storeEntry.getKey();
                int quantity = storeEntry.getValue();
                sb.append("    Store ").append(store).append(" -> Quantity: ").append(quantity).append("\n");
            }
        }

        return sb.toString();
    }
}
