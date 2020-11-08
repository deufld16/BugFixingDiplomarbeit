/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analyzer.bl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * Class to optimize the diff result by selecting the best parameters automatically
 * 
 * @author Maximilian Strohmaier
 */
public class DiffOptimizationWorker implements Callable<Object[]> {

    private List<String> ref;
    private List<String> erg;
    private double similarityRate;
    private int currentAnzKeeps = 0;
    private Differ differ;
    private Map<String, List<String>> bestResult;
    private boolean noDifferences = false;

    public DiffOptimizationWorker(List<String> ref, List<String> erg, double similarityRate) {
        this.ref = ref;
        this.erg = erg;
        this.similarityRate = similarityRate;
        differ = new Differ();
        bestResult = new HashMap<>();
    }
    
    /**
     * Method to update the optimal result to the newest version
     * 
     * @param newResult  new result which should be checked whether it is better
     *  than an old one or not
     */
    private void updateData(Map<String, List<String>> newResult) {
        int newAnzKeeps = differ.getAnzKeeps();
        if(newAnzKeeps > currentAnzKeeps || bestResult.isEmpty()) {
            currentAnzKeeps = newAnzKeeps;
            bestResult = newResult;
            if(differ.getAnzInserts() == 0 && differ.getAnzDeletes() == 0) {
                noDifferences = true;
                return;
            }
        }
        differ.setAnzKeeps(0);
        differ.setAnzInserts(0);
        differ.setAnzDeletes(0);
    }
    
    /**
     * Method that performs a diff process with each possible constellation of
     * alignment weighting and restriction for a specific similarity rate
     * 
     * @return  optimal diff result
     * 
     * @throws Exception 
     */
    @Override
    public Object[] call() throws Exception {
        
        Map<String, List<String>> result1 = differ.diff(ref, erg, similarityRate, "erg", true);
        updateData(result1);
        
        if(!noDifferences) {
            Map<String, List<String>> result2 = differ.diff(ref, erg, similarityRate, "ref", true);
            updateData(result2);

            if(!noDifferences) {
                Map<String, List<String>> result3 = differ.diff(ref, erg, similarityRate, "erg", false);
                updateData(result3);

                if(!noDifferences) {
                    Map<String, List<String>> result4 = differ.diff(ref, erg, similarityRate, "ref", false);
                    updateData(result4);
                }
            }
        }
        
        Object[] resultArray = new Object[3];
        resultArray[0] = bestResult;
        resultArray[1] = currentAnzKeeps;
        resultArray[2] = noDifferences;
        
        return resultArray;
    }
    
}
