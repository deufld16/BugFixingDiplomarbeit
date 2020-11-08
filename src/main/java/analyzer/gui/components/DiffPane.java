/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analyzer.gui.components;

import analyzer.bl.AnalyzerManager;
import analyzer.bl.HighlightPreparationWorker;
import analyzer.bl.Whitelist;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;

/**
 * This class represents an UI component for the diff-process.
 * It can display relevant text without a line wrap.
 * 
 * @author Maximilian Strohmaier
 */
public class DiffPane extends JTextPane {
    
    private final String DIFF_IDENTIFIER = AnalyzerManager.getInstance().getDIFF_IDENTIFIER();
    private final String DIFF_IDENTIFIER_END = AnalyzerManager.getInstance().getDIFF_IDENTIFIER_END();
    private final Highlighter.HighlightPainter DIFF_HIGHLIGHT_PAINTER = new DiffHighlightPainter(Color.ORANGE);
    
    public DiffPane() {
        super();
        adjustComponent();
    }
    
    @Override
    public boolean getScrollableTracksViewportWidth() {
        return getUI().getPreferredSize(this).width <= getParent().getSize().width;
    }

    @Override
    public Dimension getPreferredSize() {
        return getUI().getPreferredSize(this);
    }
    
    /***
     * Method to customize the component's appearance
     */
    private void adjustComponent()
    {
        setFont(new Font("Courier New", Font.PLAIN, 16));
        setEditable(false);
    }
    
    /***
     * Method to display a list of lines accordingly
     * 
     * @param lines  lines to display
     */
    public void setTextByLines(List<String> lines)
    {
        try {
            //Init Highlight-Preparation
            ExecutorService executor = Executors.newFixedThreadPool(6);
            CompletionService<Object[]> service = new ExecutorCompletionService<>(executor);
            int lineCnt = 0;
            for (String line : lines) {
                service.submit(new HighlightPreparationWorker(lineCnt++, line));
            }
            executor.shutdown();

            //Process Highlight-Preparation-Results
            Map<Integer, String> returnedLines = new HashMap<>();
            for (int i = 0; i < lines.size(); i++) {
                Future<Object[]> future = service.take();

                Object[] result = future.get();
                int lineNr = (int) result[0];
                String newLine = (String) result[1];

                returnedLines.put(lineNr, newLine);
            }
            
            String text = "";        
            for (int i = 0; i < lines.size(); i++) {
                String line = returnedLines.get(i);
                line = Whitelist.removeIgnoreSectionIdentifiers(line);
                text += line + "\n";            
            }
            
            String displayText = text.replaceAll(DIFF_IDENTIFIER, "");
            displayText = displayText.replaceAll(DIFF_IDENTIFIER_END, "");
            setText(displayText);
            removeHighlights();
            highlight(text);
            setCaretPosition(0);
            
        } catch (InterruptedException | ExecutionException ex) {
            ex.printStackTrace();
        }
    }
    
    /***
     * Method to highlight the text according to the diff-results
     * 
     * @param diffText  text including diff identifiers
     */
    private void highlight(String diffText) {
        try {
            Highlighter highlighter = getHighlighter();
            int startPos = diffText.indexOf(DIFF_IDENTIFIER);
            while(startPos > -1) {
                highlighter.addHighlight(
                        startPos, 
                        diffText.indexOf(DIFF_IDENTIFIER_END)-DIFF_IDENTIFIER.length(), 
                        DIFF_HIGHLIGHT_PAINTER);
                diffText = diffText.replaceFirst(DIFF_IDENTIFIER, "");
                diffText = diffText.replaceFirst(DIFF_IDENTIFIER_END, "");
                startPos = diffText.indexOf(DIFF_IDENTIFIER);
            }
        } catch (BadLocationException ex) {
            ex.printStackTrace();
        }
    }
    
    /***
     * Method to remove all highlighted parts from the component
     */
    private void removeHighlights() {
        Highlighter highlighter = getHighlighter();
        Highlighter.Highlight[] highlights = highlighter.getHighlights();
        for (int i = 0; i < highlights.length; i++) {
            if(highlights[i].getPainter() instanceof DiffHighlightPainter) {
                highlighter.removeHighlight(highlights[i]);
            }
        }
    }
    
    /***
     * Highlighter for the diff pane (prepared to be customized if whished)
     * 
     * @author Maximilian Strohmaier
     */
    class DiffHighlightPainter extends DefaultHighlighter.DefaultHighlightPainter {
        
        public DiffHighlightPainter(Color c) {
            super(c);
        }
        
    }
}
