package com.example.passwordmanager10;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class LoginActivity extends AppCompatActivity {

    Button loginButton;
    TextView welcomeTv;
    EditText loginUsername, loginPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginButton = findViewById(R.id.loginButton);
        welcomeTv = findViewById(R.id.welcomeTv);
        loginUsername = findViewById(R.id.loginUsername);
        loginPassword = findViewById(R.id.loginPassword);
        SQLDatabaseHelper dbHelper = new SQLDatabaseHelper(this);

        if(dbHelper.firstTime() == false){
            welcomeTv.setText("Welcome back!");
        }

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if(!prefs.getBoolean("firstTime", false)) {
            welcomeScreen();
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("firstTime", true);
            editor.commit();
        }

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(loginUsername.getText().toString().isEmpty() || loginPassword.getText().toString().isEmpty()){
                    Toast.makeText(LoginActivity.this, "Please fill out all the necessary fields!", Toast.LENGTH_SHORT).show();
                }else {
                    Boolean userExists = dbHelper.checkUsername(loginUsername.getText().toString());
                    if(userExists == false){
                        if(dbHelper.firstTime() == true){
                            dbHelper.addLoginCredentials(loginUsername.getText().toString(), loginPassword.getText().toString());
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            overridePendingTransition(R.anim.slide_right, R.anim.slide_out_left);
                            finish();
                        }else {
                            Toast.makeText(LoginActivity.this, "No such user!", Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        Boolean passwordCorrect = dbHelper.checkUserAndPassword(loginUsername.getText().toString(), loginPassword.getText().toString());
                        if(passwordCorrect == true){
                            Toast.makeText(LoginActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            overridePendingTransition(R.anim.slide_right, R.anim.slide_out_left);
                            finish();
                        }else {
                            Toast.makeText(LoginActivity.this, "Please check your password!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });
    }

    void welcomeScreen(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Welcome to One Pass!");
        builder.setMessage(Html.fromHtml("<p>One pass is a Password Manager with a local SQLite database, meaning all your passwords are " +
                "safely stored on device and encrypted using AES.</p><p><font color='#FF0000'><b>IMPORTANT</b></font></p><p>After closing this message, you will be greeted with a login screen. Please " +
                "enter a password and a username and click the login button bellow. Every time you enter the app after that, you will need to " +
                "enter your credentials in order to login.</p><p><b><font color='#00A3FF'>Thank you for choosing One Pass!</font></b></p>"));
        builder.setPositiveButton("Got it", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.create().show();
    }
}