package com.example.santu.nearme;

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
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
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

public class AddEventActivity extends DrawerMenuActivity {

    private Toolbar toolbar;

    JSONParser jsonParser = new JSONParser();

    @InjectView(R.id.group_name) EditText _groupNameText;
    @InjectView(R.id.day) EditText _dayText;
    @InjectView(R.id.hour) EditText _hourText;
    @InjectView(R.id.description) EditText _descriptionText;
    @InjectView(R.id.create_event) Button _addEvent;

    // url to create new pub
    private static String url_get_artist_by_name = "http://toponconcert.altervista.org/api.toponconcert.info/get_artist_by_name.php";
    private static String url_create_event = "http://toponconcert.altervista.org/api.toponconcert.info/create_event.php";

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_EVENTS = "event";
    private static final String TAG_EVENT = "id_event";
    private static final String TAG_GROUP_NAME = "group_name";
    private static final String TAG_DAY = "day";
    private static final String TAG_HOUR = "hour";
    private static final String TAG_DESCRIPTION = "description";

    private static final String TAG_ARTISTS = "group";
    private static final String TAG_ARTIST = "id_group";
    private static final String TAG_NOME = "group_name";
    private static final String TAG_MAIL = "email_group";
    private static final String TAG_TELEFONO = "phone";
    private static final String TAG_GENERE = "type_music";

    JSONParser jParser = new JSONParser();
    ArrayList<HashMap<String, String>> artistsList;
    JSONArray artist = null;
    JSONArray event = null;

    String group_name, id_group, genere, day, hour, description;


    String nome, cognome, email;
    String id_group_c;
    String id_user_pub;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        ButterKnife.inject(this);

        session = new SessionManager(getApplicationContext());
        session.checkLogin();

        HashMap<String, String> dataUser = session.getUserDetails();
        email = dataUser.get(SessionManager.USER_EMAIL);
        nome = dataUser.get(SessionManager.USER_NAME);
        cognome = dataUser.get(SessionManager.USER_SURNAME);
        id_user_pub = dataUser.get(SessionManager.USER_PUB);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_back);

        _addEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AddEventActivity.CheckArtist().execute();
            }
        });

        artistsList = new ArrayList<HashMap<String, String>>();

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

    class createEvent extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
        }

        protected String doInBackground(String... args){

            //TODO: take group_name and search the id_group
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("id_group", id_group));
            params.add(new BasicNameValuePair("id_pub", id_user_pub));
            params.add(new BasicNameValuePair("day", day));
            params.add(new BasicNameValuePair("hour", hour));
            params.add(new BasicNameValuePair("description", description));

            // getting JSON Object
            // Note that create pub url accepts POST method
            JSONObject json = jsonParser.makeHttpRequest(url_create_event,"POST", params);

            // check log cat fro response
            Log.d("Add Event", json.toString());

            // check for success tag
            try {
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // successfully created pub
                    Intent i = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(i);

                } else {
                    // failed to create user

                    Log.d("Sono qui", json.toString());
                    finish();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(AddEventActivity.this, "Evento non aggiunto.", Toast.LENGTH_LONG).show();
                        }
                    });
                    startActivity(getIntent());
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            // Building Parameters

            return null;
        }

        protected void onPostExecute(String file_url) {

        }
    }


    class CheckArtist extends AsyncTask<String, String, String> {

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
            group_name = _groupNameText.getText().toString();
            group_name = _groupNameText.getText().toString();
            day = _dayText.getText().toString();
            hour = _hourText.getText().toString();
            description = _descriptionText.getText().toString();

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("group_name", group_name));
            // getting JSON string from URL
            Log.d("group_name", group_name);
            JSONObject json = jParser.makeHttpRequest(url_get_artist_by_name, "POST", params);

            Log.d("json_artist_found", params.toString());

            try {

                int success = json.getInt(TAG_SUCCESS);

                Log.d("try_artist_found", json.getInt(TAG_SUCCESS) + "");
                if(success == 1){
                    artist = json.getJSONArray(TAG_ARTISTS);

                    for (int i = 0; i < artist.length(); i++) {
                        JSONObject c = artist.getJSONObject(i);

                        id_group_c = c.getString(TAG_ARTIST);

                        Log.d("artist_id: ", id_group_c);
                    }

                    id_group = id_group_c;
                    new AddEventActivity.createEvent().execute();

                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(AddEventActivity.this, "Gruppo inesistente", Toast.LENGTH_LONG).show();
                        }
                    });
                }

            } catch (JSONException e){
                e.printStackTrace();
            }


            return null;
        }

        protected void onPostExecute(String file_url) {

        }
    }
}

