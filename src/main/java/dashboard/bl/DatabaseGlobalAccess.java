package dashboard.bl;

import dashboard.beans.ChangeType;
import dashboard.beans.DurchlaufNew;
import dashboard.beans.NutzerNew;
import dashboard.beans.Projekt;
import dashboard.beans.TestCase;
import dashboard.beans.Testgruppe;
import java.util.LinkedList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import recorder.beans.Command;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author flori
 */
public class DatabaseGlobalAccess {

    private static DatabaseGlobalAccess instance = null;
    private NutzerNew currentNutzer;
    private List<ChangeType> allChangeTypes = new LinkedList<>();
    private List<Projekt> allProjects = new LinkedList<>();
    private List<NutzerNew> allUsers = new LinkedList<>();
    private boolean dbReachable = false;
    private DurchlaufNew durchlauf = new DurchlaufNew();
//    private List<Testgruppe> allTestGruppen = new LinkedList<>();
//    private List<TestCase> allTestCases = new LinkedList<>();
//    private List<Command> allCommands = new LinkedList<>();

    private EntityManagerFactory emf;
    private EntityManager em;

    private DatabaseGlobalAccess() {
    }

    public void getDatabaseData() {
//        em.persist(new ChangeType("DELETED"));
//        em.persist(new ChangeType("ADDED"));
//        em.persist(new ChangeType("MOVED"));
//        em.persist(new ChangeType("STATECHANGED"));
//        em.persist(new ChangeType("CHANGED"));
//        em.persist(new NutzerNew("Default User"));
//        em.getTransaction().begin();
//        em.getTransaction().commit();

        TypedQuery<ChangeType> changeTypeQuery = em.createNamedQuery("ChangeType.selectAll", ChangeType.class);
        allChangeTypes = new LinkedList<>(changeTypeQuery.getResultList());
        TypedQuery<Projekt> projectQuerry = em.createNamedQuery("Projekt.selectAll", Projekt.class);
        allProjects = new LinkedList<>(projectQuerry.getResultList());
        TypedQuery<NutzerNew> userQuerry = em.createNamedQuery("NutzerNew.selectAll", NutzerNew.class);
        allUsers = new LinkedList<>(userQuerry.getResultList());
        if (currentNutzer == null) {
            for (NutzerNew user : allUsers) {
                if (user.getUsername().equalsIgnoreCase("Default User")) {
                    currentNutzer = user;
                }
            }
        }
//        TypedQuery<Testgruppe> testGruppeQuerry = em.createNamedQuery("Testgruppe.selectAll", Testgruppe.class);
//        allTestGruppen = new LinkedList<>(testGruppeQuerry.getResultList());
//        TypedQuery<TestCase> testCaseQuerry = em.createNamedQuery("TestCase.selectAll", TestCase.class);
//        allTestCases = new LinkedList<>(testCaseQuerry.getResultList());
//        TypedQuery<Command> commandQuerry = em.createNamedQuery("Command.selectAll", Command.class);
//        allCommands = new LinkedList<>(commandQuerry.getResultList());
    }

    public boolean isDbReachable() {
        return dbReachable;
    }

    public void setDbReachable(boolean dbReachable) {
        this.dbReachable = dbReachable;
    }

    public static DatabaseGlobalAccess getInstance() {
        if (instance == null) {
            instance = new DatabaseGlobalAccess();
        }
        return instance;
    }

    public NutzerNew getCurrentNutzer() {
        return currentNutzer;
    }

    public void setCurrentNutzer(NutzerNew currentNutzer) {
        this.currentNutzer = currentNutzer;
    }

    public List<ChangeType> getAllChangeTypes() {
        return allChangeTypes;
    }

    public void setAllChangeTypes(List<ChangeType> allChangeTypes) {
        this.allChangeTypes = allChangeTypes;
    }

    public EntityManagerFactory getEmf() {
        return emf;
    }

    public void setEmf(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public EntityManager getEm() {
        return em;
    }

    public void setEm(EntityManager em) {
        this.em = em;
    }

    public List<Projekt> getAllProjects() {
        return allProjects;
    }

    public void setAllProjects(List<Projekt> allProjects) {
        this.allProjects = allProjects;
    }

    public DurchlaufNew getDurchlauf() {
        return durchlauf;
    }

    public void setDurchlauf(DurchlaufNew durchlauf) {
        this.durchlauf = durchlauf;
    }

//    public List<Testgruppe> getAllTestGruppen() {
//        return allTestGruppen;
//    }
//
//    public void setAllTestGruppen(List<Testgruppe> allTestGruppen) {
//        this.allTestGruppen = allTestGruppen;
//    }
//
//    public List<TestCase> getAllTestCases() {
//        return allTestCases;
//    }
//
//    public void setAllTestCases(List<TestCase> allTestCases) {
//        this.allTestCases = allTestCases;
//    }
//
//    public List<Command> getAllCommands() {
//        return allCommands;
//    }
//
//    public void setAllCommands(List<Command> allCommands) {
//        this.allCommands = allCommands;
//    }
    public List<NutzerNew> getAllUsers() {
        return allUsers;
    }

    public void setAllUsers(List<NutzerNew> allUsers) {
        this.allUsers = allUsers;
    }

}
