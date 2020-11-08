/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package remote.bl;

import general.beans.io_objects.ExplorerLayer;
import general.beans.io_objects.ProjectRun;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.knowm.xchart.PieChart;
import org.knowm.xchart.PieChartBuilder;
import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.style.Styler;
import remote.ftp.FTPUtil;
import remote.gui.RemotePanel;
import simulator.beans.Kasse;
import simulator.beans.Testsystem;

/**
 *
 * @author Lukas Krobath
 */
public class RemoteExecutionManager {

    private static RemoteExecutionManager theInstance;
    private List<String> possibleTargets;
    private List<ExplorerLayer> targets = new ArrayList<>();
    private Testsystem host;
    private RemotePanel panel;
    private static final Color[] SLICECOLOR = new Color[]{new Color(204, 10, 10), new Color(226, 230, 37), new Color(28, 163, 39), new Color(0, 0, 255)};
    private Map<String, PieChart> chartCash = new HashMap();

    private RemoteExecutionManager() {
    }

    public static RemoteExecutionManager getInstance() {
        if (theInstance == null) {
            theInstance = new RemoteExecutionManager();
        }
        return theInstance;
    }

    public List<String> getPossibleTargets() {
        return possibleTargets;
    }

    public void setPossibleTargets(List<String> possibleTargets) {
        this.possibleTargets = possibleTargets;
    }

    public Map<String, PieChart> getChartCash() {
        return chartCash;
    }

    public void setChartCash(Map<String, PieChart> chartCash) {
        this.chartCash = chartCash;
    }
    

    public List<ExplorerLayer> getTargets() {
        return targets;
    }

    public void setTargets(List<ExplorerLayer> targets) {
        this.targets = targets;
    }

    public RemotePanel getPanel() {
        return panel;
    }

    public void setPanel(RemotePanel panel) {
        this.panel = panel;
    }

    public PieChart addPieChart(Kasse name) {
        PieChart pie = new PieChartBuilder().theme(Styler.ChartTheme.XChart).title(name.getStrIpAdr()).build();
        pie.addSeries("Fehlgeschlagen", 1);
        pie.addSeries("Wird bearbeitet", 1);
        pie.addSeries("Erfolgreich", 1);
        pie.addSeries("Ausstehend", 1);
        //Color[] sliceColor = new Color[]{new Color(255, 0, 0),new Color(0, 255, 0),new Color(0, 0, 255)};
        pie.getStyler().setSeriesColors(SLICECOLOR);
        pie.getStyler().setLegendVisible(false);
        XChartPanel panl = new XChartPanel(pie);
        panel.getPaEinzel().add(panl);
        return pie;
    }
    
    public void cleanUpCharts(){
        panel.getPaEinzel().removeAll();
        chartCash = new HashMap<>();
    }

    public Testsystem getHost() {
        return host;
    }

    public void setHost(Testsystem host) {
        this.host = host;
    }

}
