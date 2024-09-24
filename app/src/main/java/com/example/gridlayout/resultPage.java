package com.example.gridlayout;

import android.view.View;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class resultPage extends AppCompatActivity
{
    private TextView mess;
    private int time;
    private Button playAgain;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result_page);

        mess = findViewById(R.id.mess);
        playAgain = findViewById(R.id.playAgain);

        Intent intent = getIntent();

        if(intent.hasExtra("WIN"))
        {
            mess.setText("Used " + time + " seconds.\nYou won.\nGood job!");
        }
        else if(intent.hasExtra("LOSER"))
        {
            mess.setText("Used " + time + " seconds.\nYou lost.\nTry again!");
        }

        playAgain.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent playAgainInt = new Intent(resultPage.this, MainActivity.class);
                playAgainInt.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(playAgainInt);
                finish();
            }
        });
    }


}
