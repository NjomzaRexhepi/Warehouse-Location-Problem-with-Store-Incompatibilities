package problem.src.solutions;

import problem.src.models.InstanceData;
import problem.src.models.StoreClass;
import problem.src.models.WarehouseClass;
import problem.src.models.SupplyClass;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Solver {

    public static void solver(InstanceData instance, String fileName) {

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

        writeSolution(solutionMatrix, nStores, nWarehouses, fileName);
        printScore(instance, solutionMatrix);

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

    private static void writeSolution(int[][] solutionMatrix, int nStores, int nWarehouses, String fileName) {
        File outputDir = new File("problem/src/outputs");

// Create the directory if it does not exist
        if (!outputDir.exists()) {
            boolean dirsCreated = outputDir.mkdirs();
            if (!dirsCreated) {
                System.err.println("Failed to create directory: " + outputDir.getAbsolutePath());
                return;
            }
        }

// Use fileName as the name of the output file (with .txt extension)
        File outputFile = new File(outputDir, fileName + ".txt");

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

    private static int[][] buildCostMatrix(InstanceData instance, int nStores, int nWarehouses) {
        int[][] costMatrix = new int[nStores][nWarehouses];
        // Initialize costMatrix to zeros (default)

        for (SupplyClass sc : instance.getSupplyList()) {
            int storeId = sc.getStoreId();       // expected to be 1-based
            int warehouseId = sc.getWarehouseId(); // expected to be 1-based
            int cost = sc.getCost();

            int storeIndex = storeId - 1;
            int warehouseIndex = warehouseId - 1;

            // Check bounds. If either index is out of bounds or negative, print a warning and skip.
            if (storeIndex < 0 || storeIndex >= nStores) {
//                System.err.println("Warning: storeId " + storeId + " produces index " + storeIndex +
//                        ", which is out of bounds (nStores=" + nStores + "). Skipping this record.");
                continue;
            }
            if (warehouseIndex < 0 || warehouseIndex >= nWarehouses) {
//                System.err.println("Warning: warehouseId " + warehouseId + " produces index " + warehouseIndex +
//                        ", which is out of bounds (nWarehouses=" + nWarehouses + "). Skipping this record.");
                continue;
            }
            costMatrix[storeIndex][warehouseIndex] = cost;
        }
        return costMatrix;
    }


    public static void printScore(InstanceData instance, int[][] solutionMatrix) {
        int totalSupplyCost = 0;
        int totalFixedCost = 0;

        List<WarehouseClass> warehouses = instance.getWarehouseList();
        int nStores = solutionMatrix.length;
        int nWarehouses = warehouses.size();

        // Build the cost matrix from the supply list.
        int[][] costMatrix = buildCostMatrix(instance, nStores, nWarehouses);

        // Calculate supply cost.
        for (int i = 0; i < nStores; i++) {
            for (int j = 0; j < nWarehouses; j++) {
                int quantity = solutionMatrix[i][j];
                totalSupplyCost += quantity * costMatrix[i][j];
            }
        }

        // Calculate fixed (opening) cost for warehouses that supply at least one store.
        for (int j = 0; j < nWarehouses; j++) {
            boolean isOpen = false;
            for (int i = 0; i < nStores; i++) {
                if (solutionMatrix[i][j] > 0) {
                    isOpen = true;
                    break;
                }
            }
            if (isOpen) {
                totalFixedCost += warehouses.get(j).getOpeningCost();
            }
        }

        int totalCost = totalSupplyCost + totalFixedCost;
        System.out.println("Total Supply Cost: " + totalSupplyCost);
        System.out.println("Total Opening Cost: " + totalFixedCost);
        System.out.println("Total Score (Cost): " + totalCost);
    }

    public static int[][] swapStores(int storeA, int storeB, int[][] solutionMatrix, InstanceData instance) {
        List<WarehouseClass> warehouses = instance.getWarehouseList();
        List<int[]> incompatiblePairs = instance.getIncompatiblePairs();
        int nWarehouses = warehouses.size();

        // Create a copy of the solution matrix to modify
        int[][] newSolution = new int[solutionMatrix.length][];
        for (int i = 0; i < solutionMatrix.length; i++) {
            newSolution[i] = solutionMatrix[i].clone();
        }

        // Get current assignments for both stores
        int[] storeAAssignments = newSolution[storeA];
        int[] storeBAssignments = newSolution[storeB];

        // Check if stores are compatible to be swapped
        if (!canSwapStores(storeA, storeB, storeAAssignments, storeBAssignments, incompatiblePairs, warehouses)) {
            System.out.println("Stores cannot be swapped due to incompatibility or capacity constraints");
            return solutionMatrix; // return original solution
        }

        // Calculate remaining capacities
        int[] remainingCapacity = new int[nWarehouses];
        for (int j = 0; j < nWarehouses; j++) {
            int usedCapacity = 0;
            for (int i = 0; i < newSolution.length; i++) {
                if (i != storeA && i != storeB) {
                    usedCapacity += newSolution[i][j];
                }
            }
            remainingCapacity[j] = warehouses.get(j).getCapacity() - usedCapacity;
        }

        // Calculate total demand for each store
        int storeADemand = Arrays.stream(storeAAssignments).sum();
        int storeBDemand = Arrays.stream(storeBAssignments).sum();

        // Clear current assignments (we'll rebuild them)
        Arrays.fill(newSolution[storeA], 0);
        Arrays.fill(newSolution[storeB], 0);

        // Try to assign storeB to storeA's original warehouses
        int remainingStoreBDemand = storeBDemand;
        for (int j = 0; j < nWarehouses && remainingStoreBDemand > 0; j++) {
            if (storeAAssignments[j] > 0) {
                int assignAmount = Math.min(remainingStoreBDemand, remainingCapacity[j]);
                if (assignAmount > 0) {
                    newSolution[storeB][j] = assignAmount;
                    remainingStoreBDemand -= assignAmount;
                    remainingCapacity[j] -= assignAmount;
                }
            }
        }

        // Try to assign storeA to storeB's original warehouses
        int remainingStoreADemand = storeADemand;
        for (int j = 0; j < nWarehouses && remainingStoreADemand > 0; j++) {
            if (storeBAssignments[j] > 0) {
                int assignAmount = Math.min(remainingStoreADemand, remainingCapacity[j]);
                if (assignAmount > 0) {
                    newSolution[storeA][j] = assignAmount;
                    remainingStoreADemand -= assignAmount;
                    remainingCapacity[j] -= assignAmount;
                }
            }
        }

        // If we couldn't fully assign, try other warehouses
        for (int j = 0; j < nWarehouses && (remainingStoreADemand > 0 || remainingStoreBDemand > 0); j++) {
            if (remainingStoreADemand > 0 && remainingCapacity[j] > 0) {
                int assignAmount = Math.min(remainingStoreADemand, remainingCapacity[j]);
                newSolution[storeA][j] += assignAmount;
                remainingStoreADemand -= assignAmount;
                remainingCapacity[j] -= assignAmount;
            }
            if (remainingStoreBDemand > 0 && remainingCapacity[j] > 0) {
                int assignAmount = Math.min(remainingStoreBDemand, remainingCapacity[j]);
                newSolution[storeB][j] += assignAmount;
                remainingStoreBDemand -= assignAmount;
                remainingCapacity[j] -= assignAmount;
            }
        }

        if (remainingStoreADemand > 0 || remainingStoreBDemand > 0) {
            System.out.println("Warning: Could not fully satisfy demand after swap");
            System.out.println("Remaining demand for storeA: " + remainingStoreADemand);
            System.out.println("Remaining demand for storeB: " + remainingStoreBDemand);
        }

        return newSolution;
    }

    private static boolean canSwapStores(int storeA, int storeB, int[] storeAAssignments, int[] storeBAssignments,
                                         List<int[]> incompatiblePairs, List<WarehouseClass> warehouses) {
        int storeA1Based = storeA + 1;
        int storeB1Based = storeB + 1;
        for (int[] pair : incompatiblePairs) {
            if ((pair[0] == storeA1Based && pair[1] == storeB1Based) ||
                    (pair[0] == storeB1Based && pair[1] == storeA1Based)) {
                return false;
            }
        }

        int nWarehouses = warehouses.size();
        int[] remainingCapacity = new int[nWarehouses];
        for (int j = 0; j < nWarehouses; j++) {
            remainingCapacity[j] = warehouses.get(j).getCapacity();
        }




    }

}