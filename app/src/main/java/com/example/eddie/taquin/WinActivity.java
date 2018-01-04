package com.example.eddie.taquin;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class WinActivity extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_win);

        MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.tada);
        mediaPlayer.start(); // no need to call prepare(); create() does that for you

        int nbCoups = getIntent().getExtras().getInt("nbCoups");
        TextView winText = (TextView) this.findViewById(R.id.winText2);

        winText.setText(getApplicationContext().getResources().getString(R.string.noMove) + " : " + nbCoups);

        Button restartButton = (Button) findViewById(R.id.restartButton);
        restartButton.setOnClickListener(this);
        Button menuButton = (Button) findViewById(R.id.menuButton);
        menuButton.setOnClickListener(this);
        Button quitButton = (Button) findViewById(R.id.quitButton);
        quitButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        Intent i = new Intent();
        switch (v.getId()) {
            case R.id.restartButton:
                i.putExtra("action","restart");
                break;
            case R.id.menuButton:
                i.putExtra("action","menu");
                break;
            case R.id.quitButton:
                i.putExtra("action","quit");
                break;
        }
        setResult(Activity.RESULT_OK, i);
        finish();

    }
}
