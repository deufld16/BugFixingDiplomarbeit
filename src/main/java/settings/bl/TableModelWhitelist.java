/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package settings.bl;

import analyzer.beans.WhitelistEntry;
import analyzer.enums.ResultFileType;
import general.bl.GlobalParamter;
import general.io.Loader;
import general.io.Saver;
import java.util.LinkedList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 * Model class for the management of all configured whitelist entries
 * 
 * @author Maximilian Strohmaier
 */
public class TableModelWhitelist extends AbstractTableModel {
     private String[] colNames = {"Bezeichnung", "Suchmuster", "Anwendung"};

    private List<WhitelistEntry> values = new LinkedList<>();

    public TableModelWhitelist() {
        if(!Loader.isWhitelistLoaded()) {
            Loader.loadWhitelistXml();
        }
        this.values = GlobalParamter.getInstance().getWhitelistEntries();
    }

    @Override
    public int getRowCount() {
        return values.size();
    }

    @Override
    public int getColumnCount() {
        return colNames.length;
    }
    
    /***
     * Method to receive a specific Whitelist-Entry identified by its index
     * 
     * @param index  position of the Whitelist-Entry
     * @return  Whitelist-Entry at index
     */
    public WhitelistEntry getElementAt(int index) {
        return values.get(index);
    }

    /***
     * Method to add a Whitelist-Entry
     * 
     * @param entry  entry to add
     */
    public void addElement(WhitelistEntry entry) {
        values.add(entry);
        GlobalParamter.getInstance().setWhitelistEntries(values);
        fireTableDataChanged();
        Saver.saveWhitelistXml();
    }
    
    /**
     * Method to set an entry at a specific position
     * 
     * @param entry  entry to set
     * @param position  position of entry within the table
     */
    public void setEntry(WhitelistEntry entry, int position) {
        values.set(position, entry);
        GlobalParamter.getInstance().setWhitelistEntries(values);
        fireTableDataChanged();
        Saver.saveWhitelistXml();
    }

    /***
     * Method to remove a Whitelist-Entry
     * 
     * @param entries  entry to remove
     */
    public void removeElements(List<WhitelistEntry> entries) {
        values.removeAll(entries);
        GlobalParamter.getInstance().setWhitelistEntries(values);
        fireTableDataChanged();
        Saver.saveWhitelistXml();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case 0:
                return values.get(rowIndex).getDescription();
            case 1:
                return values.get(rowIndex).getRegex();
            case 2:
                String str = "";
                List<ResultFileType> types = values.get(rowIndex).getApplicationTypes();
                for (int i = 0; i < types.size(); i++) {
                    str += types.get(i) + ((i == (types.size() - 1)) ? "" : ", ");
                }
                return str;
        }
        return "Error";
    }

    @Override
    public String getColumnName(int column) {
        return colNames[column];
    }

    @Override
    public Class<?> getColumnClass(int column) {
        switch (column) {
            case 0:
                return String.class;
            case 1:
                return String.class;
        }
        return String.class;
    }

}
