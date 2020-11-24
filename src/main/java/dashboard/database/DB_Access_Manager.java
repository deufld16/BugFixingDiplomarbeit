/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dashboard.database;

import dashboard.beans.Change;
import dashboard.beans.ChangeType;
import dashboard.beans.Command;
import dashboard.beans.Durchlauf;
import dashboard.beans.Durchlaufgegenstand;
import dashboard.beans.Nutzer;
import dashboard.beans.Projekt;
import dashboard.beans.TestCase;
import dashboard.beans.Testgruppe;
import dashboard.bl.DatabaseGlobalAccess;
import dashboard.enums.ChangeTypeNew;
import dashboard.gui.LoadingDLG;
import general.beans.io_objects.CommandRun;
import general.beans.io_objects.ExplorerLayer;
import general.beans.io_objects.ProjectRun;
import general.beans.io_objects.TestCaseRun;
import general.beans.io_objects.TestGroupRun;
import general.bl.GlobalAccess;
import general.bl.GlobalParamter;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

/**
 *
 * @author flori
 */
public class DB_Access_Manager {

//    private List<Projekt> projektToImport = new LinkedList<>();
//    private List<Testgruppe> testGruppenToImport = new LinkedList<>();
//    private List<TestCase> TestCasesToImport = new LinkedList<>();
//    private List<Command> CommandsToImport = new LinkedList<>();
    private static DB_Access_Manager instance;

    public static DB_Access_Manager getInstance() {
        if (instance == null) {
            instance = new DB_Access_Manager();
        }
        return instance;
    }

    public void connect() {

        DatabaseGlobalAccess.getInstance().setEmf(Persistence.createEntityManagerFactory("PU_dashboarddb"));
        DatabaseGlobalAccess.getInstance().setEm(DatabaseGlobalAccess.getInstance().getEmf().createEntityManager());
        DatabaseGlobalAccess.getInstance().getDatabaseData();
        DatabaseGlobalAccess.getInstance().setDbReachable(true);
    }

    public void importProject(List<ProjectRun> prRuns) {
        List<Projekt> projektToImport = new LinkedList<>();
        List<Testgruppe> testGruppenToImport = new LinkedList<>();
        List<TestCase> TestCasesToImport = new LinkedList<>();
        List<Command> CommandsToImport = new LinkedList<>();
        List<ProjectRun> existingProjects = new LinkedList<>();

        for (ProjectRun p : prRuns) {
            Projekt existingProject = projectExists(p);
            if (existingProject != null) {
                existingProjects.add(p);
                for (TestGroupRun testgroup : p.getTestgroups()) {
                    Testgruppe tg = new Testgruppe(testgroup.getDescription(), LocalDate.now(), 0);
                    if (!containsItem(existingProject, tg)) {
                        tg.setProjekt(existingProject);
                        testGruppenToImport.add(tg);
                    } else {
                        tg = existingProject.getTestgruppen().get(existingProject.getTestgruppen().indexOf(new Testgruppe(testgroup.getDescription(), LocalDate.now(), 0)));
                    }
                    testgroup.setDurchlauf_gegenstand(tg);
                    for (TestCaseRun testC : testgroup.getTestCases()) {
                        TestCase tc = new TestCase(testC.getDescription(), LocalDate.now(), 0);
                        if (!containsItem(tg, tc)) {
                            tc.setTestGruppe(tg);
                            TestCasesToImport.add(tc);
                        } else {
                            tc = tg.getTestCases().get(tg.getTestCases().indexOf(tc));
                        }
                        testC.setDurchlauf_gegenstand(tc);
                        for (CommandRun command : testC.getCommands()) {
                            Command com = new Command(command.getDescription(), LocalDate.now(), 0);
                            if (!containsItem(tc, com)) {
                                com.setTestCase(tc);
                                CommandsToImport.add(com);
                            } else {
                                com = tc.getCommands().get(tc.getCommands().indexOf(com));
                            }
                            command.setDurchlauf_gegenstand(com);
                        }
                    }
                }
            } else {
                existingProject = importProjekt(p);
                DatabaseGlobalAccess.getInstance().getAllProjects().add(existingProject);
                projektToImport.add(existingProject);
            }
            p.setDurchlauf_gegenstand(existingProject);
        }

        for (Projekt projekt : projektToImport) {
            DatabaseGlobalAccess.getInstance().getEm().persist(projekt);
        }
        for (Testgruppe testgruppe : testGruppenToImport) {
            DatabaseGlobalAccess.getInstance().getEm().persist(testgruppe);
        }
        for (TestCase testCase : TestCasesToImport) {
            DatabaseGlobalAccess.getInstance().getEm().persist(testCase);
        }
        for (Command command : CommandsToImport) {
            DatabaseGlobalAccess.getInstance().getEm().persist(command);
        }
        updateDeletedState(existingProjects);
        DatabaseGlobalAccess.getInstance().getEm().getTransaction().begin();
        DatabaseGlobalAccess.getInstance().getEm().getTransaction().commit();
    }

    private void updateDeletedState(List<ProjectRun> pr) {
        List<Projekt> localProject = new LinkedList<>();
        List<Projekt> priorState = new LinkedList<>();
        for (ProjectRun projectRun : pr) {
            Projekt p = new Projekt(projectRun.getDescription());
            localProject.add(p);
            priorState.add(DatabaseGlobalAccess.getInstance().getAllProjects().get(DatabaseGlobalAccess.getInstance().getAllProjects().indexOf(p)));
            for (TestGroupRun testgroup : projectRun.getTestgroups()) {
                Testgruppe tg = new Testgruppe(testgroup.getDescription());
                tg.setProjekt(p);
                for (TestCaseRun testCase : testgroup.getTestCases()) {
                    TestCase tc = new TestCase(testCase.getDescription());
                    tc.setTestGruppe(tg);
                    for (CommandRun command : testCase.getCommands()) {
                        Command com = new Command(command.getDescription());
                        com.setTestCase(tc);
                    }
                }
            }
        }

        for (Projekt projekt : priorState) {
            Projekt p_help = localProject.get(localProject.indexOf(projekt));
            for (Testgruppe testgruppe : projekt.getTestgruppen()) {
                int tg_index = p_help.getTestgruppen().indexOf(testgruppe);
                Testgruppe tg_help = null;
                if (tg_index != -1) {
                    tg_help = p_help.getTestgruppen().get(tg_index);
                }
                if (!p_help.getTestgruppen().contains(testgruppe)) {
                    testgruppe.setDeleted(1);
                } else {
                    testgruppe.setDeleted(0);
                }
                for (TestCase testCase : testgruppe.getTestCases()) {
                    TestCase tc_help = null;
                    if (tg_help == null) {
                        testCase.setDeleted(1);
                    } else {
                        int tc_index = tg_help.getTestCases().indexOf(testCase);
                        if (tc_index != -1) {
                            tc_help = tg_help.getTestCases().get(tc_index);
                        }
                        if (!tg_help.getTestCases().contains(testCase)) {
                            testCase.setDeleted(1);
                        } else {
                            testCase.setDeleted(0);
                        }
                    }
                    for (Command command : testCase.getCommands()) {
                        if (tc_help == null) {
                            command.setDeleted(1);
                        } else {
                            if (!tc_help.getCommands().contains(command)) {
                                command.setDeleted(1);
                            } else {
                                command.setDeleted(0);
                            }
                        }
                    }
                }
            }
        }
    }

    private boolean containsItem(Durchlaufgegenstand parent, Durchlaufgegenstand child) {
        if (parent instanceof Projekt) {
            if (((Projekt) parent).getTestgruppen().contains((Testgruppe) child)) {
                return true;
            }
        } else if (parent instanceof Testgruppe) {
            if (((Testgruppe) parent).getTestCases().contains((TestCase) child)) {
                return true;
            }
        } else if (parent instanceof TestCase) {
            if (((TestCase) parent).getCommands().contains((Command) child)) {
                return true;
            }
        }
        return false;
    }

    private Projekt importProjekt(ProjectRun pr) {
        Projekt projekt = new Projekt(pr.getDescription(), LocalDate.now(), 0);
        pr.setDurchlauf_gegenstand(projekt);
        for (TestGroupRun testgroup : pr.getTestgroups()) {
            Testgruppe tg = new Testgruppe(testgroup.getDescription(), LocalDate.now(), 0);
            tg.setProjekt(projekt);
            testgroup.setDurchlauf_gegenstand(tg);
            for (TestCaseRun testcase : testgroup.getTestCases()) {
                TestCase tc = new TestCase(testcase.getDescription(), LocalDate.now(), 0);
                tc.setTestGruppe(tg);
                testcase.setDurchlauf_gegenstand(tc);
                for (CommandRun command : testcase.getCommands()) {
                    Command c = new Command(command.getDescription(), LocalDate.now(), 0);
                    c.setTestCase(tc);
                    command.setDurchlauf_gegenstand(c);
                }
            }
        }
        return projekt;
    }

    private Projekt projectExists(ProjectRun pr) {
        for (Projekt proj : DatabaseGlobalAccess.getInstance().getAllProjects()) {
            if (pr.getDescription().equalsIgnoreCase(proj.getBezeichnung())) {
                return proj;
            }
        }
        return null;
    }

    public void disconnect() {
        DatabaseGlobalAccess.getInstance().getEm().close();
        DatabaseGlobalAccess.getInstance().getEmf().close();
    }

    public void updateData() {
        DatabaseGlobalAccess.getInstance().getEm().getTransaction().begin();
        DatabaseGlobalAccess.getInstance().getEm().getTransaction().commit();
    }

    public void addUser(String username) {
        Nutzer nutzer = new Nutzer(username);
        DatabaseGlobalAccess.getInstance().getEm().persist(nutzer);
        updateData();
        DatabaseGlobalAccess.getInstance().getAllUsers().add(nutzer);
        DatabaseGlobalAccess.getInstance().setCurrentNutzer(nutzer);
    }

    public List<Change> selectChanges() {
        TypedQuery<Change> changeTypeQuery = DatabaseGlobalAccess.getInstance().getEm().createNamedQuery("ChangeNew.selectAll", Change.class);
        return changeTypeQuery.getResultList();
    }

    public List<Change> selectChanges(LocalDateTime from, LocalDateTime until) {
        TypedQuery<Change> changeTypeQuery = DatabaseGlobalAccess.getInstance().getEm().createNamedQuery("ChangeNew.selectAllInterval", Change.class);
        changeTypeQuery.setParameter("vonDate", from);
        changeTypeQuery.setParameter("bisDate", until);
        return changeTypeQuery.getResultList();
    }

    public List<Change> selectChanges(LocalDateTime from, LocalDateTime until, Nutzer user) {
        TypedQuery<Change> changeTypeQuery = DatabaseGlobalAccess.getInstance().getEm().createNamedQuery("ChangeNew.selectAllIntervalUser", Change.class);
        changeTypeQuery.setParameter("vonDate", from);
        changeTypeQuery.setParameter("bisDate", until);
        changeTypeQuery.setParameter("userId", user.getNutzerid());
        return changeTypeQuery.getResultList();
    }

    public List<Durchlauf> selectRun() {
        TypedQuery<Durchlauf> changeTypeQuery = DatabaseGlobalAccess.getInstance().getEm().createNamedQuery("DurchlaufNew.selectAll", Durchlauf.class);
        return changeTypeQuery.getResultList();
    }

    public List<Durchlauf> selectRun(LocalDate from, LocalDate until) {
        TypedQuery<Durchlauf> changeTypeQuery = DatabaseGlobalAccess.getInstance().getEm().createNamedQuery("DurchlaufNew.selectAllInterval", Durchlauf.class);
        changeTypeQuery.setParameter("vonDate", from);
        changeTypeQuery.setParameter("bisDate", until);
        return changeTypeQuery.getResultList();
    }

    public List<Durchlauf> selectRun(LocalDate from, LocalDate until, Nutzer user) {
        TypedQuery<Durchlauf> changeTypeQuery = DatabaseGlobalAccess.getInstance().getEm().createNamedQuery("DurchlaufNew.selectAllIntervalUser", Durchlauf.class);
        changeTypeQuery.setParameter("vonDate", from);
        changeTypeQuery.setParameter("bisDate", until);
        changeTypeQuery.setParameter("userId", user.getNutzerid());
        return changeTypeQuery.getResultList();
    }

    public void addChangeEntry(ExplorerLayer entity, String bezeichnung) {
        Change change = new Change(LocalDateTime.now());
        for (ChangeType changeType : DatabaseGlobalAccess.getInstance().getAllChangeTypes()) {
            if (changeType.getBezeichnung().equalsIgnoreCase(bezeichnung)) {
                change.setChangeType(changeType);
            }
        }
        change.setGegenstand(entity.getDurchlauf_gegenstand());
        change.setNutzer(DatabaseGlobalAccess.getInstance().getCurrentNutzer());
        try {
            updateData();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void addChangeEntry(Durchlaufgegenstand entity, String bezeichnung) {
        Change change = new Change(LocalDateTime.now());
        for (ChangeType changeType : DatabaseGlobalAccess.getInstance().getAllChangeTypes()) {
            if (changeType.getBezeichnung().equalsIgnoreCase(bezeichnung)) {
                change.setChangeType(changeType);
            }
        }
        change.setGegenstand(entity);
        change.setNutzer(DatabaseGlobalAccess.getInstance().getCurrentNutzer());
        try {
            updateData();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void deleteNode(Durchlaufgegenstand entity) {
        entity.setDeleted(1);
        if (entity instanceof Projekt) {
            for (Testgruppe tg : ((Projekt) entity).getTestgruppen()) {
                tg.setDeleted(1);
                addChangeEntry(tg, "DELETED");
                for (TestCase testCase : tg.getTestCases()) {
                    testCase.setDeleted(1);
                    addChangeEntry(testCase, "DELETED");
                    for (Command command : testCase.getCommands()) {
                        command.setDeleted(1);
                        addChangeEntry(command, "DELETED");
                    }
                }
            }
        } else if (entity instanceof Testgruppe) {
            for (TestCase testCase : ((Testgruppe) entity).getTestCases()) {
                testCase.setDeleted(1);
                addChangeEntry(testCase, "DELETED");
                for (Command command : testCase.getCommands()) {
                    command.setDeleted(1);
                    addChangeEntry(command, "DELETED");
                }
            }
        } else if (entity instanceof TestCase) {
            for (Command command : ((TestCase) entity).getCommands()) {
                command.setDeleted(1);
                addChangeEntry(command, "DELETED");
            }
        }
    }
}
