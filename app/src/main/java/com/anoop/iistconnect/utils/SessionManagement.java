package com.anoop.iistconnect.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import in.rgpvnotes.alert.myresource.model.StudentModel;

import static android.content.Context.MODE_PRIVATE;

public class SessionManagement {

    public static void saveStudent(Context context,StudentModel student){

        String PREF_NAME = "Student";

        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        SharedPreferences.Editor prefsEditor = pref.edit();
        Gson gson = new Gson();
        String json = gson.toJson(student);
        prefsEditor.putString("MyObject", json);
        prefsEditor.commit();

    }

    public static StudentModel getStudent(Context context){

        String PREF_NAME = "Student";

        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        Gson gson = new Gson();
        String json = pref.getString("MyObject", "");
        return gson.fromJson(json, StudentModel.class);

    }


}
