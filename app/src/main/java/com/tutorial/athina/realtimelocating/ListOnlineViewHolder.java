package com.tutorial.athina.realtimelocating;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

public class ListOnlineViewHolder extends RecyclerView.ViewHolder {

    public TextView textEmail;

    public ListOnlineViewHolder(View itemView) {
        super(itemView);
        textEmail = (TextView) itemView.findViewById(R.id.text_email);
    }
}
