//Xiaoyu Zhu
//xzhu4
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import java.util.*;

/**
 * This class is responsible for backend. It sets up connection with MongoDB ,
 * stores user information to database and conducts analysis based on historical data.
 */
public class CocktailMongoDB {

    private MongoClientURI url;
    private MongoClient mongoClient;
    private MongoDatabase database;
    private MongoCollection<Document> collection;


    /*
    Initialization
     */
    CocktailMongoDB(){
        url = new MongoClientURI(
                "mongodb+srv://Volanda:zxy970606@mycluster.fh58b.mongodb.net/<dbname>?retryWrites=true&w=majority");
        mongoClient = new MongoClient(url);
        database = mongoClient.getDatabase("project4task2"); //database
        collection = database.getCollection("project4task2"); // database connected

    }

    /*
    The "recordData" method record the data as a Document and insert it into the database.
    1. device: user-agent
    2. timestamp: startTime
    3. latency: endTime - startTime
    4. name: beverage name
    5. category: the beverage type(i.e ordinary drink, cocktail,etc)
    6. API response: the string format of the entire json information extracted from API
    7. url: image url
     */
    public void recordData(String device, String startTime, long latency, String name, String category,String jsonResponse, String url, String requestUrl){
        collection = database.getCollection("project4task2");
        Document document = new Document();
        document.append("device", device);
        document.append("timeStamp", startTime);
        document.append("latency", latency);
        document.append("name", name);
        document.append("category", category);
        document.append("API Response", jsonResponse);
        document.append("url", url);
        document.append("requestUrl", requestUrl);
        collection.insertOne(document);


    }

    /*
    Calculate the average latency = total latency/the number of records
     */
    public String calcuateAvgLatency() {
        long totalLatency = 0;
        long countNum = collection.countDocuments();

        MongoCursor<Document> mongoCursor = collection.find().iterator();
        try{
            while(mongoCursor.hasNext()){
                totalLatency += ((Number)mongoCursor.next().get("latency")).longValue();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mongoCursor.close();
        }
        if(countNum == 0){
            return "No record!";
        }
        return totalLatency/countNum + " million seconds";

    }

    /*
    get the total records
     */
    public long calculateTotalNumber(){
        return (collection.countDocuments());
    }

    /*
    get the top three beverage name based on the user search
     */
    public String getTop3Cocktail(){
        String cocktailList = "";
        Map<String, Integer> cocktails = new HashMap<>();
        MongoCursor<Document> mongoCursor = collection.find().iterator();
        try{
            while(mongoCursor.hasNext()){
                String drinkName = (String) mongoCursor.next().get("name");
                if(cocktails.containsKey(drinkName)){
                    cocktails.put(drinkName, cocktails.get(drinkName) + 1);
                }else{cocktails.put(drinkName, 1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mongoCursor.close();
        }
        //LinkedHashMap is in reverse order
        LinkedHashMap<String, Integer> cocktailReverse = new LinkedHashMap<>();
        cocktails.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .forEachOrdered(x -> cocktailReverse.put(x.getKey(), x.getValue()));

        int index = 0;
        for(String drink : cocktailReverse.keySet()){
            cocktailList += (++index) + ". " + drink + "\n ";
            if( index >= 3){
                break;
            }
        }
        System.out.println("cocktail, " + cocktails.size());
        System.out.println(cocktailReverse.size());
        return  cocktailList;

    }

    /*
   get the top three devices information based on the user search
    */
    public String getTop3Device(){
        String deviceList = "";
        Map<String, Integer> devices = new HashMap<>();
        MongoCursor<Document> mongoCursor = collection.find().iterator();
        try{
            while(mongoCursor.hasNext()){
                String deviceName = (String) mongoCursor.next().get("device");
                if(devices.containsKey(deviceName )){
                    devices.put(deviceName , devices.get(deviceName ) + 1);
                }else{devices.put(deviceName , 1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mongoCursor.close();
        }

        LinkedHashMap<String, Integer> deviceReverse = new LinkedHashMap<>();
        devices.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .forEachOrdered(x -> deviceReverse.put(x.getKey(), x.getValue()));

        int index = 0;
        for(String device : deviceReverse.keySet()){
            deviceList += (++index) + ". " + device + "\n ";
            index++;
            if( index >= 3){
                break;
            }
        }
        return  deviceList;
    }

    /*
    get all the log information
     */
    public String getAllLog(){
        MongoCursor<Document> mongoCursor = collection.find().iterator();
        StringBuilder logData = new StringBuilder();
        try {
            while (mongoCursor.hasNext()) {
                Document document =mongoCursor.next();
                if(document.get("name") == null){
                    continue;
                }
                logData.append("<tr>");
                logData.append("<td>").append(document.get("device")).append("</td>");
                logData.append("<td>").append(document.get("timeStamp")).append("</td>");
                logData.append("<td>").append(document.get("latency")).append("</td>");
                logData.append("<td>").append(document.get("name")).append("</td>");
                logData.append("<td>").append(document.get("category")).append("</td>");
                logData.append("<td class='data'> ").append(document.get("API Response").toString()).append("</td>");
                logData.append("<td class='data2'> ").append(document.get("url").toString()).append("</td>");
                logData.append("<td class='data2'> ").append(document.get("requestUrl").toString()).append("</td>");
                logData.append("</tr>");
            }
        } finally {
            mongoCursor.close();
        }
        return logData.toString();
    }


    /*
       get the top three categories based on the user search
        */
    public String getTop3Category() {
        String categoryList = "";
        Map<String, Integer> categories = new HashMap<>();
        MongoCursor<Document> mongoCursor = collection.find().iterator();
        try{
            while(mongoCursor.hasNext()){
                String categoryName = (String) mongoCursor.next().get("category");
                if(categories.containsKey(categoryName)){
                    categories.put(categoryName , categories.get(categoryName) + 1);
                }else{categories.put(categoryName , 1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mongoCursor.close();
        }
        LinkedHashMap<String, Integer> categoryReverse = new LinkedHashMap<>();
        categories.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .forEachOrdered(x -> categoryReverse.put(x.getKey(), x.getValue()));
        int index = 0;
        for(String category : categoryReverse.keySet()){
            categoryList += (++index) + ". " + category + "\n ";
            if( index >= 3){
                break;
            }
        }
        return  categoryList;
    }
}
