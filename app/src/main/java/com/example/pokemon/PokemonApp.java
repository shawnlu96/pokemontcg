package com.example.pokemon;

import android.app.Application;

public class PokemonApp extends Application {
    private Cards[] cards;

    public Cards[] getCards(){
        return cards;
    }

    public void setCards(Cards[] cards){
        this.cards = cards;
    }
}
