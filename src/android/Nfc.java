package com.dnrps.com;
 
import org.apache.cordova.*;
import org.json.JSONArray;
import org.json.JSONException;

public class Nfc extends CordovaPlugin {
    public static final String ACTION_INIT = "init"; 

    @Override
    public boolean execute(String action, JSONArray data, CallbackContext callbackContext) throws JSONException {

        if (action.equals(ACTION_INIT)) {

            String name = data.getString(0);
            String message = "Hello, " + name;
            callbackContext.success(message);

            return true;

        } else {
            
            return false;

        }
    }
}