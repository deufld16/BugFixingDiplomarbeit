/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dashboard.gui;

import dashboard.beans.Change;
import dashboard.beans.ChangeNew;
import dashboard.beans.Nutzer;
import dashboard.database.DB_Access;
import dashboard.beans.Durchlauf;
import dashboard.beans.DurchlaufNew;
import dashboard.beans.NutzerNew;
import dashboard.bl.DatabaseGlobalAccess;
import dashboard.database.DB_Access_Manager;
import general.bl.GlobalParamter;
import java.awt.Color;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import org.knowm.xchart.CategoryChart;
import org.knowm.xchart.CategoryChartBuilder;
import org.knowm.xchart.PieChartBuilder;
import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.style.Styler;
import simulator.bl.ExecutionManager;

/**
 *
 * @author Maximilian Strohmaier
 */
public class DashboardPanel extends javax.swing.JPanel {

    private LocalDate startDate = LocalDate.now();
    private LocalDate endDate = LocalDate.now();

    private DefaultComboBoxModel dcbmHistory;
    private DefaultComboBoxModel dcbmUser;
    private static final Color[] SLICECOLOR_DURCH = new Color[]{new Color(204, 10, 10), new Color(28, 163, 39), new Color(0, 0, 255)};
    private static final Color[] SLICECOLOR_AENDERUNG = new Color[]{new Color(204, 10, 10),new Color(255, 174, 0),new Color(66, 135, 245),new Color(187, 0, 255), new Color(28, 163, 39)};
//    chart.addSeries("Entfernt", getXDisplay(getXAxisData(startDate, endDate), daysBetween(startDate, endDate)), delete, null);
//                chart.addSeries("Verändert", getXDisplay(getXAxisData(startDate, endDate), daysBetween(startDate, endDate)), add, null);
//                chart.addSeries("Verschoben", getXDisplay(getXAxisData(startDate, endDate), daysBetween(startDate, endDate)), move, null);
//                chart.addSeries("Inhalt verändert", getXDisplay(getXAxisData(startDate, endDate), daysBetween(startDate, endDate)), state, null);
//                chart.addSeries("Hinzugefügt", getXDisplay(getXAxisData(startDate, endDate), daysBetween(startDate, endDate)), changed, null);
    private CategoryChart chart;
    private Random rand = new Random();
    public static final DateTimeFormatter dtfFull = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    /**
     * Creates new form StatisticsPanel
     */
    public DashboardPanel() {
        initComponents();

        chart = new CategoryChartBuilder().theme(Styler.ChartTheme.XChart).title("Statistik").build();
        chart.addSeries("Erfolgreich", new ArrayList<String>(Arrays.asList("a")), new ArrayList<Integer>(Arrays.asList(4)));
        chart.getStyler().setSeriesColors(SLICECOLOR_DURCH);
        paGraph.add(new XChartPanel(chart));

        dcbmHistory = new DefaultComboBoxModel();
        cbHistory.setModel(dcbmHistory);

        dcbmHistory.addElement("Durchlaufstatistik");
        dcbmHistory.addElement("Änderungsstatistik");

        refillUsers();

        Date now = new Date();
        LocalDate previousMonday = LocalDate.now(ZoneId.systemDefault()).with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate nextSunday = LocalDate.now(ZoneId.systemDefault()).with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        ftfEnd.setValue(Date.from(nextSunday.atStartOfDay(ZoneId.systemDefault()).toInstant()));
        ftfStart.setValue(Date.from(previousMonday.atStartOfDay(ZoneId.systemDefault()).toInstant()));

        startDate = previousMonday;
        endDate = nextSunday;
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        paSelection = new javax.swing.JPanel();
        paComboboxes = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        cbHistory = new javax.swing.JComboBox<>();
        cbUser = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        paDates = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        paDateStart = new javax.swing.JPanel();
        ftfStart = new javax.swing.JFormattedTextField();
        paDateEnd = new javax.swing.JPanel();
        ftfEnd = new javax.swing.JFormattedTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        paButton = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        btUpdate = new javax.swing.JButton();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        paGraph = new javax.swing.JPanel();

        setBackground(new java.awt.Color(255, 255, 255));
        setBorder(javax.swing.BorderFactory.createEmptyBorder(8, 8, 8, 8));
        setLayout(new java.awt.BorderLayout(0, 15));

        paSelection.setBackground(new java.awt.Color(255, 255, 255));
        paSelection.setLayout(new java.awt.GridLayout(3, 1, 0, 10));

        paComboboxes.setBackground(new java.awt.Color(255, 255, 255));
        paComboboxes.setLayout(new java.awt.GridLayout(1, 4, 10, 0));
        paComboboxes.add(jLabel1);

        cbHistory.setBackground(new java.awt.Color(120, 120, 120));
        cbHistory.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        cbHistory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onCbHistory(evt);
            }
        });
        paComboboxes.add(cbHistory);

        cbUser.setBackground(new java.awt.Color(120, 120, 120));
        cbUser.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        paComboboxes.add(cbUser);
        paComboboxes.add(jLabel2);

        paSelection.add(paComboboxes);

        paDates.setBackground(new java.awt.Color(255, 255, 255));
        paDates.setLayout(new java.awt.GridLayout(1, 6, 10, 0));
        paDates.add(jLabel3);
        paDates.add(jLabel4);

        paDateStart.setBackground(new java.awt.Color(255, 255, 255));
        paDateStart.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Startdatum:", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 14))); // NOI18N
        paDateStart.setLayout(new java.awt.BorderLayout());

        ftfStart.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.DateFormatter()));
        ftfStart.setText("27.08.2020");
        ftfStart.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        ftfStart.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                onEnterStartDate(evt);
            }
        });
        paDateStart.add(ftfStart, java.awt.BorderLayout.CENTER);

        paDates.add(paDateStart);

        paDateEnd.setBackground(new java.awt.Color(255, 255, 255));
        paDateEnd.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Enddatum:", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 14))); // NOI18N
        paDateEnd.setLayout(new java.awt.BorderLayout());

        ftfEnd.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.DateFormatter()));
        ftfEnd.setText("02.08.2020");
        ftfEnd.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        ftfEnd.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                onEnterStartDate(evt);
            }
        });
        paDateEnd.add(ftfEnd, java.awt.BorderLayout.CENTER);

        paDates.add(paDateEnd);
        paDates.add(jLabel5);
        paDates.add(jLabel6);

        paSelection.add(paDates);

        paButton.setBackground(new java.awt.Color(255, 255, 255));
        paButton.setLayout(new java.awt.GridLayout(1, 5, 10, 0));
        paButton.add(jLabel7);
        paButton.add(jLabel8);

        btUpdate.setBackground(new java.awt.Color(51, 51, 51));
        btUpdate.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        btUpdate.setForeground(new java.awt.Color(255, 255, 255));
        btUpdate.setText("Update");
        paButton.add(btUpdate);
        paButton.add(jLabel9);
        paButton.add(jLabel10);

        paSelection.add(paButton);

        add(paSelection, java.awt.BorderLayout.NORTH);

        paGraph.setBackground(new java.awt.Color(255, 255, 255));
        paGraph.setLayout(new java.awt.BorderLayout());
        add(paGraph, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void onEnterStartDate(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_onEnterStartDate
        JFormattedTextField ftf = (JFormattedTextField) evt.getSource();
        Date date = (Date) ftf.getValue();
        if (date != null) {
            if (ftf == ftfStart) {
                startDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            } else if (ftf == ftfEnd) {
                endDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            }
        }

        updateChart();
    }//GEN-LAST:event_onEnterStartDate

    private void onCbHistory(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onCbHistory
        updateChart();
    }//GEN-LAST:event_onCbHistory

     public synchronized void updateChart() {
        try {
            if (cbHistory.getSelectedItem().equals("Durchlaufstatistik")) {
                try {
                    chart.removeSeries("Erfolgreich");
                    chart.removeSeries("Fehlgeschlagen");
                    chart.removeSeries("Referenzen übernommen");
                } catch (Exception i) {
                }
                chart.getStyler().setSeriesColors(SLICECOLOR_DURCH);
                List<Integer> fail = new ArrayList<>();
                List<Integer> success = new ArrayList<>();
                List<Integer> refs = new ArrayList<>();

                List<LocalDate> dates = convertToLocalDate(convertToQuery(getXAxisData(startDate, endDate)));
                //System.out.println(dates + "aösdfasd");
                for (int i = 0; i < dates.size() - 1; i += 2) {
                    int tmpFail = 0;
                    int tmpSuccess = 0;
                    int tmpRefs = 0;
                    try {
                        if (((Nutzer) cbUser.getSelectedItem()).getNutzerid() == -1) {
                            //for (Durchlauf durch : DB_Access.getInstance().selectRun(dates.get(i), dates.get(i + 1))) {
                            for(DurchlaufNew durch : DB_Access_Manager.getInstance().selectRun(dates.get(i), dates.get(i + 1))){
                                tmpFail += durch.getFehlgeschlagen();
                                tmpSuccess += durch.getErfolgreich();
                                tmpRefs += durch.getUebernahmeAnz();
                            }
                        } else {
                            //for (Durchlauf durch : DB_Access.getInstance().selectRun(dates.get(i), dates.get(i + 1), (Nutzer) cbUser.getSelectedItem())) {
                            for(DurchlaufNew durch : DB_Access_Manager.getInstance().selectRun(dates.get(i), dates.get(i + 1), (NutzerNew) cbUser.getSelectedItem())){
                                tmpFail += durch.getFehlgeschlagen();
                                tmpSuccess += durch.getErfolgreich();
                                tmpRefs += durch.getUebernahmeAnz();
                            }
                        }
                    } catch (NullPointerException n) {
                        //n.printStackTrace();
                    }
                    fail.add(tmpFail);
                    success.add(tmpSuccess);
                    refs.add(tmpRefs);
                }
                chart.addSeries("Fehlgeschlagen", getXDisplay(getXAxisData(startDate, endDate), daysBetween(startDate, endDate)), fail, null);
                chart.addSeries("Erfolgreich", getXDisplay(getXAxisData(startDate, endDate), daysBetween(startDate, endDate)), success, null);
                chart.addSeries("Referenzen übernommen", getXDisplay(getXAxisData(startDate, endDate), daysBetween(startDate, endDate)), refs, null);

                chart.removeSeries("Entfernt");
                chart.removeSeries("Verändert");
                chart.removeSeries("Verschoben");
                chart.removeSeries("Inhalt verändert");
                chart.removeSeries("Hinzugefügt");
            } else {

                try {
                    chart.removeSeries("Entfernt");
                    chart.removeSeries("Verändert");
                    chart.removeSeries("Verschoben");
                    chart.removeSeries("Inhalt verändert");
                    chart.removeSeries("Hinzugefügt");
                } catch (Exception i) {
                }
                chart.getStyler().setSeriesColors(SLICECOLOR_AENDERUNG);
                List<Integer> add = new ArrayList<>();
                List<Integer> delete = new ArrayList<>();
                List<Integer> move = new ArrayList<>();
                List<Integer> state = new ArrayList<>();
                List<Integer> changed = new ArrayList<>();

                List<LocalDateTime> dates = convertToQuery(getXAxisData(startDate, endDate));
                //System.out.println(dates);
                for (int i = 0; i < dates.size() - 1; i += 2) {
                    int tmpAdd = 0;
                    int tmpDelete = 0;
                    int tmpMove = 0;
                    int tmpState = 0;
                    int tmpChange = 0;
                    try {
                        //System.out.println("äalsdkfaksäödlka");
                        if (((Nutzer) cbUser.getSelectedItem()).getNutzerid() == -1) {
                            //System.out.println("a");
                            //for (Change change : DB_Access.getInstance().selectChanges(dates.get(i), dates.get(i + 1))) {
                            for(ChangeNew change : DB_Access_Manager.getInstance().selectChanges(dates.get(i), dates.get(i + 1))){
//                                System.out.println("asdf");
//                                System.out.println(change.getType());
                                switch (change.getChangeType().getBezeichnung()) {
                                    case "DELETED":
                                        tmpDelete++;
                                        break;
                                    case "CHANGED":
                                        tmpChange++;
                                        break;
                                    case "MOVED":
                                        tmpMove++;
                                        break;
                                    case "STATECHANGED":
                                        tmpState++;
                                        break;
                                    case "ADDED":
                                        tmpAdd++;
                                        break;
                                }
                            }
                        } else {
//                            System.out.println("asf");
                            for (ChangeNew change : DB_Access_Manager.getInstance().selectChanges(dates.get(i), dates.get(i + 1), (NutzerNew) cbUser.getSelectedItem())) {

//                                System.out.println(change.getType());
                                switch (change.getChangeType().getBezeichnung()) {
                                    case "DELETED":
                                        tmpDelete++;
                                        break;
                                    case "CHANGED":
                                        tmpChange++;
                                        break;
                                    case "MOVED":
                                        tmpMove++;
                                        break;
                                    case "STATECHANGED":
                                        tmpState++;
                                        break;
                                    case "ADDED":
                                        tmpAdd++;
                                        break;
                                }
                            }

                        }
                    } catch (NullPointerException n) {
                        n.printStackTrace();
                    }
//                    System.out.println(tmpAdd);
                    add.add(tmpAdd);
                    delete.add(tmpDelete);
                    move.add(tmpMove);
                    state.add(tmpState);
                    changed.add(tmpChange);
                }
                chart.addSeries("Entfernt", getXDisplay(getXAxisData(startDate, endDate), daysBetween(startDate, endDate)), delete, null);
                chart.addSeries("Verändert", getXDisplay(getXAxisData(startDate, endDate), daysBetween(startDate, endDate)), add, null);
                chart.addSeries("Verschoben", getXDisplay(getXAxisData(startDate, endDate), daysBetween(startDate, endDate)), move, null);
                chart.addSeries("Inhalt verändert", getXDisplay(getXAxisData(startDate, endDate), daysBetween(startDate, endDate)), state, null);
                chart.addSeries("Hinzugefügt", getXDisplay(getXAxisData(startDate, endDate), daysBetween(startDate, endDate)), changed, null);

                chart.removeSeries("Erfolgreich");
                chart.removeSeries("Fehlgeschlagen");
                chart.removeSeries("Referenzen übernommen");
            }
            updateUI();
        } catch (NullPointerException no) {
            //no.printStackTrace();
            JOptionPane.showMessageDialog(null, "Verbindung zur Datenbank konnte nicht hergestellt werden, Dashboard funktionalität kann nicht zur Verfügung gestellt werden");
        }
    }
     
      public void refillUsers() {
        dcbmUser = new DefaultComboBoxModel();
        cbUser.setModel(dcbmUser);
        dcbmUser.addElement(new Nutzer(-1, "Übersicht"));

//        try {
//            if (GlobalParamter.getInstance().isStatistic_db_reachable()) {
//                for (Nutzer user : DB_Access.getInstance().selectAllUsers()) {
//                    dcbmUser.addElement(user);
//                }
//            } else {
//            }
            if(DatabaseGlobalAccess.getInstance().isDbReachable()){
                for (NutzerNew user : DatabaseGlobalAccess.getInstance().getAllUsers()) {
                    dcbmUser.addElement(user);
                }
            }
//        } catch (SQLException ex) {
//            System.out.println("Failed to load users from database");
//        }
    }
     
    public List<LocalDateTime> convertToQuery(List<LocalDate> input) {;
        List<LocalDateTime> hilfe = new ArrayList<>();
        for (int i = 0; i < input.size() - 1; i++) {
            hilfe.add(input.get(i).atStartOfDay());
            hilfe.add(input.get(i + 1).atStartOfDay().minusNanos(1));

        }
        hilfe.add(input.get(input.size() - 1).atStartOfDay());
        hilfe.add(input.get(input.size() - 1).atStartOfDay().minusNanos(1));
        return hilfe;
    }
    private List<LocalDate> convertToLocalDate(List<LocalDateTime> input){
        List<LocalDate> res = new ArrayList<>();
        for (LocalDateTime localDateTime : input) {
            res.add(localDateTime.toLocalDate());
        }
        return res;
    }

    public List<LocalDate> getXAxisData(LocalDate start, LocalDate end) {
        long daysbetween = daysBetween(start, end);
        if (daysbetween > 7 && daysbetween <= 31) {
            List<LocalDate> dates = new ArrayList<>();
            for (int i = 0; i <= ChronoUnit.WEEKS.between(start, end); i++) {
                dates.add(start.plusWeeks(i));
            }
            dates.add(end);
            return dates;
        } else if (daysbetween > 31 && daysbetween <= 365) {
            List<LocalDate> dates = new ArrayList<>();
            for (int i = 0; i <= ChronoUnit.MONTHS.between(start, end); i++) {
                dates.add(start.plusMonths(i));
            }
            dates.add(end);
            return dates;
        } else if (daysbetween > 365) {
            List<LocalDate> dates = new ArrayList<>();
            for (int i = 0; i <= ChronoUnit.YEARS.between(start, end); i++) {
                dates.add(start.plusYears(i));
            }
            dates.add(end);
            return dates;
        }
        List<LocalDate> dates = new ArrayList<>();
        for (int i = 0; i <= daysbetween; i++) {
            dates.add(start.plusDays(i));
        }
        return dates;
    }

    public List<String> getXDisplay(List<LocalDate> input, long daysbetween) {
        List<String> result = new ArrayList<>();
        if (daysbetween > 7 && daysbetween <= 31) {
            for (LocalDate target : input) {
                result.add("KW " + dtfFull.format(target));
            }
            return result;
        } else if (daysbetween > 31 && daysbetween <= 365) {
            for (LocalDate target : input) {
                result.add(dtfFull.format(target));
            }
            return result;
        } else if (daysbetween > 365) {
            for (LocalDate target : input) {
                result.add(dtfFull.format(target));
            }
            return result;
        }
        for (LocalDate target : input) {
            result.add(dtfFull.format(target));
        }
        return result;
    }

    private long daysBetween(LocalDate start, LocalDate end) {
        return ChronoUnit.DAYS.between(start, end);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btUpdate;
    private javax.swing.JComboBox<String> cbHistory;
    private javax.swing.JComboBox<NutzerNew> cbUser;
    private javax.swing.JFormattedTextField ftfEnd;
    private javax.swing.JFormattedTextField ftfStart;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel paButton;
    private javax.swing.JPanel paComboboxes;
    private javax.swing.JPanel paDateEnd;
    private javax.swing.JPanel paDateStart;
    private javax.swing.JPanel paDates;
    private javax.swing.JPanel paGraph;
    private javax.swing.JPanel paSelection;
    // End of variables declaration//GEN-END:variables
}
