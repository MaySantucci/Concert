package com.example.santu.nearme;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;


public class MapFragment extends Fragment implements OnMapReadyCallback {


    private GoogleMap mMap;

    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);

        SupportMapFragment supportMapFragment = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map));
        supportMapFragment.getMapAsync(this);

        return rootView;
    }




    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Geocoder geocoder = new Geocoder(getContext());
        Log.d("geocoder: ", geocoder.isPresent() + "");

       //TODO: prendere dai pub solo l'indirizzo e cap e città, creare la lista di indirizzi e passarli alla list<Address>;

//        List<Address> addresses = null;
//
//        try {
//            addresses = geocoder.getFromLocationName(event.getPlace(), 20);
//            System.out.println(addresses);
//          for (int i = 0; i < addresses.size(); i++) { // MULTIPLE MATCHES
//
//              Address addr = addresses.get(i);
//
//              double latitude = addr.getLatitude();
//              double longitude = addr.getLongitude(); // DO SOMETHING WITH
//                                                      // VALUES
//
//              System.out.println(latitude);
//              System.out.println(longitude);
//
//          }
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
}