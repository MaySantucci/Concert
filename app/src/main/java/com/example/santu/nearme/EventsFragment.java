package com.example.santu.nearme;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.app.ProgressDialog;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class EventsFragment extends ListFragment {


    private ProgressDialog pDialog;
    // Creating JSON Parser object
    JSONParser jParser = new JSONParser();

    ArrayList<HashMap<String, String>> eventsList;


    // url to get all products list
    private static String url_all_events = "http://192.168.43.67/api.toponconcert.info/get_all_events.php";

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_EVENTS = "event";
    private static final String TAG_ID_EVENTO = "id_event";
    private static final String TAG_lOCALE = "pub_name";
    private static final String TAG_ARTISTA = "group_name";
    private static final String TAG_DATA = "day";
    private static final String TAG_ORA = "hour";
    private static final String TAG_DESCRIZIONE = "description";

    // events JSONArray
    JSONArray events = null;

    public EventsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_events, container, false);
        return rootView;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        eventsList = new ArrayList<HashMap<String, String>>();

        // Loading products in Background Thread
        new LoadAllEvents().execute();
    }


    /**
     * Background Async Task to Load all product by making HTTP Request
     */
    class LoadAllEvents extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        /**
         * getting All products from url
         */
        protected String doInBackground(String... args) {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            // getting JSON string from URL
            JSONObject json = jParser.makeHttpRequest(url_all_events, "GET", params);


            // Check your log cat for JSON reponse
            Log.d("All Events: ", json.toString());

            try {
                // Checking for SUCCESS TAG
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // products found
                    // Getting Array of Products
                    events = json.getJSONArray(TAG_EVENTS);

                    // looping through All Products
                    for (int i = 0; i < events.length(); i++) {
                        JSONObject c = events.getJSONObject(i);

                        // Storing each json item in variable
                        String id_evento = c.getString(TAG_ID_EVENTO);
                        String locale = c.getString(TAG_lOCALE);
                        String artista = c.getString(TAG_ARTISTA);
                        String data = c.getString(TAG_DATA);
                        String ora = c.getString(TAG_ORA);
                        String descrizione = c.getString(TAG_DESCRIZIONE);

                        // creating new HashMap
                        HashMap<String, String> map = new HashMap<String, String>();

                        // adding each child node to HashMap key => value
                        map.put(TAG_ID_EVENTO, id_evento);
                        map.put(TAG_lOCALE, locale);
                        map.put(TAG_ARTISTA, artista);
                        map.put(TAG_DATA, data);
                        map.put(TAG_ORA, ora);
                        map.put(TAG_DESCRIZIONE, descrizione);

                        // adding HashList to ArrayList
                        eventsList.add(map);
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e){
                e.printStackTrace();
            }

            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all products

            // updating UI from Background Thread
            try {
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        /**
                         * Updating parsed JSON data into ListView
                         * */
                        ListAdapter adapter = new SimpleAdapter(
                                getActivity(), eventsList,
                                R.layout.event_row, new String[]{
                                TAG_ARTISTA,TAG_lOCALE,TAG_DATA, TAG_ORA, TAG_DESCRIZIONE },
                                new int[]{ R.id.artista, R.id.locale, R.id.data, R.id.ora, R.id.descrizione});
                        // updating listview
                        setListAdapter(adapter);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

