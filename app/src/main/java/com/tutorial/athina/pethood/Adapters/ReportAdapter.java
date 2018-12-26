package com.tutorial.athina.pethood.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tutorial.athina.pethood.Models.MissingDog;
import com.tutorial.athina.pethood.R;

import java.util.List;

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ViewHolder> {


    private Context mContext;
    private List<MissingDog> mMissingDog;

    String firebaseUser;


    public ReportAdapter(Context mContext, List<MissingDog> mMissingDog) {
        this.mContext = mContext;
        this.mMissingDog = mMissingDog;
    }

    @NonNull
    @Override
    public ReportAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.chat_report, parent, false);
        return new ReportAdapter.ViewHolder(view);


    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        MissingDog missingDog = mMissingDog.get(position);

        holder.show_reporter.setText(missingDog.getSender());
        holder.show_message_report.setText(missingDog.getMessage());


    }

    @Override
    public int getItemCount() {
        return mMissingDog.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView show_reporter, show_message_report;

        public ViewHolder(View itemView) {
            super(itemView);

            show_reporter = itemView.findViewById(R.id.show_reporter);
            show_message_report = itemView.findViewById(R.id.show_message_report);
        }


    }


}
