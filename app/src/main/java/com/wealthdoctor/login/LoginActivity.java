package com.wealthdoctor.login;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.wealthdoctor.R;
import com.wealthdoctor.circularButton.CircularProgressButton;
import com.wealthdoctor.otp.OTPActivity;
import com.wealthdoctor.service.retrofit_services.Client;
import com.wealthdoctor.service.retrofit_services.InterfaceApi;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity
        implements ActivityCompat.OnRequestPermissionsResultCallback, View.OnClickListener, View.OnKeyListener {

    public static final String TAG = "LoginActivity";
    public int requestCode = 1;
    public static final String MOBILE_NUMBER = "mobile number";
    public static String userMobileNumber;
    private String userCountryCode;
    private TextInputEditText countryCodeEditText;
    private TextInputEditText mobileNumberEditText;
    private Map<String, String> mobileData;
    private static final int PERMISSION_REQUEST_SMS = 0;
    private static final int PERMISSION_REQUEST_READ_PHONE_STATE = 1;
    private View mLayout;
    private int count = 0;
    public static String deviceID;
    private Button submitButton;
    private CircularProgressButton circularProgressButton;
    private JsonElement status;

    /* private int progress = 0;
    private TextView percentageTV, progressAmountTV;
    public static final int sweepDuration = 5000;
    private static final String MANUAL_PROGRESS_AMOUNT_KEY = "manualProgressAmount";
    private static final String FIXED_PROGRESS_PERCENTAGE_KEY = "fixedTimeProgressPercentage";
    private static final String CONFIGURATION_CHANGE_KEY = "configurationChange";
    private int fixedTimeProgressPercentage = 0;
    private boolean hasConfigurarationChanged = false;
    private String response;

    TelephonyManager telephonyManager;*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Log.i("LoginActivity", "Giris1");

        countryCodeEditText = (TextInputEditText) findViewById(R.id.country_code_edittext);
        mobileNumberEditText = (TextInputEditText) findViewById(R.id.mobile_number_edittext);
        mLayout = findViewById(R.id.activity_main);

        // Circular button initialization
        circularProgressButton = (CircularProgressButton) findViewById(R.id.circularButton);
        //userCountryCode = countryCodeEditText.
        //permissionToDrawOverlays();

        // fetching the android secure ID
        deviceID = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID);

        Log.d(TAG, deviceID);

    }


    /**
     * submit method to send the mobile number to the server to get the OTP response.
     *
     * @param view
     */
    public void submit(View view) {


        userCountryCode = countryCodeEditText.getText().toString();
        userMobileNumber = mobileNumberEditText.getText().toString();


        if ((userMobileNumber.length() == 10) && count ==0) {
            count++;
            circularProgressButton.setIndeterminateProgressMode(true);
            circularProgressButton.setStrokeColor(ContextCompat.getColor(this, R.color.colorStroke));
            if (circularProgressButton.isIdle()) {
                circularProgressButton.showProgress();
            } /*else if (circularProgressButton.isErrorOrCompleteOrCancelled()) {
            // circularProgressButton.showIdle();
        } else if (circularProgressButton.isProgress()) {
            //circularProgressButton.showCancel();
        }*/
            mobileData = new HashMap<>();
            //Todo to set the request data in map
            mobileData.put("mobile_number", userMobileNumber);
            mobileData.put("device_id", deviceID);
            loadJSON();

        } else {
            Toast.makeText(LoginActivity.this, "Invalid Mobile Number ", Toast.LENGTH_SHORT).show();
        }
    }

    // Request permission to receive the sms on cellphone


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        // BEGIN_INCLUDE(onRequestPermissionsResult)
        if (requestCode == PERMISSION_REQUEST_SMS) {
            // Request for SMS permission.
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                startOTPActivity();

            } else {
                // Permission request was denied.
                startOTPActivity();

            }
        }

        // END_INCLUDE(onRequestPermissionsResult)
    }


    private void showOTPScreen() {

        // Check if the SMS permission has been granted
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS)
                == PackageManager.PERMISSION_GRANTED) {
            startOTPActivity();
        } else {
            // Permission is missing and must be requested.
            requestSMSSendingPermission();
        }

    }

    /**
     * Requests the {@link android.Manifest.permission# RECIEVE_SMS} permission.
     * If an additional rationale should be displayed, the user has to launch the request from
     * a SnackBar that includes additional information.
     */
    private void requestSMSSendingPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECEIVE_SMS},
                PERMISSION_REQUEST_SMS);

    }

    private void startOTPActivity() {
        circularProgressButton.setVisibility(View.INVISIBLE);
        Intent intent = new Intent(this, OTPActivity.class);
        intent.putExtra(MOBILE_NUMBER, userMobileNumber);
        startActivity(intent);

    }

    //Handling cancellation back navigation

    @Override
    public void onBackPressed() {
        // Write your code here
        requestCode = 0;

        super.onBackPressed();
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public boolean onKey(View view, int i, KeyEvent keyEvent) {
        return false;
    }

    public void loadJSON() {

        try {


            Client Client = new Client();
            InterfaceApi apiService =
                    Client.getClient().create(InterfaceApi.class);
            Call<JsonObject> call = apiService.getOTP(mobileData);
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                    JsonObject jsonObject = response.body();
                    status = jsonObject.get("status");
                    int statusResponse = ((int) status.getAsInt());
                    if(statusResponse == 1) {
                        showOTPScreen();
                        Toast.makeText(LoginActivity.this, response.body().toString(), Toast.LENGTH_LONG).show();
                        Log.d("LoginActivity", response.body().toString());
                    }

                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    //Log.d("Error", t.getMessage());
                    Toast.makeText(LoginActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();


                }
            });
        } catch (Exception e) {
            Log.d("Error", e.getMessage());
            Toast.makeText(LoginActivity.this, e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onPause() {
        // circularProgressButton.setVisibility(View.GONE);
        super.onPause();
    }
}
