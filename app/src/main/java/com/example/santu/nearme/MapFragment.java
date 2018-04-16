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

    List<Address> addressList = null;
    ArrayList<Marker> markersArray;

    Address a;
    JSONArray address_pub = null;
    double lat, lon;

    Geocoder geocoder;
    String nome_pub;
    private MarkerOptions options;
    private ArrayList<LatLng> latlngs;

    private static String url_all_addresses= "http://toponconcert.altervista.org/api.toponconcert.info/get_all_pubs.php";

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
        options = new MarkerOptions();
        latlngs = new ArrayList<>();

        new MapFragment.getCoordinates().execute();
        markersArray = new ArrayList<Marker>();

        try {
            Log.d("Sono nella try.", "");
                for(int i = 0; i < addressList.size(); i++){
                    lat = addressList.get(i).getLatitude();
                    lon = addressList.get(i).getLongitude();Log.d("Coordinate: ", lon + " " + lat);
                    latlngs.add(new LatLng(lat,lon));
                    //mMap.addMarker(new MarkerOptions().position(new LatLng(lat,lon)).title(nome_pub));
            }
            for(LatLng point : latlngs){
                mMap.addMarker(new MarkerOptions().position(point).title(nome_pub));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(point));
            }

        } catch (IllegalArgumentException ex ){
            ex.printStackTrace();
            Log.e("IllegalArgument: ", ex.getMessage() + "");
        } catch (Exception e){
            Log.e("Exeption: ", e.getStackTrace() + "");
        }

//        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    class getCoordinates extends AsyncTask<String, Void, String> {

        ProgressDialog dialog = new ProgressDialog(getContext());

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            dialog.setMessage("Caricamento in corso...");
//            dialog.setCanceledOnTouchOutside(false);
//            dialog.show();
        }

        protected String doInBackground(String... args) {


            List<NameValuePair> params = new ArrayList<>();

            JSONObject json = jsonParser.makeHttpRequest(url_all_addresses,"GET", params);

            Log.d("Addresses", json.toString());

            try {
                // Checking for SUCCESS TAG
                int success = json.getInt(TAG_SUCCESS);

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

                        Log.d("Il mio array 1: ",  indirizzo + " " + civico );
                        String full_addrress = indirizzo +" "+ civico + " " + citta + " " + cap;

                        a = new Address(Locale.ITALY);
                        a.setAddressLine(0, indirizzo + "," + civico);
                        a.setLocality(citta);
                        a.setPostalCode(cap);
                        a.setAdminArea(provincia);

                        geocoder = new Geocoder(getContext(), Locale.ITALY);

                        try {
                            addressList = geocoder.getFromLocationName(full_addrress, address_pub.length());
                            Log.d("addressList = ", addressList + "");

                        } catch (Exception ex){
                            ex.printStackTrace();
                        }

                        Log.d("Il mio array 2: ", a + "");
                    }
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
         * **/
        protected void onPostExecute(String file_url) {
        }

    }

}