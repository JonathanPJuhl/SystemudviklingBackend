
package rest;

import entities.Role;
import entities.User;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.*;
import utils.EMF_Creator;
import io.restassured.RestAssured;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import io.restassured.parsing.Parser;
import java.net.URI;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.core.UriBuilder;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.util.HttpStatus;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

//Uncomment the line below, to temporarily disable this test
@Disabled

public class StocksResourceTest {

    private static final int SERVER_PORT = 7777;
    private static final String SERVER_URL = "http://localhost/sys/api";

    static final URI BASE_URI = UriBuilder.fromUri(SERVER_URL).port(SERVER_PORT).build();
    private static HttpServer httpServer;
    private static EntityManagerFactory emf;
    private static String requestBody = "\"AAPL,user\"";




    static HttpServer startServer() {
        ResourceConfig rc = ResourceConfig.forApplication(new ApplicationConfig());
        return GrizzlyHttpServerFactory.createHttpServer(BASE_URI, rc);
    }

    @BeforeAll
    public static void setUpClass() {
        //This method must be called before you request the EntityManagerFactory
        EMF_Creator.startREST_TestWithDB();
        emf = EMF_Creator.createEntityManagerFactoryForTest();

        httpServer = startServer();
        //Setup RestAssured
        RestAssured.baseURI = SERVER_URL;
        RestAssured.port = SERVER_PORT;
        RestAssured.defaultParser = Parser.JSON;
    }

    @AfterAll
    public static void closeTestServer() {
        //System.in.read();

        //Don't forget this, if you called its counterpart in @BeforeAll
        EMF_Creator.endREST_TestWithDB();
        httpServer.shutdownNow();
    }

    // Setup the DataBase (used by the test-server and this test) in a known state BEFORE EACH TEST
    //TODO -- Make sure to change the EntityClass used below to use YOUR OWN (renamed) Entity class
    @BeforeEach
    public void setUp() {
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            em.createQuery("delete from User").executeUpdate();
            em.createQuery("delete from Role").executeUpdate();

            Role userRole = new Role("user");
            Role adminRole = new Role("admin");
            User user = new User("user", "test", "first cat", "dead");
            user.addRole(userRole);
            User admin = new User("admin", "test", "first cat", "dead");
            admin.addRole(adminRole);
            User both = new User("user_admin", "test", "first cat", "dead");
            both.addRole(userRole);
            both.addRole(adminRole);
            em.persist(userRole);
            em.persist(adminRole);
            em.persist(user);
            em.persist(admin);
            em.persist(both);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    @Test
    public void testServerIsUp() {
        given().when().get("/stock").then().statusCode(200);
    }

    //This test assumes the database contains two rows
    @Test
    public void testPinAndDeletePinAndGetPinned() throws Exception {
        Response response =
                        given()
                        .header("Content-type", "application/json")
                        .and()
                        .body(requestBody)
                        .when()
                        .post("/stock/pin" )
                        .then().extract().response();
        Assertions.assertEquals(200, response.statusCode());
        given()
                .contentType("application/json")
                .get("/stock/pinned/user").then()
                .assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode())
                .body("data.symbol", hasItem("AAPL"));
        given()
                .contentType("application/json")
                .get("/stock/deletePin/user,AAPL").then()
                .assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode());
        given()
                .contentType("application/json")
                .get("/stock/pinned/user").then()
                .assertThat()
                .statusCode(200).body("resp", equalTo("No pinned stocks found"));
    }

}
