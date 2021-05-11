package facades;

import com.nimbusds.jose.shaded.json.JSONObject;
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

    public ArrayList<String> getFiveBiggestGains(HashMap<String, Double> pin, HashMap<String, Double> pinYesterday) {
        //sammenlign keys fra maps og indsæt kun i array, hvis key er i begge
        ArrayList<String> fiveBiggestGains = new ArrayList();
        fiveBiggestGains.add("a, 0");
        fiveBiggestGains.add("b, 1");
        fiveBiggestGains.add("c, 2");
        fiveBiggestGains.add("d, 3");
        fiveBiggestGains.add("e, 4");
        ArrayList<Double> pinned = new ArrayList<Double>(pin.values());
        ArrayList<String> pinnedSymbol = new ArrayList<String>(pin.keySet());
        ArrayList<Double> pinnedYesterday = new ArrayList<Double>(pinYesterday.values());
        ArrayList<String> pinnedYesterdaySymbol = new ArrayList<String>(pinYesterday.keySet());
        HashMap<String, Double> finalMapToday = new HashMap<>();
        for(int i = 0; i<pinnedYesterdaySymbol.size(); i++){

            if(pinnedSymbol.contains(pinnedYesterdaySymbol.get(i))) {
                finalMapToday.put(pinnedYesterdaySymbol.get(i), pin.get(pinnedYesterdaySymbol.get(i)));
                //hvis dette (i) ligger et sted i arraylisten, så gør ingenting, hvis det ikke kan findes, så fjern dette (j) fra arraylisten
            }
        }
        ArrayList<Double> pinnedTodayFinal = new ArrayList<Double>(pin.values());
        ArrayList<String> pinnedFinalSymbol = new ArrayList<String>(pin.keySet());
        for(int i = 0; i<pinnedTodayFinal.size(); i++){

            double valueToday = pinnedTodayFinal.get(i);
            double valueYesterday = pinnedYesterday.get(i);
            if(valueToday>valueYesterday){
                double percentage = valueToday/valueYesterday*100;
                for(int j = 0; j<5; j++){
                    String[] fivebiggestToday = fiveBiggestGains.get(j).split(",");
                    double fiveValue = Double.parseDouble(fivebiggestToday[1]);
                    if(percentage>fiveValue){
                        fiveBiggestGains.remove(j);
                        fiveBiggestGains.add(j, pinnedSymbol.get(i)+","+pinned.get(i).toString());
                    }
                }
            }
        }

        return fiveBiggestGains;
    }

    public ArrayList<String> getFiveBiggestDrops(ArrayList<String> pinned, ArrayList<String> pinnedYesterday) {
        return null;
    }
}
