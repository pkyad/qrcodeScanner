package com.example.raj.qrcodescanner;

/**
 * Created by pradeep on 26/10/17.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class CardArrayAdapter  extends ArrayAdapter<CardItem> {
    private static final String TAG = "CardArrayAdapter";
    private List<CardItem> cardList = new ArrayList<CardItem>();

    static class CardViewHolder {
        TextView line1;
        TextView line2;
        TextView qty;
        ImageView img;
    }

    public CardArrayAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    @Override
    public void add(CardItem object) {
        cardList.add(object);
        super.add(object);
    }

    @Override
    public int getCount() {
        return this.cardList.size();
    }

    @Override
    public CardItem getItem(int index) {
        return this.cardList.get(index);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        CardViewHolder viewHolder;
        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.list_item_card, parent, false);
            viewHolder = new CardViewHolder();
            viewHolder.line1 = (TextView) row.findViewById(R.id.line2);
            viewHolder.line2 = (TextView) row.findViewById(R.id.line1);
            viewHolder.qty = (TextView) row.findViewById(R.id.qty);
            viewHolder.img = (ImageView) row.findViewById(R.id.imageView3);
            row.setTag(viewHolder);
        } else {
            viewHolder = (CardViewHolder)row.getTag();
        }
        CardItem card = getItem(position);
        viewHolder.line1.setText(card.name);
        viewHolder.line2.setText( "Rate : " +Integer.toString(card.rate) + "(INR)");
        viewHolder.qty.setText(Integer.toString(card.qty));

        Picasso.with(getContext()).load(card.imgUrl).into(viewHolder.img);
        viewHolder.img.setVisibility(View.VISIBLE);
        return row;
    }

    public Bitmap decodeToBitmap(byte[] decodedByte) {
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }
}