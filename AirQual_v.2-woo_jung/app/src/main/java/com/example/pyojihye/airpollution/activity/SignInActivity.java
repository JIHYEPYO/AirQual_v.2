package com.example.pyojihye.airpollution.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.pyojihye.airpollution.HttpConnection;
import com.example.pyojihye.airpollution.R;

import P_Data.Util_STATUS;

public class SignInActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        Button buttonLogin = (Button) findViewById(R.id.buttonLogin);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            EditText editTextEmail = (EditText) findViewById(R.id.editTextEmail);
            EditText editTextPassword = (EditText) findViewById(R.id.editTextPassword);

            @Override
            public void onClick(View view) {
                String email = editTextEmail.getText().toString();
                String password = editTextPassword.getText().toString();

                if (email.equals("") || password.equals("")) {
                    Toast.makeText(getApplicationContext(), "Please fill in all information", Toast.LENGTH_SHORT).show();
                } else {

                    Util_STATUS.HTTP_CONNECT_KIND=0;
                    HttpConnection httpConnectionSignIn = new HttpConnection(SignInActivity.this, getApplicationContext());
                    SharedPreferences pref;
                    pref=getSharedPreferences("MAC",0);
                    SharedPreferences.Editor editor = pref.edit();
                    //editor.putString("UDOOdeviceID",jsonObject.getString("deviceID"));
                    editor.putString("useremail",email);

                    editor.commit();
                    httpConnectionSignIn.execute(email,password);
                    //Intent mainIntent=new Intent(getApplicationContext(), MainActivity.class);
                    //startActivity(mainIntent);
                }
            }
        });
    }

    public void textViewSignInClick(View v) {
        if (v.isClickable()) {
            Intent signUpIntent = new Intent(SignInActivity.this, SignUpActivity.class);
            startActivity(signUpIntent);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    }
}
