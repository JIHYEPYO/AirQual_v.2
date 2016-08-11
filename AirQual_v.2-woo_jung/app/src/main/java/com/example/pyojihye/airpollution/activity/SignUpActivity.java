package com.example.pyojihye.airpollution.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.pyojihye.airpollution.HttpConnection;
import com.example.pyojihye.airpollution.R;

import P_Data.Util_STATUS;

/**
 * Created by PYOJIHYE on 2016-07-20.
 */
public class SignUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
    }
    public void onButtonRegisterClick(View v)  {
        EditText editTextFirstName = (EditText) findViewById(R.id.editTextFirstName);
        EditText editTextLastName = (EditText) findViewById(R.id.editTextLastName);
        EditText editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        EditText editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        EditText editTextConfirmPassword = (EditText) findViewById(R.id.editTextConfirmPassword);

        if (v.isClickable()) {
            String email = editTextEmail.getText().toString();
            String password = editTextPassword.getText().toString();
            String confirmPassword = editTextConfirmPassword.getText().toString();
            String firstName = editTextFirstName.getText().toString();
            String lastName = editTextLastName.getText().toString();

            if(firstName.equals("") || lastName.equals("") || email.equals("") || password.equals("")) {
                Toast.makeText(getApplicationContext(), "Please fill in all information", Toast.LENGTH_SHORT).show();
                return;
            }else{
                if(password.equals(confirmPassword)){
                    Util_STATUS.HTTP_CONNECT_KIND=1;
                    HttpConnection httpConnectionSignUp = new HttpConnection(SignUpActivity.this,getApplicationContext());
                    httpConnectionSignUp.execute(email,password,confirmPassword,firstName,lastName);
                }else{
                    Toast.makeText(getApplicationContext(), "This is not the same password and its confirmation.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
