package com.wealthdoctor.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.wealthdoctor.R;
import com.wealthdoctor.otp.OTPActivity;
import com.wealthdoctor.service.repository.ProjectRepository;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    public static final String MOBILE_NUMBER = "mobile number";

    private String userMobileNumber;

    private String userCountryCode;

    private TextInputEditText countryCodeEditText;

    private TextInputEditText mobileNumberEditText;

    private ProjectRepository projectRepository = ProjectRepository.getInstance();

    private Map<String, String> mobileData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Log.i("LoginActivity", "Giris1");

        countryCodeEditText = (TextInputEditText) findViewById(R.id.country_code_edittext);
        mobileNumberEditText = (TextInputEditText) findViewById(R.id.mobile_number_edittext);

        //userCountryCode = countryCodeEditText.

    }

    /**
     * submit method to send the mobile number to the server to get the OTP response.
     * @param view
     */
    public void submit(View view) {

        userCountryCode = countryCodeEditText.getText().toString();
        userMobileNumber = mobileNumberEditText.getText().toString();
        if (userMobileNumber.length() == 10) {

            mobileData = new HashMap<>();
            mobileData.put("mobile number", userMobileNumber);

            String response = projectRepository.get_all_articles(mobileData);
            if (response != null) {
                Toast.makeText(LoginActivity.this, response, Toast.LENGTH_LONG).show();

                Intent intent = new Intent(this, OTPActivity.class);
                intent.putExtra(MOBILE_NUMBER, userMobileNumber);
                startActivity(intent);
            }
        } else {
            Toast.makeText(LoginActivity.this, "Invalid Mobile Number ", Toast.LENGTH_LONG).show();
        }
    }

}
