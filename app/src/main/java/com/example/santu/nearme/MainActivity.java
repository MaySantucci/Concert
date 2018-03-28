package com.example.santu.nearme;

import android.content.Intent;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class MainActivity extends DrawerMenuActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClick(View view){
        switch (view.getId()){
            case R.id.mappa:
                Intent mappa = new Intent (this, MapActivity.class);
                startActivity(mappa);
                break;
            case R.id.eventi:
                Intent eventi = new Intent (this, AllEventsActivity.class);
                startActivity(eventi);
                break;
        }
    }
}
