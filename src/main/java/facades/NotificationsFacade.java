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
 * Rename Class to a relevant name Add add relevant facade methods
 */
public class NotificationsFacade {

    private static NotificationsFacade instance;
    private static EntityManagerFactory emf;
    private UserFacade facade = UserFacade.getUserFacade(emf);

    //Private Constructor to ensure Singleton
    private NotificationsFacade() {
    }


    /**
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
        User user = userFacade.findUserByUsername(username);
        em2.getTransaction().commit();
        em2.close();

        UserStockNoti stocksForUpdate = null;
        Stock stock = em.find(Stock.class, ticker);
        StockFacade sF = StockFacade.getFacadeExample(emf);

        double currentClosingValue = sF.getCurrentClosingValue(ticker);

        for (int i = 0; i < user.getStockList().size(); i++) {
            if (user.getStockList().get(i).getStockTicker().equals(ticker))
            stocksForUpdate = (new UserStockNoti(user, stock, valueInPercent, currentClosingValue));
        }

        UserStockNoti foundStockNoti = em.find(UserStockNoti.class, username + ticker);

        if (foundStockNoti != null) {
            foundStockNoti = stocksForUpdate;
            try {
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
        for (int i = 0; i < users.size(); i++) {

            //Finds everythnig from stockNoti and makes list, based on username
            TypedQuery<UserStockNoti> findUserStockNoti = em.createQuery("SELECT u from UserStockNoti u WHERE u.user = :user", UserStockNoti.class);
            TypedQuery<User> findUser = em.createQuery("SELECT u from User u WHERE u.username = :user", User.class);


            findUser.setParameter("user", users.get(i).getUsername());
            User user = findUser.getSingleResult();
            findUserStockNoti.setParameter("user", user);
            List<UserStockNoti> userStockNoti = findUserStockNoti.getResultList();

            //Get ticker, value, close for hver
            //sammenlign ny close med denne og tjek om det er højere end 5 eller lavere end -5 f.eks.

            //Checks each notificationsetting
            for (int j = 0; j < userStockNoti.size(); j++) {
                String ticker = userStockNoti.get(j).getStock().getStockTicker();
                double close = userStockNoti.get(j).getClose();
                int thresh = userStockNoti.get(j).getThreshold();
                System.out.println("THRESH: " + thresh);
                //compares noti-setting to daily closing values
                for (int h = 0; h < dailyratings.size(); h++) {

                    //Checks to find the stockticker in dailyratings
                    if (dailyratings.get(h).getStockTicker().equals(ticker)) {
                        double rate = 0.0;
                        //Calculates change from yesterdays close in percent
                        if (close > 0) {
                            rate = 100 - (dailyratings.get(h).getClose() / close) * 100;
                        } else {
                            rate = 100 + (dailyratings.get(h).getClose() / close) * 100;
                        }
                        //Checks for positive spike
                        if (rate > 0) {
                            System.out.println("r + t " + rate + "    " + thresh);
                            if (rate > thresh) {
                                addNotiToDB(rate, ticker, dailyratings.get(h).getDate().substring(0, 10), user);
                            }
                            //Checks for negative spike
                        } else {
                            if (rate < (-thresh)) {
                                addNotiToDB(rate, ticker, dailyratings.get(h).getDate().substring(0, 10), user);
                            }
                        }
                    }
                }
            }
        }
        em.close();

    }

    public void addNotiToDB(double rate, String ticker, String date, User user) {
        EntityManager em = emf.createEntityManager();
        String messageInput = "";
        if (rate > 0) {
            messageInput = " has gained over: ";
        } else {
            messageInput = " has dropped below: ";
        }
        String message = ticker + messageInput + rate + "% today";
        boolean status = true; // This is the same as "new"
        Stock stock = em.find(Stock.class, ticker);
        Notifications noti = new Notifications(date, status, message, stock);

        //Check to see if we already made this specific notification - there can only be one per stock per day
        TypedQuery<Notifications> foundNoti = em.createQuery("SELECT n from Notifications n WHERE n.date =:date AND n.message =:message", Notifications.class);
        foundNoti.setParameter("date", date);
        foundNoti.setParameter("message", message);
        List<Notifications> checkWithList = foundNoti.getResultList();

        if (checkWithList.size() != 0) {
            user.addNoti(noti);
            em.getTransaction().begin();
            em.persist(noti);
            em.merge(user);
            em.getTransaction().commit();
        } else if (checkWithList.size() == 0) {
            System.out.println("Noti already exists");
        }
        em.close();
    }

    public List<Notifications> displayUsersNotis(String username) {
        EntityManager em = emf.createEntityManager();

        TypedQuery<Notifications> getNotis = em.createQuery("SELECT n FROM Notifications n JOIN User  u where u.username = :username", Notifications.class);
        getNotis.setParameter("username", username);

        List<Notifications> notiList = getNotis.getResultList();

        return notiList;
    }

    public void updateNotiStatus(int id, boolean status) {
        EntityManager em = emf.createEntityManager();

        Notifications noti = em.find(Notifications.class, id);
        noti.setStatus(status);
        em.getTransaction().begin();
        em.merge(noti);
        em.getTransaction().commit();
        em.close();

    }

    public void deleteNoti(int id) {
        EntityManager em = emf.createEntityManager();

        Notifications noti = em.find(Notifications.class, id);

        em.getTransaction().begin();
        em.remove(noti);
        em.getTransaction().commit();
        em.close();
    }
}
