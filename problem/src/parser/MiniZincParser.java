package problem.src.parser;

import problem.src.models.InstanceData;
import problem.src.solutions.InitialSolution;

import java.io.*;
import java.util.*;
import java.util.regex.*;

public class MiniZincParser {
    public static InstanceData parseFile(String filePath) throws IOException {
        InstanceData instance = new InstanceData();
        List<String> lines = new ArrayList<>();

        // Read the file
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line.trim()); // Trim spaces but preserve "|"
            }
        }

        instance.setWarehouses(extractSingleValue(lines.get(0)));
        instance.setStores(extractSingleValue(lines.get(1)));
        instance.setCapacity(extractList(lines.get(2)));
        instance.setFixedCost(extractList(lines.get(3)));
        instance.setGoods(extractList(lines.get(4)));

        int supplyCostStart = 5;
        int supplyCostEnd = supplyCostStart + instance.getStores();
        instance.setSupplyCost(extractMatrix(lines.subList(supplyCostStart, supplyCostEnd)));

        instance.setIncompatibilities(extractSingleValue(lines.get(supplyCostEnd)));
        instance.setIncompatiblePairs(extractPairs(lines.get(supplyCostEnd + 1)));

        return instance;
    }

    private static int extractSingleValue(String line) {
        Matcher matcher = Pattern.compile("\\d+").matcher(line);
        return matcher.find() ? Integer.parseInt(matcher.group()) : 0;
    }

    private static List<Integer> extractList(String line) {
        List<Integer> values = new ArrayList<>();
        Matcher matcher = Pattern.compile("\\d+").matcher(line);
        while (matcher.find()) {
            values.add(Integer.parseInt(matcher.group()));
        }
        return values;
    }

    private static int[][] extractMatrix(List<String> lines) {
        List<int[]> matrix = new ArrayList<>();
        Pattern pattern = Pattern.compile("\\d+");

        for (String line : lines) {
            List<Integer> row = new ArrayList<>();
            Matcher matcher = pattern.matcher(line);
            while (matcher.find()) {
                row.add(Integer.parseInt(matcher.group()));
            }
            if (!row.isEmpty()) {
                matrix.add(row.stream().mapToInt(i -> i).toArray());
            }
        }
        return matrix.toArray(new int[0][]);
    }

    private static List<int[]> extractPairs(String line) {
        List<int[]> pairs = new ArrayList<>();

        line = line.replace("[|", "").replace("|]", "").trim();

        String[] pairStrings = line.split("\\|");
        for (String pairString : pairStrings) {
            pairString = pairString.trim();
            if (!pairString.isEmpty()) {
                pairString = pairString.replaceAll("[^0-9,]", "").trim();

                String[] numbers = pairString.split(",");
                if (numbers.length == 2) {
                    int first = Integer.parseInt(numbers[0].trim());
                    int second = Integer.parseInt(numbers[1].trim());
                    pairs.add(new int[]{first, second});
                }
            }
        }
        return pairs;
    }

    public static void main(String[] args) {
        try {
            InstanceData instance = parseFile("problem/src/inputs/input1.txt");
            System.out.println("Warehouses: " + instance.getWarehouses());
            System.out.println("Stores: " + instance.getStores());
            System.out.println("Capacity: " + instance.getCapacity());
            System.out.println("Fixed Cost: " + instance.getFixedCost());
            System.out.println("Goods: " + instance.getGoods());
            System.out.println("Supply Cost: " + Arrays.deepToString(instance.getSupplyCost()));
            System.out.println("Incompatibilities: " + instance.getIncompatibilities());
            System.out.print("Incompatible Pairs: ");
            for (int[] pair : instance.getIncompatiblePairs()) {
                System.out.print(Arrays.toString(pair) +", ");
            }

            InitialSolution initialSolution = new InitialSolution();
            initialSolution.generateInitialSolution(instance);

            System.out.println(initialSolution);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}