package com.example.santu.nearme;

import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.app.NavUtils;
import android.support.v4.view.GravityCompat;
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
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.HashMap;

public class MyProfileActivity extends DrawerMenuActivity {

    private Toolbar toolbar;
    SessionManager session;
    Menu mMenu;

    private TextView name, surname;

    TextView email_user;

    private String _name, _surname, _mail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        session = new SessionManager(getApplicationContext());
        session.checkLogin();

        NavigationView nav = findViewById(R.id.nav_my_profile);
        View header = nav.getHeaderView(0);
        mMenu = nav.getMenu();

        name = header.findViewById(R.id.name);
        surname = header.findViewById(R.id.surname);
        email_user = header.findViewById(R.id.email_user);

        HashMap<String, String> user = session.getUserDetails();

        _name = user.get(SessionManager.USER_NAME);
        _surname = user.get(SessionManager.USER_SURNAME);
        _mail = user.get(SessionManager.USER_EMAIL);

        name.setText(_name);
        surname.setText(_surname);
        email_user.setText(_mail);

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

            case R.id.change_email:
                //TODO change email
                break;
            case R.id.change_pw:
                //TODO go to NEW PUB
                break;
            case R.id.delete_account:
                //TODO in a popup
                break;
            case R.id.add_group:
                Intent add_group = new Intent (MyProfileActivity.this, AddArtistActivity.class);
                startActivity(add_group);
                break;
            case R.id.add_pub:
                Intent add_pub = new Intent (this, AddPubActivity.class);
                startActivity(add_pub);
                break;
            case R.id.log_out:
                session.logoutUser();
        }
    }
}
