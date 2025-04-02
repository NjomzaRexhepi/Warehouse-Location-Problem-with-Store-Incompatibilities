package problem.src.solutions;

import problem.src.models.InstanceData;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class InitialSolution {
    Map<Integer, Map<Integer, Integer>> assignments = new HashMap<>(); // w -> (s -> q)
    int totalCost;
    int violations;

    public InitialSolution() {
        this.totalCost = 0;
        this.violations = 0;
    }

    public InitialSolution generateInitialSolution(InstanceData problemInstance) {

        InitialSolution solution = new InitialSolution();
        Random random = new Random();
        return  null;

    }

}
