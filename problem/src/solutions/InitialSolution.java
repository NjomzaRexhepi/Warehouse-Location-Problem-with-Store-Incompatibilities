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

    public InitialSolution generateInitialSolution(InstanceData problemInstance) {

        InitialSolution solution = new InitialSolution();
        Random random = new Random();
        return  null;
    }
}
