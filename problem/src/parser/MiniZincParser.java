package problem.src.parser;

import problem.src.models.*;
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
                lines.add(line.trim());
            }
        }

        // Parse warehouse data (capacity and fixed costs)
        List<Integer> capacities = extractList(lines.get(2));
        List<Integer> fixedCosts = extractList(lines.get(3));
        List<WarehouseClass> warehouses = new ArrayList<>();
        for (int i = 0; i < capacities.size(); i++) {
            warehouses.add(new WarehouseClass(i, capacities.get(i), fixedCosts.get(i)));
        }
        instance.setWarehouseList(warehouses);

        // Parse store data (goods/demand)
        List<Integer> goods = extractList(lines.get(4));
        List<StoreClass> stores = new ArrayList<>();
        for (int i = 0; i < goods.size(); i++) {
            stores.add(new StoreClass(i, goods.get(i)));
        }
        instance.setStoreList(stores);

        // Parse supply costs
        int supplyCostStart = 5;
        int supplyCostEnd = supplyCostStart + stores.size(); // Use store list size instead of stores count
        int[][] supplyCostMatrix = extractMatrix(lines.subList(supplyCostStart, supplyCostEnd));
        List<SupplyClass> supplies = new ArrayList<>();
        for (int i = 0; i < warehouses.size(); i++) {
            for (int j = 0; j < stores.size(); j++) {
                supplies.add(new SupplyClass(i, j, 0, supplyCostMatrix[j][i])); // quantity set to 0 initially
            }
        }
        instance.setSupplyList(supplies);

        // Parse incompatibilities
        int incompatCount = extractSingleValue(lines.get(supplyCostEnd));
        instance.setIncompatibilities(incompatCount);
        List<int[]> incompatiblePairs = extractPairs(lines.get(supplyCostEnd + 1));
        instance.setIncompatiblePairs(incompatiblePairs);

        // Set up incompatible stores
        for (int[] pair : incompatiblePairs) {
            int store1 = pair[0] - 1;
            int store2 = pair[1] - 1;
            if (store1 < 0 || store1 >= stores.size() || store2 < 0 || store2 >= stores.size()) {
                throw new IllegalArgumentException("Incompatible pair index out of bounds: " + Arrays.toString(pair));
            }
            instance.getStoreList().get(store1).addIncompatibleStore(store2);
            instance.getStoreList().get(store2).addIncompatibleStore(store1);
        }

        return instance;
    }

    // Helper methods remain unchanged
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
            System.out.println("Warehouse count: " + instance.getWarehouseList().size());
            System.out.println("Store count: " + instance.getStoreList().size());
            System.out.println("Warehouse Capacities: " +
                    instance.getWarehouseList().stream().map(WarehouseClass::getCapacity).toList());
            System.out.println("Warehouse Fixed Costs: " +
                    instance.getWarehouseList().stream().map(WarehouseClass::getOpeningCost).toList());
            System.out.println("Store Demands: " +
                    instance.getStoreList().stream().map(StoreClass::getDemand).toList());
            System.out.println("Supply Costs: " +
                    instance.getSupplyList().stream().map(SupplyClass::getCost).toList());
            System.out.println("Incompatibilities: " + instance.getIncompatibilities());
            System.out.print("Incompatible Pairs: ");
            for (int[] pair : instance.getIncompatiblePairs()) {
                System.out.print(Arrays.toString(pair) + ", ");
            }
            System.out.println();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}