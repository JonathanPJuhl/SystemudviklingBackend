package facades;

import SVG.ChartMaker;
import entities.DailyStockRating;
import entities.Stock;
import entities.StockSymbol;
import entities.User;
import org.json.JSONArray;
import org.json.JSONObject;


import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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
        List<String> stock = stocks.getResultList();
        em.close();

        return stock;
    }

    public List<StockSymbol> getAllStockTickers() {
        EntityManager em = emf.createEntityManager();
        List<StockSymbol> symbolList;
        try{
            em.getTransaction().begin();
            TypedQuery<StockSymbol> symbols = em.createQuery("SELECT s.symbol FROM StockSymbol s", StockSymbol.class);
            symbolList = symbols.getResultList();
            em.getTransaction().commit();
            }finally {

            em.close();
        }

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

    public List<DailyStockRating> findFiveHighestGainsOrDropsFromDB(String ascendOrDescend, String firstOrSecond){
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
            System.out.println(sortedList);
                em.getTransaction().commit();
        }
        finally {
            em.close();
        }
        if(sortedList.size()==0){
            return sortedList;
        } else {
            if(firstOrSecond.equals("first")){
                return sortedList;
            }else{
            return sortedList.subList(0, 5);
            }
        }
    }

    public String makeChart(ArrayList<DailyStockRating> jsonArrayTimes) {
        ChartMaker chartMaker = new ChartMaker();
        return chartMaker.draw(jsonArrayTimes);
    }

    public String deleteTickerFromUser(String username, String ticker) {
        EntityManager em = emf.createEntityManager();
        User user = em.find(User.class, username);
        List<Stock> listOfStocks = user.getStockList();
        List<Stock> newListOfStocks = new ArrayList<>();

        for(int i = 0 ; i<listOfStocks.size(); i++){
            if(!(listOfStocks.get(i).getStockTicker().equals(ticker))){
                newListOfStocks.add(new Stock(listOfStocks.get(i).getStockTicker()));
            }
        }

        user.setStockList(newListOfStocks);
        em.getTransaction().begin();
        em.merge(user);
        em.getTransaction().commit();
        em.close();

        return "Success deleting: " + ticker +" from user: " + username;
    }

    public double getCurrentClosingValue(String ticker) {
        EntityManager em = emf.createEntityManager();
        DailyStockRating dSR = em.find(DailyStockRating.class, ticker);
        double close = dSR.getClose();
        return close;
    }
    public void timeCheckForLocal(String fill) {
        String accessKeyMarketstack = "80f90dbc8de86858f292e8e8ff76293f";
        String symbols = "";
        List<DailyStockRating> dR = findFiveHighestGainsOrDropsFromDB("ASC", "first");
        List<StockSymbol> tickers = getAllStockTickers();

        for (int i = 0; i < tickers.size(); i++) {
            if (i != tickers.size() - 1) {
                symbols += tickers.get(i) + ",";
            } else {
                symbols += tickers.get(i);
            }

        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        Calendar rightNow = Calendar.getInstance();
        int hour = rightNow.get(Calendar.HOUR_OF_DAY);
        //Using hour because stockdata is not released until 5 in the afternoon in the us (therefore 23 in  DK)
        String thisDay = LocalDate.now().format(formatter);
        String data ="";
        //Checks if there's been fetched to day already , if there has, it won't do it again
        if (dR.size() == 0 || (hour >= 23 && !dR.get(0).getDate().equals(thisDay) || fill.equals("fillForPopulate"))) {
            try {
                data = fetchData("https://api.marketstack.com/v1/eod/latest?access_key=" + accessKeyMarketstack + "&symbols=" + symbols);
            } catch (IOException e) {
                e.printStackTrace();
            }


            // String data = timeCheckForOnline();
            org.json.JSONObject json = new org.json.JSONObject(data);
            JSONArray jsonArray = json.getJSONArray("data");

            ArrayList<DailyStockRating> jsonArrayTimes = new ArrayList<>();

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject item = jsonArray.getJSONObject(i);
                String symb = (String) item.get("symbol");
                String date = (String) item.get("date");
                double close = (double) item.get("close");
                if (symb.contains(".")) {
                    jsonArray.remove(i);
                }
                if (!symb.contains(".")) {
                    jsonArrayTimes.add(new DailyStockRating(symb, date, close));
                }
            }
            //sorting the two arrays to get them in same order, before claculating the daily rate
            Collections.sort(dR, DailyStockRating.stockNameComparator);
            Collections.sort(jsonArrayTimes, DailyStockRating.stockNameComparator);
            ArrayList<DailyStockRating> jsonArrayTimesFittedForCompare = new ArrayList<>();
            ArrayList<DailyStockRating> finishedArrayForDB = new ArrayList<>();
            if (dR.size() == 0) {
                addDailyStockRatingsToDB(jsonArrayTimes);
            }


            for (int i = 0; i < dR.size(); i++) {
                for (int j = 0; j < dR.size(); j++) {
                    if (jsonArrayTimes.get(i).getStockTicker().equals(dR.get(j).getStockTicker())) {
                        jsonArrayTimesFittedForCompare.add(jsonArrayTimes.get(i));
                    }
                }
            }

            if (dR.size() == 0) {
                for (int i = 0; i < jsonArrayTimesFittedForCompare.size(); i++) {
                    double closeDB = jsonArrayTimesFittedForCompare.get(i).getClose();
                    double closeToday = jsonArrayTimesFittedForCompare.get(i).getClose();
                    double rate = 100 - ((closeToday / closeDB) * 100.00);
                    DailyStockRating forAdding = jsonArrayTimesFittedForCompare.get(i);
                    finishedArrayForDB.add(new DailyStockRating(forAdding.getStockTicker(), forAdding.getDate(), forAdding.getClose(), rate));
                }
            } else {
                for (int i = 0; i < dR.size(); i++) {

                    double closeDB = dR.get(i).getClose();
                    double closeToday = jsonArrayTimesFittedForCompare.get(i).getClose();
                    double rate = 100 - ((closeToday / closeDB) * 100.00);
                    DailyStockRating forAdding = jsonArrayTimesFittedForCompare.get(i);
                    finishedArrayForDB.add(new DailyStockRating(forAdding.getStockTicker(), forAdding.getDate(), forAdding.getClose(), rate));
                }
            }
            addDailyStockRatingsToDB(finishedArrayForDB);


        }
    }


    public List<DailyStockRating> returnDailyStockRatings(String ascOrDesc) throws InterruptedException {


        //FOR LOCAL
        //timeCheckForLocal();
        TimerTask fetchOnceADay = new TimerTask() {
            public void run() {
                String accessKeyMarketstack = "80f90dbc8de86858f292e8e8ff76293f";
                String symbols = "";
                List<DailyStockRating> dR = findFiveHighestGainsOrDropsFromDB("ASC", "first");
                List<StockSymbol> tickers = getAllStockTickers();

                for (int i = 0; i < tickers.size(); i++) {
                    if (i != tickers.size() - 1) {
                        symbols += tickers.get(i) + ",";
                    } else {
                        symbols += tickers.get(i);
                    }

                }

                String data = "";
                try {
                    data = fetchData("https://api.marketstack.com/v1/eod/latest?access_key=" + accessKeyMarketstack + "&symbols=" + symbols);
                } catch (IOException e) {
                    e.printStackTrace();
                }


                // String data = timeCheckForOnline();
                org.json.JSONObject json = new org.json.JSONObject(data);
                JSONArray jsonArray = json.getJSONArray("data");

                ArrayList<DailyStockRating> jsonArrayTimes = new ArrayList<>();

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject item = jsonArray.getJSONObject(i);
                    String symb = (String) item.get("symbol");
                    String date = (String) item.get("date");
                    double close = (double) item.get("close");
                    if (symb.contains(".")) {
                        jsonArray.remove(i);
                    }
                    if (!symb.contains(".")) {
                        jsonArrayTimes.add(new DailyStockRating(symb, date, close));
                    }
                }
                //sorting the two arrays to get them in same order, before claculating the daily rate
                Collections.sort(dR, DailyStockRating.stockNameComparator);
                Collections.sort(jsonArrayTimes, DailyStockRating.stockNameComparator);
                ArrayList<DailyStockRating> jsonArrayTimesFittedForCompare = new ArrayList<>();
                ArrayList<DailyStockRating> finishedArrayForDB = new ArrayList<>();
                if (dR.size() == 0) {
                    addDailyStockRatingsToDB(jsonArrayTimes);
                }


                for (int i = 0; i < dR.size(); i++) {
                    for (int j = 0; j < dR.size(); j++) {
                        if (jsonArrayTimes.get(i).getStockTicker().equals(dR.get(j).getStockTicker())) {
                            jsonArrayTimesFittedForCompare.add(jsonArrayTimes.get(i));
                        }
                    }
                }

                if (dR.size() == 0) {
                    for (int i = 0; i < jsonArrayTimesFittedForCompare.size(); i++) {
                        double closeDB = jsonArrayTimesFittedForCompare.get(i).getClose();
                        double closeToday = jsonArrayTimesFittedForCompare.get(i).getClose();
                        double rate = 100 - ((closeToday / closeDB) * 100.00);
                        DailyStockRating forAdding = jsonArrayTimesFittedForCompare.get(i);
                        finishedArrayForDB.add(new DailyStockRating(forAdding.getStockTicker(), forAdding.getDate(), forAdding.getClose(), rate));
                    }
                } else {
                    for (int i = 0; i < dR.size(); i++) {

                        double closeDB = dR.get(i).getClose();
                        double closeToday = jsonArrayTimesFittedForCompare.get(i).getClose();
                        double rate = 100 - ((closeToday / closeDB) * 100.00);
                        DailyStockRating forAdding = jsonArrayTimesFittedForCompare.get(i);
                        finishedArrayForDB.add(new DailyStockRating(forAdding.getStockTicker(), forAdding.getDate(), forAdding.getClose(), rate));
                    }
                }
                addDailyStockRatingsToDB(finishedArrayForDB);
                NotificationsFacade nF = NotificationsFacade.getFacadeExample(emf);
                nF.checkThresholds();
            }
            };
        //Using executor instead of timer since it's more foolproof - if a given task is interrupted it should still execute the next day
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        long delay  = 1000L;
        long period = 1000L;
        executor.scheduleAtFixedRate(fetchOnceADay, delay, period, TimeUnit.MILLISECONDS);
        Thread.sleep(delay + period * 3);
        executor.shutdown();

        return findFiveHighestGainsOrDropsFromDB(ascOrDesc, "last");

    }

    public String fetchData(String _url) throws IOException {
        URL url = new URL(_url);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Accept", "application/json");
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
        Scanner scan = new Scanner(con.getInputStream());
        String jsonStr = "";
        while (scan.hasNext()) {
            jsonStr += scan.nextLine();
        }
        scan.close();
        return jsonStr;
    }
}
