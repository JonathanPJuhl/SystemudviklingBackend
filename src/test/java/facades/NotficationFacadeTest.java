
package facades;

import entities.*;
import org.junit.jupiter.api.*;
import security.errorhandling.AuthenticationException;
import utils.EMF_Creator;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

//Uncomment the line below, to temporarily disable this test
//@Disabled
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
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
        ArrayList<String> tickers = new ArrayList<>();
        tickers.add("AAPL");
        tickers.add("MSFT");
        tickers.add("VOD");
        tickers.add("FB");
        tickers.add("GOOGL");
        sfacade.addStockTickersToDB(tickers);
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

            em.getTransaction().commit();}
        finally{
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




//        Clean up database after test is done or use a persistence unit with drop-and-create to start up clean on every test
    }

    // Setup the DataBase in a known state BEFORE EACH TEST
    //TODO -- Make sure to change the code below to use YOUR OWN entity class
    @BeforeEach
    public void setUp() {
        EntityManager em = emf.createEntityManager();


    }

    @AfterEach
    public void tearDown() {

    }


    @Test
    @Order(4)
    public void tickersShouldBeAddedToDBAndFound(){
        List<StockSymbol> tickers = sfacade.getAllStockTickers();
        assertTrue(tickers.size()==5);
    }

    @Test
    @Order(2)
    public void dailyRatingsShouldBeAddedToDB(){
        List<DailyStockRating> dsr = sfacade.returnDailyStockRatings("ASC");
        assertTrue(dsr.size()!=0);
    }



    @Test
    @Order(2)
    public void addNotiThreshAndCheckIt(){
        EntityManager em = emf.createEntityManager();
        sfacade.AddToDb("AAPL", "user");
        sfacade.returnDailyStockRatings("ASC");
        facade.AddNotiThreshToDb("user", "AAPL", 5);

        em.getTransaction().begin();
        DailyStockRating dsr =  em.find(DailyStockRating.class, "AAPL");
        User user = em.find(User.class, "user");
        TypedQuery<UserStockNoti> findUserStockNoti = em.createQuery("SELECT u from UserStockNoti u WHERE u.user = :user", UserStockNoti.class);
        findUserStockNoti.setParameter("user", user);
        UserStockNoti userStockNoti = findUserStockNoti.getSingleResult();
        userStockNoti.setClose(50);
        dsr.setRate(10);
        em.merge(userStockNoti);
        em.merge(dsr);
        em.getTransaction().commit();
        em.close();

        facade.checkThresholds();
        List<Notifications> notis = facade.displayUsersNotis("user");
        assertTrue(notis.size()==1);


    }


    @Test
    @Order(3)
    public void statusShouldChange(){
        EntityManager em = emf.createEntityManager();
        facade.updateNotiStatus(1, false);
        Notifications n = em.find(Notifications.class, 1);
        assertTrue(n.isStatus() == false);


    }
    @Test
    @Order(5)
    public void notiShouldBeDeleted(){
        EntityManager em = emf.createEntityManager();
        facade.deleteNoti(1);
        Notifications n = em.find(Notifications.class, 1);
        //assertThrows(NoResultException.class, () -> {em.find(Notifications.class, 1); } );
        assertTrue(n==null);

    }


}

