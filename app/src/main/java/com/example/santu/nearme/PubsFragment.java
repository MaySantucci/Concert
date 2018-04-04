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


    // url to get all products list
    private static String url_all_pubs = "http://192.168.43.67/api.toponconcert.info/get_all_pubs.php";

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_PUBS = "pubs";
    private static final String TAG_ID = "id_locale";
    private static final String TAG_lOCALE = "locale";
    private static final String TAG_INDIRIZZO = "indirizzo";
    private static final String TAG_CIVICO = "civico";
    private static final String TAG_CITTA = "citta";
    private static final String TAG_CAP = "cap";
    private static final String TAG_PROVINCIA = "provincia";
    private static final String TAG_TELEFONO = "telefono";
    private static final String TAG_EMAIL = "email";

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

        // Loading products in Background Thread
        new LoadAllPubs().execute();
    }


    /**
     * Background Async Task to Load all product by making HTTP Request
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
         * getting All products from url
         */
        protected String doInBackground(String... args) {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            // getting JSON string from URL
            JSONObject json = jParser.makeHttpRequest(url_all_pubs, "GET", params);


            // Check your log cat for JSON reponse
            Log.d("All Products: ", json.toString());

            try {
                // Checking for SUCCESS TAG
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // products found
                    // Getting Array of Products
                    pubs = json.getJSONArray(TAG_PUBS);

                    // looping through All Products
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
            // dismiss the dialog after getting all products

            // updating UI from Background Thread
            try {
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        /**
                         * Updating parsed JSON data into ListView
                         * */
                        ListAdapter adapter = new SimpleAdapter(
                                getActivity(), pubsList,
                                R.layout.pub_row, new String[]{TAG_ID,
                                TAG_lOCALE},
                                new int[]{R.id.id_pub, R.id.pub_name});
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

