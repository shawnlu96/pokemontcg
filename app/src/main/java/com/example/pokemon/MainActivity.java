package com.example.pokemon;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Message;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.daimajia.numberprogressbar.NumberProgressBar;

import java.io.IOException;

import okhttp3.Headers;
import pl.droidsonroids.gif.GifImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "com.example.pokemon.MESSAGE";
    Cards cards[] = new Cards[6];
    private static final String[] rarities = {"common", "uncommon", "rare", "rare holo", "rare ultra", "rare secret"};
    boolean finished = false;
    //components
    NumberProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        progressBar = findViewById(R.id.number_progress_bar);
        for (int i = 0; i < rarities.length; i++) {
            cards[i] = new Cards();
            Thread t = new Thread(new Task(i));
            t.start();

        }

    }

    /**
     * child thread for network-required task
     */
    class Task implements Runnable{
        public Task(int rarityNo){
            this.rarityNo = rarityNo;
        }
        private int rarityNo;
        @Override
        public void run() {
            // TODO
            // Do HTTP request here
            //get the page counts
            int pageCount = getPageNumByRarity(rarityNo);
            for (int i = 1; i <=pageCount ; i++) {
                loadPage(i, rarityNo);
            }
        }
    }

    public void loadPage(final int pageNum, final int rarity){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.pokemontcg.io/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        GetCardsAPI cardsAPI = retrofit.create(GetCardsAPI.class);
        Call<Cards> call = cardsAPI.getCall(pageNum,rarities[rarity]);
        call.enqueue(new Callback<Cards>() {
            @Override
            public void onResponse(Call<Cards> call, Response<Cards> response) {
                if(!response.isSuccessful()){
                    //if not successful, make a toast to show the http status code
                    Toast.makeText(MainActivity.this, "code:" + response.code(), Toast.LENGTH_SHORT).show();
                    return;
                }
                Cards temp = response.body();
                //add all cards from current page to the corresponding list
                cards[rarity].getCards().addAll(temp.getCards());
                //if successfully loaded, add the bar's progress
                synchronized (progressBar) {
                    progressBar.setProgress(progressBar.getProgress()+1);
                    GifImageView giv = findViewById(R.id.running_pikachu);
                    giv.setLeft(giv.getLeft()+10);
                    if (progressBar.getProgress()==100){
                        finished = true;
                    }
                }
            }

            @Override
            public void onFailure(Call<Cards> call, Throwable t) {
                Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public Headers getResponseHeader(int n){      //0<=n<=5, represents different rarities
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.pokemontcg.io/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        GetCardsAPI cardsAPI = retrofit.create(GetCardsAPI.class);
        Call<Cards> call = cardsAPI.getCall(1,rarities[n]);
        try {
            return call.execute().headers();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    public int getPageNumByRarity(int n){
        Headers h = getResponseHeader(n);
        int total = Integer.parseInt(h.get("total-count"));
        return (total)/100 + 1;
    }
}
