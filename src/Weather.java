//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
import java.util.Scanner;
import java.util.*;

// API imports
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class Weather {
    public static void main(String[] args) {
        try {
            Scanner scanner = new Scanner(System.in);
            String city; //Stores user input
            do {
                System.out.println("Enter your city or say 'No' to quit:  ");
                city=scanner.nextLine();
                if(city.equalsIgnoreCase("No")) break;

                //getting data from geographical API
                JSONObject cityLocationData = (JSONObject) getLocationData(city);
                double lat = (double) cityLocationData.get("latitude");
                double longitude = (double) cityLocationData.get("longitude");

                displayWeatherData(lat, longitude);


            }while(!city.equalsIgnoreCase("No"));

        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    private static JSONObject getLocationData(String city){
        city = city.replaceAll(" ", "+");

        String urlString = "https://geocoding-api.open-meteo.com/v1/search?name=" +
                city + "&count=1&language=en&format=json";

        try{
            // 1. Fetch the API response based on API Link
            HttpURLConnection apiConnection = fetchApiResponse(urlString);

            // check for response status
            // 200 - means that the connection was a success
            if(apiConnection.getResponseCode() != 200){
                System.out.println("Error: Could not connect to API");
                return null;
            }

            // 2. Read the response and convert store String type
            String jsonResponse = readApiResponse(apiConnection);

            // 3. Parse the string into a JSON Object
            JSONParser parser = new JSONParser();
            JSONObject resultsJsonObj = (JSONObject) parser.parse(jsonResponse);

            // 4. Retrieve Location Data
            // "results" is a JSON array

            JSONArray locationData = (JSONArray) resultsJsonObj.get("results");
            return (JSONObject) locationData.get(0);

        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    private static String readApiResponse(HttpURLConnection apiConnection) {
        try {
            // stores json data
            StringBuilder resultJson = new StringBuilder();

            Scanner scanner = new Scanner(apiConnection.getInputStream());
            while(scanner.hasNext()) {
                resultJson.append(scanner.nextLine());
            }
            scanner.close();
            return resultJson.toString();

        }catch(IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static HttpURLConnection fetchApiResponse(String urlString) {
        try {
            URL url = new URL (urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("GET"); //request method is set to get
            return conn;
        }catch(IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void displayWeatherData(double lat, double longitude) {
        try {
            String url = "https://api.open-meteo.com/v1/forecast?latitude=52.52&longitude=13.41&current=temperature_2m,relative_humidity_2m,wind_speed_10m";

            HttpURLConnection apiConnection = fetchApiResponse(url);
            // check for response status
            // 200 - means that the connection was a success
            if(apiConnection.getResponseCode() != 200){
                System.out.println("Error: Could not connect to API");
                return;
            }
            //Store response in a string
            String jsonResponse = readApiResponse(apiConnection);

            JSONParser parser = new JSONParser();
            JSONObject jsonObject = (JSONObject) parser.parse(jsonResponse);
            JSONObject currentWeatherJson = (JSONObject) jsonObject.get("current");

            //build output (this is what the user will see:
            String time = (String) currentWeatherJson.get("time");
            System.out.println("Current Time: " + time);

            double temperature = (double) currentWeatherJson.get("temperature_2m");
            System.out.println("Current Temperature (C): " + temperature);

            long relativeHumidity = (long) currentWeatherJson.get("relative_humidity_2m");
            System.out.println("Relative Humidity: " + relativeHumidity);

            double windSpeed = (double) currentWeatherJson.get("wind_speed_10m");
            System.out.println("Weather Description: " + windSpeed);

        }catch(Exception e) {
            e.printStackTrace();
        }


    }


}