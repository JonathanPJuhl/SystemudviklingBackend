package facades;

import SVG.ChartMaker;
import com.nimbusds.jose.shaded.json.JSONObject;
import entities.DailyStockRating;
import entities.Stock;
import entities.StockSymbol;
import entities.User;


import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.HashMap;
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

    public List<String> getPinnedStocks(String username) {
        EntityManager em = emf.createEntityManager();
        System.out.println(username);

        Query stocks = em.createQuery("SELECT s.stockTicker from Stock s join s.userList u where u.username = :username");
        stocks.setParameter("username", username);
        //User user = em.find(User.class, username);
       // List<Stock> stocks=user.getStockList();

       /* for(int i=0; i<user2.size()-1;i++){
            stocks.add(new Stock(user2.get(i)));
        }*/
        List<String> stock = stocks.getResultList();

            em.close();



        return stock;
    }

    public List<StockSymbol> getAllStockTickers() {
        EntityManager em = emf.createEntityManager();
        List<StockSymbol> symbolList = new ArrayList<>();
        try{
            em.getTransaction().begin();
            TypedQuery<StockSymbol> symbols = em.createQuery("SELECT s.symbol FROM StockSymbol s", StockSymbol.class);
            symbolList = symbols.getResultList();
            em.getTransaction().commit();
            }finally {

            em.close();
        }
       /* List<String> strings = new ArrayList<>();
        for(int i = 0; i<symbolList.size(); i++){
            strings.add(symbolList.get(i).getSymbol());
        }*/
    return symbolList;
    }

    public void addStockTickersToDB(ArrayList<String> fromJson) {
        EntityManager em = emf.createEntityManager();
        try{
        em.getTransaction().begin();
       for (int i = 0; i<fromJson.size(); i++){
           StockSymbol symbol = new StockSymbol(fromJson.get(i));
           em.persist(symbol);
       }}finally {
            em.getTransaction().commit();
            em.close();
        }


    }


    public void addDailyStockRatingsToDB(ArrayList<DailyStockRating> dailyStocks){
        EntityManager em = emf.createEntityManager();
        try{
            em.getTransaction().begin();
            for (int i = 0; i<dailyStocks.size(); i++){
                em.merge(dailyStocks.get(i));
            }
            em.getTransaction().commit();
        }finally {
            em.close();
        }
    }
    public List<DailyStockRating> findFiveHighestGainsOrDropsFromDB(String ascendOrDescend){
        EntityManager em = emf.createEntityManager();
        List<DailyStockRating> sortedList;
        try{

                em.getTransaction().begin();
                TypedQuery<DailyStockRating> findAllDailyStocksSorted = null;
                if(ascendOrDescend.equals("ASC")){
                    findAllDailyStocksSorted = em.createQuery("SELECT d from DailyStockRating d ORDER BY d.rate ASC", DailyStockRating.class);
                }else if(ascendOrDescend.equals("DESC")){
                    findAllDailyStocksSorted = em.createQuery("SELECT d from DailyStockRating d ORDER BY d.rate DESC", DailyStockRating.class);
                }
                sortedList = findAllDailyStocksSorted.getResultList();
                em.getTransaction().commit();

        }finally {
            em.close();
        }
        if(sortedList.size()==0){
            return sortedList;
        } else {

            return sortedList.subList(0, 5);
        }
    }

    public String makeChart(ArrayList<DailyStockRating> jsonArrayTimes) {
        ChartMaker chartMaker = new ChartMaker();

        return chartMaker.draw(jsonArrayTimes);
    }
}
