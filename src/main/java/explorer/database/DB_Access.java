package explorer.database;

//import com.sun.corba.se.impl.orb.PrefixParserData;
import java.sql.*;
import java.util.LinkedList;
import java.util.List;

/**
 * Class to issue SQL statements
 * @author Florian Deutschmann
 */
public class DB_Access {

    private static DB_Access theInstance = null;

    public static DB_Access getInstance() {
        if (theInstance == null) {
            theInstance = new DB_Access();
        }
        return theInstance;
    }
    private DB_Database database = null;

    private DB_Access() {
        try {
            database = DB_Database.getInstance();
        } catch (ClassNotFoundException | SQLException e) {
           e.printStackTrace();
        }
    }

    /**
     * Method to retrieve all available tables
     * @param filter
     * @return 
     */
    public List<String> selectAllTables(String filter) {
        List<String> allTables = new LinkedList<>();
        try {
            if (select_all_tables_in_scheme == null) {
                select_all_tables_in_scheme = database.getConnection().prepareStatement(SELECT_ALL_TABLES_IN_SCHEME);
            }
            String tmp_filter = "%" + filter.toUpperCase() + "%";
            System.out.println(tmp_filter);
            select_all_tables_in_scheme.setString(1, tmp_filter);
            ResultSet result = select_all_tables_in_scheme.executeQuery();
            while (result.next()) {
                String table = result.getString("TABNAME");
                allTables.add(table);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return allTables;
    }

    /**
     * Method to retrieve all fields of a given table
     * @param table
     * @return 
     */
    public List<String> selectAllFieldsOfTable(String table) {
        List<String> allFieldsOfTable = new LinkedList<>();
        try {
            if (select_all_fields_from_table == null) {

                select_all_fields_from_table = database.getConnection().prepareStatement(SELECT_ALL_FIELDS_FROM_TABLE);

            }

            select_all_fields_from_table.setString(1, table);
            ResultSet result = select_all_fields_from_table.executeQuery();
            while (result.next()) {
                String field = result.getString("COLNAME");
                allFieldsOfTable.add(field);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return allFieldsOfTable;
    }

    /**
     * Method to connect to the database
     * @throws SQLException 
     */
    public void connect() throws SQLException {
        database.connect();
        select_all_tables_in_scheme = null;
        select_all_fields_from_table = null;
    }

    /**
     * Method to connect to the database with a table name
     * @param table_name
     * @throws Exception 
     */
    public void connect(String table_name) throws Exception {
        database.connect(table_name);
        select_all_tables_in_scheme = null;
        select_all_fields_from_table = null;
    }

    /**
     * Method to disconnect from the database
     * @throws SQLException 
     */
    public void disconnect() throws SQLException {
        database.disconnect();
    }

    private final String SELECT_ALL_TABLES_IN_SCHEME = "SELECT * FROM SYSCAT.TABLES WHERE tabschema = 'NPOS' AND TABNAME LIKE ?";
    private PreparedStatement select_all_tables_in_scheme = null;

    private final String SELECT_ALL_FIELDS_FROM_TABLE = "SELECT * FROM SYSCAT.COLUMNS WHERE TABNAME=?";
    private PreparedStatement select_all_fields_from_table = null;
}
