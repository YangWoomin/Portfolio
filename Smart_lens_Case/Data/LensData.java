package com.yangproject.iot.Data;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by 우민 on 2016-06-05.
 */
public class LensData {
    private Context context;
    public LensData(Context context) { this.context = context; }

    public String getData(String name, String key) {
        SharedPreferences pref = context.getSharedPreferences(name, context.MODE_PRIVATE);
        return pref.getString(key, null);
    }

    public void putData(String name, String key, String value) {
        SharedPreferences pref = context.getSharedPreferences(name, context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public void delData(String name, String key) {
        SharedPreferences pref = context.getSharedPreferences(name, context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.remove(key);
        editor.commit();
    }
}
