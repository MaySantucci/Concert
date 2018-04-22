package com.example.santu.nearme;

import android.content.Intent;
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
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

public class DrawerMenuActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    Menu mMenu;
    SessionManager session;

    String nome, cognome, email, id_group, id_pub;

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

        if(id_group != "null" && id_pub != "null"){

            Log.d("1 caso ____", id_group + " " + id_pub);
            menu.findItem(R.id.my_group).setTitle(id_group + "");
            menu.findItem(R.id.add_group).setVisible(false);

            menu.findItem(R.id.my_pub).setTitle(id_pub + "");
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


            menu.findItem(R.id.my_pub).setTitle(id_pub + "");
            menu.findItem(R.id.my_pub).setVisible(true);
            menu.findItem(R.id.add_pub).setVisible(false);
        }


        else if(id_pub == "null" && id_group != "null"){

            Log.d("4 caso ____", id_group + " " + id_pub);
            menu.findItem(R.id.add_group).setVisible(false);
            menu.findItem(R.id.my_group).setTitle(id_group);

            menu.findItem(R.id.my_pub).setVisible(false);
            menu.findItem(R.id.add_pub).setVisible(true);
        }



        for (int i = 0; i < menu.size(); i ++){
            Log.e("item _____", menu.getItem(i).toString());
        }

        return super.onPrepareOptionsMenu(menu);
    }
}
