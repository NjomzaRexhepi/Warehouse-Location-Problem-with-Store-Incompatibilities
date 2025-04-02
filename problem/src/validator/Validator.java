package problem.src.validator;

import problem.src.models.InstanceData;
import problem.src.models.StoreClass;
import problem.src.models.WarehouseClass;
import problem.src.solutions.InitialSolution;

import java.util.Map;

public class Validator {

    // First version ( first constraint -> warehouse capacity)
    public static void validateSolution(InstanceData instanceData, InitialSolution solution) {
        int supplyCost = 0;
        int openingCost = 0;
        int violations = 0;

        // Validate assignments
        for (Map.Entry<Integer, Map<Integer, Integer>> warehouseEntry : solution.assignments.entrySet()) {
            int warehouseId = warehouseEntry.getKey();
            WarehouseClass warehouse = instanceData.getWarehouses().get(warehouseId);
            int warehouseCapacityUsed = 0;

            for (Map.Entry<Integer, Integer> storeEntry : warehouseEntry.getValue().entrySet()) {
                int storeId = storeEntry.getKey();
                int quantity = storeEntry.getValue();
                StoreClass store = instanceData.getStores().get(storeId);

                // Check if the store and warehouse are compatible
                if (isIncompatible(warehouseId, storeId, instanceData.getIncompatiblePairs())) {
                    violations++;
                    System.out.println("Incompatible pair: Warehouse " + warehouseId + " and Store " + storeId);
                }

                // Calculate the supply cost
                int cost = instanceData.getSupplyCost()[warehouseId][storeId];
                supplyCost += quantity * cost;

                // Track the total used capacity for the warehouse
                warehouseCapacityUsed += quantity;

                // Check if warehouse capacity is exceeded
                if (warehouseCapacityUsed > warehouse.getCapacity()) {
                    violations++;
                    System.out.println("Capacity violation at Warehouse " + warehouseId);
                }
            }

            // Add opening cost for warehouse
            openingCost += warehouse.getOpeningCost();
        }

        // Total cost is the sum of supply cost and opening cost
        solution.totalCost = supplyCost + openingCost;
        solution.violations = violations;

        // Print out the results
        System.out.println("Total Supply Cost: " + supplyCost);
        System.out.println("Total Opening Cost: " + openingCost);
        System.out.println("Total Violations: " + violations);
        System.out.println("Total Cost: " + solution.totalCost);
    }

    private static boolean isIncompatible(int warehouseId, int storeId, List<int[]> incompatiblePairs) {
        for (int[] pair : incompatiblePairs) {
            if ((pair[0] == warehouseId && pair[1] == storeId) || (pair[0] == storeId && pair[1] == warehouseId)) {
                return true;
            }
        }
        return false;
    }
}
