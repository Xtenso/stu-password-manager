package com.example.passwordmanager10;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    FloatingActionButton addButton;
    TextView passwordsNumberTv, noPasswordsTv;
    ImageView deleteAll, noPasswordsImage, logout, tips;

    SQLDatabaseHelper dbHelper;
    ArrayList web_id, web_name, web_username, web_password, web_image, web_date, web_favourite;
    CustomAdapter customAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.MANAGE_EXTERNAL_STORAGE},
                PackageManager.PERMISSION_GRANTED);

        logout = findViewById(R.id.logout);
        noPasswordsImage = findViewById(R.id.noPasswordsImage);
        tips = findViewById(R.id.tips);
        noPasswordsTv = findViewById(R.id.noPasswordsTv);
        deleteAll = findViewById(R.id.deleteAll);
        passwordsNumberTv = findViewById(R.id.passwordsNumberTv);
        recyclerView = findViewById(R.id.recyclerView);
        addButton = findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_right, R.anim.slide_out_left);
            }
        });

        tips.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Tips");
                builder.setMessage(Html.fromHtml("<h5><font color='#00A3FF'>Adding a password</font></h5><p>Clicking the plus button " +
                        "at the bottom of the screen will open a new window.There you will be able to add your, " +
                        "username, password, the name of website, mark it as a favourite and even add an image " +
                        "if you choose to. In order to save it, all you have to do is click the SAVE button!</p>" +
                        "<h5><font color='#00A3FF'>Deleting a password</font></h5><p>In order to delete a password, you will have to" +
                        "select it from the list, and then click the DELETE button at the bottom of the screen!</p>" +
                        "<h5><font color='#00A3FF'>Updating a password</font></h5><p>To update a password, all you have to do, is select it from " +
                        "the list, change the information you want to, and click update. If the operations was " +
                        "successful, you will receive a confirmation at the bottom of the screen.</p>"));
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                builder.create().show();
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_left, R.anim.slide_out_right);
                finish();
                Toast.makeText(MainActivity.this, "You have been successfully logged out!", Toast.LENGTH_SHORT).show();
            }
        });

        deleteAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(web_name.size() != 0){
                    confirmDeletion();
                }else {
                    Toast.makeText(MainActivity.this, "No data to delete!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        dbHelper = new SQLDatabaseHelper(MainActivity.this);
        web_id =new ArrayList<>();
        web_name = new ArrayList<>();
        web_username = new ArrayList<>();
        web_password = new ArrayList<>();
        web_image = new ArrayList<>();
        web_date = new ArrayList<>();
        web_favourite = new ArrayList<>();

        displayData();

        passwordsNumberTv.setText(String.valueOf(web_name.size()));

        customAdapter = new CustomAdapter(MainActivity.this, web_id, web_name, web_username, web_password, web_image, web_date, web_favourite);
        recyclerView.setAdapter(customAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
    }

    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }

    void displayData(){
        Cursor cursor = dbHelper.readData();
        if(cursor.getCount() == 0) {
            noPasswordsImage.setVisibility(View.VISIBLE);
            noPasswordsTv.setVisibility(View.VISIBLE);
        }else {
            while (cursor.moveToNext()){
                web_id.add(cursor.getString(0));
                web_name.add(cursor.getString(1));
                web_username.add(cursor.getString(2));
                web_password.add(cursor.getString(3));
                web_image.add(cursor.getString(4));
                web_date.add(cursor.getString(5));
                web_favourite.add(cursor.getString(6));
            }
            noPasswordsImage.setVisibility(View.GONE);
            noPasswordsTv.setVisibility(View.GONE);
        }
    }

    void confirmDeletion(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete all passwords?");
        builder.setMessage("Are you sure you want to delete all passwords?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                SQLDatabaseHelper dbHelper = new SQLDatabaseHelper(MainActivity.this);
                Toast.makeText(MainActivity.this, "Successfully deleted all data!", Toast.LENGTH_SHORT).show();
                dbHelper.deleteAllData();
                recreate();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.create().show();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_left, R.anim.slide_out_right);
    }
}