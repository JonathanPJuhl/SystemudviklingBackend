
package facades;

import entities.*;
import org.junit.jupiter.api.*;
import security.errorhandling.AuthenticationException;
import utils.EMF_Creator;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

//Uncomment the line below, to temporarily disable this test
@Disabled
public class NotficationFacadeTest {

    private static EntityManagerFactory emf;
    private static StockFacade sfacade;
    private static NotificationsFacade facade;

    public NotficationFacadeTest() {
    }

    @BeforeAll
    public static void setUpClass() {
       emf = EMF_Creator.createEntityManagerFactoryForTest();
       sfacade = StockFacade.getFacadeExample(emf);
       facade = NotificationsFacade.getFacadeExample(emf);
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
        ArrayList<String> tickers = new ArrayList<>();
        tickers.add("AAPL");
        tickers.add("MSFT");
        tickers.add("VOD");
        tickers.add("FB");
        tickers.add("GOOGL");
        sfacade.addStockTickersToDB(tickers);
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
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        em.createQuery("delete from StockSymbol").executeUpdate();
        em.getTransaction().commit();
        em.close();
    }


    @Test
    public void tickersShouldBeAddedToDBAndFound(){
        List<StockSymbol> tickers = sfacade.getAllStockTickers();
        assertTrue(tickers.size()==5);
    }
    @Test
    public void dailyRatingsShouldBeAddedToDB(){
        List<DailyStockRating> dsr = sfacade.returnDailyStockRatings("ASC");
        assertTrue(dsr.size()!=0);
    }


    @Test
    public void addNotiThreshAndCheckIt(){
        EntityManager em = emf.createEntityManager();
        sfacade.AddToDb("AAPL", "user");
        sfacade.returnDailyStockRatings("ASC");
        facade.AddNotiThreshToDb("user", "AAPL", 5);


        facade.checkThresholds();
        List<Notifications> notis = facade.displayUsersNotis("user");
        assertTrue(notis.size()==1);
    }
    /*
    @Test
    public void tickerShouldBeDeletedFromUser(){
        facade.AddToDb("AAPL", "user");
        List<String> pins = facade.getPinnedStocks("user");
        assertTrue(pins.size()==1 && pins.get(0).equals("AAPL"));
        facade.deleteTickerFromUser("user", "AAPL");
        List<String> pins2 = facade.getPinnedStocks("user");
        assertTrue(pins2.size()==0);

    }*/

}

