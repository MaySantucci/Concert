package com.example.santu.nearme;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.DeadObjectException;
import android.support.design.widget.NavigationView;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
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

public class MyPubActivity extends DrawerMenuActivity {

    private Toolbar toolbar;
    ProgressDialog dialogDelete;

    JSONParser jsonParser = new JSONParser();

    private static String url_get_pub_by_id="http://toponconcert.altervista.org/api.toponconcert.info/get_pub_by_id.php";
    private static String url_delete_pub ="http://toponconcert.altervista.org/api.toponconcert.info/delete_pub.php";
    private static String url_update_user = "http://toponconcert.altervista.org/api.toponconcert.info/delete_user_pub.php";
    private static String url_delete_events = "http://toponconcert.altervista.org/api.toponconcert.info/delete_all_events_by_pub.php";

    private static final String TAG_SUCCESS = "success";
    private static final String TAG_PUB = "pub";
    private static final String TAG_ID= "id_pub";
    private static final String TAG_NOME_LOCALE = "pub_name";
    private static final String TAG_ADDRESS = "address";
    private static final String TAG_CIV = "num_civico";
    private static final String TAG_CITY = "city";
    private static final String TAG_CAP = "cap";
    private static final String TAG_PROVINCIA = "provincia";
    private static final String TAG_PHONE = "phone";
    private static final String TAG_EMAIL_PUB = "email_pub";


    JSONArray pub = null;


    String pub_name, address, num_civico, city, cap, provincia, email_pub, phone;

    TextView pubName, pubAddress, pubEmail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_pub);

        session = new SessionManager(getApplicationContext());
        session.checkLogin();


        HashMap<String, String> dataUser = session.getUserDetails();
        email = dataUser.get(SessionManager.USER_EMAIL);
        nome = dataUser.get(SessionManager.USER_NAME);
        cognome = dataUser.get(SessionManager.USER_SURNAME);
        id_pub = dataUser.get(SessionManager.USER_PUB);

        NavigationView navigationView = findViewById(R.id.nav_view_pub);


        View headerView = navigationView.getHeaderView(0);
        pubName = headerView.findViewById(R.id.name);
        pubEmail = headerView.findViewById(R.id.email);
        pubAddress = headerView.findViewById(R.id.address);

        new MyPubActivity.GetPub().execute();

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

    public void OnMenuItemClickListener(MenuItem item){
        switch (item.getItemId()) {
            case R.id.change_pub:
                Intent change = new Intent (MyPubActivity.this, EditPubActivity.class);
                startActivity(change);
                break;
            case R.id.delete_pub:
                new DeletePub().execute();
                break;
            case R.id.add_event:
                Intent add_event = new Intent (MyPubActivity.this, AddEventActivity.class);
                startActivity(add_event);
                break;

            case R.id.all_my_events:
                Intent my_events = new Intent (MyPubActivity.this, MyEventsActivity.class);
                startActivity(my_events);
                break;

        }
    }

    class GetPub extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         */
        ProgressDialog dialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(MyPubActivity.this);
            dialog.setMessage("Caricamento in corso...");
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }

        /**
         * Creating pub
         */
        protected String doInBackground(String... args) {

            List<NameValuePair> p = new ArrayList<>();
            p.add(new BasicNameValuePair("id_pub", id_pub));

            // getting JSON Object
            // Note that create pub url accepts POST method
            JSONObject json1 = jsonParser.makeHttpRequest(url_get_pub_by_id, "POST", p);

            try {

                int success1 = json1.getInt(TAG_SUCCESS);

                if (success1 == 1) {

                    pub = json1.getJSONArray(TAG_PUB);
                    Log.d("pub: ", pub.toString());

                    for (int i = 0; i < pub.length(); i++) {
                        JSONObject c = pub.getJSONObject(i);
                        pub_name = c.getString(TAG_NOME_LOCALE);
                        address = c.getString(TAG_ADDRESS);
                        num_civico = c.getString(TAG_CIV);
                        city = c.getString(TAG_CITY);
                        cap = c.getString(TAG_CAP);
                        provincia = c.getString(TAG_PROVINCIA);
                        email_pub = c.getString(TAG_EMAIL_PUB);
                        phone = c.getString(TAG_PHONE);
                    }

                }


            } catch (JSONException e) {
                e.printStackTrace();
                Log.d("Error parsing: ", e.toString());
            }

            return null;
        }

        protected void onPostExecute(String file_url) {
            pubName.setText(pub_name);
            pubEmail.setText(email_pub);
            pubAddress.setText(address + ", " + num_civico + " - " + city);
            dialog.dismiss();
        }

    }

    class DeletePub extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialogDelete = new ProgressDialog(MyPubActivity.this);
            dialogDelete.setMessage("Eliminazione in corso...");
            dialogDelete.setCanceledOnTouchOutside(false);
            dialogDelete.show();
        }

        /**
         * Creating pub
         */
        protected String doInBackground(String... args) {

            List<NameValuePair> p = new ArrayList<>();
            p.add(new BasicNameValuePair("id_pub", id_pub));

            // getting JSON Object
            // Note that create pub url accepts POST method
            JSONObject json1 = jsonParser.makeHttpRequest(url_delete_pub, "POST", p);

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
            new MyPubActivity.DeleteEvents().execute();
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
            p.add(new BasicNameValuePair("id_pub", null));


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
                            Toast.makeText(getApplicationContext(), "Utente aggiornato con successo!", Toast.LENGTH_LONG).show();
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
            editor.putString(USER_PUB, null);
            editor.commit();

            Intent i = new Intent (MyPubActivity.this, MainActivity.class);
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
            params.add(new BasicNameValuePair("id_pub", id_pub));
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
            new UpdateUser().execute();
        }
    }
}
