package app.com.example.android.sunshine;

/**
 * Created by Karthik on 12/31/2014.
 */

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class ForecastFragment extends Fragment {

    public ForecastFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        //Add this line in order for the fragment to handle menu events
        setHasOptionsMenu(true);
    }
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){

        inflater.inflate(R.menu.forecastfragment,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Handle action bar clicks here.The action bar will automatically
        //handle clicks on the Home/Up button, so as you long as you
        //specify a parent activity in AndroidManifest.xml
        int id=item.getItemId();
        if(id==R.id.action_refresh){
            FetchWeatherTask fetchWeatherTask=new FetchWeatherTask();
            fetchWeatherTask.execute("94043");
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

// create an ArrayList for random values simulating weather data
        ArrayList<String> weekForecast = new ArrayList<String>();
        weekForecast.add("Tod-Sunny-88/63");
        weekForecast.add("Tom-Foggy-70/46");
        weekForecast.add("Wed-Cloudy-72/63");
        weekForecast.add("Thu-Rainy-63/51");
        weekForecast.add("Fri-Foggy-70/46");
        weekForecast.add("Sat-Sunny-76/68");

//Create an Adapter to display in ListView in the app.
        ArrayAdapter<String> forecastAdapter = new ArrayAdapter<String>(
                //the current context(this fragment's parent activity)
                getActivity(),
                //Id of List item layout
                R.layout.list_item_forecast,
                //ID of the TextView to populate
                R.id.list_item_forecast_textview,
                // forecast  data
                weekForecast);


//get the id of ListView to which you want to display. rootView indicates the root view of the fragment
        ListView listView = (ListView) rootView.findViewById(R.id.listview_forecast);


//Create connection between adapter and the view.
        listView.setAdapter(forecastAdapter);


        return rootView;
    }


//code for network connection

    public class FetchWeatherTask extends AsyncTask<String, Void, Void> {

        private final String LOG_TAG=FetchWeatherTask.class.getSimpleName();
        @Override
        protected Void doInBackground(String... params) {
        // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String forecastJsonStr = null;

            String format="json";
            String units="metric";
            int numDays=7;

            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are available at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast
                //URL url = new URL("http://api.openweathermap.org/data/2.5/forecast/daily?q=94043&mode=json&units=metric&cnt=7");

                final String FORECAST_BASE_URL="http://api.openweathermap.org/data/2.5/forecast/daily?";
                final String QUERY_PARAM="q";
                final String FORMATS_PARAM="mode";
                final String UNITS_PARAM="units";
                final String DAYS_PARAM="cnt";

                Uri builtUri= Uri.parse(FORECAST_BASE_URL).buildUpon()
                        .appendQueryParameter(QUERY_PARAM, params[0])
                        .appendQueryParameter(FORMATS_PARAM,format)
                        .appendQueryParameter(UNITS_PARAM,units)
                        .appendQueryParameter(DAYS_PARAM,Integer.toString(numDays))
                        .build();

                URL url=new URL(builtUri.toString());

                Log.v(LOG_TAG, "BUILD URL IS: "+ builtUri.toString());

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    forecastJsonStr = null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    forecastJsonStr = null;
                }
                forecastJsonStr = buffer.toString();
                //log the output
                Log.v(LOG_TAG,"foreacast weather  obtained is"+forecastJsonStr);

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attempting
                // to parse it.
                forecastJsonStr = null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("PlaceholderFragment", "Error closing stream", e);
                    }
                }
            }
            return null;
        }
    }
}
