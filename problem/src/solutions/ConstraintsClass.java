package problem.src.solutions;

import problem.src.models.InstanceData;
import problem.src.models.WarehouseClass;
import java.util.Map;

public class ConstraintsClass {

    public void constraintsCheck(InitialSolution solution, InstanceData problemInstance) {
        solution.violations = 0;

        for (Map.Entry<Integer, Map<Integer, Integer>> entry : solution.assignments.entrySet()) {
            int warehouseId = entry.getKey();
            WarehouseClass warehouse = problemInstance.getWarehouseList().get(warehouseId);

            if (!warehouse.isOpen) {
                solution.violations++;
            }

            int totalAssigned = entry.getValue().values().stream().mapToInt(Integer::intValue).sum();
            if (totalAssigned > warehouse.getCapacity()) {
                solution.violations++;
            }
        }

        // Check incompatibility constraints
        for (int[] pair : problemInstance.getIncompatiblePairs()) {
            int store1 = pair[0] - 1;
            int store2 = pair[1] - 1;
            for (int warehouseId : solution.assignments.keySet()) {
                Map<Integer, Integer> storeAssignments = solution.assignments.get(warehouseId);
                if (storeAssignments.containsKey(store1) && storeAssignments.containsKey(store2)) {
                    solution.violations++;
                }
            }
        }
    }
}