package uren.com.filmktphanem.FirstOpen;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;
import uren.com.filmktphanem.MainActivity;
import uren.com.filmktphanem.R;

public class SplashScreenActivity extends AppCompatActivity {

    ImageView imageView;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        Fabric.with(this, new Crashlytics());
        setImage();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                checkPreferences();
            }
        }, 3000);
    }

    private void checkPreferences(){
        sharedPreferences = getSharedPreferences("firstOpen", MODE_PRIVATE);

        if(sharedPreferences.getBoolean("check", false)){
            startMainActivity();
        }else {
            startAppIntroActivity();
        }
    }

    private void setImage() {
        imageView = findViewById(R.id.imageView);
        Glide.with(SplashScreenActivity.this)
                .load(R.drawable.app_icon)
                .apply(RequestOptions.fitCenterTransform())
                .into(imageView);
    }

    private void startMainActivity(){
        Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void startAppIntroActivity(){
        Intent intent = new Intent(SplashScreenActivity.this, AppIntroActivity.class);
        startActivity(intent);
        finish();
    }
}