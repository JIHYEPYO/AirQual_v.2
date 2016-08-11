package com.example.pyojihye.airpollution.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.pyojihye.airpollution.HttpConnection;
import com.example.pyojihye.airpollution.R;

import P_Data.Util_STATUS;

public class SignInActivity extends AppCompatActivity {

    public static Activity ActivitySignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        ActivitySignIn=SignInActivity.this;

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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                new AlertDialog.Builder(this)
                        .setTitle("Exit App")
                        .setMessage("Are you sure you want to exit the application?")
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //process kill
                                android.os.Process.killProcess(android.os.Process.myPid());
                            }
                        })
                        .setNegativeButton("NO", null)
                        .show();
                break;

            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }
}
