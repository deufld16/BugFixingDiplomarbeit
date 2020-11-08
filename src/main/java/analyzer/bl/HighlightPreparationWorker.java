/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analyzer.bl;

import java.util.concurrent.Callable;

/**
 * This class is used to reduce the amount of time needed for the highlighting-
 * process by creating sections of differences. Therefore the highlighter 
 * does not have to process each different character by itself, which boosts
 * efficiency.
 * 
 * @author Maximilian Strohmaier
 */
public class HighlightPreparationWorker implements Callable<Object[]>{

    private int lineNr;
    private String line;
    
    private final String DIFF_IDENTIFIER = AnalyzerManager.getInstance().getDIFF_IDENTIFIER();
    private final String DIFF_IDENTIFIER_END = AnalyzerManager.getInstance().getDIFF_IDENTIFIER_END();

    public HighlightPreparationWorker(int lineNr, String line) {
        this.lineNr = lineNr;
        this.line = line;
    }
    
    @Override
    public Object[] call() throws Exception {
        
        StringBuffer buf = new StringBuffer(line);
        int start = buf.indexOf(DIFF_IDENTIFIER);
        
        while(start > -1) {
            //at least 1 diff occurs
            int sectionLength = 1;
            int next = buf.indexOf(DIFF_IDENTIFIER, start+1);
            while(next > -1) {
                //another diff occurs
                if((next - start) == DIFF_IDENTIFIER.length()+sectionLength) {
                    //only 1 character in between
                    buf.replace(next, next+DIFF_IDENTIFIER.length(), "");
                    next = buf.indexOf(DIFF_IDENTIFIER, start+1);
                    sectionLength++;
                }
                else {
                    break;
                }
            }
            try {
                buf.insert(start+DIFF_IDENTIFIER.length()+sectionLength, DIFF_IDENTIFIER_END);
            } catch (IndexOutOfBoundsException ex) {
                buf.append(DIFF_IDENTIFIER_END);
            }
            start = buf.indexOf(DIFF_IDENTIFIER, start+1);
        }

        Object[] result = {lineNr, buf.toString()};
        return result;
    }    
}
