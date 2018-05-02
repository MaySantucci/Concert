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

public class EditGroupActivity extends DrawerMenuActivity {

    private Toolbar toolbar;
    SessionManager session;

    JSONParser jsonParser = new JSONParser();

    private static String url_get_group = "http://toponconcert.altervista.org/api.toponconcert.info/get_artist_by_id.php";
    private static String url_update_group = "http://toponconcert.altervista.org/api.toponconcert.info/update_artist.php";


    private static final String TAG_SUCCESS = "success";
    private static final String TAG_ARTIST = "group";
    private static final String TAG_NOME_GRUPPO = "group_name";
    private static final String TAG_PHONE = "phone";
    private static final String TAG_EMAIL_GRUPPO = "email_group";
    private static final String TAG_GENERE = "type_music";
    private static final String TAG_ID = "id_group";


    @InjectView(R.id.group_name) EditText _groupNameText;
    @InjectView(R.id.phone) EditText _phoneText;
    @InjectView(R.id.email_group) EditText _emailText;
    @InjectView(R.id.type_music) EditText _typeMusicText;
    @InjectView(R.id.edit_group) Button _editGroup;

    String nome, cognome, email, id_group_u, id_pub_u;
    String id_group,group_name, phone, email_group, typeMusic;

    JSONArray group = null;

    ArrayList<HashMap<String, String>> groupInfo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_group);
        ButterKnife.inject(this);


        session = new SessionManager(getApplicationContext());
        session.checkLogin();

        HashMap<String,String> dataUser = session.getUserDetails();
        email = dataUser.get(SessionManager.USER_EMAIL);
        nome = dataUser.get(SessionManager.USER_NAME);
        cognome = dataUser.get(SessionManager.USER_SURNAME);
        id_group_u = dataUser.get(SessionManager.USER_GROUP);
        id_pub_u = dataUser.get(SessionManager.USER_PUB);

        groupInfo = new ArrayList<>();
        new EditGroupActivity.takeGroupInfo().execute();


        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_back);

        // button click event
        _editGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // creating new pub in background thread
                new EditGroupActivity.EditGroup().execute();
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
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("id_group", id_group_u));
            // getting JSON string from URL
            JSONObject json = jParser.makeHttpRequest(url_get_group, "POST", params);

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
                    group = json.getJSONArray(TAG_ARTIST);

                    Log.d("yeah: ", group.toString());
                    // looping through All events
                    for (int i = 0; i < group.length(); i++) {
                        JSONObject c = group.getJSONObject(i);

                        // Storing each json item in variable
                        id_group = c.getString(TAG_ID);
                        group_name = c.getString(TAG_NOME_GRUPPO);
                        email_group = c.getString(TAG_EMAIL_GRUPPO);
                        phone = c.getString(TAG_PHONE);
                        typeMusic = c.getString(TAG_GENERE);

                        // creating new HashMap
                        HashMap<String, String> map = new HashMap<String, String>();

                        // adding each child node to HashMap key => value
                        map.put(TAG_ID, id_group);
                        map.put(TAG_NOME_GRUPPO, group_name);
                        map.put(TAG_EMAIL_GRUPPO, email_group);
                        map.put(TAG_PHONE, phone);
                        map.put(TAG_GENERE, typeMusic);

                        // adding HashList to ArrayList
                        groupInfo.add(map);
                    }

                    Log.d("groupInfo ___", groupInfo.toString());
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
            _groupNameText.setText(group_name);
            _emailText.setText(email_group);
            _typeMusicText.setText(typeMusic);
            _phoneText.setText(phone);

        }
    }


    class EditGroup extends AsyncTask<String, String, String> {

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

            String group_name = _groupNameText.getText().toString();
            String typeMusic = _typeMusicText.getText().toString();
            String phone = _phoneText.getText().toString();
            String email_group = _emailText.getText().toString();

            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("id_group", id_group_u));
            params.add(new BasicNameValuePair("group_name", group_name));
            params.add(new BasicNameValuePair("phone", phone));
            params.add(new BasicNameValuePair("email_group", email_group));
            params.add(new BasicNameValuePair("type_music", typeMusic));

            // getting JSON Object
            // Note that create pub url accepts POST method
            JSONObject json = jParser.makeHttpRequest(url_update_group,"POST", params);

            // check log cat fro response
            Log.d("Update Group", json.toString());

            // check for success tag
            try {
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // successfully created pub
                    session.logoutUser();
                    Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(i);
                } else {
                    // failed to create user

                    Log.d("Sono qui", json.toString());
                    finish();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(EditGroupActivity.this, "Errore.", Toast.LENGTH_LONG).show();
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
        }
    }
}
