/*
 * Copyright (C) 2015 Google Inc. All Rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.example.android.dinnerapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.tagmanager.ContainerHolder;
import com.google.android.gms.tagmanager.DataLayer;
import com.google.android.gms.tagmanager.TagManager;

import java.util.concurrent.TimeUnit;


public class MainActivity extends Activity {

    private static final String CONTAINER_ID = "GTM-K5MZZC";
    private static final String LOG_TAG = MainActivity.class.getName();
    private static final String FOOD_PREF = "food-pref";
    TagManager mTagManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Make sure that Analytics tracking has started
        ((MyApplication) getApplication()).startTracking();

        loadGtmContainer();
    }

    /*
     * Show a pop up menu of food preferences.
     * Menu items are defined in menus/food_prefs_menu.xml
     */
    public void showFoodPrefsMenu(View view) {
        // Utility.showMyToast("I will show you a menu", this);
        android.widget.PopupMenu popup = new android.widget.PopupMenu(this, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.food_prefs_menu, popup.getMenu());

        // Set the action of the menu
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                getDinnerSuggestion(item.getItemId());
                return true;
            }
        });
        // Show the popup menu
        popup.show();
    }

    /*
     * Suggest dinner for tonight.
     * This method is invoked by the button click action of the food prefs menu.
     * We use the Dinner class to figure out the dinner, based on the food pref.

     */
    public String getDinnerSuggestion(int item) {

        // Get a new Dinner, and use it to get tonight's dinner
        Dinner dinner = new Dinner(this, item);
        String dinnerChoice = dinner.getDinnerTonight();
        // Utility.showMyToast("dinner suggestion: " + dinnerChoice, this);

        // Start an intent to show the dinner suggestion
        // Put the suggested dinner in the Intent's Extras bundle
        Intent dinnerIntent = new Intent(this, ShowDinnerActivity.class);

        dinnerIntent.putExtra(String.valueOf(R.string.selected_dinner), dinnerChoice);
        startActivity(dinnerIntent);

        return dinnerChoice;
    }



    public void showDinnerList(View view) {
        // start activity that shows all dinners in app
        startActivity(new Intent(this, ShowAllDinnersActivity.class));
    }

    public void showDailySpecial(View view) {
        android.widget.PopupMenu popup = new android.widget.PopupMenu(this, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.food_prefs_menu, popup.getMenu());

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                putFoodPrefInDataLayer(menuItem.getItemId());
                startShowDailySpecialActivity();
                return true;
            }
        });
        popup.show();
    }

    private void startShowDailySpecialActivity() {
        DataLayer dl = mTagManager.getDataLayer();
        dl.pushEvent("openScreen", DataLayer.mapOf("screen-name", "Show Daily Special"));
        startActivity(new Intent(this, ShowDailySpecialActivity.class));
    }

    private void putFoodPrefInDataLayer(int itemId) {
        DataLayer dataLayer = ((MyApplication) getApplication()).getTagManager().getDataLayer();
        dataLayer.push(FOOD_PREF, Dinner.getFoodPref(itemId));
    }

    public void loadGtmContainer() {
        mTagManager = ((MyApplication) getApplication()).getTagManager();

        mTagManager.setVerboseLoggingEnabled(true); //add verbose logging

        PendingResult<ContainerHolder> pending =
                mTagManager.loadContainerPreferFresh(CONTAINER_ID, R.raw.gtm_default);
        pending.setResultCallback(new ResultCallback<ContainerHolder>() {
            @Override
            public void onResult(ContainerHolder containerHolder) {

                if (!containerHolder.getStatus().isSuccess()) {
                    Log.e(LOG_TAG, "failure loading GTM container");
                    Toast.makeText(MainActivity.this, "could not load daily special...",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                containerHolder.refresh();  // manually refresh - only every ca. 15min

                ((MyApplication)getApplication()).setContainerHolder(containerHolder);
            }
        }, 2, TimeUnit.SECONDS);
    }
}

