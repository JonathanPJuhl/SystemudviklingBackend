
package facades;

import entities.*;
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
        ArrayList<String> tickers = new ArrayList<>();
        tickers.add("AAPL");
        tickers.add("MSFT");
        tickers.add("VOD");
        tickers.add("FB");
        tickers.add("GOOGL");
        facade.addStockTickersToDB(tickers);
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
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

    @AfterAll
    public static void tearDownClass() {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            em.createQuery("delete from Role").executeUpdate();
            em.createQuery("delete from StockSymbol").executeUpdate();
            em.createQuery("delete from UserStockNoti").executeUpdate();
            em.createQuery("delete from User").executeUpdate();

            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    // Setup the DataBase in a known state BEFORE EACH TEST
    //TODO -- Make sure to change the code below to use YOUR OWN entity class
    @BeforeEach
    public void setUp() {

    }

    @AfterEach
    public void tearDown() {
//        Remove any data after each test was run
    }

    @Test
    public void testGetClosingValueShouldReturnRightValue() throws AuthenticationException, InterruptedException {
        EntityManager em = emf.createEntityManager();
        facade.returnDailyStockRatings("ASC");
        DailyStockRating dsr =  em.find(DailyStockRating.class, "AAPL");
        double trueClose = 1213.14;
        dsr.setClose(trueClose);
        em.getTransaction().begin();
        em.merge(dsr);
        em.getTransaction().commit();

       double value = facade.getCurrentClosingValue("AAPL");
       assertEquals(value, trueClose);
    }
    @Test
    public void findFiveHighestGainsAndDropsShouldMakeRightLists() throws InterruptedException {
        EntityManager em = emf.createEntityManager();
        facade.returnDailyStockRatings("ASC");

        DailyStockRating dsr =  em.find(DailyStockRating.class, "FB");
        DailyStockRating dsr1 =  em.find(DailyStockRating.class, "GOOGL");
        DailyStockRating dsr2 =  em.find(DailyStockRating.class, "MSFT");
        DailyStockRating dsr3 =  em.find(DailyStockRating.class, "VOD");

        em.getTransaction().begin();
        dsr.setRate(10);
        em.merge(dsr);
        dsr1.setRate(5);
        em.merge(dsr1);
        dsr2.setRate(3);
        em.merge(dsr2);
        dsr3.setRate(1);
        em.merge(dsr3);
        em.getTransaction().commit();
        em.close();
        List<DailyStockRating> asc = facade.findFiveHighestGainsOrDropsFromDB("ASC", "second");
        List<DailyStockRating> desc = facade.findFiveHighestGainsOrDropsFromDB("DESC", "second");

        assertTrue(asc.get(0).getStockTicker().equals("AAPL") && asc.get(1).getStockTicker().equals("VOD")
                && asc.get(2).getStockTicker().equals("MSFT") && asc.get(3).getStockTicker().equals("GOOGL")
                && asc.get(4).getStockTicker().equals("FB"));
        assertTrue(desc.get(0).getStockTicker().equals("FB") && desc.get(1).getStockTicker().equals("GOOGL")
                && desc.get(2).getStockTicker().equals("MSFT") && desc.get(3).getStockTicker().equals("VOD")
                && desc.get(4).getStockTicker().equals("AAPL"));

    }

    /*@Test
    public void testGetVerifiedUserShouldntWork()  {
      *//*  assertThrows(security.errorhandling.AuthenticationException.class, () -> facade.getVeryfiedUser("user", "ae"));*//*
    }*/

    @Test
    public void tickerShouldBeAddedToUsersDBAndPinnedStocksShouldBeFound(){
        facade.AddToDb("VOD", "user");
        List<String> usersTickers = facade.getPinnedStocks("user");
        //size==2 because of one more addition in the noti-thresh-check
        assertTrue(usersTickers.size()==1);
    }
    @Test
    public void addAndGetListOfTickers(){
        ArrayList<String> s = new ArrayList<>();
        s.add("XXXX");
        s.add("YYYY");
        facade.addStockTickersToDB(s);
        List<StockSymbol> pins = facade.getAllStockTickers();
        assertTrue(pins.size()==7);
    }
    @Test
    public void tickerShouldBeDeletedFromUser(){
        List<String> pins = facade.getPinnedStocks("user");
        assertTrue(pins.size()==1);
        facade.deleteTickerFromUser("user", "VOD");
        List<String> pins2 = facade.getPinnedStocks("user");
        assertTrue(pins2.size()==0);

    }

}

