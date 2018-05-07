package com.example.santu.nearme;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class EditEventActivity extends DrawerMenuActivity {

    private Toolbar toolbar;

    @InjectView(R.id.group_name)   EditText groupT;
    @InjectView(R.id.day) EditText dateT;
    @InjectView(R.id.hour) EditText hourT;
    @InjectView(R.id.description) EditText descriptionT;
    @InjectView(R.id.btn_edit_event) Button _editButton;

    String id_evento, gruppo, data, ora, descrizione;

    JSONParser jParser = new JSONParser();

    private static String url_get_id_group = "http://toponconcert.altervista.org/api.toponconcert.info/get_artist_by_name.php";
    private static String url_update_event = "http://toponconcert.altervista.org/api.toponconcert.info/edit_event.php";


    private static final String TAG_SUCCESS = "success";
    private static final String TAG_GROUP = "group";
    private static final String TAG_ID_GROUP = "id_group";

    JSONArray group = null;

    String id_group;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_event);
        ButterKnife.inject(this);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_back);


        id_evento = getIntent().getStringExtra("id_evento");
        gruppo = getIntent().getStringExtra("artista");
        data = getIntent().getStringExtra("data");
        ora = getIntent().getStringExtra("ora");
        descrizione = getIntent().getStringExtra("descrizione");

        groupT.setText(gruppo);
        dateT.setText(data);
        hourT.setText(ora);
        descriptionT.setText(descrizione);


        _editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new takeGroupInfo().execute();
            }
        });

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


    class takeGroupInfo extends AsyncTask<String, String, String> {

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
            String gruppoU = groupT.getText().toString();
            Log.d("gruppoU: ", gruppoU);
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("group_name", gruppoU));
            // getting JSON string from URL
            JSONObject json = jParser.makeHttpRequest(url_get_id_group, "POST", params);

            Log.d("my_ params: ", params.toString());

            // Check your log cat for JSON reponse
            Log.d("My Group: ", json.toString());

            try {
                // Checking for SUCCESS TAG
                int success = json.getInt(TAG_SUCCESS);

                Log.d("success", success + "");
                if (success == 1) {
                    // event found
                    // Getting Array of events
                    group= json.getJSONArray(TAG_GROUP);

                    // looping through All events
                    for (int i = 0; i < group.length(); i++) {
                        JSONObject c = group.getJSONObject(i);

                        // Storing each json item in variable
                        id_group = c.getString(TAG_ID_GROUP);
                    }
                    Log.d("yeah: ", id_group);
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
            //TODO: updateEvent
            new EditEvent().execute();
        }
    }
    class EditEvent extends AsyncTask<String, String, String> {

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
            String dayU = dateT.getText().toString();
            String oraU = hourT.getText().toString();
            String descrU = descriptionT.getText().toString();

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("id_event", id_evento));
            params.add(new BasicNameValuePair("id_group", id_group));
            params.add(new BasicNameValuePair("day", dayU));
            params.add(new BasicNameValuePair("hour", oraU));
            params.add(new BasicNameValuePair("description", descrU));

            // getting JSON string from URL
            JSONObject json = jParser.makeHttpRequest(url_update_event, "POST", params);

            Log.d("my_ paramsssssss: ", params.toString());

            // Check your log cat for JSON reponse
            Log.d("My Event Update: ", json.toString());

            try {
                // Checking for SUCCESS TAG
                int success = json.getInt(TAG_SUCCESS);

                Log.d("success", success + "");
                if (success == 1) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(EditEventActivity.this, "Evento aggiornato con successo.", Toast.LENGTH_LONG).show();
                        }
                    });
                    Intent i = new Intent (EditEventActivity.this,MyEventsActivity.class);
                    startActivity(i);
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
        }
    }
}
