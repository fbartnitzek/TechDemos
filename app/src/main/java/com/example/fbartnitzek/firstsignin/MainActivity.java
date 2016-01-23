package com.example.fbartnitzek.firstsignin;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    private GoogleApiClient mGoogleApiClient;
    private SignInButton mSignInButton;
    private Button mSignOutButton;
    private Button mRevokeAccessButton;
    private TextView mStatus;

    private static final int STATE_SIGNED_IN = 0;
    private static final int STATE_SIGN_IN = 1;
    private static final int STATE_IN_PROGRESS = 2;
    private int mSignInProgress;

    private static final String LOG_TAG = MainActivity.class.getName();
    private PendingIntent mSignInIntent;
    private int mSignInError;

    private static final int RC_SIGN_IN = 0;
    private static final int DIALOG_PLAY_SERVICES_ERROR = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mGoogleApiClient = buildGoogleApiClient();

        mSignInButton = (SignInButton) findViewById(R.id.signInBtn);
        mSignInButton.setOnClickListener(this);

        mSignOutButton = (Button) findViewById(R.id.btnSignOut);
        mSignOutButton.setOnClickListener(this);

        mRevokeAccessButton = (Button) findViewById(R.id.btnRevokeAccess);
        mRevokeAccessButton.setOnClickListener(this);

        mStatus = (TextView) findViewById(R.id.status);

    }

    public GoogleApiClient buildGoogleApiClient() {
        return new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API, Plus.PlusOptions.builder().build())
                .addScope(new Scope(Scopes.PROFILE))
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected() || mGoogleApiClient.isConnecting()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.v(LOG_TAG, "onConnected, " + "bundle = [" + bundle + "]");

        mSignInButton.setEnabled(false);
        mSignOutButton.setEnabled(true);
        mRevokeAccessButton.setEnabled(true);

        mSignInProgress = STATE_SIGN_IN;

        // we are signed in - retrieve some profile information to personalize app for the user
        Person currentUser = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
        mStatus.setText(String.format("Signed in to G+ as %s", currentUser.getDisplayName()));
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.v(LOG_TAG, "onConnectionSuspended, " + "cause = [" + i + "]");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.v(LOG_TAG, "onConnectionFailed, " + "errorCode= [" + connectionResult.getErrorCode() + "]");
        if (mSignInProgress != STATE_IN_PROGRESS) {
            // we do not have an intent in progress
            // => store the latest error resolution intent for use when sign in button is clicked
            mSignInIntent = connectionResult.getResolution();
            mSignInError = connectionResult.getErrorCode();

            if (mSignInProgress == STATE_SIGN_IN) {
                // indicates user already clicked sign in button
                // so we should continue processing errors until the user is signed in or clicked cancel
                resolveSignInError();
            }
        }

        onSignedOut();
    }

    private void onSignedOut() {
        Log.v(LOG_TAG, "onSignedOut, " + "");
        mSignInButton.setEnabled(true);
        mSignOutButton.setEnabled(false);
        mRevokeAccessButton.setEnabled(false);
    }

    private void resolveSignInError() {
        if (mSignInIntent != null) {
            // we have an intent which will allow user to sign in or resolve error
            // f.e. if user needs to select an account to sign in with
            //      or if they need to consent to the permissions this app is requesting

            try {
                // send pending intent that we stored on the most recent OnConnFailed callback
                // will allow the user to resolve the error currently preventing our connection
                // to Google Play Services
                mSignInProgress = STATE_IN_PROGRESS;
                startIntentSenderForResult(mSignInIntent.getIntentSender(),
                        RC_SIGN_IN, null, 0, 0, 0); // will generate an onActivityResult-call
            } catch (IntentSender.SendIntentException e) {
                Log.i(LOG_TAG, "resolveSignInError: " + e.getLocalizedMessage());
                // intent was canceled before it was sent
                // attempt to connect to get an updated ConnectionResult
                mSignInProgress = STATE_SIGN_IN;
                mGoogleApiClient.connect();
            }

        } else {
            // Google Play services wasn't able to provide an intent for some error type
            // so we show the default Google Play services error dialog
            // which may still start an intent on our behalf if the user can resolve the issue
            showDialog(DIALOG_PLAY_SERVICES_ERROR);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.v(LOG_TAG, "onActivityResult, " + "requestCode = [" + requestCode + "], resultCode = [" + resultCode + "], data = [" + data + "]");
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                // error resolution successful => continue processing errors
                mSignInProgress = STATE_SIGN_IN;
            } else {
                // error resolution was not successful or user canceled => stop processing errors
                mSignInProgress = STATE_SIGNED_IN;
            }

            if (!mGoogleApiClient.isConnecting()) {
                // google play services resolved the issue with a dialog then
                // onStart is not called, so we need to re-attempt connection
                mGoogleApiClient.connect();
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (!mGoogleApiClient.isConnecting()) { // not for transition state
            switch (v.getId()) {
                case R.id.signInBtn:
                    mStatus.setText("Signing in");
                    resolveSignInError();
                    break;
                case R.id.btnSignOut:
                    // clear default account, so that google play services will not return
                    // onConnected callback without user interaction
                    Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
                    mGoogleApiClient.disconnect();
                    mGoogleApiClient.connect();
                    break;
                case R.id.btnRevokeAccess:
                    // after we revoke permission for the user with apiClient,
                    // we must discard it and create new one
                    Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
                    // compliance to Google developer policies:
                    // register callback on revokeAccessAndDisconnect and delete user data
                    // (even if we don't cache data in sample app)
                    Plus.AccountApi.revokeAccessAndDisconnect(mGoogleApiClient);
                    mGoogleApiClient = buildGoogleApiClient();
                    mGoogleApiClient.connect();
                    break;
                default:
            }
        }
    }
}
