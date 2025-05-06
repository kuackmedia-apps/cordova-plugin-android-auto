package com.kuackmedia.androidauto;

import android.content.Intent;
import org.apache.cordova.*;
import org.json.JSONArray;
import org.json.JSONException;

public class AndroidAutoPlugin extends CordovaPlugin {

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if ("startService".equals(action)) {
            Intent intent = new Intent(cordova.getContext(), AndroidAutoService.class);
            cordova.getContext().startService(intent);
            callbackContext.success("Servicio Android Auto iniciado");
            return true;
        }
        return false;
    }
}
