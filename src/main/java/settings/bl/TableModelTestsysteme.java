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
import remote.bl.RemoteExecutionManager;
import simulator.beans.Testsystem;
import simulator.bl.ExecutionManager;

/**
 *
 * @author Lukas Krobath
 */
public class TableModelTestsysteme extends AbstractTableModel {

    private String[] colNames = {"Aktiv", "Bezeichnung", "Systemkonfiguration"};

    private List<Testsystem> values = new ArrayList<>();

    public TableModelTestsysteme(List<Testsystem> values) {
        this.values = values;
    }

    public void addElement(Testsystem test) {
        values.add(test);
        fireTableDataChanged();

        RemoteExecutionManager.getInstance().getPanel().updateTestsysteme();
        try {
            Saver.saveTestsysteme();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void removeElement(int index) {
        values.remove(index);
        fireTableDataChanged();
        RemoteExecutionManager.getInstance().getPanel().updateTestsysteme();
        try {
            Saver.saveTestsysteme();
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
                return values.get(rowIndex).isActive();
            case 1:
                return values.get(rowIndex).getName();

            case 2:
                return values.get(rowIndex).getConfig();

        }
        return "Error";
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if (columnIndex == 0) {
            for (Testsystem value : values) {
                value.setActive(false);
            }
            values.get(rowIndex).setActive((Boolean) aValue);
            if ((Boolean) aValue) {
                ExecutionManager.getInstance().setActiveSystem(values.get(rowIndex));
            } else {
                ExecutionManager.getInstance().setActiveSystem(null);
            }
        } else if (columnIndex == 1) {
            values.get(rowIndex).setName((String) aValue);
        }
        fireTableDataChanged();
        try {
            Saver.saveTestsysteme();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public String getColumnName(int column) {
        return colNames[column];
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        if (columnIndex == 0) {
            return Boolean.class;
        }
        return String.class;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex < 2;
    }

}
