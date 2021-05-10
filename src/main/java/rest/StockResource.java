package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nimbusds.jose.shaded.json.JSONObject;
import entities.PinnedStockDto;
import entities.Stock;
import entities.User;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
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
import facades.UserFacade;
import utils.EMF_Creator;
import utils.SetupTestUsers;

/**
 * @author lam@cphbusiness.dk
 */
@Path("stock")
public class StockResource {
    
    private static final EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactory();
    private static StockFacade facade = StockFacade.getFacadeExample(EMF);
    private static final Gson GSON = new  GsonBuilder().setPrettyPrinting().create();
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
            TypedQuery<User> query = em.createQuery ("select u from User u",entities.User.class);
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
        for(int i = 0 ; i<list.size(); i++){
            try {
                String pin = fetchData(URL+list.get(i));
                if(i!=list.size()-1) {
                    pinned += pin + ",";
                }else{
                    pinned += pin;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        // return stocks


        return "["+pinned+"]";
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
        while(scan.hasNext()) {
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
        System.out.println(ticker+"   " + user);
       /* System.out.println(stockTicker);
        Stock ticker = GSON.fromJson(stockTicker, Stock.class);*/
        facade.AddToDb(ticker, user);
        return "Added stock to pins";
    }
}