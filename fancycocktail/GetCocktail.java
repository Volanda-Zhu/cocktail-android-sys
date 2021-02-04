//Author: Xiaoyu Zhu
//Id: xzhu4
package edu.cmu.fancycocktail;
import java.io.*;
import java.net.*;
import androidx.appcompat.app.AppCompatActivity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;



/**
 * This class provides capabilities to search for textual information and image on CocktailDB API given a search term(beverage name).
 * The method "search" is the entry to the class. Network operations cannot be done from the UI thread, therefore this class makes
 * use of an AsyncCocktailSearch and AsyncCocktailPicture inner class that will do the network.
 * However, any UI updates should be done in the UI thread so avoid any synchronization problems.
 * For the AsyncCocktailSearch class, onPostExecution runs in the UI thread, and it calls the cocktailReady method to update the textual information from API.
 * For the AsyncCocktailPicture class, onPostExecution runs in the UI thread, and it calls the ImageView cocktailReady method to update the image.
 */
public class GetCocktail {
    AppCompatActivity ip = null;

    /*
     * search is the public GetCocktail method.  Its arguments are the search term, and the FancyCocktailobject that called it.
     * This provides a callback path such that the cocktailReady method in that object is called when the picture is available from the search.
     */
    public void search(String searchTerm, AppCompatActivity ip) {
        this.ip = ip;
        new AsyncCocktailSearch().execute(searchTerm);
    }

    /**
     * AsyncCocktailSearch provides a simple way to use a thread separate from the UI thread in which to do network operations.
     * doInBackground is run in the helper thread.
     * onPostExecute is run in the UI thread, allowing for safe UI updates.
     */
    private class AsyncCocktailSearch extends AsyncTask<String, Void, String> {

        /*
        The "doInBackground" method call "search" method.
         */
        protected String doInBackground(String... urls) {
            return search(urls[0]);
        }

        /*
        The "onPostExecute" method call the cocktailReady to display the detailed cocktail information.
         */
        protected void onPostExecute(String picture) { ((FancyCocktail)ip).cocktailReady(picture);
        }

        /*
         * Search CocktailDB API for the searchTerm argument, and return a String that can be put in an TextView.
         * Please uncomment line 66 and comment line 67 if you want to test task 1.
         * Please comment line 66 and uncomment line 67 if you want to test task 1.
         * This method will return a string that includes all JSON information such as ingredient.
         */
        private String search(String searchTerm){

            String inline = "";
            BufferedReader in = null;
            // Attempt to make and open URL connection.
            try {
                //replace space with "_" as the words are concatenated with "_" in the url in case the input data is invalid
                //URL url = new URL("https://fast-badlands-08190.herokuapp.com/searchCocktail?name=" + searchTerm.replaceAll(" ", "_")); //task1,
                URL url = new URL("https://secret-falls-66819.herokuapp.com/searchCocktail/" + searchTerm.replaceAll(" ", "_")); //task2

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET"); //set get request
                connection.connect(); //connect
                if(connection.getResponseCode() == 200){
                    System.out.println("connection success - 200");
                    in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
                    String str;
                    //Write all the JSON data into a string using a scanner
                    while ((str = in.readLine()) != null) {
                        inline += str;
                    }
                }else{
                    System.out.println("connection code:"+connection.getResponseCode());
                }

            } catch (IOException ex) {
                System.out.println("here is an exception: ");
                ex.printStackTrace();
            } finally{
                if (in != null){
                   try{
                       in.close();//Close the scanner
                   } catch (IOException e) {
                       e.printStackTrace();
                   }
                }
            }
            return inline;
        }
    }


    /*
     * fetchPicture is the public GetCocktail method.  Its arguments are the picture url which are extracted from the AsyncCocktailSearch.search method,
     * and the FancyCocktail object that called it.
     * This provides a callback path such that the cocktailReady method in that object is called when the picture is available from the search.
     */
    public void fetchPicture(String imgURL, Cocktail appCompatActivity) {
        this.ip = appCompatActivity;
        new AsyncCocktailPicture().execute(imgURL);
    }


    /**
     * AsyncCocktailPicture provides a simple way to update the picture.
     * It uses a thread separate from the UI thread in which to do network operations.
     * doInBackground is run in the helper thread.
     * onPostExecute is run in the UI thread, allowing for safe UI updates.
     */
    private class AsyncCocktailPicture extends AsyncTask<String, Void, Bitmap> {
        protected Bitmap doInBackground(String... urls) {
            try {
                return getRemoteImage(new URL(urls[0]));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            return null;
        }

        /*
        onPostExecute method calls the showImage method.
         */
        protected void onPostExecute(Bitmap picture) {
            ((Cocktail)ip).showImage( picture);
        }

        /*
        getRemoteImage fetch the image from the url and return Bitmap.
         */
        private Bitmap getRemoteImage(final URL url) {
            try {
                final URLConnection conn = url.openConnection();
                conn.connect();
                BufferedInputStream bis = new BufferedInputStream(conn.getInputStream());
                Bitmap bm = BitmapFactory.decodeStream(bis);
                bis.close();
                return bm;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
    }
}
