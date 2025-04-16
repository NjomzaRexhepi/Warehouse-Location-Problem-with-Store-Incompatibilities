package problem.src;

import problem.src.models.InstanceData;
import problem.src.parser.MiniZincParser;
import problem.src.solutions.InitialSolution;
import problem.src.solutions.Solver;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        InstanceData instance = MiniZincParser.parseFile("problem/src/inputs/input1.txt");
//        InitialSolution solution = new InitialSolution();
//        solution.generateInitialSolution(instance);
//        System.out.println(solution);
        Solver.solver(instance);
    }
}