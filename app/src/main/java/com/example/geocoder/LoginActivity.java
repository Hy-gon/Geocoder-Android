package com.example.geocoder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    // INITIALISATION DES XML ET ID
    public static final int RequestPermissionCode = 1;
    private static String USERNAME = "";
    private static String PASSWORD = "";
    private EditText FirstNameText;
    private EditText LastNameText;
    private EditText PhoneText;
    private Button ConfirmButton;
    private static final String TAG = "Helper";


    // PERMET D'INTERAGIR AVEC LE FICHIER CONFIG
    public static String getConfigValue(Context context, String name) {
        Resources resources = context.getResources();

        try {
            InputStream rawResource = resources.openRawResource(R.raw.config);
            Properties properties = new Properties();
            properties.load(rawResource);
            return properties.getProperty(name);
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Unable to find the config file: " + e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, "Failed to open config file.");
        }

        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // INITIALISATION DES CHAMP XML
        FirstNameText = (EditText) findViewById(R.id.FirstNameText);
        FirstNameText.setOnClickListener(this);
        LastNameText = (EditText) findViewById(R.id.LastNameText);
        LastNameText.setOnClickListener(this);
        PhoneText = (EditText) findViewById(R.id.PhoneText);
        PhoneText.setOnClickListener(this);
        ConfirmButton = (Button) findViewById(R.id.ConfirmButton);
        ConfirmButton.setOnClickListener(this);

        USERNAME = getConfigValue(this, "api_id");
        PASSWORD = getConfigValue(this, "api_password");

        // ENVOIE DE PERMISSION
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED)
        {
            requestPermission();
            return;
        }
    }

    @Override
    public void onClick(View v)
    {
        RequestQueue queue = Volley.newRequestQueue(this);
        final String Name = FirstNameText.getText().toString();
        final String LastName = LastNameText.getText().toString();
        final String Phone = PhoneText.getText().toString();
        String url = "https://webservicesmultimedias.clever-is.fr/api/contact/localisation?name=" + Name + "&lastName=" + LastName + "&gsmPrimary=" + Phone;

        switch (v.getId())
        {
            // VERIFICATIONS SI VIDE ET SI CORRECT PENDANT L'ENVOI
            case R.id.ConfirmButton:
                if (!(Name.matches("") || LastName.matches("") || Phone.matches("")))
                {
                    if (Phone.length() != 10 || !(isNumeric(Phone)))
                    {
                        Toast.makeText(this, R.string.téléphone_erreur, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    // VERIFICATION SI LA PERSONNE EXISTE
                    StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // ENVOI D'INFORMATION VERS LA NOUVELLE ACTIVITE
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.putExtra("EXTRA_NAME", Name);
                            intent.putExtra("EXTRA_LASTNAME", LastName);
                            intent.putExtra("EXTRA_PHONE", Phone);
                            LoginActivity.this.startActivity(intent);
                        }
                    }, new Response.ErrorListener(){
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(LoginActivity.this, R.string.erreur_login, Toast.LENGTH_SHORT).show();
                        }
                    }
                    ) {
                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            Map<String, String> headers = new HashMap<>();
                            String credentials = USERNAME + ":" + PASSWORD;
                            String auth = "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                            headers.put("Content-Type", "application/json");
                            headers.put("Authorization", auth);
                            return headers;
                        }
                    };
                    // ENVOIE DE REQUETE
                    queue.add(stringRequest);
                }
                else
                    Toast.makeText(this, R.string.completez_tout_les_champs, Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }
    // CHECK SI C'EST NUMERIC
    public static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            double d = Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    // FONCTION DE PERMISSIONS
    private void requestPermission()
    {
        ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, RequestPermissionCode);
        ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_COARSE_LOCATION}, RequestPermissionCode);
        ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.INTERNET}, RequestPermissionCode);
    }
}
