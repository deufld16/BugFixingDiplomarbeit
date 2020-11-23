/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package general.gui;

import dashboard.bl.DatabaseGlobalAccess;
import dashboard.gui.LoadingDLG;
import explorer.beans.Database;
import explorer.beans.Field;
import explorer.beans.Table;
import explorer.database.DB_Access;
import explorer.gui.ExplorerConfirmationDialog;
import general.beans.GuiComponent;
import general.beans.io_objects.ExplorerLayer;
import general.bl.ContainerKeyListener;
import general.bl.GlobalAccess;
import general.bl.GlobalMethods;
import general.bl.GlobalParamter;
import general.enums.Tools;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import recorder.gui.MainFrame;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.ScrollPaneConstants;
import org.apache.commons.io.FileUtils;
import recorder.guiOperations.GUIOperations;
import recorder.io.ConfigManager;
import simulator.bl.ExecutionManager;
import simulator.bl.SimulatorActionListener;

/**
 *
 * @author Florian Deutschmann
 */
public class Test_IDE_MainFrame extends javax.swing.JFrame {

    private ContainerKeyListener ckListener = new ContainerKeyListener();
    private SimulatorActionListener sim = new SimulatorActionListener();
    private List<GuiComponent> allComponents = new LinkedList<>();
    private Component componentToOverdraw = null;
    private WelcomePanel welcomePanel;
    private static final Font DEFAULT_PLAIN_FONT = new Font("Tahoma", Font.PLAIN, 18);

    /**
     * Creates new form MainFrame
     */
    public Test_IDE_MainFrame() {
        System.out.println(System.getProperty("user.dir"));
        attemptDatabaseConnection();
        GlobalParamter.getInstance().setDefaultParamterConfiguration();
        if (!Files.exists(Paths.get(GlobalParamter.getInstance().getSettingsResPath().toString(), "parameter.conf"))) {
            try {
                settings.io.Loader.resetToDefaultConfiguration();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        setIconImage(new ImageIcon(Paths.get(System.getProperty("user.dir"), "src", "main", "java", "general", "res", "img", "logo.png").toString()).getImage());
        initComponents();
        //        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setTitle("Rewe DStore Test IDE");
        setProxyContent();
        //        setKeyListener();
        setImages();
        initializeDynamicMenuBar();
        //        GlobalMethods.getInstance().changeTool(Tools.DASHBOARD);
        lbDashboard.setBackground(Color.lightGray);
        try {
            lbDashboard.setIcon(general.io.Loader.loadImage("house_black_v2.png", 70, 70));
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Ein Fehler ist beim Laden der SideBar-Bilder aufgetreten");
        }
        //        componentToOverdraw = lbDashboard;
        lbSettings.setBorder(BorderFactory.createMatteBorder(2, 0, 0, 0, Color.white));
        adjustMinimumSizeToScreenSize();
        displayWelcomeScreen();
        try {
            general.io.Loader.loadLastUser();
        } catch (Exception e) {

        }
    }

    /**
     * Method to take the necessary actions to display the welcome screen
     * correctly
     */
    private void displayWelcomeScreen() {
        setSize(1100, 800);
        setLocationRelativeTo(null);
        welcomePanel = new WelcomePanel();
        paDynamic.add(welcomePanel);
        Component compToRemove = null;
        for (Component component : GlobalAccess.getInstance().getTest_ide_main_frame().getContentPane().getComponents()) {
            if (component.getName() != null) {
                if (component.getName().equalsIgnoreCase("sideBarPanel")) {
                    compToRemove = component;
                }
            }
        }
        if (compToRemove != null) {
            GlobalAccess.getInstance().getTest_ide_main_frame().remove(compToRemove);
        } else {
            JOptionPane.showMessageDialog(GlobalAccess.getInstance().getTest_ide_main_frame(), "Error - Sidebar could not be removed");
        }
        GlobalAccess.getInstance().setHidden(true);
    }

    /**
     * Method that sets the minimu size to which the programm can be resized
     */
    private void adjustMinimumSizeToScreenSize() {
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        setMinimumSize(new Dimension(gd.getDisplayMode().getWidth() / 2, gd.getDisplayMode().getHeight() / 2));
    }

    /**
     * Method to try to access the configured database
     */
    private void attemptDatabaseConnection() {
        Thread t = new Thread(new GetStatisticDatabaseObjects());
        t.start();
    }

    /**
     * Method which is being used to initialize the dynamic menubars
     */
    private void initializeDynamicMenuBar() {
        List<JMenu> dashboardMenus = getDashboardMenus();
        GlobalAccess.getInstance().getDynamicHeadlineMap().put(Tools.DASHBOARD, dashboardMenus);

        List<JMenu> explorerMenus = getExplorerMenus();
        GlobalAccess.getInstance().getDynamicHeadlineMap().put(Tools.EXPLORER, explorerMenus);

        List<JMenu> recorderMenus = getRecorderMenus();
        GlobalAccess.getInstance().getDynamicHeadlineMap().put(Tools.RECORDER, recorderMenus);

        List<JMenu> analyzerMenus = getAnalyzerMenus();
        GlobalAccess.getInstance().getDynamicHeadlineMap().put(Tools.ANALYZER, analyzerMenus);

        List<JMenu> settingsMenus = getSettingsMenus();
        GlobalAccess.getInstance().getDynamicHeadlineMap().put(Tools.SETTINGS, settingsMenus);

        List<JMenu> simulatorMenus = getSimulatorMenus();
        GlobalAccess.getInstance().getDynamicHeadlineMap().put(Tools.SIMULATOR, simulatorMenus);

        List<JMenu> remoteMenus = getRemoteMenus();
        GlobalAccess.getInstance().getDynamicHeadlineMap().put(Tools.REMOTE, remoteMenus);
    }

    /**
     * Method to define the menu bar for the tool remote executor
     *
     * @return
     */
    private List<JMenu> getRemoteMenus() {
        List<JMenu> remoteMenus = new LinkedList<>();
        remoteMenus.add(createMenuAbout());
        return remoteMenus;
    }

    /**
     * Method to define the menu bar for the tool dashboard
     *
     * @return
     */
    private List<JMenu> getDashboardMenus() {
        KeyStroke ksNewPro = KeyStroke.getKeyStroke(KeyEvent.VK_N,
                ActionEvent.CTRL_MASK);
        KeyStroke ksNewRec = KeyStroke.getKeyStroke(KeyEvent.VK_N,
                ActionEvent.CTRL_MASK + ActionEvent.SHIFT_MASK);
        KeyStroke ksRun = KeyStroke.getKeyStroke(KeyEvent.VK_R,
                ActionEvent.CTRL_MASK);

        List<JMenu> dashboardMenus = new LinkedList<>();

        JMenu jmFile = new JMenu("Datei");
        jmFile.setFont(DEFAULT_PLAIN_FONT);

        JMenuItem miNewPro = new JMenuItem("Neues Projekt");
        miNewPro.setFont(DEFAULT_PLAIN_FONT);
        miNewPro.setAccelerator(ksNewPro);
        miNewPro.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                welcomePanel.createProject();
            }
        });
        jmFile.add(miNewPro);

        JMenuItem miNewRec = new JMenuItem("Neuer Rekoder");
        miNewRec.setFont(DEFAULT_PLAIN_FONT);
        miNewRec.setAccelerator(ksNewRec);
        miNewRec.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                welcomePanel.createRecorder();
            }
        });
        jmFile.add(miNewRec);
        dashboardMenus.add(jmFile);

        JMenu jmFunc = new JMenu("Funktion");
        jmFunc.setFont(DEFAULT_PLAIN_FONT);

        JMenuItem miRunAll = new JMenuItem("Gesamtdurchlauf starten");
        miRunAll.setFont(DEFAULT_PLAIN_FONT);
        miRunAll.setAccelerator(ksRun);
        miRunAll.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                welcomePanel.startExecution();
            }
        });
        jmFunc.add(miRunAll);

        dashboardMenus.add(jmFunc);

        dashboardMenus.add(createMenuAbout());

        return dashboardMenus;
    }

    /**
     * Outsourced method to create a JMenu "Über" for every tool
     *
     * @return
     */
    private JMenu createMenuAbout() {
        JMenu meAbout = new JMenu("Über");
        meAbout.setFont(DEFAULT_PLAIN_FONT);
        JMenuItem miInfo = new JMenuItem("Über das Programm");
        miInfo.setFont(DEFAULT_PLAIN_FONT);
        miInfo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JPanel panel = new JPanel();
                JLabel lbInfo = new JLabel("<html>Dieses Programm entstand im<br/>Zuge einer Diplomarbeit mit<br/>der HTBLA Kaindorf a. d. Sulm"
                        + "<br/>im Juli 2020.</html>");
                lbInfo.setFont(DEFAULT_PLAIN_FONT);
                JLabel lbTeam = new JLabel("<html>Mitwirkende:<br/>- <b>M</b>aximilian Strohmaier<br/>- <b>A</b>nna Lechner"
                        + "<br/>- <b>L</b>ukas Krobath<br/>- <b>F</b>lorian Deutschmann</html>");
                lbTeam.setFont(DEFAULT_PLAIN_FONT);
                panel.add(lbInfo);
                panel.add(Box.createHorizontalStrut(15));
                panel.add(lbTeam);
                JOptionPane.showMessageDialog(null, panel, "Über das Programm", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        meAbout.add(miInfo);
        return meAbout;
    }

    /**
     * Method to define the menu bar for the tool "Settings"
     *
     * @return
     */
    private List<JMenu> getSettingsMenus() {
        List<JMenu> settingsMenus = new LinkedList<>();

        JMenu jmUeber = createMenuAbout();
        JMenuItem miIcons = new JMenuItem("Icons provided by icons8.de");
        miIcons.setFont(DEFAULT_PLAIN_FONT);
        miIcons.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                    try {
                        Desktop.getDesktop().browse(new URI("https://icons8.de"));
                    } catch (URISyntaxException | IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
        jmUeber.add(miIcons);
        settingsMenus.add(jmUeber);
        return settingsMenus;
    }

    /**
     * Method to define the menu bar for the tool "Explorer"
     *
     * @return
     */
    private List<JMenu> getExplorerMenus() {
        List<JMenu> explorerMenus = new LinkedList<>();
//        ExplorerConfirmationDialog dlg = null;
        JMenu jmOpt = new JMenu("Shortcuts");
        jmOpt.setFont(DEFAULT_PLAIN_FONT);
        JMenuItem miBackToSavePoint = new JMenuItem("Zurück zum letzten SavePoint");
        miBackToSavePoint.setFont(DEFAULT_PLAIN_FONT);
        KeyStroke ctrlZ = KeyStroke.getKeyStroke(KeyEvent.VK_Z, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
        miBackToSavePoint.setAccelerator(ctrlZ);
        miBackToSavePoint.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String displayText = "<html>Sind Sie sich sicher, dass Sie die Änderungen zum letzten SavePoint<br/>zurücksetzen möchten? "
                        + "Die aktuellen Änderungen gehen dadurch verloren!</html>";
                ExplorerConfirmationDialog dlg = new ExplorerConfirmationDialog(GlobalAccess.getInstance().getTest_ide_main_frame(), true, displayText);
                dlg.setVisible(true);
                GlobalAccess.getInstance().getPaExplorer().clearRightPanel();
                try {
                    createOrLoadsavePoint(dlg, 0);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                if (GlobalParamter.getInstance().getTrExplorer().getTransferHandler() == null) {
                    GlobalParamter.getInstance().getTrExplorer().setTransferHandler(GlobalAccess.getInstance().getPaExplorer().getEtth());
                }
            }
        });
        jmOpt.add(miBackToSavePoint);
        JMenuItem miNewSavePoint = new JMenuItem("Neuen SavePoint erstellen");
        miNewSavePoint.setFont(DEFAULT_PLAIN_FONT);
        KeyStroke ctrlS = KeyStroke.getKeyStroke(KeyEvent.VK_S, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
        miNewSavePoint.setAccelerator(ctrlS);
        miNewSavePoint.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String displayText = "<html>Sind Sie sich sicher, dass Sie mit dem aktuellen Stand des<br/>SavePoints einen SavePoint erstellen möchten?"
                        + "<br/>Der aktuelle SavePoint (sofern einer existiert) geht dadurch verloren!</html>";
                ExplorerConfirmationDialog dlg = new ExplorerConfirmationDialog(GlobalAccess.getInstance().getTest_ide_main_frame(), true, displayText);
                dlg.setVisible(true);
                GlobalAccess.getInstance().getPaExplorer().clearRightPanel();
                try {
                    createOrLoadsavePoint(dlg, 1);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                if (GlobalParamter.getInstance().getTrExplorer().getTransferHandler() == null) {
                    GlobalParamter.getInstance().getTrExplorer().setTransferHandler(GlobalAccess.getInstance().getPaExplorer().getEtth());
                }
            }
        });
        jmOpt.add(miNewSavePoint);
        explorerMenus.add(jmOpt);
        explorerMenus.add(createMenuAbout());
        return explorerMenus;
    }

    /**
     * Method which is used to create/load a savePoint. This method also makes
     * sure, that the path on which the savepoint should be loaded fullfills all
     * the requirements to fit into the actual defined folder structure so that
     * deleting and overwriting a wrong directory is not possible
     *
     * @param dlg der Dialog, welcher mit Ja/Nein zu bestätigen ist
     * @param type dieser zeigt an welcher Dialog gerade angezeigt wird
     * @throws IOException
     */
    private void createOrLoadsavePoint(ExplorerConfirmationDialog dlg, int type) throws IOException {
        switch (type) {
            case 0:
                if (dlg.isIsOk()) {
                    if (Files.exists(Paths.get(GlobalParamter.getInstance().getParameter().get("projectPath")))) {
                        JFileChooser fc = new JFileChooser(System.getProperty("user.dir"));
                        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                        if (fc.showOpenDialog(GlobalAccess.getInstance().getTest_ide_main_frame()) == JFileChooser.APPROVE_OPTION) {
                            if (Files.exists(fc.getSelectedFile().toPath())) {
                                if (canBeDeleted(Paths.get(GlobalParamter.getInstance().getParameter().get("projectPath")))) {
                                    if (isSavePoint(fc.getSelectedFile().getAbsolutePath())) {
                                        try {
                                            FileUtils.deleteDirectory(Paths.get(GlobalParamter.getInstance().getParameter().get("projectPath")).toFile());
                                        } catch (Exception ex) {
                                            if (Files.exists(Paths.get(GlobalParamter.getInstance().getParameter().get("projectPath")))) {
                                                Files.delete(Paths.get(GlobalParamter.getInstance().getParameter().get("projectPath")));
                                            }
                                        }
                                        FileUtils.copyDirectory(fc.getSelectedFile(),
                                                Paths.get(GlobalParamter.getInstance().getParameter().get("projectPath")).toFile());
                                        GlobalAccess.getInstance().getPaExplorer().reloadTree();
                                        JOptionPane.showMessageDialog(GlobalAccess.getInstance().getTest_ide_main_frame(), "SavePoint Wiederherstellung erfolgreich!");
                                    } else {
                                        JOptionPane.showMessageDialog(GlobalAccess.getInstance().getTest_ide_main_frame(), "Bei dem Ausgewählten Ordner handelt es sich um keinen validen Savepoint!");
                                    }
                                } else {
                                    JOptionPane.showMessageDialog(GlobalAccess.getInstance().getTest_ide_main_frame(), "Der in den Einstellungen zurzeit definierte Projekt-Ordner entspricht nicht dem Format - SavePoint konnte nicht geladen werden");
                                }
                            }
                        } else {
                            JOptionPane.showMessageDialog(GlobalAccess.getInstance().getTest_ide_main_frame(), "Es existiert kein SavePoint");
                        }
                    } else {
                        JOptionPane.showMessageDialog(GlobalAccess.getInstance().getTest_ide_main_frame(), "Der in den Einstellungen definierte Pfad zum aktuellen Explorer existiert nicht");
                    }
                }
                break;
            case 1:
                if (dlg.isIsOk()) {
                    if (Files.exists(Paths.get(GlobalParamter.getInstance().getParameter().get("projectPath")))) {
                        JFileChooser fc = new JFileChooser(System.getProperty("user.dir"));
                        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                        if (fc.showSaveDialog(GlobalAccess.getInstance().getTest_ide_main_frame()) == JFileChooser.APPROVE_OPTION) {
                            String saveName = JOptionPane.showInputDialog(GlobalAccess.getInstance().getTest_ide_main_frame(), "Geben Sie den Namen "
                                    + "ein, unter welchem der Savepoint gespeichert werden soll");
                            if (saveName == null) {
                                return;
                            }

                            if (saveName.trim().isEmpty()) {
                                return;
                            }
                            Path tmp_path = Paths.get(fc.getSelectedFile().getAbsolutePath(), saveName);
                            FileUtils.copyDirectory(Paths.get(GlobalParamter.getInstance().getParameter().get("projectPath")).toFile(),
                                    tmp_path.toFile());
                        }

                    } else {
                        JOptionPane.showMessageDialog(GlobalAccess.getInstance().getTest_ide_main_frame(), "Der in den Einstellungen definierte Pfad zum aktuellen Explorer existiert nicht");
                    }
                }
                break;
        }
    }

    private boolean isSavePoint(String path) {
        try {
            int check_number = 0;
            File tmp_file = new File(path);
            if (tmp_file.listFiles().length == 1) {
                File[] dir = tmp_file.listFiles();
                if (dir[0].listFiles().length == 3) {
                    for (int i = 0; i < dir[0].listFiles().length; i++) {
                        switch (dir[0].listFiles()[i].getName()) {
                            case "run":
                                check_number++;
                                break;
                            case "erg":
                                check_number++;
                                break;
                            case "ref":
                                check_number++;
                                break;
                        }
                    }
                } else {
                    return false;
                }
            } else {
                return false;
            }
            return check_number == 3;
        } catch (Exception ex) {
            return false;
        }
    }

    /**
     * Method which checks if the directory that should be deleted/ovewritten
     * matches the defined directory structure to prevent the deletion of wrong
     * directories
     *
     * @param path of the directory that should be deleted
     * @return
     */
    private boolean canBeDeleted(Path path) {
        try {
            int check_number = 0;
            File tmp_file = new File(path.toString());
            if (tmp_file.listFiles().length == 1) {
                File[] dir = tmp_file.listFiles();
                if (dir[0].listFiles().length == 3) {
                    for (int i = 0; i < dir[0].listFiles().length; i++) {
                        switch (dir[0].listFiles()[i].getName()) {
                            case "run":
                                check_number++;
                                break;
                            case "erg":
                                check_number++;
                                break;
                            case "ref":
                                check_number++;
                                break;
                        }
                    }
                } else {
                    return false;
                }
            } else {
                return false;
            }
            return check_number == 3;
        } catch (Exception ex) {
            return false;
        }
    }

    /**
     * Method to define the menu bar for the tool "Simulator"
     *
     * @return
     */
    private List<JMenu> getSimulatorMenus() {
        List<JMenu> simulatorMenus = new LinkedList<>();

        JMenu jmOpt = new JMenu("Optionen");
        jmOpt.setFont(DEFAULT_PLAIN_FONT);
        JCheckBoxMenuItem miStart = new JCheckBoxMenuItem("Befehle einzeln abspielen");
        miStart.setFont(DEFAULT_PLAIN_FONT);
        miStart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ExecutionManager.getInstance().setStepLockActivated(miStart.isSelected());
            }
        });
        jmOpt.add(miStart);

        JMenuItem delete = new JMenuItem("Selektierte Testschritte löschen");
        delete.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
        delete.addActionListener(sim);
        delete.setFont(DEFAULT_PLAIN_FONT);

        JMenuItem deleteAll = new JMenuItem("Alle Testschritte löschen");
        deleteAll.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, KeyEvent.CTRL_DOWN_MASK));
        deleteAll.addActionListener((e) -> {
            ExecutionManager.getInstance().getDlm().removeAllElements();
            ExecutionManager.getInstance().setTargetsOnly(new ArrayList<ExplorerLayer>());
        });
        deleteAll.setFont(DEFAULT_PLAIN_FONT);

        jmOpt.add(delete);
        jmOpt.add(deleteAll);
        simulatorMenus.add(jmOpt);
        simulatorMenus.add(createMenuAbout());

        return simulatorMenus;
    }

    /**
     * Method that returns the List of JMenus that are being needed in the
     * Analyzer sub tool
     *
     * @return
     */
    private List<JMenu> getAnalyzerMenus() {
        List<JMenu> analyzerMenus = new LinkedList<>();

        JMenu jmOpt = new JMenu("Optionen");
        jmOpt.setFont(DEFAULT_PLAIN_FONT);
        JMenuItem miStart = new JMenuItem("Neuen Diff-Prozess starten");
        miStart.setFont(DEFAULT_PLAIN_FONT);
        miStart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                GlobalAccess.getInstance().getPaAnalyzer().startNewDiff();
            }
        });
        jmOpt.add(miStart);

        JMenuItem miScroll = new JMenuItem("Scrollverbindung von ref und erg aufheben");
        miScroll.setFont(DEFAULT_PLAIN_FONT);
        miScroll.setEnabled(false);
        miScroll.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                GlobalAccess.getInstance().getPaAnalyzer().editScrollBehavior();
            }
        });
        jmOpt.add(miScroll);

        JMenuItem miRestart = new JMenuItem("Neustart");
        miRestart.setFont(DEFAULT_PLAIN_FONT);
        miRestart.setEnabled(false);
        miRestart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                GlobalAccess.getInstance().getPaAnalyzer().restart();
            }
        });
        jmOpt.add(miRestart);

        analyzerMenus.add(jmOpt);
        analyzerMenus.add(createMenuAbout());

        return analyzerMenus;
    }

    /**
     * Method that returns the List of JMenus that are being needed in the
     * Recorder sub tool
     *
     * @return
     */
    private List<JMenu> getRecorderMenus() {
        List<JMenu> recorderMenus = new LinkedList<>();

        JMenuItem miConfigFileNachladen = new JMenuItem("Config-File nachladen");
        miConfigFileNachladen.setFont(DEFAULT_PLAIN_FONT);

        miConfigFileNachladen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                GlobalAccess.getInstance().getmFrameRecorder().onLoadArticleConfigFile(evt);
            }
        });

        JMenuItem miSetAuthBorder = new JMenuItem("Autorisationsgrenze setzen");
        miSetAuthBorder.setFont(DEFAULT_PLAIN_FONT);
        miSetAuthBorder.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                GlobalAccess.getInstance().getmFrameRecorder().onSetLimits(evt);
            }
        });
        JMenu recorderMenu_1 = new JMenu("Settings");
        recorderMenu_1.setFont(DEFAULT_PLAIN_FONT);
        recorderMenu_1.add(miConfigFileNachladen);
        recorderMenu_1.add(miSetAuthBorder);

        JMenuItem miNextBon = new JMenuItem("Neustart");
        miNextBon.setFont(DEFAULT_PLAIN_FONT);
        miNextBon.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                GlobalMethods.getInstance().onRestartRecorder();
            }
        });
        JMenu recorderMenu_2 = new JMenu("Nächster Bon");
        recorderMenu_2.setFont(DEFAULT_PLAIN_FONT);
        recorderMenu_2.add(miNextBon);

        recorderMenus.add(recorderMenu_1);
        recorderMenus.add(recorderMenu_2);

        JMenu recorderMenu_3 = new JMenu("Schulungsmodus");
        recorderMenu_3.setFont(DEFAULT_PLAIN_FONT);

        JCheckBoxMenuItem miSchulungsModus = new JCheckBoxMenuItem("Schulungsmodus aktivieren");
        miSchulungsModus.setFont(DEFAULT_PLAIN_FONT);
        miSchulungsModus.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!GUIOperations.isBonStarted()) {
                    GUIOperations.setTraining(miSchulungsModus.isSelected());
                } else {
                    miSchulungsModus.setSelected(false);
                    JOptionPane.showMessageDialog(null, "Schulungsmodus kann nur am Anfang eingeschaltet werden!", "Fehler", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        recorderMenu_3.add(miSchulungsModus);
        recorderMenus.add(recorderMenu_3);
        recorderMenus.add(createMenuAbout());
        return recorderMenus;
    }

    /**
     * Method which is used to scale the components whenever the programm is
     * resized
     */
    public void resizeComponents() {
        resizeSideBar();
        resizePanels();
        if (getContentPane().getSize().width < 750) {
            if (GlobalAccess.getInstance().getPaSettings().getjScrollPane1().getHorizontalScrollBarPolicy() != ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED) {
                GlobalAccess.getInstance().getPaSettings().getjScrollPane1().setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            }
        } else {
            if (GlobalAccess.getInstance().getPaSettings().getjScrollPane1().getHorizontalScrollBarPolicy() != ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER) {
                GlobalAccess.getInstance().getPaSettings().getjScrollPane1().setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
            }
        }
        GlobalMethods.getInstance().updateMainFrame();
    }

    /**
     * Method that is used to set the images of the naviagtion bar on the left
     * side of the programm
     */
    private void setImages() {
        try {
            lbDashboard.setIcon(general.io.Loader.loadImage("house_white.png", 40, 40));
            lbExplorer.setIcon(general.io.Loader.loadImage("folder_white.png", 40, 40));
            lbSettings.setIcon(general.io.Loader.loadImage("settings_white.png", 40, 40));
            lbSimulator.setIcon(general.io.Loader.loadImage("arrow_white.png", 40, 40));
            lbRecorder.setIcon(general.io.Loader.loadImage("shopping-cart_white.png", 40, 40));
            lbAnalyzer.setIcon(general.io.Loader.loadImage("check_white.png", 40, 40));
            lbRemote.setIcon(general.io.Loader.loadImage("remote_white.png", 40, 40));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Ein fehler ist beim Laden der SideBar-Bilder aufgetreten");
        }
    }

    /**
     * Method that resizes the naviagtion bar on the left side of the programm
     */
    public void resizeSideBar() {
        if (!GlobalAccess.getInstance().isHidden()) {
//            if (getContentPane().getSize().width < 750) {
//                if (!GlobalAccess.getInstance().isHidden()) {
//                    Component compToRemove = null;
//                    for (Component component : getContentPane().getComponents()) {
//                        if (component.getName() != null) {
//                            System.out.println(component.getName());
//                            if (component.getName().equalsIgnoreCase("sideBarPanel")) {
//                                compToRemove = component;
//                            }
//                        }
//                    }
//                    if (compToRemove != null) {
//                        remove(compToRemove);
//                    }
//                    GlobalMethods.getInstance().updateMainFrame();
//                    GlobalAccess.getInstance().setHidden(true);
//                }
//            } else 
            if (getContentPane().getSize().width < 1250) {
                if (GlobalAccess.getInstance().isHidden()) {
                    GlobalAccess.getInstance().setHidden(false);
                    paSideBar.setSize(50, getContentPane().getSize().height);
                    add(BorderLayout.WEST, paSideBar);
                    GlobalMethods.getInstance().updateMainFrame();
                }
            } else {
                if (GlobalAccess.getInstance().isHidden()) {
                    GlobalAccess.getInstance().setHidden(false);
                    paSideBar.setSize(75, getContentPane().getSize().height);
                    add(BorderLayout.WEST, paSideBar);
                    GlobalMethods.getInstance().updateMainFrame();
                }
            }
        }
    }

    /**
     * Method that resizes the JPanels depending on the widht of the programm
     */
    private void resizePanels() {
        if (paDynamic.getComponents().length != 0) {
            if (GlobalAccess.getInstance().isHidden()) {
                paDynamic.setSize(getContentPane().getSize().width, getContentPane().getSize().height);
                paDynamic.getComponents()[0].setSize(getContentPane().getSize().width, getContentPane().getSize().height);
            } else if (getContentPane().getSize().width < 1250) {
                paDynamic.setSize(getContentPane().getSize().width - 80, getContentPane().getSize().height);
                paDynamic.getComponents()[0].setSize(getContentPane().getSize().width - 80, getContentPane().getSize().height);
            } else {
                paDynamic.setSize(getContentPane().getSize().width - 75, getContentPane().getSize().height);
                paDynamic.getComponents()[0].setSize(getContentPane().getSize().width - 75, getContentPane().getSize().height);
            }
        }
    }

    /**
     * Method that sets the general KeyListener to all the necessary components
     */
    public void setKeyListener() {
        //paContainer.addKeyListener(ckListener);
        paDynamic.addKeyListener(ckListener);
        paSideBar.addKeyListener(ckListener);

        // paContainer.setFocusable(true);
        paDynamic.setFocusable(true);
        paSideBar.setFocusable(true);
    }

    /**
     * Method that sets the required Swing componets in the GlobalAccess
     * Singelton
     */
    private void setProxyContent() {
        GlobalAccess.getInstance().setmFrameRecorder(new MainFrame(this));
        GlobalAccess.getInstance().setTest_ide_main_frame(this);
        GlobalAccess.getInstance().setPaRecorder((JPanel) GlobalAccess.getInstance().getmFrameRecorder().getContentPane());
    }

    public JMenuBar getjMenuBar1() {
        return jMenuBar1;
    }

    public JPanel getPaDynamic() {
        return paDynamic;
    }

    public JPanel getPaSideBar() {
        return paSideBar;
    }

    public SimulatorActionListener getSim() {
        return sim;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        paSideBar = new javax.swing.JPanel();
        lbDashboard = new javax.swing.JLabel();
        lbExplorer = new javax.swing.JLabel();
        lbRecorder = new javax.swing.JLabel();
        lbSimulator = new javax.swing.JLabel();
        lbRemote = new javax.swing.JLabel();
        lbAnalyzer = new javax.swing.JLabel();
        lbSettings = new javax.swing.JLabel();
        paDynamic = new javax.swing.JPanel();
        jMenuBar1 = new javax.swing.JMenuBar();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(1500, 1000));
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                onResize(evt);
            }
        });
        addWindowStateListener(new java.awt.event.WindowStateListener() {
            public void windowStateChanged(java.awt.event.WindowEvent evt) {
                onFullScreen(evt);
            }
        });
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                onExit(evt);
            }
        });

        paSideBar.setBackground(new java.awt.Color(51, 51, 51));
        paSideBar.setName("sideBarPanel"); // NOI18N
        paSideBar.setPreferredSize(new java.awt.Dimension(75, 900));
        paSideBar.setLayout(new java.awt.GridLayout(10, 0));

        lbDashboard.setBackground(new java.awt.Color(51, 51, 51));
        lbDashboard.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbDashboard.setName("dashboard"); // NOI18N
        lbDashboard.setOpaque(true);
        lbDashboard.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                onHover(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                onDeHover(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                onChangeTools(evt);
            }
        });
        paSideBar.add(lbDashboard);

        lbExplorer.setBackground(new java.awt.Color(51, 51, 51));
        lbExplorer.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbExplorer.setName("explorer"); // NOI18N
        lbExplorer.setOpaque(true);
        lbExplorer.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                onHover(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                onDeHover(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                onChangeTools(evt);
            }
        });
        paSideBar.add(lbExplorer);

        lbRecorder.setBackground(new java.awt.Color(51, 51, 51));
        lbRecorder.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbRecorder.setName("recorder"); // NOI18N
        lbRecorder.setOpaque(true);
        lbRecorder.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                onHover(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                onDeHover(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                onChangeTools(evt);
            }
        });
        paSideBar.add(lbRecorder);

        lbSimulator.setBackground(new java.awt.Color(51, 51, 51));
        lbSimulator.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbSimulator.setName("simulator"); // NOI18N
        lbSimulator.setOpaque(true);
        lbSimulator.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                onHover(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                onDeHover(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                onChangeTools(evt);
            }
        });
        paSideBar.add(lbSimulator);

        lbRemote.setBackground(new java.awt.Color(51, 51, 51));
        lbRemote.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbRemote.setName("remote"); // NOI18N
        lbRemote.setOpaque(true);
        lbRemote.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                onHover(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                onDeHover(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                onChangeTools(evt);
            }
        });
        paSideBar.add(lbRemote);

        lbAnalyzer.setBackground(new java.awt.Color(51, 51, 51));
        lbAnalyzer.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbAnalyzer.setName("analyzer"); // NOI18N
        lbAnalyzer.setOpaque(true);
        lbAnalyzer.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                onHover(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                onDeHover(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                onChangeTools(evt);
            }
        });
        paSideBar.add(lbAnalyzer);

        lbSettings.setBackground(new java.awt.Color(51, 51, 51));
        lbSettings.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbSettings.setName("settings"); // NOI18N
        lbSettings.setOpaque(true);
        lbSettings.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                onHover(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                onDeHover(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                onChangeTools(evt);
            }
        });
        paSideBar.add(lbSettings);

        getContentPane().add(paSideBar, java.awt.BorderLayout.LINE_START);

        paDynamic.setPreferredSize(new java.awt.Dimension(0, 0));

        javax.swing.GroupLayout paDynamicLayout = new javax.swing.GroupLayout(paDynamic);
        paDynamic.setLayout(paDynamicLayout);
        paDynamicLayout.setHorizontalGroup(
            paDynamicLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 835, Short.MAX_VALUE)
        );
        paDynamicLayout.setVerticalGroup(
            paDynamicLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 447, Short.MAX_VALUE)
        );

        getContentPane().add(paDynamic, java.awt.BorderLayout.CENTER);
        setJMenuBar(jMenuBar1);

        pack();
    }// </editor-fold>//GEN-END:initComponents
    /**
     * Event that is called when a resizing action occurs
     *
     * @param evt
     */
    private void onResize(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_onResize
        resizeComponents();
    }//GEN-LAST:event_onResize
    /**
     * Method which is called when someone clicks on an JLabel in the Navigation
     * bar
     *
     * @param evt
     */
    private void onChangeTools(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_onChangeTools
        changeCurrentTool(evt.getComponent());
    }//GEN-LAST:event_onChangeTools

    /**
     * Method that is called when the Tool needs to be changed from another Tool
     * without clicking on the navigation bar
     *
     * @param identifier
     */
    public void changeTool(String identifier) {
        Component comp = null;
        if (lbExplorer.getName().equalsIgnoreCase(identifier)) {
            comp = lbExplorer;
        } else if (lbAnalyzer.getName().equalsIgnoreCase(identifier)) {
            comp = lbAnalyzer;
        } else if (lbDashboard.getName().equalsIgnoreCase(identifier)) {
            comp = lbDashboard;
        } else if (lbRecorder.getName().equalsIgnoreCase(identifier)) {
            GlobalAccess.getInstance().getmFrameRecorder().onRestartWithoutDLG();
            comp = lbRecorder;
        } else if (lbSettings.getName().equalsIgnoreCase(identifier)) {
            comp = lbSettings;
        } else if (lbSimulator.getName().equalsIgnoreCase(identifier)) {
            comp = lbSimulator;
        } else if (lbRemote.getName().equalsIgnoreCase(identifier)) {
            comp = lbRemote;
        }

        changeCurrentTool(comp);
    }

    /**
     * Method that changes the current tool and sets the images to the black
     * version of it (so that it is visible that it is selected) it also
     * deslects the previous selected JLabel (white image)
     *
     * @param comp
     */
    private void changeCurrentTool(Component comp) {
        if (componentToOverdraw == null
                || !componentToOverdraw.getName().equalsIgnoreCase(comp.getName())) {
            try {
                switch (comp.getName()) {
                    case "dashboard":
                        GlobalMethods.getInstance().changeTool(Tools.DASHBOARD);
                        ((JLabel) comp).setIcon(general.io.Loader.loadImage("house_black_v2.png", 70, 70));
                        GlobalAccess.getInstance().getPaDashboard().refillUsers();
                        break;
                    case "explorer":
                        GlobalMethods.getInstance().changeTool(Tools.EXPLORER);
                        ((JLabel) comp).setIcon(general.io.Loader.loadImage("folder_black_v2.png", 90, 70));
//                        try {
//                            if (GlobalParamter.getInstance().isStatistic_db_reachable()) {
//                                dashboard.database.DB_Access.getInstance().importProject(GlobalParamter.getInstance().getWorkingProjects());
//                            }
//                        } catch (SQLException ex) {
//                            ex.printStackTrace();
//                        }
                        if (DatabaseGlobalAccess.getInstance().isDbReachable()) {
                            dashboard.database.DB_Access_Manager.getInstance().importProject(GlobalParamter.getInstance().getWorkingProjects());
                        }
                        break;
                    case "recorder":
                        GlobalMethods.getInstance().changeTool(Tools.RECORDER);
                        resizeComponents();
                        //GlobalAccess.getInstance().getmFrameRecorder().onRestart();
                        if (Files.exists(Paths.get(settings.io.Loader.getSpecificParameter(false).get("dynamicFunc")))) {
                            GUIOperations.setDynFuncMap(recorder.io.ConfigManager.loadFile(Paths.get(settings.io.Loader.getSpecificParameter(false).get("dynamicFunc"))));
                            GUIOperations.getPaArticle().addDynamicFunctionalities(GUIOperations.getDynFuncMap());
                        }
                        if (Files.exists(Paths.get(settings.io.Loader.getSpecificParameter(false).get("artConfig")))) {
                            GUIOperations.setEanMap(ConfigManager.loadFile(Paths.get(settings.io.Loader.getSpecificParameter(false).get("artConfig"))));
                            GUIOperations.getPaArticle().setButtons(GUIOperations.getEanMap());
                            GUIOperations.getPaArticle().updateUI();
                        } else {
                            System.out.println(Paths.get(settings.io.Loader.getSpecificParameter(false).get("artConfig")));
                        }
                        ((JLabel) comp).setIcon(general.io.Loader.loadImage("shopping-cart_black_v2.png", 80, 70));
                        break;
                    case "simulator":
                        GlobalMethods.getInstance().changeTool(Tools.SIMULATOR);
                        ((JLabel) comp).setIcon(general.io.Loader.loadImage("arrow_black_v2.png", 70, 70));
                        break;
                    case "remote":
                        GlobalMethods.getInstance().changeTool(Tools.REMOTE);
                        ((JLabel) comp).setIcon(general.io.Loader.loadImage("remote_black_v2.png", 75, 75));
                        break;
                    case "analyzer":
                        GlobalMethods.getInstance().changeTool(Tools.ANALYZER);
                        ((JLabel) comp).setIcon(general.io.Loader.loadImage("check_black_v2.png", 70, 70));
                        break;
                    case "settings":
                        GlobalAccess.getInstance().getPaSettings().updateUserCB();
                        GlobalMethods.getInstance().changeTool(Tools.SETTINGS);
                        GlobalAccess.getInstance().getPaSettings().setTextOfPanels(settings.io.Loader.getSpecificParameter(false));
                        //GlobalAccess.getInstance().getPaSettings().resizeInternalComponents();
                        ((JLabel) comp).setIcon(general.io.Loader.loadImage("settings_black_v2.png", 70, 70));
                        break;
                }

                if (componentToOverdraw != null) {
                    switch (componentToOverdraw.getName()) {
                        case "dashboard":
                            ((JLabel) componentToOverdraw).setIcon(general.io.Loader.loadImage("house_white.png", 40, 40));
                            break;
                        case "explorer":
                            ((JLabel) componentToOverdraw).setIcon(general.io.Loader.loadImage("folder_white.png", 40, 40));
                            break;
                        case "recorder":
                            ((JLabel) componentToOverdraw).setIcon(general.io.Loader.loadImage("shopping-cart_white.png", 40, 40));
                            break;
                        case "simulator":
                            ((JLabel) componentToOverdraw).setIcon(general.io.Loader.loadImage("arrow_white.png", 40, 40));
                            break;
                        case "remote":
                            ((JLabel) componentToOverdraw).setIcon(general.io.Loader.loadImage("remote_white.png", 40, 40));
                            break;
                        case "analyzer":
                            ((JLabel) componentToOverdraw).setIcon(general.io.Loader.loadImage("check_white.png", 40, 40));
                            break;
                        case "settings":
                            ((JLabel) componentToOverdraw).setIcon(general.io.Loader.loadImage("settings_white.png", 40, 40));
                            break;
                    }
                    componentToOverdraw.setBackground(new Color(51, 51, 51));
                } else if (comp != lbDashboard) {
                    lbDashboard.setIcon(general.io.Loader.loadImage("house_white.png", 40, 40));
                    lbDashboard.setBackground(new Color(51, 51, 51));
                }

                comp.setBackground(Color.lightGray);
                GlobalMethods.getInstance().updateMainFrame();
                componentToOverdraw = comp;
                resizeComponents();
                if (comp.getName().equalsIgnoreCase("recorder")/*GUIOperations.isNeedsInit()*/) {
                    GlobalAccess.getInstance().getmFrameRecorder().openInitDialog();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Ein fehler ist beim Laden der SideBar-Bilder aufgetreten");
            }
        }
        System.out.println("Ende");
    }

    /**
     * Method that is called when the screen is resized via the double box next
     * to the X
     *
     * @param evt
     */
    private void onFullScreen(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_onFullScreen
        resizeComponents();
        GlobalAccess.getInstance().getPaSettings().onFullScreenPanel();
    }//GEN-LAST:event_onFullScreen
    /**
     * Hover effects for the JLabels in the navigation bar
     *
     * @param evt
     */
    private void onHover(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_onHover
        if (!evt.getComponent().getName().equals(componentToOverdraw.getName())) {
            evt.getComponent().setBackground(new Color(Color.lightGray.getRed(), Color.lightGray.getBlue(), Color.lightGray.getGreen(), 80));
        }
        GlobalMethods.getInstance().updateMainFrame();
    }//GEN-LAST:event_onHover
    /**
     * Event that is called after a button is not hovered anymore
     *
     * @param evt
     */
    private void onDeHover(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_onDeHover
        if (!evt.getComponent().getName().equals(componentToOverdraw.getName())) {
            evt.getComponent().setBackground(new Color(51, 51, 51));
        }
        GlobalMethods.getInstance().updateMainFrame();
    }//GEN-LAST:event_onDeHover

    /**
     * Event to save the last user for the next programm run
     *
     * @param evt
     */
    private void onExit(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_onExit
        try {
            general.io.Saver.saveLastUser();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }//GEN-LAST:event_onExit

    /**
     * Inner class to try to achieve a database connection
     */
    private class AttemptDatabaseConnection implements Runnable {

        @Override
        public void run() {
            try {
                DB_Access.getInstance().connect("npos");
                List<String> help_npos = DB_Access.getInstance().selectAllTables("");
                List<Table> allTablesNpos = new LinkedList<>();
                for (String tmp_table : help_npos) {
                    List<Field> tmp_all_fields = new LinkedList<>();
                    for (String tableField : DB_Access.getInstance().selectAllFieldsOfTable(tmp_table)) {
                        Field field = new Field(tableField);
                        tmp_all_fields.add(field);
                    }
                    Table table = new Table(tmp_table, tmp_all_fields);
                    allTablesNpos.add(table);
                }
                GlobalAccess.getInstance().getAllDatabases().add(new Database("npos", allTablesNpos));
                DB_Access.getInstance().disconnect();

                DB_Access.getInstance().connect("historyd");
                List<String> help_history = DB_Access.getInstance().selectAllTables("");
                List<Table> allTablesHistory = new LinkedList<>();
                for (String tmp_table : help_history) {
                    List<Field> tmp_all_fields = new LinkedList<>();
                    for (String tableField : DB_Access.getInstance().selectAllFieldsOfTable(tmp_table)) {
                        Field field = new Field(tableField);
                        tmp_all_fields.add(field);
                    }
                    Table table = new Table(tmp_table, tmp_all_fields);
                    allTablesHistory.add(table);
                }
                GlobalAccess.getInstance().getAllDatabases().add(new Database("historyd", allTablesHistory));
                DB_Access.getInstance().disconnect();

                DB_Access.getInstance().connect("etilag");
                List<String> help_etilag = DB_Access.getInstance().selectAllTables("");
                List<Table> allTablesEtilag = new LinkedList<>();
                for (String tmp_table : help_etilag) {
                    List<Field> tmp_all_fields = new LinkedList<>();
                    for (String tableField : DB_Access.getInstance().selectAllFieldsOfTable(tmp_table)) {
                        Field field = new Field(tableField);
                        tmp_all_fields.add(field);
                    }
                    Table table = new Table(tmp_table, tmp_all_fields);
                    allTablesEtilag.add(table);
                }
                GlobalAccess.getInstance().getAllDatabases().add(new Database("etilag", allTablesEtilag));
                DB_Access.getInstance().disconnect();
                GlobalAccess.getInstance().setDatabaseReachable(true);
            } catch (Exception ex) {
                GlobalAccess.getInstance().setDatabaseReachable(false);
            }
        }
    }

    /**
     * Inner class to retrieve all objects from the configured database
     */
    private class GetStatisticDatabaseObjects implements Runnable {

        @Override
        public void run() {
            try {
                //dashboard.database.DB_Access.getInstance();
                dashboard.database.DB_Access_Manager.getInstance().connect();
                System.out.println("db reachable: " + dashboard.bl.DatabaseGlobalAccess.getInstance().isDbReachable());
            } catch (Exception ex) {
//                JOptionPane.showMessageDialog(GlobalAccess.getInstance().getTest_ide_main_frame(),
//                        "Verbindung zur Datenbank für die Statistiken konnte nicht hergestellt werden");
                dashboard.bl.DatabaseGlobalAccess.getInstance().setDbReachable(false);
            }
            Thread t = new Thread(new AttemptDatabaseConnection());
            t.start();
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Test_IDE_MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Test_IDE_MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Test_IDE_MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Test_IDE_MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Test_IDE_MainFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JLabel lbAnalyzer;
    private javax.swing.JLabel lbDashboard;
    private javax.swing.JLabel lbExplorer;
    private javax.swing.JLabel lbRecorder;
    private javax.swing.JLabel lbRemote;
    private javax.swing.JLabel lbSettings;
    private javax.swing.JLabel lbSimulator;
    private javax.swing.JPanel paDynamic;
    private javax.swing.JPanel paSideBar;
    // End of variables declaration//GEN-END:variables
}
