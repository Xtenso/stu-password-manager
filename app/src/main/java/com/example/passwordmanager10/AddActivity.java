package com.example.passwordmanager10;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.FileUtils;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddActivity extends AppCompatActivity {

    ImageView editImage;
    EditText name, username, password;
    TextView tvDate;
    ImageButton ibFavourite;
    Button saveButton, cancelButtonAdd;
    String favourite = "false";
    String imagePath;
    String encryptedPassword;
    final String secretKey = "alshcnaoiec!!!!";

    int SELECT_PICTURE = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        cancelButtonAdd = findViewById(R.id.cancelButtonAdd);
        name = findViewById(R.id.name);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        editImage = findViewById(R.id.editImage);
        tvDate = findViewById(R.id.tvDate);
        ibFavourite = findViewById(R.id.ibFavourite);
        saveButton = findViewById(R.id.saveButton);

        tvDate.setText(new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date()));

        ibFavourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(favourite == "false") {
                    ibFavourite.setImageResource(R.drawable.ic_filled_star);
                    favourite = "true";
                }else {
                    ibFavourite.setImageResource(R.drawable.ic_empty_star);
                    favourite = "false";
                }
            }
        });

        editImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageChooser();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(name.getText().toString().isEmpty() || username.getText().toString().isEmpty() || password.getText().toString().isEmpty()){
                    Toast.makeText(AddActivity.this, "Please fill out all required fields!", Toast.LENGTH_SHORT).show();
                }else {
                    encryptedPassword = AES.encrypt(password.getText().toString(),secretKey);
                    SQLDatabaseHelper db = new SQLDatabaseHelper(AddActivity.this);
                    db.addPassword(name.getText().toString().trim(), username.getText().toString().trim(),
                            encryptedPassword, imagePath,
                            tvDate.getText().toString().trim(), favourite);
                    Intent intent = new Intent(AddActivity.this, MainActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_left, R.anim.slide_out_right);
                    finish();
                }
            }
        });

        cancelButtonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    void imageChooser() {
        Intent i = new Intent();
        i.setType("image/*");
        i.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                | Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        i.setAction(Intent.ACTION_OPEN_DOCUMENT);
        startActivityForResult(Intent.createChooser(i, "Select Picture"), SELECT_PICTURE);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                Uri selectedImageUri = data.getData();
                getContentResolver().takePersistableUriPermission(selectedImageUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                if (null != selectedImageUri) {
                    editImage.setImageURI(selectedImageUri);
                    imagePath = selectedImageUri.toString();
                }
            }
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_left, R.anim.slide_out_right);
    }
}