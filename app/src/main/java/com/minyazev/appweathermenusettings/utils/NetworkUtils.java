package com.minyazev.appweathermenusettings.utils;



import android.net.Uri;
import android.util.Log;

import java.net.MalformedURLException;
import java.net.URL;

public class NetworkUtils {


    public static URL buildUrlOpenWeatherMap (String city){

        String BASE_URL="https://api.openweathermap.org/data/2.5/weather";
        String PARAM_CITY="q";
        String PARAM_APPID="appid";
        String appid_value="bc3c769785e409da05b5fca6ab83f12b";

        Uri builtUri=Uri.parse(BASE_URL).buildUpon().appendQueryParameter(PARAM_CITY,city).appendQueryParameter(PARAM_APPID,appid_value).build();
        URL url = null;

        try {
            url=new URL (builtUri.toString());

        } catch (MalformedURLException e){
            e.printStackTrace();
        }
        //Log.d(TAG, "buildUrl: "+url);
        return url;

    }

}
