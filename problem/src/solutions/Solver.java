package problem.src.solutions;

import problem.src.models.InstanceData;
import problem.src.models.StoreClass;
import problem.src.models.WarehouseClass;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Solver {

    public static void solver(InstanceData instance) {

        List<WarehouseClass> warehouses = instance.getWarehouseList();
        List<StoreClass> stores = instance.getStoreList();
        List<int[]> incompatiblePairs = instance.getIncompatiblePairs();

        int nWarehouses = warehouses.size();
        int nStores = stores.size();

        int[][] solutionMatrix = new int[nStores][nWarehouses];

        int[] remainingCapacity = new int[nWarehouses];
        for (int j = 0; j < nWarehouses; j++) {
            remainingCapacity[j] = warehouses.get(j).getCapacity();
        }

        List<Set<Integer>> warehouseAssignedStores = new ArrayList<>();
        for (int j = 0; j < nWarehouses; j++) {
            warehouseAssignedStores.add(new HashSet<>());
        }


        for (int i = 0; i < nStores; i++) {
            int demand = stores.get(i).getDemand();
            int remainingDemand = demand;

            boolean assignedFully = false;
            for (int j = 0; j < nWarehouses; j++) {
                if (remainingCapacity[j] >= remainingDemand && canAssignStore(i, warehouseAssignedStores.get(j), incompatiblePairs)) {

                    solutionMatrix[i][j] = remainingDemand;
                    remainingCapacity[j] -= remainingDemand;
                    warehouseAssignedStores.get(j).add(i);
                    assignedFully = true;
                    break;
                }
            }
            if (!assignedFully) {
                for (int j = 0; j < nWarehouses && remainingDemand > 0; j++) {
                    if (!canAssignStore(i, warehouseAssignedStores.get(j), incompatiblePairs)) {
                        continue;
                    }
                    if (remainingCapacity[j] > 0) {
                        int assignQuantity = Math.min(remainingCapacity[j], remainingDemand);
                        solutionMatrix[i][j] = assignQuantity;
                        remainingCapacity[j] -= assignQuantity;
                        warehouseAssignedStores.get(j).add(i);
                        remainingDemand -= assignQuantity;
                    }
                }

                if (remainingDemand > 0) {
                    System.out.println("Warning: Could not satisfy demand for store " + (i + 1));
                }
            }
        }

        writeSolution(solutionMatrix, nStores, nWarehouses);
    }

    private static boolean canAssignStore(int storeIndex, Set<Integer> assignedStores, List<int[]> incompatiblePairs) {
        int store1Based = storeIndex + 1;
        for (int assignedStore : assignedStores) {
            int assignedStore1Based = assignedStore + 1;
            for (int[] pair : incompatiblePairs) {

                if ((pair[0] == store1Based && pair[1] == assignedStore1Based) ||
                        (pair[0] == assignedStore1Based && pair[1] == store1Based)) {
                    return false;
                }
            }
        }
        return true;
    }

    private static void writeSolution(int[][] solutionMatrix, int nStores, int nWarehouses) {
        File outputDir = new File("problem/src/outputs");

        File outputFile = new File(outputDir, "solution.txt");

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile))) {
            bw.write("[\n");
            for (int i = 0; i < nStores; i++) {
                bw.write("(");
                for (int j = 0; j < nWarehouses; j++) {
                    bw.write(String.valueOf(solutionMatrix[i][j]));
                    if (j < nWarehouses - 1) {
                        bw.write(",");
                    }
                }
                bw.write(")");
                if (i < nStores - 1) {
                    bw.write("\n");
                }
            }
            bw.write("\n]");
            bw.flush();
            System.out.println("Solution saved to: " + outputFile.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Error writing solution file: " + e.getMessage());
            e.printStackTrace();
        }
    }
}