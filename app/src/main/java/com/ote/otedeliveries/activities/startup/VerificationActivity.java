package com.ote.otedeliveries.activities.startup;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.databinding.DataBindingUtil;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.ote.otedeliveries.R;
import com.ote.otedeliveries.utils.PrefManager;

import br.com.zup.multistatelayout.MultiStateLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class VerificationActivity extends AppCompatActivity {
    @BindView(R.id.verification_code) TextInputEditText verification_code;
    @BindView(R.id.verification_message) AppCompatTextView verification_message;
    @BindView(R.id.verification_code_layout) MultiStateLayout verification_code_layout;

    @BindView(R.id.phone_number_input) TextInputLayout phone_number_input;
    @BindView(R.id.phone_number_text) TextInputEditText phone_number_text;

    @BindView(R.id.one_time_message) AppCompatTextView one_time_message;

    private boolean authenticationInProgress = false;
    private String phoneNumber = null;
    private CountDownTimer countDownTimer;
    private String verificationID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DataBindingUtil.setContentView(this, R.layout.activity_verification_code);
        ButterKnife.bind(this);

        phoneNumber = getIntent().getStringExtra("phoneNumber");

        phone_number_text.setText(getIntent().getStringExtra("phoneNumber"));
    }

    private void hideShowVerificationInputs(int verificationStatus){
        phone_number_input.setVisibility(verificationStatus);
        phone_number_text.setVisibility(verificationStatus);
        one_time_message.setVisibility(verificationStatus);
    }

    public void hideShowVerficationCodeInputs(int verificationStatus){
        verification_message.setVisibility(verificationStatus);
        verification_code.setVisibility(verificationStatus);
        (findViewById(R.id.resend_code_button)).setVisibility(verificationStatus);
        (findViewById(R.id.change_number_button)).setVisibility(verificationStatus);
    }


    @OnClick(R.id.top_back_button)
    public void top_back_button(){
        genericBack();
    }

    @Override
    public void onBackPressed() {
        genericBack();
    }

    private void genericBack(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.verification_cancel_title);
        builder.setMessage(R.string.verification_cancel_message);
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                PrefManager.setAccountVerified(false);

                startActivity(new Intent(VerificationActivity.this, LoginActivity.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }
        });
        builder.setPositiveButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        if(authenticationInProgress) {
            builder.create().show();
        }
        else{
            startActivity(new Intent(VerificationActivity.this, LoginActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }
    }
}
