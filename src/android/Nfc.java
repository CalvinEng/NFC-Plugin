package com.dnrps.nfc;

import com.nxp.nfcliblite.Interface.NxpNfcLibLite;

import org.apache.cordova.*;
import org.json.JSONArray;
import org.json.JSONException;

public class Nfc extends CordovaPlugin {
    private final String ACTION_INIT = "init";

    private String password;

    /** Create lib lite instance. */
    private NxpNfcLibLite libInstance = null;

    @Override
    public void pluginInitialize() {
        super.pluginInitialize();

        
        libInstance = NxpNfcLibLite.getInstance();
        // Call registerActivity function before using other functions of the library.
        libInstance.registerActivity(cordova.getActivity());

        //webView.loadUrl("javascript:console.log('Initialize Plugin');");
    }

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