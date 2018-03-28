package com.example.santu.nearme;

import android.content.Intent;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.FrameLayout;

public class MyProfileActivity extends DrawerMenuActivity {

    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

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
                Intent i = new Intent (this, MainActivity.class);
                startActivity(i);
                return true;

            case R.id.change_email:
                //TODO go to change my email
                return true;
            case R.id.change_pw:
                //TODO go to NEW PUB
                return true;
            case R.id.delete_account:
                //TODO in a popup
                return true;
            case R.id.add_group:
                //TODO go to NEW GROUP
                return true;
            case R.id.add_pub:
                //TODO go to NEW PUB
                return true;
            case R.id.log_out:
                //TODO: go to LOGIN ACTIVITY
                return true;


        }
        return super.onOptionsItemSelected(item);
    }


}
