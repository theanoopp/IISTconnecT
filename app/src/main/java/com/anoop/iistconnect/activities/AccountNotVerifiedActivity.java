package com.anoop.iistconnect.activities;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.anoop.iistconnect.R;

public class AccountNotVerifiedActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_not_verified);
        getSupportActionBar().setTitle("Account not verified");
    }
}
