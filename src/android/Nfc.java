package com.dnrps.nfc;

import android.content.Intent;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.Tag;

import com.nxp.nfclib.exceptions.SmartCardException;
import com.nxp.nfclib.ntag.NTag213215216;
import com.nxp.nfcliblite.Interface.NxpNfcLibLite;
import com.nxp.nfcliblite.Interface.Nxpnfcliblitecallback;

import org.apache.cordova.*;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;

public class Nfc extends CordovaPlugin {
    private final String ACTION_INIT = "init";

    private String myCbkId;
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
    public void onDestroy() {
        super.onDestroy();
        System.out.println("onDestroy");
    }

    @Override
    public void onPause(boolean multitasking) {
        super.onPause(multitasking);
        // TODO: How do we activate background scans?
        //libInstance.stopForeGroundDispatch();
        System.out.println("onPause");
    }

    @Override
    public void onResume(boolean multitasking) {
        super.onResume(multitasking);
        libInstance.startForeGroundDispatch();
        System.out.println("onResume");
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        tagInfo = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

        // Only act on intents from a tag
        if (tagInfo == null) {
            return;
        }

        Nxpnfcliblitecallback callback = new Nxpnfcliblitecallback() {
            @Override
            public void onNTag213215216CardDetected(NTag213215216 nTag213215216) {
                readTag(nTag213215216);
            }
        };

        NxpNfcLibLite.getInstance().filterIntent(intent, callback);
    }

    protected void readTag(final NTag213215216 tag)
    {
        try{
            tag.connect();

            NdefMessage ndefMessage =  tag.readNDEF();

            String message = new String(ndefMessage.getRecords()[0].getPayload()).substring(3);
            
            System.out.println(message);
            
            PluginResult result = new PluginResult(PluginResult.Status.OK, message);

            result.setKeepCallback(true);
            this.webView.sendPluginResult(result, this.myCbkId);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (SmartCardException e) {
            e.printStackTrace();
        } catch (FormatException e) {
            e.printStackTrace();
        } finally {
            try {
                tag.close();
            } catch (IOException e) {
                System.out.println("IOException at close(): " + e.getMessage());
            }
        }
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
        myCbkId =  callbackContext.getCallbackId();

        password = "1234";

        if (NxpNfcLibLite.getInstance() != null) {
            libInstance.startForeGroundDispatch();
            callbackContext.success("OK");
        }
    }
}