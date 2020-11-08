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
import general.bl.GlobalParamter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Key listener for the DefaultListModel on the simulator panel
 *
 * @author Florian Deutschmann
 */
public class SimulatorActionListener implements ActionListener {

    private SimulatorActionListener.Level level = null;

    /**
     * Enum to symbolize the
     */
    enum Level {
        Command,
        TestCase,
        TestGroup,
        Project
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    /**
     * Entry point for deletion
     *
     * @param e : corresponding action event
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        List<ExplorerLayer> allItems = new LinkedList<>();
        for (int selected : ExecutionManager.getInstance().getLiTestvorgang().getSelectedIndices()) {
            allItems.add(ExecutionManager.getInstance().getDlm().getElementAt(selected));
        }
        try{
        for (ExplorerLayer item : allItems) {
            if (item instanceof ProjectRun) {
                ExecutionManager.getInstance().getDlm().removeAllElements();
            } else {
                if (item instanceof TestGroupRun) {
                    for (TestCaseRun tc : ((TestGroupRun) item).getTestCases()) {
                        for (CommandRun command : new LinkedList<>(tc.getCommands())) {
                            ExecutionManager.getInstance().getDlm().removeElement(command);
                        }
                        ExecutionManager.getInstance().getDlm().removeElement(tc);
                    }
                } else if (item instanceof TestCaseRun) {
                    for (CommandRun command : new LinkedList<>(((TestCaseRun) item).getCommands())) {
                        ExecutionManager.getInstance().getDlm().removeElement(command);
                    }
                    TestGroupRun tg = getParent((TestCaseRun) item);
                    int count = 0;
                    for (int i = 0; i < ExecutionManager.getInstance().getDlm().size(); i++) {
                        if (ExecutionManager.getInstance().getDlm().getElementAt(i) instanceof TestCaseRun) {
                            TestCaseRun tmp_testCase = (TestCaseRun) ExecutionManager.getInstance().getDlm().getElementAt(i);
                            if (tg.getTestCases().contains(tmp_testCase) && tmp_testCase != (TestCaseRun) item) {
                                count++;
                            }
                        }
                    }
                    if (count == 0) {
                        ExecutionManager.getInstance().getDlm().removeElement(tg);
                    }
                } else if (item instanceof CommandRun) {
                    TestCaseRun tc = getParent((CommandRun) item);
                    int count = 0;
                    for (int i = 0; i < ExecutionManager.getInstance().getDlm().size(); i++) {
                        if (ExecutionManager.getInstance().getDlm().getElementAt(i) instanceof CommandRun) {
                            CommandRun tmp_command = (CommandRun) ExecutionManager.getInstance().getDlm().getElementAt(i);
                            if (tc.getCommands().contains(tmp_command) && tmp_command != (CommandRun) item) {
                                count++;
                            }
                        }
                    }
                    if (count == 0) {
                        ExecutionManager.getInstance().getDlm().removeElement(tc);
                    }
                }
                ExecutionManager.getInstance().getDlm().removeElement(item);
            }
        }
        List<ExplorerLayer> allRemainingItems = new LinkedList<>();
        for (int i = 0; i < ExecutionManager.getInstance().getDlm().getSize(); i++) {
            allRemainingItems.add((ExplorerLayer) ExecutionManager.getInstance().getDlm().getElementAt(i));
        }
        List<ExplorerLayer> target_output = createTargets(allRemainingItems);

        ExecutionManager.getInstance().setTargetsOnly(target_output);
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    /**
     * Creates a new target list
     *
     * @param remainingItems
     * @return
     */
    private List<ExplorerLayer> createTargets(List<ExplorerLayer> remainingItems) {
        try {
            List<ExplorerLayer> targets = new LinkedList<>();
            switch (level) {
                case Project: {
                    ProjectRun pr = null;
                    for (ExplorerLayer remainingItem : remainingItems) {
                        if (remainingItem instanceof ProjectRun) {
                            pr = new ProjectRun(remainingItem.getDescription(), remainingItem.getPath());
                        }
                    }
                    List<TestGroupRun> allTestGroups = createTestGroups(remainingItems);
                    pr.setTestgroups(allTestGroups);
                    targets.add(pr);
                    break;
                }
                case TestGroup: {
                    List<TestGroupRun> allTestGroups = createTestGroups(remainingItems);
                    targets = new LinkedList<>(allTestGroups);
                    break;
                }
                case TestCase:
                    List<TestCaseRun> allTestCases = createTestCases(null, remainingItems);
                    targets = new LinkedList<>(allTestCases);
                    break;
                default:
                    List<CommandRun> allCommands = createCommands(null, remainingItems);
                    targets = new LinkedList<>(allCommands);
                    break;
            }
            return targets;
        } catch (NullPointerException n) {
            return new ArrayList<>();
        }
    }

    /**
     * Creates the testgroups
     *
     * @param remainingItems : still remaining targets
     * @return
     */
    private List<TestGroupRun> createTestGroups(List<ExplorerLayer> remainingItems) {
        List<TestGroupRun> allTestGroups = new LinkedList<>();
        TestGroupRun tg = null;
        for (ExplorerLayer rem_items : remainingItems) {
            if (rem_items instanceof TestGroupRun) {
                TestGroupRun help = (TestGroupRun) rem_items;
                tg = new TestGroupRun(help.getEmpId(), help.getTllId(), help.getPassword(),
                        createTestCases(help, remainingItems), help.getDescription(), help.getPath());
                allTestGroups.add(tg);
            }
        }

        return allTestGroups;
    }

    /**
     * Creates the testcases
     *
     * @param remainingItems : still remaining targets
     * @return
     */
    private List<TestCaseRun> createTestCases(TestGroupRun parent, List<ExplorerLayer> remainingItems) {
        List<TestCaseRun> allTestsCases = new LinkedList<>();
        if (parent == null) {
            for (ExplorerLayer rem_item : remainingItems) {
                if (rem_item instanceof TestCaseRun) {
                    TestCaseRun help = (TestCaseRun) rem_item;
                    TestCaseRun tc = new TestCaseRun(createCommands(help, remainingItems), help.getDescription(), help.getPath());
                    allTestsCases.add(tc);
                }
            }
        } else {
            for (ExplorerLayer rem_item : remainingItems) {
                if (rem_item instanceof TestCaseRun) {
                    if (parent.getTestCases().contains(rem_item)) {
                        TestCaseRun help = (TestCaseRun) rem_item;
                        TestCaseRun tc = new TestCaseRun(createCommands(help, remainingItems), help.getDescription(), help.getPath());
                        allTestsCases.add(tc);
                    }
                }
            }
        }
        return allTestsCases;
    }

    /**
     * Creates the commands
     *
     * @param remainingItems : still remaining targets
     * @return
     */
    private List<CommandRun> createCommands(TestCaseRun parent, List<ExplorerLayer> remainingItems) {
        List<CommandRun> allCommands = new LinkedList<>();

        if (parent == null) {
            for (ExplorerLayer rem_item : remainingItems) {
                if (rem_item instanceof CommandRun) {
                    CommandRun help = (CommandRun) rem_item;
                    CommandRun comm = new CommandRun(help.getClassName(), help.getNodeList(), help.getDescription(), help.getPath());
                    allCommands.add(comm);
                }
            }
        } else {
            for (ExplorerLayer rem_item : remainingItems) {
                if (rem_item instanceof CommandRun) {
                    if (parent.getCommands().contains(rem_item)) {
                        CommandRun help = (CommandRun) rem_item;
                        CommandRun comm = new CommandRun(help.getClassName(), help.getNodeList(), help.getDescription(), help.getPath());
                        allCommands.add(comm);
                    }
                }
            }
        }
        return allCommands;
    }

    /**
     * Returns the testgroup for a command
     * @param command
     * @return 
     */
    private TestCaseRun getParent(CommandRun command) {
        for (ProjectRun workingProject : GlobalParamter.getInstance().getWorkingProjects()) {
            for (TestGroupRun tg : workingProject.getTestgroups()) {
                for (TestCaseRun tc : tg.getTestCases()) {
                    if (tc.getCommands().contains(command)) {
                        return tc;
                    }
                }
            }
        }
        return null;
    }
/**
     * Returns the testgroup for a command
     * @param command
     * @return 
     */
    private TestGroupRun getParent(TestCaseRun testcase) {
        for (ProjectRun workingProject : GlobalParamter.getInstance().getWorkingProjects()) {
            for (TestGroupRun tg : workingProject.getTestgroups()) {
                if (tg.getTestCases().contains(testcase)) {
                    return tg;
                }
            }
        }
        return null;
    }

}
