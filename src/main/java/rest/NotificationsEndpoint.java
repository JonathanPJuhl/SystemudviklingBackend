package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import entities.NotificationDTO;
import entities.Notifications;
import facades.NotificationsFacade;
import utils.EMF_Creator;


import javax.persistence.EntityManagerFactory;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

//Todo Remove or change relevant parts before ACTUAL use
@Path("notifications")
public class NotificationsEndpoint {

    private static final EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactory();
    private static final NotificationsFacade FACADE =  NotificationsFacade.getFacadeExample(EMF);
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();



    @GET
    @Path("/get/{username}")
    @Produces({MediaType.APPLICATION_JSON})
    public String teacherSolution(@PathParam("username") String username) throws IOException {
        List<Notifications> usersNotis = FACADE.displayUsersNotis(username);
        List<NotificationDTO> dtoList = new ArrayList<>();
        for(int i = 0; i<usersNotis.size(); i++){
            Notifications noti = usersNotis.get(i);
            String ticker = noti.getStockTicker().getStockTicker();
            dtoList.add(new NotificationDTO(noti.getDate(), noti.isStatus(), noti.getMessage(), ticker, noti.getMessageID()));
        }
        return GSON.toJson(dtoList);

    }

    @GET
    @Path("/addnotifications/{userStockNoti}")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public void addNoti(@PathParam("userStockNoti") String userStockNoti){
        String[] all = userStockNoti.split(",");
        String username = all[0];
        String stock = all[1];
        int price = Integer.parseInt(all[2]);
        FACADE.AddNotiThreshToDb(username, stock, price);
    }

    @GET
    @Path("/checkifusershouldgetnoti")
    @Produces({MediaType.APPLICATION_JSON})
    public void checkIfUserShouldGetNoti(){
        FACADE.checkThresholds();
    }

    @GET
    @Path("/markAsRead/{idAndStatus}")
    public void markNotiAsRead(@PathParam("idAndStatus") String idAndStatus){
        String[] idAndStat = idAndStatus.split(",");
        int id = Integer.parseInt(idAndStat[0]);
        boolean status = Boolean.parseBoolean(idAndStat[1]);
        FACADE.updateNotiStatus(id, status);
    }
    @GET
    @Path("/delete/{id}")
    public void deleteNoti(@PathParam("id") int id){
        FACADE.deleteNoti(id);
    }


}
