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

public class PubsFragment extends ListFragment {


    private ProgressDialog pDialog;
    // Creating JSON Parser object
    JSONParser jParser = new JSONParser();

    ArrayList<HashMap<String, String>> pubsList;


    // url to get all pubs list
    //private static String url_all_pubs = "http://192.168.43.67/api.toponconcert.info/get_all_pubs.php";

    private static String url_all_pubs="http://192.168.0.100/api.toponconcert.info/get_all_pubs.php";

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_PUBS = "pub";
    private static final String TAG_ID = "id_pub";
    private static final String TAG_lOCALE = "pub_name";
    private static final String TAG_INDIRIZZO = "address";
    private static final String TAG_CIVICO = "num_civico";
    private static final String TAG_CITTA = "city";
    private static final String TAG_CAP = "cap";
    private static final String TAG_PROVINCIA = "provincia";
    private static final String TAG_TELEFONO = "phone";
    private static final String TAG_EMAIL = "email_pub";

    // pubs JSONArray
    JSONArray pubs = null;

    public PubsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_pubs, container, false);
        return rootView;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        pubsList = new ArrayList<HashMap<String, String>>();

        // Loading pubs in Background Thread
        new LoadAllPubs().execute();
    }


    /**
     * Background Async Task to Load all pub by making HTTP Request
     */
    class LoadAllPubs extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        /**
         * getting All pubs from url
         */
        protected String doInBackground(String... args) {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            // getting JSON string from URL
            JSONObject json = jParser.makeHttpRequest(url_all_pubs, "GET", params);


            // Check your log cat for JSON reponse
            Log.d("All pubs: ", json.toString());

            try {
                // Checking for SUCCESS TAG
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // pubs found
                    // Getting Array of pubs
                    pubs = json.getJSONArray(TAG_PUBS);

                    // looping through All pubs
                    for (int i = 0; i < pubs.length(); i++) {
                        JSONObject c = pubs.getJSONObject(i);

                        // Storing each json item in variable
                        String id_locale = c.getString(TAG_ID);
                        String locale = c.getString(TAG_lOCALE);
                        String indirizzo = c.getString(TAG_INDIRIZZO);
                        String civico = c.getString(TAG_CIVICO);
                        String citta = c.getString(TAG_CITTA);
                        String cap = c.getString(TAG_CAP);
                        String provincia = c.getString(TAG_PROVINCIA);
                        String telefono = c.getString(TAG_TELEFONO);
                        String email = c.getString(TAG_EMAIL);

                        // creating new HashMap
                        HashMap<String, String> map = new HashMap<String, String>();

                        // adding each child node to HashMap key => value
                        map.put(TAG_ID, id_locale);
                        map.put(TAG_lOCALE, locale);
                        map.put(TAG_INDIRIZZO, indirizzo);
                        map.put(TAG_CIVICO, civico);
                        map.put(TAG_CITTA, citta);
                        map.put(TAG_CAP, cap);
                        map.put(TAG_PROVINCIA, provincia);
                        map.put(TAG_TELEFONO, telefono);
                        map.put(TAG_EMAIL, email);

                        // adding HashList to ArrayList
                        pubsList.add(map);
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
            // updating UI from Background Thread
            try {
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        /**
                         * Updating parsed JSON data into ListView
                         * */
                        ListAdapter adapter = new SimpleAdapter(
                                getActivity(), pubsList,
                                R.layout.pub_row, new String[]{
                                TAG_lOCALE,TAG_INDIRIZZO, TAG_CIVICO, TAG_CITTA, TAG_CAP,
                                TAG_PROVINCIA, TAG_TELEFONO, TAG_EMAIL},
                                new int[]{ R.id.pub_name, R.id.address, R.id.num_civico,
                                        R.id.city, R.id.cap, R.id.provincia, R.id.phone, R.id.email_pub });
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

