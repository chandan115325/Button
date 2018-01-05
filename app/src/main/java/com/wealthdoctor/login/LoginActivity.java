package com.wealthdoctor.login;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.wealthdoctor.R;
import com.wealthdoctor.otp.OTPActivity;
import com.wealthdoctor.service.repository.ProjectRepository;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity
        implements ActivityCompat.OnRequestPermissionsResultCallback {

    public static final String TAG = "LoginActivity";

    public int requestCode = 1;

    public static final String MOBILE_NUMBER = "mobile number";

    private String userMobileNumber;

    private String userCountryCode;

    private TextInputEditText countryCodeEditText;

    private TextInputEditText mobileNumberEditText;

    private ProjectRepository projectRepository = ProjectRepository.getInstance();

    private Map<String, String> mobileData;

    private static final int PERMISSION_REQUEST_SMS = 0;

    private static  final int PERMISSION_REQUEST_READ_PHONE_STATE = 1;

    private View mLayout;

    private String deviceID;

    TelephonyManager telephonyManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Log.i("LoginActivity", "Giris1");

        countryCodeEditText = (TextInputEditText) findViewById(R.id.country_code_edittext);
        mobileNumberEditText = (TextInputEditText) findViewById(R.id.mobile_number_edittext);
        mLayout = findViewById(R.id.activity_main);

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
        if (userMobileNumber.length() == 10) {

            mobileData = new HashMap<>();
            mobileData.put("mobile number", userMobileNumber);
            mobileData.put("device id", deviceID);

            String response = projectRepository.get_all_articles(mobileData, requestCode);
            if (response != null) {
                Toast.makeText(LoginActivity.this, response, Toast.LENGTH_LONG).show();
                showOTPScreen();

            } else {
                Toast.makeText(LoginActivity.this, response, Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(LoginActivity.this, "Invalid Mobile Number ", Toast.LENGTH_LONG).show();
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
                // Permission has been granted. Start camera preview Activity.
         /*       Snackbar.make(mLayout, "SMS receiving permission was granted. .",
                        Snackbar.LENGTH_SHORT)
                        .show();
         */
                startOTPActivity();

            } else {
                // Permission request was denied.
                startOTPActivity();
/*
                Snackbar.make(mLayout, "SMS receiving permission request was denied.",
                        Snackbar.LENGTH_SHORT)
                        .show();
*/
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
        // END_INCLUDE(startCamera)
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

   /* public final static int PERM_REQUEST_CODE_DRAW_OVERLAYS = 1234;
    *//**
     * Permission to draw Overlays/On Other Apps, related to 'android.permission.SYSTEM_ALERT_WINDOW' in Manifest
     * Resolves issue of popup in Android M and above "Screen overlay detected- To change this permission setting you first have to turn off the screen overlay from Settings > Apps"
     * If app has not been granted permission to draw on the screen, create an Intent &
     * set its destination to Settings.ACTION_MANAGE_OVERLAY_PERMISSION &
     * add a URI in the form of "package:<package name>" to send users directly to your app's page.
     * Note: Alternative Ignore URI to send user to the full list of apps.
     *//*
    public void permissionToDrawOverlays() {
        if (android.os.Build.VERSION.SDK_INT >= 23) {   //Android M Or Over
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, PERM_REQUEST_CODE_DRAW_OVERLAYS);
            }
        }
    }

//3. ------------- Called on the activity, to check on the results returned of the user action within the settings
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PERM_REQUEST_CODE_DRAW_OVERLAYS) {
            if (android.os.Build.VERSION.SDK_INT >= 23) {   //Android M Or Over
                if (!Settings.canDrawOverlays(this)) {
                    // ADD UI FOR USER TO KNOW THAT UI for SYSTEM_ALERT_WINDOW permission was not granted earlier...
                }
            }
        }
    }
*/
}
