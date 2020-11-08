/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package settings.bl;

import general.io.Saver;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import simulator.beans.Backoffice;

/**
 *
 * @author Lukas Krobath
 */
public class TableModelBackoffice extends AbstractTableModel {

    private String[] colNames = {"Backoffice IP"};

    private List<Backoffice> values = new ArrayList<>();

    public TableModelBackoffice(List<Backoffice> values) {
        this.values = values;
    }
    
    public void addElement(Backoffice back) {
        values.add(back);
        fireTableDataChanged();
        try {
            Saver.saveBackoffices();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void removeElement(int index) {
        values.remove(index);
        fireTableDataChanged();
        try {
            Saver.saveBackoffices();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public int getRowCount() {
        return values.size();
    }

    @Override
    public int getColumnCount() {
        return colNames.length;
    }


    
    
     @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case 0:
                return values.get(rowIndex).getStrIpAdr();
           
        }
        return "Error";
    }

    @Override
    public String getColumnName(int column) {
        return colNames[column];
    }
    
    
    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return String.class;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

}
