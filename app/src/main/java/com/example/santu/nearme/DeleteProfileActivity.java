package com.example.santu.nearme;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.design.widget.NavigationView;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.example.santu.nearme.SessionManager.PREF_NAME;
import static com.example.santu.nearme.SessionManager.USER_GROUP;
import static com.example.santu.nearme.SessionManager.USER_PUB;

public class DeleteProfileActivity extends DrawerMenuActivity {

    private static String url_get_user = "http://toponconcert.altervista.org/api.toponconcert.info/get_user.php";
    private static String url_delete_user = "http://toponconcert.altervista.org/api.toponconcert.info/delete_user.php";
    private static String url_delete_artist = "http://toponconcert.altervista.org/api.toponconcert.info/delete_group.php";
    private static String url_delete_pub = "http://toponconcert.altervista.org/api.toponconcert.info/delete_pub.php";
    private static String url_delete_events = "http://toponconcert.altervista.org/api.toponconcert.info/delete_all_events_by_group.php";
    private static String url_delete_events_pub = "http://toponconcert.altervista.org/api.toponconcert.info/delete_all_events_by_pub.php";


    private static final String TAG_SUCCESS = "success";
    private static final String TAG_USER = "user";
    private static final String TAG_ID_USER = "id_user";
    private static final String TAG_NOME = "name";
    private static final String TAG_COGNOME = "surname";
    private static final String TAG_EMAIL = "email";
    private static final String TAG_PASSWORD = "password";

    JSONArray user = null;
    JSONParser jParser = new JSONParser();

    private Toolbar toolbar;
    SessionManager session;

    private String _name, _surname, _mail;
    TextView name, surname, email_user, gruppo, locale;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_profile);

        session = new SessionManager(getApplicationContext());
        session.checkLogin();


        name = findViewById(R.id.name);
        surname = findViewById(R.id.surname);
        email_user = findViewById(R.id.email_user);

        HashMap<String, String> user = session.getUserDetails();

        _name = user.get(SessionManager.USER_NAME);
        _surname = user.get(SessionManager.USER_SURNAME);
        _mail = user.get(SessionManager.USER_EMAIL);

        name.setText(_name);
        surname.setText(_surname);
        email_user.setText(_mail);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_back);

        Button delete_user = findViewById(R.id.delete_account);
        delete_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DeleteProfileActivity.DeleteProfile().execute();
                if(id_group != "null"){

                    new DeleteProfileActivity.DeleteArtist().execute();
                }
                if(id_pub != "null") {
                    new DeleteProfileActivity.DeletePub().execute();
                }
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

    //TODO: delete user
    class DeleteProfile extends AsyncTask<String, String, String> {

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
            params.add(new BasicNameValuePair("email", email));
            // getting JSON string from URL
            JSONObject json = jParser.makeHttpRequest(url_delete_user, "POST", params);

            Log.d("my_ params: ", params.toString());

            // Check your log cat for JSON reponse
            Log.d("Me: ", json.toString());

            try {
                // Checking for SUCCESS TAG
                int success = json.getInt(TAG_SUCCESS);

                if(success == 1){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Utente eliminato con successo!", Toast.LENGTH_LONG).show();
                        }
                    });
                    finish();
                    Intent i = new Intent (DeleteProfileActivity.this, LoginActivity.class);
                    startActivity(i);
                }

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
    class DeleteArtist extends AsyncTask<String, String, String> {

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
            params.add(new BasicNameValuePair("id_group", id_group));
            // getting JSON string from URL
            JSONObject json = jParser.makeHttpRequest(url_delete_artist, "POST", params);

            Log.d("my_ params: ", params.toString());

            // Check your log cat for JSON reponse
            Log.d("Me: ", json.toString());

            try {
                // Checking for SUCCESS TAG
                int success = json.getInt(TAG_SUCCESS);

                if(success == 1){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Artista eliminato con successo!", Toast.LENGTH_LONG).show();
                        }
                    });
                }

            } catch (Exception e){
                e.printStackTrace();
            }

            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         **/
        protected void onPostExecute(String file_url) {
            onPrepareOptionsMenu(mMenu);

            int PRIVATE_MODE = 0;
            SharedPreferences sharedpreferences = getApplicationContext().getSharedPreferences(PREF_NAME, PRIVATE_MODE);
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString(USER_GROUP, "NULL");
            editor.commit();

            new DeleteEventsGroup().execute();
        }
    }
    class DeletePub extends AsyncTask<String, String, String> {

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
            JSONObject json = jParser.makeHttpRequest(url_delete_pub, "POST", params);

            Log.d("my_ params: ", params.toString());

            // Check your log cat for JSON reponse
            Log.d("Me: ", json.toString());

            try {
                // Checking for SUCCESS TAG
                int success = json.getInt(TAG_SUCCESS);

                if(success == 1){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Locale eliminato con successo!", Toast.LENGTH_LONG).show();
                        }
                    });
                }

            } catch (Exception e){
                e.printStackTrace();
            }

            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         **/
        protected void onPostExecute(String file_url) {
            onPrepareOptionsMenu(mMenu);

            int PRIVATE_MODE = 0;
            SharedPreferences sharedpreferences = getApplicationContext().getSharedPreferences(PREF_NAME, PRIVATE_MODE);
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString(USER_PUB, "NULL");
            editor.commit();

            new DeleteEventsPub().execute();
        }
    }


    class DeleteEventsGroup extends AsyncTask<String, String, String> {

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
            params.add(new BasicNameValuePair("id_group", id_group));
            // getting JSON string from URL
            JSONObject json = jParser.makeHttpRequest(url_delete_events, "POST", params);

            Log.d("my_ params: ", params.toString());

            // Check your log cat for JSON reponse
            Log.d("Me: ", json.toString());

            try {
                // Checking for SUCCESS TAG
                int success = json.getInt(TAG_SUCCESS);

                if(success == 1){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Eventi eliminati con successo!", Toast.LENGTH_LONG).show();
                        }
                    });
                }

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

    class DeleteEventsPub extends AsyncTask<String, String, String> {

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
            JSONObject json = jParser.makeHttpRequest(url_delete_events_pub, "POST", params);

            Log.d("my_ params: ", params.toString());

            // Check your log cat for JSON reponse
            Log.d("Me: ", json.toString());

            try {
                // Checking for SUCCESS TAG
                int success = json.getInt(TAG_SUCCESS);

                if(success == 1){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Eventi eliminati con successo!", Toast.LENGTH_LONG).show();
                        }
                    });
                }

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
