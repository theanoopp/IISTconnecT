package com.anoop.iistconnect;

import android.app.Application;
import android.content.res.Configuration;
import android.widget.Toast;

import com.downloader.PRDownloader;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Anoop on 25-03-2018.
 */

public class IISTconnecT extends Application {

    // Called when the application is starting, before any other application objects have been created.
    // Overriding this method is totally optional!
    @Override
    public void onCreate() {
        super.onCreate();
        // Required initialization logic here!

        PRDownloader.initialize(getApplicationContext());
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }

    // Called by the system when the device configuration changes while your component is running.
    // Overriding this method is totally optional!
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    // This is called when the overall system is running low on memory,
    // and would like actively running processes to tighten their belts.
    // Overriding this method is totally optional!
    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }


}
