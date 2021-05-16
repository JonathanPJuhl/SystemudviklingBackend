package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import facades.NotificationsFacade;
import utils.EMF_Creator;


import javax.persistence.EntityManagerFactory;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.IOException;

//Todo Remove or change relevant parts before ACTUAL use
@Path("notifications")
public class NotificationsEndpoint {

    private static final EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactory();
       
    private static final NotificationsFacade FACADE =  NotificationsFacade.getFacadeExample(EMF);
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
            


    @GET
    @Path("/mynotifications/{user}")
    @Produces({MediaType.APPLICATION_JSON})
    public String teacherSolution(@PathParam("user") String username) throws IOException {


        return "[]";

    }

    @GET
    @Path("/addnotifications/{userStockNoti}")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public String addNoti(@PathParam("userStockNoti") String userStockNoti){
        String[] all = userStockNoti.split(",");
        String username = all[0];
        String stock = all[1];
        int price = Integer.parseInt(all[2]);

        FACADE.AddNotiThreshToDb(username, stock, price);

        return "WUHU";
    }



}
