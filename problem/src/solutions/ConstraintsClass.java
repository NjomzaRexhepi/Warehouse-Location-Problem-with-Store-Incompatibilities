package problem.src.solutions;

import problem.src.models.WarehouseClass;

import java.util.Map;

public class ConstraintsClass{

    public void ConstraintsCheck(InitialSolution solution){
        solution.violations = 0;

        for (Map.Entry<Integer, Map<Integer, Integer>> entry : solution.assignments.entrySet()) {
            int warehouseId = entry.getKey();
            WarehouseClass warehouse = solution.warehouses.get(warehouseId - 1);
            if (!warehouse.isOpen) {
                solution.violations++;
            }
        }

        return;
    }

}
