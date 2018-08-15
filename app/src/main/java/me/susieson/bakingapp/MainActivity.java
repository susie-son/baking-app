package me.susieson.bakingapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import timber.log.Timber;
import timber.log.Timber.DebugTree;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Timber.plant(new DebugTree());
    }
}
