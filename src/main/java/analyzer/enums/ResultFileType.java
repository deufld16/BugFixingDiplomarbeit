/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analyzer.enums;

import analyzer.bl.AnalyzerManager;
import analyzer.io.ResultsIO;
import general.bl.GlobalParamter;
import java.util.Properties;

/**
 * Enum to store the differnet result types (file types)
 * 
 * @author Maximilian Strohmaier
 */
public enum ResultFileType {
    
    DISPLAY("display"),
    DRAWER("drawer"),
    PRINTER("printer"),
    GSERVER("gserver");
    
    private String description;
    private double similarityRate;
    
    private ResultFileType(String description) {
        this.description = description;
        
        Properties simRates = AnalyzerManager.getInstance().getStored_similarity_rates();
        if(simRates == null) {
            AnalyzerManager.getInstance().setStored_similarity_rates(
                    ResultsIO.loadProperties(GlobalParamter.getInstance().getSIMILARITY_RATES_FILE()));
            simRates = AnalyzerManager.getInstance().getStored_similarity_rates();
        }
        String property = simRates.getProperty(description);
        if(property == null) {
            similarityRate = 0.;
        } else {
            similarityRate = Double.parseDouble(property);
        }
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getSimilarityRate() {
        return similarityRate;
    }

    public void setSimilarityRate(double similarityRate) {
        this.similarityRate = similarityRate;
    }

    @Override
    public String toString() {
        return description;
    }
}
