package com.tutorial.athina.realtimelocating;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

public class ListOnlineViewHolder extends RecyclerView.ViewHolder implements ItemClickListener {

    public TextView textEmail;
    ItemClickListener itemClickListener;

    public ListOnlineViewHolder(View itemView) {
        super(itemView);
        textEmail = (TextView) itemView.findViewById(R.id.text_email);

    }

    public void setItemClickListener(ItemClickListener itemClickListener){
        this.itemClickListener = itemClickListener;
    }
    @Override
    public void onClick(View view, int position) {
        itemClickListener.onClick(view,getAdapterPosition());
    }


}
