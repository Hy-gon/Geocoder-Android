package com.example.geocoder;

import android.Manifest;
import android.app.Activity;
import android.app.Service;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.text.format.DateFormat;
import android.util.Base64;
import android.util.Log;
import java.util.ArrayList;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    // INITIALISATION DES VARIABLES GLOBALES
    private static final int REQUEST_CODE_EXAMPLE = 0x9988;
    public static final int RequestPermissionCode = 1;
    private static final String USERNAME = "mickael_clever";
    private static final String PASSWORD = "M12345678l";
    private String NAME;
    private String LASTNAME;
    private String PHONE;
    ArrayList<String> TIMETAB = new ArrayList<>();
    private Button ferie;
    private Button monday1;
    private Button monday2;
    private Button tuesday1;
    private Button tuesday2;
    private Button wednesday1;
    private Button wednesday2;
    private Button thursday1;
    private Button thursday2;
    private Button friday1;
    private Button friday2;
    private Button saturday1;
    private Button saturday2;
    private Button sunday1;
    private Button sunday2;
    private Button buttonWeekDay;
    private Button buttonWeekDay2;
    private Button confirmButton;
    private Switch switchWe;
    private RequestQueue queue;
    String testValue = "test";
    Calendar calendar = Calendar.getInstance();
    boolean doubleBackToExitPressedOnce = false;
    SimpleDateFormat formatter = new SimpleDateFormat("dd/M/yyyy");

    final Handler handler = new Handler();

    private final Runnable runnableCode = new Runnable() {
        @Override
        public void run() {
            LaunchRequest();

            handler.postDelayed(runnableCode, 30000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d("testArray ", "" + TIMETAB);
        // RECUPERATION DES RESSOURCES XML ET UTILISATION DANS LE OnClick()
        buttonWeekDay = (Button) findViewById(R.id.buttonWeekDay);
        buttonWeekDay.setOnClickListener(this);
        buttonWeekDay2 = (Button) findViewById(R.id.buttonWeekDay2);
        buttonWeekDay2.setOnClickListener(this);
        confirmButton = (Button) findViewById(R.id.buttonConfirm);
        confirmButton.setOnClickListener(this);
        ferie = (Button) findViewById(R.id.ferie);
        ferie.setOnClickListener(this);
        monday1 = (Button) findViewById(R.id.buttonMonday1);
        monday1.setOnClickListener(this);
        monday2 = (Button) findViewById(R.id.buttonMonday2);
        monday2.setOnClickListener(this);
        tuesday1 = (Button) findViewById(R.id.buttonTuesday1);
        tuesday1.setOnClickListener(this);
        tuesday2 = (Button) findViewById(R.id.buttonTuesday2);
        tuesday2.setOnClickListener(this);
        wednesday1 = (Button) findViewById(R.id.buttonWednesday1);
        wednesday1.setOnClickListener(this);
        wednesday2 = (Button) findViewById(R.id.buttonWednesday2);
        wednesday2.setOnClickListener(this);
        thursday1 = (Button) findViewById(R.id.buttonThursday1);
        thursday1.setOnClickListener(this);
        thursday2 = (Button) findViewById(R.id.buttonThursday2);
        thursday2.setOnClickListener(this);
        friday1 = (Button) findViewById(R.id.buttonFriday1);
        friday1.setOnClickListener(this);
        friday2 = (Button) findViewById(R.id.buttonFriday2);
        friday2.setOnClickListener(this);
        saturday1 = (Button) findViewById(R.id.buttonSaturday1);
        saturday1.setOnClickListener(this);
        saturday2 = (Button) findViewById(R.id.buttonSaturday2);
        saturday2.setOnClickListener(this);
        sunday1 = (Button) findViewById(R.id.buttonSunday1);
        sunday1.setOnClickListener(this);
        sunday2 = (Button) findViewById(R.id.buttonSunday2);
        sunday2.setOnClickListener(this);
        switchWe = (Switch) findViewById(R.id.switchWe);

        // RECUPERATION DES INFORMATIONS LOGIN
        NAME = getIntent().getStringExtra("EXTRA_NAME");
        LASTNAME = getIntent().getStringExtra("EXTRA_LASTNAME");
        PHONE = getIntent().getStringExtra("EXTRA_PHONE");

        // CHARGEMENT DES SAUVEGARDES


        loadSetting();

        Log.d("AVANT : ", "OUI");

        handler.post(runnableCode);

        Log.d("APRES : ", "OUI");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_EXAMPLE);
        {
            if (resultCode == Activity.RESULT_OK)
            {
                TIMETAB = data.getStringArrayListExtra(CalendarActivity.EXTRA_DATA);

               doSaveTimeTab(TIMETAB);
            }
        }

    }

    private void LaunchRequest()
    {
        // CREATION DE LA QUEUE REQUETE HTTP
        final LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (queue == null)
        {
            queue = Volley.newRequestQueue(this);
        }

        ArrayList<LocationProvider> providers = new ArrayList<LocationProvider>();
        ArrayList<String> names = (ArrayList<String>) locationManager.getProviders(true);



        // CONFIGURATION GPS
        Criteria critere = new Criteria();
        critere.setAccuracy(Criteria.ACCURACY_FINE);
        critere.setAltitudeRequired(false);
        critere.setBearingRequired(false);
        critere.setCostAllowed(false);
        critere.setPowerRequirement(Criteria.POWER_HIGH);
        critere.setSpeedRequired(false);

        // LANCEMENT DES PERMISSIONS
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    Activity#requestPermissions
            requestPermission();
            return;
        }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 30000, 0, new LocationListener() {
                public void onLocationChanged(final Location location) {
                    // INITIALISATION DES VALEURS POUR LA REQUETE
                    String url = "https://webservicesmultimedias.clever-is.fr/api/contacts/localisations?name=" + NAME + "&lastName=" + LASTNAME + "&gsmPrimary=" + PHONE;
                    String longitude = String.format("%.4f", location.getLongitude());
                    String latitude = String.format("%.4f", location.getLatitude());
                    final String GpsCoord = "{\n" +
                            "\t\"datas\": {\n" +
                            "\t\t\"latitude\": \"" + longitude + "\",\n" +
                            "\t\t\"longitude\": \"" + latitude + "\"\n" +
                            "\t}\n" +
                            "}";

                    // CREATION DE LA REQUETE
                    final StringRequest postRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // response
                            Log.d("Response", response);
                            Log.d("TESTING IN : ", testValue);

                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // error
                            Log.d("Error.Response", error.toString());
                            queue.cancelAll(new RequestQueue.RequestFilter() {
                                @Override
                                public boolean apply(Request<?> request) {
                                    return true;
                                }
                            });
                        }
                    }
                    ) {
                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            Map<String, String> headers = new HashMap<>();
                            String credentials = USERNAME + ":" + PASSWORD;
                            String auth = "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                            headers.put("Accept", "application/json");
                            headers.put("Content-Type", "application/json");
                            headers.put("Authorization", auth);
                            return headers;
                        }


                        @Override
                        public String getBodyContentType() {
                            return "application/json; charset=utf-8";
                        }

                        @Override
                        public byte[] getBody() throws AuthFailureError {
                            Log.e("Main", GpsCoord);
                            return GpsCoord.getBytes();
                        }
                    };

                    Calendar calendar = Calendar.getInstance();
                    int timestamp_Current = 60 * calendar.get(Calendar.HOUR_OF_DAY) + calendar.get(Calendar.MINUTE);
                    int TodayDay = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
                    switchWe = (Switch) findViewById(R.id.switchWe);

                    Date date = new Date();
                        loadSettingTimeTab();


                   Log.d("FIRST", "IN");
                   Log.d("TodayDay", " " + TodayDay);
                   Log.d("TIMETAB value : ", " " + TIMETAB);
                   Log.d("DATE : ", formatter.format(date));

                    if (!(TIMETAB.contains(formatter.format(date)))) {
                            switch (TodayDay) {
                                case Calendar.MONDAY:
                                    monday1 = (Button) findViewById(R.id.buttonMonday1);
                                    monday2 = (Button) findViewById(R.id.buttonMonday2);
                                    int timestamp_monday_1 = 60 * getHours(monday1.getText().toString()) + getMinutes(monday1.getText().toString());
                                    int timestamp_monday_2 = 60 * getHours(monday2.getText().toString()) + getMinutes(monday2.getText().toString());

                                    if (timestamp_Current >= timestamp_monday_1 && timestamp_Current <= timestamp_monday_2) {
                                        queue.add(postRequest);
                                    }
                                    break;

                                case Calendar.TUESDAY:
                                    tuesday1 = (Button) findViewById(R.id.buttonTuesday1);
                                    tuesday2 = (Button) findViewById(R.id.buttonTuesday2);
                                    int timestamp_tuesday_1 = 60 * getHours(tuesday1.getText().toString()) + getMinutes(tuesday1.getText().toString());
                                    int timestamp_tuesday_2 = 60 * getHours(tuesday2.getText().toString()) + getMinutes(tuesday2.getText().toString());

                                    if (timestamp_Current >= timestamp_tuesday_1 && timestamp_Current <= timestamp_tuesday_2) {
                                        queue.add(postRequest);
                                    }
                                    break;

                                case Calendar.WEDNESDAY:
                                    wednesday1 = (Button) findViewById(R.id.buttonWednesday1);
                                    wednesday2 = (Button) findViewById(R.id.buttonWednesday2);
                                    int timestamp_wednesday_1 = 60 * getHours(wednesday1.getText().toString()) + getMinutes(wednesday1.getText().toString());
                                    int timestamp_wednesday_2 = 60 * getHours(wednesday2.getText().toString()) + getMinutes(wednesday2.getText().toString());

                                    if (timestamp_Current >= timestamp_wednesday_1 && timestamp_Current <= timestamp_wednesday_2) {
                                        queue.add(postRequest);
                                    }
                                    break;

                                case Calendar.THURSDAY:
                                    int timestamp_thursday_1 = 60 * getHours(thursday1.getText().toString()) + getMinutes(thursday1.getText().toString());
                                    int timestamp_thursday_2 = 60 * getHours(thursday2.getText().toString()) + getMinutes(thursday2.getText().toString());
                                    thursday1 = (Button) findViewById(R.id.buttonThursday1);
                                    thursday2 = (Button) findViewById(R.id.buttonThursday2);

                                    if (timestamp_Current >= timestamp_thursday_1 && timestamp_Current <= timestamp_thursday_2) {
                                        queue.add(postRequest);
                                    }
                                    break;

                                case Calendar.FRIDAY:

                                    friday1 = (Button) findViewById(R.id.buttonFriday1);
                                    friday2 = (Button) findViewById(R.id.buttonFriday2);
                                    int timestamp_friday_1 = 60 * getHours(friday1.getText().toString()) + getMinutes(friday1.getText().toString());
                                    int timestamp_friday_2 = 60 * getHours(friday2.getText().toString()) + getMinutes(friday2.getText().toString());
                                    if (timestamp_Current >= timestamp_friday_1 && timestamp_Current <= timestamp_friday_2) {
                                        queue.add(postRequest);
                                    }
                                    break;

                                case Calendar.SATURDAY:
                                    int timestamp_saturday_1 = 60 * getHours(saturday1.getText().toString()) + getMinutes(saturday1.getText().toString());
                                    int timestamp_saturday_2 = 60 * getHours(saturday2.getText().toString()) + getMinutes(saturday2.getText().toString());
                                    saturday1 = (Button) findViewById(R.id.buttonSaturday1);
                                    saturday2 = (Button) findViewById(R.id.buttonSaturday2);

                                    if (timestamp_Current >= timestamp_saturday_1 && timestamp_Current <= timestamp_saturday_2 && switchWe.isChecked()) {
                                        queue.add(postRequest);
                                    }
                                    break;

                                case Calendar.SUNDAY:
                                    int timestamp_sunday_1 = 60 * getHours(sunday1.getText().toString()) + getMinutes(sunday1.getText().toString());
                                    int timestamp_sunday_2 = 60 * getHours(sunday2.getText().toString()) + getMinutes(sunday2.getText().toString());
                                    sunday1 = (Button) findViewById(R.id.buttonSunday1);
                                    sunday2 = (Button) findViewById(R.id.buttonSunday2);

                                    if (timestamp_Current >= timestamp_sunday_1 && timestamp_Current <= timestamp_sunday_2 && switchWe.isChecked()) {
                                        queue.add(postRequest);
                                    }
                                    break;
                                default:
                                    break;
                            }
                            locationManager.removeUpdates(this); // EVITE LES DOUBLONS !!!!!!
                            queue.getCache().clear();
                    }
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {
                }

                @Override
                public void onProviderEnabled(String provider) {
                }

                @Override
                public void onProviderDisabled(String provider) {
                }
            });
            for (String name : names)
                providers.add(locationManager.getProvider(name));
    }

    // ACTIVATION PERMISSIONS GPS
    private void requestPermission() {
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, RequestPermissionCode);
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, RequestPermissionCode);
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.INTERNET}, RequestPermissionCode);
    }

    // ACTION SI BOUTTON CLICK
    @Override
    public void onClick(View v) {
        Calendar calendar = Calendar.getInstance();
        final int hour = calendar.get(Calendar.HOUR_OF_DAY);
        final int minute = calendar.get(Calendar.MINUTE);

        switch (v.getId()) {
            case R.id.buttonConfirm:
                doSave(v);
                break;

            case R.id.ferie:
                Intent intent = new Intent(MainActivity.this, CalendarActivity.class);
                startActivityForResult(intent, REQUEST_CODE_EXAMPLE);
                break;

            case R.id.buttonWeekDay:
                Button[] buttonArrayMorning = new Button[]
                        {
                                monday1, tuesday1, wednesday1, thursday1, friday1
                        };
                dayTimePickerWeekDay(hour, minute, this, buttonArrayMorning);
                break;

            case R.id.buttonWeekDay2:
                Button[] buttonArrayAfternoon = new Button[]
                        {
                                monday2, tuesday2, wednesday2, thursday2, friday2
                        };
                dayTimePickerWeekDay(hour, minute, this, buttonArrayAfternoon);
                break;

            case R.id.buttonMonday1:
                dayTimePicker(hour, minute, this, monday1);
                break;

            case R.id.buttonMonday2:
                dayTimePicker(hour, minute, this, monday2);
                break;

            case R.id.buttonTuesday1:
                dayTimePicker(hour, minute, this, tuesday1);
                break;

            case R.id.buttonTuesday2:
                dayTimePicker(hour, minute, this, tuesday2);
                break;

            case R.id.buttonWednesday1:
                dayTimePicker(hour, minute, this, wednesday1);
                break;

            case R.id.buttonWednesday2:
                dayTimePicker(hour, minute, this, wednesday2);
                break;

            case R.id.buttonThursday1:
                dayTimePicker(hour, minute, this, thursday1);
                break;

            case R.id.buttonThursday2:
                dayTimePicker(hour, minute, this, thursday2);
                break;

            case R.id.buttonFriday1:
                dayTimePicker(hour, minute, this, friday1);
                break;

            case R.id.buttonFriday2:
                dayTimePicker(hour, minute, this, friday2);
                break;

            case R.id.buttonSaturday1:
                dayTimePicker(hour, minute, this, saturday1);
                break;

            case R.id.buttonSaturday2:
                dayTimePicker(hour, minute, this, saturday2);
                break;

            case R.id.buttonSunday1:
                dayTimePicker(hour, minute, this, sunday1);
                break;

            case R.id.buttonSunday2:
                dayTimePicker(hour, minute, this, sunday2);
                break;

            default:
                break;
        }
    }

    // DIALOG QUI S'OUVRE ET INSCRIT L'HEURE POUR CHAQUE JOUR DE LA SEMAINE ( matin, soir )
    public void dayTimePicker(int hour, int minute, Context mContext, final Button dayButton) {
        TimePickerDialog timePickerDialogMonday = new TimePickerDialog(this, android.R.style.Theme_Holo_Dialog, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                if (minute < 10) {
                    dayButton.setText(hourOfDay + "H0" + minute);
                    if (hourOfDay < 10)
                        dayButton.setText("0" + hourOfDay + "H0" + minute);
                } else if (hourOfDay < 10)
                    dayButton.setText("0" + hourOfDay + "H" + minute);
                else
                    dayButton.setText(hourOfDay + "H" + minute);
            }
        }, hour, minute, DateFormat.is24HourFormat(mContext));
        timePickerDialogMonday.show();
    }

    // IDEM POUR LES JOURS WEEK END
    public void dayTimePickerWeekDay(int hour, int minute, Context mContext, final Button[] buttonlist) {
        TimePickerDialog timePickerDialogMonday = new TimePickerDialog(this, android.R.style.Theme_Holo_Dialog, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                int cmp = 0;

                while (cmp != 5) {
                    if (minute < 10) {
                        buttonlist[cmp].setText(hourOfDay + "H0" + minute);
                        if (hourOfDay < 10)
                            buttonlist[cmp].setText("0" + hourOfDay + "H0" + minute);
                    } else if (hourOfDay < 10)
                        buttonlist[cmp].setText("0" + hourOfDay + "H" + minute);
                    else
                        buttonlist[cmp].setText(hourOfDay + "H" + minute);
                    cmp++;
                }
            }
        }, hour, minute, DateFormat.is24HourFormat(mContext));
        timePickerDialogMonday.show();
    }

    // SAUVEGARDE LES CHOIX DANS LA MEMOIRE INTERNE
    public void doSave(View view) {
        SharedPreferences sharedPreferences = this.getSharedPreferences("TimeDay", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("monday1", (String) monday1.getText().toString());
        editor.putString("monday2", (String) monday2.getText().toString());
        editor.putString("tuesday1", (String) tuesday1.getText().toString());
        editor.putString("tuesday2", (String) tuesday2.getText().toString());
        editor.putString("wednesday1", (String) wednesday1.getText().toString());
        editor.putString("wednesday2", (String) wednesday2.getText().toString());
        editor.putString("thursday1", (String) thursday1.getText().toString());
        editor.putString("thursday2", (String) thursday2.getText().toString());
        editor.putString("friday1", (String) friday1.getText().toString());
        editor.putString("friday2", (String) friday2.getText().toString());
        editor.putString("saturday1", (String) saturday1.getText().toString());
        editor.putString("saturday2", (String) saturday2.getText().toString());
        editor.putString("sunday1", (String) sunday1.getText().toString());
        editor.putString("sunday2", (String) sunday2.getText().toString());
        editor.putBoolean("switchState", switchWe.isChecked());

        editor.apply();
        Toast.makeText(this, R.string.horaire_sauvegardé, Toast.LENGTH_LONG).show();
    }

    // CHARGEMENT DES SAUVEGARDES INTERNES
    private void loadSetting() {
        SharedPreferences sharedPreferences = this.getSharedPreferences("TimeDay", Context.MODE_PRIVATE);

        String mondayMorning = sharedPreferences.getString("monday1", monday1.toString());
        String mondayAfternoon = sharedPreferences.getString("monday2", monday2.toString());
        String tuesdayMorning = sharedPreferences.getString("tuesday1", tuesday1.toString());
        String tuesdayAfternoon = sharedPreferences.getString("tuesday2", tuesday2.toString());
        String wednesdayMorning = sharedPreferences.getString("wednesday1", wednesday1.toString());
        String wednesdayAfternoon = sharedPreferences.getString("wednesday2", wednesday2.toString());
        String thursdayMorning = sharedPreferences.getString("thursday1", thursday1.toString());
        String thursdayAfternoon = sharedPreferences.getString("thursday2", thursday2.toString());
        String fridayMorning = sharedPreferences.getString("friday1", friday1.toString());
        String fridayAfternoon = sharedPreferences.getString("friday2", friday2.toString());
        String saturdayMorning = sharedPreferences.getString("saturday1", saturday1.toString());
        String saturdayAfternoon = sharedPreferences.getString("saturday2", saturday2.toString());
        String sundayMorning = sharedPreferences.getString("sunday1", sunday1.toString());
        String sundayAfternoon = sharedPreferences.getString("sunday2", sunday2.toString());
        Boolean switchState = sharedPreferences.getBoolean("switchState", switchWe.isChecked());

        if (sharedPreferences.contains("monday1"))
            this.monday1.setText(mondayMorning);
        if (sharedPreferences.contains("monday2"))
            this.monday2.setText(mondayAfternoon);
        if (sharedPreferences.contains("tuesday1"))
            this.tuesday1.setText(tuesdayMorning);
        if (sharedPreferences.contains("tuesday2"))
            this.tuesday2.setText(tuesdayAfternoon);
        if (sharedPreferences.contains("wednesday1"))
            this.wednesday1.setText(wednesdayMorning);
        if (sharedPreferences.contains("wednesday2"))
            this.wednesday2.setText(wednesdayAfternoon);
        if (sharedPreferences.contains("thursday1"))
            this.thursday1.setText(thursdayMorning);
        if (sharedPreferences.contains("thursday2"))
            this.thursday2.setText(thursdayAfternoon);
        if (sharedPreferences.contains("friday1"))
            this.friday1.setText(fridayMorning);
        if (sharedPreferences.contains("friday2"))
            this.friday2.setText(fridayAfternoon);
        if (sharedPreferences.contains("saturday1"))
            this.saturday1.setText(saturdayMorning);
        if (sharedPreferences.contains("saturday2"))
            this.saturday2.setText(saturdayAfternoon);
        if (sharedPreferences.contains("sunday1"))
            this.sunday1.setText(sundayMorning);
        if (sharedPreferences.contains("sunday2"))
            this.sunday2.setText(sundayAfternoon);
        if (sharedPreferences.contains("switchState"))
            this.switchWe.setChecked(switchState);
    }

    public void doSaveTimeTab(ArrayList<String> list)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.putString("TabTime", json);
        editor.apply();
    }

    public void loadSettingTimeTab()
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        Gson gson = new Gson();
        String json = prefs.getString("TabTime", "");
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        if (gson.fromJson(json, type) != null)
        {
            TIMETAB = gson.fromJson(json, type);
        }

      //  Log.d(" ARRAY TAB : ", TIMETAB.toString());
    }

    // CONVERSION HEURE STRING -> INT
    public int getHours(String time) {
        int nbr = 0;
        int result = 0;

        while (nbr != 2) {
            result = 10 * result + Character.getNumericValue(time.charAt(nbr));
            nbr++;
        }
        return result;
    }

    // CONVERSION MINUTE STRING -> INT
    public int getMinutes(String time) {
        int nbr = 3;
        int result = 0;

        while (nbr != 5) {
            result = 10 * result + Character.getNumericValue(time.charAt(nbr));
            nbr++;
        }
        return result;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void onBackPressed() {

        if (doubleBackToExitPressedOnce) {
            onDestroy();
            super.onBackPressed();
            setResult(Activity.RESULT_CANCELED);
            finish();
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, R.string.retour_confirmation, Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }
}
