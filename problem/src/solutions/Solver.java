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
import java.util.stream.IntStream;

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
//        swapStores(solutionMatrix,instance); TO BE UPDATED
        Random rnd = new Random();
        int a = rnd.nextInt(nStores);
        int b;
        do { b = rnd.nextInt(nStores); } while (b == a);

// attempt the operator
//        swapStores(a, b, solutionMatrix, instance, nStores, nWarehouses, fileName);
       // bestSwapStores(solutionMatrix, instance);
        bestMoveGoods(solutionMatrix, instance);
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

    private static void writeSolution(int[][] solutionMatrix,
                                      int nStores,
                                      int nWarehouses,
                                      String fileName) {

        File outputDir = new File("problem/src/outputs");
        if (!outputDir.exists() && !outputDir.mkdirs()) {
            System.err.println("Failed to create directory: " + outputDir.getAbsolutePath());
            return;
        }

        File outputFile = new File(outputDir, fileName + ".txt");

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile))) {

            bw.write("{");

            boolean first = true;
            for (int s = 0; s < nStores; s++) {
                for (int w = 0; w < nWarehouses; w++) {
                    int q = solutionMatrix[s][w];
                    if (q <= 0) continue;                // skip zero quantities

                    if (!first) bw.write(", ");
                    bw.write("(" + (s + 1) + "," + (w + 1) + "," + q + ")");
                    first = false;
                }
            }

            bw.write("}");
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


    public static int printScore(InstanceData instance, int[][] solutionMatrix) {
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
//        System.out.println("Total Supply Cost: " + totalSupplyCost);
//        System.out.println("Total Opening Cost: " + totalFixedCost);
        System.out.println("Total Score (Cost): " + totalCost);

        return totalCost;
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

        int storeADemand = Arrays.stream(storeAAssignments).sum();
        int storeBDemand = Arrays.stream(storeBAssignments).sum();

        int storeAAvailableCapacity = 0;
        for (int j = 0; j < nWarehouses; j++) {
            if (storeAAssignments[j] > 0) {
                storeAAvailableCapacity += warehouses.get(j).getCapacity();
            }
        }

        int storeBAvailableCapacity = 0;
        for (int j = 0; j < nWarehouses; j++) {
            if (storeBAssignments[j] > 0) {
                storeBAvailableCapacity += warehouses.get(j).getCapacity();
            }
        }
        return storeBDemand <= storeAAvailableCapacity && storeADemand <= storeBAvailableCapacity;

    }

    public static boolean swapStores(int storeA,
                                     int storeB,
                                     int[][] solutionMatrix,
                                     InstanceData instance,
                                     int nStores,
                                     int nWarehouses,
                                     String fileName) {

        List<int[]> incompatiblePairs = instance.getIncompatiblePairs();
        List<WarehouseClass> warehouses = instance.getWarehouseList();

        int[] storeAAssignments = solutionMatrix[storeA];
        int[] storeBAssignments = solutionMatrix[storeB];

        // 1. feasibility check
        if (!canSwapStores(storeA, storeB,
                storeAAssignments, storeBAssignments,
                incompatiblePairs, warehouses)) {
            System.out.printf("Swap of store %d and %d infeasible%n",
                    storeA + 1, storeB + 1);
            return false;
        }
        // 3. perform the swap (deep copy row-wise)
        for (int w = 0; w < warehouses.size(); w++) {
            int tmp = solutionMatrix[storeA][w];
            solutionMatrix[storeA][w] = solutionMatrix[storeB][w];
            solutionMatrix[storeB][w] = tmp;
        }

        // 4. compute score AFTER swap
        System.out.println("--- After swap  ---");
        printScore(instance, solutionMatrix);

        writeSolution(solutionMatrix, nStores, nWarehouses, fileName);

        return true;
    }


    public static boolean bestMoveGoods(int[][] sol, InstanceData ins) {
        int nStores = sol.length;
        int nWarehouses = ins.getWarehouseList().size();
        int[][] costM = buildCostMatrix(ins, nStores, nWarehouses);
        int baseCost = printScore(ins, sol);
        int bestDelta = 0;
        int bestStore = -1, bestFromWH = -1, bestToWH = -1, bestQuantity = 0;

        for (int i = 0; i < nStores; i++) {
            for (int fromWH = 0; fromWH < nWarehouses; fromWH++) {
                if (sol[i][fromWH] <= 0) continue;

                for (int toWH = 0; toWH < nWarehouses; toWH++) {
                    if (fromWH == toWH) continue;

                    // Calculate maximum possible quantity to move
                    int maxMove = sol[i][fromWH];
                    int currentUsage = 0;
                    for (int s = 0; s < nStores; s++) {
                        currentUsage += sol[s][toWH];
                    }
                    int availableCapacity = ins.getWarehouseList().get(toWH).getCapacity() - currentUsage;
                    int quantity = Math.min(maxMove, availableCapacity);
                    if (quantity <= 0) continue;

                    // Check incompatibilities
                    Set<Integer> storesInToWH = new HashSet<>();
                    for (int s = 0; s < nStores; s++) {
                        if (sol[s][toWH] > 0) {
                            storesInToWH.add(s);
                        }
                    }
                    boolean compatible = true;
                    int store1Based = i + 1;
                    for (int otherStore : storesInToWH) {
                        if (otherStore == i) continue;
                        int otherStore1Based = otherStore + 1;
                        for (int[] pair : ins.getIncompatiblePairs()) {
                            if ((pair[0] == store1Based && pair[1] == otherStore1Based) ||
                                    (pair[0] == otherStore1Based && pair[1] == store1Based)) {
                                compatible = false;
                                break;
                            }
                        }
                        if (!compatible) break;
                    }
                    if (!compatible) continue;

                    // Calculate cost delta
                    int delta = quantity * (costM[i][toWH] - costM[i][fromWH]);
                    if (delta < bestDelta) {
                        bestDelta = delta;
                        bestStore = i;
                        bestFromWH = fromWH;
                        bestToWH = toWH;
                        bestQuantity = quantity;
                    }
                }
            }
        }

        if (bestStore == -1) {
            System.out.println("No improving move exists.");
            return false;
        }

        // Apply the best move
        sol[bestStore][bestFromWH] -= bestQuantity;
        sol[bestStore][bestToWH] += bestQuantity;

        System.out.printf("Move %d units of store %d from WH %d to WH %d improved cost by %d → new score %d%n",
                bestQuantity, bestStore + 1, bestFromWH + 1, bestToWH + 1, bestDelta, baseCost + bestDelta);
        return true;
    }

    private static int getSupplyCost(int storeIdx,
                                     int whIdx,
                                     int[][] costMatrix) {
        return costMatrix[storeIdx][whIdx];   // matrix already 0-based
    }

    public static boolean bestSwapStores(int[][] sol, InstanceData ins) {

        int nStores     = sol.length;
        int nWarehouses = ins.getWarehouseList().size();
        List<int[]> incomp = ins.getIncompatiblePairs();

        // Build cost matrix once (reuses your existing code)
        int[][] costM = buildCostMatrix(ins, nStores, nWarehouses);

        int baseCost  = printScore(ins, sol);
        int bestDelta = 0, bestA = -1, bestB = -1;

        for (int i = 0; i < nStores; i++) {
            for (int j = i + 1; j < nStores; j++) {

                if (!canSwapStores(i, j, sol[i], sol[j], incomp,
                        ins.getWarehouseList())) continue;

                int delta = 0;
                for (int w = 0; w < nWarehouses; w++) {
                    int qi  = sol[i][w];
                    int qj  = sol[j][w];

                    int cIw = getSupplyCost(i, w, costM);
                    int cJw = getSupplyCost(j, w, costM);

                    delta += (qj - qi) * cIw + (qi - qj) * cJw;
                }

                if (delta < bestDelta) {          // improvement
                    bestDelta = delta;
                    bestA = i; bestB = j;
                }
            }
        }

        if (bestA == -1) {
            System.out.println("No improving swap exists.");
            return false;
        }

        // Apply swap
        for (int w = 0; w < nWarehouses; w++) {
            int tmp = sol[bestA][w];
            sol[bestA][w] = sol[bestB][w];
            sol[bestB][w] = tmp;
        }

        System.out.printf("Swap %d ↔︎ %d improved cost by %d → new score %d%n",
                bestA + 1, bestB + 1, bestDelta, baseCost + bestDelta);
        return true;
    }



    public static int[][] simulatedAnnealing(InstanceData instance,
                                             int    maxIter,
                                             double t0,
                                             double alpha,
                                             String fileName) {

        int nW  = instance.getWarehouseList().size();
        int nS  = instance.getStoreList().size();
        int[][] curr = buildInitialSolution(instance, nS, nW);
        int currCost  = printScore(instance, curr);

        int[][] best  = deepCopy(curr);
        int bestCost  = currCost;

        Random rng = new Random(2025);
        double T = t0;

        for (int k = 0; k < maxIter && T > 1e-3; k++) {

            int[][] neigh  = deepCopy(curr);
            generateNeighbour(neigh, instance, rng);
            int neighCost  = printScore(instance, neigh);
            int  delta     = neighCost - currCost;

            if (delta < 0 || Math.exp(-delta / T) > rng.nextDouble()) {
                curr = neigh;
                currCost = neighCost;
                if (currCost < bestCost) {
                    best     = deepCopy(curr);
                    bestCost = currCost;
                }
            }
            T *= alpha;
        }

        writeSolution(best, nS, nW, fileName);
        System.out.printf("SA finished: best cost = %d%n", bestCost);
        return best;
    }


    private static int[][] buildInitialSolution(InstanceData instance, int nStores, int nWarehouses) {

        int[][] sol = new int[nStores][nWarehouses];
        int[]   remCap = new int[nWarehouses];
        for (int j = 0; j < nWarehouses; j++)
            remCap[j] = instance.getWarehouseList().get(j).getCapacity();

        List<Set<Integer>> whStores = new ArrayList<>();
        for (int j = 0; j < nWarehouses; j++) whStores.add(new HashSet<>());


        int[][] costM = buildCostMatrix(instance, nStores, nWarehouses);

        Integer[] storeIdx = IntStream.range(0, nStores).boxed().toArray(Integer[]::new);

        Arrays.sort(storeIdx, (a, b) ->
                Integer.compare(
                        instance.getStoreList().get(b).getDemand(),
                        instance.getStoreList().get(a).getDemand()));

        for (int i : storeIdx) {
            int demand = instance.getStoreList().get(i).getDemand();
            int rest   = demand;

            Integer[] whIdx = IntStream.range(0, nWarehouses).boxed().toArray(Integer[]::new);
            Arrays.sort(whIdx, Comparator.comparingInt(j -> costM[i][j]));

            for (int j : whIdx) {
                if (!canAssignStore(i, whStores.get(j), instance.getIncompatiblePairs())) continue;
                int q = Math.min(remCap[j], rest);
                if (q == 0) continue;
                sol[i][j] += q;
                remCap[j] -= q;
                rest      -= q;
                whStores.get(j).add(i);
                if (rest == 0) break;
            }
            if (rest > 0)
                System.err.printf("Initial heuristic warning – unsatisfied demand for store %d%n", i + 1);
        }
        return sol;
    }


    private static void generateNeighbour(int[][] sol, InstanceData ins, Random rng) {

        if (rng.nextBoolean()) {
            int s = rng.nextInt(sol.length);
            int fromWH = rng.nextInt(sol[0].length);
            if (sol[s][fromWH] == 0) return;

            int toWH = rng.nextInt(sol[0].length);
            if (toWH == fromWH) return;

            int qty = 1 + rng.nextInt(sol[s][fromWH]);
            if (!isMoveFeasible(s, fromWH, toWH, qty, sol, ins)) return;

            sol[s][fromWH] -= qty;
            sol[s][toWH]   += qty;

        } else {
            int a = rng.nextInt(sol.length);
            int b;
            do { b = rng.nextInt(sol.length); } while (b == a);

            if (!canSwapStores(a, b, sol[a], sol[b],
                    ins.getIncompatiblePairs(), ins.getWarehouseList()))
                return;

            for (int w = 0; w < sol[0].length; w++) {
                int tmp = sol[a][w]; sol[a][w] = sol[b][w]; sol[b][w] = tmp;
            }
        }
    }

    private static boolean isMoveFeasible(int storeIdx, int fromWH, int toWH, int qty,
                                          int[][] sol, InstanceData ins) {


        int used = 0;
        for (int s = 0; s < sol.length; s++) used += sol[s][toWH];
        int cap  = ins.getWarehouseList().get(toWH).getCapacity();
        if (used + qty > cap) return false;


        Set<Integer> inToWH = new HashSet<>();
        for (int s = 0; s < sol.length; s++) if (sol[s][toWH] > 0) inToWH.add(s);
        int s1 = storeIdx + 1;
        for (int other : inToWH) {
            int o1 = other + 1;
            for (int[] p : ins.getIncompatiblePairs())
                if ((p[0]==s1 && p[1]==o1) || (p[1]==s1 && p[0]==o1))
                    return false;
        }
        return true;
    }


    private static int[][] deepCopy(int[][] m) {
        int[][] c = new int[m.length][];
        for (int i = 0; i < m.length; i++) c[i] = m[i].clone();
        return c;
    }




}