package com.example.sam.findme;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Sam on 12/3/2017.
 */

public class ListOnlineViewHolder extends RecyclerView.ViewHolder{
    public TextView txtEmail;

    public ListOnlineViewHolder(View itemView) {
        super(itemView);
        txtEmail = itemView.findViewById(R.id.txt_email);
    }
}
