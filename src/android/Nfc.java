package com.dnrps.nfc;

import org.apache.cordova.*;
import org.json.JSONArray;
import org.json.JSONException;

public class Nfc extends CordovaPlugin {
    private final String ACTION_INIT = "init";

    private String password;

    @Override
    public boolean execute(String action, JSONArray data, CallbackContext callbackContext) throws JSONException {

        if (action.equals("greet")) {
            String name = data.getString(0);
            String message = "Hello, " + name;
            callbackContext.success(message);
        }
        else if (action.equals(ACTION_INIT)){
            init(callbackContext);
        }
        else {

            return false;

        }

        return true;
    }

    private void init(CallbackContext callbackContext){
        password = "1234";

        callbackContext.success();
    }
}