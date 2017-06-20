package com.example.android.quakereport;

import android.content.Context;
import android.content.AsyncTaskLoader;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;

/**
 * Created by gurjas on 11-06-2017.
 */

public class EarthquakeLoader extends AsyncTaskLoader<List<Earthquake>> {

    private static final String LOG_TAG = EarthquakeLoader.class.getSimpleName();

    //  Query URL
    private String mUrl;

    public EarthquakeLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        Log.d(LOG_TAG, "Start Loading");
        forceLoad();
    }

    @Override
    public List<Earthquake> loadInBackground() {
        Log.d(LOG_TAG, "Load in Background");
        if (mUrl == null)
        {
            return null;
        }

        URL usgsWebsiteURL = createURL(mUrl);
        //Perform HTTP request to the URL and receive the JSON response back
        String JSONResponse = "";
        try {
            JSONResponse = makeHttpRequest(usgsWebsiteURL);
        } catch (IOException e) {
            Log.e(LOG_TAG, "JSON Response error: " + e);
        }

        //Perform the network request, parse the response and extract the list of earthquakes.
        final List<Earthquake> earthquakes = QueryUtils.extractEarthquakes(JSONResponse);
        return earthquakes;
    }

    /**
     * this method was declared and used to check if the string used is null or not.
     */
    private URL createURL(String inputURLString)
    {
        URL urlCreate = null;
        try {
            urlCreate = new URL(inputURLString);
        }
        catch (MalformedURLException e)
        {
            Log.e(LOG_TAG, "Malformed Exception: "+ e);
            return null;
        }
        return urlCreate;
    }

    /*
    **  this makeHttpRequest method is used to make a request to the URL
    *   and receive that response in a String
     */
    private String makeHttpRequest(URL usgsURL) throws IOException {
        String jsonResponse ="";
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;

        try {
            urlConnection = (HttpURLConnection) usgsURL.openConnection(); //    making connection with the url using HttpURLConnection
            urlConnection.setRequestMethod("GET"); //   GET method is used to receive information from the url
            urlConnection.setReadTimeout(10000);  //    read timeout set to 10000 milliseconds i.e. 10 seconds.
            urlConnection.setConnectTimeout(15000); //  connection timeout set to 15000 milliseconds. i.e. 15 seconds.
            urlConnection.connect();

            int responseCode = urlConnection.getResponseCode();

            /*
            *   responseCode is checked so that we can get to know what type of response is being sent.
            *   if responseCode is 200, it means that the website has successfully processed the url and is returning
            *   OK response with some data.
             */

            if (responseCode == HttpURLConnection.HTTP_OK)
            {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            }
            else {
                jsonResponse = "";
                Log.e(LOG_TAG, "JSON RESPONSE CODE: " + responseCode);
            }
        }
        catch (IOException e)
        {
            Log.e(LOG_TAG, "Error while Making HTTP request: "+e);
        }
        finally {
            if (urlConnection != null)
            {
                urlConnection.disconnect(); //disconnect the URL because we don't want it now.
            }
            if (inputStream != null)
            {
                inputStream.close(); // after fetching the data we should always close the file.
            }
        }

        return jsonResponse;
    }


    /**
     *  Convert the link received from "INPUT STREAM" into a string which contains
     *  the whole JSON response from the server.
     */
    private String readFromStream(InputStream inputStream) throws IOException
    {
        StringBuilder output = new StringBuilder();
        if (inputStream != null)
        {
            //Input Steam Reader will check the input received via INPUT STREAM and convert it to READER in UTF-8 format.
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            // Buffered Reader is used to read the inputStreamReader's data.
            BufferedReader reader = new BufferedReader(inputStreamReader);
            //String line will read every line on BufferedReader, line by line until line is null.
            String line = reader.readLine();
            while (line != null)
            {
                //append will append all the output text or string into StringBuilder object output.
                output.append(line);
                // string object line will continue reading the text from bufferedReader, line by line.
                line = reader.readLine();
            }
        }
        //return the output String Builder object in String format.
        return output.toString();
    }
}
