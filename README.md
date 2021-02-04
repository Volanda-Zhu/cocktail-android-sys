# cocktail-android-sys
An end-to-end mobile to cloud application to provide recipes for beverages

My application provides users with a cocktail recipe. It takes a search string from the user, and uses it to fetch and display a cocktail image and information including category, isAlcoholic, ingredients and instructions (English/German).

Here is how my application meets the task requirements.

1.  Implement a native Android Application.
The name of my native Android application project in Android Studio is: FancyCocktail

1.1 Has at least two different kind of views in your Layout (TextView, EditText, ImageView, etc.)
My application uses TextView, EditText, Button, ImageView, Spinner and ScrollView. See content_main.xml and content_cocktail.xml for details of how they are incorporated into the LinearLayout or RelativeLayout.

Here is a screenshot of the layout before the user clicks on any button.

<img width="350" height="650" src="https://github.com/Volanda-Zhu/cocktail-android-sys/blob/master/picture/frontpage.png"/>

1.2 Requires input from the users

1.3 Makes an HTTP request (using an appropriate HTTP to your web service)

1.4 Receives and parses a JSON formatted reply from the web service.

1.5 Display new information to the user.

<img width="350" height="650" src="https://github.com/Volanda-Zhu/cocktail-android-sys/blob/master/picture/example.png"/>

1.6 Is repeatable (i.e. the user can repeatedly reuse the application without restarting it.)
The user can click on back button to return to the main page and research the next beverage.

2. Dashboard – Display the analytical and operational data

<img width="1050" height="650" src="https://github.com/Volanda-Zhu/cocktail-android-sys/blob/master/picture/log_dashboard.png"/>

3. Deployed the web service to Heroku
