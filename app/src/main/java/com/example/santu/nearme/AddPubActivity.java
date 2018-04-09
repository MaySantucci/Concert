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
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AddPubActivity extends DrawerMenuActivity {

    private Toolbar toolbar;

    JSONParser jsonParser = new JSONParser();
    EditText inputName;
    EditText inputAddress;
    EditText inputCivico;
    EditText inputCity;
    EditText inputCap;
    EditText inputProvincia;
    EditText inputPhone;
    EditText inputEmail;
    TextView testo;

    // url to create new pub
    private static String url_create_pub = "http://192.168.43.67/api.toponconcert.info/create_pub.php";
    //private static String url_create_pub = "http://192.168.0.100/api.toponconcert.info/create_pub.php";

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pub);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_back);
        // Edit Text
        inputName = findViewById(R.id.pub_name);
        inputAddress = findViewById(R.id.address);
        inputCivico = findViewById(R.id.num_civico);
        inputCity = findViewById(R.id.city);
        inputCap = findViewById(R.id.cap);
        inputProvincia = findViewById(R.id.provincia);
        inputPhone = findViewById(R.id.phone);
        inputEmail = findViewById(R.id.email_pub);

        // Create button
        Button btnCreatePub = findViewById(R.id.confirm);
        // button click event
        btnCreatePub.setOnClickListener(new View.OnClickListener() {

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
            String name = inputName.getText().toString();
            String address = inputAddress.getText().toString();
            String civico = inputCivico.getText().toString();
            String city = inputCity.getText().toString();
            String cap = inputCap.getText().toString();
            String provincia = inputProvincia.getText().toString();
            String phone = inputPhone.getText().toString();
            String email = inputEmail.getText().toString();


            // Building Parameters
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("pub_name", name));
            params.add(new BasicNameValuePair("address", address));
            params.add(new BasicNameValuePair("num_civico", civico));
            params.add(new BasicNameValuePair("city", city));
            params.add(new BasicNameValuePair("cap", cap));
            params.add(new BasicNameValuePair("provincia", provincia));
            params.add(new BasicNameValuePair("phone", phone));
            params.add(new BasicNameValuePair("email_pub", email));

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
