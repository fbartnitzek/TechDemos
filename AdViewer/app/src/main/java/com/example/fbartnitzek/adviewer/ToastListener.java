package com.example.fbartnitzek.adviewer;

import android.content.Context;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;

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

public class ToastListener extends AdListener {

    private final Context mContext;
    private String mErrorReason;

    public ToastListener(Context context) {
        super();
        mContext = context;
    }

    @Override
    public void onAdClosed() {
        Toast.makeText(mContext, "onAdClosed", Toast.LENGTH_SHORT).show();
        super.onAdClosed();
    }

    public static String getErrorString(int errorCode) {
        switch (errorCode) {
            case AdRequest.ERROR_CODE_INTERNAL_ERROR:
                return "internal error";
            case AdRequest.ERROR_CODE_INVALID_REQUEST:
                return "invalid request";
            case AdRequest.ERROR_CODE_NETWORK_ERROR:
                return "network error";
            case AdRequest.ERROR_CODE_NO_FILL:
                return "no fill";
            default:
                return "unknown";
        }
    }

    @Override
    public void onAdFailedToLoad(int errorCode) {
        mErrorReason = getErrorString(errorCode);
        Toast.makeText(mContext, "onAdFailedToLoad with error reason: " + mErrorReason,
                Toast.LENGTH_SHORT).show();
        super.onAdFailedToLoad(errorCode);
    }

    @Override
    public void onAdLeftApplication() {
        Toast.makeText(mContext, "onAdLeftApplication", Toast.LENGTH_SHORT).show();
        super.onAdLeftApplication();
    }

    @Override
    public void onAdLoaded() {
        Toast.makeText(mContext, "onAdLoaded", Toast.LENGTH_SHORT).show();
        super.onAdLoaded();
    }

    @Override
    public void onAdOpened() {
        Toast.makeText(mContext, "onAdOpened", Toast.LENGTH_SHORT).show();
        super.onAdOpened();
    }

    public String getErrorReason() {
        return mErrorReason == null ? "" : mErrorReason;
    }
}
