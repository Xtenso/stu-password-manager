package com.example.passwordmanager10;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class UpdateActivity extends AppCompatActivity {

    ImageView editImageU, copyPassword;
    EditText nameU, usernameU, passwordU;
    TextView tvDateU;
    ImageButton ibFavouriteU;
    Button updateButton, deleteButton, cancelButtonUpdate;
    String favouriteU = "false";
    String imageU;
    String id, name, username, password, image, date, favourite;
    final String secretKey = "alshcnaoiec!!!!";
    String encryptedPasswordU, decryptedPassword;

    int SELECT_PICTURE = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        copyPassword = findViewById(R.id.copyPassword);
        cancelButtonUpdate = findViewById(R.id.cancelButtonUpdate);
        nameU = findViewById(R.id.nameU);
        usernameU = findViewById(R.id.usernameU);
        passwordU = findViewById(R.id.passwordU);
        editImageU = findViewById(R.id.editImageU);
        tvDateU = findViewById(R.id.tvDateU);
        ibFavouriteU = findViewById(R.id.ibFavouriteU);
        updateButton = findViewById(R.id.updateButton);
        deleteButton = findViewById(R.id.deleteButton);

        getAndSetData();

        copyPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Password", decryptedPassword);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(UpdateActivity.this, "Password copied to clipboard!", Toast.LENGTH_SHORT).show();
            }
        });

        ibFavouriteU.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(favouriteU == "false") {
                    ibFavouriteU.setImageResource(R.drawable.ic_filled_star);
                    favouriteU = "true";
                }else {
                    ibFavouriteU.setImageResource(R.drawable.ic_empty_star);
                    favouriteU = "false";
                }
            }
        });

        editImageU.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageChooser();
            }
        });

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(nameU.getText().toString().isEmpty() || usernameU.getText().toString().isEmpty() || passwordU.getText().toString().isEmpty()){
                    Toast.makeText(UpdateActivity.this, "Please fill out all required fields!", Toast.LENGTH_SHORT).show();
                }else {
                    encryptedPasswordU = AES.encrypt(passwordU.getText().toString(),secretKey);
                    SQLDatabaseHelper dbHelper = new SQLDatabaseHelper(UpdateActivity.this);
                    name = nameU.getText().toString().trim();
                    username = usernameU.getText().toString().trim();
                    password = encryptedPasswordU;
                    image = imageU;
                    date = tvDateU.getText().toString().trim();
                    favourite = favouriteU;
                    dbHelper.updateData(id, name, username, password, image, date, favourite);
                    Intent intent = new Intent(UpdateActivity.this, MainActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_left, R.anim.slide_out_right);
                    finish();
                }
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmDeletion();
            }
        });

        cancelButtonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    void getAndSetData(){
        if(getIntent().hasExtra("id") && getIntent().hasExtra("name") && getIntent().hasExtra("username")
        && getIntent().hasExtra("password") && getIntent().hasExtra("date") && getIntent().hasExtra("favourite")){
            //Getting data
            id = getIntent().getStringExtra("id");
            name = getIntent().getStringExtra("name");
            username = getIntent().getStringExtra("username");
            password = getIntent().getStringExtra("password");
            image = getIntent().getStringExtra("image");
            date = getIntent().getStringExtra("date");
            favourite = getIntent().getStringExtra("favourite");

            decryptedPassword = AES.decrypt(password,secretKey);

            //Setting data
            nameU.setText(name);
            usernameU.setText(username);
            passwordU.setText(decryptedPassword);
            imageU = image;
            if(image.isEmpty() == false){
                editImageU.setImageURI(null);
                editImageU.setImageURI(Uri.parse(image));
            }else {
                editImageU.setImageResource(R.drawable.ic_baseline_image_search_24);
            }
            tvDateU.setText(new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date()));
            if(favourite.contains("true")) {
                ibFavouriteU.setImageResource(R.drawable.ic_filled_star);
                favouriteU = "true";
            }else {
                ibFavouriteU.setImageResource(R.drawable.ic_empty_star);
                favouriteU = "false";
            }
        }else {
            Toast.makeText(this,"No Data!",Toast.LENGTH_SHORT).show();
        }
    }

    void confirmDeletion(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete " + name + " password?");
        builder.setMessage("Are you sure you want to delete this password?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                SQLDatabaseHelper dbHelper = new SQLDatabaseHelper(UpdateActivity.this);
                dbHelper.deleteRow(id);
                Intent intent = new Intent(UpdateActivity.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_left, R.anim.slide_out_right);
                finish();
            }
        });
        builder.setNegativeButton("Cancel   ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.create().show();
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
                    editImageU.setImageURI(selectedImageUri);
                    imageU = selectedImageUri.toString();
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