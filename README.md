
An end-to-end mobile to cloud application to provide recipes for beverages
==========
My application provides users with a cocktail recipe. It takes a search string from the user, and uses it to fetch and display a cocktail image and information including category, isAlcoholic, ingredients and instructions (English/German).

Here is how my application meets the task requirements.

## 1.  Implement a native Android Application.

The name of my native Android application project in Android Studio is: `FancyCocktail`

### 1.1 Has at least two different kind of views in your Layout (TextView, EditText, ImageView, etc.)
My application uses TextView, EditText, Button, ImageView, Spinner and ScrollView. See content_main.xml and content_cocktail.xml for details of how they are incorporated into the LinearLayout or RelativeLayout.

Here is a screenshot of the layout before the user clicks on any button.

<img width="350" height="650" src="https://github.com/Volanda-Zhu/cocktail-android-sys/blob/master/picture/1.png"/>

### 1.2 Requires input from the users
Here is a screenshot of the user searching for a picture of cocktail 

<img width="350" height="650" src="https://github.com/Volanda-Zhu/cocktail-android-sys/blob/master/picture/2.png"/>

Click on the Spinner to choose the language for instructions.

<img width="350" height="650" src="https://github.com/Volanda-Zhu/cocktail-android-sys/blob/master/picture/3.png"/>

### 1.3 Makes an HTTP request (using an appropriate HTTP to your web service)

My application does an HTTP GET request in GetCocktail.java. The HTTP request is:
https://fast-badlands-08190.herokuapp.com/searchCocktail?name=" + searchTerm.replaceAll(" ", "\_"));
where the searchTerm is the user’s search term.
Here I replace all the space with “\_” because the url use “\_” to combine the words.
For example, If the user inputs the “long island tea”, the corresponding url will be:
https://fast-badlands-08190.herokuapp.com/searchCocktail?name=long_island_tea.
The search method makes this request of my web application, parses the returned XML to find the picture URL, fetches the picture and returns the bit image of the picture. At the same time, it also fetch other textual information as mentioned above to provide users with a thorough recipe of cocktail or other beverage.

### 1.4 Receives and parses a JSON formatted reply from the web service.

### 1.5 Display new information to the user.
Here is the screen shot after the picture and textual data have been returned.

<img width="350" height="650" src="https://github.com/Volanda-Zhu/cocktail-android-sys/blob/master/picture/4.png"/>

You can scroll down to see the whole contents

<img width="350" height="650" src="https://github.com/Volanda-Zhu/cocktail-android-sys/blob/master/picture/5.png"/>

### 1.6 Is repeatable (i.e. the user can repeatedly reuse the application without restarting it.)
The user can click on back button to return to the main page and research the next beverage.

<img width="350" height="650" src="https://github.com/Volanda-Zhu/cocktail-android-sys/blob/master/picture/6.png"/>

If the data is invalid, it will notify the user that data is invalid


<img width="350" height="650" src="https://github.com/Volanda-Zhu/cocktail-android-sys/blob/master/picture/7.png"/>

## 2. Dashboard – Display the analytical and operational data

### Log useful information

The information includes:
Information about the request from the mobile phone

• device: user-agent

• name: beverage name

• category: the beverage type (i.e ordinary drink, cocktail,etc)

Information about the request and reply to the 3rd party API

• requestUrl: request from the third API

• API Response: reply from API

Information about the reply to the mobile phone.

• JSON response: API response

• latency: endTime – startTime

• start time: startTime

<img width="1050" height="750" src="https://github.com/Volanda-Zhu/cocktail-android-sys/blob/master/picture/log_dashboard.png"/>

### 2.1 structure
In my web app project:

Model: CocktailModel.java, CocktailMongoDB.java

View: result.jsp (for dashboard), index.jsp (default setting)

### 2.2 Analytical data

• Average latency

• Top 3 cocktail names

• Top3 cocktail categories

• Top 3 devices

### 2.3 Log data
• Device

• Timestamp

• startTime

• latency

• category

• API response

• imageURL (url)

• responseURL

## 3. Deployed the web service to Heroku

The URL of my web service deployed to Heroku is
https://secret-falls-66819.herokuapp.com/searchCocktail/

The search URL:
https://secret-falls-66819.herokuapp.com/searchCocktail/cocktailwhere cocktail is the name of beverage that user want to search, such as “mojito”.


