package com.ucs.adriel.selfietictactoe;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.Serializable;

public class winnerMessage extends AppCompatActivity implements Serializable {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_winner_message);

        Gson gson = new Gson();
        MainActivity.WinnerData winnerData = gson.fromJson(getIntent().getStringExtra("winnerData"), MainActivity.WinnerData.class);

        ((ImageView)findViewById(R.id.imgView)).setImageBitmap(winnerData.image);
        ((TextView)findViewById(R.id.txtWinner)).setText(winnerData.text);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
               goBackToTheGame();
            }
        }, 3000);
    }
    private void goBackToTheGame(){
        startActivity(new Intent(getBaseContext(), MainActivity.class));

    }
}
