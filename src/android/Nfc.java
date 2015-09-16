package com.dnrps.nfc;

import android.content.Intent;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;

import com.nxp.nfclib.exceptions.SmartCardException;
import com.nxp.nfclib.ntag.NTag213215216;
import com.nxp.nfcliblite.Interface.NxpNfcLibLite;
import com.nxp.nfcliblite.Interface.Nxpnfcliblitecallback;

import org.apache.cordova.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Nfc extends CordovaPlugin {
    private final String ACTION_INIT = "init";
    private final String ACTION_WRITE = "write";

    private String init_Cbk_Id;
    private String write_Cbk_Id;
    private String currentAction;
    private String messageToWrite;
    private String password;
    private String currentUser;
    private Tag tagInfo;
    private String webServiceUrl;
    public String url;
    private JSONObject resultJSON;

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

                if(currentAction.equals(ACTION_INIT)){
                    readAndUpdateTag(nTag213215216);
                }
                else if(currentAction.equals(ACTION_WRITE)){
                    writeTagWithPassword(nTag213215216);

                    currentAction = ACTION_INIT;
                }
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
            this.webView.sendPluginResult(result, this.init_Cbk_Id);

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

    protected void readAndUpdateTag(final NTag213215216 tag)
    {
        byte pack[] = {0, 0};
        byte pw[] = password.getBytes();

        try{
            tag.connect();

            NdefMessage ndefMessage =  tag.readNDEF();

            String message = new String(ndefMessage.getRecords()[0].getPayload()).substring(3);
            String[] msg = message.split(";");

            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();
            String dateTime = dateFormat.format(date);

            url = webServiceUrl + msg[0];
            url..replace(" ", "%20");
            
            System.out.println(url);

            ThreadClass thread = new ThreadClass(this);
            thread.start();

            //wait for thread to finish
            thread.join();

            String description = resultJSON.getString("Description");

            String messageToWrite = msg[0] + ";" + description + ";" + currentUser + ";" + dateTime + ";";

            if (!"".equals(password)) {
                tag.authenticatePwd(pw, pack);
            }

            ndefMessage = new NdefMessage(NdefRecord.createTextRecord("en", messageToWrite));
            tag.writeNDEF(ndefMessage);

            System.out.println(messageToWrite);

            PluginResult result = new PluginResult(PluginResult.Status.OK, messageToWrite);
            result.setKeepCallback(true);
            this.webView.sendPluginResult(result, this.init_Cbk_Id);

        } catch (IOException e) {
            System.out.println("Password Authentication fail!");
            e.printStackTrace();
        } catch (SmartCardException e) {
            e.printStackTrace();
        } catch (FormatException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            try {
                tag.close();
            } catch (IOException e) {
                System.out.println("IOException at close(): " + e.getMessage());
            }
        }
    }

    protected void writeTagWithPassword(final NTag213215216 tag){
        byte pack[] = {0, 0};
        byte pw[] = password.getBytes();

        try {
            tag.connect();

            if (!"".equals(password)) {
                tag.authenticatePwd(pw, pack);
            }

            NdefMessage ndefMessage = new NdefMessage(NdefRecord.createTextRecord("en", messageToWrite));

            tag.writeNDEF(ndefMessage);

            PluginResult result = new PluginResult(PluginResult.Status.OK, "Message successfully written.");
            result.setKeepCallback(true);
            this.webView.sendPluginResult(result, this.write_Cbk_Id);

            System.out.println("Message successfully written.");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            System.out.println("Password Authentication fail!");
            e.printStackTrace();
        } catch (SmartCardException e) {
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
        if (action.equals(ACTION_INIT)){
            String user = data.getString(0);
            String serviceUrl = data.getString(1);
            currentAction = ACTION_INIT;
            init(user, serviceUrl, callbackContext);
        }
        else if (action.equals(ACTION_WRITE)){
            currentAction = ACTION_WRITE;
            String message = data.getString(0);
            write(message, callbackContext);
        }
        else {
            return false;
        }

        return true;
    }

    private void init(String user, String serviceUrl, CallbackContext callbackContext){
        init_Cbk_Id =  callbackContext.getCallbackId();
        password = "1234";
        currentUser = user;
        webServiceUrl = serviceUrl;
        System.out.println(user);

        if (NxpNfcLibLite.getInstance() != null) {
            libInstance.startForeGroundDispatch();
        }
    }

    private void write(String message, CallbackContext callbackContext){
        write_Cbk_Id =  callbackContext.getCallbackId();
        password = "1234";

        messageToWrite = message;
    }

    public void setResultJSON(JSONObject jsonObject) {
        this.resultJSON = jsonObject;
    }
}


class ThreadClass extends Thread {
    Nfc mainActivity;

    public ThreadClass(Nfc mainActivity) {
        this.mainActivity = mainActivity;
    }

    public void run() {
        try
        {
            URL url = new URL(mainActivity.url);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestProperty("User-Agent", "");
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.connect();

            InputStream inputStream = connection.getInputStream();

            BufferedReader rd = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder responseStrBuilder = new StringBuilder();

            String inputStr;
            while ((inputStr = rd.readLine()) != null)
                responseStrBuilder.append(inputStr);

            mainActivity.setResultJSON(new JSONArray(responseStrBuilder.toString()).getJSONObject(0));

        } catch (IOException e) {
            // writing exception to log
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
