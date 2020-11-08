/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dashboard.beans;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

/**
 *
 * @author flori
 */
public class testing {

    private EntityManagerFactory emf;
    private EntityManager em;

    public void open() {
        emf = Persistence.createEntityManagerFactory("PU_dashboarddb");
        em = emf.createEntityManager();
    }

    /*public void test() {
        ChangeType ct = new ChangeType("Added");
        ChangeType ct2 = new ChangeType("Deleted");
        ChangeType ct3 = new ChangeType("Moved");
        ChangeType ct4 = new ChangeType("ChildStateChanged");
        ChangeType ct5 = new ChangeType("Changed");
        NutzerNew n1 = new NutzerNew("Florian");
        NutzerNew n2 = new NutzerNew("Anna");
        NutzerNew n3 = new NutzerNew("Lukas");
        DurchlaufgegenstandNew d1 = new DurchlaufgegenstandNew("Projekt 1", LocalDate.now(), 0);
        DurchlaufgegenstandNew d2 = new DurchlaufgegenstandNew("Testgruppe 1", LocalDate.now(), 0);
        DurchlaufgegenstandNew d3 = new DurchlaufgegenstandNew("Testcase 1", LocalDate.now(), 0);
        DurchlaufgegenstandNew d4 = new DurchlaufgegenstandNew("Command 1", LocalDate.now(), 0);
        DurchlaufgegenstandNew d5 = new DurchlaufgegenstandNew("Projekt 2", LocalDate.now(), 0);

        ChangeNew c1 = new ChangeNew(LocalDateTime.now());
        c1.setNutzer(n1);
        c1.setGegenstand(d1);
        c1.setChangeType(ct);
        ChangeNew c2 = new ChangeNew(LocalDateTime.now().minusDays(10));
        c2.setNutzer(n2);
        c2.setGegenstand(d2);
        c2.setChangeType(ct2);

        DurchlaufNew dn1 = new DurchlaufNew(LocalDateTime.now(), 0, 0, 0, 0);
        dn1.setNutzer(n1);
        dn1.setGegenstand(d5);
        DurchlaufNew dn2 = new DurchlaufNew(LocalDateTime.now().minusDays(10), 0, 0, 0, 0);
        dn2.setNutzer(n2);
        dn2.setGegenstand(d4);

        em.persist(n1);
        em.persist(n2);
        em.persist(n3);
        em.persist(d1);
        em.persist(d2);
        em.persist(d3);
        em.persist(d4);
        em.persist(d5);
        em.persist(ct);
        em.persist(ct2);
        em.persist(ct3);
        em.persist(ct4);
        em.persist(ct5);
        em.getTransaction().begin();
        em.getTransaction().commit();

        TypedQuery<NutzerNew> query = em.createNamedQuery("NutzerNew.selectAll", NutzerNew.class);
        List<NutzerNew> allUsers = query.getResultList();

        for (NutzerNew user : allUsers) {
            System.out.println(user.getUsername() + " - " + user.getNutzerid());
        }

        TypedQuery<ChangeNew> query2 = em.createNamedQuery("ChangeNew.selectAll", ChangeNew.class);
        List<ChangeNew> allChanges = new LinkedList<>(query2.getResultList());

        for (ChangeNew change : allChanges) {
            System.out.println(change);
        }
        System.out.println("----------------------------------");
        query2 = em.createNamedQuery("ChangeNew.selectAllInterval", ChangeNew.class);
        query2.setParameter("vonDate", LocalDateTime.now().minusDays(10));
        query2.setParameter("bisDate", LocalDateTime.now().plusDays(5));
        allChanges = new LinkedList(query2.getResultList());

        for (ChangeNew change : allChanges) {
            System.out.println(change);
        }

        System.out.println("----------------------------------");

        query2 = em.createNamedQuery("ChangeNew.selectAllIntervalUser", ChangeNew.class);
        query2.setParameter("vonDate", LocalDateTime.now().minusDays(10));
        query2.setParameter("bisDate", LocalDateTime.now().plusDays(5));
        query2.setParameter("userId", n3.getNutzerid());
        allChanges = new LinkedList(query2.getResultList());

        for (ChangeNew change : allChanges) {
            System.out.println(change);
        }

        TypedQuery<DurchlaufNew> query3 = em.createNamedQuery("DurchlaufNew.selectAll", DurchlaufNew.class);
        List<DurchlaufNew> allDurchlauf = new LinkedList<>(query3.getResultList());

        for (DurchlaufNew durchlauf : allDurchlauf) {
            System.out.println(durchlauf);
        }
        System.out.println("----------------------------------");
        query3 = em.createNamedQuery("DurchlaufNew.selectAllInterval", DurchlaufNew.class);
        query3.setParameter("vonDate", LocalDateTime.now().minusDays(10));
        query3.setParameter("bisDate", LocalDateTime.now().plusDays(5));
        allDurchlauf = new LinkedList(query3.getResultList());

        for (DurchlaufNew durchlauf : allDurchlauf) {
            System.out.println(durchlauf);
        }

        System.out.println("----------------------------------");

        query3 = em.createNamedQuery("DurchlaufNew.selectAllIntervalUser", DurchlaufNew.class);
        query3.setParameter("vonDate", LocalDateTime.now().minusDays(10));
        query3.setParameter("bisDate", LocalDateTime.now().plusDays(5));
        query3.setParameter("userId", n3.getNutzerid());
        allDurchlauf = new LinkedList(query3.getResultList());

        for (DurchlaufNew durchlauf : allDurchlauf) {
            System.out.println(durchlauf);
        }
    }*/

    public void close() {
        em.close();
        emf.close();
    }

    public static void main(String[] args) {
        testing t = new testing();
        t.open();
       // t.test();
        t.close();
    }
}
