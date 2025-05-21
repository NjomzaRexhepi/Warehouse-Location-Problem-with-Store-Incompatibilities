package problem.src;

import problem.src.models.InstanceData;
import problem.src.parser.MiniZincParser;
import problem.src.solutions.InitialSolution;
import problem.src.solutions.Solver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) throws IOException {
        ArrayList<String> inputs = new ArrayList<>(Arrays.asList(
                "problem/src/inputs/toy.dzn",
                "problem/src/inputs/wlp01.dzn",
                "problem/src/inputs/wlp02.dzn",
                "problem/src/inputs/wlp03.dzn",
                "problem/src/inputs/wlp04.dzn")
        );
        ArrayList<InstanceData> instance = new ArrayList<>();

        for (String input : inputs) {
//            Solver.solver(MiniZincParser.parseFile(input), input.replace("problem/src/inputs/",""));
            Solver.simulatedAnnealing(MiniZincParser.parseFile(input),
                    150_000,   // iterations
                    10_000,    // starting temperature
                    0.995,     // cooling rate
                    input.replace("problem/src/inputs/",""));
        }

//        InitialSolution solution = new InitialSolution();
//        solution.generateInitialSolution(instance);
//        System.out.println(solution);

    }
}