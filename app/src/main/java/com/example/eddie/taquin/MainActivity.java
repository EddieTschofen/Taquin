package com.example.eddie.taquin;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button imageSelect = (Button) findViewById(R.id.StartGameButton);
        imageSelect.setOnClickListener(this);

        Button rules = (Button) findViewById(R.id.rulesButton);
        rules.setOnClickListener(this);
    }

    private final static int gameActivityIndex = 0;
    public void onClick(View view){
        Intent intent;
        switch (view.getId()){
            case R.id.StartGameButton:
                intent = new Intent(this, ChoseImageActivity.class);
                startActivityForResult(intent,gameActivityIndex);
                break;
            case R.id.rulesButton:
                intent = new Intent(this, RulesActivity.class);
                startActivity(intent);
                break;

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case (gameActivityIndex) : {
                if (resultCode == Activity.RESULT_OK) {
                    String action = data.getStringExtra("action");
                    switch(action){
                        case "quit" :
                            finish();
                            break;
                    }
                }
                break;
            }
        }
    }
}
