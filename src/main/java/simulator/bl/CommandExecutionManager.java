/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simulator.bl;

import dashboard.beans.Durchlauf;
import dashboard.beans.Durchlaufgegenstand;
import dashboard.bl.DatabaseGlobalAccess;
import dashboard.database.DB_Access_Manager;
import general.beans.io_objects.CommandRun;
import general.beans.io_objects.ExplorerLayer;
import general.beans.io_objects.ProjectRun;
import general.beans.io_objects.TestCaseRun;
import general.beans.io_objects.TestGroupRun;
import general.bl.GlobalAccess;
import general.bl.GlobalParamter;
import general.io.Mapper;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import simulator.beans.Kasse;
import simulator.commands.impl.CheckRepInZAMTlogCommand;
import simulator.commands.impl.ChkSyncCommandV2;
import simulator.commands.impl.CopyCommand;
import simulator.commands.impl.CtrlCommandV2;
import simulator.commands.impl.DbimportCommand;
import simulator.commands.impl.EljoCommandV2;
import simulator.commands.impl.ExcecuteRecorderFileCommand;
import simulator.commands.impl.ExportTableContentCommand;
import simulator.commands.impl.FTPCommand;
import simulator.commands.impl.SqlQueryCommand;
import simulator.commands.impl.WaitCommand;
import simulator.commands.impl.CallGlobalCommand;
import simulator.commands.impl.ShellCommandLX;
import simulator.interfaces.BackofficeAccess;

/**
 * Manages the execution of a test
 *
 * @author Lukas Krobath
 */
public class CommandExecutionManager implements Runnable {

    private ExecutorService executor;
    private CompletionService<Map<Integer, List<Boolean>>> service;
    private List<ExplorerLayer> targets = new ArrayList<>();
    private int cashpoints;
    private LocalDateTime timestamp;
    private List<Kasse> kassen;
    private int cntT = 0;
    private int cntF = 0;
    private int totalAmount;
    private Map<Integer, Integer> cashMis;
    private Map<Integer, Integer> cashErf;
    private Map<Integer, Integer> cashFail;
    private List<String> bediener;
    private List<String> bedienerPass;
    private List<Integer> laden;
    private LocalDateTime start;

    private static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd_HHmm");

    private Map<Integer, Integer> cashWorking;

    public CommandExecutionManager(List<ExplorerLayer> targets, LocalDateTime timestamp, List<Kasse> kassen, int cnt) {
        this.timestamp = timestamp;
        this.targets = targets;
        this.kassen = kassen;
        this.cashpoints = kassen.size();
        this.totalAmount = cnt;
        this.cashMis = new HashMap<>();
        this.cashErf = new HashMap<>();
        this.cashFail = new HashMap<>();
        this.cashWorking = new HashMap<>();
        bediener = new ArrayList<>();
        bedienerPass = new ArrayList<>();
        laden = new ArrayList<>();
        for (int i = 0; i < kassen.size(); i++) {
            cashMis.put(i, cnt / kassen.size());
            cashErf.put(i, 0);
            cashFail.put(i, 0);
            cashWorking.put(i, 0);
        }
    }

    public List<ExplorerLayer> getTargets() {
        return targets;
    }

    public void setTargets(List<ExplorerLayer> targets) {
        this.targets = targets;
    }

    private List<ExplorerLayer> getAllTargetItems() {
        List<ExplorerLayer> runs = new ArrayList<>();
        for (ExplorerLayer target : targets) {
            if (target instanceof ProjectRun) {
                runs.add(target);
                for (TestGroupRun group : ((ProjectRun) target).getTestgroups()) {
                    runs.add(group);
                    for (TestCaseRun testCase : group.getTestCases()) {
                        runs.add(testCase);
                        runs.addAll(testCase.getCommands());
                    }

                }
            } else if (target instanceof TestGroupRun) {
                runs.add(target);
                for (TestCaseRun testCase : ((TestGroupRun) target).getTestCases()) {
                    runs.add(testCase);
                    runs.addAll(testCase.getCommands());
                }
            } else if (target instanceof TestCaseRun) {
                runs.add(target);
                runs.addAll(((TestCaseRun) target).getCommands());
            } else {
                runs.add(target);
            }
        }
        return runs;
    }

    private List<Durchlaufgegenstand> getAllNewTargetItems() {
        List<Durchlaufgegenstand> runs = new ArrayList<>();
        for (ExplorerLayer target : targets) {
            if (target instanceof ProjectRun) {
                runs.add(target.getDurchlauf_gegenstand());
                for (TestGroupRun group : ((ProjectRun) target).getTestgroups()) {
                    runs.add(group.getDurchlauf_gegenstand());
                    for (TestCaseRun testCase : group.getTestCases()) {
                        runs.add(testCase.getDurchlauf_gegenstand());
                        for (CommandRun command : testCase.getCommands()) {
                            runs.add(command.getDurchlauf_gegenstand());
                        }
                    }

                }
            } else if (target instanceof TestGroupRun) {
                runs.add(target.getDurchlauf_gegenstand());
                for (TestCaseRun testCase : ((TestGroupRun) target).getTestCases()) {
                    runs.add(testCase.getDurchlauf_gegenstand());
                    for (CommandRun command : testCase.getCommands()) {
                        runs.add(command.getDurchlauf_gegenstand());
                    }
                }
            } else if (target instanceof TestCaseRun) {
                runs.add(target.getDurchlauf_gegenstand());
                for (CommandRun command : ((TestCaseRun) target).getCommands()) {
                    runs.add(command.getDurchlauf_gegenstand());
                }
            } else {
                runs.add(target.getDurchlauf_gegenstand());
            }
        }
        return runs;
    }

    /**
     * Converts all ExplorerkLayer objects to a two dimensional list
     *
     * Every single list can be executed threadsafe
     *
     * @return
     */
    private List<List<CommandRun>> convertTargetsToCommands() {
        List<CommandRun> runs = new ArrayList<>();
        for (ExplorerLayer target : targets) {
            if (target instanceof ProjectRun) {
                for (TestGroupRun group : ((ProjectRun) target).getTestgroups()) {
                    for (TestCaseRun testCase : group.getTestCases()) {
                        runs.addAll(testCase.getCommands());
                    }

                }
            } else if (target instanceof TestGroupRun) {
                for (TestCaseRun testCase : ((TestGroupRun) target).getTestCases()) {
                    runs.addAll(testCase.getCommands());
                }
            } else if (target instanceof TestCaseRun) {
                runs.addAll(((TestCaseRun) target).getCommands());
            } else {
                runs.add((CommandRun) target);

            }
        }
        for (CommandRun run : runs) {
            run.getPath().getParent().getParent().getParent().resolve("erg").resolve(dtf.format(timestamp)).resolve(run.getPath().getParent().toFile().getName()).resolve(run.getPath().toFile().getName()).toFile().mkdirs();
            Properties prop = new Properties();
            int nrCas = 1;
            for (Kasse kassa : ExecutionManager.getInstance().getActiveSystem().getKassen()) {
                prop.setProperty("kasse" + nrCas++, kassa.getType().toString().toLowerCase());
            }
            try {
                prop.store(new FileOutputStream(run.getPath().getParent().getParent().getParent().resolve("erg").resolve(dtf.format(timestamp)).resolve(run.getPath().getParent().toFile().getName()).resolve(run.getPath().toFile().getName()).resolve("erg_ref_mapping.properties").toFile()), null);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        List<List<CommandRun>> result = new ArrayList<>();
        List<CommandRun> executable = new ArrayList<>();
        Path temp = Paths.get("");
        for (CommandRun commandRun : runs) {
            switch (commandRun.getClassName()) {
                case "CheckRepInZAMTlogCommand":
                    if (CheckRepInZAMTlogCommand.class.isAnnotationPresent(BackofficeAccess.class) || !commandRun.getPath().equals(temp)) {
                        result.add(executable);
                        executable = new ArrayList<>();
                        temp = commandRun.getPath();
                    }
                    executable.add(commandRun);
                    break;
                case "ChkSyncCommandV2":
                    if (ChkSyncCommandV2.class.isAnnotationPresent(BackofficeAccess.class) || !commandRun.getPath().equals(temp)) {
                        result.add(executable);
                        executable = new ArrayList<>();
                        temp = commandRun.getPath();
                    }
                    executable.add(commandRun);
                    break;
                case "CopyCommand":
                    if (CopyCommand.class.isAnnotationPresent(BackofficeAccess.class) || !commandRun.getPath().equals(temp)) {
                        result.add(executable);
                        executable = new ArrayList<>();
                        temp = commandRun.getPath();
                    }
                    executable.add(commandRun);
                    break;
                case "ExportTableContentCommand":
                    if (ExportTableContentCommand.class.isAnnotationPresent(BackofficeAccess.class) || !commandRun.getPath().equals(temp)) {
                        result.add(executable);
                        executable = new ArrayList<>();
                        temp = commandRun.getPath();
                    }
                    executable.add(commandRun);
                    break;
                case "WaitCommand":
                    if (WaitCommand.class.isAnnotationPresent(BackofficeAccess.class) || !commandRun.getPath().equals(temp)) {
                        result.add(executable);
                        executable = new ArrayList<>();
                        temp = commandRun.getPath();
                    }
                    executable.add(commandRun);
                    break;
                case "EljoCommandV2":
                    if (EljoCommandV2.class.isAnnotationPresent(BackofficeAccess.class) || !commandRun.getPath().equals(temp)) {
                        result.add(executable);
                        executable = new ArrayList<>();
                        temp = commandRun.getPath();
                    }
                    executable.add(commandRun);
                    break;
                case "SqlQueryCommand":
                    if (SqlQueryCommand.class.isAnnotationPresent(BackofficeAccess.class) || !commandRun.getPath().equals(temp)) {
                        result.add(executable);
                        executable = new ArrayList<>();
                        temp = commandRun.getPath();
                    }
                    executable.add(commandRun);
                    break;
                case "FTPCommand":
                    if (FTPCommand.class.isAnnotationPresent(BackofficeAccess.class) || !commandRun.getPath().equals(temp)) {
                        result.add(executable);
                        executable = new ArrayList<>();
                        temp = commandRun.getPath();
                    }
                    executable.add(commandRun);
                    break;
                case "CtrlCommandV2":
                    if (CtrlCommandV2.class.isAnnotationPresent(BackofficeAccess.class) || !commandRun.getPath().equals(temp)) {
                        result.add(executable);
                        executable = new ArrayList<>();
                        temp = commandRun.getPath();
                    }
                    executable.add(commandRun);
                    break;
                case "DbimportCommand":
                    if (DbimportCommand.class.isAnnotationPresent(BackofficeAccess.class) || !commandRun.getPath().equals(temp)) {
                        result.add(executable);
                        executable = new ArrayList<>();
                        temp = commandRun.getPath();
                    }
                    executable.add(commandRun);
                    break;
                case "ExecuteRecorderFileCommand":
                    if (ExcecuteRecorderFileCommand.class.isAnnotationPresent(BackofficeAccess.class) || !commandRun.getPath().equals(temp)) {
                        result.add(executable);
                        executable = new ArrayList<>();
                        temp = commandRun.getPath();
                    }
                    executable.add(commandRun);
                    break;
                case "CallGlobalCommand":
                    if (CallGlobalCommand.class.isAnnotationPresent(BackofficeAccess.class) || !commandRun.getPath().equals(temp)) {
                        result.add(executable);
                        executable = new ArrayList<>();
                        temp = commandRun.getPath();
                    }
                    executable.add(commandRun);
                    break;
                case "ShellCommand":
                    if (ShellCommandLX.class.isAnnotationPresent(BackofficeAccess.class) || !commandRun.getPath().equals(temp)) {
                        result.add(executable);
                        executable = new ArrayList<>();
                        temp = commandRun.getPath();
                    }
                    executable.add(commandRun);
                    break;
                default:
                    executable.add(commandRun);
            }
        }
        result.add(executable);
        for (List<CommandRun> list : result) {
            if (list.size() > 0) {
                bediener.add(getParent(list.get(0)).getEmpId() + "");
                bedienerPass.add(getParent(list.get(0)).getPassword() + "");
                laden.add(getParent(list.get(0)).getTllId());
                System.out.println(getParent(list.get(0)).getTllId());
            }
        }
        return result;
    }

    /**
     * Updates the overview chart
     *
     * @param total : amount of commands to be executed
     * @param working : amout of currently active teststeps
     * @param fail : amount of failed commands
     * @param success : amount of executed and successful commands
     */
    public synchronized void updateCharts(int total, int working, int fail, int success) {
        ExecutionManager.getInstance().getParent().getGes().updatePieSeries("Wird bearbeitet", working);
        ExecutionManager.getInstance().getParent().getGes().updatePieSeries("Ausstehend", total);
        ExecutionManager.getInstance().getParent().getGes().updatePieSeries("Fehlgeschlagen", fail);
        ExecutionManager.getInstance().getParent().getGes().updatePieSeries("Erfolgreich", success);
        ExecutionManager.getInstance().getParent().updateUI();
    }

    /**
     * Updates all side charts
     *
     * @param kassanr : cashpoint which has to be updated
     * @param total : amount of commands to be executed
     * @param working : amout of currently active teststeps
     * @param fail : amount of failed commands
     * @param success : amount of executed and successful commands
     */
    public synchronized void updateSubChart(int kassanr, int total, int working, int fail, int success) {
        ExecutionManager.getInstance().getChartCash().get(kassanr).updatePieSeries("Ausstehend", total);
        ExecutionManager.getInstance().getChartCash().get(kassanr).updatePieSeries("Fehlgeschlagen", fail);
        ExecutionManager.getInstance().getChartCash().get(kassanr).updatePieSeries("Erfolgreich", success);
        ExecutionManager.getInstance().getChartCash().get(kassanr).updatePieSeries("Wird bearbeitet", working);

    }

    /**
     * Updates all charts and calculates relevant values for the corresponding
     * methods
     */
    public synchronized void updatePieCharts() {
        int total = 0;
        int working = 0;
        int fail = 0;
        int success = 0;
        try {
            for (int i = 0; i < kassen.size(); i++) {
                int submissing = cashMis.get(i);
                int subworking = cashWorking.get(i);
                int subfail = cashFail.get(i);
                int subsuccess = cashErf.get(i);

                if (subworking == 0 && subfail == 0 && subsuccess == 0 && submissing == 0) {
                    updateSubChart(i, 1, 0, 0, 0);

                } else {
                    updateSubChart(i, submissing, subworking, subfail, subsuccess);
                }
                total += submissing;
                working += subworking;
                fail += subfail;
                success += subsuccess;

            }
        } catch (Exception n) {
            //n.printStackTrace();
        }
        try {
            if (working == 0 && fail == 0 && success == 0 && total == 0) {
                updateCharts(1, 0, 0, 0);
            }
            updateCharts(total, working, fail, success);
        } catch (Exception n) {
            //n.printStackTrace();
        }
    }

    /**
     * Returns the corresponding testgroup of a command
     *
     * @param command : target
     * @return
     */
    private TestGroupRun getParent(CommandRun command) {
        for (ProjectRun workingProject : GlobalParamter.getInstance().getWorkingProjects()) {
            for (TestGroupRun tg : workingProject.getTestgroups()) {
                for (TestCaseRun tc : tg.getTestCases()) {
                    if (tc.getCommands().contains(command)) {
                        return tg;
                    }
                }
            }
        }
        return null;
    }

    public Map<Integer, Integer> getCashMis() {
        return cashMis;
    }

    public Map<Integer, Integer> getCashErf() {
        return cashErf;
    }

    public Map<Integer, Integer> getCashFail() {
        return cashFail;
    }

    public Map<Integer, Integer> getCashWorking() {
        return cashWorking;
    }

    /**
     * starts a simulation generates a thread pool for each threadsafe command
     * list updates pie charts and manages cancellation
     */
    @Override
    public void run() {
        ExecutionManager.getInstance().log("Testvorgang gestartet", ExecutionManager.LOGLEVEL.HEADLINE);
        //initializes values
        Path pathToErg = null;
        Path pathToRef = null;
        boolean first = true;
        int position = 0;
        String serverIP = ExecutionManager.getInstance().getActiveSystem().getBackoffice().getStrIpAdr();
        try {
            for (List<CommandRun> run : convertTargetsToCommands()) {
                if (run.size() > 0) {
                    //sets the erg and ref path only on the first interration
                    if (first) {
                        try {
                            pathToErg = run.get(0).getPath().getParent().getParent().getParent().resolve("erg").resolve(dtf.format(timestamp));
                            List<File> references = Mapper.getDirectoriesOfDirectory(run.get(0).getPath().getParent().getParent().getParent().resolve("ref").toFile());
                            pathToRef = run.get(0).getPath().getParent().getParent().getParent().resolve("ref").resolve(references.get(references.size() - 1).getName());
                            first = false;
                        } catch (Exception e) {
                            //e.printStackTrace();
                        }
                        if (pathToRef == null) {

                            run.get(0).getPath().getParent().getParent().getParent().resolve("ref").resolve("20.070").toFile().mkdirs();
                            List<File> references = Mapper.getDirectoriesOfDirectory(run.get(0).getPath().getParent().getParent().getParent().resolve("ref").toFile());

                            pathToRef = run.get(0).getPath().getParent().getParent().getParent().resolve("ref").resolve(references.get(references.size() - 1).getName());

                        }
                    }
                    //generates a new thread pool and executor service for every command list
                    executor = Executors.newFixedThreadPool(5);
                    service = new ExecutorCompletionService<>(executor);
                    int cnt = 1;
                    for (Kasse kasse : kassen) {
                        //updates the missing and working count for every cashpoint
                        synchronized (cashMis) {
                            cashMis.put(cnt - 1, cashMis.get(cnt - 1) - run.size());
                        }
                        synchronized (cashWorking) {
                            cashWorking.put(cnt - 1, run.size());
                        }
                        //submits a worker for every cashpoint
                        int bed = Integer.parseInt(bediener.get(position)) + cnt;
                        service.submit(new CommandExecutionWorker(new ArrayList<CommandRun>(run), timestamp, bed + "", bedienerPass.get(position), laden.get(position), kasse, run.get(0).getPath(), "10.0.0.64", cnt, cashFail.get(cnt - 1), cashErf.get(cnt - 1)));
                        cnt++;
                    }

                    updatePieCharts();
                    executor.shutdown();

                    //interrates over all results to ensure everything has finished and updates pie charts
                    for (int i = 0; i < cashpoints; i++) {
                        Future<Map<Integer, List<Boolean>>> res = service.take();
                        updatePieCharts();
                    }
                    updatePieCharts();
                }

            }

            ExecutionManager.getInstance().log("Testvorgang beendet", ExecutionManager.LOGLEVEL.HEADLINE);
            ExecutionManager.getInstance().setLock(false);

        } catch (InterruptedException ex) {
            try {
                executor.shutdownNow();
                ExecutionManager.getInstance().log("Testvorgang abgebrochen", ExecutionManager.LOGLEVEL.HEADLINE);
                ExecutionManager.getInstance().setLock(false);
            } catch (Exception e) {
                //e.printStackTrace();
            }

        }
        //if (GlobalParamter.getInstance().getSelected_user() != null) {
        if (DatabaseGlobalAccess.getInstance().getCurrentNutzer() != null) {
            //Durchlauf durch = GlobalParamter.getInstance().getCurrentRun();
            Durchlauf durch = DatabaseGlobalAccess.getInstance().getNewDurchlauf();
            int fail = 0;
            int erf = 0;
            for (int i = 0; i < cashpoints; i++) {
                fail += cashFail.get(i);
                erf += cashErf.get(i);
            }
            durch.setErfolgreich(erf);
            durch.setFehlgeschlagen(fail);
            durch.setDurchlaufDatum(LocalDate.now());
            durch.setAnzahl(fail + erf);
            durch.setNutzer(DatabaseGlobalAccess.getInstance().getCurrentNutzer());
            durch.setGegenstand(getAllNewTargetItems());
            DB_Access_Manager.getInstance().updateData();
//            GlobalParamter.getInstance().setCurrentRun(durch);
//            try {
//                if (GlobalParamter.getInstance().isStatistic_db_reachable()) {
//                  DB_Access.getInstance().createRun(durch, getAllTargetItems());
//                }
//            } catch (SQLException ex) {
//                Logger.getLogger(CommandExecutionManager.class.getName()).log(Level.SEVERE, null, ex);
//            }
        }
        //starts the analyer tool

        ExecutionManager.getInstance().getParent().getBtNextStep().setEnabled(false);
        if (pathToErg != null && pathToRef != null) {
            DatabaseGlobalAccess.getInstance().setWorkflow(true);
            GlobalAccess.getInstance().getTest_ide_main_frame().changeTool("analyzer");
            GlobalAccess.getInstance().getPaAnalyzer().activate(pathToRef, pathToErg);
        } else {
            ExecutionManager.getInstance().log("Fehler beim Starten des Analysators", ExecutionManager.LOGLEVEL.ERROR);
        }
    }

}
