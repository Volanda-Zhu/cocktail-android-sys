//Xiaoyu Zhu
//xzhu4

import org.jose4j.json.internal.json_simple.JSONObject;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;

/**
 * This class fetches the data from CocktailDB API and is deployed to Heroku.
 * It has an CocktailModel object, and calls doGet to fetch information from url and creates a jsonObject containing needed information.
 */
@WebServlet(name = "CocktailServlet", urlPatterns = {"/searchCocktail/*"} )
public class CocktailServlet extends HttpServlet {
    private CocktailModel cocktailModel;
    private CocktailMongoDB cocktailMongoDB;

    /*
    Initialization.
     */
    @Override
    public void init() {
        cocktailModel = new CocktailModel();
        cocktailMongoDB = new CocktailMongoDB();
    }

    /*
    doGet methods receive the search term from user, fetch the data from API.
    When the user selects one beverage, it will store the record into MongoDB database.
    The information includes:
    1. device type
    2. startTime
    3. latency ( endTime - startTime)
    4. drink name
    5. category
    6. entire jsonResponse - the information extracted from the original API
    7. image url
    */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        SimpleDateFormat dataFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//transfer to time format
        String path=request.getPathInfo();
        long startTime = 0;
        long endTime = 0;
        String cocktailInformation = null;
        // check whether the API url is valid, request path contain a parameter, then request API and send response to Android
        if(path != null && (!path.equals("/")) ){
            String cocktail = path.substring(1); // get the parameter, the search Term (name of beverage)
            String cocktailUrl = "https://www.thecocktaildb.com/api/json/v1/1/search.php?s=" + cocktail; //url
            if(cocktail != null) {
                cocktailInformation = cocktailModel.fetchCocktail(cocktailUrl);//response from API

                if (cocktailInformation != null) {
                    try{
                        startTime = System.currentTimeMillis();
                        String startString = dataFormat.format(startTime);
                        JSONObject jsonObject = cocktailModel.getCocktailInformation(cocktailInformation);
                        String jsonToString = jsonObject.toString();
                        endTime = System.currentTimeMillis();
                        long latency = endTime - startTime;

                        String name = (String) jsonObject.get("drink");
                        String category = (String) jsonObject.get("category");
                        String urlString = (String) jsonObject.get("url");
                        String device = request.getHeader("User-Agent"); // device type
                    // record this message into MongoDB only if it is from Android user.
                    if (device.contains("Android")  ) {
                        if(jsonObject.size() != 0)
                        cocktailMongoDB.recordData(device, startString, latency, name, category, jsonToString, urlString,cocktailUrl);
                    }

                        response.setContentType("application/json");
                        response.setCharacterEncoding("UTF-8");
                        response.getWriter().write(jsonToString);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }else{
                    System.out.println("No valid data from API,please check the name!");
                }
            }
        }else if(path != null && path.equals("/")){
            System.out.println("No input, please enter the name of cocktail!");
        }
        else{
            //if there is no parameter in the request path, go to the dashboard
            String resultView;
            // call methods in CocktailMongoDB class to get the analytic or log data and choose the result view
            request.setAttribute("latency",cocktailMongoDB.calcuateAvgLatency()); // the average latency
            request.setAttribute("totalNumber", cocktailMongoDB.calculateTotalNumber()); // the total number of records
            request.setAttribute("top 3 cocktail names", cocktailMongoDB.getTop3Cocktail()); // the top three cocktails based on search time
            request.setAttribute("top 3 cocktail categories", cocktailMongoDB.getTop3Category()); // the top three categories based on search time
            request.setAttribute("top 3 devices", cocktailMongoDB.getTop3Device());//the top three decices that use this app
            request.setAttribute("log data", cocktailMongoDB.getAllLog());//get all log information
            //pass the information to result.jsp
            resultView="result.jsp";
            RequestDispatcher view = request.getRequestDispatcher(resultView);
            view.forward(request, response);

        }

    }
}