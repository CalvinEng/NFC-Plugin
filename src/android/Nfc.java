package com.dnrps.nfc;

import android.content.Intent;
import android.nfc.FormatException;
import android.nfc.NdefMessage;

import org.apache.cordova.*;
import org.json.JSONArray;
import org.json.JSONException;

import com.nxp.nfclib.exceptions.SmartCardException;
import com.nxp.nfclib.ntag.NTag213215216;
import com.nxp.nfclib.utils.NxpLogUtils;
import com.nxp.nfcliblite.Interface.NxpNfcLibLite;
import com.nxp.nfcliblite.Interface.Nxpnfcliblitecallback;

import java.io.IOException;

public class Nfc extends CordovaPlugin {
    private final String ACTION_INIT = "init";

    private String password;

    /** Lite application Tag. */
    private static final String TAG = "SampleNxpNfcLibLite";
    private static final String LOGTAG = "NFCPlugin";

    /** Create lib lite instance. */
    private NxpNfcLibLite libInstance = null;

    @Override
    public void pluginInitialize() {
        super.pluginInitialize();
        // Get and set the lib Singleton instance
        NxpNfcLibLite.getInstance().registerActivity(cordova.getActivity());

        // The default for NfcLogUtils logging is off, turn it on
        NxpLogUtils.enableLog();
        NxpLogUtils.i(LOGTAG, "MIFARE Cordova plugin pluginInitialize");
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

    @Override
    protected void onPause() {
        libInstance.stopForeGroundDispatch();
        super.onPause();
    }

    @Override
    protected void onResume() {
        libInstance.startForeGroundDispatch();
        super.onResume();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        libInstance.filterIntent(intent, new Nxpnfcliblitecallback() {
            @Override
            public void onNTag213215216CardDetected(final NTag213215216 objnTag213215216) {
                readTag(objnTag213215216);
            }
        });

        super.onNewIntent(intent);
    }

    protected void readTag(final NTag213215216 tag)
    {
        try{
            tag.connect();

            NdefMessage ndefMessage =  tag.readNDEF();

            NxpLogUtils.i(LOGTAG, new String(ndefMessage.getRecords()[0].getPayload()).substring(3), 't');

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
                NxpLogUtils.v(TAG, "IOException at close(): " + e.getMessage());
            }
        }
    }

    private void init(CallbackContext callbackContext){
        if (NxpNfcLibLite.getInstance() != null) {
            NxpLogUtils.i(LOGTAG, "Starting startForeGroundDispatch in init");
            NxpNfcLibLite.getInstance().startForeGroundDispatch();
        } else {
            NxpLogUtils.w(LOGTAG, "NxpNfcLibLite.getInstance() == null");
        }

        password = "1234";

        callbackContext.success();
    }
}
