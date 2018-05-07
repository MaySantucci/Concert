package com.example.santu.nearme;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class EditDeleteEventActivity extends DrawerMenuActivity {

    private Toolbar toolbar;
    String id, gruppo, data, ora, descrizione;
    private static String url_delete_event = "http://toponconcert.altervista.org/api.toponconcert.info/delete_event.php";


    private static final String TAG_SUCCESS = "success";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_delete_event);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_back);

        id = getIntent().getStringExtra("id_evento");
        gruppo = getIntent().getStringExtra("gruppo");
        data = getIntent().getStringExtra("data");
        ora = getIntent().getStringExtra("ora");
        descrizione = getIntent().getStringExtra("descrizione");

        TextView gruppoT = findViewById(R.id.artista);
        TextView dataT = findViewById(R.id.data);
        TextView oraT = findViewById(R.id.ora);
        TextView descrizioneT = findViewById(R.id.descrizione);

        gruppoT.setText(gruppo);
        dataT.setText(data);
        oraT.setText(ora);
        descrizioneT.setText(descrizione);

        Button editEvent = findViewById(R.id.edit_event);
        editEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(EditDeleteEventActivity.this, EditEventActivity.class);
                i.putExtra("id_evento", id);
                i.putExtra("artista", gruppo);
                i.putExtra("data", data);
                i.putExtra("ora", ora);
                i.putExtra("descrizione", descrizione);
                startActivity(i);
            }
        });
        Button deleteEvent = findViewById(R.id.delete_event);
        deleteEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new EditDeleteEventActivity.DeleteEvent().execute();
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


    class DeleteEvent extends AsyncTask<String, String, String> {

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
            params.add(new BasicNameValuePair("id_event", id));
            // getting JSON string from URL
            JSONObject json = jParser.makeHttpRequest(url_delete_event, "POST", params);

            Log.d("my_ params: ", params.toString());

            // Check your log cat for JSON reponse
            Log.d("My Events: ", json.toString());

            try {
                // Checking for SUCCESS TAG
                int success = json.getInt(TAG_SUCCESS);
                if (success == 1) {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(EditDeleteEventActivity.this, "Evento eliminato con successo!", Toast.LENGTH_LONG).show();
                        }
                    });
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
            Intent i = new Intent (EditDeleteEventActivity.this, MyEventsActivity.class);
            startActivity(i);

        }
    }

}
