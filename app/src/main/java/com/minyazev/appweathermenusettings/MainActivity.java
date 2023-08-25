package com.minyazev.appweathermenusettings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.minyazev.appweathermenusettings.utils.NetworkUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, SharedPreferences.OnSharedPreferenceChangeListener {

    private Button searchButton;
    private EditText searchField;
    private TextView cityName;
    public static final int SUCCESS = 1;
    public static final int ERROR = 2;
    public static final String TAG = "AppWeatherMenuSettings";

    private Handler mHandler = new Handler(Looper.getMainLooper()){

        @Override
        public void handleMessage( Message msg) {
            if(msg.what == SUCCESS){
                String responseData = (String)msg.obj;
                parseData(responseData);
            } else{
                if(msg.what == ERROR){
                    Log.e(TAG, "handleMessage: Ошибка при получении данных" );
                }
            }

        }
    };

    private static boolean showPressure = true;
    private static boolean showTemperature = true;
    private static String color = "red";

    private void setupSharedPreferences(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        showPressure = sharedPreferences.getBoolean(getString(R.string.show_pressure_settings_key), true);
        showTemperature=sharedPreferences.getBoolean(getString(R.string.show_temp_settings_key), true);
        color = sharedPreferences.getString(getString(R.string.pref_color_key), getString(R.string.pref_color_red_value));

        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(key.equals(getString(R.string.show_pressure_settings_key))){
            showPressure = sharedPreferences.getBoolean(getString(R.string.show_pressure_settings_key),true);

        } else if(key.equals(getString(R.string.show_temp_settings_key))){
            showTemperature = sharedPreferences.getBoolean(getString(R.string.show_temp_settings_key),true);
        } else if(key.equals(getString(R.string.pref_color_key))){
            color = sharedPreferences.getString(getString(R.string.pref_color_key), getString(R.string.pref_color_red_value));
        }
    }

    private void parseData(String output){

        try {

            JSONObject resultJSON=new JSONObject(output);
            JSONObject weather=resultJSON.getJSONObject("main");
            JSONObject sys=resultJSON.getJSONObject("sys");

            TextView temp=findViewById(R.id.tempValue);

            String temp_K=weather.getString("temp");

            float temp_C=Float.parseFloat(temp_K);
            temp_C=temp_C-(float)273.15;
            String temp_C_string=Float.toString(temp_C);

            if(showTemperature) {
                temp.setTextColor(Color.parseColor(color));
                temp.setText(temp_C_string);
            } else{
                temp.setTextColor(Color.parseColor(color));
                temp.setText("");
            }

            TextView pressure=findViewById(R.id.pressureValue);
            if(showPressure) {
                pressure.setTextColor(Color.parseColor(color));
                pressure.setText(weather.getString("pressure"));
            }
            else {
                pressure.setText("");
            }

            TextView sunrise=findViewById(R.id.timeSunrise);
            String timeSunrise=sys.getString("sunrise");
            Locale myLocale=new Locale ("ru","RU");
            SimpleDateFormat formatter=new SimpleDateFormat("HH:mm:ss", myLocale);

            String dateString=formatter.format(new Date(Long.parseLong(timeSunrise)*1000+(60*60*1000)*3));

            sunrise.setTextColor(Color.parseColor(color));
            sunrise.setText(dateString);

            TextView sunset=findViewById(R.id.timeSunset);
            String timeSunset=sys.getString("sunset");

            dateString=formatter.format(new Date(Long.parseLong(timeSunset)*1000+(60*60*1000)*3));

            sunset.setTextColor(Color.parseColor(color));
            sunset.setText(dateString);

        } catch (JSONException e){
            e.printStackTrace();
        }

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        searchButton = findViewById(R.id.buttonSearch);
        searchField = findViewById(R.id.searchField);
        cityName = findViewById(R.id.cityName);
        searchButton.setOnClickListener(this);
        setupSharedPreferences();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //return super.onCreateOptionsMenu(menu);
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.settings_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //return super.onOptionsItemSelected(item);
        int id = item.getItemId();
        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(intent);
        return true;
    }

    @Override
    public void onClick(View view) {
        String city = searchField.getText().toString();
        // https://api.openweathermap.org/data/2.5/weather?q=Kazan&appid=bc3c769785e409da05b5fca6ab83f12b
        cityName.setTextColor(Color.parseColor(color));
        cityName.setText(city);
        URL url = NetworkUtils.buildUrlOpenWeatherMap(city);
        DataFetcherThread thread = new DataFetcherThread(mHandler, url);
        thread.start();

    }
}