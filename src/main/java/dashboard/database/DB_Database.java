package dashboard.database;

import general.bl.GlobalParamter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Class to set all necessary infos for the database
 *
 * @author Florian Deutschmann
 */
public class DB_Database {

    private Connection connection;
    private String db_url = null;
    private String db_databasename = null;
    private String db_username = null;
    private String db_password = null;
    private String db_driver = null;
    private Boolean done = false;
    private DB_CachedConnection cc;

    private static DB_Database theInstance = null;
//baut verbindung auf

    public static DB_Database getInstance() throws ClassNotFoundException, SQLException {
        if (theInstance == null) {
            theInstance = new DB_Database();
        }
        return theInstance;
    }

    private DB_Database() throws ClassNotFoundException, SQLException {
        loadProperties();
        Class.forName(db_driver);
        try {
            connect();
            cc = new DB_CachedConnection(connection);
            //GlobalParamter.getInstance().setStatistic_db_reachable(true);
        } catch (Exception ex) {
            //GlobalParamter.getInstance().setStatistic_db_reachable(false);
        }
    }

    public Connection getConnection() {
        return connection;
    }

    /**
     * Method to get a statement
     *
     * @return
     * @throws SQLException
     */
    public Statement getStatement() throws SQLException {
        if (cc == null) {
            throw new RuntimeException("no statements available");
        }
        return cc.getStatement();
    }

    /**
     * Method to release a statement
     *
     * @param statement
     */
    public void releaseStatement(Statement statement) {
        if (cc == null) {
            throw new RuntimeException("no statements available");
        }
        cc.releaseStatement(statement);
    }

    /**
     * Method to disconnect from the database
     *
     * @throws SQLException
     */
    public void disconnect() throws SQLException {
        connection.close();
    }

    /**
     * Method to connect to the database
     *
     * @throws SQLException
     */
    public void connect() throws SQLException {
        if (connection != null) {
            connection.close();
        }
        connection = DriverManager.getConnection(db_url + db_databasename, db_username, db_password);
    }

    /**
     * Method to load the values from the .properties file
     */
    private void loadProperties() {
        db_url = DB_Properties.getProperty("url");
        db_databasename = DB_Properties.getProperty("databasename");
        db_username = DB_Properties.getProperty("username");
        db_password = DB_Properties.getProperty("password");
        db_driver = DB_Properties.getProperty("driver");
    }

    public static void main(String[] args) {
        try {
            DB_Database.getInstance();
        } catch (ClassNotFoundException e) {
            System.out.println(e.toString());
        } catch (SQLException e) {
            System.out.println(e.toString());
        }
    }
}
