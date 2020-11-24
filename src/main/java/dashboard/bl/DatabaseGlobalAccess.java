package dashboard.bl;

import dashboard.beans.ChangeType;
import dashboard.beans.Durchlauf;
import dashboard.beans.Nutzer;
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
 * Singelton Klasse, welche alle für die DashboardDB notwendigen Daten beinhaltet
 * @author Florian Deutschmann
 */
public class DatabaseGlobalAccess {

    private static DatabaseGlobalAccess instance = null;
    private Nutzer currentNutzer;
    private List<ChangeType> allChangeTypes = new LinkedList<>();
    private List<Projekt> allProjects = new LinkedList<>();
    private List<Nutzer> allUsers = new LinkedList<>();
    private boolean dbReachable = false;
    private List<Durchlauf> durchlauf = new LinkedList<>();

    private EntityManagerFactory emf;
    private EntityManager em;
    
    private boolean workflow = false;

    private DatabaseGlobalAccess() {
    }

    public void getDatabaseData() {
        em.persist(new ChangeType("DELETED"));
        em.persist(new ChangeType("ADDED"));
        em.persist(new ChangeType("MOVED"));
        em.persist(new ChangeType("STATECHANGED"));
        em.persist(new ChangeType("CHANGED"));
        em.persist(new Nutzer("Default User"));
        em.getTransaction().begin();
        em.getTransaction().commit();

        TypedQuery<ChangeType> changeTypeQuery = em.createNamedQuery("ChangeType.selectAll", ChangeType.class);
        allChangeTypes = new LinkedList<>(changeTypeQuery.getResultList());
        TypedQuery<Projekt> projectQuerry = em.createNamedQuery("Projekt.selectAll", Projekt.class);
        allProjects = new LinkedList<>(projectQuerry.getResultList());
        TypedQuery<Nutzer> userQuerry = em.createNamedQuery("NutzerNew.selectAll", Nutzer.class);
        allUsers = new LinkedList<>(userQuerry.getResultList());
        if (currentNutzer == null) {
            for (Nutzer user : allUsers) {
                if (user.getUsername().equalsIgnoreCase("Default User")) {
                    currentNutzer = user;
                }
            }
        }
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

    public Nutzer getCurrentNutzer() {
        return currentNutzer;
    }
    
    public Durchlauf getNewDurchlauf(){
        durchlauf.add(new Durchlauf());
        em.persist(durchlauf.get(durchlauf.size()-1));
        return durchlauf.get(durchlauf.size()-1);
    }

    public void setCurrentNutzer(Nutzer currentNutzer) {
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

    public Durchlauf getDurchlauf() {
         return durchlauf.get(durchlauf.size()-1);
    }

    public boolean isWorkflow() {
        return workflow;
    }

    public void setWorkflow(boolean workflow) {
        this.workflow = workflow;
    }

    public List<Nutzer> getAllUsers() {
        return allUsers;
    }

    public void setAllUsers(List<Nutzer> allUsers) {
        this.allUsers = allUsers;
    }

}
