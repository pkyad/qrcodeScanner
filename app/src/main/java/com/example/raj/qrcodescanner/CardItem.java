package com.example.raj.qrcodescanner;

/**
 * Created by pradeep on 26/10/17.
 */

public class CardItem {

    public String name;
    public int id;
    public String imgUrl;
    public int rate;
    public int qty;

    public CardItem(String name, int id , String imgUrl , int rate , int qty) {
        this.name = name;
        this.id = id;
        this.imgUrl = imgUrl;
        this.rate = rate;
        this.qty = qty;
    }

}
