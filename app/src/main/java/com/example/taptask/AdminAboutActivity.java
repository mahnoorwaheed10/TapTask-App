package com.example.taptask;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class AdminAboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        buildPage();
    }

    private void buildPage() {

        ScrollView scrollView =
                new ScrollView(this);

        scrollView.setFillViewport(true);
        scrollView.setBackgroundColor(
                Color.rgb(247, 246, 251)
        );

        LinearLayout root =
                new LinearLayout(this);

        root.setOrientation(
                LinearLayout.VERTICAL
        );

        scrollView.addView(root);

        // =========================================
        // HEADER
        // =========================================

        LinearLayout header =
                new LinearLayout(this);

        header.setOrientation(
                LinearLayout.HORIZONTAL
        );

        header.setGravity(
                Gravity.CENTER_VERTICAL
        );

        header.setPadding(
                dp(20),
                dp(18),
                dp(20),
                dp(18)
        );

        header.setBackground(
                createHeaderBackground()
        );

        TextView back =
                new TextView(this);

        back.setText("←");
        back.setTextColor(Color.WHITE);
        back.setTextSize(28);
        back.setGravity(Gravity.CENTER);

        back.setPadding(
                0,
                0,
                dp(18),
                0
        );

        back.setOnClickListener(
                v -> finish()
        );

        header.addView(back);

        TextView title =
                new TextView(this);

        title.setText("About TapTask");
        title.setTextColor(Color.WHITE);
        title.setTextSize(21);
        title.setTypeface(
                null,
                Typeface.BOLD
        );

        header.addView(title);

        root.addView(
                header,
                new LinearLayout.LayoutParams(
                        -1,
                        -2
                )
        );

        // =========================================
        // CONTENT
        // =========================================

        LinearLayout content =
                new LinearLayout(this);

        content.setOrientation(
                LinearLayout.VERTICAL
        );

        content.setPadding(
                dp(20),
                dp(24),
                dp(20),
                dp(30)
        );

        root.addView(
                content,
                new LinearLayout.LayoutParams(
                        -1,
                        -2
                )
        );

        // =========================================
        // APP INTRO CARD
        // =========================================

        LinearLayout introCard =
                createCard();

        introCard.setGravity(
                Gravity.CENTER_HORIZONTAL
        );

        introCard.setPadding(
                dp(22),
                dp(26),
                dp(22),
                dp(26)
        );

        TextView logo =
                new TextView(this);

        logo.setText("TT");
        logo.setTextColor(Color.WHITE);
        logo.setTextSize(27);
        logo.setTypeface(
                null,
                Typeface.BOLD
        );

        logo.setGravity(
                Gravity.CENTER
        );

        logo.setBackground(
                createLogoBackground()
        );

        introCard.addView(
                logo,
                new LinearLayout.LayoutParams(
                        dp(82),
                        dp(82)
                )
        );

        TextView appName =
                createTitle(
                        "TapTask"
                );

        appName.setTextSize(24);
        appName.setGravity(
                Gravity.CENTER
        );

        LinearLayout.LayoutParams appNameParams =
                new LinearLayout.LayoutParams(
                        -1,
                        -2
                );

        appNameParams.topMargin =
                dp(14);

        introCard.addView(
                appName,
                appNameParams
        );

        TextView tagline =
                new TextView(this);

        tagline.setText(
                "Your trusted local service marketplace"
        );

        tagline.setTextColor(
                Color.rgb(93, 85, 200)
        );

        tagline.setTextSize(13);
        tagline.setGravity(
                Gravity.CENTER
        );

        tagline.setTypeface(
                null,
                Typeface.BOLD
        );

        LinearLayout.LayoutParams taglineParams =
                new LinearLayout.LayoutParams(
                        -1,
                        -2
                );

        taglineParams.topMargin =
                dp(5);

        introCard.addView(
                tagline,
                taglineParams
        );

        content.addView(
                introCard
        );

        // =========================================
        // ABOUT TAPTASK
        // =========================================

        TextView aboutTitle =
                createSectionTitle(
                        "About the Application"
                );

        LinearLayout.LayoutParams aboutTitleParams =
                new LinearLayout.LayoutParams(
                        -1,
                        -2
                );

        aboutTitleParams.topMargin =
                dp(26);

        aboutTitleParams.bottomMargin =
                dp(12);

        content.addView(
                aboutTitle,
                aboutTitleParams
        );

        LinearLayout aboutCard =
                createCard();

        aboutCard.setPadding(
                dp(20),
                dp(20),
                dp(20),
                dp(20)
        );

        TextView aboutText =
                createDescription(
                        "TapTask is a service booking platform designed "
                                + "to connect customers with skilled and "
                                + "verified service workers. Customers can "
                                + "browse available services, select a "
                                + "worker, choose a suitable date and time, "
                                + "and create a booking from one convenient "
                                + "application."
                );

        aboutText.setTextSize(13);
        aboutText.setTextColor(
                Color.rgb(88, 85, 105)
        );

        aboutCard.addView(
                aboutText
        );

        TextView secondParagraph =
                createDescription(
                        "The platform supports different types of services "
                                + "and provides an organized workflow for "
                                + "customers, workers, and administrators. "
                                + "The admin panel helps manage users, workers, "
                                + "bookings, service areas, verification, "
                                + "statistics, and other operational settings."
                );

        secondParagraph.setTextSize(13);

        LinearLayout.LayoutParams secondParams =
                new LinearLayout.LayoutParams(
                        -1,
                        -2
                );

        secondParams.topMargin =
                dp(14);

        aboutCard.addView(
                secondParagraph,
                secondParams
        );

        content.addView(
                aboutCard
        );

        // =========================================
        // MAIN FEATURES
        // =========================================

        TextView featuresTitle =
                createSectionTitle(
                        "Key Features"
                );

        LinearLayout.LayoutParams featuresTitleParams =
                new LinearLayout.LayoutParams(
                        -1,
                        -2
                );

        featuresTitleParams.topMargin =
                dp(26);

        featuresTitleParams.bottomMargin =
                dp(12);

        content.addView(
                featuresTitle,
                featuresTitleParams
        );

        LinearLayout featuresCard =
                createCard();

        featuresCard.setPadding(
                dp(18),
                dp(20),
                dp(18),
                dp(20)
        );

        addFeature(
                featuresCard,
                "👤",
                "Customer Booking",
                "Customers can discover services and book "
                        + "workers according to their requirements."
        );

        addDivider(featuresCard);

        addFeature(
                featuresCard,
                "🛠",
                "Worker Management",
                "Workers can manage service requests and "
                        + "update booking progress."
        );

        addDivider(featuresCard);

        addFeature(
                featuresCard,
                "💳",
                "Flexible Payments",
                "TapTask supports online and cash payment "
                        + "preferences for bookings."
        );

        addDivider(featuresCard);

        addFeature(
                featuresCard,
                "📊",
                "Admin Management",
                "Administrators can monitor users, workers, "
                        + "bookings, revenue, and service activity."
        );

        content.addView(
                featuresCard
        );

        // =========================================
        // ADMIN PANEL
        // =========================================

        TextView adminTitle =
                createSectionTitle(
                        "Admin Panel"
                );

        LinearLayout.LayoutParams adminTitleParams =
                new LinearLayout.LayoutParams(
                        -1,
                        -2
                );

        adminTitleParams.topMargin =
                dp(26);

        adminTitleParams.bottomMargin =
                dp(12);

        content.addView(
                adminTitle,
                adminTitleParams
        );

        LinearLayout adminCard =
                createCard();

        adminCard.setPadding(
                dp(20),
                dp(20),
                dp(20),
                dp(20)
        );

        TextView adminText =
                createDescription(
                        "The TapTask Admin Panel provides centralized "
                                + "control over the application. Administrators "
                                + "can review platform activity, verify workers, "
                                + "manage users and bookings, maintain service "
                                + "areas, and view real-time statistics."
                );

        adminText.setTextSize(13);

        adminCard.addView(
                adminText
        );

        content.addView(
                adminCard
        );

        // =========================================
        // PROJECT PURPOSE
        // =========================================

        TextView purposeTitle =
                createSectionTitle(
                        "Project Purpose"
                );

        LinearLayout.LayoutParams purposeTitleParams =
                new LinearLayout.LayoutParams(
                        -1,
                        -2
                );

        purposeTitleParams.topMargin =
                dp(26);

        purposeTitleParams.bottomMargin =
                dp(12);

        content.addView(
                purposeTitle,
                purposeTitleParams
        );

        LinearLayout purposeCard =
                createCard();

        purposeCard.setPadding(
                dp(20),
                dp(20),
                dp(20),
                dp(20)
        );

        TextView purposeText =
                createDescription(
                        "TapTask aims to make local service booking "
                                + "simple, organized, and convenient. The "
                                + "platform brings customers and service "
                                + "workers together while providing an "
                                + "easy-to-manage digital workflow."
                );

        purposeText.setTextSize(13);

        purposeCard.addView(
                purposeText
        );

        content.addView(
                purposeCard
        );

        // =========================================
        // FOOTER
        // =========================================

        TextView footer =
                new TextView(this);

        footer.setText(
                "TapTask Admin Panel"
        );

        footer.setTextColor(
                Color.rgb(145, 141, 160)
        );

        footer.setTextSize(11);

        footer.setGravity(
                Gravity.CENTER
        );

        LinearLayout.LayoutParams footerParams =
                new LinearLayout.LayoutParams(
                        -1,
                        -2
                );

        footerParams.topMargin =
                dp(28);

        content.addView(
                footer,
                footerParams
        );

        setContentView(scrollView);
    }

    // =========================================
    // FEATURE ITEM
    // =========================================

    private void addFeature(
            LinearLayout parent,
            String iconText,
            String title,
            String description
    ) {

        LinearLayout row =
                new LinearLayout(this);

        row.setOrientation(
                LinearLayout.HORIZONTAL
        );

        row.setGravity(
                Gravity.CENTER_VERTICAL
        );

        TextView icon =
                new TextView(this);

        icon.setText(iconText);
        icon.setTextSize(23);
        icon.setGravity(
                Gravity.CENTER
        );

        icon.setBackground(
                createIconBackground()
        );

        LinearLayout.LayoutParams iconParams =
                new LinearLayout.LayoutParams(
                        dp(52),
                        dp(52)
                );

        iconParams.rightMargin =
                dp(14);

        row.addView(
                icon,
                iconParams
        );

        LinearLayout textContainer =
                new LinearLayout(this);

        textContainer.setOrientation(
                LinearLayout.VERTICAL
        );

        TextView titleView =
                createTitle(title);

        titleView.setTextSize(14);

        textContainer.addView(
                titleView
        );

        TextView descriptionView =
                createDescription(description);

        textContainer.addView(
                descriptionView
        );

        row.addView(
                textContainer,
                new LinearLayout.LayoutParams(
                        0,
                        -2,
                        1
                )
        );

        parent.addView(
                row
        );
    }

    // =========================================
    // DIVIDER
    // =========================================

    private void addDivider(
            LinearLayout parent
    ) {

        View divider =
                new View(this);

        divider.setBackgroundColor(
                Color.rgb(231, 229, 238)
        );

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        -1,
                        dp(1)
                );

        params.topMargin =
                dp(16);

        params.bottomMargin =
                dp(16);

        parent.addView(
                divider,
                params
        );
    }

    // =========================================
    // TEXT HELPERS
    // =========================================

    private TextView createSectionTitle(
            String text
    ) {

        TextView view =
                new TextView(this);

        view.setText(text);

        view.setTextColor(
                Color.rgb(38, 36, 55)
        );

        view.setTextSize(17);

        view.setTypeface(
                null,
                Typeface.BOLD
        );

        return view;
    }

    private TextView createTitle(
            String text
    ) {

        TextView view =
                new TextView(this);

        view.setText(text);

        view.setTextColor(
                Color.rgb(45, 42, 63)
        );

        view.setTextSize(15);

        view.setTypeface(
                null,
                Typeface.BOLD
        );

        return view;
    }

    private TextView createDescription(
            String text
    ) {

        TextView view =
                new TextView(this);

        view.setText(text);

        view.setTextColor(
                Color.rgb(120, 117, 139)
        );

        view.setTextSize(12);

        view.setLineSpacing(
                dp(2),
                1.0f
        );

        return view;
    }

    // =========================================
    // CARD
    // =========================================

    private LinearLayout createCard() {

        LinearLayout card =
                new LinearLayout(this);

        card.setOrientation(
                LinearLayout.VERTICAL
        );

        card.setBackground(
                createCardBackground()
        );

        card.setLayoutParams(
                new LinearLayout.LayoutParams(
                        -1,
                        -2
                )
        );

        return card;
    }

    private GradientDrawable createCardBackground() {

        GradientDrawable drawable =
                new GradientDrawable();

        drawable.setColor(
                Color.WHITE
        );

        drawable.setCornerRadius(
                dp(18)
        );

        drawable.setStroke(
                dp(1),
                Color.rgb(229, 227, 238)
        );

        return drawable;
    }

    // =========================================
    // HEADER
    // =========================================

    private GradientDrawable createHeaderBackground() {

        return new GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                new int[]{
                        Color.rgb(83, 73, 190),
                        Color.rgb(116, 91, 205)
                }
        );
    }

    // =========================================
    // LOGO
    // =========================================

    private GradientDrawable createLogoBackground() {

        GradientDrawable drawable =
                new GradientDrawable(
                        GradientDrawable.Orientation.TL_BR,
                        new int[]{
                                Color.rgb(93, 85, 200),
                                Color.rgb(128, 91, 210)
                        }
                );

        drawable.setShape(
                GradientDrawable.OVAL
        );

        return drawable;
    }

    // =========================================
    // ICON
    // =========================================

    private GradientDrawable createIconBackground() {

        GradientDrawable drawable =
                new GradientDrawable();

        drawable.setColor(
                Color.rgb(241, 239, 255)
        );

        drawable.setCornerRadius(
                dp(16)
        );

        drawable.setStroke(
                dp(1),
                Color.rgb(216, 211, 247)
        );

        return drawable;
    }

    // =========================================
    // DP
    // =========================================

    private int dp(int value) {

        float density =
                getResources()
                        .getDisplayMetrics()
                        .density;

        return (int)
                (value * density + 0.5f);
    }
}