package com.example.gabriel.ondeestacionei;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Gabriel on 06/03/2017.
 */

public class StorageProvider {
    private Context context;
    private SharedPreferences sharedPreferences;

    public StorageProvider(Context context) {
        this.context = context;
        this.sharedPreferences = context.getSharedPreferences(
                context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
    }

    public boolean isEmpty(){

        if (getLongitude() == 0f || getLatitude() == 0f) {
            return true;
        }

        return false;
    }

    public void eraseStorage(){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        setLatitude(0f);
        setLongitude(0f);
    }

    public void setLatitude(Float latitude){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat(context.getString(R.string.latitude), latitude);
        editor.apply();
    }

    public Float getLatitude(){
        Float latitude = sharedPreferences.getFloat(context.getString(R.string.latitude), 0f);

        return latitude;
    }

    public void setLongitude(Float longitude) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat(context.getString(R.string.longitude), longitude);
        editor.apply();
    }

    public Float getLongitude(){
        Float longitude = sharedPreferences.getFloat(context.getString(R.string.longitude), 0f);

        return longitude;
    }
}
