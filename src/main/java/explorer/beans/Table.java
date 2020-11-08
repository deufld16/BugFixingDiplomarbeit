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
public class Table {
    private String tableName;
    private List<Field> allFields = new LinkedList<>();

    public Table(String tableName, List<Field> allFields) {
        this.tableName = tableName;
        this.allFields = allFields;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public List<Field> getAllFields() {
        return allFields;
    }

    public void setAllFields(List<Field> allFields) {
        this.allFields = allFields;
    }

    @Override
    public String toString() {
        return tableName;
    }
}
