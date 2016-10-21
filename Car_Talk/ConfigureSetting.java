package com.yangproject.embeddedproject.Others;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by 우민 on 2016-05-26.
 */
public class ConfigureSetting {
    private static ConfigureSetting ourInstance = new ConfigureSetting();
    public static ConfigureSetting getInstance() { return ourInstance; }
    private JSONObject configObject = new JSONObject();
    private ConfigureSetting() {
        try {
            configObject.put("rotate_num", 5);
            configObject.put("brightness", 7);
            configObject.put("inversion", false);
            configObject.put("speed", 2);
            configObject.put("id", "");
            configObject.put("message", "");
        }
        catch (JSONException jexc) { }
    }
    public void setConfigure(String name, int value) {
        try {
            configObject.remove(name);
            configObject.put(name, value);
        }
        catch (Exception exc) { }
    }
    public void setConfigure(String name, boolean value) {
        try {
            configObject.remove(name);
            configObject.put(name, value);
        }
        catch (Exception exc) { }
    }
    public void setConfigure(String name, String value) {
        try {
            configObject.remove(name);
            configObject.put(name, value);
        }
        catch (Exception exc) { }
    }
    public JSONObject getConfigObject() { return configObject; }
    public int getConfigureInt(String name) {
        int value = 7;
        try {
            value = configObject.getInt(name);
        }
        catch (Exception exc) { }
        return value;
    }
    public boolean getConfigureBoolean(String name) {
        boolean value = false;
        try {
            value = configObject.getBoolean(name);
        }
        catch (Exception exc) { }
        return value;
    }
    public String getConfigureString(String name) {
        String value = " ";
        try {
            value = configObject.getString(name);
        }
        catch (Exception exc) { }
        return value;
    }
}
