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

import static com.example.santu.nearme.SessionManager.PREF_NAME;
import static com.example.santu.nearme.SessionManager.USER_GROUP;
import static com.example.santu.nearme.SessionManager.USER_PUB;


public class AddPubActivity extends DrawerMenuActivity {

    private Toolbar toolbar;

    JSONParser jsonParser = new JSONParser();
    TextView testo;


    @InjectView(R.id.pub_name) EditText _pubNameText;
    @InjectView(R.id.address) EditText _addressText;
    @InjectView(R.id.num_civico) EditText _civicoText;
    @InjectView(R.id.city) EditText _cityText;
    @InjectView(R.id.cap) EditText _capText;
    @InjectView(R.id.provincia) EditText _provinciaText;
    @InjectView(R.id.phone) EditText _phoneText;
    @InjectView(R.id.email_pub) EditText _emailText;
    @InjectView(R.id.create_pub) Button _addPub;

    // url to create new pub
    private static String url_get_user = "http://toponconcert.altervista.org/api.toponconcert.info/get_user.php";
    private static String url_create_pub = "http://toponconcert.altervista.org/api.toponconcert.info/create_pub.php";
    private static String url_get_pub="http://toponconcert.altervista.org/api.toponconcert.info/get_pub.php";
    private static String url_update_user="http://toponconcert.altervista.org/api.toponconcert.info/update_user_pub.php";
    //private static String url_create_pub = "http://192.168.43.67/api.toponconcert.info/create_pub.php";
    //private static String url_create_pub = "http://192.168.0.100/api.toponconcert.info/create_pub.php";

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_USER = "user";
    private static final String TAG_PUBS = "pub";
    private static final String TAG_ID = "id_pub";

    JSONArray pub = null;

    String nome, cognome, email, id_group, id_pub_u;

    String id_pub_p, pub_name, address, num_civico, city, cap, provincia, phone, email_pub;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pub);
        ButterKnife.inject(this);



        session = new SessionManager(getApplicationContext());
        session.checkLogin();

        HashMap<String,String> dataUser = session.getUserDetails();
        email = dataUser.get(SessionManager.USER_EMAIL);
        nome = dataUser.get(SessionManager.USER_NAME);
        cognome = dataUser.get(SessionManager.USER_SURNAME);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_back);

        // button click event
        _addPub.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // creating new pub in background thread
                new CreateNewPub().execute();
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

    /**
     * Background Async Task to Create new pub
     * */
    class CreateNewPub extends AsyncTask<String, String, String> {


        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        /**
         * Creating pub
         * */
        protected String doInBackground(String... args) {

            List<NameValuePair> p = new ArrayList<>();
            p.add(new BasicNameValuePair("email", email));

            // getting JSON Object
            // Note that create pub url accepts POST method
            JSONObject json1 = jsonParser.makeHttpRequest(url_get_user,"POST", p);

            try {

                int success1 = json1.getInt(TAG_SUCCESS);

                if(success1 == 1){

                    pub = json1.getJSONArray(TAG_USER);

                    // looping through All Products
                    for (int i = 0; i < pub.length(); i++) {
                        JSONObject c = pub.getJSONObject(i);

                        id_pub_u = c.getString(TAG_ID);

                        Log.d("id_pub_u: ", id_pub_u);
                    }

                    if (id_pub_u == "null" ){
                        addGroup();
                    } else {
                        //alert: puoi avere al massimo un gruppo!
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(AddPubActivity.this, "Hai già un pub!", Toast.LENGTH_LONG).show();
                            }
                        });
                        finish();
                    }


                }


            } catch (JSONException e){
                e.printStackTrace();
            }

            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once done
        }

        public void addGroup(){
            pub_name = _pubNameText.getText().toString();
            address = _addressText.getText().toString();
            num_civico = _civicoText.getText().toString();
            city = _cityText.getText().toString();
            cap = _capText.getText().toString();
            provincia = _provinciaText.getText().toString();
            phone = _phoneText.getText().toString();
            email_pub = _emailText.getText().toString();


            // Building Parameters
            List<NameValuePair> params = new ArrayList<>();
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
            JSONObject json = jsonParser.makeHttpRequest(url_create_pub,"POST", params);

            // check log cat fro response
            Log.d("Add Pub Activity", json.toString());

            // check for success tag
            try {
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // successfully created pub

                    getIdGroup();

                } else {
                    // failed to create pub
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public void getIdGroup (){
            List<NameValuePair> param = new ArrayList<>();
            param.add(new BasicNameValuePair("email_pub", email_pub));

            Log.d("Pub Email: ", param.toString());
            JSONObject json_id_artist = jsonParser.makeHttpRequest(url_get_pub,"POST", param);

            Log.d("Pub: ", json_id_artist.toString());

            try {
                // Checking for SUCCESS TAG
                int success2 = json_id_artist.getInt(TAG_SUCCESS);

                if (success2 == 1) {
                    // products found
                    // Getting Array of Products
                    pub = json_id_artist.getJSONArray(TAG_PUBS);

                    // looping through All Products
                    for (int i = 0; i < pub.length(); i++) {
                        JSONObject c = pub.getJSONObject(i);

                        id_pub_p = c.getString(TAG_ID);

                        Log.d("id_pub: ", id_pub_p);
                    }

                    //TODO: UPDATE USER WITH id_artista
                    updateUser();

                    Intent i = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(i);
                    // closing this screen
                    finish();

                }

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e){
                e.printStackTrace();
            }

        }

        public void updateUser() {

            List<NameValuePair> param_user= new ArrayList<>();
            param_user.add(new BasicNameValuePair("email", email));
            param_user.add(new BasicNameValuePair("id_pub", id_pub_p));
            JSONObject json_update_user = jsonParser.makeHttpRequest(url_update_user,"POST", param_user);
            Log.d("Update: ", json_update_user.toString());

            try {
                int success3 = json_update_user.getInt(TAG_SUCCESS);

                if (success3 == 1) {
                    //TODO aggiorna vista e sessione utente
                    onPrepareOptionsMenu(mMenu);

                    int PRIVATE_MODE = 0;
                    SharedPreferences sharedpreferences = getApplicationContext().getSharedPreferences(PREF_NAME, PRIVATE_MODE);
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString(USER_PUB, id_pub_p);
                    editor.commit();

                    HashMap<String,String> dataUser = session.getUserDetails();
                    email = dataUser.get(SessionManager.USER_EMAIL);
                    nome = dataUser.get(SessionManager.USER_NAME);
                    cognome = dataUser.get(SessionManager.USER_SURNAME);
                    id_group = dataUser.get(SessionManager.USER_GROUP);
                    id_pub_u = dataUser.get(SessionManager.USER_PUB);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(AddPubActivity.this, "Sessione:" + email + " " + nome + " " + id_group + " " + id_pub_u, Toast.LENGTH_LONG).show();
                        }
                    });

                }

            }catch (Exception e){
                e.printStackTrace();
            }
        }

    }
}


