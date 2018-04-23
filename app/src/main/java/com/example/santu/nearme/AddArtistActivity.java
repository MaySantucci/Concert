package com.example.santu.nearme;

import android.content.Context;
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

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

import static com.example.santu.nearme.SessionManager.PREF_NAME;
import static com.example.santu.nearme.SessionManager.USER_GROUP;
import static com.example.santu.nearme.SessionManager.USER_NAME;
import static com.google.android.gms.internal.zzbfq.NULL;

public class AddArtistActivity extends DrawerMenuActivity {

    private Toolbar toolbar;

    JSONParser jsonParser = new JSONParser();

    @InjectView(R.id.group_name) EditText _groupNameText;
    @InjectView(R.id.phone) EditText _phoneText;
    @InjectView(R.id.email_group) EditText _emailText;
    @InjectView(R.id.type_music) EditText _typeMusicText;
    @InjectView(R.id.create_artist) Button _addArtist;

    // url to create new pub
    private static String url_get_user = "http://toponconcert.altervista.org/api.toponconcert.info/get_user.php";
    private static String url_create_group = "http://toponconcert.altervista.org/api.toponconcert.info/create_group.php";
    private static String url_get_artist_by_email="http://toponconcert.altervista.org/api.toponconcert.info/get_artist_by_email.php";
    private static String url_update_user="http://toponconcert.altervista.org/api.toponconcert.info/update_user.php";
    //private static String url_create_group="http://192.168.0.100/api.toponconcert.info/create_group.php";

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_USER = "user";
    private static final String TAG_ID_USER_GRUOP = "id_group";
    private static final String TAG_ARTISTS = "group";
    private static final String TAG_ARTISTA = "id_group";

    JSONArray artist = null;
    String id_artista;

    String name, phone, email_group, typeMusic;

    String nome, cognome, email;
    String id_user_artist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_artist);
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

        _addArtist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AddArtistActivity.CreateNewArtist().execute();
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
    class CreateNewArtist extends AsyncTask<String, String, String> {

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

                    artist = json1.getJSONArray(TAG_USER);

                    // looping through All Products
                    for (int i = 0; i < artist.length(); i++) {
                        JSONObject c = artist.getJSONObject(i);

                        id_user_artist = c.getString(TAG_ID_USER_GRUOP);

                        Log.d("artist_id: ", id_user_artist);
                    }

                    if (id_user_artist == "null" ){
                        addGroup();
                    } else {
                        //alert: puoi avere al massimo un gruppo!
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(AddArtistActivity.this, "Hai già un gruppo!", Toast.LENGTH_LONG).show();
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
            name = _groupNameText.getText().toString();
            phone = _phoneText.getText().toString();
            email_group = _emailText.getText().toString();
            typeMusic = _typeMusicText.getText().toString();


            // Building Parameters
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("group_name", name));
            params.add(new BasicNameValuePair("phone", phone));
            params.add(new BasicNameValuePair("email_group", email_group));
            params.add(new BasicNameValuePair("type_music", typeMusic));

            // getting JSON Object
            // Note that create pub url accepts POST method
            JSONObject json = jsonParser.makeHttpRequest(url_create_group,"POST", params);

            // check log cat fro response
            Log.d("Add Artist Activity", json.toString());

            // check for success tag
            try {
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // successfully created pub

                    getIdGroup();

                    Intent i = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(i);
                    // closing this screen
                    finish();
                } else {
                    // failed to create pub
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public void getIdGroup (){
            List<NameValuePair> param = new ArrayList<>();
            param.add(new BasicNameValuePair("email_group", email_group));

            Log.d("Artist Email: ", param.toString());
            JSONObject json_id_artist = jsonParser.makeHttpRequest(url_get_artist_by_email,"POST", param);

            Log.d("Artist: ", json_id_artist.toString());

            try {
                // Checking for SUCCESS TAG
                int success2 = json_id_artist.getInt(TAG_SUCCESS);

                if (success2 == 1) {
                    // products found
                    // Getting Array of Products
                    artist = json_id_artist.getJSONArray(TAG_ARTISTS);

                    // looping through All Products
                    for (int i = 0; i < artist.length(); i++) {
                        JSONObject c = artist.getJSONObject(i);

                        id_artista = c.getString(TAG_ARTISTA);

                        Log.d("artist_id: ", id_artista);
                    }

                    //TODO: UPDATE USER WITH id_artista
                    updateUser();

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
            param_user.add(new BasicNameValuePair("id_group", id_artista));
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
                    editor.putString(USER_GROUP, id_artista);
                    editor.commit();

                    HashMap<String,String> dataUser = session.getUserDetails();
                    email = dataUser.get(SessionManager.USER_EMAIL);
                    nome = dataUser.get(SessionManager.USER_NAME);
                    cognome = dataUser.get(SessionManager.USER_SURNAME);
                    id_user_artist = dataUser.get(SessionManager.USER_GROUP);
                    id_pub = dataUser.get(SessionManager.USER_PUB);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(AddArtistActivity.this, "Sessione:" + email + " " + nome + " " + id_user_artist + " " + id_pub, Toast.LENGTH_LONG).show();
                        }
                    });

                }

            }catch (Exception e){
                e.printStackTrace();
            }
        }

    }

}
