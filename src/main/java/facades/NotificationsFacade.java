package facades;

import SVG.ChartMaker;
import entities.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * Rename Class to a relevant name Add add relevant facade methods
 */
public class NotificationsFacade {

    private static NotificationsFacade instance;
    private static EntityManagerFactory emf;
    private UserFacade facade = UserFacade.getUserFacade(emf);

    //Private Constructor to ensure Singleton
    private NotificationsFacade() {}
    
    
    /**
     * 
     * @param _emf
     * @return an instance of this facade class.
     */
    public static NotificationsFacade getFacadeExample(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new NotificationsFacade();
        }
        return instance;
    }

    private EntityManager getEntityManager() {
        return emf.createEntityManager();
    }
    


    public void AddNotiThreshToDb(String username, String ticker, int valueInPercent) {
        EntityManager em = emf.createEntityManager();
        EntityManager em2 = emf.createEntityManager();
        UserFacade userFacade = UserFacade.getUserFacade(emf);
        em2.getTransaction().begin();
        User user =  userFacade.findUserByUsername(username);
        em2.getTransaction().commit();
        em2.close();
        UserStockNoti stocksForUpdate = null;
        Stock stock =  em.find(Stock.class, ticker);
        StockFacade sF = StockFacade.getFacadeExample(emf);
        double currentClosingValue = sF.getCurrentClosingValue(ticker);
        for(int i = 0; i <user.getStockList().size(); i++){
            if(user.getStockList().get(i).getStockTicker().equals(ticker))
            System.out.println("USER: " + user.getUsername());
            System.out.println(" STOCK: " + stock.getStockTicker());
            System.out.println( " VALUE " +valueInPercent);
                stocksForUpdate = (new UserStockNoti(user, stock, valueInPercent, currentClosingValue));
            }
    UserStockNoti foundStockNoti = em.find(UserStockNoti.class, username+ticker);
    if(foundStockNoti!=null){
        foundStockNoti = stocksForUpdate;
        try{
            em.getTransaction().begin();
            em.merge(foundStockNoti);
            em.getTransaction().commit();

        } finally {
            em.close();
        }
    } else {

        try {
            em.getTransaction().begin();
            em.persist(stocksForUpdate);
            em.getTransaction().commit();

        } finally {
            em.close();
        }
    }
    }

    public void checkThresholds() {
        EntityManager em = emf.createEntityManager();

        //Finds all users and adds them to list
        TypedQuery<User> findUsers = em.createQuery("SELECT u from User u", User.class);
        List<User> users = findUsers.getResultList();

        //Finds all dailystockratings and adds them to list
        TypedQuery<DailyStockRating> findDaily = em.createQuery("SELECT d from DailyStockRating d", DailyStockRating.class);
        List<DailyStockRating> dailyratings = findDaily.getResultList();

        //For each user in the list, a check is made, to find all notification wishes from userStockNoti belonging to the user
        for(int i=0; i< users.size(); i++){
            //Finds everythnig from stockNoti and makes list, based on username
            TypedQuery<UserStockNoti> findUserStockNoti = em.createQuery("SELECT u from UserStockNoti u WHERE u.user = :user", UserStockNoti.class);
            findUserStockNoti.setParameter("user", users.get(i).getUsername());
            List<UserStockNoti> userStockNoti = findUserStockNoti.getResultList();

            //Get ticker, value, close for hver
            //sammenlign ny close med denne og tjek om det er højere end 5 eller lavere end -5 f.eks.

            //Checks each notificationsetting
            for(int j=0; j<userStockNoti.size(); j++){
                String ticker = userStockNoti.get(j).getStock().getStockTicker();
                double close = userStockNoti.get(j).getClose();
                int thresh = userStockNoti.get(j).getThreshold();
                    //compares noti-setting to daily closing values
                    for(int h = 0 ; h<dailyratings.size(); h++){

                        //Checks to find the stockticker in dailyratings
                        if(dailyratings.get(h).getStockTicker().equals(ticker)){
                            double rate = 0.0;
                            //Calculates change from yesterdays close in percent
                            if(close>0) {
                                rate = 100-(dailyratings.get(h).getClose() / close) * 100;
                            } else{
                                rate = 100+(dailyratings.get(h).getClose() / close) * 100;
                            }
                            //Checks for positive spike
                            if(rate>0){
                               if(rate>thresh){
                                   //ADD NOTI TO DB
                               }
                                //Checks for negative spike
                           } else{
                               if(rate<thresh){
                                   //ADD NOTI TO DB
                               }
                           }
                        }
                    }
            }
        }




        Query stocks = em.createQuery("SELECT s.stockTicker from Stock s join s.userList u where u.username = :username");
        List<String> stock = stocks.getResultList();

            em.close();



       // return stock;
    }

}
