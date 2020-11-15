package com.example.chatapp.Classes;

import android.widget.Toast;

import com.onesignal.OneSignal;

import org.json.JSONException;
import org.json.JSONObject;

public class SendNotification
{
    public SendNotification(String message, String heading, String notificationKey)
    {
        try {
            OneSignal.postNotification(new JSONObject("{'contents': {'en':'" + message + "'}, 'include_player_ids': ['" + notificationKey + "']}"), null);
        } catch (JSONException e) {
            e.printStackTrace();
        }

//        try {
//            OneSignal.postNotification(new JSONObject("{'contents': {'en':'" + message + "'}, 'include_player_ids': ['" + notificationKey + "']," +
//        "'headings' : {'en' : '" + heading + "'}"), null);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }

        /*try {
            OneSignal.postNotification(new JSONObject("{'contents': {'en':'Test Message'}, 'include_player_ids': ['" + notificationKey + "']}"), null);
        } catch (JSONException e) {
            e.printStackTrace();
        }*/
    }
}
