package problem.src.parser;

import java.io.*;
import java.util.*;
import java.util.regex.*;
class ProblemInstance {
    int warehouses;
    int stores;
    List<Integer> capacity;
    List<Integer> fixedCost;
    List<Integer> goods;
    int[][] supplyCost;
    int incompatibilities;
    List<int[]> incompatiblePairs;
}

public class MiniZincParser {
    public static ProblemInstance parseFile(String filePath) throws IOException {
        ProblemInstance instance = new ProblemInstance();
        List<String> lines = new ArrayList<>();

        // Read the file
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line.trim()); // Trim spaces but preserve "|"
            }
        }

        // Parse each line
        instance.warehouses = extractSingleValue(lines.get(0));
        instance.stores = extractSingleValue(lines.get(1));
        instance.capacity = extractList(lines.get(2));
        instance.fixedCost = extractList(lines.get(3));
        instance.goods = extractList(lines.get(4));

        int supplyCostStart = 5;
        int supplyCostEnd = supplyCostStart + instance.stores; // Store count determines matrix size
        instance.supplyCost = extractMatrix(lines.subList(supplyCostStart, supplyCostEnd));

        instance.incompatibilities = extractSingleValue(lines.get(supplyCostEnd));
        instance.incompatiblePairs = extractPairs(lines.get(supplyCostEnd + 1));

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
            ProblemInstance instance = parseFile("problem/src/inputs/input1.txt");
            System.out.println("Warehouses: " + instance.warehouses);
            System.out.println("Stores: " + instance.stores);
            System.out.println("Capacity: " + instance.capacity);
            System.out.println("Fixed Cost: " + instance.fixedCost);
            System.out.println("Goods: " + instance.goods);
            System.out.println("Supply Cost: " + Arrays.deepToString(instance.supplyCost));
            System.out.println("Incompatibilities: " + instance.incompatibilities);
            System.out.print("Incompatible Pairs: ");
            for (int[] pair : instance.incompatiblePairs) {
                System.out.print(Arrays.toString(pair) +", ");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}