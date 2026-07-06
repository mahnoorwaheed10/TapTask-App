
package com.example.taptask;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {

    private TextView areaAll, areaTench, areaSaddar;
    private String selectedArea = "All";

    private LinearLayout catHome, catShop, catOnline;
    private EditText etSearch;
    private TextView btnSearch;
    private TextView btnProfile, btnMyBookings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        bindViews();
        setupAreaFilter();
        setupCategoryClicks();
        setupSearch();
    }

    private void bindViews() {
        areaAll = findViewById(R.id.areaAll);
        areaTench = findViewById(R.id.areaTench);
        areaSaddar = findViewById(R.id.areaSaddar);

        catHome = findViewById(R.id.catHome);
        catShop = findViewById(R.id.catShop);
        catOnline = findViewById(R.id.catOnline);

        etSearch = findViewById(R.id.etSearch);
        btnSearch = findViewById(R.id.btnSearch);
        btnProfile = findViewById(R.id.btnProfile);
        btnMyBookings = findViewById(R.id.btnMyBookings);
    }

    private void setupAreaFilter() {
        areaAll.setOnClickListener(v -> selectArea("All"));
        areaTench.setOnClickListener(v -> selectArea("Tench Bhatta"));
        areaSaddar.setOnClickListener(v -> selectArea("Saddar"));
    }

    private void selectArea(String area) {
        selectedArea = area;

        areaAll.setBackgroundResource(R.drawable.bg_pill_unselected);
        areaAll.setTextColor(getResources().getColor(R.color.text_main));

        areaTench.setBackgroundResource(R.drawable.bg_pill_unselected);
        areaTench.setTextColor(getResources().getColor(R.color.text_main));

        areaSaddar.setBackgroundResource(R.drawable.bg_pill_unselected);
        areaSaddar.setTextColor(getResources().getColor(R.color.text_main));

        TextView selected;
        switch (area) {
            case "Tench Bhatta":
                selected = areaTench;
                break;
            case "Saddar":
                selected = areaSaddar;
                break;
            default:
                selected = areaAll;
                break;
        }

        selected.setBackgroundResource(R.drawable.bg_pill_selected);
        selected.setTextColor(getResources().getColor(R.color.white));

        Toast.makeText(this, "Area: " + area, Toast.LENGTH_SHORT).show();
    }

    private void setupCategoryClicks() {
        catHome.setOnClickListener(v -> openSubCategory("home"));
        catShop.setOnClickListener(v -> openSubCategory("shop"));
        catOnline.setOnClickListener(v -> openSubCategory("online"));
    }

    private void openSubCategory(String mainCatKey) {
        Intent intent = new Intent(HomeActivity.this, SubCategoryActivity.class);
        intent.putExtra(SubCategoryActivity.EXTRA_MAIN_CAT, mainCatKey);
        startActivity(intent);
    }

    private void setupSearch() {
        btnSearch.setOnClickListener(v -> {
            String query = etSearch.getText().toString().trim();
            if (query.isEmpty()) {
                Toast.makeText(this, "Please enter a service to search", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Searching for: " + query, Toast.LENGTH_SHORT).show();
            }
        });

        btnMyBookings.setOnClickListener(v ->
                startActivity(new Intent(HomeActivity.this, MyBookingsActivity.class)));

        btnProfile.setOnClickListener(v ->
                startActivity(new Intent(HomeActivity.this, ProfileActivity.class)));
    }
}