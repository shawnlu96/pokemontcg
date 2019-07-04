package com.example.pokemon;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GetCardsAPI {

    @GET("v1/cards")
    Call<Cards> getCall(
            @Query("page") Integer pageNum,
            @Query("rarity") String rarity
    );
}
