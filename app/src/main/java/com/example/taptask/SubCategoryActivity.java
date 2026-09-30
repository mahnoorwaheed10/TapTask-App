package com.example.taptask;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class SubCategoryActivity extends AppCompatActivity {

    public static final String EXTRA_MAIN_CAT = "main_cat";

    private LinearLayout subCatContainer;
    private TextView tvCategoryTitle;
    private TextView btnBack;
    private String mainCatKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subcategory);

        subCatContainer = findViewById(R.id.subCatContainer);
        tvCategoryTitle = findViewById(R.id.tvCategoryTitle);
        btnBack = findViewById(R.id.btnBack);

        mainCatKey = getIntent().getStringExtra(EXTRA_MAIN_CAT);

        if (mainCatKey == null || mainCatKey.trim().isEmpty()) {
            mainCatKey = "home";
        }

        setTitleForCategory();
        loadSubCategories();

        btnBack.setOnClickListener(v -> finish());
    }

    private void setTitleForCategory() {

        switch (mainCatKey) {

            case "shop":
                tvCategoryTitle.setText(
                        getString(R.string.cat_shop_title)
                );
                break;

            case "online":
                tvCategoryTitle.setText(
                        getString(R.string.cat_online_title)
                );
                break;

            default:
                tvCategoryTitle.setText(
                        getString(R.string.cat_home_title)
                );
                break;
        }
    }

    private List<SubCategoryData> getSubCategories() {

        List<SubCategoryData> list =
                new ArrayList<>();

        switch (mainCatKey) {

            case "shop":

                list.add(
                        new SubCategoryData(
                                "tailor",
                                "👗",
                                "Tailor",
                                "Custom stitching, suits, shalwar kameez & alterations"
                        )
                );

                list.add(
                        new SubCategoryData(
                                "mechanic",
                                "🔩",
                                "Mechanic Shop",
                                "Car & bike repair, servicing, AC & engine work"
                        )
                );

                list.add(
                        new SubCategoryData(
                                "tutor_shop",
                                "📚",
                                "Tutor (In-Person)",
                                "Private tutor at their learning center or academy"
                        )
                );

                break;

            case "online":

                list.add(
                        new SubCategoryData(
                                "home_tutor",
                                "🎓",
                                "Home Tutor (Online)",
                                "Live Zoom/video classes from home for all grades"
                        )
                );

                list.add(
                        new SubCategoryData(
                                "consultation",
                                "🩺",
                                "Consultation",
                                "Doctor, nutritionist & legal advisor consultations"
                        )
                );

                list.add(
                        new SubCategoryData(
                                "freelancer",
                                "💼",
                                "Freelancer",
                                "Web dev, design, content writing & digital projects"
                        )
                );

                break;

            default:

                list.add(
                        new SubCategoryData(
                                "electrician",
                                "⚡",
                                "Electrician",
                                "Wiring, repairs, solar panels, UPS & all electrical work"
                        )
                );

                list.add(
                        new SubCategoryData(
                                "plumber",
                                "🔧",
                                "Plumber",
                                "Pipes, geysers, water motors & complete plumbing"
                        )
                );

                list.add(
                        new SubCategoryData(
                                "cleaner",
                                "🧹",
                                "Cleaner",
                                "Deep cleaning, sofa wash, carpet & home sanitization"
                        )
                );

                break;
        }

        return list;
    }

    private void loadSubCategories() {

        subCatContainer.removeAllViews();

        for (SubCategoryData sub : getSubCategories()) {

            LinearLayout card =
                    createSubCategoryCard(sub);

            subCatContainer.addView(card);
        }
    }

    private LinearLayout createSubCategoryCard(
            SubCategoryData sub) {

        LinearLayout card =
                new LinearLayout(this);

        card.setOrientation(
                LinearLayout.HORIZONTAL
        );

        card.setGravity(
                Gravity.CENTER_VERTICAL
        );

        card.setBackgroundResource(
                R.drawable.bg_card
        );

        card.setPadding(
                dp(16),
                dp(16),
                dp(16),
                dp(16)
        );

        LinearLayout.LayoutParams cardParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        cardParams.bottomMargin = dp(12);

        card.setLayoutParams(cardParams);

        TextView icon =
                new TextView(this);

        icon.setText(sub.icon);
        icon.setTextSize(26);
        icon.setGravity(Gravity.CENTER);

        icon.setBackgroundResource(
                R.drawable.bg_chip_light
        );

        LinearLayout.LayoutParams iconParams =
                new LinearLayout.LayoutParams(
                        dp(56),
                        dp(56)
                );

        iconParams.setMarginEnd(dp(14));

        icon.setLayoutParams(iconParams);

        card.addView(icon);

        LinearLayout textContainer =
                new LinearLayout(this);

        textContainer.setOrientation(
                LinearLayout.VERTICAL
        );

        LinearLayout.LayoutParams textParams =
                new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1f
                );

        textContainer.setLayoutParams(
                textParams
        );

        TextView title =
                new TextView(this);

        title.setText(sub.title);

        title.setTextColor(
                getResources().getColor(
                        R.color.text_main
                )
        );

        title.setTextSize(15);

        title.setTypeface(
                null,
                android.graphics.Typeface.BOLD
        );

        textContainer.addView(title);

        TextView desc =
                new TextView(this);

        desc.setText(sub.description);

        desc.setTextColor(
                getResources().getColor(
                        R.color.muted
                )
        );

        desc.setTextSize(12);

        textContainer.addView(desc);

        card.addView(textContainer);

        // ========================================================
        // OPEN WORKERS LIST
        // ========================================================

        card.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            SubCategoryActivity.this,
                            WorkersListActivity.class
                    );

            // Existing sub-category data
            intent.putExtra(
                    WorkersListActivity.EXTRA_SUB_CAT,
                    sub.key
            );

            intent.putExtra(
                    WorkersListActivity.EXTRA_SUB_CAT_TITLE,
                    sub.title
            );

            // NEW:
            // Pass Home / Shop / Online forward
            intent.putExtra(
                    WorkersListActivity.EXTRA_MAIN_CAT,
                    mainCatKey
            );

            startActivity(intent);
        });

        return card;
    }

    private int dp(int value) {

        float density =
                getResources()
                        .getDisplayMetrics()
                        .density;

        return (int) (value * density);
    }
}