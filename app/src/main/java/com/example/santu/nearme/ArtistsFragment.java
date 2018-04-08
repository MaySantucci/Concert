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

public class ArtistsFragment extends ListFragment {


    private ProgressDialog pDialog;
    // Creating JSON Parser object
    JSONParser jParser = new JSONParser();

    ArrayList<HashMap<String, String>> artistsList;


    // url to get all products list
    //private static String url_all_artists = "http://192.168.43.67/api.toponconcert.info/get_all_artists.php";
    private static String url_all_artists="http://192.168.0.100/api.toponconcert.info/get_all_artists.php";

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_ARTISTS = "artist";
    private static final String TAG_ARTISTA = "id_group";
    private static final String TAG_NOME = "group_name";
    private static final String TAG_MAIL = "email_group";
    private static final String TAG_TELEFONO = "phone";
    private static final String TAG_GENERE = "type_music";

    // artists JSONArray
    JSONArray artists = null;

    public ArtistsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_artists, container, false);
        return rootView;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        artistsList = new ArrayList<HashMap<String, String>>();

        // Loading products in Background Thread
        new LoadAllArtists().execute();
    }


    /**
     * Background Async Task to Load all product by making HTTP Request
     */
    class LoadAllArtists extends AsyncTask<String, String, String> {

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
            JSONObject json = jParser.makeHttpRequest(url_all_artists, "GET", params);


            // Check your log cat for JSON reponse
            Log.d("All Artists: ", json.toString());

            try {
                // Checking for SUCCESS TAG
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // products found
                    // Getting Array of Products
                    artists = json.getJSONArray(TAG_ARTISTS);

                    // looping through All Products
                    for (int i = 0; i < artists.length(); i++) {
                        JSONObject c = artists.getJSONObject(i);


                        // Storing each json item in variable
                        String id_artista = c.getString(TAG_ARTISTA);
                        String nome_gruppo = c.getString(TAG_NOME);
                        String email = c.getString(TAG_MAIL);
                        String telefono = c.getString(TAG_TELEFONO);
                        String genere = c.getString(TAG_GENERE);

                        // creating new HashMap
                        HashMap<String, String> map = new HashMap<String, String>();

                        // adding each child node to HashMap key => value
                        map.put(TAG_ARTISTA, id_artista);
                        map.put(TAG_NOME, nome_gruppo);
                        map.put(TAG_MAIL, email);
                        map.put(TAG_TELEFONO, telefono);
                        map.put(TAG_GENERE, genere);

                        // adding HashList to ArrayList
                        artistsList.add(map);
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
                                getActivity(), artistsList,
                                R.layout.artist_row, new String[]{
                                TAG_NOME, TAG_MAIL,TAG_TELEFONO, TAG_GENERE },
                                new int[]{ R.id.group_name, R.id.email_group,
                                        R.id.phone, R.id.type_music, });
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

