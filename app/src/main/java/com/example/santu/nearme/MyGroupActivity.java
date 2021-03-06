package com.example.santu.nearme;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.design.widget.NavigationView;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
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

import static com.example.santu.nearme.SessionManager.PREF_NAME;
import static com.example.santu.nearme.SessionManager.USER_GROUP;
import static com.example.santu.nearme.SessionManager.USER_PUB;
import static com.google.android.gms.internal.zzbfq.NULL;

public class MyGroupActivity extends DrawerMenuActivity {

    private Toolbar toolbar;

    String _id_group;

    JSONParser jsonParser = new JSONParser();

    private static String url_get_artist_by_id = "http://toponconcert.altervista.org/api.toponconcert.info/get_artist_by_id.php";
    private static String url_delete_artist = "http://toponconcert.altervista.org/api.toponconcert.info/delete_group.php";
    private static String url_update_user = "http://toponconcert.altervista.org/api.toponconcert.info/delete_user_gruop.php";
    private static String url_delete_events = "http://toponconcert.altervista.org/api.toponconcert.info/delete_all_events_by_group.php";

    private static final String TAG_SUCCESS = "success";
    private static final String TAG_ARTIST = "group";
    private static final String TAG_NOME_GRUPPO = "group_name";
    private static final String TAG_PHONE = "phone";
    private static final String TAG_EMAIL_GRUPPO = "email_group";
    private static final String TAG_GENERE = "type_music";
    private static final String TAG_ID = "id_group";


    JSONArray artist = null;

    String id_group, id_artista, group_name, phone, email_group, typeMusic;

    TextView groupName, groupEmail, groupPhone, groupMusic;

    ProgressDialog dialogDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_group);

        session = new SessionManager(getApplicationContext());
        session.checkLogin();

        HashMap<String, String> dataUser = session.getUserDetails();
        email = dataUser.get(SessionManager.USER_EMAIL);
        nome = dataUser.get(SessionManager.USER_NAME);
        cognome = dataUser.get(SessionManager.USER_SURNAME);
        id_group = dataUser.get(SessionManager.USER_GROUP);

        NavigationView navigationView = findViewById(R.id.nav_view_group);


        View headerView = navigationView.getHeaderView(0);
        groupName = headerView.findViewById(R.id.name);
        groupEmail = headerView.findViewById(R.id.mail);
        groupPhone = headerView.findViewById(R.id.phone);
        groupMusic = headerView.findViewById(R.id.genere);

        new MyGroupActivity.GetArtist().execute();


        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_back);
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

    public void OnMenuItemClickListener(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.change_group:
                Intent edit_profile = new Intent(MyGroupActivity.this, EditGroupActivity.class);
                startActivity(edit_profile);
                break;
            case R.id.delete_group:
               new DeleteArtist().execute();
                break;
        }
    }

    class GetArtist extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         */

        ProgressDialog dialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(MyGroupActivity.this);
            dialog.setMessage("Caricamento in corso...");
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }

        /**
         * Creating pub
         */
        protected String doInBackground(String... args) {

            List<NameValuePair> p = new ArrayList<>();
            p.add(new BasicNameValuePair("id_group", id_group));

            // getting JSON Object
            // Note that create pub url accepts POST method
            JSONObject json1 = jsonParser.makeHttpRequest(url_get_artist_by_id, "POST", p);

            try {

                int success1 = json1.getInt(TAG_SUCCESS);

                if (success1 == 1) {

                    artist = json1.getJSONArray(TAG_ARTIST);
                    Log.d("artist: ", artist.toString());

                    for (int i = 0; i < artist.length(); i++) {
                        JSONObject c = artist.getJSONObject(i);
                        id_artista = c.getString(TAG_ID);
                        group_name = c.getString(TAG_NOME_GRUPPO);
                        email_group = c.getString(TAG_EMAIL_GRUPPO);
                        phone = c.getString(TAG_PHONE);
                        typeMusic = c.getString(TAG_GENERE);
                    }

                }


            } catch (JSONException e) {
                e.printStackTrace();
                Log.d("Error parsing: ", e.toString());
            }

            return null;
        }

        protected void onPostExecute(String file_url) {
            groupName.setText(group_name);
            groupEmail.setText(email_group);
            groupPhone.setText(phone);
            groupMusic.setText(typeMusic);
            dialog.dismiss();
        }

    }

    class DeleteArtist extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialogDelete = new ProgressDialog(MyGroupActivity.this);
            dialogDelete.setMessage("Eliminazione in corso...");
            dialogDelete.setCanceledOnTouchOutside(false);
            dialogDelete.show();
        }

        /**
         * Creating pub
         */
        protected String doInBackground(String... args) {

            List<NameValuePair> p = new ArrayList<>();
            p.add(new BasicNameValuePair("id_group", id_group));

            // getting JSON Object
            // Note that create pub url accepts POST method
            JSONObject json1 = jsonParser.makeHttpRequest(url_delete_artist, "POST", p);

            try {

                int success1 = json1.getInt(TAG_SUCCESS);

                if (success1 == 1) {
                    Log.d("Successo: ", success1 + "");
                }


            } catch (JSONException e) {
                e.printStackTrace();
                Log.d("Error parsing: ", e.toString());
            }

            return null;
        }

        protected void onPostExecute(String file_url) {
            new DeleteEvents().execute();
        }
    }

    class UpdateUser extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        /**
         * Creating pub
         */
        protected String doInBackground(String... args) {

            List<NameValuePair> p = new ArrayList<>();
            p.add(new BasicNameValuePair("email", email));

            // getting JSON Object
            // Note that create pub url accepts POST method
            JSONObject json = jsonParser.makeHttpRequest(url_update_user, "POST", p);

            Log.d("p___", p.toString());
            Log.d("json1 resp__", json.toString());

            try {

                int success1 = json.getInt(TAG_SUCCESS);

                if (success1 == 1) {

                    runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Gruppo aggiornato con successo!", Toast.LENGTH_LONG).show();
                    }
                });
                }


            } catch (JSONException e) {
                e.printStackTrace();
                Log.d("Error parsing: ", e.toString());
            }

            return null;
        }

        protected void onPostExecute(String file_url) {

            int PRIVATE_MODE = 0;
            SharedPreferences sharedpreferences = getApplicationContext().getSharedPreferences(PREF_NAME, PRIVATE_MODE);
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString(USER_GROUP, null);
            editor.commit();

            Intent i = new Intent (MyGroupActivity.this, MainActivity.class);
            startActivity(i);

            dialogDelete.dismiss();
            onPrepareOptionsMenu(mMenu);
        }

    }


    class DeleteEvents extends AsyncTask<String, String, String> {

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
            new MyGroupActivity.UpdateUser().execute();
        }
    }


}
