package com.wealthdoctor.otp;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.wealthdoctor.R;
import com.wealthdoctor.login.LoginActivity;
import com.wealthdoctor.registration.RegistrationActivity;

public class OTPActivity extends AppCompatActivity {

    private ProgressBar progressbar;

    private TextView updateProgressBar;

    private int progressCount;

    Dialog myDialog;

    private Handler progressHandler = new Handler();

    private String mobileNumber;

    private TextView mobileNumberText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        mobileNumberText = (TextView)findViewById(R.id.mobile_number);

        myDialog = new Dialog(this);

        mobileNumber = getIntent().getStringExtra(LoginActivity.MOBILE_NUMBER);

        mobileNumberText.setText(mobileNumber);

       progressbar = (ProgressBar)findViewById(R.id.progress_view);
        Button submitButton = (Button)findViewById(R.id.submit_otp);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(OTPActivity.this, RegistrationActivity.class);
                startActivity(intent);

            }
        });

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
                if (s.toString().length() == ("1234").length()) {
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    //inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    Toast.makeText(OTPActivity.this, "Success", Toast.LENGTH_SHORT).show();
                } /*else if (s.length() == "1234".length()) {
                    Toast.makeText(LoginActivity.this, "Incorrect", Toast.LENGTH_SHORT).show();
                    txtPinEntry.setText(null);
                }*/
            }
        });
    }}
