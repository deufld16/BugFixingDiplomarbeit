/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simulator.bl;

import general.beans.io_objects.CommandRun;
import general.beans.io_objects.ExplorerLayer;
import general.beans.io_objects.ProjectRun;
import general.beans.io_objects.TestCaseRun;
import general.beans.io_objects.TestGroupRun;
import general.bl.GlobalAccess;
import java.awt.Color;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.swing.DefaultListModel;
import javax.swing.JEditorPane;
import javax.swing.JList;
import org.knowm.xchart.PieChart;
import org.knowm.xchart.PieChartBuilder;
import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.style.Styler;
import simulator.beans.Kasse;
import simulator.beans.Testsystem;
import simulator.gui.SimulatorPanel;

/**
 * Class to manage all simulator executions
 *
 * @author Lukas Krobath
 */
public class ExecutionManager {

    private static ExecutionManager theInstance;
    private List<ExplorerLayer> targets = new ArrayList<>();
    private CommandExecutionManager man;
    private Thread exec;
    private JEditorPane epLog;
    private List<String> messages;
    private boolean lock;
    private SimulatorPanel parent;
    private DefaultListModel<ExplorerLayer> dlm;
    private JList liTestvorgang = null;
    private Map<Integer, PieChart> chartCash = new HashMap();
    private static final Color[] SLICECOLOR = new Color[]{new Color(204, 10, 10), new Color(226, 230, 37), new Color(28, 163, 39), new Color(0, 0, 255)};
    private int cnt = 0;
    private int stepLock;
    private boolean stepLockActivated = false;
    private boolean activeTest;
    private Testsystem activeSystem;

    /**
     * Enum for unifying log levels
     */
    public static enum LOGLEVEL {
        NORMAL(0),
        ERROR(1),
        HEADLINE(2),
        XML(3),
        CHECK_OK(4),
        CHECK_NOK(5),
        SPEZIAL(6),
        CHECK_NEW(7);

        private int number;

        LOGLEVEL(int nr) {
            this.number = nr;
        }

        public int number() {
            return number;
        }
    }

    private ExecutionManager() {
        messages = new ArrayList<>();
    }

    public CommandExecutionManager getMan() {
        return man;
    }

    public static ExecutionManager getInstance() {
        if (theInstance == null) {
            theInstance = new ExecutionManager();
        }
        return theInstance;
    }

    public static Color[] getSLICECOLOR() {
        return SLICECOLOR;
    }

    /**
     * Starts a new CommandExecutionManager
     */
    public void startSimulation() throws NullPointerException {
        man = new CommandExecutionManager(targets, LocalDateTime.now(), activeSystem.getKassen(), cnt * activeSystem.getKassen().size());
        stepLock = 0;
        activeTest = true;
        if (exec == null) {
            exec = new Thread(man);

        }
        if (!exec.isInterrupted()) {
            exec.interrupt();
            exec = new Thread(man);
        }
        exec.setName("Manager");
        exec.start();
        lock = true;
        if (stepLockActivated) {
            parent.getBtNextStep().setEnabled(true);
        }
    }

    public JList getLiTestvorgang() {
        return liTestvorgang;
    }

    public void setLiTestvorgang(JList liTestvorgang) {
        this.liTestvorgang = liTestvorgang;
    }

    /**
     * Creates a new PieChart
     *
     * @param name : Headline for the PieChart
     * @return
     */
    private PieChart addPieChart(Kasse name) {
        PieChart pie = new PieChartBuilder().theme(Styler.ChartTheme.XChart).title(name.getStrIpAdr()).build();
        pie.addSeries("Fehlgeschlagen", 1);
        pie.addSeries("Wird bearbeitet", 1);
        pie.addSeries("Erfolgreich", 1);
        pie.addSeries("Ausstehend", 1);
        pie.getStyler().setSeriesColors(SLICECOLOR);
        pie.getStyler().setLegendVisible(false);
        XChartPanel panel = new XChartPanel(pie);
        parent.getPaEinzel().add(panel);
        return pie;
    }

    public boolean isStepLockActivated() {
        return stepLockActivated;
    }

    public void setStepLockActivated(boolean stepLockActivated) {
        this.stepLockActivated = stepLockActivated;

    }

    public Testsystem getActiveSystem() {
        return activeSystem;
    }

    /**
     * Sets the active cashpoints and adds the pie charts
     *
     * @param activeSystem
     */
    public void setActiveSystem(Testsystem activeSystem) {
        this.activeSystem = activeSystem;
        if (activeSystem != null) {
            System.out.println(activeSystem);
            parent.getPaEinzel().removeAll();
            int cnt = 0;
            for (Kasse kasse : activeSystem.getKassen()) {
                chartCash.put(cnt++, addPieChart(kasse));
            }
        }
    }

    public SimulatorPanel getParent() {
        return parent;
    }

    public void setParent(SimulatorPanel parent) {
        this.parent = parent;
    }

    public DefaultListModel<ExplorerLayer> getDlm() {
        return dlm;
    }

    public void setDlm(DefaultListModel<ExplorerLayer> dlm) {
        this.dlm = dlm;
    }

    public Map<Integer, PieChart> getChartCash() {
        return chartCash;
    }

    public int getStepLock() {
        return stepLock;
    }

    public void setStepLock(int stepLock) {
        this.stepLock = stepLock;
    }

    public void setChartCash(Map<Integer, PieChart> chartCash) {
        this.chartCash = chartCash;
    }

    public JEditorPane getEpLog() {
        return epLog;
    }

    public void setEpLog(JEditorPane epLog) {
        this.epLog = epLog;
    }

    /**
     * Stops a simulation
     */
    public void stopSimulation() {
        exec.interrupt();
        activeTest = false;
        stepLock = -1;
        parent.getBtNextStep().setEnabled(false);
    }

    public List<ExplorerLayer> getTargets() {
        return targets;
    }

    /**
     * Sets the targets, adds them into the overview list and adjusts the charts
     *
     * @param targets : targets for simulation
     */
    public void setTargets(List<ExplorerLayer> targets) {
        cnt = 0;
        this.targets = targets;
        for (ExplorerLayer target : targets) {
            System.out.println(target.getDurchlauf_gegenstand() + " bei Set");
        }
        dlm.removeAllElements();
        for (ExplorerLayer target : targets) {
            if (target instanceof ProjectRun) {
                dlm.addElement(target);
                for (TestGroupRun group : ((ProjectRun) target).getTestgroups()) {
                    dlm.addElement(group);
                    for (TestCaseRun testCase : group.getTestCases()) {
                        dlm.addElement(testCase);
                        for (CommandRun command : testCase.getCommands()) {
                            dlm.addElement(command);
                            cnt++;
                        }
                    }
                }
            } else if (target instanceof TestGroupRun) {
                dlm.addElement(target);
                for (TestCaseRun testCase : ((TestGroupRun) target).getTestCases()) {
                    dlm.addElement(testCase);
                    for (CommandRun command : testCase.getCommands()) {
                        dlm.addElement(command);
                        cnt++;
                    }
                }
            } else if (target instanceof TestCaseRun) {
                dlm.addElement(target);
                for (CommandRun command : ((TestCaseRun) target).getCommands()) {
                    dlm.addElement(command);
                    cnt++;
                }
            } else {
                dlm.addElement(target);
                cnt++;
            }
        }
        updatePieChartsStart();
    }

    /**
     * Calculates the total amount of teststeps
     */
    public void updateCnt() {
        cnt = 0;
        for (ExplorerLayer target : targets) {
            if (target instanceof ProjectRun) {
                for (TestGroupRun group : ((ProjectRun) target).getTestgroups()) {
                    for (TestCaseRun testCase : group.getTestCases()) {
                        for (CommandRun command : testCase.getCommands()) {
                            cnt++;
                        }
                    }
                }
            } else if (target instanceof TestGroupRun) {
                for (TestCaseRun testCase : ((TestGroupRun) target).getTestCases()) {
                    for (CommandRun command : testCase.getCommands()) {
                        cnt++;
                    }
                }
            } else if (target instanceof TestCaseRun) {
                for (CommandRun command : ((TestCaseRun) target).getCommands()) {
                    cnt++;
                }
            } else {
                cnt++;
            }
        }
    }

    /**
     * Sets only the targets
     *
     * @param targets
     */
    public void setTargetsOnly(List<ExplorerLayer> targets) {
        this.targets = new LinkedList<>(targets);
        updatePieChartsStart();
    }

    /**
     * Adds targets
     *
     * @param targets : targets for simulation
     */
    public void addTargets(List<ExplorerLayer> targets) {
        this.targets.addAll(targets);
        for (ExplorerLayer target : targets) {
            System.out.println(target.getDurchlauf_gegenstand() + " bei Set");
        }
        int level = -1;
        for (ExplorerLayer target : targets) {
            if (target instanceof ProjectRun) {
                level = 3;
                dlm.addElement(target);
                for (TestGroupRun group : ((ProjectRun) target).getTestgroups()) {
                    dlm.addElement(group);
                    for (TestCaseRun testCase : group.getTestCases()) {
                        dlm.addElement(testCase);
                        for (CommandRun command : testCase.getCommands()) {
                            dlm.addElement(command);
                            cnt++;
                        }
                    }
                }
            } else if (target instanceof TestGroupRun) {
                if (level != 3) {
                    level = 2;
                }
                dlm.addElement(target);
                for (TestCaseRun testCase : ((TestGroupRun) target).getTestCases()) {
                    dlm.addElement(testCase);
                    for (CommandRun command : testCase.getCommands()) {
                        dlm.addElement(command);
                        cnt++;
                    }
                }
            } else if (target instanceof TestCaseRun) {
                if (level != 3 && level != 2) {
                    level = 1;
                }
                dlm.addElement(target);
                for (CommandRun command : ((TestCaseRun) target).getCommands()) {
                    dlm.addElement(command);
                    cnt++;
                }
            } else {
                if (level == -1) {
                    level = 0;
                }
                dlm.addElement(target);
                cnt++;
            }
        }

        switch (level) {
            case 0:
                GlobalAccess.getInstance().getTest_ide_main_frame().getSim().setLevel(SimulatorActionListener.Level.Command);
                break;
            case 2:
                GlobalAccess.getInstance().getTest_ide_main_frame().getSim().setLevel(SimulatorActionListener.Level.TestGroup);
                break;
            case 1:
                GlobalAccess.getInstance().getTest_ide_main_frame().getSim().setLevel(SimulatorActionListener.Level.TestCase);
                break;
            case 3:
                GlobalAccess.getInstance().getTest_ide_main_frame().getSim().setLevel(SimulatorActionListener.Level.Project);
                break;
        }

        updatePieChartsStart();
    }

    /**
     * Removes targets
     *
     * @param targets : targets to remove from simulation
     */
    public void removeTargets(List<ExplorerLayer> targets) {
        this.targets.removeAll(targets);
        for (ExplorerLayer target : targets) {
            if (target instanceof ProjectRun) {
                dlm.removeElement(target);
                for (TestGroupRun group : ((ProjectRun) target).getTestgroups()) {
                    dlm.removeElement(group);
                    for (TestCaseRun testCase : group.getTestCases()) {
                        dlm.removeElement(testCase);
                        for (CommandRun command : testCase.getCommands()) {
                            dlm.removeElement(command);
                            cnt--;
                        }
                    }
                }
            } else if (target instanceof TestGroupRun) {
                dlm.removeElement(target);
                for (TestCaseRun testCase : ((TestGroupRun) target).getTestCases()) {
                    dlm.removeElement(testCase);
                    for (CommandRun command : testCase.getCommands()) {
                        dlm.removeElement(command);
                        cnt--;
                    }
                }
            } else if (target instanceof TestCaseRun) {
                dlm.removeElement(target);
                for (CommandRun command : ((TestCaseRun) target).getCommands()) {
                    dlm.removeElement(command);
                    cnt--;
                }
            } else {
                dlm.removeElement(target);
                cnt--;
            }
        }
        updatePieChartsStart();
    }

    /**
     * Initializes the pie chart values
     */
    private void updatePieChartsStart() {
        updateCnt();
        parent.getGes().updatePieSeries("Ausstehend", cnt * activeSystem.getKassen().size());
        parent.getGes().updatePieSeries("Fehlgeschlagen", 0);
        parent.getGes().updatePieSeries("Wird bearbeitet", 0);
        parent.getGes().updatePieSeries("Erfolgreich", 0);

        for (int kasse : chartCash.keySet()) {
            chartCash.get(kasse).updatePieSeries("Ausstehend", cnt);
            chartCash.get(kasse).updatePieSeries("Wird bearbeitet", 0);
            chartCash.get(kasse).updatePieSeries("Fehlgeschlagen", 0);
            chartCash.get(kasse).updatePieSeries("Erfolgreich", 0);
        }
    }

    public boolean isLock() {
        return lock;
    }

    /**
     * Sets the lock and updates button values
     *
     * @param lock : status of lock
     */
    public void setLock(boolean lock) {
        this.lock = lock;
        if (!lock) {
            parent.tvFinished();
        }
    }

    /**
     * Threadsafe logging method
     *
     * @param message : String with any content
     * @param log : Enum with level
     */
    public synchronized void log(String message, LOGLEVEL log) {
        //System.out.println("here");
        switch (log) {
            case NORMAL:
                messages.add("<p>" + message + "</p>");
                break;
            case ERROR:
                messages.add("<p><span style='color:red'>" + message + "</span></p>");
                break;
            case HEADLINE:
                messages.add("<h3>" + message + "</h3>");
                break;
            case XML:
                if (!messages.contains(message)) {
                    messages.add("<p><xmp>" + message + "</xmp> <b>wird nun ausgeführt</b></p>");
                }
                break;
            case CHECK_OK:
                messages.add("<p><span style='color:green'>" + message + "</span></p>");
                break;
            case CHECK_NOK:
                messages.add("<p><span style='color:red'>" + message + "</span></p>");
                break;
            case CHECK_NEW:
                messages.add("<p><span style='color:blue'>" + message + "</span></p>");
                break;
            case SPEZIAL:
                messages.add(message);
                break;
        }
        String res = "<html><body>";
        String prev = "";
        for (String mes : messages) {
            if (!mes.startsWith("<p><xmp>")) {
                res = res.concat(mes);
                prev = mes;
            } else if (!mes.equals(prev)) {
                res = res.concat(mes);
                prev = mes;
            }

        }
        res += "</body></html>";
        epLog.setText(res);
        epLog.setCaretPosition(epLog.getDocument().getLength());
    }

}
