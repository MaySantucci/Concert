package com.example.santu.nearme;

import android.graphics.Color;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

public class MainActivity extends DrawerMenuActivity
        implements ConnectivityReceiver.ConnectivityReceiverListener{

    private TabLayout tabLayout;
    DrawerLayout mDrawerLayout;
    private ViewPager viewPager;
    SessionManager session;
    String name, surname, email, id_group, id_pub;

    private TextView _name, _surname, _email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewPager = findViewById(R.id.viewpager);

        checkConnection();

        session = new SessionManager(getApplicationContext());
        session.checkLogin();

        HashMap<String,String> dataUser = session.getUserDetails();
        name = dataUser.get(SessionManager.USER_NAME);
        surname = dataUser.get(SessionManager.USER_SURNAME);
        email = dataUser.get(SessionManager.USER_EMAIL);
        id_group = dataUser.get(SessionManager.USER_GROUP);
        id_pub = dataUser.get(SessionManager.USER_PUB);


        mDrawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view_drawer);

        mMenu = navigationView.getMenu();

        View headerView = navigationView.getHeaderView(0);
        _name = headerView.findViewById(R.id.name);
        _surname = headerView.findViewById(R.id.surname);
        _email = headerView.findViewById(R.id.email);
        _name.setText(name);
        _surname.setText(surname);
        _email.setText(email);
//
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                Toast.makeText(MainActivity.this, name + " " + surname + " " + id_group + " " + id_pub, Toast.LENGTH_LONG).show();
//            }
//        });

        // Create an adapter that knows which fragment should be shown on each page
        PagerAdapter adapter = new PagerAdapter(this, getSupportFragmentManager());

        // Set the adapter onto the view pager
        viewPager.setAdapter(adapter);

        // Give the TabLayout the ViewPager
        tabLayout = findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Mappa"));
        tabLayout.addTab(tabLayout.newTab().setText("Eventi"));
        tabLayout.addTab(tabLayout.newTab().setText("Artisti"));
        tabLayout.addTab(tabLayout.newTab().setText("Locali"));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                tabLayout.getTabAt(position).select();
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

    }

    @Override
    public void onBackPressed() {
        // disable going back to the MainActivity
        moveTaskToBack(true);
    }


    //Controllo connessione del dispositivo.
    private void checkConnection() {
        boolean isConnected = ConnectivityReceiver.isConnected();
        if(isConnected){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MainActivity.this, "Connesso", Toast.LENGTH_LONG).show();
                }
            });
        }else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MainActivity.this, "Connessione assente!", Toast.LENGTH_LONG).show();
                }
            });
        }
    }


    @Override
    protected void onResume() {
        super.onResume();

        // register connection status listener
        MyApplication.getInstance().setConnectivityListener(MainActivity.this);
    }
    /**
     * Callback will be triggered when there is change in
     * network connection
     */
    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if(isConnected){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MainActivity.this, "Connesso", Toast.LENGTH_LONG).show();
                }
            });
        }else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MainActivity.this, "Connessione assente!", Toast.LENGTH_LONG).show();
                }
            });
        }
    }


}
