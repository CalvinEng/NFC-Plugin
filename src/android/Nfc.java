package com.dnrps.nfc;

import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;

import com.nxp.nfclib.ntag.NTag213215216;
import com.nxp.nfcliblite.Interface.NxpNfcLibLite;
import com.nxp.nfcliblite.Interface.Nxpnfcliblitecallback;

import org.apache.cordova.*;
import org.json.JSONArray;
import org.json.JSONException;

public class Nfc extends CordovaPlugin {
    private final String ACTION_INIT = "init";

    private static String TAG = "NfcPlugin";

    private String password;
    private Tag tagInfo;

    /** Create lib lite instance. */
    private NxpNfcLibLite libInstance = null;

    @Override
    public void pluginInitialize() {
        super.pluginInitialize();

        libInstance = NxpNfcLibLite.getInstance();
        // Call registerActivity function before using other functions of the library.
        libInstance.registerActivity(cordova.getActivity());

        System.out.println("Plugin Initialized.");
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        
        System.out.println("onNewIntent Intent: " + intent.toString());
        System.out.println("onNewIntent Action: " + intent.getAction());

        tagInfo = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

        // Only act on intents from a tag
        if (tagInfo == null) {
            return;
        }

        System.out.println("Tag info: " + tagInfo.toString());

        Nxpnfcliblitecallback callback = new Nxpnfcliblitecallback() {
            @Override
            public void onNTag213215216CardDetected(NTag213215216 nTag213215216) {

            }
        };

        libInstance.filterIntent(intent, callback);
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

        if (NxpNfcLibLite.getInstance() != null) {
            System.out.println("Starting startForeGroundDispatch in init");
            libInstance.startForeGroundDispatch();
        }

        callbackContext.success();
    }
}