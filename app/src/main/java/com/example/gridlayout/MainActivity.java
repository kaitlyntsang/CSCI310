package com.example.gridlayout;




import androidx.appcompat.app.AppCompatActivity;
import androidx.gridlayout.widget.GridLayout;


import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;




import org.w3c.dom.Text;




import java.util.ArrayList;
import java.util.List;
import java.util.Random;








public class MainActivity extends AppCompatActivity {

    private TextView btnTest;
    public TextView time;
    private boolean isPick = true;


    private static final int COLUMN_COUNT = 10;



    private ArrayList<TextView> cell_tvs;




    private int dpToPixel(int dp) {
        float density = Resources.getSystem().getDisplayMetrics().density;
        return Math.round(dp * density);
    }


    int flagCount;
    Button playAgain;
    TextView flagVal;
    TextView clock;
    int timer =0;
    int foundBombs = 0;
    ArrayList<Integer> visited;
    boolean gameOver = false;








    List<Integer> bombPos = new ArrayList<Integer>();




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        flagCount = 4;
        flagVal = findViewById(R.id.flagVal);
        clock = findViewById(R.id.clockVal);
        playAgain = findViewById(R.id.playAgain);

        runTimer();

        //change pick/ flag icon
        TextView timeVal = findViewById(R.id.clockVal);
        btnTest = findViewById(R.id.pickImg);
        btnTest.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                if (isPick)
                {
                    isPick = false;
                    btnTest.setText(R.string.flag);
                } else {
                    isPick = true;
                    btnTest.setText(R.string.pick);
                }
            }
        });


        cell_tvs = new ArrayList<TextView>();


        GridLayout grid = (GridLayout) findViewById(R.id.gridLayout01);
        for (int i = 0; i < 12; i++) {
            for (int j=0; j <10; j++) {
                TextView tv = new TextView(this);
                tv.setHeight( dpToPixel(31) );
                tv.setWidth( dpToPixel(31) );
                tv.setTextSize( 14 );//dpToPixel(32) );
                tv.setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER);
                tv.setTextColor(Color.GREEN);
                tv.setBackgroundColor(Color.GREEN);
                tv.setOnClickListener(this::onClickTV);

                GridLayout.LayoutParams lp = new GridLayout.LayoutParams();
                lp.setMargins(dpToPixel(2), dpToPixel(2), dpToPixel(2), dpToPixel(2));
                lp.rowSpec = GridLayout.spec(i);
                lp.columnSpec = GridLayout.spec(j);


                grid.addView(tv, lp);


                cell_tvs.add(tv);
            }
        }
        setBombs();
    }




    private void setBombs()
    {
        for(int i =0; i < 4; i++)
        {
            Random rand = new Random();
            bombPos.add(rand.nextInt(120));
        }
    }


    private int findIndexOfCellTextView(TextView tv) {
        for (int n=0; n<cell_tvs.size(); n++) {
            if (cell_tvs.get(n) == tv)
                return n;
        }
        return -1;
    }




    public void onClickTV(View view){
        TextView result = findViewById(R.id.result);
        TextView tv = (TextView) view;
        int bombCount = 0;
        int n = findIndexOfCellTextView(tv);
        int i = n/COLUMN_COUNT;
        int j = n%COLUMN_COUNT;

        if(n == -1)
        {
            return;
        }

        if(gameOver)
        {
            if(foundBombs == 4 || visited.size() == 116)
            {
                showResult(1, timer);
            }
            else
            {
                showResult(0, timer);
            }
        }



        if(visited == null)
        {
            visited = new ArrayList<>();
        }

        if(!isPick)
        {
            if(flagCount > 0)
            {

                if(tv.getText().equals(getString(R.string.flag)))
                {
                    tv.setText("");
                    flagCount++;
                }
                else
                {
                    tv.setText(R.string.flag);
                    tv.setTextSize(15);
                    flagCount--;
                }
                tv.setBackgroundColor(Color.GREEN);
                tv.setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER);


                flagVal.setText(String.valueOf(flagCount));
            }
            else if (flagCount == 0)
            {
                if(tv.getText().equals(getString(R.string.flag)))
                {
                    tv.setText("");
                    flagCount++;
                }
            }

            if(bombPos.contains(n))
            {
                foundBombs++;
            }

        }
        else
        {

            if(bombPos.contains(n))
            {
                tv.setText(R.string.mine);
                tv.setBackgroundColor(Color.RED);
                tv.setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER);
                revealMines();
                gameOver = true;
                return;
            }
            else
            {
                bombCount = countBombsAround(i,j);

                if(bombCount > 0)
                {
                    if(!visited.contains(n))
                    {
                        visited.add(n);
                    }
                    tv.setText(String.valueOf(bombCount));
                    tv.setTextColor(Color.BLACK);
                }
                else
                {
                    tv.setTextColor(Color.LTGRAY);
                    revealCells(i,j, visited);
                }


                tv.setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER);
                tv.setBackgroundColor(Color.LTGRAY);


            }

        }


        if(foundBombs == 4 || visited.size() == 116)
        {
            gameOver = true;
            result.setText(String.valueOf("complete"));
            revealMines();
        }

    }

    private void runTimer() {
        clock = (TextView) findViewById(R.id.clockVal);
        final Handler handler = new Handler();

        handler.post(new Runnable() {
            @Override
            public void run() {

                int seconds = timer%60;

                String time = String.valueOf(seconds);
                clock.setText(time);

                if (gameOver == false) {
                    timer++;
                }
                handler.postDelayed(this, 1000);
            }
        });
    }

    private void revealMines()
    {
        for(int i =0; i < 4; i++)
        {
            int index = bombPos.get(i);
            TextView bombCell = cell_tvs.get(index);

            bombCell.setText(R.string.mine);
            bombCell.setBackgroundColor(Color.RED);
            bombCell.setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER);
        }

    }
    private void showResult(int result, int endTime)
    {
        Intent intent = new Intent(this, resultPage.class);
        intent.putExtra("time", endTime);
        if(result == 1) //win
        {
            intent.putExtra("WIN", 1);
        }
        else if(result ==0) //lost
        {
            intent.putExtra("LOSER", 0);
        }
        startActivity(intent);
    }


    private int countBombsAround(int row, int col) {
        int bombCount = 0;
        int[] rowsAround = {-1, -1, -1, 0, 0, 1, 1, 1};
        int[] colsAround = {-1, 0, 1, -1, 1, -1, 0, 1};

        for (int i = 0; i < 8; i++) {
            int currRow = row + rowsAround[i];
            int currCol = col + colsAround[i];

            if (currRow >= 0 && currRow < 12 && currCol >= 0 && currCol < 10) {
                int curr = currRow * 10 + currCol;
                if (bombPos.contains(curr)) {
                    bombCount++;
                }
            }
        }
        return bombCount;
    }


    private void revealCells(int row, int col, ArrayList<Integer> visited)
    {
        int[] rowsAround = {-1, -1, -1, 0, 0, 1, 1, 1};
        int[] colsAround = {-1, 0, 1, -1, 1, -1, 0, 1};


        int currCell = row * 10 + col;

        if(visited.contains(currCell))
        {
            return;
        }

        visited.add(currCell);

        int bombCount = countBombsAround(row, col);
        TextView cell = cell_tvs.get(currCell);

        if(bombCount == 0 && !(bombPos.contains(currCell)) && !(cell.getText().equals(getString(R.string.flag))))
        {
            cell.setBackgroundColor(Color.LTGRAY);
            cell.setText("");
        }
        else if(!(cell.getText().equals(getString(R.string.flag))))
        {
            cell.setText(String.valueOf(bombCount));
            cell.setTextColor(Color.BLACK);
            cell.setBackgroundColor(Color.LTGRAY);
            return;




        }




        for (int i = 0; i < 8; i++) {
            int currRow = row + rowsAround[i];
            int currCol = col + colsAround[i];




            if(currRow >= 0 && currRow < 12 && currCol >= 0 && currCol < 10)
            {
                revealCells(currRow, currCol, visited);
            }
        }


    }

}



