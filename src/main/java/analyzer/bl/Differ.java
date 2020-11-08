/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analyzer.bl;

import general.bl.GlobalParamter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.text.diff.CommandVisitor;
import org.apache.commons.text.diff.StringsComparator;

/**
 * Class that executes the diffing-process
 * Includes the application of apache.commons.text.diff
 * 
 * @author Maximilian Strohmaier
 */
public class Differ {
    private final String DIFF_IDENTIFIER = AnalyzerManager.getInstance().getDIFF_IDENTIFIER();
    private double similarityRate = 0.9;
    private boolean restricted;
    
    private int anzInserts;
    private int anzDeletes;
    private int anzKeeps;
    private int bestAnzKeeps;
    private Map<String, List<String>> bestResult;
    
    /**
     * Method to update the result of the diff process to the optimal result
     * when performing an automatic optimization of the diff-parameters
     * 
     * @param newResult  result with a specific set of diff-parameters
     */
    private void updateData(Map<String, List<String>> newResult) {
        if(anzKeeps > bestAnzKeeps || bestResult.isEmpty()) {
            bestAnzKeeps = anzKeeps;
            bestResult = newResult;
        }
        anzKeeps = 0;
    }
    
    /***
     * Method to run an entire diff-process between two lists of lines 
     * (with both automatic or static parameters, as set by the user)
     * 
     * @param ref  all lines from the reference file
     * @param erg  all lines from the results file
     * @param similarityRate  similarity rate as defined in the settings
     * @return  a map with a new list of lines for both, reference and results file
     */
    public Map<String, List<String>> work(List<String> ref, List<String> erg, double similarityRate)
    {        
        anzInserts = 0;
        anzDeletes = 0;
        anzKeeps = 0;
        bestAnzKeeps = 0;
        bestResult = new HashMap<>();

        if (GlobalParamter.getInstance().isOptimizeDiffer()) {
            try {
                ExecutorService executor = Executors.newFixedThreadPool(6);
                CompletionService<Object[]> service = new ExecutorCompletionService<>(executor);
                for (double i = 0; i <= 1; i+=0.1) {
                    service.submit(new DiffOptimizationWorker(ref, erg, i));
                }
                executor.shutdown();

                for (double i = 0; i <= 1; i+=0.1) {
                    Future<Object[]> future = service.take();
                    Object[] returnedResults = future.get();
                    
                    Map<String, List<String>> diffResult = (Map<String, List<String>>) returnedResults[0];
                    anzKeeps = (int) returnedResults[1];
                    boolean noDifferences = (boolean) returnedResults[2];

                    updateData(diffResult);
                    if(noDifferences) {
                        break;
                    }
                }
            } catch (InterruptedException | ExecutionException ex) {
                ex.printStackTrace();
            }
            return bestResult;
        }
        
        return diff(ref, erg, similarityRate, 
                GlobalParamter.getInstance().getErgRefWeighting(),
                GlobalParamter.getInstance().isDiffAlignmentRestriction());
        
    }
    
    /***
     * Method to run a characterwise differenciation between two lists of lines 
     * 
     * @param ref  all lines from the reference file
     * @param erg  all lines from the results file
     * @param similarityRate  similarity rate that should be used for this diffing process
     * @param ergRefWeighting  defines which site should be used firstly for checking for further occurences
     * @param restricted  defines whether the alignment-restriction is enabled or not
     * 
     * @return  a map with a new list of lines for both, reference and results file
     */
    public Map<String, List<String>> diff(List<String> ref, List<String> erg, double similarityRate,
            String ergRefWeighting, boolean restricted)
    {  
        this.similarityRate = similarityRate;
        Map<String, List<String>> diffResults = new HashMap<>();
        
        List<String> tmpRef = new LinkedList<>(ref);
        List<String> tmpErg = new LinkedList<>(erg);
        
        List<String> refDiffed = new LinkedList<>();
        List<String> ergDiffed = new LinkedList<>();
        
        this.restricted = restricted;
        
        for (int i = 0; i < Integer.max(tmpRef.size(), tmpErg.size()); i++) {
            /***
            * read the lines of both files (if one has more than the other
            * fill up with "") 
            ***/
            String refLine = "";
            String ergLine = "";
            try {
                refLine= tmpRef.get(i);
            } catch(IndexOutOfBoundsException ex) {
                
            }
            try {
                ergLine = tmpErg.get(i);
            } catch(IndexOutOfBoundsException ex) {
                
            }
            
            String refLineWhitelisted = Whitelist.removeIgnoreSectionsInline(refLine);
            String ergLineWhitelisted = Whitelist.removeIgnoreSectionsInline(ergLine);
            
            ResultDiffVisitor visitor = new ResultDiffVisitor();

            StringsComparator compHelp = new StringsComparator(refLineWhitelisted, ergLineWhitelisted);
            
            if(refLineWhitelisted.equals("")
                    || ergLineWhitelisted.equals("")
                    || (compHelp.getScript().getLCSLength() 
                         >= (Integer.max(refLineWhitelisted.length(), ergLineWhitelisted.length()) 
                                * similarityRate))) {
                //lines are similar resp. should be compared because other line is empty
                StringsComparator comp = new StringsComparator(refLine, ergLine);
                comp.getScript().visit(visitor);
                refDiffed.add(visitor.ref);
                ergDiffed.add(visitor.erg);
            } else {
                //lines are not similar
                
                int lineDifference;
                
                if(ergRefWeighting.equals("erg")) {
                    lineDifference = searchLine(tmpErg.subList(i, tmpErg.size()), 
                            tmpRef.subList(i, tmpRef.size()));
                    if(lineDifference > -1) {
                        //erg-line found again in ref-lines
                        while(lineDifference > 0) {
                            try {
                                tmpErg.add(i, "");
                            } catch (IndexOutOfBoundsException ex) {
                                tmpErg.add("");
                            }
                            lineDifference--;
                        }
                        i--;
                        continue;
                    }
                }
                
                lineDifference = searchLine(tmpRef.subList(i, tmpRef.size()), 
                        tmpErg.subList(i, tmpErg.size()));
                if(lineDifference > -1) {
                    //ref-line found again in erg-lines
                    while(lineDifference > 0) {
                        try {
                            tmpRef.add(i, "");
                        } catch (IndexOutOfBoundsException ex) {
                            tmpRef.add("");
                        }
                        lineDifference--;
                    }
                    i--;
                    continue;
                }
                
                
                if(ergRefWeighting.equals("ref")) {
                    lineDifference = searchLine(tmpErg.subList(i, tmpErg.size()), 
                            tmpRef.subList(i, tmpRef.size()));
                    if(lineDifference > -1) {
                        //erg-line found again in ref-lines
                        while(lineDifference > 0) {
                            try {
                                tmpErg.add(i, "");
                            } catch (IndexOutOfBoundsException ex) {
                                tmpErg.add("");
                            }
                            lineDifference--;
                        }
                        i--;
                        continue;
                    }
                }
                
                StringsComparator compRef = new StringsComparator(refLine, "");
                compRef.getScript().visit(visitor);
                refDiffed.add(visitor.ref);
                ergDiffed.add(visitor.erg);
                
                visitor = new ResultDiffVisitor();
                StringsComparator compErg = new StringsComparator("", ergLine);
                compErg.getScript().visit(visitor);
                refDiffed.add(visitor.ref);
                ergDiffed.add(visitor.erg);
            }

        }
        
        diffResults.put("ref", refDiffed);
        diffResults.put("erg", ergDiffed);
        return diffResults;
    }
    
    /***
     * Method to search through the remaining lines of an erg- or ref-file, 
     * checking if a specific line occurs again (not a fully equally, but rather a
     * similar line, as defined by the similarity rate)
     * 
     * @param target  
     * @param linesToSearchThrough  
     * @return  amount of lines that would have to be inserted to get to 
     *          the wanted line, -1 if no other occurence was found
     */
    private int searchLine(List<String> originLines, 
            List<String> targetLines) {
        int cnt = 0;
        String origin = originLines.get(0);
        origin = Whitelist.removeIgnoreSectionsInline(origin);
        for (String target : targetLines) {
            target = Whitelist.removeIgnoreSectionsInline(target);
            StringsComparator helpComp = new StringsComparator(origin, target);
            if(restricted) {
                try {
                    if(originLines.get(cnt).equals(target)) {
                        return -1;
                    }
                } catch (IndexOutOfBoundsException ex) {

                }
            }
            if((helpComp.getScript().getLCSLength() 
                    >= (Integer.max(origin.length(), target.length()) * similarityRate))) {
                //target found
                return cnt;
            }
            cnt++;
        }
        return -1;
    }

    public int getAnzKeeps() {
        return anzKeeps;
    }

    public void setAnzKeeps(int anzKeeps) {
        this.anzKeeps = anzKeeps;
    }

    public int getAnzInserts() {
        return anzInserts;
    }

    public void setAnzInserts(int anzInserts) {
        this.anzInserts = anzInserts;
    }

    public int getAnzDeletes() {
        return anzDeletes;
    }

    public void setAnzDeletes(int anzDeletes) {
        this.anzDeletes = anzDeletes;
    }
    
    /***
     * Class that handles the results of the diffing-process 
     * 
     * @author Maximilian Strohmaier
     */
    private class ResultDiffVisitor implements CommandVisitor<Character> {

        String ref = "";
        String erg = "";
        
        @Override
        public void visitInsertCommand(Character t) {
            //Character is only present in erg file
            erg += DIFF_IDENTIFIER + t;
            anzInserts++;
        }

        @Override
        public void visitKeepCommand(Character t) {
            //Character is present in ref file and erg file
            ref += t;
            erg += t;
            anzKeeps++;
        }

        @Override
        public void visitDeleteCommand(Character t) {
            //Character is only present in ref file
            ref += DIFF_IDENTIFIER + t;
            anzDeletes++;
        }
        
    }
}
