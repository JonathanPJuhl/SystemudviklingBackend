package facades;

import entities.Stock;
import entities.User;

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
public class StockFacade {

    private static StockFacade instance;
    private static EntityManagerFactory emf;
    private UserFacade facade = UserFacade.getUserFacade(emf);
    
    //Private Constructor to ensure Singleton
    private StockFacade() {}
    
    
    /**
     * 
     * @param _emf
     * @return an instance of this facade class.
     */
    public static StockFacade getFacadeExample(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new StockFacade();
        }
        return instance;
    }

    private EntityManager getEntityManager() {
        return emf.createEntityManager();
    }
    


    public void AddToDb(String ticker, String username) {
        EntityManager em = emf.createEntityManager();
        EntityManager em2 = emf.createEntityManager();
        UserFacade userFacade = UserFacade.getUserFacade(emf);
        em2.getTransaction().begin();
        User user =  userFacade.findUserByUsername(username);
        em2.getTransaction().commit();
        em2.close();
        Stock stock = new Stock(ticker);
        System.out.println(stock.toString());
        System.out.println(user.toString());
        user.addStock(stock);
        try{
            em.getTransaction().begin();
            Stock foundStock = em.find(Stock.class, ticker);
            if(foundStock!=null){
            user.addStock(foundStock);
            em.merge(user);
            em.getTransaction().commit();
            }

            else{
                em.persist(stock);

                em.merge(user);
            em.getTransaction().commit();
        }
        }finally{
            em.close();
        }
    }

    public User getPinnedStocks(String username) {
        EntityManager em = emf.createEntityManager();
        System.out.println(username);

        User user = em.find(User.class, username);
        List<Stock> stocks=user.getStockList();

       /* for(int i=0; i<user2.size()-1;i++){
            stocks.add(new Stock(user2.get(i)));
        }*/

            em.close();



        return user;
    }
}
