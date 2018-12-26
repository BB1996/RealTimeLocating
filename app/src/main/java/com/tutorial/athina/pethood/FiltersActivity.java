package com.tutorial.athina.pethood;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.tutorial.athina.pethood.Models.Breeds;

import java.util.ArrayList;

public class FiltersActivity extends AppCompatActivity implements View.OnClickListener {

    private String checkboxSize, checkboxMating, checkBreed;
    private EditText searchBox;
    private ImageButton buttonSearch;
    RecyclerView resultList;
    private DatabaseReference breedsRef;
    FirebaseRecyclerAdapter<Breeds, BreedViewHolder> adapter;
    private Button applyFilters;
    private ArrayList<String> userList;

    private CheckBox dogSmall, dogMedium, dogLarge, matingYes, matingNo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filters_layout);

        checkboxSize = "";
        checkboxMating = "";
        checkBreed = "";
        userList = new ArrayList<>();

        breedsRef = FirebaseDatabase.getInstance().getReference().child("breeds");

        dogSmall = (CheckBox) findViewById(R.id.dogSizeSmallSearch);
        dogMedium = (CheckBox) findViewById(R.id.dogSizeMediumSearch);
        dogLarge = (CheckBox) findViewById(R.id.dogSizeBigSearch);

        matingYes = (CheckBox) findViewById(R.id.yesMating);
        matingNo = (CheckBox) findViewById(R.id.noMating);

        applyFilters = (Button) findViewById(R.id.buttonApplyFilters);

        searchBox = (EditText) findViewById(R.id.searchBox);
        buttonSearch = (ImageButton) findViewById(R.id.img_ButtonSearch);
        resultList = (RecyclerView) findViewById(R.id.resultList);
        resultList.setHasFixedSize(true);
        resultList.setLayoutManager(new LinearLayoutManager(this));

        if (getIntent().hasExtra("userList")) {
            userList = getIntent().getStringArrayListExtra("userList");
        }

        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String searchText = searchBox.getText().toString().trim();
                fireBaseBreedSearch(searchText);
            }
        });


        applyFilters.setOnClickListener(this);

    }

    private void fireBaseBreedSearch(String searchText) {

        Toast.makeText(this, "Started searching..", Toast.LENGTH_SHORT).show();

        Query firebaseQuery = breedsRef.orderByChild("name").startAt(searchText).endAt(searchText + "\uf8ff");
        FirebaseRecyclerOptions<Breeds> options =
                new FirebaseRecyclerOptions.Builder<Breeds>()
                        .setQuery(firebaseQuery, Breeds.class)
                        .build();
        adapter = new FirebaseRecyclerAdapter<Breeds, BreedViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final BreedViewHolder holder, int position, @NonNull final Breeds model) {
                holder.breedResult.setText(model.getName());
                holder.itemClickListener = new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position) {
                        holder.breedResult.setTextColor(Color.MAGENTA);
                        checkBreed = model.getName();

                    }
                };
            }

            @NonNull
            @Override
            public BreedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(getBaseContext()).inflate(R.layout.list_breeds, parent, false);

                return new BreedViewHolder(itemView);
            }
        };

        adapter.startListening();
        resultList.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    public void onCheckboxClicked(View view) {
        boolean checked = ((CheckBox) view).isChecked();


        switch (view.getId()) {
            case R.id.dogSizeSmallSearch:
                if (checked) {
                    checkboxSize = "Small";
                }
                break;
            case R.id.dogSizeMediumSearch:
                if (checked) {
                    checkboxSize = "Medium";
                }

                break;
            case R.id.dogSizeBigSearch:
                if (checked) {
                    checkboxSize = "Big";
                }

                break;
            case R.id.yesMating:
                if (checked) {
                    checkboxMating = "Y";
                }

                break;
            case R.id.noMating:
                if (checked) {
                    checkboxMating = "N";
                }

                break;
            default:
                break;
        }

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.buttonApplyFilters) {
            Intent filtersIntent = new Intent(FiltersActivity.this, MapTracking.class);
            filtersIntent.putExtra("size", checkboxSize);
            filtersIntent.putExtra("breed", checkBreed);
            filtersIntent.putExtra("mating", checkboxMating);
            filtersIntent.putStringArrayListExtra("userList", (ArrayList<String>) userList);
            startActivity(filtersIntent);
        }
    }

    public static class BreedViewHolder extends RecyclerView.ViewHolder implements ItemClickListener, View.OnClickListener {

        public TextView breedResult;
        View mView;
        ItemClickListener itemClickListener;

        public BreedViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            breedResult = (TextView) mView.findViewById(R.id.breedResult);
            itemView.setOnClickListener(this);
        }

        public void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }

        @Override
        public void onClick(View view, int position) {
            itemClickListener.onClick(view, getAdapterPosition());
        }

        @Override
        public void onClick(View view) {
            itemClickListener.onClick(view, getAdapterPosition());
        }
    }
}
