//Xiaoyu Zhu
//xzhu4
import org.jose4j.json.internal.json_simple.JSONArray;
import org.jose4j.json.internal.json_simple.JSONObject;
import org.jose4j.json.internal.json_simple.parser.JSONParser;
import org.jose4j.json.internal.json_simple.parser.ParseException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;

/**
 * This class is the model for the servlet.
 * It contains getCocktailInformation and fetchCocktail methods.
 */
public class CocktailModel {

    /*
        This method parse the information and return an jsonObject which has all needed information from the string.
         */
    public JSONObject getCocktailInformation(String cocktailInformation)  {
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject = null;
        JSONObject jsonResponse = new JSONObject();
        JSONArray drinks = null;

        //converting string to json object
        try {
            jsonObject = (JSONObject) jsonParser.parse(cocktailInformation);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        try{
            drinks = (JSONArray) jsonObject.get("drinks");
            //some cocktail names would return a list of cocktails.
            //in that case, get a random index to choose a cocktail name

            if(drinks.size() != 0){
                Random random = new Random();
                int index = random.nextInt(drinks.size());
                //put info to the jsonResponse
                JSONObject j = (JSONObject) drinks.get(index);
                jsonResponse.put("drink", j.get("strDrink"));
                jsonResponse.put("category", j.get("strCategory"));
                jsonResponse.put("isAlcoholic", j.get("strAlcoholic"));
                jsonResponse.put("instructionEn", j.get("strInstructions"));
                jsonResponse.put("instructionDe", j.get("strInstructionsDE"));
                jsonResponse.put("url", j.get("strDrinkThumb"));
                String ingredient = "";
                // "strIngredient" is the name of ingredient and "strMeasure" is the measurement/amount of ingredient
                for(int i = 1; i < 16; i++){
                    String curr = "strIngredient" + i;
                    String measure = "strMeasure" + i;
                    if ( j.get(curr) != null && j.get(measure) != null && j.get(measure) != "" && j.get(curr) != null  ){
                        ingredient += (String) j.get(measure) + (String) j.get(curr) +"\n ";
                    }else{
                        break;
                    }
                }
                jsonResponse.put("ingredients", ingredient);
            }
        } catch (Exception e) {
            System.out.println("Error, invalid data!");
        }

        return jsonResponse;
    }

    /*
    This method fetches the cocktail from url
    200 means the connection is set up, otherwise, it will output the respond the corresponding error.(Mobile app network failure, unable to reach server)
     */
    public String fetchCocktail(String cocktailUrl) {
        String response = "";
        try {
            URL url = new URL(cocktailUrl);
            //create Http URL connection
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            if(connection.getResponseCode() == 200){
                System.out.println("connection success: 200");
                // Read all the text returned by the server
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
                String str;
                // Read each line of "in" until done, adding each to "response"
                while ((str = in.readLine()) != null) {
                    // str is one line of text readLine() strips newline characters
                    response += str;
                }

                in.close();
            }else{
                System.out.println("connection code:"+connection.getResponseCode());//if bad connection, print responsecode
            }

        } catch (IOException e) {
            System.out.println("Exception occurs");

        }

        return response;
    }
}
