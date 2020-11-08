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
import simulator.beans.Kasse;

/**
 *
 * @author Lukas Krobath
 */
public class TableModelKasse extends AbstractTableModel {

    private String[] colNames = {"Kassen IP", "Kassen ID", "Kassengruppe", "Typ"};

    private List<Kasse> values = new ArrayList<>();

    public TableModelKasse(List<Kasse> values) {
        this.values = values;

    }

    @Override
    public int getRowCount() {
        return values.size();
    }

    @Override
    public int getColumnCount() {
        return colNames.length;
    }

    public void addElement(Kasse kassa) {
        values.add(kassa);
        fireTableDataChanged();
        try {
            Saver.saveCashpoints();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void removeElement(int index) {
        values.remove(index);
        fireTableDataChanged();
        try {
            Saver.saveCashpoints();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case 0:
                return values.get(rowIndex).getStrIpAdr();
            case 1:
                return values.get(rowIndex).getiRegId();
            case 2:
                return values.get(rowIndex).getiRegGrp();
            case 3:
                return values.get(rowIndex).getType();
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
                return Integer.class;
            case 2:
                return Integer.class;
            case 3:
                return Kasse.TYP.class;
        }
        return String.class;
    }


}
