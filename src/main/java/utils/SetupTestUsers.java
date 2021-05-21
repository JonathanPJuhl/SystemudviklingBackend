package utils;


import entities.Role;
import entities.Stock;
import entities.User;
import facades.StockFacade;
import rest.StockResource;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

public class SetupTestUsers {
  public void populate(){
    EntityManagerFactory emf = EMF_Creator.createEntityManagerFactory();
    EntityManager em = emf.createEntityManager();


    User user = new User("user", "usertest");
    User admin = new User("admin", "admintest");
    User both = new User("user_admin", "bothtest");
    StockResource sR = new StockResource();
    if(admin.getPassword().equals("test")||user.getPassword().equals("test")||both.getPassword().equals("test"))
      throw new UnsupportedOperationException("You have not changed the passwords");

    em.getTransaction().begin();
    Role userRole = new Role("user");
    Role adminRole = new Role("admin");

    em.persist(userRole);
    em.persist(adminRole);
    em.persist(user);
    sR.fillDb();
   StockFacade sF = StockFacade.getFacadeExample(emf);
   sF.timeCheckForLocal("fillForPopulate");
    user.addRole(userRole);
    admin.addRole(adminRole);
    both.addRole(userRole);
    both.addRole(adminRole);
    em.getTransaction().commit();
    System.out.println("PW: " + user.getPassword());
    System.out.println("Testing user with OK password: " + user.verifyPassword("test"));
    System.out.println("Testing user with wrong password: " + user.verifyPassword("test1"));
    System.out.println("Created TEST Users");
  }

  public static void main(String[] args) {

    EntityManagerFactory emf = EMF_Creator.createEntityManagerFactory();
    EntityManager em = emf.createEntityManager();
    
    // IMPORTAAAAAAAAAANT!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    // This breaks one of the MOST fundamental security rules in that it ships with default users and passwords
    // CHANGE the three passwords below, before you uncomment and execute the code below
    // Also, either delete this file, when users are created or rename and add to .gitignore
    // Whatever you do DO NOT COMMIT and PUSH with the real passwords

    User user = new User("user", "usertest");
    User admin = new User("admin", "admintest");
    User both = new User("user_admin", "bothtest");

    if(admin.getPassword().equals("test")||user.getPassword().equals("test")||both.getPassword().equals("test"))
      throw new UnsupportedOperationException("You have not changed the passwords");

    em.getTransaction().begin();
    Role userRole = new Role("user");

    user.addRole(userRole);

    both.addRole(userRole);

    em.persist(userRole);

    em.persist(user);
    em.persist(admin);
    em.persist(both);
    em.getTransaction().commit();
    StockResource sR = new StockResource();
    sR.fillDb();
    System.out.println("PW: " + user.getPassword());
    System.out.println("Testing user with OK password: " + user.verifyPassword("test"));
    System.out.println("Testing user with wrong password: " + user.verifyPassword("test1"));
    System.out.println("Created TEST Users");
   
  }

}
