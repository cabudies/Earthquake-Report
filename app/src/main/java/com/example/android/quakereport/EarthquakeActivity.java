/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.quakereport;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Loader;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

public class EarthquakeActivity extends AppCompatActivity implements LoaderCallbacks<List<Earthquake>> {

    public static final String LOG_TAG = EarthquakeActivity.class.getSimpleName();
    private static final  String USGS_REQUEST_URL = "https://earthquake.usgs.gov/fdsnws/event/1/query";
    private static final int EARTHQUAKE_LOADER_ID = 1;
    private EarthquakeAdapter earthquakeAdapter;
    private TextView emptyTextView;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.earthquake_activity);

        //Async Task Data fetch
//        EarthQuakeFetchDetails earthQuakeFetchDetailsObject = new EarthQuakeFetchDetails();
//        earthQuakeFetchDetailsObject.execute();

        // Create a fake list of earthquake locations.
        //final ArrayList<Earthquake> earthquakes = QueryUtils.extractEarthquakes();

        emptyTextView = (TextView) findViewById(R.id.emptyTextView);
        progressBar = (ProgressBar) findViewById(R.id.loading_progress_bar);

        // Find a reference to the {@link ListView} in the layout
        ListView earthquakeListView = (ListView) findViewById(R.id.list);
        earthquakeListView.setEmptyView(emptyTextView);

        // Create a new adapter that takes an empty list of earthquakes as input
        earthquakeAdapter = new EarthquakeAdapter(this, new ArrayList<Earthquake>());

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        earthquakeListView.setAdapter(earthquakeAdapter);

        // Set an item click listener on the ListView, which sends an intent to a web browser
        // to open a website with more information about the selected earthquake.
        earthquakeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Find the current earthquake that was clicked on
                Earthquake currentEarthquake = earthquakeAdapter.getItem(position);

                // Convert the String URL into a URI object (to pass into the Intent constructor)
                Uri earthquakeUri = Uri.parse(currentEarthquake.getEarthQuakeCheckURL());

                // Create a new intent to view the earthquake URI
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, earthquakeUri);

                // Send the intent to launch a new activity
                startActivity(websiteIntent);
            }
        });

        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNework = connectivityManager.getActiveNetworkInfo();
        boolean isConnected = activeNework !=null &&
                activeNework.isConnectedOrConnecting();
        if (isConnected)
        {
            //Loader Manager
            LoaderManager loaderManager = getLoaderManager();
            loaderManager.initLoader(EARTHQUAKE_LOADER_ID, null, this);

        }
        else {
            emptyTextView.setText("No internet connection.");
            progressBar.setVisibility(View.GONE);
        }
    }


    @Override
    public Loader<List<Earthquake>> onCreateLoader(int i, Bundle bundle) {

        // Shared Preferences

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String minMagnitude = sharedPreferences.getString(
                getString(R.string.settings_min_magnitude_key),
                getString(R.string.settings_min_magnitude_default));

        String orderBy = sharedPreferences.getString(
                getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default));

        Uri baseUri = Uri.parse(USGS_REQUEST_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        uriBuilder.appendQueryParameter("format", "geojson");
        uriBuilder.appendQueryParameter("limit", "10");
        uriBuilder.appendQueryParameter("minmag", minMagnitude);
        uriBuilder.appendQueryParameter("orderby", orderBy);

        return new EarthquakeLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<Earthquake>> loader, List<Earthquake> earthquakesData) {

        //Clear the adapter for previous data
        progressBar.setVisibility(View.GONE);
        emptyTextView.setText("No earthquakes found.");
        earthquakeAdapter.clear();

        // If there is a valid list of {@link Earthquake} then add them to
        // the adapter's data set. This will trigger the ListView to update.
        if (earthquakesData != null && !earthquakesData.isEmpty())
        {
            earthquakeAdapter.addAll(earthquakesData);
            //updateUI(earthquakesData);
        }

    }

    @Override
    public void onLoaderReset(Loader<List<Earthquake>> loader) {

        earthquakeAdapter.clear();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings)
        {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Async Task Class
     */
    /*
    private class EarthQuakeFetchDetails extends AsyncTask<String, Void, List<Earthquake> > {
        @Override
        protected List<Earthquake> doInBackground(String... strings) {
            //convert the USGS Final String into URL to connect to the website
            URL usgsWebsiteURL = createURL(USGS_REQUEST_URL);
            //Perform HTTP request to the URL and receive the JSON response back
            String JSONResponse = "";
            try {
                JSONResponse = makeHttpRequest(usgsWebsiteURL);
            } catch (IOException e) {
                Log.e(LOG_TAG, "JSON Response error: " + e);
            }

            //Extract relevant fields from the JSON reponse that we just got and create {@link Earthquake} object
            final ArrayList<Earthquake> earthquakes = QueryUtils.extractEarthquakes(JSONResponse);

            //return the object as the result
            return earthquakes;
        }

        @Override
        protected void onPostExecute(List<Earthquake> earthquakes) {
            if (earthquakes == null) {
                return;
            }

            updateUI(earthquakes);
        }

    }*/



    /*private void updateUI(final List<Earthquake> earthquakes)
    {
        // Find a reference to the {@link ListView} in the layout
        ListView earthquakeListView = (ListView) findViewById(R.id.list);

        // Create a new {@link ArrayAdapter} of earthquakes
        earthquakeAdapter = new EarthquakeAdapter(this, earthquakes);

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        earthquakeListView.setAdapter(earthquakeAdapter);

        earthquakeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Earthquake queryUtils = earthquakes.get(position);
                String url = queryUtils.getEarthQuakeCheckURL();
                Intent quakeURL = new Intent(Intent.ACTION_VIEW);
                quakeURL.setData(Uri.parse(url));
                startActivity(quakeURL);
            }
        });
    }*/
    /*
    *//**
     * this method was declared and used to check if the string used is null or not.
     *//*
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

    *//*
    **  this makeHttpRequest method is used to make a request to the URL
    *   and receive that response in a String
     *//*
    private String makeHttpRequest(URL usgsURL) throws IOException{
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

            *//*
            *   responseCode is checked so that we can get to know what type of response is being sent.
            *   if responseCode is 200, it means that the website has successfully processed the url and is returning
            *   OK response with some data.
             *//*

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


    *//**
     *  Convert the link received from "INPUT STREAM" into a string which contains
     *  the whole JSON response from the server.
     *//*
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
    }*/

}