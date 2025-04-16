package problem.src.solutions;

import problem.src.models.InstanceData;
import problem.src.models.StoreClass;
import problem.src.models.WarehouseClass;

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
               //TODO
            }
            if (!assignedFully) {
                for (int j = 0; j < nWarehouses && remainingDemand > 0; j++) {
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
    }
}