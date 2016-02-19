package com.example.android.dinnerapp;

import android.app.Application;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Logger;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.tagmanager.ContainerHolder;
import com.google.android.gms.tagmanager.TagManager;

/**
 * Copyright 2016.  Frank Bartnitzek
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

public class MyApplication extends Application {

    public Tracker mTracker;
    public TagManager mTagManager;

    public ContainerHolder getContainerHolder() {
        return mContainerHolder;
    }

    public void setContainerHolder(ContainerHolder mContainerHolder) {
        this.mContainerHolder = mContainerHolder;
    }

    public ContainerHolder mContainerHolder;

    public void startTracking() {

        // create tracker if it does not exist
        if (mTracker == null) {
            GoogleAnalytics ga = GoogleAnalytics.getInstance(this);
            mTracker = ga.newTracker(R.xml.track_app);
            ga.enableAutoActivityReports(this);

            ga.getLogger().setLogLevel(Logger.LogLevel.VERBOSE);
        }
    }

    public Tracker getTracker() {
        startTracking();    // will exist at the end
        return mTracker;
    }

    public TagManager getTagManager() {
        if (mTagManager == null) {
            mTagManager = TagManager.getInstance(this);
        }
        return mTagManager;
    }
}
