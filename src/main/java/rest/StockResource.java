package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import entities.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import javax.annotation.security.RolesAllowed;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;

import facades.StockFacade;

import utils.EMF_Creator;
import utils.SetupTestUsers;

/**
 * @author lam@cphbusiness.dk
 */
@Path("stock")
public class StockResource {

    private static final EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactory();
    private static StockFacade facade = StockFacade.getFacadeExample(EMF);
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    @Context
    private UriInfo context;

    @Context
    SecurityContext securityContext;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getInfoForAll() {
        return "{\"msg\":\"Hello anonymous\"}";
    }

    //Just to verify if the database is setup
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("all")
    public String allUsers() {

        EntityManager em = EMF.createEntityManager();
        try {
            TypedQuery<User> query = em.createQuery("select u from User u", entities.User.class);
            List<User> users = query.getResultList();
            return "[" + users.size() + "]";
        } finally {
            em.close();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("user")
    @RolesAllowed({"user"})
    public String getFromUser() {
        String thisuser = securityContext.getUserPrincipal().getName();
        return "{\"msg\": \"Hello to User: " + thisuser + "\"}";
    }

    @GET
    @Path("pinned/{username}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    //@RolesAllowed("user")
    public String getPinnedByUser(@PathParam("username") String username) {
        /*List<Stock> stockTicker = facade.getPinnedStocks(username);*/
        List<String> list = facade.getPinnedStocks(username);
        String URL = "https://api.marketstack.com/v1/eod/latest?access_key=5feeee1a869fedc6e6e24e62c735bc22&symbols=";
        String pinned = "";
        for (int i = 0; i < list.size(); i++) {
            if (i != list.size() - 1) {
                URL += list.get(i) + ",";
            } else {
                URL += list.get(i) ;
            }

        }
        // return stocks
        String pin ="";
        try {
            pin = fetchData(URL);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return "[" + pin + "]";
    }

    public String fetchData(String _url) throws MalformedURLException, IOException {
        URL url = new URL(_url);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        //con.setRequestProperty("Accept", "application/json;charset=UTF-8");
        con.setRequestProperty("Accept", "application/json");
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
        //con.setRequestProperty("User-Agent", "server"); //remember if you are using SWAPI
        Scanner scan = new Scanner(con.getInputStream());
        String jsonStr = "";
        while (scan.hasNext()) {
            jsonStr += scan.nextLine();
        }
        scan.close();
        return jsonStr;
    }

    @GET
    @Path("populate")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public String populate() {
        SetupTestUsers s = new SetupTestUsers();
        s.populate();
        return "Success";
    }

    @POST
    @Path("pin")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public String addStockToPerson(String stockTicker) {
        String str = stockTicker.substring(1, stockTicker.length() - 1);
        String[] thisUser = str.split(",");
        String ticker = thisUser[0];
        String user = thisUser[1];
        System.out.println(ticker + "   " + user);
       /* System.out.println(stockTicker);
        Stock ticker = GSON.fromJson(stockTicker, Stock.class);*/
        facade.AddToDb(ticker, user);
        return "Added stock to pins";
    }


//    @GET
//    @Path("topfive")
//    @Produces(MediaType.APPLICATION_JSON)
//    @Consumes(MediaType.APPLICATION_JSON)
//    public String getTopFive() {
//        /*List<Stock> stockTicker = facade.getPinnedStocks(username);*/
//        List<StockSymbol> list = facade.getAllStockTickers();
//        Instant now = Instant.now();
//        String yesterday = now.minus(1, ChronoUnit.DAYS).toString().substring(0,10);
//        String URL = "https://api.marketstack.com/v1/eod/latest?access_key=5feeee1a869fedc6e6e24e62c735bc22&symbols=";
//        String yesterdayURL = "https://api.marketstack.com/v1/eod/"+yesterday+"?access_key=5feeee1a869fedc6e6e24e62c735bc22&symbols=";
//
//
//
//
//        String pinned = "";
//        String pinnedYesterday = "";
//        for (int i = 0; i < list.size(); i++) {
//            if(i!=list.size()-1) {
//                URL += list.get(i) + ",";
//                yesterdayURL += list.get(i) + ",";
//            } else{
//                URL += list.get(i);
//                yesterdayURL += list.get(i);
//
//            }
//        }
//
//
//            try {
//                pinned = fetchData(URL);
//                pinnedYesterday = fetchData(yesterdayURL);
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//        JSONObject json = new JSONObject(pinned);  //initial JSONObject (See explanation section below)
//        JSONObject jsonYesterday = new JSONObject(pinnedYesterday);  //initial JSONObject (See explanation section below)
//        JSONArray jsonArray = json.getJSONArray("data");  //"results" JSONArray
//
//        JSONArray jsonArrayYesterday = jsonYesterday.getJSONArray("data");  //"results" JSONArray
//         System.out.println(jsonArrayYesterday.toString());
//        //first JSONObject inside "results" JSONArray
//        HashMap<String, Double> jsonMapToday = new HashMap<>();
//        HashMap<String, Double> jsonMapYesterday = new HashMap<>();
//
//        ArrayList<String> jsonArrayListToday = new ArrayList<>();  //"times" JSONArray
//        ArrayList<String> jsonArrayListYesterday = new ArrayList<>();  //"times" JSONArray
//        System.out.println("LÆNGDE : " + jsonArrayYesterday.length());
//        for (int i = 0; i < jsonArrayYesterday.length(); i++) {
//            JSONObject itemYesterday = jsonArrayYesterday.getJSONObject(i);
//            String symbolYesterday = (String)itemYesterday.get("symbol");
//            Double highYesterday = (Double)itemYesterday.get("high");
//            jsonMapYesterday.put(symbolYesterday, highYesterday);
//        }
//        for (int i = 0; i < jsonArray.length(); i++) {
//            JSONObject itemToday = jsonArray.getJSONObject(i);
//            String symbolToday = (String)itemToday.get("symbol");
//            Double highToday = (Double)itemToday.get("high");
//            jsonMapToday.put(symbolToday,highToday);
//        }
//
//        System.out.println("TODAY: " + jsonMapToday.keySet());
//        System.out.println("YESTER" + jsonMapYesterday.keySet());
//            ArrayList<String> biggestGains = facade.getFiveBiggestGains(jsonMapToday, jsonMapYesterday);
//            ArrayList<String> biggestDrops =facade.getFiveBiggestDrops(jsonArrayListToday, jsonArrayListYesterday);
//        System.out.println(biggestGains.toString());
//
//         return GSON.toJson(pinned);
//
//
//        //return "[" + pinned + "]";
//    }

    @GET
    @Path("fillDBwithTickers")
    @Consumes(MediaType.APPLICATION_JSON)
    public String fillDb() {
        List<StockSymbol> list = facade.getAllStockTickers();
        String URL = "https://api.marketstack.com/v1/eod/latest?access_key=5feeee1a869fedc6e6e24e62c735bc22&symbols=";
        String data = "";
        for (int i = 0; i < list.size(); i++) {
            if(i!=list.size()-1) {
                URL += list.get(i) + ",";
            } else{
                URL += list.get(i);
            }
        }
        try {
            data = fetchData(URL);
        } catch (IOException e) {
            e.printStackTrace();
        }
        JSONObject json = new JSONObject(data);  //initial JSONObject (See explanation section below)
        JSONArray jsonArray = json.getJSONArray("data");  //"results" JSONArray
         //first JSONObject inside "results" JSONArray
        ArrayList<String> jsonArrayTimes = new ArrayList<>();  //"times" JSONArray

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject item = jsonArray.getJSONObject(i);
            String symb = (String)item.get("symbol");
            System.out.println(symb);
            if(!symb.contains(".")){
            jsonArrayTimes.add(symb);}
        }
        facade.addStockTickersToDB(jsonArrayTimes);
        return "success";
    }

    //SHOULD BE FUNCTIONAL
    @GET
    @Path("filldbwithdailyratings/{ascordesc}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String fillDbWithDailyStockRatings(@PathParam("ascordesc") String ascOrDesc) {
        List<DailyStockRating> dR = facade.findFiveHighestGainsOrDropsFromDB("ASC");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String thisDay = LocalDate.now().format(formatter);
        //Checks if there's been fetched to day already , if there has, it won't do it again
        if(!dR.get(0).getDate().equals(thisDay)) {
            String data = "";
            try {
                data = fetchData("https://api.marketstack.com/v1/tickers?access_key=5feeee1a869fedc6e6e24e62c735bc22");
            } catch (IOException e) {
                e.printStackTrace();
            }
            JSONObject json = new JSONObject(data);  //initial JSONObject (See explanation section below)
            JSONArray jsonArray = json.getJSONArray("data");  //"results" JSONArray
            //first JSONObject inside "results" JSONArray
            ArrayList<DailyStockRating> jsonArrayTimes = new ArrayList<>();  //"times" JSONArray

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject item = jsonArray.getJSONObject(i);
                String symb = (String) item.get("symbol");
                String date = (String) item.get("date");
                double close = (double) item.get("close");
                System.out.println(symb);
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
            ArrayList<DailyStockRating> finishedArrayForDB = new ArrayList<>();
            for(int i=0; i<dR.size(); i++){
                double closeDB = dR.get(i).getClose();
                double closeToday = jsonArrayTimes.get(i).getClose();
                double rate = closeToday/closeDB*100;
                DailyStockRating forAdding = jsonArrayTimes.get(i);
                finishedArrayForDB.add(new DailyStockRating(forAdding.getStockTicker(), forAdding.getDate(), forAdding.getClose(), rate));
            }
            facade.addDailyStockRatingsToDB(finishedArrayForDB);
            return GSON.toJson(facade.findFiveHighestGainsOrDropsFromDB(ascOrDesc));
        }else{
            return "failed - data already up-to-date";
        }
    }
}