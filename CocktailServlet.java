//Author: Xiaoyu Zhu
//Id: xzhu4

import org.jose4j.json.internal.json_simple.JSONArray;
import org.jose4j.json.internal.json_simple.JSONObject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * This class fetches the data from CocktailDB API and is deployed to Heroku.
 * It has an CocktailModel object, and calls doGet to fetch information from url and creates a jsonObject containing needed information.
 */
@WebServlet(name = "CocktailServlet", urlPatterns = {"/searchCocktail"} )
public class CocktailServlet extends HttpServlet {
    private CocktailModel cocktailModel;

    /*
    Initialization.
     */
    @Override
    public void init() {
        cocktailModel = new CocktailModel();
    }


    /*
    doGet methods receive the search term from user, fetch the data from API.
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String cocktail = request.getParameter("name");
        String cocktailUrl = "https://www.thecocktaildb.com/api/json/v1/1/search.php?s=" + cocktail;

        String cocktailInformation = cocktailModel.fetchCocktail(cocktailUrl);//response from API
        // check whether the url is valid
        if (cocktailInformation != null) {

            JSONObject jsonObject = cocktailModel.getCocktailInformation(cocktailInformation);
            String jsonToString = jsonObject.toString();
            //System.out.println(jsonObject); uncomment it if you would like to see the jsonObject in the terminal
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(jsonToString);
        }
    }
}