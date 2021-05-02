package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import utils.EMF_Creator;
import facades.FacadeExample;

import javax.annotation.security.RolesAllowed;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

//Todo Remove or change relevant parts before ACTUAL use
@Path("5things")
public class FiveThingsResource {

    private static final EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactory();
       
    private static final FacadeExample FACADE =  FacadeExample.getFacadeExample(EMF);
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
            
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public String demo() throws IOException {
        String[] urls = {"https://api.covid19api.com/summary", "https://jsonplaceholder.typicode.com/todos",
                "https://jsonplaceholder.typicode.com/todos", "https://jsonplaceholder.typicode.com/todos", "https://jsonplaceholder.typicode.com/comments"};
        return createJson(urls);
    }

    @GET
    @Path("/teachersSolution")
    @RolesAllowed({"user", "admin"})
    @Produces({MediaType.APPLICATION_JSON})
    public String teacherSolution() throws IOException {
        String firstFetch = fetchData("https://swapi.dev/api/people/1/");
        String firstFetch1 = fetchData("https://swapi.dev/api/people/2/");
        String firstFetch2 = fetchData("https://swapi.dev/api/people/3/");
        String firstFetch3 = fetchData("https://swapi.dev/api/people/4/");
        String firstFetch4 = fetchData("https://swapi.dev/api/people/5/");
        return "["+firstFetch+",\n\n\n\n"+firstFetch1+",\n\n\n\n"+firstFetch2+",\n\n\n\n"+firstFetch3+",\n\n\n\n"+firstFetch4+"]";

    }

    public String fetchData(String _url) throws MalformedURLException, IOException{
        URL url = new URL(_url);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        //con.setRequestProperty("Accept", "application/json;charset=UTF-8");
        con.setRequestProperty("Accept", "application/json");
        con.setRequestProperty("User-Agent", "server"); //remember if you are using SWAPI
        Scanner scan = new Scanner(con.getInputStream());
        String jsonStr = "";
        while(scan.hasNext()) {
            jsonStr += scan.nextLine();
        }
        scan.close();
        return jsonStr;
    }


    public String createJson(String[] urls) throws IOException {
        URL url = new URL(urls[0]);
        URL url1 = new URL(urls[1]);
        URL url2 = new URL(urls[2]);
        URL url3 = new URL(urls[3]);
        URL url4 = new URL(urls[4]);


        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        HttpURLConnection conn1 = (HttpURLConnection) url1.openConnection();
        HttpURLConnection conn2 = (HttpURLConnection) url2.openConnection();
        HttpURLConnection conn3 = (HttpURLConnection) url3.openConnection();
        HttpURLConnection conn4 = (HttpURLConnection) url4.openConnection();
        conn.setRequestMethod("GET");
        conn1.setRequestMethod("GET");
        conn2.setRequestMethod("GET");
        conn3.setRequestMethod("GET");
        conn4.setRequestMethod("GET");
        conn.connect();
        conn1.connect();
        conn2.connect();
        conn3.connect();
        conn4.connect();

//Getting the response code
        int responsecode = conn.getResponseCode();
        int responsecode1 = conn1.getResponseCode();
        int responsecode2 = conn2.getResponseCode();
        int responsecode3 = conn3.getResponseCode();
        int responsecode4 = conn4.getResponseCode();

        if (responsecode != 200 || responsecode1 != 200 || responsecode2 != 200 || responsecode3 != 200 || responsecode4 != 200) {
            throw new RuntimeException("HttpResponseCode: " + responsecode + "HttpResponseCode: " + responsecode1 + "HttpResponseCode: " + responsecode2 + "HttpResponseCode: " + responsecode3+ "HttpResponseCode: " + responsecode4);
        } else {

            String inline = "";
            String inline1 = "";
            String inline2 = "";
            String inline3 = "";
            String inline4 = "";
            Scanner scanner = new Scanner(url.openStream());
            Scanner scanner1 = new Scanner(url1.openStream());
            Scanner scanner2= new Scanner(url2.openStream());
            Scanner scanner3 = new Scanner(url3.openStream());
            Scanner scanner4 = new Scanner(url4.openStream());

            //Write all the JSON data into a string using a scanner
            while (scanner.hasNext()) {
                inline += scanner.nextLine();
            }
            while (scanner1.hasNext()) {
                inline1 += scanner1.nextLine();
            }
            while (scanner2.hasNext()) {
                inline2 += scanner2.nextLine();
            }
            while (scanner3.hasNext()) {
                inline3 += scanner3.nextLine();
            }
            while (scanner4.hasNext()) {
                inline4 += scanner4.nextLine();
            }

            //Close the scanner
            scanner.close();
            scanner1.close();
            scanner2.close();
            scanner3.close();
            scanner4.close();

            //Using the JSON simple library parse the string into a json object
            //JSONParser parse = new JSONParser();
            String json = GSON.toJson(inline);
            String json1 = GSON.toJson(inline1);
            String json2 = GSON.toJson(inline2);
            String json3 = GSON.toJson(inline3);
            String json4 = GSON.toJson(inline4);
            //JSONObject data_obj = (JSONObject) parse.parse(inline);

            //Get the required object from the above created object
            // JSONObject obj = (JSONObject) data_obj.get("Global");

            //Get the required data using its key
            //System.out.println(obj.get("TotalRecovered"));
            return inline+"\n"+inline1+"\n"+inline2+"\n"+inline3+"\n"+inline4;
        }
    }

}
