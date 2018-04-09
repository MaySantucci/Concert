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

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AddArtistActivity extends DrawerMenuActivity {

    private Toolbar toolbar;

    JSONParser jsonParser = new JSONParser();
    EditText inputName;
    EditText inputPhone;
    EditText inputEmail;
    EditText inputTypeMusic;

    // url to create new pub
    private static String url_create_group = "http://192.168.43.67/api.toponconcert.info/create_artist.php";
    //private static String url_create_group="http://192.168.0.100/api.toponconcert.info/create_group.php";

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_artist);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_back);
        // Edit Text
        inputName = findViewById(R.id.group_name);
        inputPhone = findViewById(R.id.phone);
        inputEmail = findViewById(R.id.email_group);
        inputTypeMusic = findViewById(R.id.type_music);

        // Create button
        Button btnCreateArtist = findViewById(R.id.createArtist);
        // button click event
        btnCreateArtist.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // creating new pub in background thread
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
            String name = inputName.getText().toString();
            String phone = inputPhone.getText().toString();
            String email = inputEmail.getText().toString();
            String typeMusic = inputTypeMusic.getText().toString();


            // Building Parameters
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("group_name", name));
            params.add(new BasicNameValuePair("phone", phone));
            params.add(new BasicNameValuePair("email_group", email));
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
            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once done
        }

    }
}
