package com.example.eddie.taquin;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;

public class GameActivity extends AppCompatActivity {

    private int hidden; //Index of the hidden chunk
    private int cuts; //Number of chunk on each row/collumn
    private Bitmap b; //Full image
    private ArrayList<Bitmap> chucks; //List of chunks in actual order
    private ArrayList<Integer> actualOrder;
    private ArrayList<Bitmap> winChucks; //List of chunks in actual order
    private GridView gameGrid;
    private int nbCoups = 0; //Number of move before the end
    private TextView moves;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        //Get what was sent from other activity
        Bundle extras = getIntent().getExtras();

        //Get infos from extra
        String info = extras.get("cuts") + " - " + extras.get("selected") + " - " + extras.get("id");
        Toast.makeText(this, info, Toast.LENGTH_LONG);

        cuts = Integer.parseInt(extras.get("cuts") + "");
        b = getBitmap(extras);
        //If the image is not square, resize it
        if (b.getHeight() != b.getWidth()) {
            b = resizeBitmap(b);
        }

        //Display the full image
        final ImageView IV = (ImageView) this.findViewById(R.id.fullPic);
        IV.setImageBitmap(b);

        //Cut the image
        chucks = cutBitmap();
        winChucks = (ArrayList<Bitmap>) chucks.clone();
        hidden = chucks.size() - 1;
        actualOrder = new ArrayList<>();
        int n = chucks.size();
        for (int i = 1; i <= n; i++) actualOrder.add(i);
        Log.d("actualOrder", actualOrder.toString());


        //Shuffle the chunks
//        Log.d("beforeS",chucks.toString());
        chucks = shuffle(chucks);
//        Log.d("AfterS",chucks.toString());

        //init Gridview
        gameGrid = (GridView) findViewById(R.id.taquinGrid);
        gameGrid.setNumColumns(cuts);
        gameGrid.setBackgroundColor(Color.BLACK);
        gameGrid.setAdapter(new TaquinImageAdapter(this, chucks, cuts, hidden));

        gameGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
            }
        });

        //on swipe
        gameGrid.setOnTouchListener(new OnSwipeTouchListener(this) {
            float xRatio = 0.335f*3/cuts;
            float yRatio = 0.325f*3/cuts;
            //Move the tiles
            public void onSwipeTop() {
                if (hidden < chucks.size() - cuts) {
                    View movedTile = gameGrid.getChildAt(hidden + cuts);
                    float y = gameGrid.getHeight() * yRatio;
                    movedTile.animate().translationY(-y).withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            gameGrid.setAdapter(new TaquinImageAdapter(getApplicationContext(), chucks, cuts, hidden));
                        }
                    });

                    swipeChunks(chucks, hidden, hidden + cuts);
//                    gameGrid.setAdapter(new TaquinImageAdapter(getApplicationContext(), chucks, cuts, hidden));
                    nbCoups++;
                    moves.setText(getApplicationContext().getResources().getString(R.string.noMove) + " : " + nbCoups);
                }
//                Toast.makeText(GameActivity.this, "top", Toast.LENGTH_SHORT).show();
                checkWin();
            }

            public void onSwipeRight() {
                if (hidden % cuts > 0) {
                    View movedTile = gameGrid.getChildAt(hidden - 1);
                    float x = gameGrid.getWidth() * xRatio;
                    movedTile.animate().translationX(x).withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            gameGrid.setAdapter(new TaquinImageAdapter(getApplicationContext(), chucks, cuts, hidden));
                        }
                    });

                    swipeChunks(chucks, hidden, hidden - 1);
//                    gameGrid.setAdapter(new TaquinImageAdapter(getApplicationContext(), chucks, cuts, hidden));
                    nbCoups++;
                    moves.setText(getApplicationContext().getResources().getString(R.string.noMove) + " : " + nbCoups);
                }
//                Toast.makeText(GameActivity.this, "right", Toast.LENGTH_SHORT).show();
                checkWin();
            }

            public void onSwipeLeft() {
                if (hidden % cuts < cuts - 1) {
                    View movedTile = gameGrid.getChildAt(hidden + 1);
                    float x = gameGrid.getWidth() * xRatio;
                    movedTile.animate().translationX(-x).withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            gameGrid.setAdapter(new TaquinImageAdapter(getApplicationContext(), chucks, cuts, hidden));
                        }
                    });

                    swipeChunks(chucks, hidden, hidden + 1);
//                    gameGrid.setAdapter(new TaquinImageAdapter(getApplicationContext(), chucks, cuts, hidden));
                    nbCoups++;
                    moves.setText(getApplicationContext().getResources().getString(R.string.noMove) + " : " + nbCoups);
                }
//                Toast.makeText(GameActivity.this, "left", Toast.LENGTH_SHORT).show();
                checkWin();
            }

            public void onSwipeBottom() {
                if (hidden > cuts - 1) {
                    View movedTile = gameGrid.getChildAt(hidden - cuts);
                    float y = gameGrid.getHeight() * yRatio;
                    movedTile.animate().translationY(y).withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            gameGrid.setAdapter(new TaquinImageAdapter(getApplicationContext(), chucks, cuts, hidden));
                        }
                    });
                    swipeChunks(chucks, hidden, hidden - cuts);
//                    gameGrid.setAdapter(new TaquinImageAdapter(getApplicationContext(), chucks, cuts, hidden));
                    nbCoups++;
                    moves.setText(getApplicationContext().getResources().getString(R.string.noMove) + " : " + nbCoups);
                }
//                Toast.makeText(GameActivity.this, "bottom", Toast.LENGTH_SHORT).show();
                checkWin();
            }
        });


        //Init noMove
        moves = (TextView) this.findViewById(R.id.moveNumber);
        moves.setText(this.getResources().getString(R.string.noMove) + " : " + nbCoups);

        //checkBox
        CheckBox box = (CheckBox) this.findViewById(R.id.displayOriginal);
        box.setChecked(true);
        box.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
               @Override
               public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                   if(isChecked)
                       IV.setVisibility(View.VISIBLE);
                   else
                       IV.setVisibility(View.INVISIBLE);
               }
           }
        );
    }

    private final static int winActivityIndex = 0;

    private void checkWin() {
        Boolean isWined = true;
        int i = 0;

        while (isWined && i < chucks.size()) {
            isWined = isWined && (chucks.get(i).sameAs(winChucks.get(i)) ? true : false);
            i++;
        }

//        Log.d("chucks",chucks.toString());
//        Log.d("winChucks",winChucks.toString());
//        Log.d("identical ?",""+isWined);

        if (isWined) {
            Intent in = new Intent(this, WinActivity.class);
            in.putExtra("nbCoups", nbCoups);
//            startActivityForResults(in, 0);
            this.startActivityForResult(in, winActivityIndex);
//            this.startActivity(in);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case (winActivityIndex): {
                if (resultCode == Activity.RESULT_OK) {
                    String action = data.getStringExtra("action");
                    switch (action) {
                        case "restart":
                            chucks = shuffle(chucks);
                            nbCoups = 0;
                            moves.setText(getApplicationContext().getResources().getString(R.string.noMove) + " : " + nbCoups);
                            gameGrid.setAdapter(new TaquinImageAdapter(getApplicationContext(), chucks, cuts, hidden));
                            break;
                        case "menu":
                            Intent i = new Intent();
                            i.putExtra("action", "menu");
                            setResult(Activity.RESULT_OK, i);
                            finish();
                            break;
                        case "quit":
                            Intent in = new Intent();
                            in.putExtra("action", "quit");
                            setResult(Activity.RESULT_OK, in);
                            finish();
                            break;
                    }
                }
                break;
            }
        }
    }

    /**
     * Shuffle the gameboard
     *
     * @param chucks
     * @return
     */
    private ArrayList<Bitmap> shuffle(ArrayList<Bitmap> chucks) {
        int r = 1;
        while (r % 2 != 0) {
            r = 50 + (int) (Math.random() * (500 - 50));
        }
        int i = 0;
        while (i < r || !isValidShuffle(chucks)) {
            int m1 = (int) (Math.random() * (chucks.size()));
            int m2 = m1;
            while (m2 == m1) {
                m2 = (int) (Math.random() * (chucks.size()));
            }

            chucks = swipeChunks(chucks, m1, m2);
            i++;
        }
        return chucks;
    }

    /**
     * swipe 2 chunkes
     *
     * @param chucks
     * @param m1
     * @param m2
     * @return
     */
    private ArrayList<Bitmap> swipeChunks(ArrayList<Bitmap> chucks, int m1, int m2) {
        Collections.swap(chucks, m1, m2);
        Collections.swap(actualOrder, m1, m2);
//        Log.d("actualOrder", actualOrder.toString());

        if (m1 == hidden) {
            hidden = m2;
        } else if (m2 == hidden) {
            hidden = m1;
        }
        return chucks;
    }

    /**
     * resize an image
     *
     * @param b
     * @return
     */
    private Bitmap resizeBitmap(Bitmap b) {
        int gap;
        if (b.getHeight() > b.getWidth()) {
            gap = (b.getHeight() - b.getWidth()) / 2;
            return Bitmap.createBitmap(b, 0, gap, b.getWidth(), b.getWidth());
        } else {
            gap = (b.getHeight() - b.getWidth()) / 2;
            return Bitmap.createBitmap(b, gap, 0, b.getHeight(), b.getHeight());
        }
    }

    private Bitmap getBitmap(Bundle extras) {
        int selected = (int) extras.get("selected");

        Bitmap b;

        if (selected == 0) {
            b = BitmapFactory.decodeResource(this.getResources(), Integer.parseInt(extras.get("id") + ""));
            return b;
        } else if (selected == 1) {
            Uri u = (Uri) extras.get("id");
            try {
                InputStream is = getContentResolver().openInputStream(u);
                b = BitmapFactory.decodeStream(is);

                return b;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        } else if (selected == 2) {
            byte[] byteArray = getIntent().getByteArrayExtra("photo");
            b = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);

            return b;
        }
        return null;
    }

    private ArrayList<Bitmap> cutBitmap() {
        ArrayList<Bitmap> chunks = new ArrayList<>();
        int h = b.getHeight();
        int w = b.getWidth();
        for (int i = 0; i < cuts; i++) {
            for (int j = 0; j < cuts; j++) {
                chunks.add(Bitmap.createBitmap(b, j * (w / cuts), i * (h / cuts), w / cuts, h / cuts));
            }
        }
        return chunks;
    }

    public boolean isValidShuffle(ArrayList<Bitmap> chucks) {
        boolean validShuffle = false;
        ArrayList<Integer> order = (ArrayList<Integer>) actualOrder.clone();
        int inversions = 0;
        for (int i = 1; i <= (cuts * cuts); i++) {
            int index = order.indexOf(i) + 1;
//            Log.d("order", i + " - " + index);
            inversions += (index - 1);
            order.remove(index - 1);
        }
//        Log.d("inversions", inversions + "");
        if (((cuts % 2) != 0) && (inversions % 2) == 0) return true; //cuts odd and inversion even*/
        else {
            int blankIndex = actualOrder.indexOf(actualOrder.size());
            int blankRowCount=1;
            while(blankIndex>cuts-1){
                blankIndex -= cuts;
                blankRowCount++;
            }
            Log.d("blank ligne : ",blankRowCount+"");

            int blankRowCountFromBottom = cuts - blankRowCount +1;
            if(blankRowCountFromBottom%2 == 0) return(inversions%2!=0);
            else return(inversions%2==0);
        }

        //return false;
    }
}
