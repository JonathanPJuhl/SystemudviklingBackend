package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import entities.User;
import facades.UserFacade;
import utils.EMF_Creator;
import utils.SetupTestUsers;

import javax.annotation.security.RolesAllowed;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;
import java.nio.channels.UnresolvedAddressException;
import java.util.List;

/**
 * @author lam@cphbusiness.dk
 */
@Path("user")
public class UserResource {
    
    private static final EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactory();
    private static UserFacade facade = UserFacade.getUserFacade(EMF);
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
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("create")
    public String createUser(String user) {
        System.out.println(user);
        User userForCreation = GSON.fromJson(user, User.class);


       User userForReturn = facade.createUser(userForCreation);

        return GSON.toJson(userForReturn);
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
    @Produces(MediaType.APPLICATION_JSON)
    @Path("admin")
    @RolesAllowed("admin")
    public String getFromAdmin() {
        String thisuser = securityContext.getUserPrincipal().getName();
        return "{\"msg\": \"Hello to (admin) User: " + thisuser + "\"}";
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
 /*   @POST
    @Path("create")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public String addPerson(String json) {
        PersonDTO persDTO = GSON.fromJson(json, PersonDTO.class);
        PersonDTO persistedPers = FACADE.create(persDTO);
        return GSON.toJson(persistedPers);
    }*/
}