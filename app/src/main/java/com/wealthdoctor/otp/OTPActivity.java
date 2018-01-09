package com.wealthdoctor.otp;

import android.Manifest;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsMessage;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.wealthdoctor.R;
import com.wealthdoctor.service.retrofit_services.Client;
import com.wealthdoctor.service.retrofit_services.InterfaceApi;
import com.wealthdoctor.login.LoginActivity;
import com.wealthdoctor.registration.RegistrationActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OTPActivity extends AppCompatActivity implements View.OnKeyListener, View.OnClickListener {

    private ProgressBar progressbar;

    private TextView updateProgressBar;

    private int progressCount;

    Dialog myDialog;

    private Handler progressHandler = new Handler();

    private String mobileNumber;

    private TextView mobileNumberText;

    private String otpCode;

    private Map<String, String> otpRequest;

    //private ProjectRepository projectRepository = ProjectRepository.getInstance();

    public static String appVersion;

    private String mobileModel;

    private Cursor cursor;

    private String name, phonenumber, email;

    private JsonObject StoreContacts = new JsonObject();

    private JSONArray json_array = new JSONArray();

    public static final int RequestPermissionCode = 1;

    ContactListTask contactList = new ContactListTask();

    private String deviceName;

    String json;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        mobileNumberText = (TextView) findViewById(R.id.mobile_number);

        mobileNumber = getIntent().getStringExtra(LoginActivity.MOBILE_NUMBER);

        mobileNumberText.setText(mobileNumber);

        deviceName = android.os.Build.MODEL; // returns model name


        progressbar = (ProgressBar) findViewById(R.id.progress_view);
        Button submitButton = (Button) findViewById(R.id.submit_otp);
        submitButton.setOnClickListener(this);

        contactList();


        final OTPEditText txtPinEntry = (OTPEditText) findViewById(R.id.txt_pin_entry);


        txtPinEntry.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                otpCode = s.toString();
                if (s.toString().length() == ("1234").length()) {
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    //inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    Toast.makeText(OTPActivity.this, "Success" + otpCode, Toast.LENGTH_SHORT).show();
                } /*else if (s.length() == "1234".length()) {
                    Toast.makeText(LoginActivity.this, "Incorrect", Toast.LENGTH_SHORT).show();
                    txtPinEntry.setText(null);
                }*/
            }
        });
    }

    @Override
    public boolean onKey(View view, int i, KeyEvent keyEvent) {

        if (i == KeyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {

            Log.d("OTPActivity", "enter pressed");
            Intent intent = new Intent(OTPActivity.this, RegistrationActivity.class);
            startActivity(intent);

        }

        return false;
    }

    @Override
    public void onClick(View view) {

        otpRequest = new HashMap<>();


        /*otpRequest.put("otp_number", otpCode);
        otpRequest.put("version_", appVersion);
        otpRequest.put("device_id", LoginActivity.deviceID);
        otpRequest.put("fcm_code", " ");
        otpRequest.put("mobile_number", LoginActivity.userMobileNumber);
        //otpRequest.put("", deviceName);
        otpRequest.put("contat_list", json_array.toString());
        */loadJSONOTP();

        //String response = projectRepository.get_all_articles(otpRequest, 1);

       /* if (response != null) {
            Toast.makeText(OTPActivity.this, response, Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(OTPActivity.this, RegistrationActivity.class);
            startActivity(intent);

        } else {
            Toast.makeText(OTPActivity.this, "Invalid OTP ", Toast.LENGTH_SHORT).show();
        }
*/
    }

    public void loadJSONOTP() {

        try {


            Client Client = new Client();
            InterfaceApi apiService =
                    Client.getClient().create(InterfaceApi.class);
            Call<JsonElement> call = apiService.getArticles(otpRequest);
            call.enqueue(new Callback<JsonElement>() {
                @Override
                public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {

                    Intent intent = new Intent(OTPActivity.this, RegistrationActivity.class);
                    startActivity(intent);

                    Toast.makeText(OTPActivity.this, response.body().toString(), Toast.LENGTH_LONG).show();
                    Log.d("LoginActivity", response.body().toString());

                }

                @Override
                public void onFailure(Call<JsonElement> call, Throwable t) {
                    //Log.d("Error", t.getMessage());
                    Toast.makeText(OTPActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();


                }
            });
        } catch (Exception e) {
            Log.d("Error", e.getMessage());
            Toast.makeText(OTPActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }



    @Override
    public void onBackPressed() {

        // Write your code here

        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);

        super.onBackPressed();
    }

    /*// Method to get the contactList
        public void getContactsIntoArrayList(){

            *//*cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null, null, null);

        while (cursor.moveToNext()) {

            name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));

            phonenumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

            StoreContacts.add(name + " "  + ":" + " " + phonenumber);
        }
        for (int i=0;i<StoreContacts.size();i++){Log.d("OTPActivity",StoreContacts.get(i));}

        cursor.close();*//*

    }*/
// requesting runtime permission to read the contact list.
    private void contactList() {

        // Check if the SMS permission has been granted
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                == PackageManager.PERMISSION_GRANTED) {

            contactList.execute();
        } else {
            // Permission is missing and must be requested.
            requestReadContactPermission();
        }
        // END_INCLUDE(startCamera)
    }

    /**
     * Requests the {@link android.Manifest.permission# RECIEVE_SMS} permission.
     * If an additional rationale should be displayed, the user has to launch the request from
     * a SnackBar that includes additional information.
     */
    private void requestReadContactPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS},
                RequestPermissionCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        // BEGIN_INCLUDE(onRequestPermissionsResult)
        if (requestCode == RequestPermissionCode) {
            // Request for SMS permission.
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                contactList.execute();

            } else {
                // Permission request was denied.


            }
        }

        // END_INCLUDE(onRequestPermissionsResult)
    }

    private class ContactListTask extends AsyncTask {


        @Override
        protected Object doInBackground(Object[] objects) {

            cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);


            while (cursor.moveToNext()) {

                name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));

                phonenumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                try {
                    JSONObject contactObj = new JSONObject();
                    contactObj.put("phone_no", phonenumber);
                    contactObj.put("name", name);

                    json_array.put(contactObj);

                } catch (JSONException e) {

                }
            }

            Log.d("OTPActivity", json_array.toString());

            cursor.close();
            return StoreContacts;

        }

    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equalsIgnoreCase("otp")) {
                final String message = intent.getStringExtra("message");

               // TextView tv = (TextView) findViewById(R.id.txtview);
                //tv.setText(message);
            }
        }
    };

    @Override
    public void onResume() {
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter("otp"));
        super.onResume();
        Intent intent = new Intent();
        final Bundle bundle = intent.getExtras();

        String message_from_sms="";
        try
        {
            if (bundle != null)
            {
                final Object[] pdusObj = (Object[]) bundle.get("pdus");

                try
                {
                    SmsMessage currentMessage =
                            SmsMessage.createFromPdu((byte[])pdusObj[pdusObj .length-1]);
                    String phoneNumber = currentMessage.getDisplayOriginatingAddress();
                    String senderNum = phoneNumber ;
                    message_from_sms = currentMessage .getDisplayMessageBody();

                }
                catch(Exception e)
                {

                }


            }

        } catch (Exception e)
        {

        }
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    }
}
