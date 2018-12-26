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

import com.tutorial.athina.pethood.Models.Dog;
import com.tutorial.athina.pethood.R;

import java.util.List;

public class DogList extends ArrayAdapter {

    private Activity context;
    private List<Dog> dogList;

    public DogList(@NonNull Activity context, List<Dog> dogList) {
        super(context, R.layout.dog_details_view, dogList);
        this.context = context;
        this.dogList = dogList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();

        @SuppressLint("ViewHolder") View listViewItem = inflater.inflate(R.layout.list_dogs, null, true);

        TextView textViewName = (TextView) listViewItem.findViewById(R.id.dogNameView);
        TextView textViewOwner = (TextView) listViewItem.findViewById(R.id.dogOwnerView);
        TextView textViewBreed = (TextView) listViewItem.findViewById(R.id.dogBreedView);
        TextView textViewAge = (TextView) listViewItem.findViewById(R.id.dogAgeView);
        TextView textViewColor = (TextView) listViewItem.findViewById(R.id.dogColorView);
        TextView textViewSize = (TextView) listViewItem.findViewById(R.id.dogSizeView);
        TextView textViewMating = (TextView) listViewItem.findViewById(R.id.dogMateView);

        Dog dog = dogList.get(position);

        textViewName.setText(dog.getDogName());
        textViewOwner.setText(dog.getDogOwner());
        textViewBreed.setText(dog.getDogBreed());
        textViewAge.setText(dog.getDogAge());
        textViewSize.setText(dog.getDogSize());
        textViewColor.setText(dog.getDogColor());
        textViewMating.setText(dog.getDogMateFlag());

        return listViewItem;

    }
}
