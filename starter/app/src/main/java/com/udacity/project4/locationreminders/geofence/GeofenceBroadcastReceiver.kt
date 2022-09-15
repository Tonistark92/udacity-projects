package com.udacity.project4.locationreminders.geofence

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent


class GeofenceBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {

        if (intent.action ==  "ACTION_GEOFENCE_EVENT") {
//            when the broadcast got regestered with pending intent later will start geofincing
            GeofenceTransitionsJobIntentService.enqueueWork(context, intent)

        }

    }
}