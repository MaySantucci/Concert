package com.example.santu.nearme;

import android.content.Intent;
import android.content.SharedPreferences;
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

import static com.example.santu.nearme.SessionManager.PREF_NAME;
import static com.example.santu.nearme.SessionManager.USER_EMAIL;
import static com.example.santu.nearme.SessionManager.USER_GROUP;
import static com.example.santu.nearme.SessionManager.USER_NAME;
import static com.example.santu.nearme.SessionManager.USER_SURNAME;

public class EditProfileActivity extends DrawerMenuActivity {
    private Toolbar toolbar;
    SessionManager session;

    JSONParser jParser = new JSONParser();

    ArrayList<HashMap<String, String>> profileInfo;

    private static String url_get_user = "http://toponconcert.altervista.org/api.toponconcert.info/get_user.php";
    private static String url_update_user = "http://toponconcert.altervista.org/api.toponconcert.info/update_user_info.php";

    private static final String TAG_SUCCESS = "success";
    private static final String TAG_USER = "user";
    private static final String TAG_ID_USER = "id_user";
    private static final String TAG_NOME = "name";
    private static final String TAG_COGNOME = "surname";
    private static final String TAG_EMAIL = "email";
    private static final String TAG_PASSWORD = "password";

    JSONArray user = null;

    String id_user, name, surname, email_r, password;

    String nome_edit,cognome_edit, email_edit, password_edit;

    @InjectView(R.id.name) EditText _nameText;
    @InjectView(R.id.surname) EditText _surnameText;
    @InjectView(R.id.input_email) EditText _emailText;
    @InjectView(R.id.input_password) EditText _passwordText;
    @InjectView(R.id.btn_edit_user) Button _editButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        ButterKnife.inject(this);

        session = new SessionManager(getApplicationContext());
        session.checkLogin();

        HashMap<String, String> dataUser = session.getUserDetails();
        email = dataUser.get(SessionManager.USER_EMAIL);
        nome = dataUser.get(SessionManager.USER_NAME);
        cognome = dataUser.get(SessionManager.USER_SURNAME);
        id_group = dataUser.get(SessionManager.USER_GROUP);
        id_pub = dataUser.get(SessionManager.USER_PUB);

        profileInfo = new ArrayList<>();
        new EditProfileActivity.EditProfile().execute();

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_back);

        _editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new EditProfileActivity.EditUser().execute();
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


    class EditProfile extends AsyncTask<String, String, String> {

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
            JSONObject json = jParser.makeHttpRequest(url_get_user, "POST", params);

            Log.d("my_ params: ", params.toString());

            // Check your log cat for JSON reponse
            Log.d("Me: ", json.toString());

            try {
                // Checking for SUCCESS TAG
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // event found
                    // Getting Array of events
                    user = json.getJSONArray(TAG_USER);

                    Log.d("yeah: ", user.toString());
                    // looping through All events
                    for (int i = 0; i < user.length(); i++) {
                        JSONObject c = user.getJSONObject(i);

                        // Storing each json item in variable
                        id_user = c.getString(TAG_ID_USER);
                        name = c.getString(TAG_NOME);
                        surname = c.getString(TAG_COGNOME);
                        email_r = c.getString(TAG_EMAIL);
                        password = c.getString(TAG_PASSWORD);

                        // creating new HashMap
                        HashMap<String, String> map = new HashMap<String, String>();

                        // adding each child node to HashMap key => value
                        map.put(TAG_ID_USER, id_user);
                        map.put(TAG_NOME, name);
                        map.put(TAG_COGNOME, surname);
                        map.put(TAG_EMAIL, email_r);
                        map.put(TAG_PASSWORD, password);

                        // adding HashList to ArrayList
                        profileInfo.add(map);
                    }

                    Log.d("profileInfo___", profileInfo.toString());
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
            _nameText.setText(name);
            _surnameText.setText(surname);
            _emailText.setText(email_r);
            _passwordText.setText(password);
        }
    }
    class EditUser extends AsyncTask<String, String, String> {

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

            nome_edit = _nameText.getText().toString();
            cognome_edit = _surnameText.getText().toString();
            email_edit = _emailText.getText().toString();
            password_edit = _passwordText.getText().toString();

            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("name", nome_edit));
            params.add(new BasicNameValuePair("surname", cognome_edit));
            params.add(new BasicNameValuePair("new_email", email_edit));
            params.add(new BasicNameValuePair("old_email", email));
            params.add(new BasicNameValuePair("password", password_edit));


            Log.d("PARAMETRI___", params.toString());
            // getting JSON Object
            // Note that create pub url accepts POST method
            JSONObject json = jParser.makeHttpRequest(url_update_user,"POST", params);

            // check log cat fro response
            Log.d("Add User", json.toString());

            // check for success tag
            try {
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Profilo aggiornato con successo!", Toast.LENGTH_LONG).show();
                        }
                    });



                } else {
                    // failed to create user

                    Log.d("Sono qui", json.toString());
                    finish();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(EditProfileActivity.this, "Errore.", Toast.LENGTH_LONG).show();
                        }
                    });
                    startActivity(getIntent());
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         **/
        protected void onPostExecute(String file_url) {

            int PRIVATE_MODE = 0;
            SharedPreferences sharedpreferences = getApplicationContext().getSharedPreferences(PREF_NAME, PRIVATE_MODE);
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString(USER_NAME, nome_edit);
            editor.putString(USER_SURNAME, cognome_edit);
            editor.putString(USER_EMAIL, email_edit);
            editor.commit();

            Intent i = new Intent (EditProfileActivity.this, MainActivity.class);
            startActivity(i);
        }
    }
}
