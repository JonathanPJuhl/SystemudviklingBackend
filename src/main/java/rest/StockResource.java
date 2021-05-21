package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import entities.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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

    private static String accessKeyMarketstack = "80f90dbc8de86858f292e8e8ff76293f";

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

    /*//Just to verify if the database is setup
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
    }*/

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
        List<String> list = facade.getPinnedStocks(username);
        String URL = "https://api.marketstack.com/v1/eod/latest?access_key=" + accessKeyMarketstack + "&symbols=";


        for (int i = 0; i < list.size(); i++) {
            if (i != list.size() - 1) {
                URL += list.get(i) + ",";
            } else {
                URL += list.get(i);
            }

        }
        String pin = "";
        try {
            pin = fetchData(URL);

        } catch (IOException e) {
            e.printStackTrace();
        }
        if(pin!=""){
        return pin;}
        else{
            return "{\"resp\": \"No pinned stocks found\"}";
        }
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

    @GET
    @Path("populate")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public void populate() {
        SetupTestUsers s = new SetupTestUsers();
        s.populate();
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
        facade.AddToDb(ticker, user);
        return "\"msg\":\"Added stock to pins\"";
    }


    @GET
    @Path("fillDBwithTickers")
    @Consumes(MediaType.APPLICATION_JSON)
    public void fillDb() {
        String URL = "https://api.marketstack.com/v1/tickers?access_key=" + accessKeyMarketstack;
        String data = "";

        try {
            data = fetchData(URL);
        } catch (IOException e) {
            e.printStackTrace();
        }
        JSONObject json = new JSONObject(data);  //initial JSONObject (See explanation section below)
        JSONArray jsonArray = json.getJSONArray("data");  //"results" JSONArray
        ArrayList<String> jsonArrayTimes = new ArrayList<>();  //"times" JSONArray

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject item = jsonArray.getJSONObject(i);
            String symb = (String) item.get("symbol");
            System.out.println(symb);
            if (!symb.contains(".")) {
                jsonArrayTimes.add(symb);
            }
        }
        facade.addStockTickersToDB(jsonArrayTimes);
    }

    @POST
    @Path("makechart")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces({MediaType.APPLICATION_JSON})
    public String makeChart(String jsonFromFront) {

        JSONObject json = new JSONObject(jsonFromFront);  //initial JSONObject (See explanation section below)
        JSONArray jsonArray = json.getJSONArray("data");  //"results" JSONArray
        //first JSONObject inside "results" JSONArray
        ArrayList<DailyStockRating> jsonArrayTimes = new ArrayList<>();  //"times" JSONArray


        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject item = jsonArray.getJSONObject(i);
            String symb = (String) item.get("symbol");
            String date = (String) item.get("date");
            Double close = Double.parseDouble(item.get("close").toString());
            if (symb.contains(".")) {
                jsonArray.remove(i);
            }
            if (!symb.contains(".")) {
                jsonArrayTimes.add(new DailyStockRating(symb, date, close));
            }
        }
        System.out.println("SIZE: " + jsonArrayTimes.size());
        facade.makeChart(jsonArrayTimes);

        return GSON.toJson(facade.makeChart(jsonArrayTimes));
    }
    @GET
    @Path("filldbwithdailyratings/{ascordesc}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String fillDbWithDailyStockRatings(@PathParam("ascordesc") String ascOrDesc) throws InterruptedException {
        return GSON.toJson(facade.returnDailyStockRatings(ascOrDesc));
    }

    //SHOULD BE FUNCTIONAL
    /*@GET
    @Path("filldbwithdailyratings/{ascordesc}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String fillDbWithDailyStockRatings(@PathParam("ascordesc") String ascOrDesc) {
        String symbols = "";
        List<StockSymbol> tickers = facade.getAllStockTickers();
        for (int i = 0; i < tickers.size(); i++) {
            if (i != tickers.size() - 1) {
                symbols += tickers.get(i) + ",";
            } else {
                symbols += tickers.get(i);
            }

        }
        List<DailyStockRating> dR = facade.findFiveHighestGainsOrDropsFromDB("ASC", "first");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        Calendar rightNow = Calendar.getInstance();
        int hour = rightNow.get(Calendar.HOUR_OF_DAY);
        //Using hour because stockdata is not released until 5 in the afternoon in the us (therefore 23 in  DK)
        String thisDay = LocalDate.now().format(formatter);

        //Checks if there's been fetched to day already , if there has, it won't do it again
        if (dR.size() == 0 || (hour >= 23 && !dR.get(0).getDate().equals(thisDay))) {
            String data = "";
            try {
                data = fetchData("https://api.marketstack.com/v1/eod/latest?access_key=" + accessKeyMarketstack + "&symbols=" + symbols);
            } catch (IOException e) {
                e.printStackTrace();
            }

            JSONObject json = new JSONObject(data);
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
            facade.addDailyStockRatingsToDB(finishedArrayForDB);
            return GSON.toJson(facade.findFiveHighestGainsOrDropsFromDB(ascOrDesc, "last"));
        } else {
            return GSON.toJson(facade.findFiveHighestGainsOrDropsFromDB(ascOrDesc, "last"));
        }
    }*/

    @GET
    @Path("/deletePin/{userTicker}")
    @Consumes(MediaType.APPLICATION_JSON)
    public String deletePinnedStockFromUser(@PathParam("userTicker") String userAndTicker) {
        String[] usernameAndTicker = userAndTicker.split(",");
        String username = usernameAndTicker[0];
        String ticker = usernameAndTicker[1];
        String status = facade.deleteTickerFromUser(username, ticker);
        return "{ \"resp\": \"" + status + "\"}";

    }

}