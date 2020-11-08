/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package explorer.beans;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Florian Deutschmann
 */
public class Database {
    private String databaseName;
    private List<Table> allTables = new LinkedList<>();

    public Database(String databaseName, List<Table> allTables) {
        this.databaseName = databaseName;
        this.allTables = allTables;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String DatabaseName) {
        this.databaseName = DatabaseName;
    }

    public List<Table> getAllTables() {
        return allTables;
    }

    public void setAllTables(List<Table> allTables) {
        this.allTables = allTables;
    }

    @Override
    public String toString() {
        return databaseName;
    }    
}
