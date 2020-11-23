/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simulator.bl;

import general.beans.io_objects.CommandRun;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import simulator.beans.Kasse;
import simulator.beans.RuntimeEnv;
import simulator.beans.Testcase;
import simulator.beans.Testgroup;
import simulator.commands.ACommand;
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
import simulator.communication.CommandClient;
import simulator.interfaces.BackofficeAccess;

/**
 * Worker for several corresponding teststeps on one cashpoint
 *
 * @author Lukas Krobath
 */
public class CommandExecutionWorker implements Callable<Map<Integer, List<Boolean>>> {

    private RuntimeEnv runtimeEnv;
    private Kasse kassa;
    private List<CommandRun> targets;
    private int kassaNr;
    private String testcaseName;
    private String testgroupName;
    private int fail;
    private int erf;
    private List<Boolean> results = new ArrayList<>();
    private LocalDateTime timestamp;

    public CommandExecutionWorker(List<CommandRun> targets, LocalDateTime timestamp, String bediener, String bedienerPass, int ladeID, Kasse kassa, Path strTestCasePath, String serverIP, int kassaNr, int fail, int erf) {
        this.targets = targets;
        this.kassaNr = kassaNr;
        this.fail = fail;
        this.erf = erf;
        this.kassa = kassa;
        this.timestamp = timestamp;
        this.testcaseName = strTestCasePath.toFile().getName();
        this.testgroupName = strTestCasePath.getParent().toFile().getName();
        this.runtimeEnv = new RuntimeEnv(serverIP, 1,
                new Testgroup(bediener, bedienerPass, ladeID, new Testcase(strTestCasePath, timestamp, kassaNr)), kassa);
    }

    /**
     * Executes the set targets logs the execution to the log area
     *
     * Updates the pie charts
     *
     * @return
     */
    @Override
    public Map<Integer, List<Boolean>> call() {

        List<ACommand> holder = createCommandList(targets);

        int erfL = 0;
        int failL = 0;
        for (ACommand aCommand : holder) {

            if (aCommand instanceof ExcecuteRecorderFileCommand) {
                ((ExcecuteRecorderFileCommand) aCommand).setLocalStep(ExecutionManager.getInstance().getStepLock());
            }
            results.add(callCommand(aCommand));

            if (results.get(results.size() - 1)) {
                erfL++;
                ExecutionManager.getInstance().getMan().getCashErf().put(kassaNr - 1, ++erf);
            } else {
                failL++;
                ExecutionManager.getInstance().getMan().getCashFail().put(kassaNr - 1, ++fail);
            }

            ExecutionManager.getInstance().getMan().getCashWorking().put(kassaNr - 1, holder.size() - (erfL + failL));
            ExecutionManager.getInstance().getMan().updatePieCharts();

        }

        Map<Integer, List<Boolean>> erg = new HashMap<Integer, List<Boolean>>();
        erg.put(kassaNr - 1, results);

        return erg;
    }

    /**
     * Initializes a corresponding log message to a command
     *
     * @param aCommand : target
     * @param remote : if the target is remotely executed
     */
    private void log(ACommand aCommand, boolean remote) {
        if (aCommand instanceof CheckRepInZAMTlogCommand) {
            logToLogArea("CheckRepInZAMTlog Befehl", remote);
        } else if (aCommand instanceof ChkSyncCommandV2) {
            logToLogArea("ChkSync Befehl", remote);
        } else if (aCommand instanceof CopyCommand) {
            logToLogArea("Copy Befehl", remote);
        } else if (aCommand instanceof ExportTableContentCommand) {
            logToLogArea("ExportTableContent Befehl", remote);
        } else if (aCommand instanceof WaitCommand) {
            logToLogArea("Wait Befehl", remote);
        } else if (aCommand instanceof EljoCommandV2) {
            logToLogArea("Eljo Befehl", remote);
        } else if (aCommand instanceof SqlQueryCommand) {
            logToLogArea("SQL Abfrage", remote);
        } else if (aCommand instanceof FTPCommand) {
            logToLogArea("FTP Befehl", remote);
        } else if (aCommand instanceof CtrlCommandV2) {
            logToLogArea("Ctrl Befehl", remote);
        } else if (aCommand instanceof DbimportCommand) {
            logToLogArea("Dbimport Befehl", remote);
        } else if (aCommand instanceof ExcecuteRecorderFileCommand) {
            logToLogArea("Rekorder " + ((ExcecuteRecorderFileCommand) aCommand).getXmlRecFile().getName(), remote);
        } else if (aCommand instanceof CallGlobalCommand) {
            logToLogArea("Globaler Befehl", remote);
        } else if (aCommand instanceof ShellCommandLX) {
            logToLogArea("Shell Befehl in Linux", remote);
        }
    }

    /**
     * Submethod for log() to log messages easier to the log area
     *
     * @param commandname : Name of the logged command
     */
    private void logToLogArea(String commandname, boolean remote) {
        if (!remote) {
            ExecutionManager.getInstance().log(String.format("Kassa %s - <b>%s</b> in der <b>Testgruppe %s (Testcase %s)</b> wurde lokal ausgeführt", kassaNr, commandname, testgroupName, testcaseName), ExecutionManager.LOGLEVEL.NORMAL);
        } else {
            ExecutionManager.getInstance().log(String.format("Kassa %s - <b>%s</b> in der <b>Testgruppe %s (Testcase %s)</b> wurde remote ausgeführt", kassaNr, commandname, testgroupName, testcaseName), ExecutionManager.LOGLEVEL.NORMAL);

        }
    }

    /**
     * Converts the list of CommandRun objects to a executable List of ACommands
     *
     * @param input : Input list of CommandRun objects
     * @return
     */
    private List<ACommand> createCommandList(List<CommandRun> input) {
        List<ACommand> result = new ArrayList<>();
        for (CommandRun commandRun : input) {
            ACommand tmp = null;
            switch (commandRun.getClassName()) {
                case "CheckRepInZAMTlogCommand":
                    tmp = new CheckRepInZAMTlogCommand(runtimeEnv);
                    break;
                case "ChkSyncCommandV2":
                    tmp = new ChkSyncCommandV2(runtimeEnv);
                    break;
                case "CopyCommand":
                    tmp = new CopyCommand(runtimeEnv);
                    break;
                case "ExportTableContentCommand":
                    tmp = new ExportTableContentCommand(runtimeEnv);
                    break;
                case "WaitCommand":
                    tmp = new WaitCommand(runtimeEnv);
                    break;
                case "EljoCommandV2":
                    tmp = new EljoCommandV2(runtimeEnv);
                    break;
                case "SqlQueryCommand":
                    tmp = new SqlQueryCommand(runtimeEnv);
                    break;
                case "FTPCommand":
                    tmp = new FTPCommand(runtimeEnv);
                    break;
                case "CtrlCommandV2":
                    tmp = new CtrlCommandV2(runtimeEnv);
                    break;
                case "DbimportCommand":
                    tmp = new DbimportCommand(runtimeEnv);
                    break;
                case "ExecuteRecorderFileCommand":
                    tmp = new ExcecuteRecorderFileCommand(runtimeEnv, commandRun.getPath(), kassaNr, timestamp);
                    break;
                case "GlobalCommand":
                    tmp = new CallGlobalCommand();
                    break;
                case "ShellCommand":
                    tmp = new ShellCommandLX(runtimeEnv);
                    break;
                default:
                    ExecutionManager.getInstance().log("Befehl " + commandRun.getClassName() + " wird noch nicht unterstützt", ExecutionManager.LOGLEVEL.ERROR);
                    ExecutionManager.getInstance().getMan().getCashFail().put(kassaNr - 1, ++fail);
                    results.add(false);
            }
            if (tmp != null && commandRun.getNodeList() != null) {
                try {
                    tmp.setNodeParams(commandRun.getNodeList());
                    result.add(tmp);
                } catch (NullPointerException n) {
                    System.out.println(tmp + " had a nullpointer");
                    //n.printStackTrace();
                }
            }
        }

        return result;
    }

    /**
     * Calls the doWork() and doCheck() methods of a ACommand object
     *
     * @param command : Command to Execute
     * @return
     */
    private boolean callCommand(ACommand command) {

        if (command.getClass().isAnnotationPresent(BackofficeAccess.class)) {
            FutureTask<Boolean> futureTask;
            try {
                futureTask = new FutureTask<>(new CommandClient(command));
                new Thread(futureTask).start();
                log(command, true);
                return futureTask.get();
            } catch (Exception ex) {
                ExecutionManager.getInstance().log("<b>Backoffice ist nicht erreichbar</b> - Remote Commands werden nicht mehr ausgeführt", ExecutionManager.LOGLEVEL.ERROR);
            }

        } else {
            try {
                boolean bDoWork = command.doWork();
                boolean bDoCheck = command.doCheck();

                if (!bDoWork || !bDoCheck) {

                    return false;
                }
                log(command, false);
                return true;

            } catch (Exception e) {
                //e.printStackTrace();
                return false;
            }
        }
        return false;
    }

}
