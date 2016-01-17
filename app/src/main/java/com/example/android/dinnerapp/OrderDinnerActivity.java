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
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.analytics.ecommerce.Product;
import com.google.android.gms.analytics.ecommerce.ProductAction;


public class OrderDinnerActivity extends Activity {
    String selectedDinnerExtrasKey = String.valueOf(R.string.selected_dinner);

    String mDinner;
    String mDinnerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_dinner);
    }

    protected void onStart() {
        super.onStart();

        // Set the heading
        TextView headingText = (TextView) findViewById(R.id.textView_info_heading);
        headingText.setText(getResources().getText(R.string.order_online_heading));

        // Set the text
        TextView tv = (TextView) findViewById(R.id.textView_info);

        mDinner = getIntent().getStringExtra(selectedDinnerExtrasKey);
        mDinnerId = Utility.getDinnerId(mDinner);
        tv.setText("This is where you will order the selected dinner: \n\n" + mDinner);
        sendViewProductHit();
    }

    public void addDinnerToCart(View view) {
        Toast.makeText(OrderDinnerActivity.this, "I will add the dinner "
                + mDinner + " to the cart", Toast.LENGTH_SHORT).show();

        Button button = (Button) findViewById(R.id.start_checkout_button);
        button.setVisibility(View.VISIBLE);

        button = (Button) findViewById(R.id.add_to_cart_button);
        button.setVisibility(View.INVISIBLE);

        sendAddToCartHit();
    }

    public void startCheckout(View view) {
        Toast.makeText(OrderDinnerActivity.this, "checkout started ...", Toast.LENGTH_SHORT).show();
        sendProductToHit(new ProductAction(ProductAction.ACTION_CHECKOUT), "Check out product");

        findViewById(R.id.add_to_cart_button).setVisibility(View.INVISIBLE);
        findViewById(R.id.start_checkout_button).setVisibility(View.INVISIBLE);
        findViewById(R.id.checkout_step_2_button).setVisibility(View.VISIBLE);
    }

    public void getPaymentInfo(View view) {
        Toast.makeText(OrderDinnerActivity.this, "collection payment infos ...", Toast.LENGTH_SHORT).show();

        ProductAction productAction = new ProductAction(ProductAction.ACTION_CHECKOUT_OPTION)
                .setCheckoutStep(2);
        sendProductToHit(productAction, "get payment");

        findViewById(R.id.checkout_step_2_button).setVisibility(View.INVISIBLE);
        findViewById(R.id.purchase_button).setVisibility(View.VISIBLE);
    }

    public void purchaseDinner(View view) {
        Toast.makeText(OrderDinnerActivity.this, "Purchasing " + mDinner, Toast.LENGTH_SHORT).show();
        // in production it should be a Product for every item in cart...
        ProductAction pa = new ProductAction(ProductAction.ACTION_PURCHASE)
                .setTransactionId(Utility.getUniqueTransactionId(mDinnerId));
        sendProductToHit(pa, "purchasing dinner");

    }

    private void sendAddToCartHit() {
        sendProductToHit(new ProductAction(ProductAction.ACTION_ADD), "Add dinner to card");
    }

    public void sendViewProductHit() {
        sendProductToHit(new ProductAction(ProductAction.ACTION_DETAIL), "View Order Dinner screen");
    }

    private void sendProductToHit(ProductAction productAction, String actionText) {
        Product product = new Product()
                .setName("dinner")
                .setPrice(5)
                .setVariant(mDinner)
                .setId(mDinnerId)
                .setQuantity(1);

        Tracker tracker = ((MyApplication) getApplication()).getTracker();

        tracker.send(new HitBuilders.EventBuilder()
                .setCategory("Shopping steps")
                .setAction(actionText)
                .setLabel(mDinner)
                .addProduct(product)
                .setProductAction(productAction)
                .build());
    }

}
