package com.example.santu.nearme;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.IllegalFormatException;
import java.util.List;
import java.util.Locale;
import android.location.Geocoder;

import android.app.ProgressDialog;


public class MapFragment extends Fragment implements OnMapReadyCallback {


    private GoogleMap mMap;
    JSONParser jsonParser = new JSONParser();

    private static final String TAG_SUCCESS = "success";
    private static final String TAG_PUBS = "pub";
    private static final String TAG_NOME_LOCALE = "pub_name";
    private static final String TAG_INDIRIZZO = "address";
    private static final String TAG_CIVICO = "num_civico";
    private static final String TAG_CITTA = "city";
    private static final String TAG_CAP = "cap";
    private static final String TAG_PROVINCIA = "provincia";


    JSONArray address_pub = null;

    Geocoder geocoder;
    String nome_pub;
    String full_addrress = "";

    private ArrayList<LatLng> latlngs;
    ArrayList<List<Address>> addressList = null;
    List<String> addresses = null;
    List<String> nomiPub = null;
    ProgressDialog dialog;

    double lat;
    double lon;

    private static String url_all_addresses= "http://toponconcert.altervista.org/api.toponconcert.info/get_all_pubs.php";

    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);

        new MapFragment.getCoordinates().execute();
        return rootView;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        try {
            if(latlngs.size() == 0 || latlngs == null){
                Log.d("Caricamento: ", latlngs.size() + "");
            }
            else {
                int i = 0;
                for (LatLng marker : latlngs){
                    mMap.addMarker(new MarkerOptions().position(marker).title(nomiPub.get(i)));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(marker));
                    i++;
                }
            }

        } catch (IllegalFormatException e){
            e.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        dialog.dismiss();
    }

    void initializeMap(){
        SupportMapFragment supportMapFragment = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map));
        supportMapFragment.getMapAsync(this);
    }

    class getCoordinates extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(getContext());
            dialog.setMessage("Caricamento in corso...");
           dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }

        protected String doInBackground(String... args) {
            List<NameValuePair> params = new ArrayList<>();
            JSONObject json = jsonParser.makeHttpRequest(url_all_addresses,"GET", params);
            Log.d("Addresses", json.toString());
            try {
                // Checking for SUCCESS TAG
                int success = json.getInt(TAG_SUCCESS);
                addresses = new ArrayList<>();
                nomiPub = new ArrayList<>();

                if (success == 1) {
                    // pubs found
                    // Getting Array of pubs
                    address_pub = json.getJSONArray(TAG_PUBS);
                    int i;
                    // looping through All pubs
                    for (i = 0; i < address_pub.length(); i++) {
                        JSONObject c = address_pub.getJSONObject(i);

                        // Storing each json item in variable
                        nome_pub = c.getString(TAG_NOME_LOCALE);
                        String indirizzo = c.getString(TAG_INDIRIZZO);
                        String civico = c.getString(TAG_CIVICO);
                        String citta = c.getString(TAG_CITTA);
                        String cap = c.getString(TAG_CAP);
                        String provincia = c.getString(TAG_PROVINCIA);

                        full_addrress = indirizzo +" "+ civico + " " + citta + " " + cap ;
                        Log.d("FULL ADDRESSES: ",  full_addrress );
                        addresses.add(i, full_addrress);
                        nomiPub.add(i, nome_pub);
                    }

                    Log.d("addresses: ",  addresses.toString() );
                }

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e){
                e.printStackTrace();
            }

            geocoder = new Geocoder(getContext(), Locale.ITALY);
            addressList = new ArrayList<>();
            try {
                for (int i = 0; i < addresses.size(); i ++){
                    addressList.add(i,geocoder.getFromLocationName(addresses.get(i), addresses.size())) ;
                }
                Log.d("addressList = ", addressList.toString());
                Log.d("addressList = ", addressList.size() + "");

            } catch (Exception ex){
                ex.printStackTrace();
            }

            latlngs = new ArrayList<>();
            try {

                Log.e("size: ", addressList.size() + "");
                for (int i = 0; i < addressList.size(); i ++){
                    lat = addressList.get(i).get(0).getLatitude();
                    lon = addressList.get(i).get(0).getLongitude();
                    latlngs.add(i, new LatLng(lat,lon));
                    Log.e("latitudine: ", lat + "");
                    Log.e("longitudine: ", lon + "");
                }

            } catch (Exception ex){
                ex.printStackTrace();
            }



            return null;
        }

        protected void onPostExecute(String file_url) {
            initializeMap();
        }

    }

}