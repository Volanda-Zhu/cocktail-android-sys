import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.jose4j.json.internal.json_simple.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * Author: Xiaoyu Zhu
 *
 * Parse through every entry in log
 * Get top 5 names and Categories searched by user
 * Calculate average latency
 * Create log list
 * All all the information to Dashboard.jsp to display
 */
@WebServlet(name = "CocktailLogServlet", urlPatterns = {"/getLog"})
public class CocktailLogServlet extends HttpServlet {
    MongoCollection<Document> collection;
    MongoDatabase database;
    MongoClient mongoClient;
    List<CocktailLog> cocktailLogList;

    /**
     * This is called when web application is launched or when /getLog is called.
     * It read the mongo db to get all the logs
     * It finds the most common cocktail names and categories, calculates avg latency
     * and passes all this information to Dahsboard.jsp along with the log list
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        setupMongoConnection();
        int count = 0;
        long latency = 0;
        //create a Map to keep of number of times each entry is present in log
        Map<String, Integer> cocktailNames = new HashMap<>();
        Map<String, Integer> categoryNames = new HashMap<>();

        //create Mongo cursor to parse to all the logs in Mongo db
        MongoCursor<Document> myCursor = collection.find().iterator();
        //initialize cocktailLogList
        cocktailLogList = new ArrayList<>();

        //for every entry in the log keep track of:
        //1. cocktail name, category that were search by user
        //2. sum all the latencies to calculate avg latency
        while (myCursor.hasNext()) {
            JSONObject jsonObject = new JSONObject(myCursor.next());
            latency += (long) jsonObject.get("latency");
            count++;//counter to count the number of entries in log
            createLog(jsonObject);
            //In cocktailNames Map, store the number of times "name" appeared in log
            String name = (jsonObject.get("name").toString());
            if (cocktailNames.containsKey(name)) {
                int oldValue = cocktailNames.get(name);
                cocktailNames.put(name, oldValue + 1);
            } else {
                cocktailNames.put(name, 1);
            }
            //In categoryNames Map, store the number of times "category" appeared in log
            String category = (jsonObject.get("category").toString());
            if (categoryNames.containsKey(category)) {
                int oldValue = categoryNames.get(category);
                categoryNames.put(category, oldValue + 1);
            } else {
                categoryNames.put(category, 1);
            }
        }
        //Sort categoryNames and cocktailNames based on the count and keep in descending order
        LinkedHashMap<String, Integer> reverseSortedMap_name = new LinkedHashMap<>();
        cocktailNames.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .forEachOrdered(x -> reverseSortedMap_name.put(x.getKey(), x.getValue()));

        LinkedHashMap<String, Integer> reverseSortedMap_category = new LinkedHashMap<>();
        categoryNames.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .forEachOrdered(x -> reverseSortedMap_category.put(x.getKey(), x.getValue()));

        List<String> top5Category = new ArrayList<>();
        List<String> top5Name = new ArrayList<>();

        int i = 0;

        //get only the first 5 entries from name list to get Top 5 name
        for (String s : reverseSortedMap_name.keySet()) {
            top5Name.add(s);
            i++;
            if (i >= 5) {
                break;
            }
        }

        i = 0;
        //get only the first 5 entries from category list to get Top 5 categories
        for (String s : reverseSortedMap_category.keySet()) {
            top5Category.add(s);
            i++;
            if (i >= 5) {
                break;
            }
        }
        //set attributes that will be displayed in Dashboard.jsp
        request.setAttribute("latency", String.valueOf(latency / count));
        request.setAttribute("top5Names", top5Name);
        request.setAttribute("top5Categories", top5Category);
        request.setAttribute("logs", cocktailLogList);
        request.setAttribute("doctype", "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">");
        //forward to Dahsboard.jsp
        request.getRequestDispatcher("Dashboard.jsp").forward(request, response);

    }

    /**
     * populate cocktailLogList which will be displayed in Dashboard
     *
     * @param jsonObject
     */
    private void createLog(JSONObject jsonObject) {
        cocktailLogList.add(new CocktailLog(jsonObject.get("timestamp_request_received").toString(),
                jsonObject.get("latency").toString(),
                jsonObject.get("name").toString(),
                jsonObject.get("category").toString(),
                jsonObject.get("response_param").toString(),
                jsonObject.get("device_type").toString(),
                jsonObject.get("timestamp_reply").toString()));
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
}
