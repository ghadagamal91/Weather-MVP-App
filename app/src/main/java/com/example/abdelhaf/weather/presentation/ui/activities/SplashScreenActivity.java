package com.example.abdelhaf.weather.presentation.ui.activities;


import android.content.Intent;
import android.content.SharedPreferences;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import com.example.abdelhaf.weather.App;
import com.example.abdelhaf.weather.R;
import javax.inject.Inject;


import butterknife.ButterKnife;


public class SplashScreenActivity extends AppCompatActivity  {



    @Inject
    SharedPreferences sharedPreferences;
    private final int SPLASH_DISPLAY_LENGTH = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        App app = (App) getApplication();
        app.getApiComponent().inject(SplashScreenActivity.this);


            new Handler().postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
                    startActivity(intent);
                  finish();
                }
            }, SPLASH_DISPLAY_LENGTH);
        }




}
