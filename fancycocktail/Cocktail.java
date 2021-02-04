//Author: Xiaoyu Zhu
//Id: xzhu4

package edu.cmu.fancycocktail;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


/**
 * This class display the information of the cocktail including:
 * 1. name of the drink
 * 2. category
 * 3. is Alcoholic
 * 4. The ingredient and the amount
 * 5. The instruction of cocktail. Users can choose english or German as preferred language.
 */
public class Cocktail extends AppCompatActivity {

    TextView drink;
    TextView category;
    TextView isAlcoholic;
    TextView ingredients;
    TextView instructions;

    /*
    Start the Cocktail.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras(); //provides runtime binding between main_activity and cocktail_activity.
        String cocktail = bundle.getString("cocktail"); // json data that contains cocktail information
        String language = bundle.getString("option"); // option of whether English or German
        setContentView(R.layout.content_cocktail);
        /*
         * The click listener will need a reference to this object, so that upon successfully finding the json data from API, it
         * can callback to this object with the resulting json.
         */

        displayLayout(); // call "displayLayout method to initialize the layout.
        showCocktail(cocktail,language); // show the result

        /*
         * Find the "search" button, and add a listener to it, return to the main page when the user click on it.
         */
        Button backButton = findViewById(R.id.back);
        // Add a listener to the send button
        backButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View viewParam) {
                Intent intent = new Intent(Cocktail.this, FancyCocktail.class);
                Cocktail.this.startActivity(intent);
            }
        });
    }

    /*
    the "displayLayout" mathod defines the TextView layout.
     */
    private void displayLayout() {
        drink = findViewById(R.id.drink);
        category = findViewById(R.id.category);
        isAlcoholic = findViewById(R.id.alcoholic);
        ingredients = findViewById(R.id.ingredients);
        instructions = findViewById(R.id.instructions);
    }

    /*
    Show the layout.
    The string cocktail contains information including drink, category, isAlcoholic, ingredients and instructions.
    The string language contains language.
     */
    private void showCocktail(String cocktail,String language) {
        if(cocktail != null) {
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = null;

            try {
                //converting string to json object
                jsonObject = (JSONObject) jsonParser.parse(cocktail);

            } catch (ParseException e) {
                e.printStackTrace();
            }

            // find the information if the jsonObject is not "{}"
            if(jsonObject != null && (jsonObject.size() != 0) ){
                System.out.println(jsonObject.toJSONString());
                System.out.println(jsonObject.size());
                GetCocktail getCocktail = new GetCocktail();
                Cocktail cd = this;
                getCocktail.fetchPicture((String)jsonObject.get("url"), cd); // call the fetchPicture to get remote image

                //Set the drink name
                String names = (String)jsonObject.get("drink");
                drink.setText("Beverage Name: "+names);
                if(jsonObject.get("category") != null){
                    category.setText("Category: "+ jsonObject.get("category").toString());
                }else{
                    category.setText("Category: Unspecified");
                }
                //Set the isAlcoholic TextView
                if (jsonObject.get("isAlcoholic") != null) {
                    isAlcoholic.setText("isAlcoholic: "+jsonObject.get("isAlcoholic").toString());
                }else{
                    isAlcoholic.setText("isAlcoholic: Unspecified");
                }
                //Set the ingredients TextView
                String ingredient = "";
                if(jsonObject.get("ingredients")!= null){
                    String[] ingredientObject = jsonObject.get("ingredients").toString().split("\\n");
                    for(String s:ingredientObject ){
                        ingredient += s + "\n";
                    }
                    ingredients.setText("Ingredients: \n"+ingredient);
                }else
                {
                    ingredients.setText("Ingredients: Unspecifies");
                }
                //Set the instructons TextView
                if(language.equalsIgnoreCase("english")){
                    instructions.setText("Instructions: \n"+(String)jsonObject.get("instructionEn"));
                }else if (language.equalsIgnoreCase("german")){
                    instructions.setText("Instructions: \n"+(String)jsonObject.get("instructionDe"));
                }
            }else{
                //When the jsonObject is "{}"
                drink.setText("Oops,the cocktail is not available!");
                category.setVisibility(View.INVISIBLE);
                isAlcoholic.setVisibility(View.INVISIBLE);
                ingredients.setVisibility(View.INVISIBLE);
                instructions.setVisibility(View.INVISIBLE);
            }
        }
        else{
            //When cocktail string in null
            drink.setText("Oops,the cocktail is not available!");
            category.setVisibility(View.INVISIBLE);
            isAlcoholic.setVisibility(View.INVISIBLE);
            ingredients.setVisibility(View.INVISIBLE);
            instructions.setVisibility(View.INVISIBLE);
        }
    }

    /*
    The showImage method displays the image of cocktail.
     */
    public void showImage(Bitmap image) {
        ImageView pictureView = findViewById(R.id.image);
        pictureView.setImageBitmap(image);
        pictureView.setVisibility(View.VISIBLE);
    }
}
