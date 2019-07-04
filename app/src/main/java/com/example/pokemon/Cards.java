package com.example.pokemon;


import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Cards implements Parcelable {

    private ArrayList<CardsBean> cards;

    public ArrayList<CardsBean> getCards() {
        return cards;
    }

    public void setCards(ArrayList<CardsBean> cards) {
        this.cards = cards;
    }

    //method to include other cards in this object
    public void addCards(Cards anotherCards) {
        this.getCards().addAll(anotherCards.getCards());
    }

    public static class CardsBean implements Parcelable {

        private String id;
        private String imageUrlHiRes;
        private String rarity;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getImageUrlHiRes() {
            return imageUrlHiRes;
        }

        public void setImageUrlHiRes(String imageUrlHiRes) {
            this.imageUrlHiRes = imageUrlHiRes;
        }


        public String getRarity() {
            return rarity;
        }

        public void setRarity(String rarity) {
            this.rarity = rarity;
        }


        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.id);
            dest.writeString(this.imageUrlHiRes);
            dest.writeString(this.rarity);
        }

        public CardsBean() {
        }

        protected CardsBean(Parcel in) {
            this.id = in.readString();
            this.imageUrlHiRes = in.readString();
            this.rarity = in.readString();
        }

        public static final Creator<CardsBean> CREATOR = new Creator<CardsBean>() {
            @Override
            public CardsBean createFromParcel(Parcel source) {
                return new CardsBean(source);
            }

            @Override
            public CardsBean[] newArray(int size) {
                return new CardsBean[size];
            }
        };
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(this.cards);
    }

    public Cards() {
    }

    protected Cards(Parcel in) {
        this.cards = new ArrayList<CardsBean>();
        in.readList(this.cards, CardsBean.class.getClassLoader());
    }

    public static final Creator<Cards> CREATOR = new Creator<Cards>() {
        @Override
        public Cards createFromParcel(Parcel source) {
            return new Cards(source);
        }

        @Override
        public Cards[] newArray(int size) {
            return new Cards[size];
        }
    };
}
