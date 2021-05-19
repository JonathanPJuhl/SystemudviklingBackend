
package facades;

import entities.Role;
import entities.Stock;
import entities.StockSymbol;
import entities.User;
import org.junit.jupiter.api.*;
import security.errorhandling.AuthenticationException;
import utils.EMF_Creator;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

//Uncomment the line below, to temporarily disable this test
@Disabled
public class StockFacadeTest {

    private static EntityManagerFactory emf;
    private static StockFacade facade;

    public StockFacadeTest() {
    }

    @BeforeAll
    public static void setUpClass() {
       emf = EMF_Creator.createEntityManagerFactoryForTest();
       facade = StockFacade.getFacadeExample(emf);
    }

    @AfterAll
    public static void tearDownClass() {
//        Clean up database after test is done or use a persistence unit with drop-and-create to start up clean on every test
    }

    // Setup the DataBase in a known state BEFORE EACH TEST
    //TODO -- Make sure to change the code below to use YOUR OWN entity class
    @BeforeEach
    public void setUp() {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.createQuery("delete from User").executeUpdate();
            em.createQuery("delete from Role").executeUpdate();
            Role userRole = new Role("user");
            Role adminRole = new Role("admin");
            User user = new User("user", "test");
            user.addRole(userRole);
            User admin = new User("admin", "test");
            admin.addRole(adminRole);
            User both = new User("user_admin", "test");
            both.addRole(userRole);
            both.addRole(adminRole);
            em.persist(userRole);
            em.persist(adminRole);
            em.persist(user);
            em.persist(admin);
            em.persist(both);

            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    @AfterEach
    public void tearDown() {
//        Remove any data after each test was run
    }

    @Test
    public void testGetVerifiedUserShouldWork() throws AuthenticationException {
       /* User user = facade.getVeryfiedUser("user", "test");
        assertTrue(user!=null);*/
    }

    @Test
    public void testGetVerifiedUserShouldntWork()  {
      /*  assertThrows(security.errorhandling.AuthenticationException.class, () -> facade.getVeryfiedUser("user", "ae"));*/
    }
    @Test
    public void tickerShouldBeAddedToDBAndFound(){
        facade.AddToDb("AAPL", "user");
        List<String> pins = facade.getPinnedStocks("user");
        assertTrue(pins.size()==1 && pins.get(0).equals("AAPL"));
    }
    @Test
    public void addAndGetListOfTickers(){
        ArrayList<String> s = new ArrayList<>();
        s.add("AAPL");
        s.add("MSFT");
        facade.addStockTickersToDB(s);
        List<StockSymbol> pins = facade.getAllStockTickers();
        assertTrue(pins.size()==2);
    }
    @Test
    public void tickerShouldBeDeletedFromUser(){
        facade.AddToDb("AAPL", "user");
        List<String> pins = facade.getPinnedStocks("user");
        assertTrue(pins.size()==1 && pins.get(0).equals("AAPL"));
        facade.deleteTickerFromUser("user", "AAPL");
        List<String> pins2 = facade.getPinnedStocks("user");
        assertTrue(pins2.size()==0);

    }

}

