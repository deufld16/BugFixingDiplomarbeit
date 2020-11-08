/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dashboard.database;

import dashboard.beans.DurchlaufgegenstandNew;
import dashboard.bl.DatabaseGlobalAccess;
import general.beans.io_objects.CommandRun;
import general.beans.io_objects.ProjectRun;
import general.beans.io_objects.TestCaseRun;
import general.beans.io_objects.TestGroupRun;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Month;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author A180953
 */
public class Test {

    public void testImport() {
        List<ProjectRun> allProjects = new LinkedList<>();

        ProjectRun pr = new ProjectRun("Projekt - 1", null);
        ProjectRun pr2 = new ProjectRun("Projekt - 2", null);
        TestGroupRun tg1 = new TestGroupRun("Testgroup - 1", null);
        TestGroupRun tg2 = new TestGroupRun("Testgroup - 2", null);
        TestGroupRun tg3 = new TestGroupRun("Testgroup - 1", null);
        TestGroupRun tg4 = new TestGroupRun("Testgroup - 2", null);
        TestCaseRun tc1 = new TestCaseRun("TestCase - 1", null);
        TestCaseRun tc2 = new TestCaseRun("TestCase - 2", null);
        TestCaseRun tc3 = new TestCaseRun("TestCase - 1", null);
        TestCaseRun tc4 = new TestCaseRun("TestCase - 2", null);
        TestCaseRun tc5 = new TestCaseRun("TestCase - 1", null);
        //TestCaseRun tc6 = new TestCaseRun("TestCase - 2", null);
        TestCaseRun tc7 = new TestCaseRun("TestCase - 1", null);
        TestCaseRun tc8 = new TestCaseRun("TestCase - 2", null);
        CommandRun cr = new CommandRun(null, null, "Command - 1", null);
        CommandRun cr2 = new CommandRun(null, null, "Command - 1", null);
        CommandRun cr3 = new CommandRun(null, null, "Command - 1", null);
        CommandRun cr4 = new CommandRun(null, null, "Command - 1", null);
        CommandRun cr5 = new CommandRun(null, null, "Command - 1", null);
        CommandRun cr6 = new CommandRun(null, null, "Command - 2", null);
        CommandRun cr7 = new CommandRun(null, null, "Command - 1", null);
        CommandRun cr8 = new CommandRun(null, null, "Command - 1", null);

        tc1.getCommands().add(cr);
        tc2.getCommands().add(cr2);
        tc3.getCommands().add(cr3);
        tc4.getCommands().add(cr4);
        tc5.getCommands().add(cr5);
        tc5.getCommands().add(cr6);
        //tc6.getCommands().add(cr6);
        tc7.getCommands().add(cr7);
        tc8.getCommands().add(cr8);

        tg1.getTestCases().add(tc1);
        tg1.getTestCases().add(tc2);
        tg2.getTestCases().add(tc3);
        tg2.getTestCases().add(tc4);
        tg3.getTestCases().add(tc5);
        //tg3.getTestCases().add(tc6);
        tg4.getTestCases().add(tc7);
        tg4.getTestCases().add(tc8);

        pr.getTestgroups().add(tg1);
        pr.getTestgroups().add(tg2);
        pr2.getTestgroups().add(tg3);
        pr2.getTestgroups().add(tg4);

        allProjects.add(pr);
        allProjects.add(pr2);

        DB_Access_Manager.getInstance().importProject(allProjects);
    }

    //1:35 für 1 Millionen Datensätze
    public void limitTest() {
        for (int i = 0; i < 1000000; i++) {
            if (i % 10000 == 0) {
                System.out.println(i);
            }
            DatabaseGlobalAccess.getInstance().getEm().persist(new DurchlaufgegenstandNew("Test_" + i, LocalDate.now(), 0));
        }
        DatabaseGlobalAccess.getInstance().getEm().getTransaction().begin();
        DatabaseGlobalAccess.getInstance().getEm().getTransaction().commit();
    }

    public static void main(String[] args) {
        Test test = new Test();
        DB_Access_Manager.getInstance().connect();
        test.limitTest();
        DB_Access_Manager.getInstance().disconnect();
    }
}
