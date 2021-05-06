package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import entities.Stock;
import entities.User;

import java.util.ArrayList;
import java.util.List;
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
        List<Stock> list2 = new ArrayList<>();
        for(int i = 0 ; i<list.size()-1; i++){
            list2.add(new Stock(list.get(i)));
        }
        return GSON.toJson(list2);
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