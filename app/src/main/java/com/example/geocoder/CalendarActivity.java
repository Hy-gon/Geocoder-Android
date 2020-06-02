package com.example.geocoder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.CalendarContract;
import android.util.EventLog;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.events.calendar.views.EventsCalendar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimerTask;

public class CalendarActivity extends AppCompatActivity implements View.OnClickListener, EventsCalendar.Callback
{
    private Button ConfirmButton;
    private Button AddButton;
    private TextView DateView;
    private ListView listViewDate;
    ArrayList<String> TabDate = new ArrayList<String>();
    private ArrayAdapter arrayAdapter;
    public static final String EXTRA_DATA = "EXTRA_DATA";
    String dateOut = null;
    String[] windowArray = {"TEST1", "TEST2", "TEST3"};

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar_layout);


        ConfirmButton = (Button) findViewById(R.id.ConfirmButtonCalendar);
        ConfirmButton.setOnClickListener(this);
        DateView = (TextView) findViewById(R.id.DateView);
        AddButton = (Button) findViewById(R.id.buttonAdd);
        AddButton.setOnClickListener(this);
        DateView.setText(R.string.ajouter_journée);

        listViewDate = (ListView) findViewById(R.id.listviewDate);

        loadSetting();
        // CHECK COLOR DAY

        ArrayAdapter arrayAdapter = new ArrayAdapter<String>(this, R.layout.mytestview, TabDate);

        listViewDate.setAdapter(arrayAdapter);

        for (int i = 0; i < TabDate.size(); i++)
        {

            TabDate.indexOf(i);
        }


        Log.d("ARRAY : ", " " + TabDate);

        CalendarView calendarView = findViewById(R.id.calendarView);

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                String date = dayOfMonth + "/" + ( month + 1 ) + "/" + year;

                if (DateView != null)
                {
                    DateView.setText(date);
                }
                dateOut = date;
            }
        });
    }

    @Override
    public void onClick(View v)
    {
        CalendarView calendarView = findViewById(R.id.calendarView);

        switch (v.getId())
        {
            case R.id.ConfirmButtonCalendar:
                final Intent data = new Intent();

                doSave(TabDate);
                data.putExtra(EXTRA_DATA, TabDate);
                setResult(Activity.RESULT_OK, data);
                finish();
                break;
            case R.id.buttonAdd:
                if (TabDate.contains(dateOut))
                {
                    TabDate.remove(TabDate.indexOf(dateOut));
                    Toast.makeText(this, R.string.jour_supprimé, Toast.LENGTH_SHORT).show();
                   // Log.d("ARRAY ADD : ", TabDate.toString());
                    ArrayAdapter arrayAdapter = new ArrayAdapter<String>(this, R.layout.mytestview, TabDate);

                    listViewDate.setAdapter(arrayAdapter);
                    break;
                }
                if (dateOut != null)
                {
                    TabDate.add(dateOut);
                    Toast.makeText(this, R.string.jour_ajouté, Toast.LENGTH_SHORT).show();

                    ArrayAdapter arrayAdapter = new ArrayAdapter<String>(this, R.layout.mytestview, TabDate);

                    listViewDate.setAdapter(arrayAdapter);
                }
                //Log.d("ARRAY ADD : ", TabDate.toString());
                break;
            default:
                break;
        }
    }

    public void doSave(ArrayList<String> list)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.putString("TabTime", json);
        editor.apply();
    }

    public void loadSetting()
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        Gson gson = new Gson();
        String json = prefs.getString("TabTime", "");
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        if (gson.fromJson(json, type) != null)
        {
            TabDate = gson.fromJson(json, type);
        }

        //Log.d(" ARRAY TAB : ", TabDate.toString());
    }

    @Override
    public void onBackPressed() {
        setResult(Activity.RESULT_CANCELED);
        super.onBackPressed();
    }

    @Override
    public void onDayLongPressed(Calendar calendar) {

    }

    @Override
    public void onDaySelected(Calendar calendar) {

    }

    @Override
    public void onMonthChanged(Calendar calendar) {

    }
}

