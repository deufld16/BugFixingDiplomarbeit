/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analyzer.bl;

import analyzer.beans.WhitelistEntry;
import analyzer.enums.ResultFileType;
import general.bl.GlobalParamter;
import general.io.Loader;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class is used to manage a whitelist so that 
 * some items of a response are not diffed with the corresponding reference
 * 
 * @author Maximilian Strohmaier
 */
public class Whitelist {
    
    private static final String SECTION_START;
    private static final String SECTION_END;
    private static String sectionStartDiffed = "";
    private static String sectionEndDiffed = "";
    private static final String DIFF_IDENTIFIER;
    private static final String DIFF_IDENTIFIER_END;
    
    static {
        SECTION_START = AnalyzerManager.getInstance().getWHITELIST_SECTION_IDENTIFIER_START();
        SECTION_END = AnalyzerManager.getInstance().getWHITELIST_SECTION_IDENTIFIER_END();
        DIFF_IDENTIFIER = AnalyzerManager.getInstance().getDIFF_IDENTIFIER();
        DIFF_IDENTIFIER_END = AnalyzerManager.getInstance().getDIFF_IDENTIFIER_END();
        
        sectionStartDiffed = DIFF_IDENTIFIER + SECTION_START + DIFF_IDENTIFIER_END;
        sectionEndDiffed = DIFF_IDENTIFIER + SECTION_END + DIFF_IDENTIFIER_END;
        
        if(!Loader.isWhitelistLoaded()) {
            Loader.loadWhitelistXml();
        }
    }
    
    /***
     * This method transforms list of lines into a list of lines 
     * where specific parts will be ignored during the diffing-process
     * 
     * @param lines  original lines
     * @param type  type of the Result-File from which the lines are originating
     * @return  transformed lines
     */
    public static List<String> applyWhitelist(List<String> lines, ResultFileType type) {
        List<String> filteredLines = new LinkedList<>(lines);
        
        for (int i = 0; i < filteredLines.size(); i++) {
            List<WhitelistEntry> entries = GlobalParamter.getInstance().getWhitelistEntries();
            for (WhitelistEntry entry : entries) {
                if(entry.getApplicationTypes().contains(type)) {
                    filteredLines.set(i, filter(filteredLines.get(i), entry.getRegex()));
                }
            }
            
        }
        
        return filteredLines;
    }
    
    /***
     * This method removes all sections that should be ignored for several lines
     * 
     * @param lines  lines with sections
     * @return  lines without sections
     */
    public static List<String> removeIgnoreSections(List<String> lines) {
        List<String> newLines = new LinkedList<>();
        for (String line : lines) {
            newLines.add(removeIgnoreSectionsInline(line));
        }
        return newLines;
    }

    /***
     * This method removes all sections that should be ignored for one lines
     * 
     * @param line  line with sections
     * @return  line without sections
     */
    public static String removeIgnoreSectionsInline(String line) {
        int start = line.indexOf(SECTION_START);
        StringBuffer buf = new StringBuffer(line);
        while (start != -1) {
            int end = buf.indexOf(SECTION_END) + SECTION_END.length();
            String placeholder = "";
            for (int i = start+SECTION_START.length(); i < (end - SECTION_END.length()); i++) {
                placeholder += " ";
            }
            buf = buf.replace(start, end, placeholder);
            start = buf.indexOf(SECTION_START);
        }
        return buf.toString();
    }
    
    /**
     * This method removes all Whitelist Identifiers and unnecessary Diff Identifiers
     * from the given line, so that it only includes valid text
     * 
     * @param line  line with identifiers
     * @return  line without identifiers
     */
    public static String removeIgnoreSectionIdentifiers(String line) {
        //Firstly check with identifiers that have been marked as a difference itself
        if(line.contains(sectionStartDiffed) && line.contains(sectionEndDiffed)) {
            line = removeIgnoreSectionIdentifierHelper(line, sectionStartDiffed, sectionEndDiffed);
        }
        
        //Secondly check with normal section identifiers
        if(line.contains(SECTION_START) && line.contains(SECTION_END)) {
            line = removeIgnoreSectionIdentifierHelper(line, SECTION_START, SECTION_END);
        }
        
        return line;
    }
    
    /**
     * Helper-Method that executes the removal of the ignore section identifiers
     * 
     * @param line  line that contains the section identifiers
     * @param sectionIdStart  identifier that marks the start of a section
     * @param sectionIdEnd  identifier that marks the end of a section
     * @return  line without section identifiers
     */
    private static String removeIgnoreSectionIdentifierHelper(String line, 
        String sectionIdStart, String sectionIdEnd) {
        int start = line.indexOf(sectionIdStart);
        StringBuffer buf = new StringBuffer(line);
        while (start != -1) {
            int end = buf.indexOf(sectionIdEnd) + sectionIdEnd.length();
            String ignoreSection = buf.substring(start, end);
            ignoreSection = ignoreSection.replaceAll(DIFF_IDENTIFIER, "");
            ignoreSection = ignoreSection.replaceAll(DIFF_IDENTIFIER_END, "");
            ignoreSection = ignoreSection.replace(sectionIdStart, "");
            ignoreSection = ignoreSection.replace(sectionIdEnd, "");
            buf = buf.replace(start, end, ignoreSection);
            start = buf.indexOf(sectionIdStart);
        }
        return buf.toString();
    }
    
    /***
     * This method is used to go through a line and 
     * label specific sections accordingly, 
     * so that they will be ignored in the further process
     * 
     * @param line  line to check
     * @param regex  regular expression that represents the rule for the sections that should be ignored
     * @return  lines with labeled sections to ignore
     */
    private static String filter(String line, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(line);

        while(matcher.find()) {
            StringBuffer buf = new StringBuffer(line);
            buf = buf.insert(matcher.start(), SECTION_START);
            buf = buf.insert(matcher.end()+SECTION_START.length(), SECTION_END);
            line = buf.toString();
        }
        
        return line;
    }
    
}
