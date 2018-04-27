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

public class EditPubActivity extends DrawerMenuActivity {

    private Toolbar toolbar;
    SessionManager session;

    JSONParser jsonParser = new JSONParser();

    private static String url_get_pub = "http://toponconcert.altervista.org/api.toponconcert.info/get_pub_by_id.php";
    private static String url_update_pub = "http://toponconcert.altervista.org/api.toponconcert.info/update_pub.php";


    private static final String TAG_SUCCESS = "success";
    private static final String TAG_PUB = "pub";
    private static final String TAG_ID = "id_pub";
    private static final String TAG_LOCALE = "pub_name";
    private static final String TAG_ADDRESS = "address";
    private static final String TAG_CIV = "num_civico";
    private static final String TAG_CITY = "city";
    private static final String TAG_CAP = "cap";
    private static final String TAG_PROV = "provincia";
    private static final String TAG_PHONE = "phone";
    private static final String TAG_EMAIL_PUB = "email_pub";

    @InjectView(R.id.pub_name) EditText _pubNameText;
    @InjectView(R.id.address) EditText _addressText;
    @InjectView(R.id.num_civico) EditText _civicoText;
    @InjectView(R.id.city) EditText _cityText;
    @InjectView(R.id.cap) EditText _capText;
    @InjectView(R.id.provincia) EditText _provinciaText;
    @InjectView(R.id.phone) EditText _phoneText;
    @InjectView(R.id.email_pub) EditText _emailText;
    @InjectView(R.id.edit_pub) Button _editPub;

    String nome, cognome, email, id_group, id_pub_u;
    String id_pub_p, pub_name, address, num_civico, city, cap, provincia, phone, email_pub;

    JSONArray pub = null;

    ArrayList<HashMap<String, String>> pubInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_pub);
        ButterKnife.inject(this);

        session = new SessionManager(getApplicationContext());
        session.checkLogin();

        HashMap<String,String> dataUser = session.getUserDetails();
        email = dataUser.get(SessionManager.USER_EMAIL);
        nome = dataUser.get(SessionManager.USER_NAME);
        cognome = dataUser.get(SessionManager.USER_SURNAME);
        id_group = dataUser.get(SessionManager.USER_GROUP);
        id_pub_u = dataUser.get(SessionManager.USER_PUB);

        pubInfo = new ArrayList<>();
        new EditPubActivity.takePubInfo().execute();


        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_back);

        // button click event
        _editPub.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // creating new pub in background thread
                new EditPubActivity.EditPub().execute();
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

    class takePubInfo extends AsyncTask<String, String, String> {

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
            params.add(new BasicNameValuePair("id_pub", id_pub_u));
            // getting JSON string from URL
            JSONObject json = jParser.makeHttpRequest(url_get_pub, "POST", params);

            Log.d("my_ params: ", params.toString());

            // Check your log cat for JSON reponse
            Log.d("My Pub: ", json.toString());

            try {
                // Checking for SUCCESS TAG
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // event found
                    // Getting Array of events
                    pub = json.getJSONArray(TAG_PUB);

                    Log.d("yeah: ", pub.toString());
                    // looping through All events
                    for (int i = 0; i < pub.length(); i++) {
                        JSONObject c = pub.getJSONObject(i);

                        // Storing each json item in variable
                        id_pub_p = c.getString(TAG_ID);
                        pub_name = c.getString(TAG_LOCALE);
                        address = c.getString(TAG_ADDRESS);
                        num_civico = c.getString(TAG_CIV);
                        city = c.getString(TAG_CITY);
                        cap = c.getString(TAG_CAP);
                        provincia = c.getString(TAG_PROV);
                        phone = c.getString(TAG_PHONE);
                        email_pub = c.getString(TAG_EMAIL_PUB);

                        // creating new HashMap
                        HashMap<String, String> map = new HashMap<String, String>();

                        // adding each child node to HashMap key => value
                        map.put(TAG_ID, id_pub_p);
                        map.put(TAG_LOCALE, pub_name);
                        map.put(TAG_ADDRESS, address);
                        map.put(TAG_CIV, num_civico);
                        map.put(TAG_CITY, city);
                        map.put(TAG_CAP, cap);
                        map.put(TAG_PROV, provincia);
                        map.put(TAG_PHONE, phone);
                        map.put(TAG_EMAIL_PUB, email_pub);

                        // adding HashList to ArrayList
                        pubInfo.add(map);
                    }

                    Log.d("pubInfo ___", pubInfo.toString());
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
            _pubNameText.setText(pub_name);
            _addressText.setText(address);
            _civicoText.setText(num_civico);
            _cityText.setText(city);
            _capText.setText(cap);
            _provinciaText.setText(provincia);
            _phoneText.setText(phone);
            _emailText.setText(email_pub);

        }
    }


    class EditPub extends AsyncTask<String, String, String> {

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

            String pub_name = _pubNameText.getText().toString();
            String address = _addressText.getText().toString();
            String num_civico = _civicoText.getText().toString();
            String city = _cityText.getText().toString();
            String cap = _capText.getText().toString();
            String provincia = _provinciaText.getText().toString();
            String phone = _phoneText.getText().toString();
            String email_pub = _emailText.getText().toString();

            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("id_pub", id_pub_u));
            params.add(new BasicNameValuePair("pub_name", pub_name));
            params.add(new BasicNameValuePair("address", address));
            params.add(new BasicNameValuePair("num_civico", num_civico));
            params.add(new BasicNameValuePair("city", city));
            params.add(new BasicNameValuePair("cap", cap));
            params.add(new BasicNameValuePair("provincia", provincia));
            params.add(new BasicNameValuePair("phone", phone));
            params.add(new BasicNameValuePair("email_pub", email_pub));

            // getting JSON Object
            // Note that create pub url accepts POST method
            JSONObject json = jParser.makeHttpRequest(url_update_pub,"POST", params);

            // check log cat fro response
            Log.d("Update Pub", json.toString());

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
                            Toast.makeText(EditPubActivity.this, "Errore.", Toast.LENGTH_LONG).show();
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


