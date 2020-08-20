import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.jose4j.json.internal.json_simple.JSONArray;
import org.jose4j.json.internal.json_simple.JSONObject;
import org.jose4j.json.internal.json_simple.parser.JSONParser;
import org.jose4j.json.internal.json_simple.parser.ParseException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Author: Xiaoyu Zhu
 * <p>
 * When a user searches for a cocktail, he/she can search by name or category.
 * 1. if search by name then API is called with specific URL and details about that cocktail is returned
 * 2. if search by category - API returns a list of cocktails that are listed under that category
 * One of the cocktail ids are randomly selected from that list
 * API is called using that cocktail id to get the details about that cocktail
 * 3. Mongo db connection is set up
 * 4. Details about the search is logged to Mongo db
 */
@WebServlet(name = "CocktailServlet", urlPatterns = {"/getCocktailDetail"})
public class CocktailServlet extends HttpServlet {
    MongoCollection<Document> collection;
    MongoDatabase database;
    MongoClient mongoClient;
    DateFormat df = new SimpleDateFormat("dd:MM:yy:HH:mm:ss");

    /**
     * This is invoked when Android app calls this web application.
     * It prepares the appropriate API url, fetches data, prepares JSON object to return to Android app
     * It also creates a log entry in mongo db
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //get timestamp when request was receieved
        long timestamp_request_received = System.currentTimeMillis();
        long start = 0, end;
        //get parameters sent from app
        String cocktailName = request.getParameter("name");
        String category = request.getParameter("category");

        String cocktailDBUrl;//API url
        String cocktailDetailAsJSONString = null;//response from API
        //user can send either name or category
        //based on user input, construct API url
        if (category != null) {
            start = System.currentTimeMillis();
            cocktailDBUrl = "https://www.thecocktaildb.com/api/json/v1/1/filter.php?c=" + category;
            //get a list of cocktails under the given category
            String cocktailCategory_json = fetchCocktailByCategory(cocktailDBUrl);
            //randomly choose one of the cocktails and fetch details of that cocktail
            cocktailDetailAsJSONString = getRandomCocktailFromCategoryList(cocktailCategory_json);
        } else if (cocktailName != null) {
            start = System.currentTimeMillis();
            cocktailDBUrl = "https://www.thecocktaildb.com/api/json/v1/1/search.php?s=" + cocktailName;
            //fetch cocktail detail
            cocktailDetailAsJSONString = fetchCocktailByName(cocktailDBUrl);
        }
        if (cocktailDetailAsJSONString != null) {//if response from API is not null
            JSONObject jsonResponse = getCocktailDetailAsJSON(cocktailDetailAsJSONString);

            //get details to store in log
            String name_log = (String) jsonResponse.get("name");
            String category_log = (String) jsonResponse.get("category");
            String imgUrl = (String) jsonResponse.get("imgURL");

            end = System.currentTimeMillis();
            long latency = (end - start);

            String device_type = request.getHeader("User-Agent");
            long timestamp_reply = end;
            if (device_type.contains("Android")){//add entry to log only if request received from mobile
                addLog(timestamp_request_received, latency, name_log, category_log, imgUrl, device_type, timestamp_reply);
            }

            //Refered: https://stackoverflow.com/questions/2010990/how-do-you-return-a-json-object-from-a-java-servlet
            //Return json response to Android app
            String json = jsonResponse.toString();
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            response.getWriter().write(json);
        }
    }

    /**
     * Collect all the necessary details from the current search
     * Write an entry to mongo db.
     *
     * @param timestamp_request_received
     * @param latency
     * @param name
     * @param category
     * @param response_param
     * @param device_type
     * @param timestamp_reply
     */
    private void addLog(long timestamp_request_received, long latency, String name, String category, String response_param, String device_type, long timestamp_reply) {
        setupMongoConnection();
        Document document = new Document();
        document.append("latency", latency);
        document.append("name", name);
        document.append("category", category);
        document.append("response_param", response_param);
        document.append("device_type", device_type);
        document.append("timestamp_request_received", df.format(timestamp_request_received));
        document.append("timestamp_reply", df.format(timestamp_reply));
        collection.insertOne(document);
    }

    /**
     * Set up Mongo db connection
     */
    private void setupMongoConnection() {
        MongoClientURI uri = new MongoClientURI(
                "mongodb://ananyaDS:ananya123@cluster0-shard-00-00-qv1ts.mongodb.net:27017,cluster0-shard-00-01-qv1ts.mongodb.net:27017,cluster0-shard-00-02-qv1ts.mongodb.net:27017/sample_airbnb?ssl=true&replicaSet=Cluster0-shard-0&authSource=admin&retryWrites=true&w=majority");
        mongoClient = new MongoClient(uri);
        database = mongoClient.getDatabase("CocktailAppLog");
        collection = database.getCollection("UserLog");
    }

    /**
     * Given a category name, API returns a list of cocktails.
     * Randomly select a cocktail ID and fetch details of that cocktail
     *
     * @param cocktailCategory_json
     * @return
     */
    private String getRandomCocktailFromCategoryList(String cocktailCategory_json) {
        JSONParser jparse = new JSONParser();
        JSONObject jsonObject = null;
        try {
            //converting string to json object
            jsonObject = (JSONObject) jparse.parse(cocktailCategory_json);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        JSONArray drinks = (JSONArray) jsonObject.get("drinks");
        //get a random index to choose a cocktail name
        Random r = new Random();
        int index = r.nextInt(drinks.size());

        JSONObject j = (JSONObject) drinks.get(index);
        String cocktailID = j.get("idDrink").toString();

        String response = "";
        try {
            URL url = new URL("https://www.thecocktaildb.com/api/json/v1/1/lookup.php?i=" + cocktailID);
            //create URL connection
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            // Read all the text returned by the server
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
            String str;
            // Read each line of "in" until done, adding each to "response"
            while ((str = in.readLine()) != null) {
                // str is one line of text readLine() strips newline characters
                response += str;
            }

            in.close();
        } catch (IOException e) {
            System.out.println("Eeek, an exception");

        }

        return response;
    }

    /**
     * Parse the json string. Create a JSONObject.
     * Fetch all the data from the JSON response
     * Return the JSON object containing all the information receieved from API
     *
     * @param jsonString
     * @return
     */
    private JSONObject getCocktailDetailAsJSON(String jsonString) {
        JSONParser jparse = new JSONParser();
        JSONObject jsonObject = null;
        JSONObject jsonResponse = new JSONObject();
        try {
            //converting string to json object
            jsonObject = (JSONObject) jparse.parse(jsonString);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        JSONArray drinks = (JSONArray) jsonObject.get("drinks");
        //some cocktail names would return a list of cocktails.
        //in that case, get a random index to choose a cocktail name
        Random r = new Random();
        int index = r.nextInt(drinks.size());

        JSONObject j = (JSONObject) drinks.get(index);
        jsonResponse.put("id", j.get("idDrink"));
        jsonResponse.put("name", j.get("strDrink"));
        jsonResponse.put("category", j.get("strCategory"));
        jsonResponse.put("isAlcoholic", j.get("strAlcoholic"));
        jsonResponse.put("instructions", j.get("strInstructions"));
        jsonResponse.put("imgURL", j.get("strDrinkThumb"));
        Map<String, String> ingredients = new HashMap<>();
        int num = 1;
        while (j.get("strIngredient" + num) != null) {
            ingredients.put(j.get("strIngredient" + num).toString(), j.get("strMeasure" + num).toString());
            num++;
        }
        jsonResponse.put("ingredients", ingredients);

        return jsonResponse;
    }

    /**
     * Given a category name, return all the cocktails in that category from the API
     *
     * @param cocktailDBUrl
     * @return
     */
    private String fetchCocktailByCategory(String cocktailDBUrl) {
        String response = "";
        try {
            URL url = new URL(cocktailDBUrl);
            //create Http URL connection
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            // Read all the text returned by the server
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
            String str;
            // Read each line of "in" until done, adding each to "response"
            while ((str = in.readLine()) != null) {
                // str is one line of text readLine() strips newline characters
                response += str;
            }
            in.close();
        } catch (IOException e) {
            System.out.println("Eeek, an exception");

        }

        return response;
    }

    /**
     * Given the URL of the API, fetch the details about the cocktail
     *
     * @param cocktailDBUrl
     * @return
     */
    private String fetchCocktailByName(String cocktailDBUrl) {
        String response = "";
        try {
            URL url = new URL(cocktailDBUrl);
            //create Http URL connection
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            // Read all the text returned by the server
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
            String str;
            // Read each line of "in" until done, adding each to "response"
            while ((str = in.readLine()) != null) {
                // str is one line of text readLine() strips newline characters
                response += str;
            }

            in.close();
        } catch (IOException e) {
            System.out.println("Eeek, an exception");

        }

        return response;
    }
}
