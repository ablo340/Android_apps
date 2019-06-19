package com.previsions.retroshooterlite;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //turn title off
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        //set to full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(new ShooterPanel(this));

        /*
        //Animation du lancement du jeu
        ImageView image = (ImageView) findViewById(R.id.lancement);

        AlphaAnimation alpha = new AlphaAnimation(1f, 0f);
        alpha.setDuration(5000);
        image.startAnimation(alpha);

        // L'appli attend 5 sec avant de lancer la deuxième image
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ImageView image = (ImageView) findViewById(R.id.lancement);
                image.setImageResource(R.drawable.lancement_2);
                AlphaAnimation alpha = new AlphaAnimation(1f, 0f);
                alpha.setDuration(5000);
                image.startAnimation(alpha);
            }
        }, 5000);*/
    }
}
