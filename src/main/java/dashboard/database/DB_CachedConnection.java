package dashboard.database;


import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;

/**
 * Class to provide or release statements
 * @author Florian Deutschmann
 */
public class DB_CachedConnection {
    private Connection connection;
    private LinkedList<Statement> stmtQueue = new LinkedList<>();

    public DB_CachedConnection(Connection connection){
        this.connection = connection;
    }

    /**
     * Method to get a statement
     * @return
     * @throws SQLException 
     */
    public synchronized Statement getStatement() throws SQLException {
        if(connection == null){
            throw new RuntimeException("not connected");
        }
        if(stmtQueue.isEmpty()){
            return connection.createStatement();
        }
        return stmtQueue.poll(); //nimmt was raus aus der Que, es erste
    }

    /**
     * Method to release a statement
     * @param statement 
     */
    public synchronized void releaseStatement(Statement statement){
        if(connection == null){
            throw new RuntimeException("not connected");
        }
        stmtQueue.offer(statement);
    }
}
