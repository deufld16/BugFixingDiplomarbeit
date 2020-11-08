package dashboard.database;

//import com.sun.corba.se.impl.orb.PrefixParserData;
import dashboard.beans.Change;
import dashboard.beans.Durchlaufgegenstand;
import dashboard.beans.Nutzer;
import dashboard.beans.Durchlauf;
import dashboard.enums.ChangeType;
import dashboard.gui.LoadingDLG;
import general.beans.io_objects.CommandRun;
import general.beans.io_objects.ExplorerLayer;
import general.beans.io_objects.ProjectRun;
import general.beans.io_objects.TestCaseRun;
import general.beans.io_objects.TestGroupRun;
import general.bl.GlobalAccess;
import general.bl.GlobalParamter;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JDialog;
import org.postgresql.util.PSQLException;

/**
 * Class to issue SQL statements
 *
 * @author Florian Deutschmann
 */
public class DB_Access {

//    private static DB_Access theInstance = null;
//
//    public static DB_Access getInstance() {
//        if (theInstance == null) {
//            theInstance = new DB_Access();
//        }
//        return theInstance;
//    }
//    private DB_Database database = null;
//
//    private DB_Access() {
//        try {
//            database = DB_Database.getInstance();
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * Method to get all users from the database
//     *
//     * @return
//     * @throws SQLException
//     */
//    public List<Nutzer> selectAllUsers() throws SQLException {
//        List<Nutzer> allUsers = new LinkedList<>();
//        Statement statement = database.getStatement();
//        ResultSet rs = statement.executeQuery("SELECT * FROM NUTZER");
//        while (rs.next()) {
//            allUsers.add(new Nutzer(rs.getInt("nutzerid"), rs.getString("username")));
//        }
//        database.releaseStatement(statement);
//        return allUsers;
//    }
//
//    /**
//     * Method to get all entries of the change history
//     *
//     * @return
//     * @throws SQLException
//     */
//    public List<Change> selectChanges() throws SQLException {
//        Statement statement = database.getStatement();
//        ResultSet rs = statement.executeQuery("SELECT * FROM CHANGEHISTORY");
//        List<Change> allChanges = new LinkedList<>();
//        while (rs.next()) {
//            allChanges.add(createChange(rs));
//        }
//        database.releaseStatement(statement);
//        return allChanges;
//    }
//
//    public List<Change> selectChanges(LocalDateTime from, LocalDateTime until) throws SQLException {
//        if (select_changes_date == null) {
//            select_changes_date = database.getConnection().prepareStatement(SELECT_CHANGES_DATE);
//        }
//
//        List<Change> allChanges = new LinkedList<>();
//        select_changes_date.setTimestamp(1, java.sql.Timestamp.valueOf(from));
//        select_changes_date.setTimestamp(2, java.sql.Timestamp.valueOf(until));
//
//        ResultSet rs = select_changes_date.executeQuery();
//
//        while (rs.next()) {
//            allChanges.add(createChange(rs));
//        }
//        return allChanges;
//    }
//
//    public List<Change> selectChanges(LocalDateTime from, LocalDateTime until, Nutzer user) throws SQLException {
//        if (select_changes_per_user == null) {
//            select_changes_per_user = database.getConnection().prepareStatement(SELECT_CHANGES_PER_USER);
//        }
//
//        List<Change> allChanges = new LinkedList<>();
//        select_changes_per_user.setTimestamp(1, java.sql.Timestamp.valueOf(from));
//        select_changes_per_user.setTimestamp(2, java.sql.Timestamp.valueOf(until));
//        select_changes_per_user.setInt(3, user.getNutzerid());
//
//        ResultSet rs = select_changes_per_user.executeQuery();
//
//        while (rs.next()) {
//            allChanges.add(createChange(rs));
//        }
//        return allChanges;
//    }
//
//    public List<Durchlauf> selectRun() throws SQLException {
//        Statement statement = database.getStatement();
//        ResultSet rs = statement.executeQuery("SELECT * FROM DURCHLAUF");
//        List<Durchlauf> allRuns = new LinkedList<>();
//        while (rs.next()) {
//            allRuns.add(createDurchlauf(rs));
//        }
//        database.releaseStatement(statement);
//        return allRuns;
//    }
//
//    public List<Durchlauf> selectRun(LocalDate from, LocalDate until) throws SQLException {
//        if (select_runs_date == null) {
//            select_runs_date = database.getConnection().prepareStatement(SELECT_RUNS_DATE);
//        }
//
//        List<Durchlauf> allRuns = new LinkedList<>();
//        select_runs_date.setDate(1, java.sql.Date.valueOf(from));
//        select_runs_date.setDate(2, java.sql.Date.valueOf(until));
//
//        ResultSet rs = select_runs_date.executeQuery();
//
//        while (rs.next()) {
//            allRuns.add(createDurchlauf(rs));
//        }
//        return allRuns;
//    }
//
//    public List<Durchlauf> selectRun(LocalDate from, LocalDate until, Nutzer user) throws SQLException {
//        if (select_runs_per_user == null) {
//            select_runs_per_user = database.getConnection().prepareStatement(SELECT_RUNS_PER_USER);
//        }
//
//        List<Durchlauf> allRuns = new LinkedList<>();
//        select_runs_per_user.setDate(1, java.sql.Date.valueOf(from));
//        select_runs_per_user.setDate(2, java.sql.Date.valueOf(until));
//        select_runs_per_user.setInt(3, user.getNutzerid());
//
//        ResultSet rs = select_runs_per_user.executeQuery();
//
//        while (rs.next()) {
//            allRuns.add(createDurchlauf(rs));
//        }
//        return allRuns;
//    }
//
//    public void updateDurchlauf(Durchlauf durchlauf) throws SQLException {
//        if (update_durchlauf == null) {
//            update_durchlauf = database.getConnection().prepareStatement(UPDATE_DURCHLAUF);
//        }
//        update_durchlauf.setInt(1, durchlauf.getUebernahmeAnz());
//        update_durchlauf.setInt(2, durchlauf.getDurchlaufId());
//        update_durchlauf.executeUpdate();
//    }
//
//    public void addChangeEntry(ExplorerLayer entry, ChangeType type) throws SQLException {
//        if (GlobalParamter.getInstance().getSelected_user() != null) {
//            if (insert_change_history_entry == null) {
//                insert_change_history_entry = database.getConnection().prepareStatement(INSERT_CHANGE_HISTORY_ENTRY);
//            }
//            int index = getIdOfDurchlaufgegenstand(entry, entry);
//            Nutzer user = GlobalParamter.getInstance().getSelected_user();
//            int user_id = user.getNutzerid();
//            insert_change_history_entry.setInt(1, user_id);
//            insert_change_history_entry.setInt(2, index);
//            insert_change_history_entry.setTimestamp(3, java.sql.Timestamp.valueOf(LocalDateTime.now()));
//            insert_change_history_entry.setInt(4, type.getType());
//            insert_change_history_entry.executeUpdate();
//        }
//    }
//
//    /**
//     * Beim Umbenennen
//     *
//     * @param child
//     * @param oldDescription
//     * @throws SQLException
//     */
//    public void updateEntry(ExplorerLayer child, String oldDesc) throws SQLException {
//        if (update_description == null) {
//            update_description = database.getConnection().prepareStatement(UPDATE_DESCRIPTION);
//        }
//        ExplorerLayer tmp = null;
//        if (child instanceof ProjectRun) {
//            tmp = new ProjectRun(oldDesc, null);
//        } else if (child instanceof TestGroupRun) {
//            tmp = new TestGroupRun(oldDesc, null);
//        } else if (child instanceof TestCaseRun) {
//            tmp = new TestCaseRun(oldDesc, null);
//        } else if (child instanceof CommandRun) {
//            tmp = new CommandRun(null, null, oldDesc, null);
//        }
//        int index = getIdOfDurchlaufgegenstand(tmp, child);
//        update_description.setString(1, child.getDescription());
//        update_description.setInt(2, index);
//        update_description.executeUpdate();
//    }
//
//    public void updateCommand(CommandRun command, TestCaseRun parent) throws SQLException {
//        if (update_description == null) {
//            update_description = database.getConnection().prepareStatement(UPDATE_DESCRIPTION);
//        }
//        int index = getIdOfCommand(command, parent);
//        update_description.setString(1, command.getDescription());
//        update_description.setInt(2, index);
//        update_description.executeUpdate();
//    }
//
//    public void removeEntry(ExplorerLayer explo) throws SQLException {
//        if (update_deleted == null) {
//            update_deleted = database.getConnection().prepareStatement(UPDATE_DELETED_TRUE);
//        }
//        int index = getIdOfDurchlaufgegenstand(explo, explo);
//        update_deleted.setInt(1, index);
//        update_deleted.executeUpdate();
//    }
//
//    public void removeEntry(int delete_index) throws SQLException {
//        if (update_deleted == null) {
//            update_deleted = database.getConnection().prepareStatement(UPDATE_DELETED_TRUE);
//        }
//        update_deleted.setInt(1, delete_index);
//        update_deleted.executeUpdate();
//    }
//
//    /**
//     * Beim Verschieben
//     *
//     * @param child
//     * @param parent
//     * @param oldDescription
//     * @throws SQLException
//     */
//    public void updateEntry(ExplorerLayer child, ExplorerLayer parent, String oldDescription) throws SQLException {
////        if (child instanceof CommandRun) {
////            updateCommand((CommandRun) child, (TestCaseRun) parent);
////        } else {
//        updateEntry(child, oldDescription);
//        //}
//
//        if (update_parent_tc == null || update_parent_tg == null || update_parent_command == null) {
//            update_parent_tc = database.getConnection().prepareStatement(UPDATE_PARENT_TC);
//            update_parent_tg = database.getConnection().prepareStatement(UPDATE_PARENT_TG);
//            update_parent_command = database.getConnection().prepareStatement(UPDATE_PARENT_COMMAND);
//        }
//        //Durchlaufgegenstand Bezeichnung ändern fehlt
//        int index_child = -1;
//
////        if (child instanceof CommandRun) {
////            index_child = getIdOfCommand(child, (TestCaseRun) parent);
////        } else {
//        index_child = getIdOfDurchlaufgegenstand(child, child);
//        //}
//        int index_parent = getIdOfDurchlaufgegenstand(parent, parent);
//
//        if (child instanceof TestGroupRun) {
//            update_parent_tg.setInt(1, index_parent);
//            update_parent_tg.setInt(2, index_child);
//            update_parent_tg.executeUpdate();
//        } else if (child instanceof TestCaseRun) {
//            update_parent_tc.setInt(1, index_parent);
//            update_parent_tc.setInt(2, index_child);
//            update_parent_tc.executeUpdate();
//        } else if (child instanceof CommandRun) {
//            update_parent_command.setInt(1, index_parent);
//            update_parent_command.setInt(2, index_child);
//            try {
//                update_parent_command.executeUpdate();
//            } catch (Exception ex) {
//                ex.printStackTrace();
//            }
//        }
//    }
//
//    //public void update
//    public void deleteDurchlaufItem(int gegenstandid) {
//
//    }
//
//    public void importProject(List<ProjectRun> prRuns) throws SQLException {
//        System.out.println("import started");
//        int ammountTestGroups = 0;
//        for (ProjectRun workingProject : GlobalParamter.getInstance().getWorkingProjects()) {
//            for (TestGroupRun testgroup : workingProject.getTestgroups()) {
//                ammountTestGroups++;
//            }
//        }
//        LoadingDLG dlg = new LoadingDLG(GlobalAccess.getInstance().getTest_ide_main_frame(), 
//                true, ammountTestGroups, "Importing data into database", 
//                "%d Elemente von %d Elementen importiert");
//        Thread importThread = new Thread(new ImportData(prRuns, dlg));
//        importThread.start();
//        dlg.setVisible(true);
//        System.out.println("import finished");
//    }
//
//    private class ImportData implements Runnable {
//
//        private List<ProjectRun> allProjects = new LinkedList<>();
//        private LoadingDLG dlg = null;
//
//        public ImportData(List<ProjectRun> allProjects, LoadingDLG dlg) {
//            this.allProjects = allProjects;
//            this.dlg = dlg;
//        }
//
//        @Override
//        public void run() {
//            for (ProjectRun pr : allProjects) {
//                try {
//                    if (checkIfProjectExists(pr)) {
//                        for (TestGroupRun testgroup : pr.getTestgroups()) {
//                            if (!durchlaufItemExists(testgroup, pr)) {
//                                int projekt_index = getIdOfDurchlaufgegenstand(pr, pr);
//                                addTestGroup(testgroup, projekt_index);
//                            }
//                            dlg.increaseCurrValue();
//                            for (TestCaseRun testCase : testgroup.getTestCases()) {
//                                if (!durchlaufItemExists(testCase, testgroup)) {
//                                    int tg_index = getIdOfDurchlaufgegenstand(testgroup, testgroup);
//                                    addTestCase(testCase, tg_index);
//                                }
//                                for (CommandRun command : testCase.getCommands()) {
//                                    if (!durchlaufItemExists(command, testCase)) {
//                                        int tc_index = getIdOfDurchlaufgegenstand(testCase, testCase);
//                                        addCommand(command, tc_index);
//                                    }
//                                }
//                            }
//                        }
//                        deleteMissingEntities(pr);
//                    } else {
//                        importIntoDataBase(pr);
//                    }
//                } catch (SQLException ex) {
//                    Logger.getLogger(DB_Access.class.getName()).log(Level.SEVERE, null, ex);
//                }
//            }
//        }
//
//    }
//
//    public void forcefullyImportProject() throws SQLException {
//        deleteFromDatabase();
//        for (ProjectRun pr : GlobalParamter.getInstance().getWorkingProjects()) {
//            importIntoDataBase(pr);
//        }
//    }
//
//    private void importIntoDataBase(ProjectRun pr) throws SQLException {
//        int project_index = addProject(pr);
//        for (TestGroupRun tg : pr.getTestgroups()) {
//            int testGroup_index = addTestGroup(tg, project_index);
//            for (TestCaseRun tc : tg.getTestCases()) {
//                int testCase_index = addTestCase(tc, testGroup_index);
//                for (CommandRun command : tc.getCommands()) {
//                    addCommand(command, testCase_index);
//                }
//            }
//        }
//    }
//
//    private void deleteFromDatabase() throws SQLException {
//        if (delete_commands == null || delete_project == null || delete_testcases == null || delete_testgruppen == null || delete_durchlaufgegenstand == null) {
//            delete_commands = database.getConnection().prepareStatement(DELETE_COMMANDS);
//            delete_project = database.getConnection().prepareStatement(DELETE_PROJECT);
//            delete_testcases = database.getConnection().prepareStatement(DELETE_TESTCASES);
//            delete_testgruppen = database.getConnection().prepareStatement(DELETE_TESTGRUPPEN);
//            delete_durchlaufgegenstand = database.getConnection().prepareStatement(DELETE_DURCHLAUFGEGENSTAND);
//        }
//
//        List<Integer> deletedCommandIndex = new LinkedList<>();
//        List<Integer> deletedProjectIndex = new LinkedList<>();
//        List<Integer> deletedTestGroupIndex = new LinkedList<>();
//        List<Integer> deletedTestCaseIndex = new LinkedList<>();
//        for (ProjectRun pr : GlobalParamter.getInstance().getWorkingProjects()) {
//            for (TestGroupRun testgroup : pr.getTestgroups()) {
//                for (TestCaseRun testCase : testgroup.getTestCases()) {
//                    for (CommandRun command : testCase.getCommands()) {
//                        int command_index = getIdOfDurchlaufgegenstand(command, command);
//                        deletedCommandIndex.add(command_index);
//                    }
//                    int test_case_index = getIdOfDurchlaufgegenstand(testCase, testCase);
//                    delete_commands.setInt(1, test_case_index);
//                    delete_commands.executeUpdate();
//                    deletedTestCaseIndex.add(test_case_index);
//                }
//                int test_group_index = getIdOfDurchlaufgegenstand(testgroup, testgroup);
//                try {
//                    delete_testcases.setInt(1, test_group_index);
//                    delete_testcases.executeUpdate();
//                    deletedTestGroupIndex.add(test_group_index);
//                } catch (PSQLException ex) {
//                    System.out.println("wird noch verwendet");
//                }
//            }
//            int project_index = getIdOfDurchlaufgegenstand(pr, pr);
//            try {
//                delete_testgruppen.setInt(1, project_index);
//                delete_testgruppen.executeUpdate();
//
//                delete_project.setInt(1, project_index);
//                delete_project.executeUpdate();
//                deletedProjectIndex.add(project_index);
//            } catch (Exception ex) {
//                System.out.println("Wird noch verwendet");
//            }
//        }
//        deleteListIndex(deletedCommandIndex);
//        deleteListIndex(deletedTestCaseIndex);
//        deleteListIndex(deletedTestGroupIndex);
//        deleteListIndex(deletedProjectIndex);
//    }
//
//    private void deleteListIndex(List<Integer> allToDelete) throws SQLException {
////        if (delete_durchlaufgegenstand == null) {
////            delete_durchlaufgegenstand = database.getConnection().prepareStatement(DELETE_DURCHLAUFGEGENSTAND);
////        }
//
////        for (Integer to_delete : allToDelete) {
////            delete_durchlaufgegenstand.setInt(1, to_delete);
////            delete_durchlaufgegenstand.executeUpdate();
////        }
//        for (Integer to_delete : allToDelete) {
//            removeEntry(to_delete);
//        }
//    }
//
//    public int getIdOfDurchlaufgegenstand(ExplorerLayer old, ExplorerLayer newItem) throws SQLException {
//        if (get_id_of_command == null || get_id_of_projekt == null || get_id_of_testcase == null || get_id_of_testgruppe == null) {
//            get_id_of_command = database.getConnection().prepareStatement(GET_ID_OF_COMMAND);
//            get_id_of_projekt = database.getConnection().prepareStatement(GET_ID_OF_PROJEKT);
//            get_id_of_testcase = database.getConnection().prepareStatement(GET_ID_OF_TESTCASE);
//            get_id_of_testgruppe = database.getConnection().prepareStatement(GET_ID_OF_TESTGRUPPE);
//        }
//        ResultSet rs = null;
//        List<ProjectRun> allProjects = new LinkedList<>();
//        List<TestGroupRun> allTGs = new LinkedList<>();
//        List<TestCaseRun> allTCs = new LinkedList<>();
//
//        for (ProjectRun pr : GlobalParamter.getInstance().getWorkingProjects()) {
//            allProjects.add(pr);
//            for (TestGroupRun testgroup : pr.getTestgroups()) {
//                allTGs.add(testgroup);
//                for (TestCaseRun testCase : testgroup.getTestCases()) {
//                    allTCs.add(testCase);
//                }
//            }
//        }
//        if (old instanceof ProjectRun) {
//            get_id_of_projekt.setString(1, old.getDescription());
//            rs = get_id_of_projekt.executeQuery();
//        } else if (old instanceof TestGroupRun) {
//            get_id_of_testgruppe.setString(1, old.getDescription());
//            for (ProjectRun project : allProjects) {
//                if (project.getTestgroups().contains((TestGroupRun) newItem)) {
//                    get_id_of_testgruppe.setInt(2, getIdOfDurchlaufgegenstand(project, project));
//                }
//            }
//            rs = get_id_of_testgruppe.executeQuery();
//        } else if (old instanceof TestCaseRun) {
//            get_id_of_testcase.setString(1, old.getDescription());
//            for (TestGroupRun tg : allTGs) {
//                if (tg.getTestCases().contains((TestCaseRun) newItem)) {
//                    get_id_of_testcase.setInt(2, getIdOfDurchlaufgegenstand(tg, tg));
//                }
//            }
//            rs = get_id_of_testcase.executeQuery();
//        } else if (old instanceof CommandRun) {
//            get_id_of_command.setString(1, old.getDescription());
//
//            for (TestCaseRun tc : allTCs) {
//                if (tc.getCommands().contains((CommandRun) newItem)) {
//                    get_id_of_command.setInt(2, getIdOfDurchlaufgegenstand(tc, tc));
//                }
//            }
//            rs = get_id_of_command.executeQuery();
//        }
//        rs.next();
//        return rs.getInt(1);
//    }
//
//    public int getIdOfCommand(ExplorerLayer item, TestCaseRun tc) throws SQLException {
//        if (get_id_of_command == null) {
//            get_id_of_command = database.getConnection().prepareStatement(GET_ID_OF_COMMAND);
//        }
//        int testcase_index = getIdOfDurchlaufgegenstand(tc, tc);
//
//        get_id_of_command.setString(1, item.getDescription());
//        get_id_of_command.setInt(2, testcase_index);
//        ResultSet rs = get_id_of_command.executeQuery();
//        rs.next();
//        return rs.getInt(1);
//    }
//
//    private boolean durchlaufItemExists(ExplorerLayer item, ExplorerLayer parent) throws SQLException {
//        ResultSet rs = null;
//        if (item instanceof TestGroupRun) {
//            Statement statement = database.getStatement();
//            rs = statement.executeQuery("SELECT bezeichnung,p.gegenstandid FROM TESTGRUPPE t INNER JOIN DURCHLAUFGEGENSTAND d ON d.gegenstandid = t.gegenstandid "
//                    + "INNER JOIN PROJEKT p ON p.gegenstandid = t.projektid");
//        } else if (item instanceof TestCaseRun) {
//            Statement statement = database.getStatement();
//            rs = statement.executeQuery("SELECT bezeichnung,tg.gegenstandid FROM TESTCASE t INNER JOIN DURCHLAUFGEGENSTAND d ON d.gegenstandid = t.gegenstandid "
//                    + "INNER JOIN TESTGRUPPE tg ON tg.gegenstandid = t.TESTGRUPPEID");
//        } else if (item instanceof CommandRun) {
//            Statement statement = database.getStatement();
//            rs = statement.executeQuery("SELECT bezeichnung,tc.gegenstandid FROM COMMAND c INNER JOIN DURCHLAUFGEGENSTAND d ON d.gegenstandid = c.gegenstandid "
//                    + "INNER JOIN TESTCASE tc ON tc.gegenstandid = c.TESTCASEID");
//        }
//        while (rs.next()) {
//            if (rs.getString(1).equalsIgnoreCase(item.getDescription())) {
//                if (rs.getInt(2) == getIdOfDurchlaufgegenstand(parent, parent)) {
//                    return true;
//                }
//            }
//        }
//        return false;
//    }
//
//    public void addCommand(CommandRun command, int index_testcase) throws SQLException {
//        if (insert_durchlaufitem == null || insert_command == null) {
//            insert_durchlaufitem = database.getConnection().prepareStatement(INSERT_DURCHLAUFITEM);
//            insert_command = database.getConnection().prepareStatement(INSERT_COMMAND);
//        }
//
//        insert_durchlaufitem.setString(1, command.getDescription());
//        insert_durchlaufitem.setDate(2, java.sql.Date.valueOf(LocalDate.now()));
//        insert_durchlaufitem.setInt(3, 0);
//        ResultSet rs = insert_durchlaufitem.executeQuery();
//        rs.next();
//
//        int index = rs.getInt(1);
//        insert_command.setInt(1, index);
//        insert_command.setInt(2, index_testcase);
//        insert_command.executeUpdate();
//
//    }
//
//    public int addTestGroup(TestGroupRun tg, int index_project) throws SQLException {
//        if (insert_durchlaufitem == null || insert_testgruppe == null) {
//            insert_durchlaufitem = database.getConnection().prepareStatement(INSERT_DURCHLAUFITEM);
//            insert_testgruppe = database.getConnection().prepareStatement(INSERT_TESTGRUPPE);
//        }
//
//        insert_durchlaufitem.setString(1, tg.getDescription());
//        insert_durchlaufitem.setDate(2, java.sql.Date.valueOf(LocalDate.now()));
//        insert_durchlaufitem.setInt(3, 0);
//        ResultSet rs = insert_durchlaufitem.executeQuery();
//        rs.next();
//
//        int index = rs.getInt(1);
//        insert_testgruppe.setInt(1, index);
//        insert_testgruppe.setInt(2, index_project);
//        insert_testgruppe.executeUpdate();
//
//        return index;
//    }
//
//    public int addTestCase(TestCaseRun tc, int index_testgroup) throws SQLException {
//        if (insert_durchlaufitem == null || insert_testcase == null) {
//            insert_durchlaufitem = database.getConnection().prepareStatement(INSERT_DURCHLAUFITEM);
//            insert_testcase = database.getConnection().prepareStatement(INSERT_TESTCASE);
//        }
//
//        insert_durchlaufitem.setString(1, tc.getDescription());
//        insert_durchlaufitem.setDate(2, java.sql.Date.valueOf(LocalDate.now()));
//        insert_durchlaufitem.setInt(3, 0);
//        ResultSet rs = insert_durchlaufitem.executeQuery();
//        rs.next();
//
//        int index = rs.getInt(1);
//        insert_testcase.setInt(1, index);
//        insert_testcase.setInt(2, index_testgroup);
//        insert_testcase.executeUpdate();
//
//        return index;
//    }
//
//    public int addProject(ProjectRun pr) throws SQLException {
//        if (insert_durchlaufitem == null || insert_project == null) {
//            insert_durchlaufitem = database.getConnection().prepareStatement(INSERT_DURCHLAUFITEM);
//            insert_project = database.getConnection().prepareStatement(INSERT_PROJECT);
//        }
//
//        insert_durchlaufitem.setString(1, pr.getDescription());
//        insert_durchlaufitem.setDate(2, java.sql.Date.valueOf(LocalDate.now()));
//        insert_durchlaufitem.setInt(3, 0);
//        ResultSet rs = insert_durchlaufitem.executeQuery();
//        rs.next();
//
//        int index = rs.getInt(1);
//        insert_project.setInt(1, index);
//        insert_project.executeUpdate();
//
//        return index;
//    }
//
//    public Nutzer addUser(String username) throws SQLException {
//        if (insert_user == null) {
//            insert_user = database.getConnection().prepareStatement(INSERT_USER);
//        }
//        insert_user.setString(1, username);
//        ResultSet rs = insert_user.executeQuery();
//        rs.next();
//        int user_id = rs.getInt(1);
//
//        return new Nutzer(user_id, username);
//    }
//
//    public void createRun(Durchlauf d, List<ExplorerLayer> allItems) throws SQLException {
//        if (insert_run == null || insert_run_runitem == null) {
//            insert_run = database.getConnection().prepareStatement(INSERT_RUN);
//            insert_run_runitem = database.getConnection().prepareStatement(INSERT_RUN_RUNITEM);
//        }
//        insert_run.setDate(1, java.sql.Date.valueOf(LocalDate.now()));
//        insert_run.setInt(2, d.getAnzahl());
//        insert_run.setInt(3, d.getErfolgreich());
//        insert_run.setInt(4, d.getFehlgeschlagen());
//        insert_run.setInt(5, 0);
//        insert_run.setInt(6, d.getNutzerId());
//
//        ResultSet rs = insert_run.executeQuery();
//        rs.next();
//        int durchlauf_id = rs.getInt(1);
//        for (ExplorerLayer item : allItems) {
//            int durchlaufgegenstand_index = getIdOfDurchlaufgegenstand(item, item);
//            insert_run_runitem.setInt(1, durchlauf_id);
//            insert_run_runitem.setInt(2, durchlaufgegenstand_index);
//            insert_run_runitem.executeUpdate();
//        }
//        GlobalParamter.getInstance().getCurrentRun().setDurchlaufId(durchlauf_id);
//    }
//
//    public boolean checkIfProjectExists(ProjectRun pr) throws SQLException {
//        CallableStatement cs = database.getConnection().prepareCall("{ ? = call check_if_project_exists(?)}");
//        cs.registerOutParameter(1, Types.INTEGER);
//        cs.setString(2, pr.getDescription());
//        cs.execute();
//        return cs.getInt(1) == 1;
//    }
//
//    public List<Durchlaufgegenstand> selectDurchlaufitems() throws SQLException {
//        List<Durchlaufgegenstand> allDurchlaufItems = new LinkedList<>();
//        Statement statement = database.getStatement();
//        ResultSet rs = statement.executeQuery("SELECT * FROM DURCHLAUFGEGENSTAND");
//        while (rs.next()) {
//            allDurchlaufItems.add(new Durchlaufgegenstand(rs.getInt("gegenstandid"), rs.getString("bezeichnung"),
//                    rs.getTimestamp("erstelldatum").toLocalDateTime().toLocalDate(), 0));
//        }
//        return allDurchlaufItems;
//    }
//
//    private Change createChange(ResultSet rs) throws SQLException {
//        int nutzer_id = rs.getInt("nutzerid");
//        int gegenstand_id = rs.getInt("gegenstandid");
//        LocalDateTime change_date = rs.getTimestamp("changedate").toLocalDateTime();
//        int type = rs.getInt("type");
//        return new Change(nutzer_id, gegenstand_id, change_date, type);
//    }
//
//    private Durchlauf createDurchlauf(ResultSet rs) throws SQLException {
//        int durchlauf_id = rs.getInt("durchlaufid");
//        LocalDateTime durchlauf_datum = rs.getTimestamp("durchlaufdatum").toLocalDateTime();
//        int anzahl = rs.getInt("anzahl");
//        int erfolgreich = rs.getInt("erfolgreich");
//        int fehlgeschlagen = rs.getInt("fehlgeschlagen");
//        int uebernahmeanz = rs.getInt("uebernahmeanz");
//        int nutzerid = rs.getInt("nutzerid");
//
//        return new Durchlauf(durchlauf_id, durchlauf_datum, anzahl, erfolgreich, fehlgeschlagen, uebernahmeanz, nutzerid);
//    }
//
//    private void deleteMissingEntities(ProjectRun pr) throws SQLException {
//        if (select_all_tg_of_projekt == null || select_all_tc_of_projekt == null || select_all_commands_of_projekt == null) {
//            select_all_tg_of_projekt = database.getConnection().prepareStatement(SELECT_ALL_TG_OF_PROJECT);
//            select_all_tc_of_projekt = database.getConnection().prepareStatement(SELECT_ALL_TC_OF_PROJECT);
//            select_all_commands_of_projekt = database.getConnection().prepareStatement(SELECT_ALL_COMMANDS_OF_PROJECT);
//        }
//        int project_index = getIdOfDurchlaufgegenstand(pr, pr);
//        select_all_commands_of_projekt.setInt(1, project_index);
//        ResultSet command_resultSet = select_all_commands_of_projekt.executeQuery();
//        select_all_tc_of_projekt.setInt(1, project_index);
//        ResultSet testcase_resultSet = select_all_tc_of_projekt.executeQuery();
//        select_all_tg_of_projekt.setInt(1, project_index);
//        ResultSet testgroup_resultSet = select_all_tg_of_projekt.executeQuery();
//        boolean help = false;
//        List<String> allCommands = new LinkedList<>();
//        List<String> allTestCases = new LinkedList<>();
//        List<String> allTestGroups = new LinkedList<>();
//        for (TestGroupRun testgroup : pr.getTestgroups()) {
//            allTestGroups.add(testgroup.getDescription());
//            for (TestCaseRun testcase : testgroup.getTestCases()) {
//                allTestCases.add(testcase.getDescription());
//                for (CommandRun command : testcase.getCommands()) {
//                    allCommands.add(command.getDescription());
//                }
//            }
//        }
//        while (command_resultSet.next()) {
//            if (!allCommands.contains(command_resultSet.getString(1))) {
//                removeEntry(command_resultSet.getInt(2));
//            }
//        }
//
//        while (testcase_resultSet.next()) {
//            if (!allTestCases.contains(testcase_resultSet.getString(1))) {
//                removeEntry(testcase_resultSet.getInt(2));
//            }
//        }
//
//        while (testgroup_resultSet.next()) {
//            if (!allTestGroups.contains(testgroup_resultSet.getString(1))) {
//                removeEntry(testgroup_resultSet.getInt(2));
//            }
//        }
//    }
//
//    private final String SELECT_CHANGES_PER_USER = "SELECT * FROM CHANGEHISTORY WHERE changedate BETWEEN ? AND ? AND nutzerid = ?";
//    private PreparedStatement select_changes_per_user = null;
//
//    private final String SELECT_CHANGES_DATE = "SELECT * FROM CHANGEHISTORY WHERE changedate BETWEEN ? AND ?";
//    private PreparedStatement select_changes_date = null;
//
//    private final String SELECT_RUNS_PER_USER = "SELECT * FROM DURCHLAUF WHERE DURCHLAUFDATUM BETWEEN ? AND ? AND nutzerid = ?";
//    private PreparedStatement select_runs_per_user = null;
//
//    private final String SELECT_RUNS_DATE = "SELECT * FROM DURCHLAUF WHERE DURCHLAUFDATUM BETWEEN ? AND ?";
//    private PreparedStatement select_runs_date = null;
//
//    private final String INSERT_DURCHLAUFITEM = "INSERT INTO DURCHLAUFGEGENSTAND (BEZEICHNUNG, ERSTELLDATUM, DELETED) "
//            + "VALUES (?,?,?) RETURNING GEGENSTANDID;";
//    private PreparedStatement insert_durchlaufitem = null;
//
//    private final String INSERT_PROJECT = "INSERT INTO PROJEKT VALUES (?)";
//    private PreparedStatement insert_project = null;
//
//    private final String INSERT_TESTCASE = "INSERT INTO TESTCASE VALUES (?,?)";
//    private PreparedStatement insert_testcase = null;
//
//    private final String INSERT_TESTGRUPPE = "INSERT INTO TESTGRUPPE VALUES (?,?)";
//    private PreparedStatement insert_testgruppe = null;
//
//    private final String INSERT_COMMAND = "INSERT INTO COMMAND VALUES (?,?)";
//    private PreparedStatement insert_command = null;
//
//    private final String INSERT_USER = "INSERT INTO NUTZER (username) VALUES (?) RETURNING NUTZERID";
//    private PreparedStatement insert_user = null;
//
//    private final String UPDATE_DURCHLAUF = "UPDATE DURCHLAUF SET UEBERNAHMEANZ = ? WHERE DURCHLAUFID=?";
//    private PreparedStatement update_durchlauf = null;
//
//    private final String INSERT_CHANGE_HISTORY_ENTRY = "INSERT INTO CHANGEHISTORY VALUES(?,?,?,?)";
//    private PreparedStatement insert_change_history_entry = null;
//
//    private final String UPDATE_DESCRIPTION = "UPDATE DURCHLAUFGEGENSTAND SET BEZEICHNUNG = ? WHERE GEGENSTANDID=?";
//    private PreparedStatement update_description = null;
//
//    private final String UPDATE_DELETED_TRUE = "UPDATE DURCHLAUFGEGENSTAND SET DELETED = 1 WHERE GEGENSTANDID = ?";
//    private PreparedStatement update_deleted = null;
//
//    private final String UPDATE_PARENT_TG = "UPDATE TESTGRUPPE SET PROJEKTID = ? WHERE GEGENSTANDID = ?";
//    private PreparedStatement update_parent_tg = null;
//
//    private final String UPDATE_PARENT_TC = "UPDATE TESTCASE SET TESTGRUPPEID = ? WHERE GEGENSTANDID = ?";
//    private PreparedStatement update_parent_tc = null;
//
//    private final String UPDATE_PARENT_COMMAND = "UPDATE COMMAND SET TESTCASEID = ? WHERE GEGENSTANDID = ?";
//    private PreparedStatement update_parent_command = null;
//
//    private final String INSERT_RUN = "INSERT INTO DURCHLAUF (DURCHLAUFDATUM,ANZAHL,ERFOLGREICH,FEHLGESCHLAGEN,UEBERNAHMEANZ,NUTZERID) VALUES(?,?,?,?,?,?) RETURNING DURCHLAUFID";
//    private PreparedStatement insert_run = null;
//
//    private final String INSERT_RUN_RUNITEM = "INSERT INTO DURCHLAUF_DURCHLAUFGEGENSTAND VALUES(?,?)";
//    private PreparedStatement insert_run_runitem = null;
//
//    private final String DELETE_PROJECT = "DELETE FROM PROJEKT WHERE GEGENSTANDID=?";
//    private PreparedStatement delete_project = null;
//
//    private final String DELETE_TESTCASES = "DELETE FROM TESTCASE WHERE TESTGRUPPEID=?";
//    private PreparedStatement delete_testcases = null;
//
//    private final String DELETE_TESTGRUPPEN = "DELETE FROM TESTGRUPPE WHERE PROJEKTID=?";
//    private PreparedStatement delete_testgruppen = null;
//
//    private final String DELETE_COMMANDS = "DELETE FROM COMMAND WHERE TESTCASEID=?";
//    private PreparedStatement delete_commands = null;
//
//    private final String DELETE_DURCHLAUFGEGENSTAND = "DELETE FROM DURCHLAUFGEGENSTAND WHERE GEGENSTANDID=?";
//    private PreparedStatement delete_durchlaufgegenstand = null;
//
//    private final String GET_ID_OF_PROJEKT = "SELECT d.GEGENSTANDID FROM DURCHLAUFGEGENSTAND d INNER JOIN PROJEKT p ON p.gegenstandid = d.gegenstandid WHERE d.BEZEICHNUNG = ?";
//    private PreparedStatement get_id_of_projekt = null;
//
//    private final String GET_ID_OF_TESTGRUPPE = "SELECT d.GEGENSTANDID FROM DURCHLAUFGEGENSTAND d INNER JOIN TESTGRUPPE t ON t.gegenstandid = d.gegenstandid "
//            + "INNER JOIN PROJEKT p ON p.gegenstandid = t.projektid WHERE d.BEZEICHNUNG = ? AND p.gegenstandid = ?";
//    private PreparedStatement get_id_of_testgruppe = null;
//
//    private final String GET_ID_OF_TESTCASE = "SELECT d.GEGENSTANDID FROM DURCHLAUFGEGENSTAND d INNER JOIN TESTCASE t ON t.gegenstandid = d.gegenstandid "
//            + "INNER JOIN TESTGRUPPE tg ON t.TESTGRUPPEID = tg.gegenstandid WHERE d.BEZEICHNUNG = ? AND tg.gegenstandid = ?";
//    private PreparedStatement get_id_of_testcase = null;
//
//    private final String GET_ID_OF_COMMAND = "SELECT d.GEGENSTANDID FROM DURCHLAUFGEGENSTAND d INNER JOIN COMMAND c ON c.gegenstandid = d.gegenstandid"
//            + " INNER JOIN TESTCASE tc ON c.testcaseid = tc.gegenstandid WHERE d.BEZEICHNUNG = ? AND tc.gegenstandid = ?";
//    private PreparedStatement get_id_of_command = null;
//
//    private final String SELECT_ALL_TG_OF_PROJECT = "SELECT bezeichnung,tg.gegenstandid FROM TESTGRUPPE tg INNER JOIN DURCHLAUFGEGENSTAND d ON d.gegenstandid = tg.gegenstandid WHERE PROJEKTID = ?";
//    private PreparedStatement select_all_tg_of_projekt = null;
//
//    private final String SELECT_ALL_TC_OF_PROJECT = "SELECT bezeichnung,t.gegenstandid FROM TESTCASE t INNER JOIN TESTGRUPPE tg ON tg.gegenstandid = t.TESTGRUPPEID "
//            + "INNER JOIN DURCHLAUFGEGENSTAND d ON d.gegenstandid = t.gegenstandid WHERE tg.projektid=?";
//    private PreparedStatement select_all_tc_of_projekt = null;
//
//    private final String SELECT_ALL_COMMANDS_OF_PROJECT = "SELECT bezeichnung,c.gegenstandid FROM COMMAND c INNER JOIN TESTCASE t ON c.TESTCASEID = t.gegenstandid "
//            + "INNER JOIN TESTGRUPPE tg ON tg.gegenstandid = t.TESTGRUPPEID "
//            + "INNER JOIN DURCHLAUFGEGENSTAND d ON d.gegenstandid = t.gegenstandid WHERE tg.projektid=?";
//    private PreparedStatement select_all_commands_of_projekt = null;

}
