//Author: Xiaoyu Zhu
//Id: xzhu4

package edu.cmu.fancycocktail;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

/**
 * The FancyCocktail is the android application that provides users with a menu of cocktail.
 * This class starts the engine, call the getCocktial class to get the information from the CocktailDB API.
 * It visualizes the main page of the android system.
 */
public class FancyCocktail extends AppCompatActivity {
    public static final String COCKTAIL = "cocktail";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*
         * The click listener will need a reference to this object, so that upon successfully finding a picture from CocktailDB, it
         * can callback to this object with the resulting picture Bitmap.  The "this" of the OnClick will be the OnClickListener, not
         * this InterestingPicture.
         */
        final FancyCocktail ma = this;
        /*
         * Find the "submit" button, and add a listener to it
         */
        Button submitButton = findViewById(R.id.submit);
        // Add a listener to the send button
        submitButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View viewParam) {
                String searchTerm = ((EditText) findViewById(R.id.searchTerm)).getText().toString();
                GetCocktail gp = new GetCocktail();
                gp.search(searchTerm, ma); // Done asynchronously in another thread.  It calls ip.pictureReady() in this thread when complete.
                TextView searchView = (EditText)findViewById(R.id.searchTerm);
                searchView.setVisibility(View.INVISIBLE);
            }
        });
    }

    /*
     * This is called by the GetCocktail object when the Json information is ready.
     * This allows for passing back the String textual information for updating the TextView and ImageView
     * It  provides runtime binding between main_activity and cocktail_activity.
     * It passes the information from the user side to the Cocktail class that allows it to visualize the layout.
     */
    public void cocktailReady(String picture) {

        Intent intent = new Intent(FancyCocktail.this, Cocktail.class);
        Spinner languageSpinner = findViewById(R.id.language);
        String language = languageSpinner.getSelectedItem().toString();
        intent.putExtra(COCKTAIL, picture);
        intent.putExtra("option", language);
        FancyCocktail.this.startActivity(intent);


    }
}

