package problem.src.solutions;

import problem.src.models.InstanceData;
import problem.src.models.SupplyClass;

import java.util.*;

public class InitialSolution {
    public Map<Integer, Map<Integer, Integer>> assignments = new HashMap<>(); // w -> (s -> q)
    private int totalCost;
    int violations;

    public InitialSolution() {
        this.totalCost = 0;
        this.violations = 0;
    }

    public void generateInitialSolution(InstanceData problemInstance) {
        Random random = new Random();
        int[] usedCapacity = new int[problemInstance.getWarehouseList().size()];

        // Iterate over stores
        for (int storeIdx = 0; storeIdx < problemInstance.getStoreList().size(); storeIdx++) {
            int demand = problemInstance.getStoreList().get(storeIdx).getDemand();

            while (demand > 0) {
                int warehouseIdx = random.nextInt(problemInstance.getWarehouseList().size());
                int availableCapacity = problemInstance.getWarehouseList().get(warehouseIdx).getCapacity() - usedCapacity[warehouseIdx];

                if (availableCapacity > 0) {
                    int assignedGoods = Math.min(demand, availableCapacity);

                    // Add assignment: warehouse -> (store -> quantity)
                    assignments.putIfAbsent(warehouseIdx, new HashMap<>());
                    assignments.get(warehouseIdx).put(storeIdx, assignedGoods);

                    // Update used capacity and remaining demand
                    usedCapacity[warehouseIdx] += assignedGoods;
                    demand -= assignedGoods;

                    // Update total cost
                    int finalStoreIdx = storeIdx;
                    int supplyCost = problemInstance.getSupplyList().stream()
                            .filter(s -> s.getWarehouseId() == warehouseIdx && s.getStoreId() == finalStoreIdx)
                            .findFirst()
                            .map(SupplyClass::getCost)
                            .orElse(0);
                    totalCost += supplyCost * assignedGoods;
                } else {
                    violations++; // This only happens if no warehouse has capacity, which shouldn't occur if total capacity >= total demand
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