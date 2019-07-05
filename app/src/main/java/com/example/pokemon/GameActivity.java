package com.example.pokemon;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;

import java.io.IOException;
import java.util.Random;


public class GameActivity extends Activity {
    private Cards[] cards;
    private boolean leftWinner = true;
    private boolean leftLoaded = false;
    private boolean rightloaded = false;
    private int winCount = 0;
    private SoundPool soundPool;
    private int soundID;
    private int round = 1;
    private ImageView cardLeft, cardRight;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //set activity fullscreen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,  WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_game);
        //initialisation...
        soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC,0);
        soundID = soundPool.load(this, R.raw.coins,1);
        //get global variables
        PokemonApp app = (PokemonApp) getApplicationContext();
        cards = app.getCards();
        cardLeft = findViewById(R.id.CardLeft);
        cardRight = findViewById(R.id.CardRight);
        cardLeft.setOnClickListener(listener);
        cardRight.setOnClickListener(listener);
//        Toast.makeText(GameActivity.this, cards[0].getCards().get(0).getId() ,Toast.LENGTH_SHORT).show();
        loadNewPics();
    }

    private void initRound(){
        cardLeft.setImageDrawable(null);
        cardRight.setImageDrawable(null);
        leftLoaded = false;
        rightloaded = false;
        loadNewPics();
    }

    private void addWinCount() {
        switch (winCount){
            case 0:
                findViewById(R.id.pokecoin_1).setVisibility(View.VISIBLE);break;
            case 1:
                findViewById(R.id.pokecoin_2).setVisibility(View.VISIBLE);break;
            case 2:
                findViewById(R.id.pokecoin_3).setVisibility(View.VISIBLE);break;
            case 3:
                findViewById(R.id.pokecoin_4).setVisibility(View.VISIBLE);break;
            case 4:
                findViewById(R.id.pokecoin_5).setVisibility(View.VISIBLE);break;
        }
        winCount++;
        soundPool.play(soundID,1.0f, 1.0f, 0, 0, 1.0f);
    }

    //listener added to change pic when there's error with the pic server
    private RequestListener<Drawable> rl = new RequestListener<Drawable>() {
        @Override
        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
            loadNewPics();
            return false;
        }

        @Override
        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
            return false;
        }
    };
    //method to automatically choose two cards with different rarity and load them into the ImageViews
    private void loadNewPics(){
        Random r = new Random();
        int left = r.nextInt(6);
        int right;
        do{
            Random k = new Random();
            right = k.nextInt(6);

        }while (left==right);
        if(left>right){
            leftWinner = true;
        }else{
            leftWinner = false;
        }
        int leftIndex = r.nextInt(cards[left].getCards().size());
        int rightIndex = r.nextInt(cards[right].getCards().size());
        String leftImgUrl = cards[left].getCards().get(leftIndex).getImageUrlHiRes();
        String rightImgUrl = cards[right].getCards().get(rightIndex).getImageUrlHiRes();
        Glide.with(GameActivity.this).load(leftImgUrl).listener(rl).error(R.drawable.no_image).into(new SimpleTarget<Drawable>() {
            @Override
            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                cardLeft.setImageDrawable(resource);
                //flag set to true when callback method invoked
                leftLoaded = true;
            }
        });
        Glide.with(GameActivity.this).load(rightImgUrl).listener(rl).error(R.drawable.no_image).into(new SimpleTarget<Drawable>() {
            @Override
            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                cardRight.setImageDrawable(resource);
                rightloaded = true;
            }
        });
    }

    public void overallWin(){
        cardLeft.setVisibility(View.INVISIBLE);
        cardRight.setVisibility(View.INVISIBLE);
        leftLoaded = false;
        findViewById(R.id.textView).setVisibility(View.VISIBLE);
        findViewById(R.id.imageView5).setVisibility(View.VISIBLE);
    }

    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if((!leftLoaded)||(!rightloaded)){return ;}
            boolean win = false;
            switch (view.getId()){
                case R.id.CardLeft:
                    if(leftWinner){
                        win = true;
                    }
                    break;
                case R.id.CardRight:
                    if(!leftWinner){
                        win = true;
                    }
                    break;
            }
            if(win){
                addWinCount();
                if(winCount==5){    //if the player has won the overall game
                    overallWin();
                }else {         // if not, go to next round
                    initRound();
                }
            }else{
                initRound();
            }
            TextView tv = findViewById(R.id.round_text);
            tv.setText("Round " + String.valueOf(++round));
        }
    };

    public void refresh(View v){
        Intent intent = new Intent(GameActivity.this,GameActivity.class);
        startActivity(intent);
        GameActivity.this.finish();

    }



}
