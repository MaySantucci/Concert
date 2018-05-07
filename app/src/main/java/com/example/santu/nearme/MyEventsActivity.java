package com.example.santu.nearme;

import android.app.ListActivity;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v4.app.NavUtils;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

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
    String id_evento, locale, artista, data, ora, descrizione;

    SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_events);


        eventsList = new ArrayList<HashMap<String, String>>();

        toolbar = findViewById(R.id.toolbar_events);
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setTitle("I miei eventi");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                NavUtils.navigateUpFromSameTask(MyEventsActivity.this);
            }
        });


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
                        id_evento = c.getString(TAG_ID_EVENTO);
                        locale = c.getString(TAG_lOCALE);
                        artista = c.getString(TAG_ARTISTA);
                        data = c.getString(TAG_DATA);
                        ora = c.getString(TAG_ORA);
                        descrizione = c.getString(TAG_DESCRIZIONE);


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

                my_events = getListView();
                runOnUiThread(new Runnable() {
                    public void run() {
                        /**
                         * Updating parsed JSON data into ListView
                         * */

                        adapter = new SimpleAdapter(
                                getApplicationContext(), eventsList,
                                R.layout.my_event_row, new String[]{
                                TAG_ARTISTA,TAG_DATA, TAG_ORA, TAG_DESCRIZIONE },
                                new int[]{ R.id.artista, R.id.data, R.id.ora, R.id.descrizione});

                       my_events.setAdapter(adapter);
                    }

                });
            } catch (Exception e) {
                e.printStackTrace();
            }

            my_events.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    Log.d("position", position + "");
                    Intent i = new Intent(MyEventsActivity.this, EditDeleteEventActivity.class);

                    i.putExtra("id_evento", id_evento);
                    i.putExtra("gruppo", artista);
                    i.putExtra("data", data);
                    i.putExtra("ora", ora);
                    i.putExtra("descrizione", descrizione);
                    startActivity(i);
                }
            });

        }
    }
}
