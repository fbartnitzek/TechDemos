package com.example.android.dinnerapp;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

public class ShowAllDinnersActivity extends ListActivity {

    String selectedDinnerExtrasKey = String.valueOf(R.string.selected_dinner);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        long startTime = System.nanoTime();
        setContentView(R.layout.list_all_dinners);

        // get array of all dinners
        Dinner dinner = new Dinner();
        String[] allDinners = dinner.getAllDinners(this);

        // create array adapter & attack to listview
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.show_dinner_in_row, R.id.textview_dinner_row, allDinners);
        ListView listView = (ListView) findViewById(android.R.id.list); // generic id, ok ...
        listView.setAdapter(adapter);
        long stopTime = System.nanoTime();

        sendTimeEvent(stopTime - startTime);    //nanoseconds
    }

    private void sendTimeEvent(long elapsedTime) {
        Tracker tracker = ((MyApplication)getApplication()).getTracker();
        tracker.send(new HitBuilders.TimingBuilder()
                .setCategory("List all the dinners")
                .setValue(elapsedTime / 1000000)
                .setLabel("display list")
                .setVariable("duration")
                .build());
    }


    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        String value = (String) getListView().getItemAtPosition(position);

        Utility.showMyToast("selected dinner is " + value, this);
        Intent intent = new Intent(this, OrderDinnerActivity.class);
        intent.putExtra(selectedDinnerExtrasKey, value);

        startActivity(intent);
    }
}
