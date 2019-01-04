package com.tutorial.athina.pethood.Adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.tutorial.athina.pethood.Models.Owner;
import com.tutorial.athina.pethood.R;

import java.util.List;

public class OwnerList extends ArrayAdapter {

    private Activity context;
    private List<Owner> ownerList;

    public OwnerList(@NonNull Activity context, List<Owner> ownerList) {
        super(context, R.layout.user_details_view, ownerList);
        this.context = context;
        this.ownerList = ownerList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();

        @SuppressLint("ViewHolder") View listViewItem = inflater.inflate(R.layout.list_users, null, true);

        TextView textViewName = (TextView) listViewItem.findViewById(R.id.ownerNameView);
        TextView textViewSurname = (TextView) listViewItem.findViewById(R.id.ownerSurnameView);
        TextView textViewPhone = (TextView) listViewItem.findViewById(R.id.ownerPhoneView);
        TextView textViewEmail = (TextView) listViewItem.findViewById(R.id.ownerEmailView);


        Owner owner = ownerList.get(position);

        textViewName.setText("Name: \n" + owner.getName());
        textViewSurname.setText("Surname: \n" + owner.getSurname());
        textViewPhone.setText("Phone: \n" + owner.getPhone());
        textViewEmail.setText("Email: \n" + owner.getEmail());


        return listViewItem;

    }
}
