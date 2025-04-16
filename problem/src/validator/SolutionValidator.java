package problem.src.validator;

import problem.src.models.InstanceData;
import problem.src.models.WarehouseClass;
import problem.src.parser.MiniZincParser;
import problem.src.solutions.InitialSolution;

import java.util.Map;

public class SolutionValidator {

    public boolean isWarehouseCapacityValid(InitialSolution solution, InstanceData problemInstance) {
        // Check each warehouse's assigned capacity against its limit
        for (Map.Entry<Integer, Map<Integer, Integer>> entry : solution.assignments.entrySet()) {
            int warehouseId = entry.getKey();
            WarehouseClass warehouse = problemInstance.getWarehouseList().get(warehouseId);

            // Calculate total assigned goods for this warehouse
            int totalAssigned = entry.getValue().values().stream()
                    .mapToInt(Integer::intValue)
                    .sum();

            // Check if total assigned exceeds warehouse capacity
            if (totalAssigned > warehouse.getCapacity()) {
                System.out.println("Warehouse " + warehouseId + " capacity exceeded: " +
                        totalAssigned + " > " + warehouse.getCapacity());
                return false;
            }
        }

        return true;
    }

    public static void main(String[] args) {
        try {
            // Example usage
            InstanceData instance = MiniZincParser.parseFile("problem/src/inputs/input1.txt");
            InitialSolution solution = new InitialSolution();
            solution.generateInitialSolution(instance);

            SolutionValidator validator = new SolutionValidator();
            boolean isValid = validator.isWarehouseCapacityValid(solution, instance);

            System.out.println(solution);
            System.out.println("Warehouse capacity constraint satisfied: " + isValid);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}