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

public class AdminPaymentSettingsActivity extends AppCompatActivity {

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

        title.setText("Payment Settings");
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
        // INTRO CARD
        // =========================================

        LinearLayout introCard =
                createCard();

        introCard.setGravity(
                Gravity.CENTER_HORIZONTAL
        );

        introCard.setPadding(
                dp(20),
                dp(22),
                dp(20),
                dp(22)
        );

        TextView introIcon =
                new TextView(this);

        introIcon.setText("💳");
        introIcon.setTextSize(32);
        introIcon.setGravity(
                Gravity.CENTER
        );

        introCard.addView(
                introIcon,
                new LinearLayout.LayoutParams(
                        -1,
                        -2
                )
        );

        TextView introTitle =
                createTitle(
                        "TapTask Payments"
                );

        introTitle.setGravity(
                Gravity.CENTER
        );

        LinearLayout.LayoutParams introTitleParams =
                new LinearLayout.LayoutParams(
                        -1,
                        -2
                );

        introTitleParams.topMargin =
                dp(8);

        introCard.addView(
                introTitle,
                introTitleParams
        );

        TextView introDescription =
                createDescription(
                        "Manage the payment methods available "
                                + "for TapTask bookings. Customers can "
                                + "choose their preferred payment option "
                                + "during booking."
                );

        introDescription.setGravity(
                Gravity.CENTER
        );

        introCard.addView(
                introDescription
        );

        content.addView(
                introCard
        );

        // =========================================
        // SECTION TITLE
        // =========================================

        TextView methodsTitle =
                createSectionTitle(
                        "Available Payment Methods"
                );

        LinearLayout.LayoutParams methodsTitleParams =
                new LinearLayout.LayoutParams(
                        -1,
                        -2
                );

        methodsTitleParams.topMargin =
                dp(24);

        methodsTitleParams.bottomMargin =
                dp(12);

        content.addView(
                methodsTitle,
                methodsTitleParams
        );

        // =========================================
        // ONLINE PAYMENT
        // =========================================

        LinearLayout onlineCard =
                createPaymentCard(
                        "💳",
                        "Online Payment",
                        "Customers can pay online after "
                                + "the service is completed."
                );

        content.addView(
                onlineCard
        );

        // =========================================
        // CASH PAYMENT
        // =========================================

        LinearLayout cashCard =
                createPaymentCard(
                        "💵",
                        "Cash Payment",
                        "Customers can select cash payment "
                                + "and pay the worker directly."
                );

        LinearLayout.LayoutParams cashParams =
                new LinearLayout.LayoutParams(
                        -1,
                        -2
                );

        cashParams.topMargin =
                dp(14);

        content.addView(
                cashCard,
                cashParams
        );

        // =========================================
        // PAYMENT FLOW
        // =========================================

        TextView flowTitle =
                createSectionTitle(
                        "Payment Flow"
                );

        LinearLayout.LayoutParams flowTitleParams =
                new LinearLayout.LayoutParams(
                        -1,
                        -2
                );

        flowTitleParams.topMargin =
                dp(26);

        flowTitleParams.bottomMargin =
                dp(12);

        content.addView(
                flowTitle,
                flowTitleParams
        );

        LinearLayout flowCard =
                createCard();

        flowCard.setPadding(
                dp(20),
                dp(20),
                dp(20),
                dp(20)
        );

        addFlowStep(
                flowCard,
                "01",
                "Customer books a service",
                "The customer selects a payment preference."
        );

        addDivider(flowCard);

        addFlowStep(
                flowCard,
                "02",
                "Service is completed",
                "The booking is marked as completed."
        );

        addDivider(flowCard);

        addFlowStep(
                flowCard,
                "03",
                "Payment is completed",
                "Online customers can use Pay Now, while "
                        + "cash customers pay directly."
        );

        content.addView(
                flowCard
        );

        // =========================================
        // SECURITY
        // =========================================

        LinearLayout securityCard =
                createCard();

        securityCard.setPadding(
                dp(18),
                dp(18),
                dp(18),
                dp(18)
        );

        LinearLayout.LayoutParams securityParams =
                new LinearLayout.LayoutParams(
                        -1,
                        -2
                );

        securityParams.topMargin =
                dp(18);

        content.addView(
                securityCard,
                securityParams
        );

        TextView securityTitle =
                createTitle(
                        "🔐 Payment Security"
                );

        securityCard.addView(
                securityTitle
        );

        TextView securityText =
                createDescription(
                        "TapTask keeps payment information separate "
                                + "from booking information. Online payment "
                                + "status is recorded against the existing "
                                + "booking."
                );

        securityCard.addView(
                securityText
        );

        // =========================================
        // SYSTEM STATUS
        // =========================================

        LinearLayout statusCard =
                createCard();

        statusCard.setPadding(
                dp(18),
                dp(18),
                dp(18),
                dp(18)
        );

        LinearLayout.LayoutParams statusCardParams =
                new LinearLayout.LayoutParams(
                        -1,
                        -2
                );

        statusCardParams.topMargin =
                dp(18);

        content.addView(
                statusCard,
                statusCardParams
        );

        TextView statusTitle =
                createTitle(
                        "System Status"
                );

        statusCard.addView(
                statusTitle
        );

        TextView status =
                new TextView(this);

        status.setText(
                "●  Payment system is ready"
        );

        status.setTextColor(
                Color.rgb(47, 145, 91)
        );

        status.setTextSize(14);

        status.setTypeface(
                null,
                Typeface.BOLD
        );

        LinearLayout.LayoutParams statusParams =
                new LinearLayout.LayoutParams(
                        -1,
                        -2
                );

        statusParams.topMargin =
                dp(10);

        status.setLayoutParams(
                statusParams
        );

        statusCard.addView(
                status
        );

        // =========================================
        // FOOTER
        // =========================================

        TextView footer =
                new TextView(this);

        footer.setText(
                "TapTask Admin • Payment Management"
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
    // PAYMENT CARD
    // =========================================

    private LinearLayout createPaymentCard(
            String iconText,
            String titleText,
            String descriptionText
    ) {

        LinearLayout card =
                createCard();

        card.setOrientation(
                LinearLayout.HORIZONTAL
        );

        card.setGravity(
                Gravity.CENTER_VERTICAL
        );

        card.setPadding(
                dp(18),
                dp(18),
                dp(18),
                dp(18)
        );

        TextView icon =
                new TextView(this);

        icon.setText(iconText);
        icon.setTextSize(27);
        icon.setGravity(
                Gravity.CENTER
        );

        icon.setBackground(
                createIconBackground()
        );

        LinearLayout.LayoutParams iconParams =
                new LinearLayout.LayoutParams(
                        dp(58),
                        dp(58)
                );

        iconParams.rightMargin =
                dp(16);

        card.addView(
                icon,
                iconParams
        );

        LinearLayout textContainer =
                new LinearLayout(this);

        textContainer.setOrientation(
                LinearLayout.VERTICAL
        );

        TextView title =
                createTitle(titleText);

        textContainer.addView(
                title
        );

        TextView description =
                createDescription(
                        descriptionText
                );

        textContainer.addView(
                description
        );

        TextView available =
                new TextView(this);

        available.setText(
                "✓ Available"
        );

        available.setTextColor(
                Color.rgb(47, 145, 91)
        );

        available.setTextSize(11);

        available.setTypeface(
                null,
                Typeface.BOLD
        );

        LinearLayout.LayoutParams availableParams =
                new LinearLayout.LayoutParams(
                        -2,
                        -2
                );

        availableParams.topMargin =
                dp(7);

        textContainer.addView(
                available,
                availableParams
        );

        card.addView(
                textContainer,
                new LinearLayout.LayoutParams(
                        0,
                        -2,
                        1
                )
        );

        return card;
    }

    // =========================================
    // FLOW STEP
    // =========================================

    private void addFlowStep(
            LinearLayout parent,
            String number,
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

        TextView numberView =
                new TextView(this);

        numberView.setText(number);
        numberView.setTextColor(Color.WHITE);
        numberView.setTextSize(12);
        numberView.setTypeface(
                null,
                Typeface.BOLD
        );

        numberView.setGravity(
                Gravity.CENTER
        );

        numberView.setBackground(
                createNumberBackground()
        );

        LinearLayout.LayoutParams numberParams =
                new LinearLayout.LayoutParams(
                        dp(42),
                        dp(42)
                );

        numberParams.rightMargin =
                dp(14);

        row.addView(
                numberView,
                numberParams
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

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        -1,
                        -2
                );

        params.topMargin =
                dp(6);

        view.setLayoutParams(
                params
        );

        return view;
    }

    // =========================================
    // CARD BACKGROUND
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
    // HEADER BACKGROUND
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
    // ICON BACKGROUND
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
    // NUMBER BACKGROUND
    // =========================================

    private GradientDrawable createNumberBackground() {

        GradientDrawable drawable =
                new GradientDrawable(
                        GradientDrawable.Orientation.TL_BR,
                        new int[]{
                                Color.rgb(93, 85, 200),
                                Color.rgb(116, 91, 205)
                        }
                );

        drawable.setShape(
                GradientDrawable.OVAL
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