package com.example.santu.nearme;

        import android.app.ProgressDialog;
        import android.graphics.Color;
        import android.os.AsyncTask;
        import android.os.Bundle;
        import android.support.design.widget.Snackbar;
        import android.support.v7.app.AppCompatActivity;
        import android.util.Log;

        import android.content.Intent;
        import android.view.View;
        import android.widget.Button;
        import android.widget.EditText;
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

        import butterknife.ButterKnife;
        import butterknife.InjectView;

public class LoginActivity extends AppCompatActivity
        implements ConnectivityReceiver.ConnectivityReceiverListener {
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;

    @InjectView(R.id.input_email) EditText _emailText;
    @InjectView(R.id.input_password) EditText _passwordText;
    @InjectView(R.id.btn_login) Button _loginButton;
    @InjectView(R.id.link_signup) TextView _signupLink;

    JSONParser jsonParser = new JSONParser();
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_USER = "user";
    private static final String TAG_EMAIL = "email";
    private static final String TAG_NOME = "name";
    private static final String TAG_COGNOME = "surname";
    private static final String TAG_GROUP = "id_group";
    private static final String TAG_PUB = "id_pub";


    String nome, cognome, email, id_group, id_pub ;

    // events JSONArray
    JSONArray user = null;
    private ProgressDialog progressDialog;
    private static String url_login_user="http://toponconcert.altervista.org/api.toponconcert.info/login_user.php";
//    private static String url_login_user="http://192.168.43.67/api.toponconcert.info/login_user.php";
    //private static String url_login_user="http://192.168.0.100/api.toponconcert.info/login_user.php";
    private static String url_get_user="http://toponconcert.altervista.org/api.toponconcert.info/get_user.php";
//    private static String url_get_user="http://192.168.43.67/api.toponconcert.info/get_user.php";
    //private static String url_login_user="http://192.168.0.100/api.toponconcert.info/login_user.php";

    SessionManager session;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.inject(this);

        // Session Manager
        session = new SessionManager(getApplicationContext());

        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });

        _signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
            }
        });


        checkConnection();

    }

    //Controllo connessione del dispositivo.
    private void checkConnection() {
        boolean isConnected = ConnectivityReceiver.isConnected();
        showSnack(isConnected);
    }

    private void showSnack(boolean isConnected) {
        String message;
        int color;
        if (isConnected) {
            message = "Connesso a internet!";
            color = Color.WHITE;
        } else {
            message = "Connessione assente!";
            color = Color.RED;
        }

        Snackbar snackbar = Snackbar
                .make(findViewById(R.id.fab), message, Snackbar.LENGTH_LONG);

        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(color);
        snackbar.show();
    }
    @Override
    protected void onResume() {
        super.onResume();

        // register connection status listener
        MyApplication.getInstance().setConnectivityListener(LoginActivity.this);
    }
    /**
     * Callback will be triggered when there is change in
     * network connection
     */
    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showSnack(isConnected);
    }


    //Gestione Login
    public void login() {
        Log.d(TAG, "Login");


        progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Autenticazione...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();


        // TODO: Implement your own authentication logic here.
        new LoginActivity.LoginUser().execute();

    }


    @Override
    public void onBackPressed() {
        // disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess() {
        _loginButton.setEnabled(true);
        progressDialog.dismiss();
        email = _emailText.getText().toString();

        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("email", email));

        JSONObject json = jsonParser.makeHttpRequest(url_get_user,"POST", params);
        // Check your log cat for JSON reponse
        Log.d("User: ", json.toString());
        try {
            // Checking for SUCCESS TAG
            int success = json.getInt(TAG_SUCCESS);

            Log.d("Success: ", success + "");
            if (success == 1) {
                // event found
                // Getting Array of events
                user = json.getJSONArray(TAG_USER);

                Log.d(TAG, "User mail session: " + user);

                // looping through All events
                for (int i = 0; i < user.length(); i++) {
                    JSONObject c = user.getJSONObject(i);

                    // Storing each json item in variable
                    nome = c.getString(TAG_NOME);
                    cognome = c.getString(TAG_COGNOME);
                    email = c.getString(TAG_EMAIL);
                    if(c.isNull(TAG_GROUP)){
                        id_group = null;
                    } else {
                        id_group = c.getString(TAG_GROUP);
                    }
                    if(c.isNull(TAG_PUB)){
                        id_pub = null;
                    } else {
                        id_pub = c.getString(TAG_PUB);
                    }

                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }
        session.createLoginSession(nome, cognome, email, id_group, id_pub );
        Intent i = new Intent (this, MainActivity.class);
        startActivity(i);
    }

    public void onLoginFailed() {

        progressDialog.dismiss();
        finish();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(LoginActivity.this, "Credenziali non valide.", Toast.LENGTH_LONG).show();
            }
        });
        startActivity(getIntent());
    }


    class LoginUser extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        /**
         * Login user
         * */
        protected String doInBackground(String... args) {
            String email = _emailText.getText().toString();
            String password = _passwordText.getText().toString();


            // Building Parameters
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("email", email));
            params.add(new BasicNameValuePair("password", password));

            Log.d(TAG, "Login Email: " + email);
            Log.d(TAG, "Login Password: " + password);

            // getting JSON Object
            // Note that create user url accepts POST method
            JSONObject json = jsonParser.makeHttpRequest(url_login_user,"POST", params);

            // check log cat fro response
            Log.d("Login User", json.toString());

            // check for success tag

            try {
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // successfully logged user
                    onLoginSuccess();
                } else {
                    //login failed
                    onLoginFailed();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once done
        }

    }

}