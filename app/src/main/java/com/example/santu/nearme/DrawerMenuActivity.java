package com.example.santu.nearme;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
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
import java.util.Map;

public class DrawerMenuActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    Menu mMenu;
    SessionManager session;

    String nome, cognome, email, id_group, id_pub, nomeGruppo, locale;

    JSONParser jParser = new JSONParser();

    ArrayList<HashMap<String, String>> pubList;
    ArrayList<HashMap<String, String>> groupList;

    ListAdapter adapter;

    // url to get all events list
    private static String url_get_pub = "http://toponconcert.altervista.org/api.toponconcert.info/get_pub_by_id.php";
    private static String url_get_group = "http://toponconcert.altervista.org/api.toponconcert.info/get_artist_by_id.php";
//    private static String url_all_events = "http://192.168.43.67/api.toponconcert.info/get_all_events.php";
    //private static String url_all_events="http://192.168.0.100/api.toponconcert.info/get_all_events.php";

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_PUB = "pub";
    private static final String TAG_ID_PUB = "id_pub";
    private static final String TAG_lOCALE = "pub_name";
    private static final String TAG_ARTIST = "group";
    private static final String TAG_ID_GROUP = "id_group";
    private static final String TAG_ARTISTA = "group_name";

    // events JSONArray
    JSONArray pub = null;
    JSONArray group = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer_menu);


        session = new SessionManager(getApplicationContext());
        session.checkLogin();


        mDrawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view_drawer);
        mMenu = navigationView.getMenu();

        onPrepareOptionsMenu(mMenu);

        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // set item as selected to persist highlight
                        menuItem.setChecked(true);
                        // close drawer when item is tapped
                        mDrawerLayout.closeDrawers();

                        // Add code here to update the UI based on the item selected
                        // For example, swap UI fragments here


                        return true;
                    }
                });

        mDrawerLayout.addDrawerListener(
                new DrawerLayout.DrawerListener() {
                    @Override
                    public void onDrawerSlide(View drawerView, float slideOffset) {
                        // Respond when the drawer's position changes
                    }

                    @Override
                    public void onDrawerOpened(View drawerView) {
                        // Respond when the drawer is opened
                    }

                    @Override
                    public void onDrawerClosed(View drawerView) {
                        // Respond when the drawer is closed
                    }

                    @Override
                    public void onDrawerStateChanged(int newState) {
                        // Respond when the drawer motion state changes
                    }
                }
        );

        pubList = new ArrayList<HashMap<String, String>>();
        groupList = new ArrayList<>();

        boolean notFinish = true;
        while (notFinish) {
            new DrawerMenuActivity.GetGroupName().execute();
            new DrawerMenuActivity.GetPubName().execute();
            notFinish = false;
        }

        onPrepareOptionsMenu(mMenu);
        Log.d("sono fuori dal", "while");
    }

    @Override
    public void setContentView(int layoutResID)
    {
        DrawerLayout fullView = (DrawerLayout) getLayoutInflater().inflate(R.layout.activity_drawer_menu, null);
        FrameLayout activityContainer = fullView.findViewById(R.id.content_frame);
        getLayoutInflater().inflate(layoutResID, activityContainer, true);
        super.setContentView(fullView);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout = findViewById(R.id.drawer_layout);
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    public void OnMenuItemClickListener(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.my_profile:
                Intent my_profile = new Intent(this, MyProfileActivity.class);
                startActivity(my_profile);
                mDrawerLayout = findViewById(R.id.drawer_layout);
                mDrawerLayout.closeDrawers();
                break;
            case R.id.my_group:
                Intent my_group = new Intent(this, MyGroupActivity.class);
                startActivity(my_group);
                mDrawerLayout = findViewById(R.id.drawer_layout);
                mDrawerLayout.closeDrawers();
                break;
            case R.id.add_group:
                Intent add_group = new Intent(this, AddArtistActivity.class);
                startActivity(add_group);
                mDrawerLayout = findViewById(R.id.drawer_layout);
                mDrawerLayout.closeDrawers();
                break;
            case R.id.my_pub:
                Intent my_pub = new Intent(this, MyPubActivity.class);
                startActivity(my_pub);
                mDrawerLayout = findViewById(R.id.drawer_layout);
                mDrawerLayout.closeDrawers();
                break;
            case R.id.add_pub:
                Intent add_pub = new Intent(this, AddPubActivity.class);
                startActivity(add_pub);
                mDrawerLayout = findViewById(R.id.drawer_layout);
                mDrawerLayout.closeDrawers();
                break;
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        menu = mMenu;

        Log.e("size _____", menu.size() + "");

        HashMap<String, String> dataUser = session.getUserDetails();
        email = dataUser.get(SessionManager.USER_EMAIL);
        nome = dataUser.get(SessionManager.USER_NAME);
        cognome = dataUser.get(SessionManager.USER_SURNAME);
        id_group = dataUser.get(SessionManager.USER_GROUP);
        id_pub = dataUser.get(SessionManager.USER_PUB);

        boolean pronto = false;

        if(id_group != "null" && id_pub != "null"){

            Log.d("1 caso ____", id_group + " " + id_pub);
            menu.findItem(R.id.my_group).setTitle("Il mio gruppo ");
//            menu.findItem(R.id.my_group).setTitle("Il mio gruppo: " + nomeGruppo);
            menu.findItem(R.id.add_group).setVisible(false);


            menu.findItem(R.id.my_pub).setTitle("Il mio locale");
//            menu.findItem(R.id.my_pub).setTitle("Il mio locale: " + locale);
            menu.findItem(R.id.add_pub).setVisible(false);

        }
        else if(id_pub == "null" && id_group == "null"){

            Log.d("2 caso ____", id_group + " " + id_pub);
            menu.findItem(R.id.add_group).setVisible(true);
            menu.findItem(R.id.my_group).setVisible(false);

            menu.findItem(R.id.my_pub).setVisible(false);
            menu.findItem(R.id.add_pub).setVisible(true);
        }

        else if(id_pub != "null" && id_group == "null"){

            Log.d("3 caso ____", id_group + " " + id_pub);
            menu.findItem(R.id.add_group).setVisible(true);
            menu.findItem(R.id.my_group).setVisible(false);


            menu.findItem(R.id.my_pub).setTitle("Il mio locale");
//            menu.findItem(R.id.my_pub).setTitle("Locale: " + locale);
            menu.findItem(R.id.my_pub).setVisible(true);
            menu.findItem(R.id.add_pub).setVisible(false);
        }


        else if(id_pub == "null" && id_group != "null"){

            Log.d("4 caso ____", id_group + " " + id_pub);
            menu.findItem(R.id.add_group).setVisible(false);
            menu.findItem(R.id.my_group).setTitle("Il mio gruppo");
//            menu.findItem(R.id.my_group).setTitle("Gruppo: " + nomeGruppo);

            menu.findItem(R.id.my_pub).setVisible(false);
            menu.findItem(R.id.add_pub).setVisible(true);
        }



        for (int i = 0; i < menu.size(); i ++){
            Log.e("item _____", menu.getItem(i).toString());
        }

        return super.onPrepareOptionsMenu(menu);
    }

    class GetPubName extends AsyncTask<String, String, String> {

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
            JSONObject json = jParser.makeHttpRequest(url_get_pub, "POST", params);

            Log.d("name_params: ", params.toString());

            // Check your log cat for JSON reponse
            Log.d("Name_pub: ", json.toString());

            try {
                // Checking for SUCCESS TAG
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // event found
                    // Getting Array of events
                    pub = json.getJSONArray(TAG_PUB);

                    Log.d("yeah_nname: ", pub.toString());
                    // looping through All events
                    for (int i = 0; i < pub.length(); i++) {
                        JSONObject c = pub.getJSONObject(i);

                        // Storing each json item in variable
                        String id_pub_r = c.getString(TAG_ID_PUB);
                        locale = c.getString(TAG_lOCALE);

                        // creating new HashMap
                        HashMap<String, String> map = new HashMap<String, String>();

                        // adding each child node to HashMap key => value
                        map.put(TAG_ID_PUB, id_pub_r);
                        map.put(TAG_lOCALE, locale);

                        // adding HashList to ArrayList
                        pubList.add(map);
                    }

                    Log.d("NamePub", pubList.toString());
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
        }
    }
    class GetGroupName extends AsyncTask<String, String, String> {

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
            JSONObject json = jParser.makeHttpRequest(url_get_group, "POST", params);

            Log.d("group_params: ", params.toString());

            // Check your log cat for JSON reponse
            Log.d("Name_group: ", json.toString());

            try {
                // Checking for SUCCESS TAG
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // event found
                    // Getting Array of events
                    group = json.getJSONArray(TAG_ARTIST);

                    Log.d("yeah_GROUPname: ", group.toString());
                    // looping through All events
                    for (int i = 0; i < group.length(); i++) {
                        JSONObject c = group.getJSONObject(i);

                        // Storing each json item in variable
                        String id_group_r = c.getString(TAG_ID_GROUP);
                        nomeGruppo = c.getString(TAG_ARTISTA);

                        // creating new HashMap
                        HashMap<String, String> map = new HashMap<String, String>();

                        // adding each child node to HashMap key => value
                        map.put(TAG_ID_GROUP, id_group_r);
                        map.put(TAG_ARTISTA, nomeGruppo);

                        // adding HashList to ArrayList
                        groupList.add(map);
                    }

                    Log.d("NameGroup", groupList.toString());
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
        }
    }
}
