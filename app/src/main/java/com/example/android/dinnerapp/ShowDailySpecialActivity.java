package com.example.android.dinnerapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tagmanager.ContainerHolder;

public class ShowDailySpecialActivity extends Activity {


    private static final String DAILY_SPECIAL_KEY = "daily-special";
    String selectedDinnerExtrasKey = String.valueOf(R.string.selected_dinner);

    private static final String LOG_TAG = ShowDailySpecialActivity.class.getName();
    public String mDailySpecial = "Fried egg with kit kat rashers";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_daily_special);

        TextView headingText = (TextView) findViewById(R.id.textView_info_heading);
        headingText.setText("The special for today");

        updateDailySpecial();
//        TextView infoText = (TextView) findViewById(R.id.textView_info);
//        infoText.setText(mDailySpecial);

    }

    public void updateDailySpecial() {
        ContainerHolder holder = ((MyApplication) getApplication()).getContainerHolder();
        mDailySpecial = holder.getContainer().getString(DAILY_SPECIAL_KEY);
        Log.v(LOG_TAG, "updateDailySpecial, mDailySpecial" + mDailySpecial);
        ((TextView) findViewById(R.id.textView_info)).setText(mDailySpecial);
    }

    public void orderOnline(View view) {
        Intent intent = new Intent(this, OrderDinnerActivity.class);
        intent.putExtra(selectedDinnerExtrasKey, mDailySpecial);
        startActivity(intent);
    }

}
