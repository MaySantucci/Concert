package com.example.santu.nearme;

import android.app.LauncherActivity;
import android.app.ListActivity;
import android.content.ClipData;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.NavUtils;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MyEventsActivity extends ListActivity {

    private Toolbar toolbar;

    JSONParser jParser = new JSONParser();

    ArrayList<HashMap<String, String>> eventsList;

    ListView my_events;
    ListAdapter adapter;

    // url to get all events list
    private static String url_my_events = "http://toponconcert.altervista.org/api.toponconcert.info/get_my_events.php";
//    private static String url_all_events = "http://192.168.43.67/api.toponconcert.info/get_all_events.php";
    //private static String url_all_events="http://192.168.0.100/api.toponconcert.info/get_all_events.php";

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

    String nome, cognome, email, id_pub, id_group;

    SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_events);


        eventsList = new ArrayList<HashMap<String, String>>();

//        ActionBar actionbar = getSupportActionBar();
//        actionbar.setDisplayHomeAsUpEnabled(true);
//        actionbar.setHomeAsUpIndicator(R.drawable.ic_back);

        session = new SessionManager(getApplicationContext());
        session.checkLogin();

        HashMap<String, String> dataUser = session.getUserDetails();
        email = dataUser.get(SessionManager.USER_EMAIL);
        nome = dataUser.get(SessionManager.USER_NAME);
        cognome = dataUser.get(SessionManager.USER_SURNAME);
        id_group = dataUser.get(SessionManager.USER_GROUP);
        id_pub = dataUser.get(SessionManager.USER_PUB);

        Log.d("test__", id_pub);
        new MyEventsActivity.LoadMyEvents().execute();

    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    class LoadMyEvents extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        /**
         * getting All events from url
         */
        protected String doInBackground(String... args) {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("id_pub", id_pub));
            // getting JSON string from URL
            JSONObject json = jParser.makeHttpRequest(url_my_events, "POST", params);

            Log.d("my_ params: ", params.toString());

            // Check your log cat for JSON reponse
            Log.d("My Events: ", json.toString());

            try {
                // Checking for SUCCESS TAG
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // event found
                    // Getting Array of events
                    events = json.getJSONArray(TAG_EVENTS);

                    Log.d("yeah: ", events.toString());
                    // looping through All events
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

                    Log.d("myEvents___", eventsList.toString());
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
            // dismiss the dialog after getting all events

            // updating UI from Background Thread
            try {
                runOnUiThread(new Runnable() {
                    public void run() {
                        /**
                         * Updating parsed JSON data into ListView
                         * */
//                        my_events = findViewById(R.id.list_my_events);
//                        if(!eventsList.isEmpty()){
//
//                            adapter = new SimpleAdapter(
//                                    getApplicationContext(), eventsList,
//                                    R.layout.my_event_row, new String[]{
//                                    TAG_ID_EVENTO, TAG_ARTISTA,TAG_DATA, TAG_ORA, TAG_DESCRIZIONE },
//                                    new int[]{R.id.id_evento, R.id.artista, R.id.data, R.id.ora, R.id.descrizione});
//                            // updating listview
//                            my_events.setAdapter(adapter);
//                        } else {
//                            my_events.setVisibility(View.INVISIBLE);
//                            TextView no_events = findViewById(R.id.empty);
//                            no_events.setVisibility(View.VISIBLE);
//                        }
                        adapter = new SimpleAdapter(
                                getApplicationContext(), eventsList,
                                R.layout.my_event_row, new String[]{
                                TAG_ID_EVENTO, TAG_ARTISTA,TAG_DATA, TAG_ORA, TAG_DESCRIZIONE },
                                new int[]{R.id.id_evento, R.id.artista, R.id.data, R.id.ora, R.id.descrizione});

                        setListAdapter(adapter);
                    }

                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
